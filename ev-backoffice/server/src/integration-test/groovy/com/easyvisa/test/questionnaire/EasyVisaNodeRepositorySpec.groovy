package com.easyvisa.test.questionnaire

import com.easyvisa.AdminService
import com.easyvisa.AnswerService
import com.easyvisa.Applicant
import com.easyvisa.AttorneyService
import com.easyvisa.Package
import com.easyvisa.PackageQuestionnaireService
import com.easyvisa.PackageService
import com.easyvisa.PaymentService
import com.easyvisa.ProfileService
import com.easyvisa.TaxService
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
class EasyVisaNodeRepositorySpec extends TestMockUtils {

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
    private ProfileService profileService

    @Autowired
    private PaymentService paymentService
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

    void testIntroSectionQuestionnaire() throws Exception {
        given:
        String sectionId = 'Sec_1'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListStub.answerList(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        SectionNodeInstance sectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id,
                        petitionerApplicant.id, sectionId, answerList)

        then:
        assertNotNull(sectionInstance)
        assertAnswers(sectionInstance)
        assertVisibility(sectionInstance)
        assertFieldOrder(sectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertVisibility(SectionNodeInstance sectionInstance) {
        List<EasyVisaNodeInstance> subsectionInstanceList = sectionInstance.getChildren()
        assertEquals(3, subsectionInstanceList.size())

        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) subsectionInstanceList.get(0)
        assertEquals('Previous Immigration (Visa) Petitions You Filed for Another Person', subsection1Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = subsection1Instance.getChildren()

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())
        QuestionNodeInstance everFilledPetitionQuestion = questionNodeInstanceList.get(0)

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
        EasyVisaNodeInstance issuedAlienNoEasyVisInstance1Question = repeatingQuestionGroupNodeInstance1Children.get(9)
        assertEquals(1, issuedAlienNoEasyVisInstance1Question.getChildren().size())
        EasyVisaNodeInstance alienNoEasyVisInstance1Question = issuedAlienNoEasyVisInstance1Question.getChildren().get(0)
        assertTrue(issuedAlienNoEasyVisInstance1Question instanceof QuestionNodeInstance)
        QuestionNodeInstance issuedAlienNoEasyVisInstance1 = (QuestionNodeInstance) issuedAlienNoEasyVisInstance1Question
        Answer issuedAlienNoEasyVisInstance1Answer = issuedAlienNoEasyVisInstance1.getAnswer()
        assertEquals('no', issuedAlienNoEasyVisInstance1Answer.getValue())
        assertFalse(issuedAlienNoEasyVisInstance1Question.isVisibility())

        List<EasyVisaNodeInstance> repeatingQuestionGroupNodeInstance2Children = repeatingQuestionGroupNodeInstance2.getChildren()
        EasyVisaNodeInstance issuedAlienNoEasyVisInstance2Question = repeatingQuestionGroupNodeInstance2Children.get(9)
        assertEquals(1, issuedAlienNoEasyVisInstance2Question.getChildren().size())
        EasyVisaNodeInstance alienNoEasyVisInstance2Question = issuedAlienNoEasyVisInstance2Question.getChildren().get(0)
        assertTrue(issuedAlienNoEasyVisInstance2Question instanceof QuestionNodeInstance)
        QuestionNodeInstance issuedAlienNoEasyVisInstance2 = (QuestionNodeInstance) issuedAlienNoEasyVisInstance2Question
        Answer issuedAlienNoEasyVisInstance2Answer = issuedAlienNoEasyVisInstance2.getAnswer()
        assertEquals('yes', issuedAlienNoEasyVisInstance2Answer.getValue())
        assertFalse(issuedAlienNoEasyVisInstance2Question.isVisibility())
    }

    private void assertFieldOrder(SectionNodeInstance sectionInstance) {
        List<EasyVisaNodeInstance> subsectionInstanceList = sectionInstance.getChildren()
        assertEquals(3, subsectionInstanceList.size())

        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) subsectionInstanceList.get(0)
        assertEquals('Previous Immigration (Visa) Petitions You Filed for Another Person', subsection1Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsection1Children = subsection1Instance.getChildren()

        List<QuestionNodeInstance> questionNodeInstanceList = subsection1Children.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())
        QuestionNodeInstance everFilledPetitionQuestion = questionNodeInstanceList.get(0)
        List<EasyVisaNodeInstance> everFilledPetitionQuestionChildren = everFilledPetitionQuestion.getChildren()
        assertEquals(4, everFilledPetitionQuestionChildren.size())

        EasyVisaNodeInstance easyVisaNodeInstance11 = everFilledPetitionQuestionChildren.get(0)
        assertTrue(easyVisaNodeInstance11 instanceof RepeatingQuestionGroupNodeInstance)
        EasyVisaNodeInstance easyVisaNodeInstance12 = everFilledPetitionQuestionChildren.get(1)
        assertTrue(easyVisaNodeInstance12 instanceof RepeatingQuestionGroupNodeInstance)
        EasyVisaNodeInstance easyVisaNodeInstance13 = everFilledPetitionQuestionChildren.get(2)
        assertTrue(easyVisaNodeInstance13 instanceof QuestionNodeInstance)

        SubSectionNodeInstance subsection2Instance = (SubSectionNodeInstance) subsectionInstanceList.get(1)
        assertEquals('Petitions for Other Relatives', subsection2Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsection2Children = subsection2Instance.getChildren()
        assertEquals(1, subsection2Children.size())

        EasyVisaNodeInstance filledForOtherRelativeEasyVisaQuestion = subsection2Children.get(0)
        assertTrue(filledForOtherRelativeEasyVisaQuestion instanceof QuestionNodeInstance)
        QuestionNodeInstance filledForOtherRelativesQuestion = (QuestionNodeInstance) filledForOtherRelativeEasyVisaQuestion
        List<EasyVisaNodeInstance> filledForOtherRelativesQuestionChildren = everFilledPetitionQuestion.getChildren()
        assertEquals(4, filledForOtherRelativesQuestionChildren.size())

        EasyVisaNodeInstance easyVisaNodeInstance21 = filledForOtherRelativesQuestionChildren.get(0)
        assertTrue(easyVisaNodeInstance21 instanceof RepeatingQuestionGroupNodeInstance)
        EasyVisaNodeInstance easyVisaNodeInstance22 = filledForOtherRelativesQuestionChildren.get(1)
        assertTrue(easyVisaNodeInstance22 instanceof RepeatingQuestionGroupNodeInstance)
        EasyVisaNodeInstance easyVisaNodeInstance23 = filledForOtherRelativesQuestionChildren.get(2)
        assertTrue(easyVisaNodeInstance23 instanceof QuestionNodeInstance)

        SubSectionNodeInstance subsection3Instance = (SubSectionNodeInstance) subsectionInstanceList.get(2)
        assertEquals('Sponsorship Relationship', subsection3Instance.getDisplayText())
    }

    private void assertAnswers(SectionNodeInstance sectionInstance) {
        List<EasyVisaNodeInstance> subsectionInstanceList = sectionInstance.getChildren()
        assertEquals(3, subsectionInstanceList.size())
        assertSubSectionInstance1_Answers(subsectionInstanceList)
        assertSubSectionInstance2_Answers(subsectionInstanceList)
        assertSubSectionInstance3_Answers(subsectionInstanceList)
    }

    private void assertSubSectionInstance1_Answers(List<EasyVisaNodeInstance> subsectionInstanceList) {
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
        assertSubSectionInstance1RepeatingGroupAnswers(everFilledPetitionQuestion)
        //check repeating group question answers
    }

    private void assertSubSectionInstance2_Answers(List<EasyVisaNodeInstance> subsectionInstanceList) {
        SubSectionNodeInstance subsection2Instance = (SubSectionNodeInstance) subsectionInstanceList.get(1)
        assertEquals('Petitions for Other Relatives', subsection2Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = subsection2Instance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())
        QuestionNodeInstance filledForOtherRelativesQuestion = questionNodeInstanceList.get(0)

        List<RepeatingQuestionGroupNodeInstance> repeatingQuestionGroupNodeInstanceList = filledForOtherRelativesQuestion.getChildren().stream()
                .filter({ nodeInstance -> (nodeInstance instanceof RepeatingQuestionGroupNodeInstance) })
                .map({ nodeInstance -> (RepeatingQuestionGroupNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, repeatingQuestionGroupNodeInstanceList.size())
    }

    private void assertSubSectionInstance3_Answers(List<EasyVisaNodeInstance> subsectionInstanceList) {
        SubSectionNodeInstance subsection3Instance = (SubSectionNodeInstance) subsectionInstanceList.get(2)
        assertEquals('Sponsorship Relationship', subsection3Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = subsection3Instance.getChildren()
        assertEquals(1, subsectionChildren.size())
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

    private void assertSubSectionInstance1RepeatingGroupAnswers(QuestionNodeInstance everFilledPetitionQuestion) {
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
        assertRepeatingQuestionAnswer(firstNameEasyVisInstance, 'Given Name (First name)', 'John', 'Sec_1/SubSec_1/Q_2/0')
        EasyVisaNodeInstance middleNameEasyVisInstance = repeatingQuestionGroupNodeInstance1Children.get(1)
        assertRepeatingQuestionAnswer(middleNameEasyVisInstance, 'Middle Name (Do Not Abbreviate)', 'Williams', 'Sec_1/SubSec_1/Q_3/0')
        EasyVisaNodeInstance familyNameEasyVisInstance = repeatingQuestionGroupNodeInstance1Children.get(2)
        assertRepeatingQuestionAnswer(familyNameEasyVisInstance, 'Family Name/Last Name/Surname', 'Peer', 'Sec_1/SubSec_1/Q_4/0')
        EasyVisaNodeInstance relationshipToYouEasyVisInstance = repeatingQuestionGroupNodeInstance1Children.get(3)
        assertRepeatingQuestionAnswer(relationshipToYouEasyVisInstance, "What was this person's relationship to you?", 'Brother', 'Sec_1/SubSec_1/Q_5/0')

        List<EasyVisaNodeInstance> repeatingQuestionGroupNodeInstance2Children = repeatingQuestionGroupNodeInstance2.getChildren()
        EasyVisaNodeInstance cityortownEasyVisInstance = repeatingQuestionGroupNodeInstance2Children.get(5)
        assertRepeatingQuestionAnswer(cityortownEasyVisInstance, 'City or town where petition was filed', 'Brisbane', 'Sec_1/SubSec_1/Q_7/1')
        EasyVisaNodeInstance stateEasyVisInstance = repeatingQuestionGroupNodeInstance2Children.get(6)
        assertRepeatingQuestionAnswer(stateEasyVisInstance, 'State where petition was filed', 'Queensland', 'Sec_1/SubSec_1/Q_8/1')
        EasyVisaNodeInstance resultOfPetitionEasyVisInstance = repeatingQuestionGroupNodeInstance2Children.get(8)
        assertRepeatingQuestionAnswer(resultOfPetitionEasyVisInstance, 'What was the result of the petition?', 'Under Progress', 'Sec_1/SubSec_1/Q_10/1')
        EasyVisaNodeInstance issuedAlienNoEasyVisInstance = repeatingQuestionGroupNodeInstance2Children.get(9)
        assertRepeatingQuestionAnswer(issuedAlienNoEasyVisInstance, 'Was this person issued an Alien Registration Number (A-Number)?', 'yes',
                'Sec_1/SubSec_1/Q_11/1')

        assertEquals(1, issuedAlienNoEasyVisInstance.getChildren().size())
        EasyVisaNodeInstance alienNoEasyVisInstance = issuedAlienNoEasyVisInstance.getChildren().get(0)
        assertRepeatingQuestionAnswer(alienNoEasyVisInstance, "What was that person's Alien Registration Number (A-number)?", 'A1008', 'Sec_1/SubSec_1/Q_12/1')
    }

    protected void assertRepeatingQuestionAnswer(EasyVisaNodeInstance easyVisInstance, String questionDisplayText, String answerValue, String answerPath) {
        assertTrue(easyVisInstance instanceof QuestionNodeInstance)
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) easyVisInstance
        assertEquals(questionDisplayText, questionNodeInstance.getDisplayText())
        Answer answer = questionNodeInstance.getAnswer()
        assertEquals(answerValue, answer.getValue())
        assertEquals(answerPath, answer.getPath())
    }
}
