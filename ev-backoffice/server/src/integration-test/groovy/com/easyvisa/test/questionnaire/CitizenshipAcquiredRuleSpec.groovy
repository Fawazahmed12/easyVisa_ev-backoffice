package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
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
import spock.lang.Unroll

import java.util.stream.Collectors

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

@Integration
class CitizenshipAcquiredRuleSpec extends TestMockUtils {

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
    ProfileService profileService

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

    @Unroll
    void "test How Citizenship Acquired Radio Rule for #benefitCategory"() throws Exception {
        given:
        String sectionId = "Sec_legalStatusInUS"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, benefitCategory)

        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.client

        List<Answer> answerList = AnswerListStub.usCitizenLegalStatusRuleAnswerList(aPackage.id, applicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id, sectionId,
                answerList)

        when:
        List<Answer> legalStatusSectionAnswerList = this.answerService.fetchAnswers(aPackage.id, applicant.id, [sectionId])
        SectionNodeInstance sectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id,
                        sectionId, legalStatusSectionAnswerList)
        assertNotNull(sectionInstance)

        then:
        assertK1CategoryCitizenshipAcquiredRadioOptions(aPackage, sectionInstance, inputSourceTypeSize, lastAnswerValue)

        cleanup:
        testHelper.clean()

        where:
        benefitCategory                 | inputSourceTypeSize | lastAnswerValue
        ImmigrationBenefitCategory.K1K3 | 3                   | "parents_marriage"
        ImmigrationBenefitCategory.IR1  | 3                   | "parents_marriage"

    }

    private void assertK1CategoryCitizenshipAcquiredRadioOptions(Package aPackage, SectionNodeInstance sectionInstance, int inputSourceTypeSize, String lastAnswerValue) {
        List<SubSectionNodeInstance> subsectionInstanceList = sectionInstance.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof SubSectionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (SubSectionNodeInstance) nodeInstance })
                .collect(Collectors.toList())

        // There are 2 subsections
        assertEquals(2, subsectionInstanceList.size())

        // Validate U.S. Citizens subsection
        SubSectionNodeInstance usCitizenInstance = (SubSectionNodeInstance) subsectionInstanceList.get(1)
        assertNotNull(usCitizenInstance)
        assertEquals("SubSec_usCitizens", usCitizenInstance.getId())

        List<QuestionNodeInstance> usCitizenshipQuestionInstanceList = usCitizenInstance.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())

        assertEquals(1, usCitizenshipQuestionInstanceList.size())


        QuestionNodeInstance citizenshipAcquiredQuestion = usCitizenshipQuestionInstanceList.get(0)
        assertEquals("howCitizenshipAcquired", citizenshipAcquiredQuestion.getName())
        Answer citizenshipAcquiredAnswer = citizenshipAcquiredQuestion.getAnswer()

        assertEquals("Sec_legalStatusInUS/SubSec_usCitizens/Q_120", citizenshipAcquiredAnswer.getPath())

        //Drop down list should have an option options (Another Method)
        InputSourceType howAcquiredInputSourceType = citizenshipAcquiredQuestion.getInputSourceType()
        assertNotNull(howAcquiredInputSourceType)

        assertEquals(inputSourceTypeSize, howAcquiredInputSourceType.getValues().size())
        assertEquals(lastAnswerValue, howAcquiredInputSourceType.getValues().last().getValue())
    }

}
