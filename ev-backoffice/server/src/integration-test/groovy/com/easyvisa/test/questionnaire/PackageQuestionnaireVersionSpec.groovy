package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.AppConfigType
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.PackageQuestionnaireVersion
import com.easyvisa.questionnaire.QuestionnaireCompletionStats
import com.easyvisa.questionnaire.QuestionnaireVersion
import com.easyvisa.questionnaire.answering.*
import com.easyvisa.questionnaire.model.ApplicantType
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.AnswerListStub
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.TestMockUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import spock.lang.Ignore

import java.time.LocalDate
import java.time.Month
import java.util.stream.Collectors

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

@Integration
class PackageQuestionnaireVersionSpec extends TestMockUtils {

    @Autowired
    private PackageQuestionnaireService packageQuestionnaireService
    @Autowired
    private PackageQuestionnaireVersionService packageQuestionnaireVersionService
    @Autowired
    private PackageService packageService
    @Autowired
    private AnswerService answerService
    @Autowired
    private AttorneyService attorneyService
    @Autowired
    private AdminService adminService
    @Autowired
    private StartUpService startUpService
    @Autowired
    private PaymentService paymentService
    private PaymentService paymentServiceMock = Mock(PaymentService)
    @Autowired
    private TaxService taxService
    @Autowired
    private ProfileService profileService

    private TaxService taxServiceMock = Mock(TaxService)

    @Value('${local.server.port}')
    Integer serverPort

    void setup() {
        updateToMock(packageService.accountService, paymentServiceMock, taxServiceMock)
        successPayMock(paymentServiceMock, taxServiceMock)
    }

    void cleanup() {
        updateToService(packageService.accountService, paymentService, taxService)
    }

    @Ignore
    void testQuestionnaireVersionSections() throws Exception {
        given:
        String neojVersion
        AppConfig.withNewTransaction {
            neojVersion = AppConfig.findByType(AppConfigType.NEO4J_LAST_UPDATE).value
        }

        PackageTestBuilder packageLeadHelper = PackageTestBuilder.init([
                serverPort                 : serverPort,
                adminService               : adminService,
                attorneyService            : attorneyService,
                packageService             : packageService,
                answerService              : answerService,
                packageQuestionnaireService: packageQuestionnaireService,
                profileService : profileService])
        packageLeadHelper.buildPetitionerAndBeneficiaryLeadPackage(true, ImmigrationBenefitCategory.IR1)
                .buildUsersForPackageApplicants()
        PackageTestBuilder packageOpenHelper = PackageTestBuilder.init(packageLeadHelper)
        packageOpenHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)
                .buildUsersForPackageApplicants()
        Package openPackage = packageOpenHelper.aPackage
        def oldVersionPackageSections = this.packageQuestionnaireService.fetchPackageSections(openPackage.id)
        QuestionnaireVersion questionnaireVersion = createTestQuestionnaireVersion()
        startUpService.generateQuestionnaireCompletionWeightageData()
        packageQuestionnaireVersionService.upgradePackageQuestionnaireVersionIfNeeded()

        when:
        def newVersionPackageSections = this.packageQuestionnaireService.fetchPackageSections(openPackage.id)

        then:
        assertNotNull(openPackage)
        assertNotNull(openPackage.petitioner)
        assertNotNull(openPackage.petitioner.applicant)
        assertNotNull(oldVersionPackageSections)
        assertNotNull(newVersionPackageSections)
        assertTestPackageVersionSections(oldVersionPackageSections, newVersionPackageSections)

        cleanup:
        packageLeadHelper.deletePackageOnly()
        packageOpenHelper.clean()
        removeTestQuestionnaireVersionAndItsDeps(questionnaireVersion, neojVersion)
    }

    private void removeTestQuestionnaireVersionAndItsDeps(QuestionnaireVersion questionnaireVersion, String neo4jVersion) {
        QuestionnaireVersion.withNewTransaction {
            AppConfig config = AppConfig.findByType(AppConfigType.NEO4J_LAST_UPDATE)
            config.value = neo4jVersion
            config.save(failOnError: true)
            QuestionnaireCompletionStats.findAllByQuestionnaireVersion(questionnaireVersion).each {
                it.delete(failOnError: true)
            }
            List<PackageQuestionnaireVersion> matchedPackageQuestionnaireVersionList = PackageQuestionnaireVersion.findAllByQuestionnaireVersion(questionnaireVersion)
            matchedPackageQuestionnaireVersionList.each {
                it.delete(failOnError: true, flush: true)
            }
            matchedPackageQuestionnaireVersionList.each {
                List<PackageQuestionnaireVersion> packageQuestionnaireVersionList = PackageQuestionnaireVersion.findAllByAPackage(it.aPackage, [max: 1, sort: 'dateCreated', order: 'desc'])
                PackageQuestionnaireVersion latestPackageQuestionnaireVersion = packageQuestionnaireVersionList.first()
                latestPackageQuestionnaireVersion.latest = true
                latestPackageQuestionnaireVersion.save(failOnError: true, flush: true)
            }
            questionnaireVersion?.delete(failOnError: true)
        }
    }

    private void assertTestPackageVersionSections(oldVersionPackageSections, newVersionPackageSections) {
        def oldVerPetitionerSectionData = oldVersionPackageSections.find {
            it.applicantType == ApplicantType.Petitioner.name()
        }
        def oldVerPetitionerApplicantSections = oldVerPetitionerSectionData.sections
        assertEquals(14, oldVerPetitionerApplicantSections.size())
        def oldVerBeneficiarySectionData = oldVersionPackageSections.find {
            it.applicantType == ApplicantType.Beneficiary.name()
        }
        def oldVerBeneficiaryApplicantSections = oldVerBeneficiarySectionData.sections
        assertEquals(10, oldVerBeneficiaryApplicantSections.size())

        def newVerPetitionerSectionData = newVersionPackageSections.find {
            it.applicantType == ApplicantType.Petitioner.name()
        }
        def newVerPetitionerApplicantSections = newVerPetitionerSectionData.sections
        assertEquals(0, newVerPetitionerApplicantSections.size())
        def newVerBeneficiarySectionData = newVersionPackageSections.find {
            it.applicantType == ApplicantType.Beneficiary.name()
        }
        def newVerBeneficiaryApplicantSections = newVerBeneficiarySectionData.sections
        assertEquals(5, newVerBeneficiaryApplicantSections.size())


        def nameSectionData = findSectionById(newVerBeneficiaryApplicantSections, 'Sec_nameForBeneficiary')
        assertNotNull(nameSectionData)
        assertEquals(nameSectionData.completedPercentage, new Double(67), 0)
        assertEquals(nameSectionData.weightageValue, new Double(6.67), 0)

        def addressHistorySectionData = findSectionById(newVerBeneficiaryApplicantSections, 'Sec_addressHistoryForBeneficiary')
        assertNotNull(addressHistorySectionData)
        assertEquals(addressHistorySectionData.completedPercentage, new Double(0), 0)
        assertEquals(addressHistorySectionData.weightageValue, new Double(26.67), 0)

        def contactInformationSectionData = findSectionById(newVerBeneficiaryApplicantSections, 'Sec_contactInformationForBeneficiary')
        assertNotNull(contactInformationSectionData)
        assertEquals(contactInformationSectionData.completedPercentage, new Double(8), 0)
        assertEquals(contactInformationSectionData.weightageValue, new Double(28.89), 0)

        def birthInformationSectionData = findSectionById(newVerBeneficiaryApplicantSections, 'Sec_birthInformationForBeneficiary')
        assertNotNull(birthInformationSectionData)
        assertEquals(birthInformationSectionData.completedPercentage, new Double(0), 0)
        assertEquals(birthInformationSectionData.weightageValue, new Double(13.33), 0)

        def personelInformationSectionData = findSectionById(newVerBeneficiaryApplicantSections, 'Sec_personelInformationForBeneficiary')
        assertNotNull(personelInformationSectionData)
        assertEquals(personelInformationSectionData.completedPercentage, new Double(0), 0)
        assertEquals(personelInformationSectionData.weightageValue, new Double(24.44), 0)
    }


    private def findSectionById(sections, sectionId) {
        def sectionData = sections.stream()
                .filter({ section -> section.id.equals(sectionId) })
                .findFirst()
                .orElse(null)
        return sectionData
    }


    private QuestionnaireVersion createTestQuestionnaireVersion() {
        QuestionnaireVersion.withNewTransaction {
            AppConfig config = AppConfig.findByType(AppConfigType.NEO4J_LAST_UPDATE)
            config.value = '2020-01-01T00:00:00'
            config.save(failOnError: true)
            QuestionnaireVersion questionnaireVersion =
                    new QuestionnaireVersion(questVersion: 'quest_version_test', startDate: new Date())
            return questionnaireVersion.save(failOnError: true, flush: true)
        }
    }

    @Ignore
    void testQuestionnaireVersionPersonalInformationQuestions() throws Exception {
        given:
        String neojVersion
        AppConfig.withNewTransaction {
            neojVersion = AppConfig.findByType(AppConfigType.NEO4J_LAST_UPDATE).value
        }
        String sectionId = "Sec_personelInformationForBeneficiary"
        PackageTestBuilder testHelper = PackageTestBuilder.init([
                serverPort                 : serverPort,
                adminService               : adminService,
                attorneyService            : attorneyService,
                packageService             : packageService,
                answerService              : answerService,
                packageQuestionnaireService: packageQuestionnaireService,
                profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)
                .buildUsersForPackageApplicants()

        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.principalBeneficiary
        def answerList = AnswerListStub.personelInformationBeneficiaryAnswerList(aPackage.id, applicant.id)
        testHelper.buildAnswers(answerList)

        def oldVerSectionAnswerList = answerService.fetchAnswers(aPackage.id, applicant.id)
        SectionNodeInstance oldVerSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id, sectionId, oldVerSectionAnswerList)
        QuestionnaireVersion questionnaireVersion = createTestQuestionnaireVersion()
        startUpService.generateQuestionnaireCompletionWeightageData()
        packageQuestionnaireVersionService.upgradePackageQuestionnaireVersionIfNeeded()

        when:
        def newVerSectionAnswerList = answerService.fetchAnswers(aPackage.id, applicant.id)
        SectionNodeInstance newVerSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id, sectionId, newVerSectionAnswerList)

        then:
        assertNotNull(oldVerSectionInstance)
        assertOldVerPersonelInformationAnswers(oldVerSectionInstance)
        assertNotNull(newVerSectionInstance)
        assertNewVerPersonelInformationAnswers(newVerSectionInstance)

        cleanup:
        testHelper.clean()
        removeTestQuestionnaireVersionAndItsDeps(questionnaireVersion, neojVersion)
    }

    private void assertOldVerPersonelInformationAnswers(SectionNodeInstance sectionInstance) {
        List<EasyVisaNodeInstance> subsectionInstanceList = sectionInstance.getChildren()
        assertEquals(1, subsectionInstanceList.size())

        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) subsectionInstanceList.get(0)
        assertEquals("Personal Information", subsection1Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = subsection1Instance.getChildren()
        assertEquals(4, subsectionChildren.size())

        List<RepeatingQuestionGroupNodeInstance> personelInformationForBeneficiaryRepeatingInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof RepeatingQuestionGroupNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (RepeatingQuestionGroupNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, personelInformationForBeneficiaryRepeatingInstanceList.size())

        RepeatingQuestionGroupNodeInstance personelInformationFirstInstance = personelInformationForBeneficiaryRepeatingInstanceList[0]
        List<QuestionNodeInstance> personelInformationRepeatingQuestionNodeInstanceList = personelInformationFirstInstance.getChildren().stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, personelInformationRepeatingQuestionNodeInstanceList.size())

        QuestionNodeInstance countryOfCitizenshipQuestion = personelInformationRepeatingQuestionNodeInstanceList.get(0)
        assertEquals("countryOfCitizenship", countryOfCitizenshipQuestion.getName())
        Answer countryOfCitizenshipAnswer = countryOfCitizenshipQuestion.getAnswer()
        assertEquals("Algeria", countryOfCitizenshipAnswer.getValue())
        assertEquals("Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2401/0", countryOfCitizenshipAnswer.getPath())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(3, questionNodeInstanceList.size())

        QuestionNodeInstance hasSocialSecurityCardQuestion = questionNodeInstanceList.get(0)
        assertEquals("hasSocialSecurityCard", hasSocialSecurityCardQuestion.getName())
        Answer hasSocialSecurityCardAnswer = hasSocialSecurityCardQuestion.getAnswer()
        assertEquals("No", hasSocialSecurityCardAnswer.getValue())
        assertEquals("Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2407", hasSocialSecurityCardAnswer.getPath())

        QuestionNodeInstance doesThisPersonHaveSSNBeneficiaryQuestion = questionNodeInstanceList.get(1)
        assertEquals("doesThisPersonHaveSSNBeneficiary", doesThisPersonHaveSSNBeneficiaryQuestion.getName())
        Answer doesThisPersonHaveSSNBeneficiaryAnswer = doesThisPersonHaveSSNBeneficiaryQuestion.getAnswer()
        assertEquals("No", doesThisPersonHaveSSNBeneficiaryAnswer.getValue())
        assertEquals("Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_6098", doesThisPersonHaveSSNBeneficiaryAnswer.getPath())

        QuestionNodeInstance alienRegistrationNumberQuestion = questionNodeInstanceList.get(2)
        assertEquals("alienRegistrationNumber", alienRegistrationNumberQuestion.getName())
        Answer alienRegistrationNumberAnswer = alienRegistrationNumberQuestion.getAnswer()
        assertEquals("A12345678", alienRegistrationNumberAnswer.getValue())
        assertEquals("Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2412", alienRegistrationNumberAnswer.getPath())
    }

    private void assertNewVerPersonelInformationAnswers(SectionNodeInstance sectionInstance) {
        List<EasyVisaNodeInstance> subsectionInstanceList = sectionInstance.getChildren()
        assertEquals(1, subsectionInstanceList.size())

        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) subsectionInstanceList.get(0)
        assertEquals("Personal Information", subsection1Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = subsection1Instance.getChildren()
        assertEquals(9, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(9, questionNodeInstanceList.size())


        QuestionNodeInstance passportNumberQuestion = questionNodeInstanceList.get(0)
        assertEquals("passportNumber", passportNumberQuestion.getName())
        Answer passportNumberAnswer = passportNumberQuestion.getAnswer()
        assertEquals("MGG5311089", passportNumberAnswer.getValue())
        assertEquals("Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2402", passportNumberAnswer.getPath())

        QuestionNodeInstance haveYouBeenIssuedTravelDocNoQuestion = questionNodeInstanceList.get(1)
        assertEquals("haveYouBeenIssuedTravelDocNo", haveYouBeenIssuedTravelDocNoQuestion.getName())
        Answer haveYouBeenIssuedTravelDocNoAnswer = haveYouBeenIssuedTravelDocNoQuestion.getAnswer()
        assertEquals("No", haveYouBeenIssuedTravelDocNoAnswer.getValue())
        assertEquals("Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2403", haveYouBeenIssuedTravelDocNoAnswer.getPath())

        QuestionNodeInstance countryOfIssuanceOfPassportQuestion = questionNodeInstanceList.get(2)
        assertEquals("countryOfIssuanceOfPassport", countryOfIssuanceOfPassportQuestion.getName())
        Answer countryOfIssuanceOfPassportAnswer = countryOfIssuanceOfPassportQuestion.getAnswer()
        assertEquals("Germany", countryOfIssuanceOfPassportAnswer.getValue())
        assertEquals("Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2405", countryOfIssuanceOfPassportAnswer.getPath())

        QuestionNodeInstance expirtDateOfPassportQuestion = questionNodeInstanceList.get(3)
        assertEquals("expirtDateOfPassport", expirtDateOfPassportQuestion.getName())
        Answer expirtDateOfPassportAnswer = expirtDateOfPassportQuestion.getAnswer()
        assertEquals(DateUtil.fromDate(LocalDate.of(2014, Month.JANUARY, 17)), expirtDateOfPassportAnswer.getValue())
        assertEquals("Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2406", expirtDateOfPassportAnswer.getPath())

        QuestionNodeInstance doYouHaveELISNumberQuestion = questionNodeInstanceList.get(4)
        assertEquals("doYouHaveELISNumber", doYouHaveELISNumberQuestion.getName())
        Answer doYouHaveELISNumberAnswer = doYouHaveELISNumberQuestion.getAnswer()
        assertEquals("No", doYouHaveELISNumberAnswer.getValue())
        assertEquals("Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2410", doYouHaveELISNumberAnswer.getPath())

        QuestionNodeInstance alienRegistrationNumberQuestion = questionNodeInstanceList.get(5)
        assertEquals("alienRegistrationNumber", alienRegistrationNumberQuestion.getName())
        Answer alienRegistrationNumberAnswer = alienRegistrationNumberQuestion.getAnswer()
        assertEquals("A12345678", alienRegistrationNumberAnswer.getValue())
        assertEquals("Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2412", alienRegistrationNumberAnswer.getPath())

        QuestionNodeInstance dateOfAdnissionForLPRQuestion = questionNodeInstanceList.get(6)
        assertEquals("dateOfAdnissionForLPR", dateOfAdnissionForLPRQuestion.getName())
        Answer dateOfAdnissionForLPRAnswer = dateOfAdnissionForLPRQuestion.getAnswer()
        assertEquals(DateUtil.fromDate(LocalDate.of(2008, Month.DECEMBER, 01)), dateOfAdnissionForLPRAnswer.getValue())
        assertEquals("Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2413", dateOfAdnissionForLPRAnswer.getPath())

        QuestionNodeInstance placeOfAdmissionForLPRQuestion = questionNodeInstanceList.get(7)
        assertEquals("placeOfAdmissionForLPR", placeOfAdmissionForLPRQuestion.getName())
        Answer placeOfAdmissionForLPRAnswer = placeOfAdmissionForLPRQuestion.getAnswer()
        assertEquals("DC", placeOfAdmissionForLPRAnswer.getValue())
        assertEquals("Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2414", placeOfAdmissionForLPRAnswer.getPath())

        QuestionNodeInstance genderQuestion = questionNodeInstanceList.get(8)
        assertEquals("gender", genderQuestion.getName())
        Answer genderAnswer = genderQuestion.getAnswer()
        assertEquals("Male", genderAnswer.getValue())
        assertEquals("Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2416", genderAnswer.getPath())
    }

    @Ignore
    void testQuestionnaireVersionAddressHistorySubSections() throws Exception {
        given:
        String neojVersion
        AppConfig.withNewTransaction {
            neojVersion = AppConfig.findByType(AppConfigType.NEO4J_LAST_UPDATE).value
        }
        String sectionId = "Sec_addressHistoryForBeneficiary"
        PackageTestBuilder testHelper = PackageTestBuilder.init([
                serverPort                 : serverPort,
                adminService               : adminService,
                attorneyService            : attorneyService,
                packageService             : packageService,
                answerService              : answerService,
                packageQuestionnaireService: packageQuestionnaireService,
                profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)
                .buildUsersForPackageApplicants()

        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.principalBeneficiary
        def answerList = AnswerListStub.addressHistoryBeneficaryAnswerList(aPackage.id, applicant.id)
        testHelper.buildAnswers(answerList)

        def oldVerSectionAnswerList = answerService.fetchAnswers(aPackage.id, applicant.id)
        SectionNodeInstance oldVerSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id, sectionId, oldVerSectionAnswerList)
        QuestionnaireVersion questionnaireVersion = createTestQuestionnaireVersion()
        startUpService.generateQuestionnaireCompletionWeightageData()
        packageQuestionnaireVersionService.upgradePackageQuestionnaireVersionIfNeeded()

        when:
        def newVerSectionAnswerList = answerService.fetchAnswers(aPackage.id, applicant.id)
        SectionNodeInstance newVerSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id, sectionId, newVerSectionAnswerList)

        then:
        assertNotNull(oldVerSectionInstance)
        assertOldVerAddressHistorySubSections(oldVerSectionInstance)
        assertNotNull(newVerSectionInstance)
        assertNewVerAddressHistorySubSections(newVerSectionInstance)

        cleanup:
        testHelper.clean()
        removeTestQuestionnaireVersionAndItsDeps(questionnaireVersion, neojVersion)
    }


    private void assertOldVerAddressHistorySubSections(SectionNodeInstance sectionInstance) {
        List<EasyVisaNodeInstance> subsectionChildren = sectionInstance.getChildren()
        assertEquals(5, subsectionChildren.size())

        List<SubSectionNodeInstance> subSectionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof SubSectionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (SubSectionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(5, subSectionNodeInstanceList.size())

        SubSectionNodeInstance subsection0Instance = subSectionNodeInstanceList[0]
        assertEquals("Current Physical Address", subsection0Instance.getDisplayText())
        assertEquals("SubSec_currentPhysicalAddressForBeneficiary", subsection0Instance.getId())

        SubSectionNodeInstance subsection1Instance = subSectionNodeInstanceList[1]
        assertEquals("Previous Physical Addresses", subsection1Instance.getDisplayText())
        assertEquals("SubSec_previousPhysicalAddressForBeneficiary", subsection1Instance.getId())

        SubSectionNodeInstance subsection2Instance = subSectionNodeInstanceList[2]
        assertEquals("Current Mailing Address", subsection2Instance.getDisplayText())
        assertEquals("SubSec_currentMailingAddressForBeneficiary", subsection2Instance.getId())

        SubSectionNodeInstance subsection4Instance = subSectionNodeInstanceList[3]
        assertEquals("Address Where You Intend to Live in U.S.", subsection4Instance.getDisplayText())
        assertEquals("SubSec_addressWhereYouIntendToLiveInUSForBeneficiary", subsection4Instance.getId())

        SubSectionNodeInstance subsection5Instance = subSectionNodeInstanceList[4]
        assertEquals("Physical Address Abroad", subsection5Instance.getDisplayText())
        assertEquals("SubSec_physicalAddressAbroadForBeneficiary", subsection5Instance.getId())
    }


    private void assertNewVerAddressHistorySubSections(SectionNodeInstance sectionInstance) {
        List<EasyVisaNodeInstance> subsectionChildren = sectionInstance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<SubSectionNodeInstance> subSectionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof SubSectionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (SubSectionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, subSectionNodeInstanceList.size())

        SubSectionNodeInstance subsection0Instance = subSectionNodeInstanceList[0]
        assertEquals("Current Physical Address", subsection0Instance.getDisplayText())
        assertEquals("SubSec_currentPhysicalAddressForBeneficiary", subsection0Instance.getId())
    }
}


