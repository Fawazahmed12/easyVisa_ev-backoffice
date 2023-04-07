package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.*
import com.easyvisa.questionnaire.model.Question
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.AnswerListStub
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.QuestionnaireTestDBSetupUtility
import com.easyvisa.utils.TestMockUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import java.time.LocalDate
import java.util.stream.Collectors

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNull

@Integration
class PreviousPhysicalAddressPopulateRuleSpec extends TestMockUtils {

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


    void testMoveInDateAfterTheConditionalResidenceDate() throws Exception {
        given:
        String sectionId = "Sec_addressHistoryForBeneficiary"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.REMOVECOND)


        Boolean haveYouResidedAnyAddressSincePRBegan = true
        Long packageId = testHelper.aPackage.id
        Applicant applicant = testHelper.aPackage.principalBeneficiary


        List<Answer> answerList = AnswerListStub.addressHistoryBeneficaryAnswerListForForm751(packageId, applicant.id, haveYouResidedAnyAddressSincePRBegan)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, packageId, applicant.id, sectionId,
                answerList)

        when:
        def sectionAnswerList = answerService.fetchAnswers(packageId, applicant.id)
        SectionNodeInstance addressHistorySectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(packageId, applicant.id, sectionId,
                        sectionAnswerList)

        then:
        assertNotNull(addressHistorySectionInstance)
        assertAddressHistorySubSections(addressHistorySectionInstance)
        assertMoveInDateAfterTheConditionalResidenceDate(addressHistorySectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertAddressHistorySubSections(SectionNodeInstance addressHistorySectionInstance, Boolean haveYouResidedBeforePR = true) {
        List<EasyVisaNodeInstance> addressHistorySectionInstanceList = addressHistorySectionInstance.getChildren()

        List<EasyVisaNodeInstance> visibleAddressHistorySectionInstanceList = addressHistorySectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())

        assertEquals("SubSec_currentPhysicalAddressForBeneficiary", visibleAddressHistorySectionInstanceList[0].id)
        assertEquals("Current Physical Address", visibleAddressHistorySectionInstanceList[0].displayText)

        if (haveYouResidedBeforePR) {
            assertEquals(3, visibleAddressHistorySectionInstanceList.size())

            assertEquals("SubSec_previousPhysicalAddressForBeneficiary", visibleAddressHistorySectionInstanceList[1].id)
            assertEquals("Previous Physical Addresses", visibleAddressHistorySectionInstanceList[1].displayText)

            assertEquals("SubSec_currentMailingAddressForBeneficiary", visibleAddressHistorySectionInstanceList[2].id)
            assertEquals("Current Mailing Address", visibleAddressHistorySectionInstanceList[2].displayText)
        } else {
            assertEquals(2, visibleAddressHistorySectionInstanceList.size())

            assertEquals("SubSec_currentMailingAddressForBeneficiary", visibleAddressHistorySectionInstanceList[1].id)
            assertEquals("Current Mailing Address", visibleAddressHistorySectionInstanceList[1].displayText)

        }

    }


    private void assertMoveInDateAfterTheConditionalResidenceDate(SectionNodeInstance addressHistorySectionInstance) {

        SubSectionNodeInstance currentPhysicalAddressSubSectionInstance = (SubSectionNodeInstance) addressHistorySectionInstance.getChildren().stream()
                .filter({ x -> x.getDefinitionNode().getId().equals('SubSec_currentPhysicalAddressForBeneficiary') })
                .findFirst()
                .orElse(null)
        // 7 id = Q_2028, name=dateThatYourConditionalPermanentResidenceBegan
        // children size = 9, 8 id = Q_2029, name=haveYouResidedAnyAddressSincePRBegan, visibility=false
        assertNotNull(currentPhysicalAddressSubSectionInstance)

        // get current Physical Address Children and validate 'haveYouResidedAnyAddressSincePRBegan
        List<EasyVisaNodeInstance> curAddressQuestions = currentPhysicalAddressSubSectionInstance.children
        assertNotNull(curAddressQuestions)
        assertEquals(9, curAddressQuestions.size())

        // assert newly added question 2028 and 2029 exist
        QuestionNodeInstance q2028 = (QuestionNodeInstance) curAddressQuestions[7]
        assertEquals('dateThatYourConditionalPermanentResidenceBegan', q2028?.name)

        QuestionNodeInstance q2029 = (QuestionNodeInstance) curAddressQuestions[8]
        assertEquals('haveYouResidedAnyAddressSincePRBegan', q2029?.name)
        assertFalse(q2029.visibility)
        Answer haveYouResidedAnyAddressSincePRBeganAnswer = q2029?.answer
        assertEquals("yes", haveYouResidedAnyAddressSincePRBeganAnswer.value)

        // Assert Previous Address History Section
        SubSectionNodeInstance previousPhysicalAddressSubSectionInstance = (SubSectionNodeInstance) addressHistorySectionInstance.getChildren().stream()
                .filter({ x -> x.getDefinitionNode().getId().equals('SubSec_previousPhysicalAddressForBeneficiary') })
                .findFirst()
                .orElse(null)
        assertNotNull(previousPhysicalAddressSubSectionInstance)
        // Should just have a Repeating Question Group for Previous Address
        assertEquals(1, previousPhysicalAddressSubSectionInstance.getChildren().size())

        List<RepeatingQuestionGroupNodeInstance> childrenInformationQuestionNodeInstanceList = previousPhysicalAddressSubSectionInstance.getChildren().stream()
                .filter({ nodeInstance -> (nodeInstance instanceof RepeatingQuestionGroupNodeInstance) })
                .map({ nodeInstance -> (RepeatingQuestionGroupNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        // RQG_previousPhysicalAddressForBeneficiary751

        assertEquals(1, childrenInformationQuestionNodeInstanceList.size())
        assertEquals('RQG_previousPhysicalAddressForBeneficiary751', childrenInformationQuestionNodeInstanceList[0]?.id)
    }


    void testMoveInDateBeforeTheConditionalResidenceDate() throws Exception {
        given:
        String sectionId = "Sec_addressHistoryForBeneficiary"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.REMOVECOND)

        Boolean haveYouResidedAnyAddressSincePRBegan = false
        Long packageId = testHelper.aPackage.id
        Applicant applicant = testHelper.aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListStub.addressHistoryBeneficaryAnswerListForForm751(packageId, applicant.id, haveYouResidedAnyAddressSincePRBegan)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, packageId, applicant.id, sectionId,
                answerList)

        when:
        def sectionAnswerList = answerService.fetchAnswers(packageId, applicant.id)
        SectionNodeInstance addressHistorySectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(packageId, applicant.id, sectionId,
                        sectionAnswerList)

        then:
        assertNotNull(addressHistorySectionInstance)
        assertAddressHistorySubSections(addressHistorySectionInstance, haveYouResidedAnyAddressSincePRBegan)
        assertMoveInDateBeforeTheConditionalResidenceDate(addressHistorySectionInstance)

        cleanup:
        testHelper.clean()
    }


    private void assertMoveInDateBeforeTheConditionalResidenceDate(SectionNodeInstance addressHistorySectionInstance) {

        SubSectionNodeInstance currentPhysicalAddressSubSectionInstance = (SubSectionNodeInstance) addressHistorySectionInstance.getChildren().stream()
                .filter({ x -> x.getDefinitionNode().getId().equals('SubSec_currentPhysicalAddressForBeneficiary') })
                .findFirst()
                .orElse(null)
        // 7 id = Q_2028, name=dateThatYourConditionalPermanentResidenceBegan
        // children size = 9, 8 id = Q_2029, name=haveYouResidedAnyAddressSincePRBegan, visibility=false
        assertNotNull(currentPhysicalAddressSubSectionInstance)

        // get current Physical Address Children and validate 'haveYouResidedAnyAddressSincePRBegan
        List<EasyVisaNodeInstance> curAddressQuestions = currentPhysicalAddressSubSectionInstance.children
        assertNotNull(curAddressQuestions)
        assertEquals(9, curAddressQuestions.size())

        // assert newly added question 2028 and 2029 exist
        QuestionNodeInstance q2028 = (QuestionNodeInstance) curAddressQuestions[7]
        assertEquals('dateThatYourConditionalPermanentResidenceBegan', q2028?.name)

        QuestionNodeInstance q2029 = (QuestionNodeInstance) curAddressQuestions[8]
        assertEquals('haveYouResidedAnyAddressSincePRBegan', q2029?.name)
        assertFalse(q2029.visibility)
        Answer haveYouResidedAnyAddressSincePRBeganAnswer = q2029?.answer
        assertEquals("no", haveYouResidedAnyAddressSincePRBeganAnswer.value)
    }
}
