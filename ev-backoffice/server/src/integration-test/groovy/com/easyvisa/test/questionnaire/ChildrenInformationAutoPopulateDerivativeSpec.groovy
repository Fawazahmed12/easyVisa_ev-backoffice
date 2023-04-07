package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.RepeatingQuestionGroupNodeInstance
import com.easyvisa.questionnaire.answering.SectionNodeInstance
import com.easyvisa.questionnaire.answering.SubSectionNodeInstance
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.TestMockUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import java.util.stream.Collectors

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

@Integration
class ChildrenInformationAutoPopulateDerivativeSpec extends TestMockUtils {

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

    void testFamilyInformation_DerivativeChildrenQuestionnaire() throws Exception {
        given:
        String sectionId = 'Sec_familyInformationForBeneficiary'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndTwoBeneficiariesOpenPackage(ImmigrationBenefitCategory.K1K3, ImmigrationBenefitCategory.K2K4)

        when:
        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.principalBeneficiary;
        List<Answer> answerList = answerService.fetchAnswers(aPackage.id, applicant.id)
        SectionNodeInstance familyInformationSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id,
                        sectionId, answerList)

        then:
        assertNotNull(familyInformationSectionInstance)
        assertFamilyInformationDerivativeChildrenAnswers(familyInformationSectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertFamilyInformationDerivativeChildrenAnswers(SectionNodeInstance familyInformationSectionInstance) {
        List<EasyVisaNodeInstance> familyInformationSectionInstanceList = familyInformationSectionInstance.getChildren()
        assertEquals(5, familyInformationSectionInstanceList.size())

        List<EasyVisaNodeInstance> visibleFamilyInformationSectionInstanceList = familyInformationSectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())
        assertEquals(4, visibleFamilyInformationSectionInstanceList.size())

        SubSectionNodeInstance childrenInformationSubSectionInstance = (SubSectionNodeInstance) visibleFamilyInformationSectionInstanceList.stream()
                .filter({ x -> x.getDefinitionNode().getId().equals('SubSec_childrenInformationForBeneficiary') })
                .findFirst()
                .orElse(null)
        assertNotNull(childrenInformationSubSectionInstance)

        List<QuestionNodeInstance> childrenInformationQuestionNodeInstanceList = childrenInformationSubSectionInstance.getChildren().stream()
                        .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                        .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                        .collect(Collectors.toList())
        assertEquals(childrenInformationQuestionNodeInstanceList.size(), 1)

        QuestionNodeInstance doYouHaveAnyChldrenQuestion = childrenInformationQuestionNodeInstanceList[0]
        assertEquals('doYouHaveAnyChldren', doYouHaveAnyChldrenQuestion.getName())
        Answer doYouHaveAnyChldrenAnswer = doYouHaveAnyChldrenQuestion.getAnswer()
        assertEquals('yes', doYouHaveAnyChldrenAnswer.getValue())
        assertEquals('Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2741', doYouHaveAnyChldrenAnswer.getPath())


        List<RepeatingQuestionGroupNodeInstance> repeatingQuestionGroupNodeInstanceList = doYouHaveAnyChldrenQuestion.getChildren().stream()
                .filter({ nodeInstance -> (nodeInstance instanceof RepeatingQuestionGroupNodeInstance) })
                .map({ nodeInstance -> (RepeatingQuestionGroupNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, repeatingQuestionGroupNodeInstanceList.size())

        RepeatingQuestionGroupNodeInstance repeatingQuestionGroupNodeInstance1 = repeatingQuestionGroupNodeInstanceList.get(0)
        assertEquals(10, repeatingQuestionGroupNodeInstance1.getChildren().size())

        List<QuestionNodeInstance> childrenInformationForBeneficiaryFirstInstanceQuestionList = repeatingQuestionGroupNodeInstance1.getChildren().stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertNotNull(childrenInformationForBeneficiaryFirstInstanceQuestionList)
        assertEquals(10, childrenInformationForBeneficiaryFirstInstanceQuestionList.size())


        QuestionNodeInstance childGivenNameQuestion = childrenInformationForBeneficiaryFirstInstanceQuestionList.get(1)
        assertEquals("childGivenName", childGivenNameQuestion.getName())
        Answer childGivenNameAnswer = childGivenNameQuestion.getAnswer()
        assertEquals("derivative-applicant-first", childGivenNameAnswer.getValue())
        assertEquals("Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2743/0", childGivenNameAnswer.getPath())

        QuestionNodeInstance childMiddleNameQuestion = childrenInformationForBeneficiaryFirstInstanceQuestionList.get(2)
        assertEquals("childMiddleName", childMiddleNameQuestion.getName())
        Answer childMiddleNameAnswer = childMiddleNameQuestion.getAnswer()
        assertEquals("Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2746/0", childMiddleNameAnswer.getPath())

        QuestionNodeInstance childLastNameQuestion = childrenInformationForBeneficiaryFirstInstanceQuestionList.get(3)
        assertEquals("childLastName", childLastNameQuestion.getName())
        Answer childLastNameAnswer = childLastNameQuestion.getAnswer()
        assertEquals("derivative-applicant-last", childLastNameAnswer.getValue())
        assertEquals("Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2749/0", childLastNameAnswer.getPath())
    }
}
