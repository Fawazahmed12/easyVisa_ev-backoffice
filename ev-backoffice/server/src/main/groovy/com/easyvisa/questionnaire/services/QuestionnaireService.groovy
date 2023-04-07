package com.easyvisa.questionnaire.services

import com.easyvisa.*
import com.easyvisa.enums.DisplayTextLanguage
import com.easyvisa.enums.EmployeePosition
import com.easyvisa.enums.PackageStatus
import com.easyvisa.enums.QuestionnaireDisplayNodeType
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.PackageQuestionnaireVersion
import com.easyvisa.questionnaire.QuestionnaireVersion
import com.easyvisa.questionnaire.answering.*
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import com.easyvisa.questionnaire.answering.rule.SectionVisibilityRuleEvaluationContext
import com.easyvisa.questionnaire.dto.CompletionWarningDto
import com.easyvisa.questionnaire.dto.FieldItemDto
import com.easyvisa.questionnaire.meta.InputSourceType
import com.easyvisa.questionnaire.model.*
import com.easyvisa.questionnaire.repositories.*
import com.easyvisa.questionnaire.services.formly.FormlyQuestionnaireVisitor
import com.easyvisa.questionnaire.services.formly.QuestionItemBuilder
import com.easyvisa.questionnaire.services.rule.pdfmapping.PdfMappingRuleEvaluator
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional
import grails.plugin.cache.Cacheable
import grails.plugin.springsecurity.SpringSecurityService
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.time.LocalDate
import java.util.stream.Collectors

@Service
@GrailsCompileStatic
class QuestionnaireService {

    @Autowired
    SectionDAO sectionDAO

    @Autowired
    FormQuestionDAO formQuestionDAO

    @Autowired
    ContinuationSheetService continuationSheetService

    @Autowired
    FormDAO formDAO

    @Autowired
    FormUIMetaDataDAO formUIMetaDataDAO

    @Autowired
    QuestionDAO questionDAO

    @Autowired
    BenefitCategoryDAO benefitCategoryDAO

    @Autowired
    QuestionnaireGraphBuilder sectionGraphBuilder

    @Autowired
    PdfFieldMappingBuilder pdfFieldMappingBuiler

    @Autowired
    RuleEvaluator answerEvaluator

    @Autowired
    MetaDataMapper metaDataMapper

    @Autowired
    RuleActionHandler ruleActionHandler

    @Autowired
    QuestionItemBuilder questionItemBuilder

    @Autowired
    PdfMappingRuleEvaluator pdfMappingRuleEvaluator

    SpringSecurityService springSecurityService

    ApplicantService applicantService

    AttorneyService attorneyService

    OrganizationService organizationService

    @Autowired
    PdfFieldFilteringService pdfFieldFilteringService

    @Autowired
    QuestionnaireTranslationService questionnaireTranslationService

    @Transactional
    SectionNodeInstance questionGraphByBenefitCategoryAndSection(Long packageId, Long applicantId,
                                                                 String sectionId, List<Answer> answerList,
                                                                 String benefitCategoryId, DisplayTextLanguage displayTextLanguage,
                                                                 LocalDate currentDate) {
        QuestionnaireVersion questionnaireVersion = this.findQuestionnaireVersion(packageId)

        Section section = this.sectionQuestionByBenefitCategoryAndSection(questionnaireVersion.questVersion, benefitCategoryId, sectionId)

        AnswerBindingVisitor answerBindingVisitor = new AnswerBindingVisitor(section, answerList,
                metaDataMapper, ruleActionHandler, questionnaireTranslationService, packageId, applicantId,
                displayTextLanguage, currentDate)
        section.accept(answerBindingVisitor) // Do Node Visits
        SectionNodeInstance answerPopulatedSection = answerBindingVisitor.getSectionInstance()

        VisibilityAssignmentVisitor visibilityAssignmentVisitor = new VisibilityAssignmentVisitor(packageId, applicantId, answerList,
                answerEvaluator, ruleActionHandler)
        answerPopulatedSection.accept(visibilityAssignmentVisitor)

        answerPopulatedSection.sortChildren()
        return answerPopulatedSection
    }


    @Cacheable('sectionQuestionByBenefitCategoryAndSection')
    Section sectionQuestionByBenefitCategoryAndSection(String questVersion, String benefitCategoryId, String sectionId) {
        Section section = sectionDAO.sectionQuestionByBenefitCategoryAndSection(questVersion, benefitCategoryId, sectionId)
        EasyVisaNode questionnaireGraph = sectionGraphBuilder.buildQuestionnaireGraph(section)
        return (Section) questionnaireGraph
    }


    @Transactional
    FieldItemDto fetchFormlyQuestionnaire(Long packageId, Long applicantId, String sectionId, String benefitCategoryId,
                                          DisplayTextLanguage displayTextLanguage, LocalDate currentDate) {
        List<Answer> answerList = Answer.findAllByPackageIdAndApplicantId(packageId, applicantId)
        SectionNodeInstance answerPopulatedSection = questionGraphByBenefitCategoryAndSection(packageId, applicantId, sectionId,
                answerList, benefitCategoryId, displayTextLanguage, currentDate)
        return this.buildFormlyQuestionnaire(answerList, packageId, applicantId, answerPopulatedSection)
    }


    @Transactional
    FieldItemDto buildFormlyQuestionnaire(List<Answer> answerList, Long packageId, Long applicantId,
                                          SectionNodeInstance answerPopulatedSection) {
        Package aPackage = Package.get(packageId)
        User user = springSecurityService.currentUser as User
        def questionnaireAccessState = user ? this.fetchQuestionnaireAccessState(aPackage, user) : [access: false, readOnly: true]
        FormlyQuestionnaireVisitor formlyQuestionnaireVisitor = new FormlyQuestionnaireVisitor(packageId, applicantId,
                questionnaireAccessState, answerList, metaDataMapper, questionItemBuilder, ruleActionHandler)
        formlyQuestionnaireVisitor.visit(answerPopulatedSection)
        FieldItemDto sectionDto = formlyQuestionnaireVisitor.getFormlySectionDto()
        return sectionDto
    }

    @Transactional
    List<SectionNodeInstance> sectionsByBenefitCategoryAndApplicantType(Long packageId, Long applicantId, String benefitCategoryId, String applicantType,
                                                                        DisplayTextLanguage displayTextLanguage, LocalDate currentDate) {
        QuestionnaireVersion questionnaireVersion = this.findQuestionnaireVersion(packageId)
        List<Section> sectionList = questionnaireVersion ? this.sectionsByBenefitCategoryAndApplicantType(questionnaireVersion.questVersion, benefitCategoryId, applicantType) : []
        List<SectionNodeInstance> sectionNodeInstanceList = sectionList.stream()
                .map({ section ->
                    this.buildSectionNodeInstance(section, packageId, applicantId, benefitCategoryId,
                            displayTextLanguage, currentDate)
                })
                .collect(Collectors.toList())
        return sectionNodeInstanceList
    }

    @Cacheable('sectionsByBenefitCategoryAndApplicantType')
    List<Section> sectionsByBenefitCategoryAndApplicantType(String questVersion, String benefitCategoryId, String applicantType) {
        return sectionDAO.sectionsByBenefitCategoryAndApplicantType(questVersion, benefitCategoryId, applicantType)
    }

    private SectionNodeInstance buildSectionNodeInstance(Section section, Long packageId, Long applicantId, String benefitCategoryId,
                                                         DisplayTextLanguage displayTextLanguage, LocalDate currentDate) {
        SectionNodeInstance sectionNodeInstance = new SectionNodeInstance(section, displayTextLanguage, currentDate, this.questionnaireTranslationService)
        sectionNodeInstance.setVisibility(true)
        if (StringUtils.isNotEmpty(sectionNodeInstance.getSectionVisibilityRule())) {
            SectionVisibilityRuleEvaluationContext ruleEvaluationContext = new SectionVisibilityRuleEvaluationContext(sectionNodeInstance, packageId, applicantId, benefitCategoryId)
            ruleActionHandler.updateSectionVisibilityOnSuccessfulNodeRule(sectionNodeInstance.getSectionVisibilityRule(), ruleEvaluationContext)
        }
        return sectionNodeInstance
    }


    @Cacheable('fetchSectionsByForm')
    List<Section> fetchSectionsByForm(String questVersion, String formId) {
        return sectionDAO.sectionsByForm(questVersion, formId)
    }


    @Cacheable('fetchFormByContinuationSheet')
    Form fetchFormByContinuationSheet(String questVersion, String continuationSheetId) {
        return formDAO.formByContinuationSheet(questVersion, continuationSheetId)
    }

    @Transactional
    PdfFieldExpressionInfo fetchPdfFieldExpressions(Long packageId, Long applicantId, String formId,
                                                    List<PdfFieldDetail> pdfFieldDetailList) {
        Package aPackage = Package.get(packageId)
        this.pdfFieldFilteringService.validateDerivativeInclusion(aPackage, applicantId, formId, pdfFieldDetailList)
        List<String> questionIdList = pdfFieldDetailList.collect { it.questionId }
        QuestionnaireVersion questionnaireVersion = this.findQuestionnaireVersion(packageId)
        Set<FormQuestion> formQuestionSetWithDetails = this.formQuestionsByFormAndQuestionList(questionnaireVersion.questVersion, formId, questionIdList)
        List<ContinuationSheet> continuationSheetList = continuationSheetService.fetchContinuationSheetsByForm(questionnaireVersion.questVersion, formId)
        List<PdfFieldDetail> mappedPdfFieldDetailList = pdfFieldMappingBuiler.buildPdfFieldMapping(pdfFieldDetailList, continuationSheetList, formQuestionSetWithDetails)
        ContinuationSheetHeaderInfo continuationSheetHeaderInfo = continuationSheetService.getContinuationSheetHeaderByFormId(aPackage, formId)
        PdfMappingEvaluationContext pdfMappingEvaluationContext = new PdfMappingEvaluationContext(aPackage, applicantId, formId, continuationSheetHeaderInfo, mappedPdfFieldDetailList)
        pdfMappingRuleEvaluator.evaluateMappingRules(mappedPdfFieldDetailList, pdfMappingEvaluationContext)
        return new PdfFieldExpressionInfo(pdfFieldDetailList: mappedPdfFieldDetailList, continuationSheetHeaderInfo: continuationSheetHeaderInfo)
    }

    Set<FormQuestion> formQuestionsByFormAndQuestionList(String questVersion, String formId, List<String> questionIdList) {
        Set<FormQuestion> formQuestionSetWithDetails = formQuestionDAO.formQuestionsByFormAndQuestionList(questVersion, formId, questionIdList)
        return formQuestionSetWithDetails
    }

    @Cacheable("findAllForms")
    List<Form> findAllForms(String questVersion) {
        return formDAO.findAllForms(questVersion)
    }

    @Cacheable("findAllBenefitCategory")
    List<BenefitCategory> findAllBenefitCategory(String questVersion) {
        return benefitCategoryDAO.findAllBenefitCategory(questVersion)
    }

    @Cacheable("fetchFormToSectionListMapper")
    Map<String, Set<Section>> fetchFormToSectionListMapper(String questVersion) {
        return sectionDAO.fetchFormToSectionListMapper(questVersion)
    }

    List<Map<String, EasyVisaNode>> findAllSubSections(String questVersion, String formId,String sectionId) {
        sectionDAO.subsectionByFormAndSection(questVersion, formId, sectionId)
    }

    List<Question> findQuestionByFormSubsection(String questVersion, String formId,String subsectionId) {
        questionDAO.findQuestionByFormSubsection(questVersion, formId, subsectionId)
    }

    Question findQuestionById(String questVersion,String questionId) {
        questionDAO.findByEasyVisaId(questVersion,questionId)
    }

    List<Form> findAllQuestionnaireForms(String questVersion, List<String> questionIdList) {
        return formDAO.findAllQuestionnaireForms(questVersion, questionIdList)
    }


    @Transactional
    CompletionWarningDto fetchQuestionnaireCompletionWarning(Long packageId, Long applicantId, SectionNodeInstance sectionNodeInstance, List<Answer> answerList) {
        NodeRuleEvaluationContext ruleEvaluationContext = new NodeRuleEvaluationContext(answerList, sectionNodeInstance, packageId, applicantId)
        return this.ruleActionHandler.getCompletionWarning(sectionNodeInstance.sectionCompletionRule, ruleEvaluationContext)
    }

    @Transactional
    QuestionnaireVersion findQuestionnaireVersion(Long packageId) {
        findQuestionnaireVersion(Package.findById(packageId))
    }

    @Transactional
    QuestionnaireVersion findQuestionnaireVersion(Package aPackage) {
        PackageQuestionnaireVersion latestPackageQuestionnaireVersion = PackageQuestionnaireVersion.findByAPackageAndLatest(aPackage, true)
        latestPackageQuestionnaireVersion?.questionnaireVersion
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
        if (aPackage.status == PackageStatus.LEAD) {
            return [access: false, readOnly: true]
        } else if (aPackage.status == PackageStatus.OPEN && this.hasPackageApplicantOrRelevantOrganizationEmployee(aPackage, user)) {
            OrganizationEmployee organizationEmployee = this.findOrganizationEmployeeData(aPackage, user)
            Boolean readOnlyValue = (organizationEmployee && organizationEmployee.position == EmployeePosition.TRAINEE)
            return [access: true, readOnly: readOnlyValue]
        } else if (aPackage.status == PackageStatus.BLOCKED && this.isRelevantOrganizationEmployee(aPackage, user)) {
            OrganizationEmployee organizationEmployee = this.findOrganizationEmployeeData(aPackage, user)
            Boolean readOnlyValue = (organizationEmployee.position == EmployeePosition.TRAINEE)
            return [access: true, readOnly: readOnlyValue]
        } else if (aPackage.status == PackageStatus.CLOSED && this.hasPackageApplicantOrRelevantOrganizationEmployee(aPackage, user)) {
            return [access: true, readOnly: true]
        } else if (aPackage.status == PackageStatus.TRANSFERRED) {
            return [access: true, readOnly: true]
        }
        return [access: false, readOnly: true]
    }

    private Boolean hasPackageApplicantOrRelevantOrganizationEmployee(Package aPackage, User user) {
        Applicant currentApplicant = this.applicantService.findApplicantByUser(user.id)
        if (currentApplicant && aPackage.doesUserBelongToPackage(currentApplicant)) {
            return true
        }
        return this.isRelevantOrganizationEmployee(aPackage, user)
    }

    private Boolean isRelevantOrganizationEmployee(Package aPackage, User user) {
        Organization organization = aPackage.organization
        Employee employee = this.attorneyService.findEmployeeByUser(user.id)
        if (employee && this.organizationService.doesEmployeeBelongToOrganization(employee.id, organization.id)) {
            return true
        }
        return false
    }

    private OrganizationEmployee findOrganizationEmployeeData(Package aPackage, User user) {
        Organization organization = aPackage.organization
        Employee employee = this.attorneyService.findEmployeeByUser(user.id)
        OrganizationEmployee organizationEmployee = OrganizationEmployee.findByEmployeeAndOrganization(employee, organization)
        return organizationEmployee
    }

    @Transactional
    Section getSectionNode(String questVersion, String sectionId) {
        sectionDAO.getSectionNode(questVersion, sectionId)
    }

    InputSourceType getInputSourceType(String inputTypeName, String questVersion,
                                       DisplayTextLanguage displayTextLanguage) {
        InputSourceType inputSourceType = formUIMetaDataDAO.getInputSourceTypeModel(inputTypeName)
        return this.getTranslatedInputSourceType(inputSourceType, questVersion, displayTextLanguage)
    }

    InputSourceType getTranslatedInputSourceType(InputSourceType inputSourceType, String questVersion,
                                                 DisplayTextLanguage displayTextLanguage) {
        if (inputSourceType == null) {
            return inputSourceType
        }

        List<InputSourceType.ValueMap> inputSourceValues = inputSourceType.values.collect { InputSourceType.ValueMap valueMap ->
            InputSourceType.ValueMap clonedValueMap = valueMap.clone()
            String displayText = this.questionnaireTranslationService.getTranslatorValue(questVersion,
                    QuestionnaireDisplayNodeType.INPUT_SOURCE_TYPE, clonedValueMap.getValue(), displayTextLanguage)
            clonedValueMap.setLabel(displayText)
            return clonedValueMap
        }
        return new InputSourceType(inputSourceType.getType(), inputSourceValues)
    }
}
