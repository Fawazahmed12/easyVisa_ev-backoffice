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
class ContactRepresentativeTooltipRuleSpec extends TestMockUtils {

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

    void testContactRepresentativeTooltipRule() throws Exception {
        given:
        String sectionId = "Sec_1"
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
        SectionNodeInstance introSectionInstance = packageQuestionnaireService
                .questionGraphByBenefitCategoryAndSection(aPackage.id, petitionerApplicant.id, sectionId, answerList)

        then:
        assertNotNull(introSectionInstance)
        assertIntroSectionAnswers(introSectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertIntroSectionAnswers(SectionNodeInstance introSectionInstance) {
        List<EasyVisaNodeInstance> introSectionInstanceList = introSectionInstance.getChildren()
        assertEquals(3, introSectionInstanceList.size())

        List<EasyVisaNodeInstance> validIntroSectionInstanceList = introSectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())
        assertEquals(3, validIntroSectionInstanceList.size())
        assertDynamicTooltipAnswers(validIntroSectionInstanceList)
    }

    private void assertDynamicTooltipAnswers(List<EasyVisaNodeInstance> introSectionInstanceList) {
        SubSectionNodeInstance previousImmigrationFilledForAnotherPersonInstance = (SubSectionNodeInstance) introSectionInstanceList.get(0)
        assertEquals("SubSec_1", previousImmigrationFilledForAnotherPersonInstance.getId())
        List<EasyVisaNodeInstance> subsectionChildren = previousImmigrationFilledForAnotherPersonInstance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())
    }

}
