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

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

@Integration
class NameSectionApplicantTypeSpec extends TestMockUtils {

    @Autowired
    private PackageQuestionnaireService packageQuestionnaireService
    @Autowired
    private PackageService packageService
    @Autowired
    private AnswerService answerService
    @Autowired
    private AttorneyService attorneyService
    @Autowired
    private PdfPopulationService pdfPopulationService
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

    void testNameSectionPetitionerQuestionnaire() throws Exception {
        given:
        String sectionId = 'Sec_2'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListStub.nameSectionPetitionerAnswerList(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def sectionAnswerList = answerService.fetchAnswers(aPackage.id, petitionerApplicant.id)
        SectionNodeInstance petitionerSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id,
                        petitionerApplicant.id, sectionId, sectionAnswerList)

        then:
        assertNotNull(petitionerSectionInstance)
        assertPetitionerAnswers(petitionerSectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertPetitionerAnswers(SectionNodeInstance petitionerSectionInstance) {
        List<EasyVisaNodeInstance> petitionerSubsectionInstanceList = petitionerSectionInstance.getChildren()
        assertEquals(2, petitionerSubsectionInstanceList.size())
        assertPetitionerSubSectionInstance1_Answers(petitionerSubsectionInstanceList)
        assertPetitionerSubSectionInstance2_Answers(petitionerSubsectionInstanceList)
    }

    private void assertPetitionerSubSectionInstance1_Answers(List<EasyVisaNodeInstance> petitionerSubsectionInstanceList) {
        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) petitionerSubsectionInstanceList.get(0)
        assertEquals('SubSec_5', subsection1Instance.getId())
        List<EasyVisaNodeInstance> subsectionChildren = subsection1Instance.getChildren()
        assertEquals(3, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(3, questionNodeInstanceList.size())

        QuestionNodeInstance firstNameQuestion = questionNodeInstanceList.get(0)
        assertEquals('firstName', firstNameQuestion.getName())
        Answer firstNameAnswer = firstNameQuestion.getAnswer()
        assertEquals('John', firstNameAnswer.getValue())
        assertEquals('Sec_2/SubSec_5/Q_32', firstNameAnswer.getPath())

        QuestionNodeInstance middleNameQuestion = questionNodeInstanceList.get(1)
        assertEquals('middleName', middleNameQuestion.getName())
        Answer middleNameAnswer = middleNameQuestion.getAnswer()
        assertEquals('Watson', middleNameAnswer.getValue())
        assertEquals('Sec_2/SubSec_5/Q_33', middleNameAnswer.getPath())

        QuestionNodeInstance familyNameQuestion = questionNodeInstanceList.get(2)
        assertEquals('familyName', familyNameQuestion.getName())
        Answer familyNameAnswer = familyNameQuestion.getAnswer()
        assertEquals('Alexa', familyNameAnswer.getValue())
        assertEquals('Sec_2/SubSec_5/Q_34', familyNameAnswer.getPath())
    }

    private void assertPetitionerSubSectionInstance2_Answers(List<EasyVisaNodeInstance> petitionerSubsectionInstanceList) {
        SubSectionNodeInstance subsection2Instance = (SubSectionNodeInstance) petitionerSubsectionInstanceList.get(1)
        assertEquals('Other Names Used', subsection2Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = subsection2Instance.getChildren()
        assertEquals(2, subsectionChildren.size())

        List<QuestionNodeInstance> questionGroupNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, questionGroupNodeInstanceList.size())
        QuestionNodeInstance firstMiddleLastQuestion = questionGroupNodeInstanceList.get(1)
        assertEquals('firstMiddleLast', firstMiddleLastQuestion.getName())
        Answer firstMiddleLastAnswer = firstMiddleLastQuestion.getAnswer()
        assertEquals('yes', firstMiddleLastAnswer.getValue())
        assertEquals('Sec_2/SubSec_6/Q_35', firstMiddleLastAnswer.getPath())

        List<RepeatingQuestionGroupNodeInstance> repeatingQuestionGroupNodeInstanceList = firstMiddleLastQuestion.getChildren().stream()
                .filter({ nodeInstance -> (nodeInstance instanceof RepeatingQuestionGroupNodeInstance) })
                .map({ nodeInstance -> (RepeatingQuestionGroupNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, repeatingQuestionGroupNodeInstanceList.size())
        RepeatingQuestionGroupNodeInstance firstMiddleLastQuestionRepeatGroup = repeatingQuestionGroupNodeInstanceList.get(0)
        assertEquals('firstMiddleLastQuestions', firstMiddleLastQuestionRepeatGroup.getRepeatingQuestionGroup().getName())
        assertEquals(3, firstMiddleLastQuestionRepeatGroup.getChildren().size())

        List<QuestionNodeInstance> firstMiddleLastQuestionInstanceList = firstMiddleLastQuestionRepeatGroup.getChildren().stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(3, firstMiddleLastQuestionInstanceList.size())


        QuestionNodeInstance listAnyOtherFirstNameQuestion = firstMiddleLastQuestionInstanceList.get(0)
        assertEquals('listAnyOtherFirstName', listAnyOtherFirstNameQuestion.getName())
        Answer listAnyOtherFirstNameAnswer = listAnyOtherFirstNameQuestion.getAnswer()
        assertEquals('Williams', listAnyOtherFirstNameAnswer.getValue())
        assertEquals('Sec_2/SubSec_6/Q_37/0', listAnyOtherFirstNameAnswer.getPath())


        QuestionNodeInstance listAnyOtherMiddleNameQuestion = firstMiddleLastQuestionInstanceList.get(1)
        assertEquals('listAnyOtherMiddleName', listAnyOtherMiddleNameQuestion.getName())
        Answer listAnyOtherMiddleNameAnswer = listAnyOtherMiddleNameQuestion.getAnswer()
        assertEquals('Maxx', listAnyOtherMiddleNameAnswer.getValue())
        assertEquals('Sec_2/SubSec_6/Q_39/0', listAnyOtherMiddleNameAnswer.getPath())
    }

    void testNameSectionBeneficiaryQuestionnaire() throws Exception {
        given:
        String sectionId = 'Sec_nameForBeneficiary'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListStub.nameSectionBeneficiaryAnswerList(aPackage.id, applicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id, sectionId, answerList)

        when:
        def sectionAnswerList = answerService.fetchAnswers(aPackage.id, applicant.id)
        SectionNodeInstance beneficiarySectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id, sectionId, sectionAnswerList)
        assertNotNull(beneficiarySectionInstance)
        beneficiarySectionInstance.sortChildren()

        then:
        assertNotNull(beneficiarySectionInstance)
        assertBeneficiaryAnswers(beneficiarySectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertBeneficiaryAnswers(SectionNodeInstance beneficiarySectionInstance) {
        List<EasyVisaNodeInstance> beneficiarySubsectionInstanceList = beneficiarySectionInstance.getChildren()
        assertEquals(2, beneficiarySubsectionInstanceList.size())
        assertBeneficiarySubSectionInstance1_Answers(beneficiarySubsectionInstanceList)
    }

    private void assertBeneficiarySubSectionInstance1_Answers(List<EasyVisaNodeInstance> beneficiarySubsectionInstanceList) {
        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) beneficiarySubsectionInstanceList.get(0)
        assertEquals('SubSec_currentLegalNameForBeneficiary', subsection1Instance.getId())
        List<EasyVisaNodeInstance> subsectionChildren = subsection1Instance.getChildren()
        assertEquals(3, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(3, questionNodeInstanceList.size())

        QuestionNodeInstance firstNameQuestion = questionNodeInstanceList.get(0)
        assertEquals('currentLegalNameGivenName', firstNameQuestion.getName())
        Answer firstNameAnswer = firstNameQuestion.getAnswer()
        assertEquals('Peter', firstNameAnswer.getValue())
        assertEquals('Sec_nameForBeneficiary/SubSec_currentLegalNameForBeneficiary/Q_1901', firstNameAnswer.getPath())

        QuestionNodeInstance middleNameQuestion = questionNodeInstanceList.get(1)
        assertEquals('currentLegalNameMiddleName', middleNameQuestion.getName())
        Answer middleNameAnswer = middleNameQuestion.getAnswer()
        assertEquals('Clark', middleNameAnswer.getValue())
        assertEquals('Sec_nameForBeneficiary/SubSec_currentLegalNameForBeneficiary/Q_1902', middleNameAnswer.getPath())

        QuestionNodeInstance familyNameQuestion = questionNodeInstanceList.get(2)
        assertEquals('currentLegalNameFamilyName', familyNameQuestion.getName())
        Answer familyNameAnswer = familyNameQuestion.getAnswer()
        assertEquals('Johnson', familyNameAnswer.getValue())
        assertEquals('Sec_nameForBeneficiary/SubSec_currentLegalNameForBeneficiary/Q_1903', familyNameAnswer.getPath())
    }

}
