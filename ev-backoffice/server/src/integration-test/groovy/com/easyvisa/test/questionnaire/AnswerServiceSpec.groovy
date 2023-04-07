package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.SectionNodeInstance
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.QuestionnaireTestDBSetupUtility
import com.easyvisa.utils.TestMockUtils
import com.easyvisa.utils.TestUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

@Integration
class AnswerServiceSpec extends TestMockUtils {

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
    private ProfileService profileService

    @Autowired
    private PaymentService paymentService
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

    void testDocumentActionForPetitionInPrev2Years() throws Exception {
        given:
        String sectionId = 'Sec_1'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = [TestUtils.getAnswerInstance(aPackage.id, petitionerApplicant.id,
                'Sec_1/SubSec_1/Q_13', 'yes')]
        //Have you had a petition of Alien Fiance(e) approved by the USCIS within the previous two years? Help
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        SectionNodeInstance sectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id,
                        petitionerApplicant.id, sectionId, answerList)

        then:
        assertNotNull(sectionInstance)
        assertPetitionerWarnings(sectionInstance, aPackage)

        cleanup:
        testHelper.clean()
    }

    private void assertPetitionerWarnings(SectionNodeInstance sectionInstance, Package aPackage) {
        assertNotNull(aPackage)
        assertNotNull(sectionInstance)
        Warning.withNewTransaction {
            Warning hadPetitionInPrev2YearsDocumentAction = Warning.findByAPackageAndQuestionId(aPackage, 'Q_13')
            assertNotNull(hadPetitionInPrev2YearsDocumentAction)
            assertEquals(hadPetitionInPrev2YearsDocumentAction.body, 'Your client petitioner-first ' +
                    "petitioner-last answered 'Yes' to the question: '<span class='warning-question'>Have you had a petition of Alien Fiance(e) " +
                    "approved by the USCIS within the previous two years?</span>'.")
        }
    }

}
