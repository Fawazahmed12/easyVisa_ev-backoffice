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
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import spock.lang.Unroll

import java.time.LocalDate
import java.util.stream.Collectors

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNull
import static org.junit.Assert.assertTrue

@Integration
class AddressHistoryWithFiveYearsSpec extends TestMockUtils {

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

    void testAddressHistoryWithLessThan5YearsData() throws Exception {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        String sectionId = "Sec_addressHistory"
        Package aPackage = testHelper.aPackage;
        Applicant petitionerApplicant = aPackage.petitioner.applicant;

        LocalDate twoYearsAgo = DateUtil.today().minusYears(2)
        List<Answer> answerList = AnswerListStub.addressHistoryWith5YearsAnswerList(aPackage.id, petitionerApplicant.id, twoYearsAgo)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        List<Answer> sectionAnswerList = answerService.fetchAnswers(aPackage.id, petitionerApplicant.id)
        SectionNodeInstance addressHistorySectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id,
                        petitionerApplicant.id, sectionId, sectionAnswerList)

        then:
        assertNotNull(addressHistorySectionInstance)
        assertAddressHistoryWithLessThan5YearsAnswers(addressHistorySectionInstance, twoYearsAgo)

        cleanup:
        testHelper.clean()
    }

    private void assertAddressHistoryWithLessThan5YearsAnswers(SectionNodeInstance addressHistorySectionInstance, LocalDate moveInDate) {
        List<EasyVisaNodeInstance> addressHistorySectionInstanceList = addressHistorySectionInstance.getChildren()
        assertEquals(3, addressHistorySectionInstanceList.size())

        List<EasyVisaNodeInstance> visibleAddressHistorySectionInstanceList = addressHistorySectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())
        assertEquals(3, visibleAddressHistorySectionInstanceList.size())

        assertCurrentPhysicalAddressSubSectionInstance(visibleAddressHistorySectionInstanceList, moveInDate)
        assertPreviousPhysicalAddressSubSectionInstance(visibleAddressHistorySectionInstanceList)
        assertCurrentMailingAddressSubSectionInstance(visibleAddressHistorySectionInstanceList)


    }

    void testAddressHistoryWhichExceedsMoreThan5YearsData() throws Exception {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        String sectionId = "Sec_addressHistory"
        Package aPackage = testHelper.aPackage;
        Applicant petitionerApplicant = aPackage.petitioner.applicant;

        LocalDate fiveYearsAgo = DateUtil.today().minusYears(5)
        List<Answer> answerList = AnswerListStub.addressHistoryWith5YearsAnswerList(aPackage.id, petitionerApplicant.id, fiveYearsAgo)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        List<Answer> sectionAnswerList = answerService.fetchAnswers(aPackage.id, petitionerApplicant.id)
        SectionNodeInstance addressHistorySectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id,
                        petitionerApplicant.id, sectionId, sectionAnswerList)

        then:
        assertNotNull(addressHistorySectionInstance)
        assertAddressHistoryWhichExceedsMoreThan5YearsData(addressHistorySectionInstance, fiveYearsAgo)

        cleanup:
        testHelper.clean()
    }


    private void assertAddressHistoryWhichExceedsMoreThan5YearsData(SectionNodeInstance addressHistorySectionInstance, LocalDate moveInDate) {
        List<EasyVisaNodeInstance> addressHistorySectionInstanceList = addressHistorySectionInstance.getChildren()
        assertEquals(3, addressHistorySectionInstanceList.size())

        List<EasyVisaNodeInstance> visibleAddressHistorySectionInstanceList = addressHistorySectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())
        assertEquals(2, visibleAddressHistorySectionInstanceList.size())

        assertCurrentPhysicalAddressSubSectionInstance(visibleAddressHistorySectionInstanceList, moveInDate)
        assertCurrentMailingAddressSubSectionInstance(visibleAddressHistorySectionInstanceList)
    }

    void testAddressHistoryWhichExceedsMoreThan5YearsSplittedData() throws Exception {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        String sectionId = "Sec_addressHistory"
        Package aPackage = testHelper.aPackage;
        Applicant petitionerApplicant = aPackage.petitioner.applicant;

        LocalDate twoYearsAgo = DateUtil.today().minusYears(2);// 20-Apr-2020
        List<LocalDate> prevMoveInDates = [
                twoYearsAgo.minusMonths(2).minusYears(2), // Iteration:1  20-Feb-2018
                twoYearsAgo.minusMonths(3).minusYears(5),  // Iteration:2  20-Jan-2015
                twoYearsAgo.minusDays(10).minusYears(6)  // Iteration:3  10-Apr-2014

        ];
        List<LocalDate> prevMoveOutDates = [
                twoYearsAgo.minusMonths(2).minusYears(1),  // Iteration:1  20-Feb-2019
                twoYearsAgo.minusDays(10).minusMonths(3).minusYears(3),   // Iteration:2  10-Jan-2017
                twoYearsAgo.plusDays(10).minusYears(6)  // Iteration:3  30-Apr-2014
        ]
        List<Answer> answerList = AnswerListStub.addressHistoryWith5YearsSplittedAnswerList(aPackage.id, petitionerApplicant.id,
                twoYearsAgo, prevMoveInDates, prevMoveOutDates)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        List<Answer> sectionAnswerList = answerService.fetchAnswers(aPackage.id, petitionerApplicant.id)
        SectionNodeInstance addressHistorySectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id,
                        petitionerApplicant.id, sectionId, sectionAnswerList)

        then:
        assertNotNull(addressHistorySectionInstance)
        assertAddressHistoryWhichExceedsMoreThan5YearsSplittedData(addressHistorySectionInstance, twoYearsAgo, prevMoveInDates, prevMoveOutDates)

        cleanup:
        testHelper.clean()
    }

    @Unroll
    void "Test Address History With #label"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        String sectionId = "Sec_addressHistory"
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant


        LocalDate startDate = DateUtil.today().minusYears(currentDuration)
        // get dateRows
        List dateHistory = generateResidenceDates(startDate, gapBetweenCurrentAndFirstPrevious, numIteration, gapInDays, gapInHowManyIterations, removeMoveIn, removeMoveOut)


        List<Answer> answerList = AnswerListStub.addressHistoryForGapDays(aPackage.id, petitionerApplicant.id,
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
        label                                                             | currentDuration | gapBetweenCurrentAndFirstPrevious | numIteration | removeMoveIn | removeMoveOut | gapInDays | gapInHowManyIterations | warning | messageContains
        "No Gap, No Warning"                                              | 2               | 1                                 | 3            | -1           | -1            | 0         | 2                      | false   | null
        "No History, No Warning, Current > 5"                             | 6               | 1                                 | 0            | -1           | -1            | 0         | 2                      | false   | null
        "History Gap = 1, No Warning"                                     | 2               | 1                                 | 3            | -1           | -1            | 1         | 2                      | false   | null
        "History Gap= 30, No Warning"                                     | 2               | 1                                 | 3            | -1           | -1            | 30        | 1                      | false   | null
        "History Gap = 31, Gap Warning"                                   | 2               | 1                                 | 4            | -1           | -1            | 31        | 2                      | true    | "30 days between residences"
        "History Gap = 35, Gap Warning"                                   | 1               | 1                                 | 4            | -1           | -1            | 35        | 2                      | true    | "30 days between residences"
        "Current and History Gap = 31"                                    | 1               | 31                                | 4            | -1           | -1            | 35        | 2                      | true    | "30 days between residences"
        "Current and History Gap = 31, Address <5 Years, 5 Years Warning" | 1               | 31                                | 1            | -1           | -1            | 1         | 2                      | true    | "5 full years"
        "Current and History Gap = 1, Address <5 Years, 5 Years Warning"  | 1               | 1                                 | 2            | -1           | -1            | 1         | 2                      | true    | "5 full years"
        "Missing Movein Date"                                             | 1               | 20                                | 4            | 1            | -1            | 20        | 2                      | false   | null
        "Missing Moveout Date"                                            | 1               | 1                                 | 4            | -1           | 2             | 1         | 2                      | false   | null
        "Missing Moveout Date in First History"                           | 1               | 1                                 | 4            | -1           | 0             | 1         | 2                      | false   | null
        "Missing MoveIn Date in First History"                            | 1               | 1                                 | 4            | 0            | -1            | 1         | 2                      | false   | null


    }

    /**
     * Based on number of splits we generate a set of move-in, move-out data
     * @param splits
     * @param gap
     * @return
     */

    private List generateResidenceDates(LocalDate startFrom, int gapBetwenCurrentAndFirstPrevious, int numIterations, int gapInDays, int gapInHowManyIterations, int removeMoveIn = -1, int removeMoveOut = -1) {

        // We'll move backwards from startDate
        // numIterations determine how many sets to create
        // Each stay iteration will be 1 year long
        List dateRows = []
        LocalDate lastMoveOut = startFrom.minusDays(gapBetwenCurrentAndFirstPrevious)
        LocalDate moveOut = lastMoveOut
        
        // In case we dont want Previous Address History
        if (numIterations<=0) return dateRows

        (0..numIterations).each { iteration ->

            LocalDate moveIn = moveOut.minusYears(1)

            // Add to the list
            dateRows <<
                    ([
                            'moveIn' : (iteration == removeMoveIn) ? null : moveIn,
                            'moveOut': (iteration == removeMoveOut) ? null : moveOut
                    ])
            moveOut = (iteration < gapInHowManyIterations) ? moveIn.minusDays(gapInDays) : moveIn.minusDays(1)
        }
        return dateRows

    }


    private void assertAddressHistoryWhichExceedsMoreThan5YearsSplittedData(SectionNodeInstance addressHistorySectionInstance, LocalDate moveInDate,
                                                                            List<LocalDate> prevMoveInDates, List<LocalDate> prevMoveOutDates) {
        List<EasyVisaNodeInstance> addressHistorySectionInstanceList = addressHistorySectionInstance.getChildren()
        assertEquals(3, addressHistorySectionInstanceList.size())

        List<EasyVisaNodeInstance> visibleAddressHistorySectionInstanceList = addressHistorySectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())
        assertEquals(3, visibleAddressHistorySectionInstanceList.size())

        assertCurrentPhysicalAddressSubSectionInstance(visibleAddressHistorySectionInstanceList, moveInDate)
        assertCurrentMailingAddressSubSectionInstance(visibleAddressHistorySectionInstanceList)
        assertPreviousPhysicalAddressSubSectionInstanceAnswers(visibleAddressHistorySectionInstanceList, prevMoveInDates, prevMoveOutDates)
    }


    private void assertCurrentPhysicalAddressSubSectionInstance(List<EasyVisaNodeInstance> visibleLegalStatusInUSSectionInstanceList,
                                                                LocalDate moveInDate) {
        SubSectionNodeInstance legalStatusInUSndGovtIDNosSubSectionInstance = (SubSectionNodeInstance) visibleLegalStatusInUSSectionInstanceList.stream()
                .filter({ x -> x.getDefinitionNode().getId().equals("SubSec_currentPhysicalAddress") })
                .findFirst()
                .orElse(null)
        assertNotNull(legalStatusInUSndGovtIDNosSubSectionInstance)

        List<EasyVisaNodeInstance> subsectionChildren = legalStatusInUSndGovtIDNosSubSectionInstance.getChildren()
        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertNotNull(questionNodeInstanceList)

        QuestionNodeInstance moveIntoThisAddressQuestion = questionNodeInstanceList.stream()
                .filter({ x -> x.getDefinitionNode().getId().equals("Q_52") })
                .findFirst()
                .orElse(null)
        assertNotNull(moveIntoThisAddressQuestion)

        assertEquals("moveIntoThisAddress", moveIntoThisAddressQuestion.getName())
        Answer moveIntoThisAddressAnswer = moveIntoThisAddressQuestion.getAnswer()
        assertEquals(DateUtil.fromDate(moveInDate), moveIntoThisAddressAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_currentPhysicalAddress/Q_52", moveIntoThisAddressAnswer.getPath())

    }

    private void assertCurrentMailingAddressSubSectionInstance(List<EasyVisaNodeInstance> visibleLegalStatusInUSSectionInstanceList) {
        SubSectionNodeInstance currentMailingAddressSubSectionInstance = (SubSectionNodeInstance) visibleLegalStatusInUSSectionInstanceList.stream()
                .filter({ x -> x.getDefinitionNode().getId().equals("SubSec_currentMailingAddress") })
                .findFirst()
                .orElse(null)
        assertNotNull(currentMailingAddressSubSectionInstance)
    }

    private void assertPreviousPhysicalAddressSubSectionInstance(List<EasyVisaNodeInstance> visibleLegalStatusInUSSectionInstanceList) {
        SubSectionNodeInstance previousPhysicalAddressSubSectionInstance = (SubSectionNodeInstance) visibleLegalStatusInUSSectionInstanceList.stream()
                .filter({ x -> x.getDefinitionNode().getId().equals("SubSec_previousPhysicalAddress") })
                .findFirst()
                .orElse(null)
        assertNotNull(previousPhysicalAddressSubSectionInstance)
    }

    private void assertPreviousPhysicalAddressSubSectionInstanceAnswers(List<EasyVisaNodeInstance> visibleLegalStatusInUSSectionInstanceList,
                                                                        List<LocalDate> prevMoveInDates, List<LocalDate> prevMoveOutDates) {
        SubSectionNodeInstance previousPhysicalAddressSubSectionInstance = (SubSectionNodeInstance) visibleLegalStatusInUSSectionInstanceList.stream()
                .filter({ x -> x.getDefinitionNode().getId().equals("SubSec_previousPhysicalAddress") })
                .findFirst()
                .orElse(null)
        assertNotNull(previousPhysicalAddressSubSectionInstance)

        List<EasyVisaNodeInstance> subsectionChildren = previousPhysicalAddressSubSectionInstance.getChildren()
        assertEquals(3, subsectionChildren.size())

        List<RepeatingQuestionGroupNodeInstance> previousPhysicalAddressRepeatingInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof RepeatingQuestionGroupNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (RepeatingQuestionGroupNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(3, previousPhysicalAddressRepeatingInstanceList.size())


        prevMoveInDates.eachWithIndex { LocalDate prevMoveInDate, int index ->
            RepeatingQuestionGroupNodeInstance repeatingQuestionGroupNodeInstance = previousPhysicalAddressRepeatingInstanceList[index];

            QuestionNodeInstance moveIntoThisAddressQuestion = (QuestionNodeInstance) repeatingQuestionGroupNodeInstance.getChildren().stream()
                    .filter({ x -> x.getDefinitionNode().getId().equals("Q_64") })
                    .findFirst()
                    .orElse(null)
            assertNotNull(moveIntoThisAddressQuestion)

            assertEquals("moveIntoThisAddress", moveIntoThisAddressQuestion.getName())
            Answer moveIntoThisAddressAnswer = moveIntoThisAddressQuestion.getAnswer()
            assertEquals(DateUtil.fromDate(prevMoveInDate), moveIntoThisAddressAnswer.getValue())
            assertEquals("Sec_addressHistory/SubSec_previousPhysicalAddress/Q_64/${index}".toString(), moveIntoThisAddressAnswer.getPath())
        }

        prevMoveOutDates.eachWithIndex { LocalDate prevMoveOutDate, int index ->
            RepeatingQuestionGroupNodeInstance repeatingQuestionGroupNodeInstance = previousPhysicalAddressRepeatingInstanceList[index];

            QuestionNodeInstance moveOutOfThisAddressQuestion = (QuestionNodeInstance) repeatingQuestionGroupNodeInstance.getChildren().stream()
                    .filter({ x -> x.getDefinitionNode().getId().equals("Q_65") })
                    .findFirst()
                    .orElse(null)
            assertNotNull(moveOutOfThisAddressQuestion)

            assertEquals("moveOutOfThisAddress", moveOutOfThisAddressQuestion.getName())
            Answer moveOutOfThisAddressAnswer = moveOutOfThisAddressQuestion.getAnswer()
            assertEquals(DateUtil.fromDate(prevMoveOutDate), moveOutOfThisAddressAnswer.getValue())
            assertEquals("Sec_addressHistory/SubSec_previousPhysicalAddress/Q_65/${index}".toString(), moveOutOfThisAddressAnswer.getPath())
        }
    }
}
