package com.easyvisa

import com.easyvisa.document.BaseDocument
import com.easyvisa.document.DocumentAttachment
import com.easyvisa.document.DocumentCompletionStatus
import com.easyvisa.document.ReceivedDocument
import com.easyvisa.document.RequiredDocument
import com.easyvisa.document.SentDocument
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.PackageStatus
import com.easyvisa.enums.ProcessRequestState
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.SectionCompletionStatus
import com.easyvisa.utils.ExceptionUtils
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional
import grails.gsp.PageRenderer
import grails.plugin.springsecurity.SpringSecurityService
import groovy.transform.CompileDynamic
import org.apache.http.HttpStatus

import static com.easyvisa.enums.EasyVisaSystemMessageType.APPLICANT_PACKAGE_TRANSFER_REQUEST
import static com.easyvisa.enums.EasyVisaSystemMessageType.APPLICANT_PACKAGE_TRANSFER_REQUEST_OLD_LEGAL_REP
import static com.easyvisa.enums.EasyVisaSystemMessageType.APPLICANT_PACKAGE_TRANSFER_REQUEST_OLD_LEGAL_REP_ALERT

@SuppressWarnings('FactoryMethodName')
@Transactional
@GrailsCompileStatic
class ApplicantService {

    ProfileService profileService
    AlertService alertService
    PageRenderer groovyPageRenderer
    PackageQuestionnaireService packageQuestionnaireService
    FileService fileService
    SpringSecurityService springSecurityService
    ProcessService processService
    AsyncService asyncService
    AnswerService answerService
    PackageDocumentService packageDocumentService

    Applicant findApplicant(String email) {
        Profile profile = profileService.findProfileWithUserByEmail(email)
        if (profile) {
            Applicant applicant = Applicant.findByProfile(profile)
            if (!applicant) {
                throw new EasyVisaException(errorCode: HttpStatus.SC_LOCKED,
                        errorMessageCode: 'user.exist.but.not.applicant')
            }
            applicant
        }
    }

    @CompileDynamic
    Applicant findApplicantByUser(Long userId) {
        Applicant.createCriteria().get {
            'profile' {
                eq('user.id', userId)
            }
        } as Applicant
    }

    Petitioner updatePetitioner(Petitioner petitioner, ApplicantCommand changes, Package aPackage) {
        //no new petitioner return null
        if (!changes) {
            deletePetitioner(petitioner, aPackage)
            return null
        }
        //if exist old petitioner and it's the same petitioner - updates the data
        if (petitioner && petitioner.applicant.id == changes.id) {
            petitioner.with {
                citizenshipStatus = changes.citizenshipStatus
            }
            Applicant existingApplicant = petitioner.applicant
            Applicant applicant = profileService.updateApplicant(existingApplicant, changes, aPackage)
            petitioner.applicant = applicant
            if (existingApplicant.id != applicant.id && applicant.user) {
                petitioner.optIn = ProcessRequestState.PENDING
            }
            return petitioner.save()
        }
        //no previous petitioner or new petitioner added - create new one
        deletePetitioner(petitioner, aPackage)
        // we need to ensure that it always true for Petitioner. Using this flag we will need to send an invitation during package opening
        changes.inviteApplicant = true;
        profileService.createPetitioner(changes, aPackage)
    }

    void removeApplicantFromPackage(Applicant applicant, Package aPackage) {
        if (applicant && !Applicant.findById(applicant.id)) {
            return;
        }

        Boolean isBeneficiaryApplicant = aPackage.getBenefitForApplicant(applicant) != null // applicant was a beneficiary or derivative beneficiary
        Boolean hasApplicantNotRegistered = !applicant.user // applicant was never invited to log on - there was no user/login object created
        ImmigrationBenefit benefit = aPackage.getBenefitForApplicant(applicant)
        processService.deleteProcessRequest(PackageOptInForImmigrationBenefitRequest.findByImmigrationBenefit(benefit))
        if (benefit.applicantTransactions) {
            if (benefit.paid) {
                AccountTransaction.executeUpdate('''update AccountTransaction at set at.immigrationBenefit = null,
                applicant_transactions_idx = null where at.id in (:ids)''', [ids: benefit.applicantTransactions*.id])
            } else {
                AccountTransaction.deleteAll(benefit.applicantTransactions)
            }
            benefit.applicantTransactions.clear()
        }
        aPackage.removeFromBenefits(benefit)
        benefit.delete(failOnError: true)
        deleteApplicantDataFromPackage(applicant, aPackage)

        if(isBeneficiaryApplicant && hasApplicantNotRegistered) {
            this.deleteNonRegisteredApplicant(applicant);
        }
    }

    void deletePetitioner(Petitioner petitioner, Package aPackage) {
        if (petitioner && Petitioner.findById(petitioner.id)) {
            Applicant applicant = petitioner.applicant
            deleteApplicantDataFromPackage(applicant, aPackage)
            processService.deleteProcessRequest(PackageOptInForPetitionerRequest.findByPetitioner(petitioner))
            petitioner.delete(failOnError: true)

            Boolean hasApplicantNotRegistered = !petitioner.applicant.user // applicant was never invited to log on - there was no user/login object created
            if (hasApplicantNotRegistered) {
                this.deleteNonRegisteredApplicant(applicant);
            }
        }
    }

    void deleteNonRegisteredApplicant(Applicant applicant) {
        Profile profile = applicant.profile
        applicant.delete(failOnError: true)
        profile.delete(failOnError: true)
    }

    private void deleteApplicantDataFromPackage(Applicant applicant, Package aPackage) {
        //Warnings
        Warning.executeUpdate('delete from Warning where aPackage = :package and applicant = :applicant',
                [package: aPackage, applicant: applicant])
        if (PackageStatus.LEAD == aPackage.status) {
            return
        }
        // Questionnaire Items
        List<Answer> answerList = Answer.findAllByApplicantIdAndPackageId(applicant.id, aPackage.id)
        this.answerService.removeAnswers(answerList);
        SectionCompletionStatus.executeUpdate('delete from SectionCompletionStatus where applicantId = :applicantId and packageId = :packageId',
                [applicantId: applicant.id, packageId: aPackage.id])

        // Document Items
        this.deleteDocumentReferenceAndItsAttachments(RequiredDocument.findAllByApplicantAndAPackage(applicant, aPackage) as BaseDocument[])
        this.deleteDocumentReferenceAndItsAttachments(SentDocument.findAllByApplicantAndAPackage(applicant, aPackage) as BaseDocument[])
        this.deleteDocumentReferenceAndItsAttachments(ReceivedDocument.findAllByApplicantAndAPackage(applicant, aPackage) as BaseDocument[])
        DocumentCompletionStatus.executeUpdate('delete from DocumentCompletionStatus where aPackage = :package',
                [package: aPackage], [flush: true])
    }

    void deleteDocumentReferenceAndItsAttachments(BaseDocument[] baseDocuments) {
        baseDocuments.each { BaseDocument baseDocument ->
            List<DocumentAttachment> documentAttachmentList = DocumentAttachment.findAllByDocumentReference(baseDocument);
            documentAttachmentList.each { DocumentAttachment documentAttachment ->
                this.packageDocumentService.deleteDocumentAttachment(documentAttachment)
            };
            baseDocument.delete(failOnError: true)
        }
    }

    Applicant saveApplicant(Applicant applicant) {
        applicant.save(failOnError: true)
        return applicant
    }

    Petitioner savePetitioner(Petitioner petitioner) {
        petitioner.save(failOnError: true)
        return petitioner
    }

    /**
     * Deletes an applicant. Uses for delete account action.
     * @param applicant applicant
     */
    void deleteApplicant(Applicant applicant) {
        //deleting completion stats
//        SectionCompletionStatus.executeUpdate('delete from SectionCompletionStatus s where s.applicantId = :applicantId',
//                [applicantId: applicant.id])
        //deleting all user answers and warnings
//        Warning.executeUpdate('delete from Warning w where w.answer in (from Answer a where a.applicantId = :applicantId)', [applicantId: applicant.id])
//        Answer.executeUpdate('delete from Answer a where a.applicantId = :applicantId', [applicantId: applicant.id])
        //nullify applicant data
//        profileService.nullifyProfileData(applicant.profile)
//        applicant.with {
//            dateOfBirth = null
//            if (home) {
//                home.delete(failOnError: true)
//                it.profile.address = null
//            }
//            mobileNumber = null
//            homeNumber = null
//            workNumber = null
//        }
//        applicant.save(failOnError: true, flush: true)
        asyncService.runAsyncDelayed({
            sendEmailApplicantLeaving(applicant.id)
        }, "Send email of leaving Applicant [$applicant.id]")
        //delete files
//        deleteApplicantPackageFiles(applicant)
    }

    private void sendEmailApplicantLeaving(Long applicantId) {
        Applicant applicant = Applicant.get(applicantId)
        //getting packages affected by applicant answers deletion and sending warning about it
        List<Package> packages = getApplicantPackages(applicant)
        EasyVisaSystemMessageType warningMessageType = EasyVisaSystemMessageType.PACKAGE_APPLICANT_DELETION
        packages.each {
            String subject = String.format(warningMessageType.subject, applicant.name)
            String body = groovyPageRenderer.render(template: warningMessageType.templatePath,
                    model: [applicant: applicant])
            alertService.createPackageWarning(it, applicant, warningMessageType, body, null, null, subject)
        }
    }

    @CompileDynamic
    private List<Package> getApplicantPackages(Applicant applicant) {
        List<Package> beneficiaryPackages = Package.executeQuery('select p from Package p join p.benefits pb where pb.applicant = :applicant',
                [applicant: applicant]) as List<Package>
        List<Package> petitionerPackages = Package.executeQuery('select p from Package p join p.benefits pb where (p.petitioner is not null AND p.petitioner.applicant = :applicant)',
                [applicant: applicant]) as List<Package>
        List<Package> applicantPackages = (beneficiaryPackages << petitionerPackages).flatten()
        return applicantPackages;
    }

    /**
     * Returns default questions for further usage possibilities after deleting an Applicant answers.
     * This is a workaround due to transactions issues.
     * @param applicantId an applicant id
     */
    void restoreDefaultQuestions(Long applicantId) {
        Applicant applicant = Applicant.get(applicantId)
        //getting packages affected by applicant answers deletion and sending warning about it
        List<Package> packages = getApplicantPackages(applicant)
        packages.each {
            //return default questions for further usage possibilities
            if (it.status != PackageStatus.LEAD) {
                packageQuestionnaireService.syncDefaultQuestionnaireAnswers(it, applicant)
            }
        }
    }

    @CompileDynamic
    private void deleteApplicantPackageFiles(Applicant applicant) {
        List<DocumentAttachment> attachments = DocumentAttachment.createCriteria().list {
            'documentReference' {
                eq('applicant', applicant)
            }
        } as List<DocumentAttachment>
        List<BaseDocument> baseDocuments = []
        attachments.each {
            baseDocuments << it.documentReference
            it.delete(failOnError: true)
            fileService.deleteEasyVisaFile(it.file)
        }
        baseDocuments.each {
            it.delete(failOnError: true)
        }
    }

    void deleteApplicantData(Applicant applicant) {
        Applicant currentApplicant = findApplicantByUser(springSecurityService.currentUserId as Long)
        if (!currentApplicant) {
            throw ExceptionUtils.createAccessDeniedException('user.exist.but.not.applicant')
        }
        List<Package> packages = Package.executeQuery('select p from Package p join p.benefits b where b.applicant = :applicant',
                [applicant: applicant]) as List<Package>
        if (!packages || !packages[0].doesUserBelongToPackage(currentApplicant)) {
            throw ExceptionUtils.createAccessDeniedException('package.not.found.with.id')
        }
        deleteApplicant(applicant)
        restoreDefaultQuestions(applicant.id)
    }

    /**
     * Deletes an applicant and profile associated. This can be used to delete applicant which is not registered in the
     * system.
     * @param applicant Applicant object to be deleted
     */
    void deleteApplicantWithOutUser(Applicant applicant) {
        Profile profile = applicant.profile
        applicant.delete()
        profile.delete()
    }

    /**
     * Create a package transfer request initiated by an applicant.
     * @param aPackage package to transfer
     * @param representative new legal representative
     * @param requestedBy requested user
     * @param toOrg new organization
     */
    void transferPackage(Package aPackage, LegalRepresentative representative, User requestedBy, Organization toOrg) {
        if ([PackageStatus.LEAD, PackageStatus.BLOCKED, PackageStatus.TRANSFERRED].contains(aPackage.status)) {
            throw ExceptionUtils.createUnProcessableDataException('applicant.blocked.package.transfer')
        }
        Organization fromOrg = aPackage.organization
        LegalRepresentative currentAssignee = aPackage.currentAssignee.representative
        Profile requestedByProfile = requestedBy.profile
        ApplicantPackageTransferRequest request = new ApplicantPackageTransferRequest(aPackage: aPackage,
                representative: representative, requestedBy: requestedByProfile)
        request.with {
            oldOrganization = fromOrg
            oldAssignee = currentAssignee
            representativeOrganization = toOrg
        }
        request.save(failOnError: true)
        Applicant applicant = findApplicantByUser(requestedBy.id)
        if (aPackage.doesUserBelongToPackage(applicant) && applicant == aPackage.client) {
            alertService.createProcessRequestAlert(request, APPLICANT_PACKAGE_TRANSFER_REQUEST,
                    representative.profile.user, requestedByProfile.fullName)
            alertService.createPackageWarning(aPackage, applicant, APPLICANT_PACKAGE_TRANSFER_REQUEST_OLD_LEGAL_REP,
                    null, null, null, null, requestedByProfile.fullName)
            asyncService.runAsyncDelayed({
                sendAttorneyPackageTransferAlert(requestedBy.profile.fullName, requestedByProfile.title, aPackage.id, request)
            }, "Send email of creating transfer request by Applicant [$applicant.id]")

        } else {
            throw ExceptionUtils.createPackageAccessDeniedException('package.petitioner.transfer')
        }
    }

    private void sendAttorneyPackageTransferAlert(String clientFullName, String clientName, Long packageId, ApplicantPackageTransferRequest request) {
        EasyVisaSystemMessageType alertTypeOldAttorney = APPLICANT_PACKAGE_TRANSFER_REQUEST_OLD_LEGAL_REP_ALERT
        Package aPackage = Package.get(packageId)
        String body = alertService.renderTemplate(alertTypeOldAttorney.templatePath, [clientFullName: clientFullName, aPackage: aPackage])
        alertService.createProcessRequestAlert(request, alertTypeOldAttorney,
                aPackage.attorney.profile.user, EvSystemMessage.EASYVISA_SOURCE, body,
                String.format(alertTypeOldAttorney.subject, clientName))
    }

    /**
     * Find applicant from registration token
     * @param token registration token
     * @return Applicant This returns Applicant instance if found
     */
    @CompileDynamic
    Applicant findApplicantFromRegToken(String token) {
        List<Applicant> applicants = Applicant.createCriteria().list {
            'profile' {
                eq('easyVisaId', RegistrationCode.findByToken(token)?.easyVisaId)
            }
        } as List<Applicant>
        Applicant result = null
        if (applicants) {
            result = applicants.last()
        }
        result
    }
}
