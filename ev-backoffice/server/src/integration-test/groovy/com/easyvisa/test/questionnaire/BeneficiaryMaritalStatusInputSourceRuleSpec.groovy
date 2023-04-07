package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.SectionNodeInstance
import com.easyvisa.questionnaire.answering.SubSectionNodeInstance
import com.easyvisa.questionnaire.meta.InputSourceType
import com.easyvisa.utils.AnswerListStub
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.QuestionnaireTestDBSetupUtility
import com.easyvisa.utils.TestMockUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import java.util.stream.Collectors

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

@Integration
class BeneficiaryMaritalStatusInputSourceRuleSpec extends TestMockUtils {

    @Autowired
    private PackageQuestionnaireService packageQuestionnaireService
    @Autowired
    private PackageService packageService
    @Autowired
    private AnswerService answerService
    @Autowired
    private AttorneyService attorneyService
    @Autowired
    private AdminService adminService
    @Autowired
    private PaymentService paymentService
    @Autowired
    private ProfileService profileService
    private PaymentService paymentServiceMock = Mock(PaymentService)
    @Autowired
    private TaxService taxService
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

    void testRemoveCondnCategoryBeneficiaryFamilyInformQuestionnaire() throws Exception {
        given:
        String sectionId = "Sec_familyInformationForBeneficiary"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.REMOVECOND)

        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.principalBeneficiary
        List<Answer> answerList =
                AnswerListStub.populateBeneficiaryMarriedTimesVisibilityConstraintRuleAnswerList(aPackage.id,
                        applicant.id, 'Yes')
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id, sectionId,
                answerList)

        when:
        def sectionAnswerList = answerService.fetchAnswers(aPackage.id, applicant.id)
        SectionNodeInstance beneficiarySectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id,
                        sectionId, sectionAnswerList)

        then:
        assertNotNull(beneficiarySectionInstance)
        assertRemoveCondnFamilyInformationSectionBeneficiaryAnswers(beneficiarySectionInstance, 'Yes', true)

        cleanup:
        testHelper.clean()
    }

    private void assertRemoveCondnFamilyInformationSectionBeneficiaryAnswers(SectionNodeInstance beneficiarySectionInstance,
                                                                             String everBeenMarriedValue,
                                                                             Boolean howManyTimesHaveYouBeenMarriedValue) {
        List<EasyVisaNodeInstance> beneficiaryFamilyInformationSubsectionInstanceList = beneficiarySectionInstance.getChildren()
        assertEquals(4, beneficiaryFamilyInformationSubsectionInstanceList.size())

        SubSectionNodeInstance maritalStatusSubsection1Instance = (SubSectionNodeInstance) beneficiaryFamilyInformationSubsectionInstanceList[1]
        assertEquals('SubSec_maritalStatusForBeneficiary', maritalStatusSubsection1Instance.getId())
        List<EasyVisaNodeInstance> maritalStatusSubsectionChildren = maritalStatusSubsection1Instance.getChildren()
        assertEquals(2, maritalStatusSubsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = maritalStatusSubsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, questionNodeInstanceList.size())

        QuestionNodeInstance everBeenMarriedQuestion = questionNodeInstanceList[0]
        assertEquals('haveYouEverBeenMarried', everBeenMarriedQuestion.getName())
        Answer everBeenMarriedAnswer = everBeenMarriedQuestion.getAnswer()
        assertEquals(everBeenMarriedValue, everBeenMarriedAnswer.getValue())
        assertEquals('Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2778', everBeenMarriedAnswer.getPath())

        QuestionNodeInstance currentMaritalStatusQuestion = questionNodeInstanceList[1]
        assertEquals('currentMaritalStatus', currentMaritalStatusQuestion.getName())
        Answer currentMaritalStatusAnswer = currentMaritalStatusQuestion.getAnswer()
        assertEquals('Married', currentMaritalStatusAnswer.getValue())
        assertEquals('Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2781', currentMaritalStatusAnswer.getPath())

        //Drop down list should have an option options (Single, Married, Divorced and Widowed)
        InputSourceType relationshipInputSourceType = currentMaritalStatusQuestion.getInputSourceType()
        assertNotNull(relationshipInputSourceType)
        assertEquals(4, relationshipInputSourceType.getValues().size())
    }


    void testEADCategoryBeneficiaryFamilyInformQuestionnaire() throws Exception {
        given:
        String sectionId = "Sec_familyInformationForBeneficiary"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildNoPetitionerOpenPackage(ImmigrationBenefitCategory.EAD)


        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.principalBeneficiary
        List<Answer> answerList =
                AnswerListStub.populateBeneficiaryMarriedTimesVisibilityConstraintRuleAnswerList(aPackage.id,
                        applicant.id, 'Yes')
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id, sectionId,
                answerList)

        when:
        def sectionAnswerList = answerService.fetchAnswers(aPackage.id, applicant.id)
        SectionNodeInstance beneficiarySectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id,
                        sectionId, sectionAnswerList)

        then:
        assertNotNull(beneficiarySectionInstance)
        assertEADFamilyInformationSectionBeneficiaryAnswers(beneficiarySectionInstance, 'Yes', true)

        cleanup:
        testHelper.clean()
    }

    private void assertEADFamilyInformationSectionBeneficiaryAnswers(SectionNodeInstance beneficiarySectionInstance,
                                                                     String everBeenMarriedValue,
                                                                     Boolean howManyTimesHaveYouBeenMarriedValue) {
        List<EasyVisaNodeInstance> beneficiaryFamilyInformationSubsectionInstanceList = beneficiarySectionInstance.getChildren()
        assertEquals(3, beneficiaryFamilyInformationSubsectionInstanceList.size())

        SubSectionNodeInstance maritalStatusSubsection1Instance = (SubSectionNodeInstance) beneficiaryFamilyInformationSubsectionInstanceList[0]
        assertEquals('SubSec_maritalStatusForBeneficiary', maritalStatusSubsection1Instance.getId())
        List<EasyVisaNodeInstance> maritalStatusSubsectionChildren = maritalStatusSubsection1Instance.getChildren()
        assertEquals(2, maritalStatusSubsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = maritalStatusSubsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, questionNodeInstanceList.size())

        QuestionNodeInstance everBeenMarriedQuestion = questionNodeInstanceList[0]
        assertEquals('haveYouEverBeenMarried', everBeenMarriedQuestion.getName())
        Answer everBeenMarriedAnswer = everBeenMarriedQuestion.getAnswer()
        assertEquals(everBeenMarriedValue, everBeenMarriedAnswer.getValue())
        assertEquals('Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2778', everBeenMarriedAnswer.getPath())

        QuestionNodeInstance currentMaritalStatusQuestion = questionNodeInstanceList[1]
        assertEquals('currentMaritalStatus', currentMaritalStatusQuestion.getName())
        Answer currentMaritalStatusAnswer = currentMaritalStatusQuestion.getAnswer()
        assertEquals('Married', currentMaritalStatusAnswer.getValue())
        assertEquals('Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2781', currentMaritalStatusAnswer.getPath())

        //Drop down list should have an option options (Single, Married, Divorced and Widowed)
        InputSourceType relationshipInputSourceType = currentMaritalStatusQuestion.getInputSourceType()
        assertNotNull(relationshipInputSourceType)
        assertEquals(4, relationshipInputSourceType.getValues().size())

        SubSectionNodeInstance parent1SubsectionInstance = (SubSectionNodeInstance) beneficiaryFamilyInformationSubsectionInstanceList[1]
        assertEquals('SubSec_parent1ForBeneficiary', parent1SubsectionInstance.getId())
        List<EasyVisaNodeInstance> parent1SubsectionChildren = parent1SubsectionInstance.getChildren()
        assertEquals(3, parent1SubsectionChildren.size())

        SubSectionNodeInstance parent2SubsectionInstance = (SubSectionNodeInstance) beneficiaryFamilyInformationSubsectionInstanceList[2]
        assertEquals('SubSec_parent2ForBeneficiary', parent2SubsectionInstance.getId())
        List<EasyVisaNodeInstance> parent2SubsectionChildren = parent2SubsectionInstance.getChildren()
        assertEquals(2, parent2SubsectionChildren.size())
    }


    void testK1K3CategoryBeneficiaryFamilyInformQuestionnaire() throws Exception {
        given:
        String sectionId = 'Sec_familyInformationForBeneficiary'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.principalBeneficiary
        List<Answer> answerList =
                AnswerListStub.populateBeneficiaryMarriedTimesVisibilityConstraintRuleAnswerList(aPackage.id,
                        applicant.id, 'Yes')
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id, sectionId,
                answerList)

        when:
        def sectionAnswerList = answerService.fetchAnswers(aPackage.id, applicant.id)
        SectionNodeInstance beneficiarySectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id,
                        sectionId, sectionAnswerList)

        then:
        assertNotNull(beneficiarySectionInstance)
        assertK1K3FamilyInformationSectionBeneficiaryAnswers(beneficiarySectionInstance, 'Yes', true)

        cleanup:
        testHelper.clean()
    }

    private void assertK1K3FamilyInformationSectionBeneficiaryAnswers(SectionNodeInstance beneficiarySectionInstance,
                                                                      String everBeenMarriedValue,
                                                                      Boolean howManyTimesHaveYouBeenMarriedValue) {
        List<EasyVisaNodeInstance> beneficiaryFamilyInformationSubsectionInstanceList = beneficiarySectionInstance.getChildren()
        assertEquals(5, beneficiaryFamilyInformationSubsectionInstanceList.size())

        SubSectionNodeInstance maritalStatusSubsection1Instance = (SubSectionNodeInstance) beneficiaryFamilyInformationSubsectionInstanceList.stream()
                .filter({ x -> x.getDefinitionNode().getId().equals('SubSec_maritalStatusForBeneficiary') })
                .findFirst()
                .orElse(null)
        assertNotNull(maritalStatusSubsection1Instance)
        List<EasyVisaNodeInstance> maritalStatusSubsectionChildren = maritalStatusSubsection1Instance.getChildren()
        assertEquals(2, maritalStatusSubsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = maritalStatusSubsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, questionNodeInstanceList.size())

        QuestionNodeInstance everBeenMarriedQuestion = questionNodeInstanceList[0]
        assertEquals('haveYouEverBeenMarried', everBeenMarriedQuestion.getName())
        Answer everBeenMarriedAnswer = everBeenMarriedQuestion.getAnswer()
        assertEquals(everBeenMarriedValue, everBeenMarriedAnswer.getValue())
        assertEquals('Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2778', everBeenMarriedAnswer.getPath())

        QuestionNodeInstance currentMaritalStatusQuestion = questionNodeInstanceList[1]
        assertEquals('currentMaritalStatus', currentMaritalStatusQuestion.getName())
        Answer currentMaritalStatusAnswer = currentMaritalStatusQuestion.getAnswer()
        assertEquals('Married', currentMaritalStatusAnswer.getValue())
        assertEquals('Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2781', currentMaritalStatusAnswer.getPath())

        //Drop down list should have an option options (Single, Married, Divorced, Widowed, Legally Separated and Marriage Annulled)
        InputSourceType relationshipInputSourceType = currentMaritalStatusQuestion.getInputSourceType()
        assertNotNull(relationshipInputSourceType)
        assertEquals(6, relationshipInputSourceType.getValues().size())


        SubSectionNodeInstance parent2SubsectionInstance = (SubSectionNodeInstance) beneficiaryFamilyInformationSubsectionInstanceList.stream()
                .filter({ x -> x.getDefinitionNode().getId().equals('SubSec_parent2ForBeneficiary') })
                .findFirst()
                .orElse(null)
        assertNotNull(parent2SubsectionInstance)
        List<EasyVisaNodeInstance> parent2SubsectionChildren = parent2SubsectionInstance.getChildren()
        assertEquals(8, parent2SubsectionChildren.size())
    }
}

