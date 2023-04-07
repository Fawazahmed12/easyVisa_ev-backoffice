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
import org.junit.Ignore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import java.util.stream.Collectors

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

@Integration
@Ignore
//As of now we are removing Form_134.
class SupportAndContributionsSpec extends TestMockUtils {

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


    void testSupportAndContributionsQuestionnaire() throws Exception {
        given:
        String sectionId = 'Sec_supportAndContributions'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListStub.supportAndContributionsAnswerList(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def sectionAnswerList = answerService.fetchAnswers(aPackage.id, petitionerApplicant.id)
        SectionNodeInstance supportAndContributionSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id,
                        petitionerApplicant.id, sectionId, sectionAnswerList)

        then:
        assertNotNull(supportAndContributionSectionInstance)
        assertSupportAndContributionsAnswers(supportAndContributionSectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertSupportAndContributionsAnswers(SectionNodeInstance supportAndContributionSectionInstance) {
        List<EasyVisaNodeInstance> supportAndContributionSectionInstanceList = supportAndContributionSectionInstance.getChildren()
        assertEquals(2, supportAndContributionSectionInstanceList.size())

        List<EasyVisaNodeInstance> visibleSupportAndContributionSectionInstanceList = supportAndContributionSectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())
        assertEquals(2, visibleSupportAndContributionSectionInstanceList.size())

        assertNatureOfContributionsAnswers(visibleSupportAndContributionSectionInstanceList)
    }


    private void assertNatureOfContributionsAnswers(List<EasyVisaNodeInstance> supportAndContributionSectionInstanceList) {
        SubSectionNodeInstance natureOfContributionsSubSectionInstance = (SubSectionNodeInstance) supportAndContributionSectionInstanceList.stream()
                .filter({ x -> x.getDefinitionNode().getId().equals('SubSec_natureOfContributions') })
                .findFirst()
                .orElse(null)
        assertNotNull(natureOfContributionsSubSectionInstance)

        List<EasyVisaNodeInstance> subsectionChildren = natureOfContributionsSubSectionInstance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())

        QuestionNodeInstance monetaryContributionsPersonQuestion = questionNodeInstanceList.get(0)
        assertEquals('monetaryContributionsPerson', monetaryContributionsPersonQuestion.getName())
        Answer monetaryContributionsPersonAnswer = monetaryContributionsPersonQuestion.getAnswer()
        assertEquals('Yes', monetaryContributionsPersonAnswer.getValue())
        assertEquals('Sec_supportAndContributions/SubSec_natureOfContributions/Q_1404', monetaryContributionsPersonAnswer.getPath())


        List<EasyVisaNodeInstance> monetaryContributionsPersonChildren = monetaryContributionsPersonQuestion.getChildren()
        assertEquals(1, monetaryContributionsPersonChildren.size())

        List<RepeatingQuestionGroupNodeInstance> natureOfContributionRepeatingInstanceList = monetaryContributionsPersonChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof RepeatingQuestionGroupNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (RepeatingQuestionGroupNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, natureOfContributionRepeatingInstanceList.size())


        RepeatingQuestionGroupNodeInstance natureOfContributionFirstInstance = natureOfContributionRepeatingInstanceList[0]
        List<EasyVisaNodeInstance> natureOfContributionFirstInstanceChildren = natureOfContributionFirstInstance.getChildren()
        assertEquals(5, natureOfContributionFirstInstanceChildren.size())

        List<QuestionNodeInstance> natureOfContributionFirstInstanceQuestionList = natureOfContributionFirstInstanceChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(5, natureOfContributionFirstInstanceQuestionList.size())

        QuestionNodeInstance describeContributionQuestion = natureOfContributionFirstInstanceQuestionList[0]
        assertEquals('describeContribution', describeContributionQuestion.getName())
        Answer describeContributionAnswer = describeContributionQuestion.getAnswer()
        assertEquals('United States', describeContributionAnswer.getValue())
        assertEquals('Sec_supportAndContributions/SubSec_natureOfContributions/Q_1405/0', describeContributionAnswer.getPath())

        QuestionNodeInstance usDollarAmountQuestion = natureOfContributionFirstInstanceQuestionList[1]
        assertEquals('usDollarAmount', usDollarAmountQuestion.getName())
        Answer usDollarAmountAnswer = usDollarAmountQuestion.getAnswer()
        assertEquals('5000', usDollarAmountAnswer.getValue())
        assertEquals('Sec_supportAndContributions/SubSec_natureOfContributions/Q_1406/0', usDollarAmountAnswer.getPath())

        QuestionNodeInstance intendToThisContributionQuestion = natureOfContributionFirstInstanceQuestionList[2]
        assertEquals('intendToThisContribution', intendToThisContributionQuestion.getName())
        Answer intendToThisContributionAnswer = intendToThisContributionQuestion.getAnswer()
        assertEquals('Annually', intendToThisContributionAnswer.getValue())
        assertEquals('Sec_supportAndContributions/SubSec_natureOfContributions/Q_1407/0', intendToThisContributionAnswer.getPath())

        QuestionNodeInstance intendToContinueContributionQuestion = natureOfContributionFirstInstanceQuestionList[3]
        assertEquals('intendToContinueContribution', intendToContinueContributionQuestion.getName())
        assertEquals('For how many years do you intend to continue making this contribution?', intendToContinueContributionQuestion.getDisplayText())
        Answer intendToContinueContributionAnswer = intendToContinueContributionQuestion.getAnswer()
        assertEquals('4', intendToContinueContributionAnswer.getValue())
        assertEquals('Sec_supportAndContributions/SubSec_natureOfContributions/Q_1408/0', intendToContinueContributionAnswer.getPath())

        QuestionNodeInstance nonMonetaryContributionsQuestion = natureOfContributionFirstInstanceQuestionList[4]
        assertEquals('nonMonetaryContributions', nonMonetaryContributionsQuestion.getName())
        Answer nonMonetaryContributionsAnswer = nonMonetaryContributionsQuestion.getAnswer()
        assertEquals('Colorado', nonMonetaryContributionsAnswer.getValue())
        assertEquals('Sec_supportAndContributions/SubSec_natureOfContributions/Q_1410/0', nonMonetaryContributionsAnswer.getPath())
    }
}
