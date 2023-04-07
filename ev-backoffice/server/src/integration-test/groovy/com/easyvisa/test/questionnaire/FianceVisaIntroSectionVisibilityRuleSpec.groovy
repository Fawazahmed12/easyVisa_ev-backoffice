package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.*
import com.easyvisa.utils.AnswerListStub
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.QuestionnaireTestDBSetupUtility
import com.easyvisa.utils.TestMockUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import java.util.stream.Collectors

import static org.junit.Assert.*

@Integration
class FianceVisaIntroSectionVisibilityRuleSpec extends TestMockUtils {

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

    void testK1CategoryIntroSectionQuestionnaire() throws Exception {
        given:
        String sectionId = "Sec_1"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.client
        List<Answer> answerList = AnswerListStub.answerList(aPackage.id, applicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id, sectionId,
                answerList)

        when:
        List<Answer> introSectionAnswerList = this.answerService.fetchAnswers(aPackage.id, applicant.id, [sectionId])
        SectionNodeInstance sectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id,
                        sectionId, introSectionAnswerList)
        assertNotNull(sectionInstance)

        then:
        assertK1CategoryIntroSectionQuestions(aPackage, sectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertK1CategoryIntroSectionQuestions(Package aPackage, SectionNodeInstance sectionInstance) {
        List<EasyVisaNodeInstance> subsectionInstanceList = sectionInstance.getChildren()
        assertEquals(2, subsectionInstanceList.size())

        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) subsectionInstanceList.get(0)
        assertEquals('Previous Immigration (Visa) Petitions You Filed for Another Person', subsection1Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = subsection1Instance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())
        QuestionNodeInstance everFilledPetitionQuestion = questionNodeInstanceList.get(0)

        assertSubSection1QuestionAnswers(everFilledPetitionQuestion)//check direct question answers
        assertK1CategorySubSectionInstance1RepeatingGroupAnswers(everFilledPetitionQuestion)
    }

    private void assertK1CategorySubSectionInstance1RepeatingGroupAnswers(QuestionNodeInstance everFilledPetitionQuestion) {
        List<RepeatingQuestionGroupNodeInstance> repeatingQuestionGroupNodeInstanceList = everFilledPetitionQuestion.getChildren().stream()
                .filter({ nodeInstance -> (nodeInstance instanceof RepeatingQuestionGroupNodeInstance) })
                .map({ nodeInstance -> (RepeatingQuestionGroupNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, repeatingQuestionGroupNodeInstanceList.size())

        RepeatingQuestionGroupNodeInstance repeatingQuestionGroupNodeInstance1 = repeatingQuestionGroupNodeInstanceList.get(0)
        assertEquals(10, repeatingQuestionGroupNodeInstance1.getChildren().size())
        List<EasyVisaNodeInstance> repeatingQuestionGroupNodeInstance1Children = repeatingQuestionGroupNodeInstance1.getChildren().stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())
        assertEquals(6, repeatingQuestionGroupNodeInstance1Children.size())

        EasyVisaNodeInstance firstNameEasyVisInstance = repeatingQuestionGroupNodeInstance1Children.get(0)
        assertRepeatingQuestionAnswerValueAndPath(firstNameEasyVisInstance, 'Given Name (First name)', 'John', 'Sec_1/SubSec_1/Q_2/0')
        EasyVisaNodeInstance middleNameEasyVisInstance = repeatingQuestionGroupNodeInstance1Children.get(1)
        assertRepeatingQuestionAnswerValueAndPath(middleNameEasyVisInstance, 'Middle Name (Do Not Abbreviate)', 'Williams', 'Sec_1/SubSec_1/Q_3/0')
        EasyVisaNodeInstance familyNameEasyVisInstance = repeatingQuestionGroupNodeInstance1Children.get(2)
        assertRepeatingQuestionAnswerValueAndPath(familyNameEasyVisInstance, 'Family Name/Last Name/Surname', 'Peer', 'Sec_1/SubSec_1/Q_4/0')
        EasyVisaNodeInstance dateOfFillingInstance = repeatingQuestionGroupNodeInstance1Children.get(3)
        assertRepeatingQuestionAnswerPath(dateOfFillingInstance, "Date of Filing", 'Sec_1/SubSec_1/Q_9/0')
        EasyVisaNodeInstance resultOfPetitionInstance = repeatingQuestionGroupNodeInstance1Children.get(4)
        assertRepeatingQuestionAnswerPath(resultOfPetitionInstance, "What was the result of the petition?", 'Sec_1/SubSec_1/Q_10/0')
        EasyVisaNodeInstance alienNumberInstance = repeatingQuestionGroupNodeInstance1Children.get(5)
        assertRepeatingQuestionAnswerPath(alienNumberInstance, "Was this person issued an Alien Registration Number (A-Number)?", 'Sec_1/SubSec_1/Q_11/0')
    }


    void testF1CategoryIntroSectionQuestionnaire() throws Exception {
        given:
        String sectionId = "Sec_1"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.F1_A)

        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.client
        List<Answer> answerList = AnswerListStub.answerList(aPackage.id, applicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id, sectionId,
                answerList)

        when:
        List<Answer> introSectionAnswerList = this.answerService.fetchAnswers(aPackage.id, applicant.id, [sectionId])
        SectionNodeInstance sectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id,
                        sectionId, introSectionAnswerList)
        assertNotNull(sectionInstance)

        then:
        assertF1CategoryIntroSectionQuestions(aPackage, sectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertF1CategoryIntroSectionQuestions(Package aPackage, SectionNodeInstance sectionInstance) {
        List<EasyVisaNodeInstance> subsectionInstanceList = sectionInstance.getChildren()
        assertEquals(3, subsectionInstanceList.size())

        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) subsectionInstanceList.get(0)
        assertEquals('Previous Immigration (Visa) Petitions You Filed for Another Person', subsection1Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = subsection1Instance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())
        QuestionNodeInstance everFilledPetitionQuestion = questionNodeInstanceList.get(0)

        assertSubSection1QuestionAnswers(everFilledPetitionQuestion)//check direct question answers
        assertF1CategorySubSectionInstance1RepeatingGroupAnswers(everFilledPetitionQuestion)
    }


    private void assertF1CategorySubSectionInstance1RepeatingGroupAnswers(QuestionNodeInstance everFilledPetitionQuestion) {
        List<RepeatingQuestionGroupNodeInstance> repeatingQuestionGroupNodeInstanceList = everFilledPetitionQuestion.getChildren().stream()
                .filter({ nodeInstance -> (nodeInstance instanceof RepeatingQuestionGroupNodeInstance) })
                .map({ nodeInstance -> (RepeatingQuestionGroupNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, repeatingQuestionGroupNodeInstanceList.size())

        RepeatingQuestionGroupNodeInstance repeatingQuestionGroupNodeInstance1 = repeatingQuestionGroupNodeInstanceList.get(0)
        RepeatingQuestionGroupNodeInstance repeatingQuestionGroupNodeInstance2 = repeatingQuestionGroupNodeInstanceList.get(1)
        assertEquals(10, repeatingQuestionGroupNodeInstance1.getChildren().size())
        assertEquals(10, repeatingQuestionGroupNodeInstance2.getChildren().size())

        List<EasyVisaNodeInstance> repeatingQuestionGroupNodeInstance1Children = repeatingQuestionGroupNodeInstance1.getChildren()
        EasyVisaNodeInstance firstNameEasyVisInstance = repeatingQuestionGroupNodeInstance1Children.get(0)
        assertRepeatingQuestionAnswerValueAndPath(firstNameEasyVisInstance, 'Given Name (First name)', 'John', 'Sec_1/SubSec_1/Q_2/0')
        EasyVisaNodeInstance middleNameEasyVisInstance = repeatingQuestionGroupNodeInstance1Children.get(1)
        assertRepeatingQuestionAnswerValueAndPath(middleNameEasyVisInstance, 'Middle Name (Do Not Abbreviate)', 'Williams', 'Sec_1/SubSec_1/Q_3/0')
        EasyVisaNodeInstance familyNameEasyVisInstance = repeatingQuestionGroupNodeInstance1Children.get(2)
        assertRepeatingQuestionAnswerValueAndPath(familyNameEasyVisInstance, 'Family Name/Last Name/Surname', 'Peer', 'Sec_1/SubSec_1/Q_4/0')
        EasyVisaNodeInstance relationshipToYouEasyVisInstance = repeatingQuestionGroupNodeInstance1Children.get(3)
        assertRepeatingQuestionAnswerValueAndPath(relationshipToYouEasyVisInstance, "What was this person's relationship to you?", 'Brother', 'Sec_1/SubSec_1/Q_5/0')
        EasyVisaNodeInstance dateOfBirthInstance = repeatingQuestionGroupNodeInstance1Children.get(4)
        assertRepeatingQuestionAnswerPath(dateOfBirthInstance, "What was this person's date of birth?", 'Sec_1/SubSec_1/Q_6/0')
        EasyVisaNodeInstance cityInstance = repeatingQuestionGroupNodeInstance1Children.get(5)
        assertRepeatingQuestionAnswerPath(cityInstance, "City or town where petition was filed", 'Sec_1/SubSec_1/Q_7/0')
        EasyVisaNodeInstance stateInstance = repeatingQuestionGroupNodeInstance1Children.get(6)
        assertRepeatingQuestionAnswerPath(stateInstance, "State where petition was filed", 'Sec_1/SubSec_1/Q_8/0')
        EasyVisaNodeInstance dateOfFillingInstance = repeatingQuestionGroupNodeInstance1Children.get(7)
        assertRepeatingQuestionAnswerPath(dateOfFillingInstance, "Date of Filing", 'Sec_1/SubSec_1/Q_9/0')
        EasyVisaNodeInstance resultOfPetitionInstance = repeatingQuestionGroupNodeInstance1Children.get(8)
        assertRepeatingQuestionAnswerPath(resultOfPetitionInstance, "What was the result of the petition?", 'Sec_1/SubSec_1/Q_10/0')
        EasyVisaNodeInstance alienNumberInstance = repeatingQuestionGroupNodeInstance1Children.get(9)
        assertRepeatingQuestionAnswerPath(alienNumberInstance, "Was this person issued an Alien Registration Number (A-Number)?", 'Sec_1/SubSec_1/Q_11/0')
    }


    private void assertSubSection1QuestionAnswers(QuestionNodeInstance everFilledPetitionQuestion) {
        assertEquals('everFilledPetition', everFilledPetitionQuestion.getName())
        Answer everFilledAnswer = everFilledPetitionQuestion.getAnswer()
        assertEquals('yes', everFilledAnswer.getValue())
        assertEquals('Sec_1/SubSec_1/Q_1', everFilledAnswer.getPath())

        List<QuestionNodeInstance> questionNodeInstanceList = everFilledPetitionQuestion.getChildren().stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        QuestionNodeInstance filled2orMorePetitionsQuestion = questionNodeInstanceList.stream()
                .filter({ x -> x.getId().equals('Q_14') })
                .findFirst()
                .orElse(null)
        assertNotNull(filled2orMorePetitionsQuestion)
        Answer filled2orMorePetitionsAnswer = filled2orMorePetitionsQuestion.getAnswer()
        assertEquals('no', filled2orMorePetitionsAnswer.getValue())
    }


    protected void assertRepeatingQuestionAnswerValueAndPath(EasyVisaNodeInstance easyVisInstance, String questionDisplayText, String answerValue, String answerPath) {
        assertTrue(easyVisInstance instanceof QuestionNodeInstance)
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) easyVisInstance
        assertEquals(questionDisplayText, questionNodeInstance.getDisplayText())
        Answer answer = questionNodeInstance.getAnswer()
        assertEquals(answerValue, answer.getValue())
        assertEquals(answerPath, answer.getPath())
    }

    protected void assertRepeatingQuestionAnswerPath(EasyVisaNodeInstance easyVisInstance, String questionDisplayText, String answerPath) {
        assertTrue(easyVisInstance instanceof QuestionNodeInstance)
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) easyVisInstance
        assertEquals(questionDisplayText, questionNodeInstance.getDisplayText())
        Answer answer = questionNodeInstance.getAnswer()
        assertEquals(answerPath, answer.getPath())
    }
}
