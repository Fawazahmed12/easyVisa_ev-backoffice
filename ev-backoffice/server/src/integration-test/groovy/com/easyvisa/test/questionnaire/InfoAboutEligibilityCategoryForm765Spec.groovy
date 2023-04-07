package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.*
import com.easyvisa.questionnaire.dto.CompletionWarningDto
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.AnswerListStub
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.QuestionnaireTestDBSetupUtility
import com.easyvisa.utils.TestMockUtils
import grails.gorm.transactions.Transactional
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import spock.lang.Unroll

import java.time.LocalDate
import java.util.stream.Collectors

import static org.junit.Assert.*

@Integration
class InfoAboutEligibilityCategoryForm765Spec extends TestMockUtils {

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

    @Autowired
    private AlertService alertService

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

    @Transactional
    @Unroll
    void "Test Eligibility Category With #label"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildNoPetitionerOpenPackage(ImmigrationBenefitCategory.EAD)

        String sectionId = "Sec_informationAboutEligibilityCategory"
        Package aPackage = testHelper.aPackage

        Applicant petitionerApplicant = aPackage.benefits[0].applicant


        List<Answer> answerList = AnswerListStub.eligibilityCategoryForEAD(aPackage.id, petitionerApplicant.id, eligibilityCategory, arrQ)


        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        List<Answer> sectionAnswerList = answerService.fetchAnswers(aPackage.id, petitionerApplicant.id)
        SectionNodeInstance eligibilityCategorySectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id,
                        petitionerApplicant.id, sectionId, sectionAnswerList)


        Warning w = Warning.findByApplicantAndAPackage(petitionerApplicant, aPackage)


        then:
        assertNotNull(eligibilityCategorySectionInstance)


        // Get Subsection
        SubSectionNodeInstance subSecNodeInstance = eligibilityCategorySectionInstance.children[0]
        assertNotNull(subSecNodeInstance)
        // Get questions
        List<QuestionNodeInstance> questions = subSecNodeInstance.children
        assertNotNull(questions)
        // Two top level questions
        assertEquals(2, questions.size())

        assertTrue(questions*.id.containsAll(topLevelQuestIds))
        // get children of Q_6202 - Eligibility Category Dropdown
        List<QuestionNodeInstance> childQuestions = questions.find { it.id == "Q_6202" }?.children
        // Filter Only visible questions
        List<QuestionNodeInstance> visibleChildQuestions = childQuestions.findAll { it.visibility == true }
        // assert size of questions that come up on dropdown selection value for category
        assertEquals(questCount, visibleChildQuestions.size())
        if (questCount > 0) {
            assertTrue(visibleChildQuestions*.id.containsAll(questIds))
        }

        // Assert Warning for "Have you been Arrested?"
        if (wmsg) {
            assertNotNull(w)
            assertTrue(w.body?.contains(wmsg))

        }

        cleanup:
        testHelper.clean()

        where:
        label                                                                 | eligibilityCategory | topLevelQuestIds     | questCount | questIds                       | arrQ     | wmsg
        "c3C- F-1 student, 24-month extension for STEM students"              | "c_3_C"             | ["Q_6201", "Q_6202"] | 3          | ["Q_6203", "Q_6204", "Q_6205"] | null     | null
        "c35 Principal beneficiary of an approved employment-based immigrant" | "c_35"              | ["Q_6201", "Q_6202"] | 2          | ["Q_6210", "Q_6206"]           | "Q_6210" | "Refer to Employment-Based Nonimmigrant Categories"
        "c8 Asylum application pending"                                       | "c_8"               | ["Q_6201", "Q_6202"] | 1          | ["Q_6209"]                     | "Q_6209" | "which may require Special Filing"
        "c36 Spouse or unmarried child of a principal beneficiary"            | "c_36"              | ["Q_6201", "Q_6202"] | 2          | ["Q_6207", "Q_6210"]           | null     | null
        "c26 Spouse of an H-1B nonimmigrant"                                  | "c_26"              | ["Q_6201", "Q_6202"] | 1          | ["Q_6208"]                     | null     | null
        "a5 Asylee"                                                           | "a5"                | ["Q_6201", "Q_6202"] | 0          | []                             | null     | null


    }

}
