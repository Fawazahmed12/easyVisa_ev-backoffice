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
import org.junit.Ignore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import java.util.stream.Collectors

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

@Integration
@Ignore
//As of now we are removed Form_134
class IncomeHistoryQuestionnaireSpec extends TestMockUtils {

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

    void testIncomeHistoryQuestionnaire() throws Exception {
        given:
        String sectionId = 'Sec_incomeHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer>  answerList = AnswerListStub.form134ExclusionIncomeHistoryAnswerList(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        SectionNodeInstance incomeHistorySectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id,
                        petitionerApplicant.id, sectionId, answerList)

        then:
        assertNotNull(incomeHistorySectionInstance)
        asserIncomeHistorySectionAnswers(incomeHistorySectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void asserIncomeHistorySectionAnswers(SectionNodeInstance incomeHistorySectionInstance) {
        List<EasyVisaNodeInstance> incomeHistorySectionInstanceList = incomeHistorySectionInstance.getChildren()
        assertEquals(1, incomeHistorySectionInstanceList.size())

        List<EasyVisaNodeInstance> validIncomeHistorySectionInstanceList = incomeHistorySectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())
        assertEquals(1, validIncomeHistorySectionInstanceList.size())

        SubSectionNodeInstance incomeHistorySubSectionInstance = (SubSectionNodeInstance) validIncomeHistorySectionInstanceList.get(0)
        assertEquals('SubSec_incomeHistory', incomeHistorySubSectionInstance.getId())
        assertEquals(true, incomeHistorySubSectionInstance.isVisibility())

        List<EasyVisaNodeInstance> subsectionChildren = incomeHistorySubSectionInstance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility())})
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())

        QuestionNodeInstance currentIndividualAnnualIncomeQuestion = questionNodeInstanceList.get(0)
        assertEquals('currentIndividualAnnualIncome', currentIndividualAnnualIncomeQuestion.getName())
        assertEquals('ev-currencyinput', currentIndividualAnnualIncomeQuestion.getInputType())
        Answer currentIndividualAnnualIncomeAnswer = currentIndividualAnnualIncomeQuestion.getAnswer()
        assertEquals('2000', currentIndividualAnnualIncomeAnswer.getValue())
        assertEquals('Sec_incomeHistory/SubSec_incomeHistory/Q_132', currentIndividualAnnualIncomeAnswer.getPath())
    }
}
