package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.SectionNodeInstance
import com.easyvisa.questionnaire.answering.SubSectionNodeInstance
import com.easyvisa.utils.AnswerListStub
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.QuestionnaireTestDBSetupUtility
import com.easyvisa.utils.TestMockUtils
import com.easyvisa.utils.TestUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import java.util.stream.Collectors

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

@Integration
class MarriedTimesVisibilityConstraintRuleSpec extends TestMockUtils {

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

    void testFamilyInformationSectionPetitionerQuestionnaire1() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer>  answerList = 
                AnswerListStub.populatePetitionerMarriedTimesVisibilityConstraintRuleAnswerList(aPackage.id,
                        petitionerApplicant.id, 'No')
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def sectionAnswerList = answerService.fetchAnswers(aPackage.id, petitionerApplicant.id)
        SectionNodeInstance petitionerSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id,
                        petitionerApplicant.id, sectionId, sectionAnswerList)

        then:
        TestUtils.delayCurrentThread()
        assertNotNull(petitionerSectionInstance)
        assertFamilyInformationSectionPetitionerAnswers(petitionerSectionInstance, 'No', false)

        cleanup:
        testHelper.clean()
    }

    void testFamilyInformationSectionPetitionerQuestionnaire2() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer>  answerList =
                AnswerListStub.populatePetitionerMarriedTimesVisibilityConstraintRuleAnswerList(aPackage.id,
                        petitionerApplicant.id, 'Yes')
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def sectionAnswerList = answerService.fetchAnswers(aPackage.id, petitionerApplicant.id)
        SectionNodeInstance petitionerSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id,
                        petitionerApplicant.id, sectionId, sectionAnswerList)

        then:
        assertNotNull(petitionerSectionInstance)
        assertFamilyInformationSectionPetitionerAnswers(petitionerSectionInstance, 'Yes', true)

        cleanup:
        testHelper.clean()
    }

    private void assertFamilyInformationSectionPetitionerAnswers(SectionNodeInstance petitionerFamilyInformationSectionInstance, String everBeenMarriedValue, Boolean howManyTimesHaveYouBeenMarriedValue) {
        List<EasyVisaNodeInstance> petitionerFamilyInformationSubsectionInstanceList = petitionerFamilyInformationSectionInstance.getChildren()
        assertEquals(5, petitionerFamilyInformationSubsectionInstanceList.size())

        SubSectionNodeInstance maritalStatusSubsection1Instance = (SubSectionNodeInstance) petitionerFamilyInformationSubsectionInstanceList[0]
        assertEquals('SubSec_maritalStatus', maritalStatusSubsection1Instance.getId())
        List<EasyVisaNodeInstance> maritalStatusSubsectionChildren = maritalStatusSubsection1Instance.getChildren()
        assertEquals(2, maritalStatusSubsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = maritalStatusSubsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, questionNodeInstanceList.size())

        QuestionNodeInstance everBeenMarriedQuestion = questionNodeInstanceList[0]
        assertEquals('everBeenMarried', everBeenMarriedQuestion.getName())
        Answer everBeenMarriedAnswer = everBeenMarriedQuestion.getAnswer()
        assertEquals(everBeenMarriedValue, everBeenMarriedAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_maritalStatus/Q_1201', everBeenMarriedAnswer.getPath())

        QuestionNodeInstance currentMaritalStatusQuestion = questionNodeInstanceList[1]
        assertEquals('currentMaritalStatus', currentMaritalStatusQuestion.getName())
        Answer currentMaritalStatusAnswer = currentMaritalStatusQuestion.getAnswer()
        assertEquals('Married', currentMaritalStatusAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_maritalStatus/Q_1204', currentMaritalStatusAnswer.getPath())
    }

    void testFamilyInformationSectionBeneficiaryQuestionnaire1() throws Exception {
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
        List<Answer>  answerList =
                AnswerListStub.populateBeneficiaryMarriedTimesVisibilityConstraintRuleAnswerList(aPackage.id,
                        applicant.id, 'No')
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id, sectionId,
                answerList)

        when:
        def sectionAnswerList = answerService.fetchAnswers(aPackage.id, applicant.id)
        SectionNodeInstance beneficiarySectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id,
                        sectionId, sectionAnswerList)

        then:
        assertNotNull(beneficiarySectionInstance)
        assertFamilyInformationSectionBeneficiaryAnswers(beneficiarySectionInstance, 'No', false)

        cleanup:
        testHelper.clean()
    }

    void testFamilyInformationSectionBeneficiaryQuestionnaire2() throws Exception {
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
        List<Answer>  answerList =
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
        assertFamilyInformationSectionBeneficiaryAnswers(beneficiarySectionInstance, 'Yes', true)

        cleanup:
        testHelper.clean()
    }

    private void assertFamilyInformationSectionBeneficiaryAnswers(SectionNodeInstance beneficiarySectionInstance,
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
    }

}
