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
import com.easyvisa.Warning
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.SectionNodeInstance
import com.easyvisa.questionnaire.answering.SubSectionNodeInstance
import com.easyvisa.questionnaire.meta.InputSourceType
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
class SponsorshipRelationshipSpec  extends TestMockUtils {

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


    void testK1CategorySponsorshipRelationshipQuestionnaire() throws Exception {
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
        List<Answer> answerList = AnswerListStub.form134ExclusionIntroSectionAnswerList(aPackage.id, applicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id, sectionId,
                answerList)

        when:
        List<Answer> introSectionAnswerList = this.answerService.fetchAnswers(aPackage.id, applicant.id, [sectionId])
        SectionNodeInstance sectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id,
                        sectionId, introSectionAnswerList)
        assertNotNull(sectionInstance)

        then:
        assertK1CategorySponsorshipRelationshipRadioOptions(aPackage, sectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertK1CategorySponsorshipRelationshipRadioOptions(Package aPackage, SectionNodeInstance sectionInstance) {
        List<SubSectionNodeInstance> subsectionInstanceList = sectionInstance.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof SubSectionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (SubSectionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, subsectionInstanceList.size())

        SubSectionNodeInstance previousImmigrationFilledForAnotherPersonInstance = (SubSectionNodeInstance) subsectionInstanceList.get(1)
        assertNotNull(previousImmigrationFilledForAnotherPersonInstance)
        assertEquals("SubSec_4", previousImmigrationFilledForAnotherPersonInstance.getId())

        List<QuestionNodeInstance> sponsorshipRelationshipQuestionInstanceList = previousImmigrationFilledForAnotherPersonInstance.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, sponsorshipRelationshipQuestionInstanceList.size())


        QuestionNodeInstance sponsorshipRelationshipQuestion = sponsorshipRelationshipQuestionInstanceList.get(0)
        assertEquals("beneficiaryRelatedToYou", sponsorshipRelationshipQuestion.getName())
        Answer sponsorshipRelationshipAnswer = sponsorshipRelationshipQuestion.getAnswer()
        assertEquals("spouse", sponsorshipRelationshipAnswer.getValue())
        assertEquals("Sec_1/SubSec_4/Q_27", sponsorshipRelationshipAnswer.getPath())


        //Drop down list should have an option options (Fiancé and Spouse)
        InputSourceType relationshipInputSourceType = sponsorshipRelationshipQuestion.getInputSourceType()
        assertNotNull(relationshipInputSourceType)
        assertEquals(2, relationshipInputSourceType.getValues().size())

        assertSponsorshipRelationshipWarning(aPackage, sectionInstance, sponsorshipRelationshipAnswer);
    }

    void testIR1CategorySponsorshipRelationshipQuestionnaire() throws Exception {
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
        Applicant applicant = aPackage.client
        List<Answer> answerList = AnswerListStub.form134ExclusionIntroSectionAnswerList(aPackage.id, applicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id, sectionId,
                answerList)

        when:
        List<Answer> introSectionAnswerList = this.answerService.fetchAnswers(aPackage.id, applicant.id, [sectionId])
        SectionNodeInstance sectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id,
                        sectionId, introSectionAnswerList)
        assertNotNull(sectionInstance)

        then:
        assertIR1CategorySponsorshipRelationshipRadioOptions(aPackage, sectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertIR1CategorySponsorshipRelationshipRadioOptions(Package aPackage, SectionNodeInstance sectionInstance) {
        List<SubSectionNodeInstance> subsectionInstanceList = sectionInstance.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof SubSectionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (SubSectionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(3, subsectionInstanceList.size())

        SubSectionNodeInstance previousImmigrationFilledForAnotherPersonInstance = (SubSectionNodeInstance) subsectionInstanceList.get(2)
        assertNotNull(previousImmigrationFilledForAnotherPersonInstance)
        assertEquals("SubSec_4", previousImmigrationFilledForAnotherPersonInstance.getId())

        List<QuestionNodeInstance> sponsorshipRelationshipQuestionInstanceList = previousImmigrationFilledForAnotherPersonInstance.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, sponsorshipRelationshipQuestionInstanceList.size())


        QuestionNodeInstance sponsorshipRelationshipQuestion = sponsorshipRelationshipQuestionInstanceList.get(0)
        assertEquals("beneficiaryRelatedToYou", sponsorshipRelationshipQuestion.getName())
        Answer sponsorshipRelationshipAnswer = sponsorshipRelationshipQuestion.getAnswer()
        assertEquals("spouse", sponsorshipRelationshipAnswer.getValue())
        assertEquals("Sec_1/SubSec_4/Q_27", sponsorshipRelationshipAnswer.getPath())


        //Drop down list should have an option options (Fiancé and Spouse)
        InputSourceType relationshipInputSourceType = sponsorshipRelationshipQuestion.getInputSourceType()
        assertNotNull(relationshipInputSourceType)
        assertEquals(5, relationshipInputSourceType.getValues().size())
    }


    private void assertSponsorshipRelationshipWarning(Package aPackage, SectionNodeInstance sectionInstance, Answer sponsorshipRelationshipAnswer) {
        assertNotNull(aPackage)
        assertNotNull(sectionInstance)
        Warning.withNewTransaction {
            Warning sponsorshipRelationshipWarning = Warning.findByAPackageAndQuestionId(aPackage, 'Q_27')
            assertNotNull(sponsorshipRelationshipWarning)
            assertNotNull(sponsorshipRelationshipWarning.answer)
            assertEquals(sponsorshipRelationshipWarning.answer.id, sponsorshipRelationshipAnswer.id)
            assertNotNull(sponsorshipRelationshipWarning.body)
        }
    }
}
