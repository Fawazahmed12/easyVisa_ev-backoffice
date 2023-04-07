package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.*
import com.easyvisa.questionnaire.dto.CompletionWarningDto
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.AnswerListStub
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.QuestionnaireTestDBSetupUtility
import com.easyvisa.utils.TestMockUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import spock.lang.Unroll

import java.time.LocalDate
import java.util.stream.Collectors

import static org.junit.Assert.*

@Integration
class EmploymentHistoryCompletionRuleSpec extends TestMockUtils {

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
    void "Test Employment History With #label"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        String sectionId = "Sec_employmentHistory"
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant


        LocalDate startDate = DateUtil.today().minusYears(currentDuration)
        // get dateRows
        List dateHistory = generateEmploymentDates(startDate, gapInCurAndPrev, numIteration, gapInDays, gapInHowManyIterations, removeStart, removeEnd, stillWorking)

        List<Answer> answerList = AnswerListStub.employmentHistoryAnswerList(aPackage.id, petitionerApplicant.id,
                startDate, dateHistory)

        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        List<Answer> sectionAnswerList = answerService.fetchAnswers(aPackage.id, petitionerApplicant.id)
        SectionNodeInstance addressHistorySectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id,
                        petitionerApplicant.id, sectionId, sectionAnswerList)

        //Long packageId, Long applicantId, String sectionId, LocalDate currentDate
        CompletionWarningDto fqcw = packageQuestionnaireService.fetchQuestionnaireCompletionWarning(aPackage.id, petitionerApplicant.id, sectionId, LocalDate.now())

        then:
        assertNotNull(addressHistorySectionInstance)
        assert (fqcw.hasSectionWarning == warning)
        if (!messageContains)
            assertNull(fqcw.warningMessage)
        else
            assertTrue(fqcw.warningMessage.contains(messageContains))

        cleanup:
        testHelper.clean()

        where:
        label                                                          | currentDuration | gapInCurAndPrev | numIteration | removeStart | removeEnd | gapInDays | gapInHowManyIterations | warning | messageContains              | stillWorking
        "No Gap, No Warning"                                           | 2               | 1               | 3            | -1          | -1        | 0         | 2                      | false   | null                         | -1
        "History Gap = 1, No Warning"                                  | 2               | 1               | 3            | -1          | -1        | 1         | 2                      | false   | null                         | -1
        "History Gap= 30, No Warning"                                  | 2               | 1               | 3            | -1          | -1        | 30        | 1                      | false   | null                         | -1
        "History Gap = 31, Gap Warning"                                | 2               | 1               | 4            | -1          | -1        | 31        | 2                      | true    | "30 days between employment" | -1
        "History Gap = 35, Gap Warning"                                | 1               | 1               | 4            | -1          | -1        | 35        | 2                      | true    | "30 days between employment" | -1
        "Current and History Gap = 31"                                 | 1               | 31              | 4            | -1          | -1        | 35        | 2                      | true    | "30 days between employment" | -1
        "Current, History Gap = 31, Address <5 Years, 5 Years Warning" | 1               | 31              | 1            | -1          | -1        | 1         | 2                      | true    | "5 full years"               | -1
        "Address <5 Years, 5 Years Warning"                            | 1               | 1               | 2            | -1          | -1        | 1         | 2                      | true    | "5 full years"               | -1
        "Missing Start Date"                                           | 1               | 20              | 4            | 1           | -1        | 20        | 2                      | false   | null                         | -1
        "Missing End Date"                                             | 1               | 1               | 4            | -1          | 2         | 1         | 2                      | false   | null                         | -1
        "Missing End Date in First History"                            | 1               | 1               | 4            | -1          | 0         | 1         | 2                      | false   | null                         | -1
        "Missing Start Date in First History"                          | 1               | 1               | 4            | 0           | -1        | 1         | 2                      | false   | null                         | -1
        "Still Working Yes, No Gap, No Warning"                        | 2               | 1               | 3            | -1          | -1        | 1         | 2                      | false   | null                         | 2
        "Still Working Yes, Overlapping jobs, No Warning"              | 2               | -300            | 4            | -1          | -1        | 1         | 2                      | false   | null                         | 2
        "Still Working Yes, Overlapping jobs, Gap Warning"             | 2               | -300            | 4            | -1          | -1        | 31        | 1                      | true    | "30 days between employment" | 2
        "Still Working Yes, 5 Year Warning"                            | 2               | -100            | 2            | -1          | -1        | 30        | 1                      | true    | "5 full years"               | 2
        "History after current, Still working"                         | 1               | -370            | 4            | -1          | -1        | 30        | 1                      | false   | ""                           | 0
        "Current Employment > 5 years"                                 | 5               | 0               | 0            | -1          | -1        | 0         | 0                      | false   | ""                           | 0

    }

    /**
     * Based on number of splits we generate a set of move-in, move-out data
     * @param splits
     * @param gap
     * @return
     */

    private List generateEmploymentDates(LocalDate startFrom, int gapBetwenCurrentAndFirstPrevious, int numIterations, int gapInDays, int gapInHowManyIterations, int removeStart = -1, int removeEnd = -1, int stillWorking = -1) {

        // We'll move backwards from startDate
        // numIterations determine how many sets to create
        // Each stay iteration will be 1 year long
        List dateRows = []
        LocalDate lastEndDate = startFrom.minusDays(gapBetwenCurrentAndFirstPrevious)
        LocalDate endDate = lastEndDate
        List lstEmpStatus = ["Employed", "Unemployed", "Retired"]

        Map lstStatusFields = [
                "Employed"  : [
                        "Sec_employmentHistory/SubSec_employmentStatus/Q_1008",
                        "Sec_employmentHistory/SubSec_employmentStatus/Q_1014",
                        "Sec_employmentHistory/SubSec_employmentStatus/Q_1015",
                        "Sec_employmentHistory/SubSec_employmentStatus/Q_1016"
                ],

                "Unemployed": [
                        "Sec_employmentHistory/SubSec_employmentStatus/Q_1008",
                        "Sec_employmentHistory/SubSec_employmentStatus/Q_1009",
                        "Sec_employmentHistory/SubSec_employmentStatus/Q_1010"
                ],
                "Retired"   : [
                        "Sec_employmentHistory/SubSec_employmentStatus/Q_1008",
                        "Sec_employmentHistory/SubSec_employmentStatus/Q_1011",
                        "Sec_employmentHistory/SubSec_employmentStatus/Q_1012"
                ]
        ]
        Random rnd = new Random()

        (0..numIterations).each { iteration ->
            LocalDate startDate = endDate.minusYears(1)
            // index to select random employment Status
            int idx = Math.abs(rnd.nextInt(1000000) % 3)
            String status = lstEmpStatus[idx]

            // Populate data rows
            List flds = lstStatusFields[status]

            Map tmpMap = [
                    (flds[0]): status,
                    (flds[1]): (iteration == removeStart) ? null : startDate
            ]

            if (status == "Employed") {
                if (stillWorking == iteration) {
                    tmpMap[flds[2]] = RelationshipTypeConstants.YES.value
                } else {
                    tmpMap[flds[2]] = RelationshipTypeConstants.NO.value
                    tmpMap[flds[3]] = (iteration == removeEnd) ? null : endDate
                }
            } else {
                tmpMap[flds[2]] = (iteration == removeEnd) ? null : endDate

            }

            dateRows << tmpMap


            endDate = (iteration < gapInHowManyIterations) ? startDate.minusDays(gapInDays) : startDate.minusDays(1)
        }

        return dateRows

    }
}
