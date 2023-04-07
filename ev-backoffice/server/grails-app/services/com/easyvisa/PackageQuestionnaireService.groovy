package com.easyvisa

import com.easyvisa.dto.PackageApplicantProgressDto
import com.easyvisa.enums.*
import com.easyvisa.questionnaire.*
import com.easyvisa.questionnaire.answering.*
import com.easyvisa.questionnaire.answering.rule.AnswerEvaluationContext
import com.easyvisa.questionnaire.answering.rule.FormQuestionEvaluationContext
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.SectionVisibilityRuleEvaluationContext
import com.easyvisa.questionnaire.dto.AnswerItemDto
import com.easyvisa.questionnaire.dto.CompletionWarningDto
import com.easyvisa.questionnaire.dto.FieldItemDto
import com.easyvisa.questionnaire.dto.QuestionnaireResponseDto
import com.easyvisa.questionnaire.model.*
import com.easyvisa.questionnaire.services.*
import com.easyvisa.questionnaire.services.formly.FormlyAnswerBuilder
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.DateUtils
import com.easyvisa.utils.ExceptionUtils
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional
import grails.plugin.json.view.JsonViewTemplateEngine
import groovy.transform.CompileDynamic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.time.LocalDate

@Service
@GrailsCompileStatic
class PackageQuestionnaireService {

    @Autowired
    PackageService packageService

    @Autowired
    AnswerService answerService

    @Autowired
    QuestionnaireService questionnaireService

    @Autowired
    QuestionRelationshipMappingService questionRelationshipMappingService

    @Autowired
    SectionCompletionStatusService sectionCompletionStatusService

    @Autowired
    ApplicantService applicantService

    @Autowired
    ContinuationSheetService continuationSheetService

    @Autowired
    AnswerVisibilityEvaluatorService answerVisibilityEvaluatorService

    @Autowired
    AlertService alertService

    @Autowired
    DocumentService documentService


    @Autowired
    AsyncService asyncService

    @Autowired
    JsonViewTemplateEngine jsonTemplateEngine

    @Autowired
    RuleActionHandler ruleActionHandler

    LocalDate defaultCurrentDate = DateUtil.today()
    DisplayTextLanguage defaultDisplayTextLanguage = DisplayTextLanguage.defaultLanguage

    List<EasyVisaNodeInstance> getExcludedPercentageCalculationQuestions(SectionNodeInstance sectionNodeInstance) {
        def questionNodeFilter = { EasyVisaNodeInstance easyVisaNodeInstance ->
            if (easyVisaNodeInstance instanceof QuestionNodeInstance) {
                QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) easyVisaNodeInstance
                return questionNodeInstance.hasExcludeFromPercentageCalculation()
            }
            return false
        }
        List<EasyVisaNodeInstance> flattenedVisibleNodeList = sectionNodeInstance.flattenCollect({ EasyVisaNodeInstance easyVisaNodeInstance -> easyVisaNodeInstance.isVisibility() })
        List<EasyVisaNodeInstance> questionNodeList = flattenedVisibleNodeList.findAll { questionNodeFilter(it) }
        return questionNodeList
    }


    @Transactional
    SectionNodeInstance questionGraphByBenefitCategoryAndSection(Long packageId, Long applicantId,
                                                                 String sectionId, List<Answer> answerList,
                                                                 DisplayTextLanguage displayTextLanguage = this.defaultDisplayTextLanguage,
                                                                 LocalDate currentDate = this.defaultCurrentDate) {
        ImmigrationBenefitCategory directBenefitCategory = packageService.getDirectBenefitCategory(packageId)
        String benefitCategoryId = directBenefitCategory.getEasyVisaId()
        questionnaireService.questionGraphByBenefitCategoryAndSection(packageId, applicantId,
                sectionId, answerList, benefitCategoryId, displayTextLanguage, currentDate)
    }

    @Transactional
    FieldItemDto fetchFormlyQuestionnaire(Long packageId, Long applicantId, String sectionId,
                                          LocalDate currentDate = this.defaultCurrentDate, DisplayTextLanguage displayTextLanguage = this.defaultDisplayTextLanguage) {
        ImmigrationBenefitCategory directBenefitCategory = packageService.getDirectBenefitCategory(packageId)
        String benefitCategoryId = directBenefitCategory.getEasyVisaId()
        questionnaireService.fetchFormlyQuestionnaire(packageId, applicantId, sectionId, benefitCategoryId,
                displayTextLanguage, currentDate)
    }

    @Transactional
    AnswerItemDto fetchFormlyAnswers(Long packageId, Long applicantId, String sectionId,
                                     LocalDate currentDate = this.defaultCurrentDate,
                                     DisplayTextLanguage displayTextLanguage = this.defaultDisplayTextLanguage) {
        List<Answer> answerList = Answer.findAllByPackageIdAndApplicantIdAndSectionId(packageId, applicantId, sectionId)
        SectionNodeInstance sectionNodeInstance = this.questionGraphByBenefitCategoryAndSection(packageId, applicantId, sectionId, answerList, displayTextLanguage, currentDate)
        return this.buildFormlyAnswers(sectionNodeInstance)
    }

    @Transactional
    CompletionWarningDto fetchQuestionnaireCompletionWarning(Long packageId, Long applicantId,
                                                             String sectionId, LocalDate currentDate) {
        Section sectionNode = this.getSectionNode(packageId, sectionId)
        if (sectionNode.sectionCompletionRule != null) {
            List<Answer> answerList = Answer.findAllByPackageIdAndApplicantIdAndSectionId(packageId, applicantId, sectionId)
            SectionNodeInstance sectionNodeInstance = this.questionGraphByBenefitCategoryAndSection(packageId, applicantId, sectionId, answerList,
                    this.defaultDisplayTextLanguage, currentDate)
            return questionnaireService.fetchQuestionnaireCompletionWarning(packageId, applicantId, sectionNodeInstance, answerList)
        }
        return new CompletionWarningDto()
    }

    @Transactional
    AnswerItemDto buildFormlyAnswers(SectionNodeInstance sectionNodeInstance) {
        FormlyAnswerBuilder formlyAnswerBuilder = new FormlyAnswerBuilder()
        AnswerItemDto sectionAnswerDto = formlyAnswerBuilder.toAnswerItem(sectionNodeInstance)
        return sectionAnswerDto
    }

    @Transactional
    PdfFieldExpressionInfo fetchPdfFieldExpressions(PdfFieldPrintingParams pdfFieldPrintingParams) {
        String pdfFieldPrintingFormId = findPdfFieldPrintingFormId(pdfFieldPrintingParams)
        List<PdfFieldDetail> pdfFieldDetailList = fetchAllPdfFieldDetailsByForm(pdfFieldPrintingParams.packageId, pdfFieldPrintingFormId)
        questionnaireService.fetchPdfFieldExpressions(pdfFieldPrintingParams.packageId, pdfFieldPrintingParams.applicantId,
                pdfFieldPrintingFormId, pdfFieldDetailList)
    }

    @Transactional
    PdfFieldExpressionInfo fetchPdfFieldExpressionsBySection(Long packageId, Long applicantId, String formId, String sectionId) {
        List<PdfFieldDetail> pdfFieldDetailList = fetchAllPdfFieldDetailsBySection(packageId, applicantId, sectionId)
        questionnaireService.fetchPdfFieldExpressions(packageId, applicantId, formId, pdfFieldDetailList)
    }


    String findPdfFieldPrintingFormId(PdfFieldPrintingParams pdfFieldPrintingParams) {
        if (pdfFieldPrintingParams.formId) {
            return pdfFieldPrintingParams.formId
        }
        QuestionnaireVersion questionnaireVersion = this.findQuestionnaireVersion(pdfFieldPrintingParams.packageId)
        Form form = questionnaireService.fetchFormByContinuationSheet(questionnaireVersion.questVersion, pdfFieldPrintingParams.continuationSheetId)
        return form.id
    }


    List<PdfFieldDetail> fetchAllPdfFieldDetailsByForm(Long packageId, String formId) {
        QuestionnaireVersion questionnaireVersion = this.findQuestionnaireVersion(packageId)
        List<Section> sectionList = questionnaireService.fetchSectionsByForm(questionnaireVersion.questVersion, formId)
        List<Answer> entirePackageAnswerList = answerService.fetchPackageAnswers(packageId)

        Package aPackage = Package.get(packageId)
        Applicant petitionerApplicant = aPackage.petitioner?.applicant
        Applicant principalBeneficiary = aPackage.getPrincipalBeneficiary()

        List<PdfFieldDetail> pdfFieldDetailList = new ArrayList<>()
        sectionList.each {
            Long applicantId = it.applicantType == ApplicantType.Petitioner.value ? petitionerApplicant?.id : principalBeneficiary?.id
            if (applicantId != null) {
                String sectionId = it.getId()
                SectionNodeInstance sectionNodeInstance = this.questionGraphByBenefitCategoryAndSection(packageId, applicantId, sectionId, entirePackageAnswerList)
                AnswerEvaluationContext answerEvaluationContext = new AnswerEvaluationContext(packageId: packageId, applicantId: applicantId, answerList: entirePackageAnswerList)
                PdfFieldCollector pdfFieldCollector = new PdfFieldCollector(answerEvaluationContext, this.answerVisibilityEvaluatorService, this.ruleActionHandler)
                List<PdfFieldDetail> sectionPdfFieldDetailList = pdfFieldCollector.toCollect(sectionNodeInstance)
                if (!sectionPdfFieldDetailList.isEmpty()) {
                    pdfFieldDetailList.addAll(sectionPdfFieldDetailList)
                }
            }
        }
        return pdfFieldDetailList
    }

    List<PdfFieldDetail> fetchAllPdfFieldDetailsBySection(Long packageId, Long applicantId, String sectionId) {
        List<Answer> sectionAnswerList = Answer.findAllByPackageIdAndApplicantIdAndSectionId(packageId, applicantId, sectionId)
        SectionNodeInstance sectionNodeInstance = this.questionGraphByBenefitCategoryAndSection(packageId, applicantId, sectionId, sectionAnswerList)
        AnswerEvaluationContext answerEvaluationContext = new AnswerEvaluationContext(packageId: packageId, applicantId: applicantId, answerList: sectionAnswerList)
        PdfFieldCollector pdfFieldCollector = new PdfFieldCollector(answerEvaluationContext, this.answerVisibilityEvaluatorService, this.ruleActionHandler)
        List<PdfFieldDetail> sectionPdfFieldDetailList = pdfFieldCollector.toCollect(sectionNodeInstance)
        return sectionPdfFieldDetailList
    }

    @Transactional
    @CompileDynamic
    List fetchPackageSections(Long packageId, LocalDate currentDate = this.defaultCurrentDate,
                              DisplayTextLanguage displayTextLanguage = this.defaultDisplayTextLanguage) {
        Package aPackage = Package.get(packageId)
        QuestionnaireVersion questionnaireVersion = this.findQuestionnaireVersion(packageId)
        def packageSections = []
        List<ImmigrationBenefit> benefits = aPackage.orderedBenefits
        ImmigrationBenefitCategory directBenefitCategory = aPackage.directBenefit.category
        String benefitCategoryId = directBenefitCategory.getEasyVisaId()
        Petitioner petitioner = aPackage.petitioner
        List<SectionCompletionStatus> sectionCompletionStatusList
        List<SectionNodeInstance> sectionNodeInstanceList
        List<QuestionnaireCompletionStats> questionnaireCompletionStatsList
        int packageSectionIndex = 0
        if (petitioner) {
            Applicant sponsorApplicant = petitioner.applicant
            sectionCompletionStatusList = SectionCompletionStatus.findAllByPackageIdAndApplicantId(packageId, sponsorApplicant.id)
            sectionNodeInstanceList = questionnaireService.sectionsByBenefitCategoryAndApplicantType(packageId, sponsorApplicant.id, benefitCategoryId,
                    ApplicantType.Petitioner.name(), displayTextLanguage, currentDate)
            questionnaireCompletionStatsList = QuestionnaireCompletionStats
                    .findAllByBenefitCategoryIdAndApplicantTypeAndQuestionnaireVersion(benefitCategoryId, ApplicantType.Petitioner.name(), questionnaireVersion)
            List petitionerSections = this.toUIData(sectionNodeInstanceList.sort {
                it.order
            }, sectionCompletionStatusList, questionnaireCompletionStatsList)
            packageSections.push([applicantType      : ApplicantType.Petitioner.name(),
                                  applicantTitle     : ApplicantType.Petitioner.name(),
                                  applicantId        : sponsorApplicant.id,
                                  applicantName      : sponsorApplicant.name,
                                  direct             : false,
                                  benefitCategoryName: "Sponsor",
                                  order              : packageSectionIndex++,
                                  completedWeightage : this.computeApplicantCompletedWeightage(petitionerSections),
                                  sections           : petitionerSections.findAll { it['visibility'] as Boolean }])
        }

        benefits.eachWithIndex { it, index ->
            ImmigrationBenefitCategory benefitCategory = it.category // Needs to be Fixed
            benefitCategoryId = benefitCategory.getEasyVisaId()
            Long applicantId = it.applicant.id
            List beneficiarySections = []
            if (it.direct) {
                sectionCompletionStatusList = SectionCompletionStatus.findAllByPackageIdAndApplicantId(packageId, applicantId)
                questionnaireCompletionStatsList = QuestionnaireCompletionStats
                        .findAllByBenefitCategoryIdAndApplicantTypeAndQuestionnaireVersion(benefitCategoryId, ApplicantType.Beneficiary.name(), questionnaireVersion)
                sectionNodeInstanceList = questionnaireService.sectionsByBenefitCategoryAndApplicantType(packageId, applicantId, benefitCategoryId,
                        ApplicantType.Beneficiary.name(), displayTextLanguage, currentDate)
                beneficiarySections = this.toUIData(sectionNodeInstanceList.sort {
                    it.order
                }, sectionCompletionStatusList, questionnaireCompletionStatsList)
            }
            packageSections.push([applicantType      : ApplicantType.Beneficiary.name(),
                                  applicantTitle     : it.direct ? ApplicantType.Beneficiary.name() : getApplicantTitle(index, benefits.size()),
                                  applicantId        : applicantId,
                                  applicantName      : it.applicant.name,
                                  direct             : it.direct,
                                  benefitCategoryName: benefitCategory.getSearchAbbreviation(),
                                  order              : packageSectionIndex++,
                                  completedWeightage : this.computeApplicantCompletedWeightage(beneficiarySections),
                                  sections           : beneficiarySections.findAll { it['visibility'] as Boolean }])
        }
        return packageSections.sort {
            it.order
        }
    }

    // If a package has more than one Derivative Beneficiary, then they should be numbered sequentially (i.e. Derivative Beneficiary 1, Derivative Beneficiary 2, Derivative Beneficiary 3)
    def getApplicantTitle(int iterationIndex, int totalBenefits) {
        ApplicantType derivativeBeneficiary = ApplicantType.Derivative_Beneficiary
        if (totalBenefits <= 2) {
            return derivativeBeneficiary.getValue()
        }
        return derivativeBeneficiary.getValue() + " " + iterationIndex
    }


    // We are using this method to display list of Forms in the bottom of 'Questionnaire' page
    def fetchQuestionnaireForms(Long packageId) {
        QuestionnaireVersion questionnaireVersion = this.findQuestionnaireVersion(packageId)
        Package aPackage= Package.get(packageId)
        ImmigrationBenefit immigrationBenefit= aPackage.getDirectBenefit()
        List<Form> allFormList = questionnaireService.findAllForms(questionnaireVersion.questVersion)
        List<Form> packageForms = documentService.findFormsByBenefitCategory(questionnaireVersion.questVersion, immigrationBenefit.category.easyVisaId)

        Map<String, Set<ContinuationSheet>> formToContinuationSheetListMapper = continuationSheetService
                 .fetchFormToContinuationSheetListMapper(questionnaireVersion.questVersion, packageForms)

        def allForm = allFormList.collect{
            constructQuestionnaireFormData(it, packageForms, formToContinuationSheetListMapper)
        }
        return allForm.sort { it['order'] as Integer }
    }


    private constructQuestionnaireFormData(Form form,
                                           List<Form> packageForms,
                                           Map<String, Set<ContinuationSheet>> formToContinuationSheetListMapper){
        def formId = form.getId()
        def isFormSelectable = (packageForms && packageForms.find { linkedForm -> linkedForm.getId().equals(formId) })
        Set<ContinuationSheet> continuationSheetSet = formToContinuationSheetListMapper[formId] ?: new TreeSet<ContinuationSheet>()
        return [
                id                   : formId,
                displayText          : form.getDisplayText(),
                order                : form.getOrder(),
                disabled             : !isFormSelectable,
                answered             : true,
                continuationSheetList: this.constructContinuationSheetDataList(continuationSheetSet.toList())
        ]
    }

    private constructContinuationSheetDataList(List<ContinuationSheet> continuationSheetList) {
        def continuationSheetDataList = continuationSheetList.collect {
            def continuationSheetId = it.getId()
            return [
                    id         : continuationSheetId,
                    order      : it.getOrder(),
                    displayText: it.getSheetName(),
                    sheetNumber: it.getSheetNumber(),
                    page       : it.getPage(),
                    part       : it.getPart(),
                    item       : it.getItem(),
            ]
        }
        return continuationSheetDataList.sort { it.order }
    }


    private List toUIData(List<SectionNodeInstance> sectionNodeInstanceList,
                          List<SectionCompletionStatus> sectionCompletionStatusList,
                          List<QuestionnaireCompletionStats> questionnaireCompletionStatsList) {
        Map<String, SectionCompletionStatus> sectionCompletionMapper = sectionCompletionStatusList.collectEntries {
            [it.sectionId, it]
        }
        Map<String, QuestionnaireCompletionStats> questionniareCompletionStatsMapper = questionnaireCompletionStatsList.collectEntries {
            [it.sectionId, it]
        }
        sectionNodeInstanceList.collect {
            SectionNodeInstance sectionNodeInstance = it
            SectionCompletionStatus sectionCompletionStatus = sectionCompletionMapper[sectionNodeInstance.id]
            QuestionnaireCompletionStats questionnaireCompletionStats = questionniareCompletionStatsMapper[sectionNodeInstance.id]
            boolean hasSectionCompleted = sectionCompletionStatus ? sectionCompletionStatus.completionState.value : false
            double completedPercentage = sectionCompletionStatus ? (sectionCompletionStatus.completedPercentage ?: 0) : 0
            boolean hasCompletionWarningRule = (sectionNodeInstance.sectionCompletionRule != null)
            return [id                      : it.id, displayText: it.displayText, shortName: it.shortName,
                    completionState         : hasSectionCompleted,
                    completedPercentage     : it.isVisibility() ? Math.round(completedPercentage) : 100,
                    weightageValue          : questionnaireCompletionStats.weightageValue,
                    hasCompletionWarningRule: hasCompletionWarningRule,
                    visibility              : it.isVisibility()]
        }
    }


    void syncAndCopyQuestionnaireAnswers(Package aPackage, LocalDate currentDate) {
        aPackage.applicants.each { Applicant applicant ->
            this.syncAndCopyQuestionnaireAnswersByApplicant(aPackage, applicant)
        }
        List packageSections = this.fetchPackageSections(aPackage.id, currentDate, this.defaultDisplayTextLanguage)
        this.updateQuestionnaireCompletionPercentage(aPackage.id, packageSections)
    }

    void syncAndCopyQuestionnaireAnswersByApplicant(Package aPackage, Applicant applicant) {
        this.validationApplicantOptInStatus(aPackage, applicant)
        this.copyApplicantRecentQuestionnaireAnswers(aPackage, applicant)
        this.syncDefaultQuestionnaireAnswers(aPackage, applicant)
    }


    private void validationApplicantOptInStatus(Package aPackage, Applicant applicant) {
        if (aPackage.status != PackageStatus.OPEN) {
            throw ExceptionUtils.createUnProcessableDataException('package.not.open.for.sync.and.copy')
        }

        if (!aPackage.doesUserBelongToPackage(applicant)) {
            throw ExceptionUtils.createAccessDeniedException('user.not.allowed.to.access.package')
        }

        Petitioner petitioner = aPackage.petitioner
        ProcessRequestState applicantOptIn = (petitioner?.applicant == applicant) ?
                petitioner.optIn : aPackage.getBenefitForApplicant(applicant)?.optIn
        if (applicantOptIn != ProcessRequestState.ACCEPTED) {
            throw ExceptionUtils.createLockedException('package.applicant.not.opt.in',
                    ErrorMessageType.MEMBERS_WITH_PENDING_OR_DENY_STATUS)
        }
    }

    @Transactional
    private void copyApplicantRecentQuestionnaireAnswers(Package aPackage, Applicant applicant) {
        if (aPackage.beneficiaries.contains(applicant)) {
            List<Package> packagesByBeneficiary = packageService.fetchPackagesByBeneficiary(applicant)
                    .findAll { it.id != aPackage.id }
            this.copyApplicantQuestionnaireAnswers(packagesByBeneficiary, applicant, aPackage)
        } else if (aPackage.petitioner?.applicant == applicant) {
            List<Package> packagesByPetitioner = packageService.fetchPackagesByPetitioner(aPackage.petitioner)
                    .findAll { it.id != aPackage.id }
            this.copyApplicantQuestionnaireAnswers(packagesByPetitioner, applicant, aPackage)
            if (packagesByPetitioner.size() == 0) {
                List<Package> packagesByPetitionerApplicant = packageService.fetchPackagesByBeneficiary(applicant)
                this.copyBeneficiaryToPetitionerQuestionnaireAnswers(packagesByPetitionerApplicant, applicant, aPackage)
            }
        }
    }


    private copyApplicantQuestionnaireAnswers(List<Package> applicantMatchedPackages, Applicant applicant, Package aPackage) {
        if (applicantMatchedPackages.size() == 0) {
            return
        }

        List<Answer> recentApplicantAnswers = answerService.fetchRecentAnswers(applicantMatchedPackages, applicant)
        List<Answer> copiedApplicantAnswers = answerService.copyApplicantAnswers(recentApplicantAnswers, aPackage)
        def sectionIdList = copiedApplicantAnswers.collect { it.sectionId }
        Set sectionIdSet = sectionIdList.toSet()
        sectionIdSet.each {
            this.sectionCompletionStatusService.updateSectionCompletionStatus(aPackage.id, applicant.id, it)
        }
    }


    private copyBeneficiaryToPetitionerQuestionnaireAnswers(List<Package> applicantMatchedPackages, Applicant applicant, Package aPackage) {
        if (applicantMatchedPackages.size() == 0) {
            return
        }


        Map<String, PetitionerBeneficiaryMapping> beneficiaryToPetitionerPathMap = new HashMap<>()
        List<PetitionerBeneficiaryMapping> petitionerBeneficiaryMappingList = PetitionerBeneficiaryMapping.findAll()
        petitionerBeneficiaryMappingList.each {
            String beneficiaryAnswerPath = [it.beneficiarySectionNodeid, it.beneficiarySubsectionNodeid, it.beneficiaryQuestionNodeid].join('/')
            beneficiaryToPetitionerPathMap[beneficiaryAnswerPath] = it
        }

        List<Answer> recentApplicantAnswers = answerService.fetchRecentAnswers(applicantMatchedPackages, applicant)
        List<Answer> beneficiaryToPetitionerMappedAnswers = []
        recentApplicantAnswers.each {
            String beneficiaryAnswerPath = [it.sectionId, it.subsectionId, it.questionId].join('/')
            PetitionerBeneficiaryMapping petitionerBeneficiaryMapping = beneficiaryToPetitionerPathMap[beneficiaryAnswerPath]
            if (petitionerBeneficiaryMapping) {
                Answer beneficiaryToPetitionerAnswer = new Answer(packageId: aPackage.id, applicantId: applicant.id, index: it.index,
                        sectionId: petitionerBeneficiaryMapping.petitionerSectionNodeid, subsectionId: petitionerBeneficiaryMapping.petitionerSubsectionNodeid,
                        questionId: petitionerBeneficiaryMapping.petitionerQuestionNodeid, value: it.value)
                beneficiaryToPetitionerMappedAnswers.push(beneficiaryToPetitionerAnswer)
            }
        }

        List<Answer> copiedApplicantAnswers = answerService.copyApplicantAnswers(beneficiaryToPetitionerMappedAnswers, aPackage)
        def sectionIdList = copiedApplicantAnswers.collect { it.sectionId }
        Set sectionIdSet = sectionIdList.toSet()
        sectionIdSet.each {
            this.sectionCompletionStatusService.updateSectionCompletionStatus(aPackage.id, applicant.id, it, this.defaultDisplayTextLanguage)
        }
    }


    @Transactional
    void syncDefaultQuestionnaireAnswers(Package aPackage, Applicant applicant) {
        List packageSections = fetchPackageSections(aPackage.id)
        Map packageApplicantSectionData = packageSections.find { it['applicantId'] == applicant.id } as Map
        List applicantSections = packageApplicantSectionData.sections as List
        List<String> sectionIdList = applicantSections.collect { it['id'] } as List<String>
        Set<String> packageSectionIdList = sectionIdList.toSet()
        this.populateQuestionnaireAnswersBySection(aPackage, packageSectionIdList)
    }


    @Transactional
    void populateDefaultQuestionnaireAnswers(Package aPackage, LocalDate currentDate) {
        Set<String> packageSectionIdList = this.findAllApplicantSections(aPackage)
        this.populateQuestionnaireAnswersBySection(aPackage, packageSectionIdList)

        List packageSections = this.fetchPackageSections(aPackage.id, currentDate, this.defaultDisplayTextLanguage)
        this.updateQuestionnaireCompletionPercentage(aPackage.id, packageSections)
    }

    @Transactional
    private Set<String> findAllApplicantSections(Package aPackage) {
        List packageSections = fetchPackageSections(aPackage.id)
        Set<String> packageSectionIdList = []
        packageSections.each {
            List applicantSections = it['sections'] as List
            List<String> sectionIdList = applicantSections.collect { it['id'] } as List<String>
            Set<String> sectionIdSet = sectionIdList.toSet()
            packageSectionIdList.addAll(sectionIdSet)
        }
        return packageSectionIdList
    }


    private populateQuestionnaireAnswersBySection(Package aPackage, Set<String> packageSectionIdList) {
        this.populateQuestionnaireNameDetails(aPackage, packageSectionIdList)
        this.populateQuestionnaireApplicantDetails(aPackage, packageSectionIdList)
        this.populateQuestionnaireContactInformationDetails(aPackage, packageSectionIdList)
        this.populateQuestionnaireBirthInformationDetails(aPackage, packageSectionIdList)
        this.populateQuestionnaireEmploymentStatusDetails(aPackage, packageSectionIdList)
        this.populateQuestionnaireCitizenshipStatusDetails(aPackage, packageSectionIdList)
        this.populateQuestionnairePersonalInformationDetails(aPackage, packageSectionIdList)
        this.populateQuestionnaireFamilyInformationDetails(aPackage, packageSectionIdList) // TODO..
    }

    private void populateQuestionnaireNameDetails(Package aPackage, Set<String> packageSections) {
        if (aPackage.petitioner && packageSections.contains('Sec_2')) {
            List petitionerQuestionFieldPaths = [
                    [propertyName: 'firstName', questionPath: 'Sec_2/SubSec_5/Q_32'],
                    [propertyName: 'middleName', questionPath: 'Sec_2/SubSec_5/Q_33'],
                    [propertyName: 'lastName', questionPath: 'Sec_2/SubSec_5/Q_34']
            ]
            Petitioner petitioner = aPackage.petitioner
            Applicant sponsorApplicant = petitioner.applicant
            this.populateQuestionnaireProfileDetails(aPackage, sponsorApplicant, petitionerQuestionFieldPaths)
            this.sectionCompletionStatusService.updateSectionCompletionStatus(aPackage.id, sponsorApplicant.id, 'Sec_2')
        }

        Applicant principalBeneficiary = aPackage.getPrincipalBeneficiary()
        if (principalBeneficiary && packageSections.contains('Sec_nameForBeneficiary')) {
            List applicantQuestionFieldPaths = [
                    [propertyName: 'firstName', questionPath: 'Sec_nameForBeneficiary/SubSec_currentLegalNameForBeneficiary/Q_1901'],
                    [propertyName: 'middleName', questionPath: 'Sec_nameForBeneficiary/SubSec_currentLegalNameForBeneficiary/Q_1902'],
                    [propertyName: 'lastName', questionPath: 'Sec_nameForBeneficiary/SubSec_currentLegalNameForBeneficiary/Q_1903']
            ]
            this.populateQuestionnaireProfileDetails(aPackage, principalBeneficiary, applicantQuestionFieldPaths)
            this.sectionCompletionStatusService.updateSectionCompletionStatus(aPackage.id, principalBeneficiary.id, 'Sec_nameForBeneficiary')
        }
    }

    private void populateQuestionnaireContactInformationDetails(Package aPackage, Set<String> packageSections) {
        if (aPackage.petitioner && packageSections.contains('Sec_contactInformation')) {
            List petitionerEmailQuestionFieldPaths = [
                    [propertyName: 'email', questionPath: 'Sec_contactInformation/SubSec_email/Q_86'],
            ]
            Petitioner petitioner = aPackage.petitioner
            Applicant sponsorApplicant = petitioner.applicant
            this.populateQuestionnaireProfileDetails(aPackage, sponsorApplicant, petitionerEmailQuestionFieldPaths)
            this.sectionCompletionStatusService.updateSectionCompletionStatus(aPackage.id, sponsorApplicant.id, 'Sec_contactInformation')
        }

        Applicant principalBeneficiary = aPackage.getPrincipalBeneficiary()
        if (principalBeneficiary && packageSections.contains('Sec_contactInformationForBeneficiary')) {
            List applicantEmailQuestionFieldPaths = [
                    [propertyName: 'email', questionPath: 'Sec_contactInformationForBeneficiary/SubSec_emailForBeneficiary/Q_2158']
            ]
            this.populateQuestionnaireProfileDetails(aPackage, principalBeneficiary, applicantEmailQuestionFieldPaths)

            // at least these questions need to have false values,
            // otherwise sys will consider these sections as incomplete one..
            def beneficiaryQuestionFieldPaths = [
                    [value: 'false', questionPath: 'Sec_contactInformationForBeneficiary/SubSec_mobilePhoneNumberForBeneficiary/Q_6011'],
                    [value: 'false', questionPath: 'Sec_contactInformationForBeneficiary/SubSec_intendedDaytimePhoneNumberInUSForBeneficiary/Q_6012'],
                    [value: 'false', questionPath: 'Sec_contactInformationForBeneficiary/SubSec_homePhoneNumberForBeneficiary/Q_6013'],
                    [value: 'false', questionPath: 'Sec_contactInformationForBeneficiary/SubSec_officePhoneNumberForBeneficiary/Q_6014'],
                    [value: 'false', questionPath: 'Sec_contactInformationForBeneficiary/SubSec_emailForBeneficiary/Q_6015']
            ]
            this.createDefaultAnswersIfNotExists(aPackage, principalBeneficiary, beneficiaryQuestionFieldPaths)

            this.sectionCompletionStatusService.updateSectionCompletionStatus(aPackage.id, principalBeneficiary.id, 'Sec_contactInformationForBeneficiary')
        }
    }

    private void populateQuestionnaireProfileDetails(Package packageObj, Applicant applicant, List questionFieldPaths) {
        questionFieldPaths.each {
            String propertyName = it['propertyName'] as String
            String questionPath = it['questionPath'] as String
            String[] pathInfoList = questionPath.split("/")
            Profile profile = applicant.profile
            String value = profile[propertyName]
            Answer answer = new Answer(packageId: packageObj.id, applicantId: applicant.id,
                    sectionId: pathInfoList[0], subsectionId: pathInfoList[1], questionId: pathInfoList[2],
                    value: value, path: questionPath)
            this.answerService.saveAnswer(answer)
        }
    }

    private void populateQuestionnaireApplicantDetails(Package aPackage, Set<String> packageSections) {
        if (aPackage.petitioner && packageSections.contains('Sec_contactInformation')) {
            List petitionerQuestionFieldPaths = [
                    [propertyName: 'mobileNumber', questionPath: 'Sec_contactInformation/SubSec_mobilePhoneNumber/Q_81'],
                    [propertyName: 'homeNumber', questionPath: 'Sec_contactInformation/SubSec_daytimeAndHomePhoneNumber/Q_83'],
                    [propertyName: 'workNumber', questionPath: 'Sec_contactInformation/SubSec_officePhoneNumber/Q_85']
            ]
            Petitioner petitioner = aPackage.petitioner
            Applicant sponsorApplicant = petitioner.applicant
            this.populateQuestionnaireApplicantInfoDetail(aPackage, sponsorApplicant, petitionerQuestionFieldPaths)
            this.sectionCompletionStatusService.updateSectionCompletionStatus(aPackage.id, sponsorApplicant.id, 'Sec_contactInformation')
        }


        Applicant principalBeneficiary = aPackage.getPrincipalBeneficiary()
        if (principalBeneficiary && packageSections.contains('Sec_contactInformationForBeneficiary')) {
            List beneficiaryQuestionFieldPaths = [
                    [propertyName: 'mobileNumber', questionPath: 'Sec_contactInformationForBeneficiary/SubSec_mobilePhoneNumberForBeneficiary/Q_2152'],
                    [propertyName: 'homeNumber', questionPath: 'Sec_contactInformationForBeneficiary/SubSec_homePhoneNumberForBeneficiary/Q_2155'],
                    [propertyName: 'workNumber', questionPath: 'Sec_contactInformationForBeneficiary/SubSec_officePhoneNumberForBeneficiary/Q_2157']
            ]
            this.populateQuestionnaireApplicantInfoDetail(aPackage, principalBeneficiary, beneficiaryQuestionFieldPaths)
            this.sectionCompletionStatusService.updateSectionCompletionStatus(aPackage.id, principalBeneficiary.id, 'Sec_contactInformationForBeneficiary')
        }
    }

    private void populateQuestionnaireApplicantInfoDetail(Package packageObj, Applicant applicant, List questionFieldPaths) {
        questionFieldPaths.each {
            String propertyName = it['propertyName'] as String
            String questionPath = it['questionPath'] as String
            String[] pathInfoList = questionPath.split("/")
            String value = applicant[propertyName]
            Answer answer = new Answer(packageId: packageObj.id, applicantId: applicant.id,
                    sectionId: pathInfoList[0], subsectionId: pathInfoList[1], questionId: pathInfoList[2],
                    value: value, path: questionPath)
            Boolean evaluateRule = false
            this.answerService.saveAnswer(answer, evaluateRule)
        }
    }

    private void populateQuestionnaireBirthInformationDetails(Package aPackage, Set<String> packageSections) {
        if (aPackage.petitioner && packageSections.contains('Sec_birthInformation')) {
            def petitionerEmailQuestionFieldPaths = [
                    [questionPath: 'Sec_birthInformation/SubSec_birthInformation/Q_88'],
            ]
            Petitioner petitioner = aPackage.petitioner
            Applicant sponsorApplicant = petitioner.applicant
            this.populateApplicantDOBQuestionnaireDetails(aPackage, sponsorApplicant, petitionerEmailQuestionFieldPaths)
            this.sectionCompletionStatusService.updateSectionCompletionStatus(aPackage.id, sponsorApplicant.id, 'Sec_birthInformation')
        }

        Applicant principalBeneficiary = aPackage.getPrincipalBeneficiary()
        if (principalBeneficiary && packageSections.contains('Sec_birthInformationForBeneficiary')) {
            def applicantEmailQuestionFieldPaths = [
                    [questionPath: 'Sec_birthInformationForBeneficiary/SubSec_birthInformationForBeneficiary/Q_2202']
            ]
            this.populateApplicantDOBQuestionnaireDetails(aPackage, principalBeneficiary, applicantEmailQuestionFieldPaths)
            this.sectionCompletionStatusService.updateSectionCompletionStatus(aPackage.id, principalBeneficiary.id, 'Sec_birthInformationForBeneficiary')
        }
    }

    private void populateApplicantDOBQuestionnaireDetails(Package packageObj, Applicant applicant,
                                                          def questionFieldPaths) {
        questionFieldPaths.each {
            String questionPath = it['questionPath'] as String
            String[] pathInfoList = questionPath.split("/")
            Date dateOfBirth = applicant.dateOfBirth
            if (dateOfBirth) {
                Answer answer = new Answer(packageId: packageObj.id, applicantId: applicant.id,
                        sectionId: pathInfoList[0], subsectionId: pathInfoList[1], questionId: pathInfoList[2],
                        value: DateUtil.fromDate(dateOfBirth), path: questionPath)
                this.answerService.saveAnswer(answer)
            }
        }
    }

    private void populateQuestionnaireEmploymentStatusDetails(Package aPackage, Set<String> packageSections) {
        if (aPackage.petitioner && packageSections.contains('Sec_employmentHistory')) {
            def petitionerQuestionFieldPaths = [
                    [value: '', questionPath: 'Sec_employmentHistory/SubSec_employmentStatus/Q_1008/0']
            ]
            Petitioner petitioner = aPackage.petitioner
            Applicant sponsorApplicant = petitioner.applicant
            this.createDefaultAnswersIfNotExists(aPackage, sponsorApplicant, petitionerQuestionFieldPaths)
            this.sectionCompletionStatusService.updateSectionCompletionStatus(aPackage.id, sponsorApplicant.id, 'Sec_employmentHistory')
        }

        Applicant principalBeneficiary = aPackage.getPrincipalBeneficiary()
        if (principalBeneficiary && packageSections.contains('Sec_employmentHistoryForBeneficiary')) {
            def applicantQuestionFieldPaths = [
                    [value: '', questionPath: 'Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2608/0']
            ]
            this.createDefaultAnswersIfNotExists(aPackage, principalBeneficiary, applicantQuestionFieldPaths)
            this.sectionCompletionStatusService.updateSectionCompletionStatus(aPackage.id, principalBeneficiary.id, 'Sec_employmentHistoryForBeneficiary')
        }
    }

    private void createDefaultAnswersIfNotExists(Package packageObj, Applicant applicant,
                                                 def questionFieldPaths, Boolean forceUpdate = false) {
        questionFieldPaths.each {
            String answerValue = it['value'] as String
            String questionPath = it['questionPath'] as String
            if (forceUpdate || !Answer.findByPackageIdAndApplicantIdAndPath(packageObj.id, applicant.id, questionPath)) {
                String[] pathInfoList = questionPath.split("/")
                Integer answerIndex = (pathInfoList.size() == 4) ? (pathInfoList[3] as Integer) : null
                Answer answer = new Answer(packageId: packageObj.id, applicantId: applicant.id, index: answerIndex,
                        sectionId: pathInfoList[0], subsectionId: pathInfoList[1], questionId: pathInfoList[2],
                        value: answerValue, path: questionPath)
                Boolean evaluateRule = false
                this.answerService.saveAnswer(answer, evaluateRule)
            }
        }
    }

    @Transactional
    List<PackageApplicantProgressDto> calculateProgress(Package aPackage, LocalDate currentDate) {
        List<PackageApplicantProgressDto> result = []
        String benefitCategoryId = aPackage.directBenefit.category.easyVisaId
        if (aPackage.petitioner) {
            collectApplicantProgress(aPackage, aPackage.petitioner.applicant, benefitCategoryId, ApplicantType.Petitioner, currentDate, result)
        }
        collectApplicantProgress(aPackage, aPackage.principalBeneficiary, benefitCategoryId, ApplicantType.Beneficiary, currentDate, result)
        result
    }

    private void collectApplicantProgress(Package aPackage, Applicant applicant, String benefitCategoryId,
                                          ApplicantType applicantType, LocalDate currentDate,
                                          List<PackageApplicantProgressDto> result) {
        //getting completion sections
        List<SectionCompletionStatus> completeList = SectionCompletionStatus.
                findAllByApplicantIdAndPackageId(applicant.id, aPackage.id)
        PackageApplicantProgressDto applicantProgress = new PackageApplicantProgressDto()

        applicantProgress.with {
            name = applicant.name
            it.applicantType = applicantType.value
            packageStatus = aPackage.status.displayName
            //populate statistics if the user answered on some question(s)
            if (completeList) {
                completeList.sort { it.dateCreated }
                SectionCompletionStatus firstAnswer = completeList.first()
                percentComplete = getPercentCompleted(completeList, applicantType, aPackage.id, applicant.id,
                        benefitCategoryId, currentDate)
                Date firstDate = firstAnswer.dateCreated
                elapsedDays = DateUtils.getDays(firstDate)
                dateStarted = formatDate(firstDate)
                if (percentComplete == 100) {
                    completeList.sort { it.lastUpdated }
                    elapsedDays = null
                    Date lastDate = completeList.last().dateCreated
                    totalDays = DateUtils.getDays(firstDate, lastDate)
                    dateCompleted = formatDate(lastDate)
                }
            }
        }
        result << applicantProgress
    }

    private int getPercentCompleted(List<SectionCompletionStatus> completeList, ApplicantType applicantType,
                                    Long packageId, Long applicantId, String benefitCategoryId, LocalDate currentDate) {
        QuestionnaireVersion questionnaireVersion = this.findQuestionnaireVersion(packageId)
        //getting list of sections
        List<SectionNodeInstance> sectionsList = questionnaireService.sectionsByBenefitCategoryAndApplicantType(packageId, applicantId,
                benefitCategoryId, applicantType.name(), this.defaultDisplayTextLanguage, currentDate)
        List<QuestionnaireCompletionStats> questionnaireCompletionStatsList = QuestionnaireCompletionStats
                .findAllByBenefitCategoryIdAndApplicantTypeAndQuestionnaireVersion(benefitCategoryId, applicantType.name(), questionnaireVersion)
        List petitionerSections = this.toUIData(sectionsList.sort { it.order }, completeList,
                questionnaireCompletionStatsList)
        return this.computeApplicantCompletedWeightage(petitionerSections)
    }

    private Integer computeApplicantCompletedWeightage(List applicantSections) {
        if (!applicantSections || applicantSections.size() == 0) {
            return 100
        }

        Double completedWeightage = applicantSections.sum {
            Double weightageValue = it['weightageValue'] as Double
            Long completedPercentage = it['completedPercentage'] as Long
            def result = (weightageValue * completedPercentage) / 100
            return result
        } as Double
        return Math.round(completedWeightage)
    }

    private String formatDate(Date date) {
        date.format(DateUtil.PDF_FORM_DATE_FORMAT)
    }


    private void populateQuestionnaireCitizenshipStatusDetails(Package aPackage, Set<String> packageSections) {
        Petitioner sourcePetitioner = aPackage.getPetitioner()
        if (sourcePetitioner && packageSections.contains('Sec_legalStatusInUS')) {
            this.syncCitizenshipStatusQuestionnaire(aPackage, sourcePetitioner.getCitizenshipStatus())
            this.updateCitizenshipStatusInOtherOpenPackages(aPackage)
        }
    }


    private void populateQuestionnairePersonalInformationDetails(Package aPackage, Set<String> packageSections) {
        Applicant principalBeneficiary = aPackage.getPrincipalBeneficiary()
        if (principalBeneficiary && packageSections.contains('Sec_personelInformationForBeneficiary')) {
            def applicantQuestionFieldPaths = [
                    [value: '', questionPath: 'Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2401/0']
            ]
            this.createDefaultAnswersIfNotExists(aPackage, principalBeneficiary, applicantQuestionFieldPaths)
            this.sectionCompletionStatusService.updateSectionCompletionStatus(aPackage.id, principalBeneficiary.id, 'Sec_personelInformationForBeneficiary')
        }
    }

    private void populateQuestionnaireFamilyInformationDetails(Package aPackage, Set<String> packageSections) {
        List<ImmigrationBenefit> derivativeBenefits = aPackage.getDerivativeBenefits()
        if (!packageSections.contains('Sec_familyInformationForBeneficiary') || !derivativeBenefits || derivativeBenefits.size() == 0) {
            return
        }

        ImmigrationBenefit directBenefitCategory = aPackage.getDirectBenefit()
        if (directBenefitCategory && directBenefitCategory.category == ImmigrationBenefitCategory.K1K3) {
            Applicant principalApplicant = aPackage.getPrincipalBeneficiary()
            this.populateQuestionnaireChildrenInformationDetails(aPackage)
            this.sectionCompletionStatusService.updateSectionCompletionStatus(aPackage.id, principalApplicant.id, 'Sec_familyInformationForBeneficiary')
        }
    }


    private void populateQuestionnaireChildrenInformationDetails(Package aPackage) {
        List applicantQuestionFieldPaths = [
                [value: 'yes', questionPath: 'Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2741']
        ]

        List<ImmigrationBenefit> derivativeBenefits = aPackage.getDerivativeBenefits()
        derivativeBenefits.eachWithIndex { it, index ->
            def derivativeApplicant = it.applicant
            Profile profile = derivativeApplicant.profile
            applicantQuestionFieldPaths.add([value: profile.firstName, questionPath: "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2743/${index}".toString()])
            applicantQuestionFieldPaths.add([value: profile.middleName, questionPath: "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2746/${index}".toString()])
            applicantQuestionFieldPaths.add([value: profile.lastName, questionPath: "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2749/${index}".toString()])
        }

        Applicant principalApplicant = aPackage.getPrincipalBeneficiary()
        Boolean forceUpdate = true
        this.createDefaultAnswersIfNotExists(aPackage, principalApplicant, applicantQuestionFieldPaths, forceUpdate)
    }


    @Transactional
    void updateCitizenshipStatusInOtherOpenPackages(Package aPackage) {
        Petitioner sourcePetitioner = aPackage.getPetitioner()
        List<Package> apackages = packageService.fetchPackagesByPetitionerAndStatus(sourcePetitioner, PackageStatus.OPEN)
                .findAll { it.id != aPackage.id }
        apackages.each { Package apackage ->
            Petitioner petitioner = apackage.getPetitioner()
            if (petitioner != null) {
                petitioner.setCitizenshipStatus(sourcePetitioner.citizenshipStatus)
                this.applicantService.savePetitioner(petitioner)
                this.syncCitizenshipStatusQuestionnaire(apackage, sourcePetitioner.getCitizenshipStatus())
            }
        }
    }


    private syncCitizenshipStatusQuestionnaire(Package aPackage, CitizenshipStatus citizenshipStatus) {
        String questionPath = "Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_109"
        String[] pathInfoList = questionPath.split("/")
        Petitioner petitioner = aPackage.getPetitioner()
        Applicant petitionerApplicant = petitioner.getApplicant()
        String value = this.getCitizenshipStatusQuestionnaireValue(citizenshipStatus)
        Answer answer = new Answer(packageId: aPackage.id, applicantId: petitionerApplicant.id,
                sectionId: pathInfoList[0], subsectionId: pathInfoList[1], questionId: pathInfoList[2],
                value: value, path: questionPath)
        Boolean evaluateRule = false
        this.saveAnswerAndUpdateSectionCompletionStatus(answer, evaluateRule)
    }


    @Transactional
    void saveAnswerAndUpdateSectionCompletionStatus(Answer answer, Boolean evaluateRule) {
        this.answerService.saveAnswer(answer, evaluateRule)
        this.sectionCompletionStatusService.updateSectionCompletionStatus(answer.packageId, answer.applicantId, answer.sectionId)
    }


    String getCitizenshipStatusQuestionnaireValue(CitizenshipStatus citizenshipStatus) {
        def citizenshipStatusValues = [
                'U.S. Citizen'             : RelationshipTypeConstants.US_CITIZEN.value,
                'Alien'                    : RelationshipTypeConstants.ALIEN.value,
                'Lawful Permanent Resident': RelationshipTypeConstants.LPR.value,
                'U.S. National'            : RelationshipTypeConstants.US_NATIONAL.value
        ]
        return citizenshipStatusValues[citizenshipStatus.getDisplayName()]
    }


    @Transactional
    QuestionnaireVersion findQuestionnaireVersion(Long packageId) {
        QuestionnaireVersion questionnaireVersion = this.questionnaireService.findQuestionnaireVersion(packageId)
        return questionnaireVersion
    }

    /**
     *
     * LEAD - changes and access are not allowed to any one (i.e) nobody can access it
     * OPEN - allowed to non trainee organization users and a package applicants (i.e) relevant organization employees and applicants
     * BLOCKED - changes and access allowed to non trainee organization users (i.e) relevant organization employees
     * CLOSED - read-only access for non trainee organization users and a package applicants
     * TRANSFERRED - read-only access for all
     *
     * Remember that organization employees with the Trainee position can only view the questionnaire, not edit it.
     */
    @Transactional
    Map fetchQuestionnaireAccessState(Package aPackage, User user) {
        this.questionnaireService.fetchQuestionnaireAccessState(aPackage, user)
    }

    /**
     *
     * This method gets called from PackageQuestionnaireVersionService class, while upgrading packge version to latest.
     * During questionnaire-version upgrade, sometime need to add default questionnaire answers based on new questions
     *
     * @param aPackage
     * @param latestQuestionnaireVersion
     */
    @Transactional
    void addDefaultQuestionnaireVersionAnswers(Package aPackage, QuestionnaireVersion latestQuestionnaireVersion) {
        Set<String> packageSectionIdList = this.findAllApplicantSections(aPackage)
        this.populateQuestionnaireAnswersBySection(aPackage, packageSectionIdList, latestQuestionnaireVersion)
    }


    private populateQuestionnaireAnswersBySection(Package aPackage, Set<String> packageSectionIdList, QuestionnaireVersion latestQuestionnaireVersion) {
        if (latestQuestionnaireVersion.questVersion != "quest_version_1") {
            this.populateQuestionnaireVersion2AnswersBySection(aPackage, packageSectionIdList)
        }
    }

    private populateQuestionnaireVersion2AnswersBySection(Package aPackage, Set<String> packageSectionIdList) {
        this.populateQuestionnairePersonalInformationDetails(aPackage, packageSectionIdList)
    }

    @Transactional
    void sendNativeAlphabetWarningAfterQuestionnaireComplete(Long packageId, Long applicantId) {
        Package packageObj = Package.get(packageId)
        Applicant applicant = Applicant.get(applicantId)
        List<ImmigrationBenefit> categoriesForNativeAlphabetForms = packageObj.getOrderedBenefits().findAll { (ImmigrationBenefitCategory.categoriesForNativeAlphabetForms.contains(it.category)) }
        if (categoriesForNativeAlphabetForms.size()) {
            EasyVisaSystemMessageType warningMessageType = EasyVisaSystemMessageType.NATIVE_ALPHABET_FORM_WARNING
            String body = alertService.renderTemplate(warningMessageType.templatePath, [aPackage: packageObj])
            alertService.createPackageWarning(packageObj, applicant, warningMessageType, body)
        }
    }

    @Transactional
    void sendQuestionnaireCompletionAlert(Long packageId, Long applicantId, List packageSections) {
        Package packageObj = Package.get(packageId)
        Applicant applicant = Applicant.get(applicantId)
        Map packageSection = packageSections.find { (it['sections'] as List).size() != 0 } as Map
        List sections = packageSection.sections as List
        def sectionId = (sections[0] as Map).id

        EasyVisaSystemMessageType attorneyWarningMessageType = EasyVisaSystemMessageType.QUESTIONNAIRE_COMPLETED_TO_ATTORNEY
        String attorneyAlertBody = alertService.renderTemplate(attorneyWarningMessageType.templatePath, [aPackage: packageObj, applicantId: applicant.id, sectionId: sectionId])
        alertService.createAlert(attorneyWarningMessageType, packageObj.attorney.user, Alert.EASYVISA_SOURCE, attorneyAlertBody)

        EasyVisaSystemMessageType warningMessageType = EasyVisaSystemMessageType.QUESTIONNAIRE_COMPLETED
        String body = alertService.renderTemplate(warningMessageType.templatePath, [aPackage: packageObj, applicantId: applicant.id, sectionId: sectionId])
        alertService.createAlert(warningMessageType, applicant.profile.user, Alert.EASYVISA_SOURCE, body)
    }

    @Transactional
    void triggerQuestionnaireCompletionAlertAndWarnings(Long packageId, List packageSections) {
        Package aPackage = Package.get(packageId)
        def totalCompletedPercentage = aPackage.questionnaireCompletedPercentage
        if (totalCompletedPercentage == 100) {
            Applicant applicant = aPackage.getClient()
            asyncService.runAsync({
                sendNativeAlphabetWarningAfterQuestionnaireComplete(aPackage.id, applicant.id)
                sendQuestionnaireCompletionAlert(aPackage.id, applicant.id, packageSections)
            }, "Send Questionnaire completion for Package [${aPackage.id}] to Applicant [$applicant.id]")
        }
    }

    @Transactional
    void answerSaveCompletionHandler(Map updatedSectionInstanceData, Long packageId,
                                     LocalDate currentDate, DisplayTextLanguage displayTextLanguage) {
        updatedSectionInstanceData['packageSections'] = this.fetchPackageSections(packageId, currentDate, displayTextLanguage)
        this.updateQuestionnaireCompletionPercentage(packageId, updatedSectionInstanceData.packageSections as List)
        this.triggerQuestionnaireCompletionAlertAndWarnings(packageId, updatedSectionInstanceData.packageSections as List)
    }

    private updateQuestionnaireCompletionPercentage(Long packageId, List packageSections) {
        Double totalCompletedPercentage = this.getCompletedPercentage(packageSections)
        Package aPackage = Package.get(packageId)
        Double roundedTotalCompletedPercentage = Math.floor(totalCompletedPercentage)?.round()
        aPackage.setQuestionnaireCompletedPercentage(roundedTotalCompletedPercentage)
        this.packageService.savePackage(aPackage)
    }

    private Double getCompletedPercentage(List packageSections) {
        List validPackageSections = packageSections.findAll { !this.isDerivativeBeneficiary(it) }
        Double allSectionsCompletedPercentage = validPackageSections.sum { it['completedWeightage'] } as Double
        BigDecimal totalCompletedPercentage = (allSectionsCompletedPercentage / validPackageSections.size())
        return totalCompletedPercentage.doubleValue()
    }

    private Boolean isDerivativeBeneficiary(packageSection) {
        return (packageSection['applicantType'] == ApplicantType.Beneficiary.name() && packageSection["direct"] == false)
    }

    @Transactional
    QuestionnaireResponseDto buildQuestionnaireResponseDto(Long packageId, Long applicantId,
                                                           String sourceFieldId, Map updatedSectionInstanceData) {
        SectionNodeInstance answerPopulatedSection = updatedSectionInstanceData.sectionNodeInstance as SectionNodeInstance
        List<Answer> allAnswerList = updatedSectionInstanceData.allAnswerList as List<Answer>
        QuestionnaireResponseDto questionnaireResponseDto = new QuestionnaireResponseDto(activePackage: this.generatePackageData(packageId),
                packageSections: updatedSectionInstanceData.packageSections as List,
                sectionAnswer: this.buildFormlyAnswers(answerPopulatedSection),
                sectionQuestions: this.questionnaireService.buildFormlyQuestionnaire(allAnswerList, packageId, applicantId, answerPopulatedSection),
                sourceFieldId: sourceFieldId)
        return questionnaireResponseDto
    }


    private String generatePackageData(Long packageId) {
        Package activePackage = Package.get(packageId)
        def packageTemplate = this.jsonTemplateEngine.resolveTemplate('/_package')
        def writable = packageTemplate.make(aPackage: activePackage)
        def stringWriter = new StringWriter()
        writable.writeTo(stringWriter)
        String packageContent = stringWriter.toString()
        return packageContent
    }


    @Transactional
    Section getSectionNode(Long packageId, String sectionId) {
        QuestionnaireVersion questionnaireVersion = this.findQuestionnaireVersion(packageId)
        this.questionnaireService.getSectionNode(questionnaireVersion.questVersion, sectionId)
    }

    @Transactional
    void copyPackageAnswers(Package fromPackage, Package toPackage) {
        PackageQuestionnaireVersion fromVersion = PackageQuestionnaireVersion.findByAPackage(fromPackage)
        PackageQuestionnaireVersion toVersion = fromVersion.copy(toPackage)
        toVersion.save(failOnError: true)
        toPackage.applicants.each { toApplicant ->
            Applicant fromApplicant = fromPackage.applicants.find { it.profile.easyVisaId == toApplicant.profile.easyVisaId }
            copyApplicantData(fromApplicant, fromPackage, toApplicant, toPackage)
        }
    }

    private void copyApplicantData(Applicant fromApplicant, Package fromPackage, Applicant toApplicant, Package toPackage) {
        Answer.executeUpdate("""insert into Answer (
                                                    version, value, path,
                                                    applicantId, packageId, 
                                                    sectionId, subsectionId, questionId,
                                                    index, dateCreated, lastUpdated, createdBy, updatedBy)
                                                 select 0L, value, path,
                                                    :toApplicantId, :toPackageId, 
                                                    sectionId, subsectionId, questionId,
                                                    index, dateCreated, lastUpdated, createdBy, updatedBy
                                                 from Answer
                                                 where applicant_id = :fromApplicantId
                                                    and package_id = :fromPackageId""",
                [fromApplicantId: fromApplicant.id, fromPackageId: fromPackage.id, toApplicantId: toApplicant.id, toPackageId: toPackage.id])
        SectionCompletionStatus.executeUpdate("""insert into SectionCompletionStatus (
                                                    version, applicantId, packageId,
                                                    sectionId, completionState, completedPercentage,
                                                    dateCreated, lastUpdated)
                                                 select 0L, :toApplicantId, :toPackageId, 
                                                    sectionId, completionState, completedPercentage,
                                                    dateCreated, lastUpdated
                                                 from SectionCompletionStatus
                                                 where applicantId = :fromApplicantId
                                                    and packageId = :fromPackageId""",
                [fromApplicantId: fromApplicant.id, fromPackageId: fromPackage.id, toApplicantId: toApplicant.id, toPackageId: toPackage.id])
    }

    @Transactional
    void deletePackageAnswers(Package aPackage) {
        PackageQuestionnaireVersion.executeUpdate("delete from PackageQuestionnaireVersion where id = :id", [id: aPackage.id])
        Answer.executeUpdate("delete from Answer where packageId = :id", [id: aPackage.id])
        SectionCompletionStatus.executeUpdate("delete from SectionCompletionStatus where packageId = :id", [id: aPackage.id])
    }

    // This method checks, If the given Form is a part of the current Package's BenefitCategory,
    //                     as well as given section is part of the Form
    boolean isSectionIncluded(SectionVisibilityRuleEvaluationContext ruleEvaluationContext, PdfForm pdfForm) {
        ImmigrationBenefitCategory directBenefitCategory = packageService.getDirectBenefitCategory(ruleEvaluationContext.packageId)
        FormQuestionEvaluationContext formQuestionEvaluationContext = new FormQuestionEvaluationContext(directBenefitCategory, pdfForm, ruleEvaluationContext.sectionNodeInstance)
        return questionRelationshipMappingService.isSectionIncluded(formQuestionEvaluationContext)
    }

    // This method checks, If the given Form is part of the current Package's BenefitCategory,
    //                     as well as given question is part of the Form
    boolean isQuestionIncluded(NodeRuleEvaluationContext nodeRuleEvaluationContext, PdfForm pdfForm) {
        ImmigrationBenefitCategory directBenefitCategory = packageService.getDirectBenefitCategory(nodeRuleEvaluationContext.packageId)
        FormQuestionEvaluationContext formQuestionEvaluationContext = new FormQuestionEvaluationContext(directBenefitCategory, pdfForm, nodeRuleEvaluationContext.easyVisaNodeInstance)
        return questionRelationshipMappingService.isQuestionIncluded(formQuestionEvaluationContext)
    }

    // This method checks, If the given Form is part of the current Package's BenefitCategory,
    //                     as well as given SubSection is part of the Form
    boolean isSubSectionIncluded(NodeRuleEvaluationContext nodeRuleEvaluationContext, PdfForm pdfForm) {
        ImmigrationBenefitCategory directBenefitCategory = packageService.getDirectBenefitCategory(nodeRuleEvaluationContext.packageId)
        FormQuestionEvaluationContext formQuestionEvaluationContext = new FormQuestionEvaluationContext(directBenefitCategory, pdfForm, nodeRuleEvaluationContext.easyVisaNodeInstance)
        return questionRelationshipMappingService.isSubSectionIncluded(formQuestionEvaluationContext)
    }


    // This method checks, If any one of the given Form is part of the current Package's BenefitCategory,
    //                     as well as the given question is  part of the Form
    boolean isQuestionIncludedInAnyForm(NodeRuleEvaluationContext nodeRuleEvaluationContext, List<PdfForm> pdfForms) {
        ImmigrationBenefitCategory directBenefitCategory = packageService.getDirectBenefitCategory(nodeRuleEvaluationContext.packageId)
        return pdfForms.any { pdfForm ->
            FormQuestionEvaluationContext formQuestionEvaluationContext = new FormQuestionEvaluationContext(directBenefitCategory, pdfForm, nodeRuleEvaluationContext.easyVisaNodeInstance)
            return questionRelationshipMappingService.isQuestionIncluded(formQuestionEvaluationContext)
        }
    }

    boolean isRQGIncludedInAnyForm(NodeRuleEvaluationContext nodeRuleEvaluationContext, List<PdfForm> pdfForms) {
        ImmigrationBenefitCategory directBenefitCategory = packageService.getDirectBenefitCategory(nodeRuleEvaluationContext.packageId)
        return pdfForms.any { pdfForm ->
            FormQuestionEvaluationContext formQuestionEvaluationContext = new FormQuestionEvaluationContext(directBenefitCategory, pdfForm, nodeRuleEvaluationContext.easyVisaNodeInstance)
            return questionRelationshipMappingService.isRQGIncluded(formQuestionEvaluationContext)
        }
    }
}
