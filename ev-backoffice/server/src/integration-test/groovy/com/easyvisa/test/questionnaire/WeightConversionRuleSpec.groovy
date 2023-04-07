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
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import java.util.stream.Collectors

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

@Integration
class WeightConversionRuleSpec extends TestMockUtils {

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

    void testNewEmployeeHistoryQuestionnaire() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListStub.biographicInformationWeightHeightAnswerList(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        List<Answer> biographicAnswerList = this.answerService.fetchAnswers(aPackage.id, petitionerApplicant.id,
                [sectionId])
        SectionNodeInstance biographicInformationSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id,
                        petitionerApplicant.id, sectionId, biographicAnswerList)

        then:
        assertNotNull(biographicInformationSectionInstance)
        assertBiographicInformationAnswers(biographicInformationSectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertBiographicInformationAnswers(SectionNodeInstance biographicInformationSectionInstance) {
        List<EasyVisaNodeInstance> biographicInformationSubSectionInstanceList = biographicInformationSectionInstance.getChildren()
        assertEquals(6, biographicInformationSubSectionInstanceList.size())

        List<SubSectionNodeInstance> validBiographicInformationSubSectionInstanceList = biographicInformationSubSectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() && (nodeInstance instanceof SubSectionNodeInstance) })
                .map({ nodeInstance -> (SubSectionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(6, validBiographicInformationSubSectionInstanceList.size())

        assertHeightSubSectionAnswers(validBiographicInformationSubSectionInstanceList[2])
        assertWeightSubSectionAnswers(validBiographicInformationSubSectionInstanceList[3])
    }


    private void assertHeightSubSectionAnswers(SubSectionNodeInstance heightSubSectionInstance) {
        assertEquals('SubSec_height', heightSubSectionInstance.getId())
        assertEquals('Height', heightSubSectionInstance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = heightSubSectionInstance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())

        QuestionNodeInstance unitsMeasureQuestion = questionNodeInstanceList.get(0)
        assertEquals('unitsMeasure', unitsMeasureQuestion.getName())
        Answer unitsMeasureAnswer = unitsMeasureQuestion.getAnswer()
        assertEquals('Metric', unitsMeasureAnswer.getValue())
        assertEquals('Sec_biographicInformation/SubSec_height/Q_101', unitsMeasureAnswer.getPath())

        ///////////

        assertEquals(3, unitsMeasureQuestion.getChildren().size())
        List<QuestionNodeInstance> unitsMeasureQuestionInstanceList = unitsMeasureQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, unitsMeasureQuestionInstanceList.size())

        QuestionNodeInstance centimetersQuestion = (QuestionNodeInstance) unitsMeasureQuestion.getChildren()[0]
        assertEquals(true, centimetersQuestion.isVisibility())
        assertEquals('centimeters', centimetersQuestion.getName())
        Answer centimetersAnswer = centimetersQuestion.getAnswer()
        assertEquals('177', centimetersAnswer.getValue())
        assertEquals('Sec_biographicInformation/SubSec_height/Q_102', centimetersAnswer.getPath())

        QuestionNodeInstance feetQuestion = (QuestionNodeInstance) unitsMeasureQuestion.getChildren()[1]
        assertEquals(false, feetQuestion.isVisibility())
        assertEquals('feet', feetQuestion.getName())
        Answer feetAnswer = feetQuestion.getAnswer()
        assertEquals('5', feetAnswer.getValue())
        assertEquals('Sec_biographicInformation/SubSec_height/Q_103', feetAnswer.getPath())

        QuestionNodeInstance inchesQuestion = (QuestionNodeInstance) unitsMeasureQuestion.getChildren()[2]
        assertEquals(false, inchesQuestion.isVisibility())
        assertEquals('inches', inchesQuestion.getName())
        Answer inchesAnswer = inchesQuestion.getAnswer()
        assertEquals('9', inchesAnswer.getValue())
        assertEquals('Sec_biographicInformation/SubSec_height/Q_104', inchesAnswer.getPath())
    }


    private void assertWeightSubSectionAnswers(SubSectionNodeInstance weightSubSectionInstance) {
        assertEquals('SubSec_weight', weightSubSectionInstance.getId())
        assertEquals('Weight', weightSubSectionInstance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = weightSubSectionInstance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())

        QuestionNodeInstance unitsMeasureQuestion = questionNodeInstanceList[0]
        assertEquals('unitsMeasure', unitsMeasureQuestion.getName())
        Answer unitsMeasureAnswer = unitsMeasureQuestion.getAnswer()
        assertEquals('Metric', unitsMeasureAnswer.getValue())
        assertEquals('Sec_biographicInformation/SubSec_weight/Q_105', unitsMeasureAnswer.getPath())

        ///////////

        assertEquals(2, unitsMeasureQuestion.getChildren().size())
        List<QuestionNodeInstance> unitsMeasureQuestionInstanceList = unitsMeasureQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, unitsMeasureQuestionInstanceList.size())

        QuestionNodeInstance lbsWeightQuestion = (QuestionNodeInstance) unitsMeasureQuestion.getChildren()[0]
        assertEquals(false, lbsWeightQuestion.isVisibility())
        assertEquals('lbsWeight', lbsWeightQuestion.getName())
        Answer lbsWeightAnswer = lbsWeightQuestion.getAnswer()
        assertEquals('205', lbsWeightAnswer.getValue())
        assertEquals('Sec_biographicInformation/SubSec_weight/Q_106', lbsWeightAnswer.getPath())

        QuestionNodeInstance metricWeightQuestion = (QuestionNodeInstance) unitsMeasureQuestion.getChildren()[1]
        assertEquals(true, metricWeightQuestion.isVisibility())
        assertEquals('metricWeight', metricWeightQuestion.getName())
        Answer metricWeightAnswer = metricWeightQuestion.getAnswer()
        assertEquals('93', metricWeightAnswer.getValue())
        assertEquals('Sec_biographicInformation/SubSec_weight/Q_6009', metricWeightAnswer.getPath())
    }
}
