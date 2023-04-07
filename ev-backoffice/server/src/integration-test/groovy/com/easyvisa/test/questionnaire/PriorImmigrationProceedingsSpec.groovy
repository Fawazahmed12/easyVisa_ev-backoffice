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
class PriorImmigrationProceedingsSpec extends TestMockUtils {

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

    void testPriorImmigrationProceedingsQuestionnaire() throws Exception {
        given:
        String sectionId = 'Sec_inadmissibilityAndOtherLegalIssues'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListStub.priorImmigrationProceedingsAnswerList(aPackage.id, applicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id, sectionId,
                answerList)

        when:
        def sectionAnswerList = answerService.fetchAnswers(aPackage.id, applicant.id)
        SectionNodeInstance inadmissibilityAndOtherLegalIssuesSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id,
                        sectionId, sectionAnswerList)

        then:
        assertNotNull(inadmissibilityAndOtherLegalIssuesSectionInstance)
        assertInadmissibilityAndOtherLegalIssuesAnswers(inadmissibilityAndOtherLegalIssuesSectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertInadmissibilityAndOtherLegalIssuesAnswers(SectionNodeInstance inadmissibilityAndOtherLegalIssuesSectionInstance) {
        List<EasyVisaNodeInstance> inadmissibilityAndOtherLegalIssuesSectionInstanceList = inadmissibilityAndOtherLegalIssuesSectionInstance.getChildren()
        assertEquals(1, inadmissibilityAndOtherLegalIssuesSectionInstanceList.size())

        List<EasyVisaNodeInstance> visibleInadmissibilityAndOtherLegalIssuesSectionInstanceList = inadmissibilityAndOtherLegalIssuesSectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())
        assertEquals(1, visibleInadmissibilityAndOtherLegalIssuesSectionInstanceList.size())

        assertEquals('SubSec_immigrationHistoryGeneral', visibleInadmissibilityAndOtherLegalIssuesSectionInstanceList[0].id)
        assertEquals('Immigration History General', visibleInadmissibilityAndOtherLegalIssuesSectionInstanceList[0].displayText)
    }


    private void assertPriorImmigrationProceedingsSubSectionAnswers(List<EasyVisaNodeInstance> inadmissibilityAndOtherLegalIssuesSectionInstanceList) {
        SubSectionNodeInstance priorImmigrationProceedingsSubSectionInstance = (SubSectionNodeInstance) inadmissibilityAndOtherLegalIssuesSectionInstanceList.stream()
                .filter({ x -> x.getDefinitionNode().getId().equals('SubSec_priorImmigrationProceedings') })
                .findFirst()
                .orElse(null)
        assertNotNull(priorImmigrationProceedingsSubSectionInstance)

        List<EasyVisaNodeInstance> subsectionChildren = priorImmigrationProceedingsSubSectionInstance.getChildren()
        assertEquals(14, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(14, questionNodeInstanceList.size())

        QuestionNodeInstance wasBeneficiaryEverInImmigrationProceedingsQuestion = questionNodeInstanceList.get(0)
        assertEquals('priorImmigrationProceedings_wasBeneficiaryEverInImmigrationProceedings', wasBeneficiaryEverInImmigrationProceedingsQuestion.getName())
        Answer wasBeneficiaryEverInImmigrationProceedingsAnswer = wasBeneficiaryEverInImmigrationProceedingsQuestion.getAnswer()
        assertEquals('Yes', wasBeneficiaryEverInImmigrationProceedingsAnswer.getValue())
        assertEquals('Sec_inadmissibilityAndOtherLegalIssues/SubSec_priorImmigrationProceedings/Q_3081', wasBeneficiaryEverInImmigrationProceedingsAnswer.getPath())


        List<EasyVisaNodeInstance> wasBeneficiaryEverInImmigrationProceedingsChildren = wasBeneficiaryEverInImmigrationProceedingsQuestion.getChildren()
        assertEquals(1, wasBeneficiaryEverInImmigrationProceedingsChildren.size())

        List<RepeatingQuestionGroupNodeInstance> wasBeneficiaryEverInImmigrationProceedingsRepeatingInstanceList = wasBeneficiaryEverInImmigrationProceedingsChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof RepeatingQuestionGroupNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (RepeatingQuestionGroupNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, wasBeneficiaryEverInImmigrationProceedingsRepeatingInstanceList.size())


        RepeatingQuestionGroupNodeInstance wasBeneficiaryEverInImmigrationProceedingFirstInstance = wasBeneficiaryEverInImmigrationProceedingsRepeatingInstanceList[0]
        List<EasyVisaNodeInstance> wasBeneficiaryEverInImmigrationProceedingFirstInstanceChildren = wasBeneficiaryEverInImmigrationProceedingFirstInstance.getChildren()
        assertEquals(4, wasBeneficiaryEverInImmigrationProceedingFirstInstanceChildren.size())

        List<QuestionNodeInstance> wasBeneficiaryEverInImmigrationProceedingFirstInstanceQuestionList = wasBeneficiaryEverInImmigrationProceedingFirstInstanceChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(4, wasBeneficiaryEverInImmigrationProceedingFirstInstanceQuestionList.size())
    }
}
