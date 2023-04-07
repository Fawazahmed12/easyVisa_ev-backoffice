package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.*
import com.easyvisa.questionnaire.dto.InputTypeConstant
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.AnswerListStub
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.QuestionnaireTestDBSetupUtility
import com.easyvisa.utils.TestMockUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import java.time.LocalDate
import java.time.Month
import java.util.stream.Collectors

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

@Integration
class EmployeeHistoryForBeneficiarySpec extends TestMockUtils {

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

    void testEmployedStatusEmployeeHistoryQuestionnaire() throws Exception {
        given:
        String sectionId = 'Sec_employmentHistoryForBeneficiary'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.principalBeneficiary

        List<Answer> answerList = AnswerListStub.employedStatusEmployeeHistoryForBeneficiaryAnswerList(aPackage.id,
                applicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id, sectionId,
                answerList)

        when:
        SectionNodeInstance employeeHistorySectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id,
                        sectionId, answerList)

        then:
        assertNotNull(employeeHistorySectionInstance)
        assertEmployedStatusHistoryAnswers(employeeHistorySectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertEmployedStatusHistoryAnswers(SectionNodeInstance employeeHistorySectionInstance) {
        List<EasyVisaNodeInstance> employedStatusEmployeeHistorySectionInstanceList = employeeHistorySectionInstance.getChildren()
        assertEquals(1, employedStatusEmployeeHistorySectionInstanceList.size())

        List<EasyVisaNodeInstance> validEmployeeHistorySectionInstanceList = employedStatusEmployeeHistorySectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())
        assertEquals(1, validEmployeeHistorySectionInstanceList.size())

        assertEmploymentStatusSubSectionInstanceAnswers(validEmployeeHistorySectionInstanceList[0])
    }


    private void assertEmploymentStatusSubSectionInstanceAnswers(SubSectionNodeInstance employmentStatusInstance) {
        assertNotNull(employmentStatusInstance)
        assertEquals('SubSec_employmentStatusForBeneficiary', employmentStatusInstance.getId())
        List<EasyVisaNodeInstance> subsectionChildren = employmentStatusInstance.getChildren()
        assertEquals(4, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())
        QuestionNodeInstance employmentStatusInfoLabelQuestion = questionNodeInstanceList[0]
        assertEquals('employmentStatusInfoLabel', employmentStatusInfoLabelQuestion.getName())
        assertEquals(InputTypeConstant.LABEL.value, employmentStatusInfoLabelQuestion.getInputType())
        assertEquals('Q_2601', employmentStatusInfoLabelQuestion.getId())

        List<RepeatingQuestionGroupNodeInstance> repeatingQuestionGroupNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof RepeatingQuestionGroupNodeInstance) })
                .map({ nodeInstance -> (RepeatingQuestionGroupNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(3, repeatingQuestionGroupNodeInstanceList.size())

        assertEmployedStatusRepeatingQuestionGroupNodeInstance1(repeatingQuestionGroupNodeInstanceList[0])
        assertUnemployedStatusRepeatingQuestionGroupNodeInstance(repeatingQuestionGroupNodeInstanceList[1])
        assertEmployedStatusRepeatingQuestionGroupNodeInstance2(repeatingQuestionGroupNodeInstanceList[2])
    }

    private void assertEmployedStatusRepeatingQuestionGroupNodeInstance1(RepeatingQuestionGroupNodeInstance employedRepeatingQuestionGroupNodeInstance) {
        assertEquals('RQG_employmentStatusForBeneficiary', employedRepeatingQuestionGroupNodeInstance.getId())
        assertEquals('Employer/Employment Status 1', employedRepeatingQuestionGroupNodeInstance.getDisplayText())
        List<EasyVisaNodeInstance> repeatingQuestionGroupChildren = employedRepeatingQuestionGroupNodeInstance.getChildren()
        assertEquals(2, repeatingQuestionGroupChildren.size())

        QuestionNodeInstance employmentStatusIterationLabelQuestion = repeatingQuestionGroupChildren[0]
        assertEquals('employmentStatusIterationLabel', employmentStatusIterationLabelQuestion.getName())
        assertEquals('Employer/Employment Status 1', employmentStatusIterationLabelQuestion.getDisplayText())

        QuestionNodeInstance currentEmploymentStatusQuestion = repeatingQuestionGroupChildren[1]
        assertEquals('currentEmploymentStatus', currentEmploymentStatusQuestion.getName())
        assertEquals('What is your current employment status?', currentEmploymentStatusQuestion.getDisplayText())
        Answer currentEmploymentStatusAnswer = currentEmploymentStatusQuestion.getAnswer()
        assertEquals('Employed', currentEmploymentStatusAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2608/0', currentEmploymentStatusAnswer.getPath())

        assertEquals(14, currentEmploymentStatusQuestion.getChildren().size())
        List<QuestionNodeInstance> questionNodeInstanceList = currentEmploymentStatusQuestion.getChildren().stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()})
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(7, questionNodeInstanceList.size())

        QuestionNodeInstance startDateOfCurrentEmploymentQuestion = questionNodeInstanceList[0]
        assertEquals('startDateOfCurrentEmployment', startDateOfCurrentEmploymentQuestion.getName())
        Answer startDateOfCurrentEmploymentAnswer = startDateOfCurrentEmploymentQuestion.getAnswer()
        assertEquals(DateUtil.fromDate(LocalDate.of(2019, Month.JANUARY, 17)), startDateOfCurrentEmploymentAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2614/0', startDateOfCurrentEmploymentAnswer.getPath())

        QuestionNodeInstance employerCountryLocationQuestion = questionNodeInstanceList[1]
        assertEquals('employerCountryLocation', employerCountryLocationQuestion.getName())
        Answer employerCountryLocationAnswer = employerCountryLocationQuestion.getAnswer()
        assertEquals('United States', employerCountryLocationAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2618/0', employerCountryLocationAnswer.getPath())

        QuestionNodeInstance employerFullNameQuestion = questionNodeInstanceList[2]
        assertEquals('employerFullName', employerFullNameQuestion.getName())
        Answer employerFullNameAnswer = employerFullNameQuestion.getAnswer()
        assertEquals('Kevin Peterson', employerFullNameAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2619/0', employerFullNameAnswer.getPath())

        QuestionNodeInstance employerStreetNumberAndNameQuestion = questionNodeInstanceList[3]
        assertEquals('employerStreetNumberAndName', employerStreetNumberAndNameQuestion.getName())
        Answer employerStreetNumberAndNameAnswer = employerStreetNumberAndNameQuestion.getAnswer()
        assertEquals('400 Godown Street', employerStreetNumberAndNameAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2620/0', employerStreetNumberAndNameAnswer.getPath())

        QuestionNodeInstance hasEmployerHavingSecondaryAddressDescriptionQuestion = questionNodeInstanceList[4]
        assertEquals('hasEmployerHavingSecondaryAddressDescription', hasEmployerHavingSecondaryAddressDescriptionQuestion.getName())
        Answer hasEmployerHavingSecondaryAddressDescriptionAnswer = hasEmployerHavingSecondaryAddressDescriptionQuestion.getAnswer()
        assertEquals('Yes', hasEmployerHavingSecondaryAddressDescriptionAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2621/0', hasEmployerHavingSecondaryAddressDescriptionAnswer.getPath())

        QuestionNodeInstance employerCityQuestion = questionNodeInstanceList[5]
        assertEquals('employerCity', employerCityQuestion.getName())
        Answer employerCityAnswer = employerCityQuestion.getAnswer()
        assertEquals('Aurora', employerCityAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2624/0', employerCityAnswer.getPath())

        QuestionNodeInstance employmentOccupationQuestion = questionNodeInstanceList[6]
        assertEquals('employmentOccupation', employmentOccupationQuestion.getName())
        assertEquals('What is your occupation at this employer?', employmentOccupationQuestion.getDisplayText())
        Answer employmentOccupationAnswer = employmentOccupationQuestion.getAnswer()
        assertEquals('Senior Software Developer', employmentOccupationAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2629/0', employmentOccupationAnswer.getPath())


        assertEquals(2, hasEmployerHavingSecondaryAddressDescriptionQuestion.getChildren().size())
        List<QuestionNodeInstance> hasEmployerHavingSecondaryAddressDescriptionQuestionInstanceList = hasEmployerHavingSecondaryAddressDescriptionQuestion.getChildren().stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()})
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, hasEmployerHavingSecondaryAddressDescriptionQuestionInstanceList.size())


        QuestionNodeInstance employerSecondaryAddressDescriptionQuestion = hasEmployerHavingSecondaryAddressDescriptionQuestionInstanceList[0]
        assertEquals('employerSecondaryAddressDescription', employerSecondaryAddressDescriptionQuestion.getName())
        Answer employerSecondaryAddressDescriptionAnswer = employerSecondaryAddressDescriptionQuestion.getAnswer()
        assertEquals('Floor', employerSecondaryAddressDescriptionAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2622/0', employerSecondaryAddressDescriptionAnswer.getPath())

        QuestionNodeInstance employerApartmentQuestion = hasEmployerHavingSecondaryAddressDescriptionQuestionInstanceList[1]
        assertEquals('employerApartment', employerApartmentQuestion.getName())
        Answer employerApartmentAnswer = employerApartmentQuestion.getAnswer()
        assertEquals('21', employerApartmentAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2623/0', employerApartmentAnswer.getPath())


        assertEquals(4, employerCountryLocationQuestion.getChildren().size())
        List<QuestionNodeInstance> employerCountryLocationQuestionInstanceList = employerCountryLocationQuestion.getChildren().stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance)  && nodeInstance.isVisibility()})
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, employerCountryLocationQuestionInstanceList.size())

        QuestionNodeInstance employerStateQuestion = employerCountryLocationQuestionInstanceList[0]
        assertEquals('employerState', employerStateQuestion.getName())
        Answer employerStateAnswer = employerStateQuestion.getAnswer()
        assertEquals('Colorado', employerStateAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2625/0', employerStateAnswer.getPath())

        QuestionNodeInstance employerZipCodeQuestion = employerCountryLocationQuestionInstanceList[1]
        assertEquals('employerZipCode', employerZipCodeQuestion.getName())
        Answer employerZipCodeAnswer = employerZipCodeQuestion.getAnswer()
        assertEquals('80011', employerZipCodeAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2627/0', employerZipCodeAnswer.getPath())
    }

    private void assertUnemployedStatusRepeatingQuestionGroupNodeInstance(RepeatingQuestionGroupNodeInstance unEmployedRepeatingQuestionGroupNodeInstance) {
        assertEquals('RQG_employmentStatusForBeneficiary', unEmployedRepeatingQuestionGroupNodeInstance.getId())
        assertEquals('Employer/Employment Status 2', unEmployedRepeatingQuestionGroupNodeInstance.getDisplayText())
        List<EasyVisaNodeInstance> repeatingQuestionGroupChildren = unEmployedRepeatingQuestionGroupNodeInstance.getChildren()
        assertEquals(2, repeatingQuestionGroupChildren.size())

        QuestionNodeInstance employmentStatusIterationLabelQuestion = repeatingQuestionGroupChildren[0]
        assertEquals('employmentStatusIterationLabel', employmentStatusIterationLabelQuestion.getName())
        assertEquals('Employer/Employment Status 2', employmentStatusIterationLabelQuestion.getDisplayText())

        QuestionNodeInstance currentEmploymentStatusQuestion = repeatingQuestionGroupChildren[1]
        assertEquals('currentEmploymentStatus', currentEmploymentStatusQuestion.getName())
        assertEquals('What was your previous employment status?', currentEmploymentStatusQuestion.getDisplayText())
        Answer currentEmploymentStatusAnswer = currentEmploymentStatusQuestion.getAnswer()
        assertEquals('Unemployed', currentEmploymentStatusAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2608/1', currentEmploymentStatusAnswer.getPath())

        assertEquals(14, currentEmploymentStatusQuestion.getChildren().size())
        List<QuestionNodeInstance> questionNodeInstanceList = currentEmploymentStatusQuestion.getChildren().stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()})
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, questionNodeInstanceList.size())

        QuestionNodeInstance dateBecameUnemployedQuestion = questionNodeInstanceList[0]
        assertEquals('dateBecameUnemployed', dateBecameUnemployedQuestion.getName())
        Answer dateBecameUnemployedAnswer = dateBecameUnemployedQuestion.getAnswer()
        assertEquals(DateUtil.fromDate(LocalDate.of(2018, Month.MAY, 17)), dateBecameUnemployedAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2609/1', dateBecameUnemployedAnswer.getPath())

        QuestionNodeInstance lastDateOfUnemploymentQuestion = questionNodeInstanceList[1]
        assertEquals('lastDateOfUnemployment', lastDateOfUnemploymentQuestion.getName())
        Answer lastDateOfUnemploymentAnswer = lastDateOfUnemploymentQuestion.getAnswer()
        assertEquals(DateUtil.fromDate(LocalDate.of(2019, Month.JANUARY, 5)), lastDateOfUnemploymentAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2610/1', lastDateOfUnemploymentAnswer.getPath())
    }

    private void assertEmployedStatusRepeatingQuestionGroupNodeInstance2(RepeatingQuestionGroupNodeInstance employedRepeatingQuestionGroupNodeInstance) {
        assertNotNull(employedRepeatingQuestionGroupNodeInstance)
        assertEquals(employedRepeatingQuestionGroupNodeInstance.addButtonTitle, 'Add Another Employment Status')
        assertEquals(employedRepeatingQuestionGroupNodeInstance.answerIndex, 2)
        assertNotNull(employedRepeatingQuestionGroupNodeInstance.attributes)
        assertEquals(employedRepeatingQuestionGroupNodeInstance.attributes['showAddButton'],true)
        assertEquals(employedRepeatingQuestionGroupNodeInstance.attributes['showRemoveButton'],true)

        assertEquals('RQG_employmentStatusForBeneficiary', employedRepeatingQuestionGroupNodeInstance.getId())
        assertEquals('Employer/Employment Status 3', employedRepeatingQuestionGroupNodeInstance.getDisplayText())
        List<EasyVisaNodeInstance> repeatingQuestionGroupChildren = employedRepeatingQuestionGroupNodeInstance.getChildren()
        assertEquals(2, repeatingQuestionGroupChildren.size())

        QuestionNodeInstance employmentStatusIterationLabelQuestion = repeatingQuestionGroupChildren[0]
        assertEquals('employmentStatusIterationLabel', employmentStatusIterationLabelQuestion.getName())
        assertEquals('Employer/Employment Status 3', employmentStatusIterationLabelQuestion.getDisplayText())

        QuestionNodeInstance currentEmploymentStatusQuestion = repeatingQuestionGroupChildren[1]
        assertEquals('currentEmploymentStatus', currentEmploymentStatusQuestion.getName())
        assertEquals('What was your previous employment status?', currentEmploymentStatusQuestion.getDisplayText())
        Answer currentEmploymentStatusAnswer = currentEmploymentStatusQuestion.getAnswer()
        assertEquals('Employed', currentEmploymentStatusAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2608/2', currentEmploymentStatusAnswer.getPath())

        assertEquals(14, currentEmploymentStatusQuestion.getChildren().size())
        List<QuestionNodeInstance> questionNodeInstanceList = currentEmploymentStatusQuestion.getChildren().stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()})
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(8, questionNodeInstanceList.size())

        QuestionNodeInstance startDateOfCurrentEmploymentQuestion = questionNodeInstanceList[0]
        assertEquals('startDateOfCurrentEmployment', startDateOfCurrentEmploymentQuestion.getName())
        Answer startDateOfCurrentEmploymentAnswer = startDateOfCurrentEmploymentQuestion.getAnswer()
        assertEquals(DateUtil.fromDate(LocalDate.of(2016, Month.JANUARY, 17)), startDateOfCurrentEmploymentAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2614/2', startDateOfCurrentEmploymentAnswer.getPath())

        QuestionNodeInstance stillWorkingAtThisEmployerQuestion = questionNodeInstanceList[1]
        assertEquals('stillWorkingAtThisEmployer', stillWorkingAtThisEmployerQuestion.getName())
        Answer stillWorkingAtThisEmployerAnswer = stillWorkingAtThisEmployerQuestion.getAnswer()
        assertEquals('Yes', stillWorkingAtThisEmployerAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2615/2', stillWorkingAtThisEmployerAnswer.getPath())

        QuestionNodeInstance employerCountryLocationQuestion = questionNodeInstanceList[2]
        assertEquals('employerCountryLocation', employerCountryLocationQuestion.getName())
        Answer employerCountryLocationAnswer = employerCountryLocationQuestion.getAnswer()
        assertEquals('Australia', employerCountryLocationAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2618/2', employerCountryLocationAnswer.getPath())

        QuestionNodeInstance employerFullNameQuestion = questionNodeInstanceList[3]
        assertEquals('employerFullName', employerFullNameQuestion.getName())
        Answer employerFullNameAnswer = employerFullNameQuestion.getAnswer()
        assertEquals('Watson', employerFullNameAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2619/2', employerFullNameAnswer.getPath())

        QuestionNodeInstance employerStreetNumberAndNameQuestion = questionNodeInstanceList[4]
        assertEquals('employerStreetNumberAndName', employerStreetNumberAndNameQuestion.getName())
        Answer employerStreetNumberAndNameAnswer = employerStreetNumberAndNameQuestion.getAnswer()
        assertEquals('250 Maiden Cross Lane', employerStreetNumberAndNameAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2620/2', employerStreetNumberAndNameAnswer.getPath())

        QuestionNodeInstance hasEmployerHavingSecondaryAddressDescriptionQuestion = questionNodeInstanceList[5]
        assertEquals('hasEmployerHavingSecondaryAddressDescription', hasEmployerHavingSecondaryAddressDescriptionQuestion.getName())
        Answer hasEmployerHavingSecondaryAddressDescriptionAnswer = hasEmployerHavingSecondaryAddressDescriptionQuestion.getAnswer()
        assertEquals('No', hasEmployerHavingSecondaryAddressDescriptionAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2621/2', hasEmployerHavingSecondaryAddressDescriptionAnswer.getPath())

        QuestionNodeInstance employerCityQuestion = questionNodeInstanceList[6]
        assertEquals('employerCity', employerCityQuestion.getName())
        Answer employerCityAnswer = employerCityQuestion.getAnswer()
        assertEquals('Sydney', employerCityAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2624/2', employerCityAnswer.getPath())

        QuestionNodeInstance employmentOccupationQuestion = questionNodeInstanceList[7]
        assertEquals('employmentOccupation', employmentOccupationQuestion.getName())
        assertEquals('What was your occupation at this employer?', employmentOccupationQuestion.getDisplayText())
        Answer employmentOccupationAnswer = employmentOccupationQuestion.getAnswer()
        assertEquals('Software Developer', employmentOccupationAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2629/2', employmentOccupationAnswer.getPath())



        assertEquals(2, hasEmployerHavingSecondaryAddressDescriptionQuestion.getChildren().size())
        List<QuestionNodeInstance> hasEmployerHavingSecondaryAddressDescriptionQuestionInstanceList = hasEmployerHavingSecondaryAddressDescriptionQuestion.getChildren().stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()})
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(0, hasEmployerHavingSecondaryAddressDescriptionQuestionInstanceList.size())



        assertEquals(4, employerCountryLocationQuestion.getChildren().size())
        List<QuestionNodeInstance> employerCountryLocationQuestionInstanceList = employerCountryLocationQuestion.getChildren().stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()})
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, employerCountryLocationQuestionInstanceList.size())

        QuestionNodeInstance employerTerritoryQuestion = employerCountryLocationQuestionInstanceList[0]
        assertEquals('employerTerritory', employerTerritoryQuestion.getName())
        Answer employerTerritoryAnswer = employerTerritoryQuestion.getAnswer()
        assertEquals('Brisbane', employerTerritoryAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2626/2', employerTerritoryAnswer.getPath())

        QuestionNodeInstance employerPostalCodeQuestion = employerCountryLocationQuestionInstanceList[1]
        assertEquals('employerPostalCode', employerPostalCodeQuestion.getName())
        Answer employerPostalCodeAnswer = employerPostalCodeQuestion.getAnswer()
        assertEquals('12334', employerPostalCodeAnswer.getValue())
        assertEquals('Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2628/2', employerPostalCodeAnswer.getPath())
    }
}
