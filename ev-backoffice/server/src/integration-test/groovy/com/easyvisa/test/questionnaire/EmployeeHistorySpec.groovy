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
import com.easyvisa.User
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.dto.*
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
class EmployeeHistorySpec extends TestMockUtils {

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

    void testNewEmployeeHistoryQuestionnaire() throws Exception {
        given:
        String sectionId = 'Sec_employmentHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant

        List<Answer> answerList = AnswerListStub.employedStatusEmployeeHistoryAnswerList(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        FieldItemDto fieldItemDto =
                packageQuestionnaireService.fetchFormlyQuestionnaire(aPackage.id, petitionerApplicant.id, sectionId)

        then:
        assertNotNull(fieldItemDto)
        assertEmployedStatusHistoryAnswers(fieldItemDto)

        cleanup:
        testHelper.clean()
    }

    private void assertEmployedStatusHistoryAnswers(FieldItemDto sectionFieldItemDto) {
        assertNotNull(sectionFieldItemDto)
        assertEquals(sectionFieldItemDto.getFieldId(), 'Sec_employmentHistory')

        List<IFieldGroup> subSectionFieldGroups = sectionFieldItemDto.getFieldGroups()
        assertEquals(1, subSectionFieldGroups.size())

        List<FieldItemDto> subSectionFieldItemDtoList = subSectionFieldGroups.stream()
                .filter({ fieldGroup -> (fieldGroup instanceof FieldItemDto) })
                .map({ fieldGroup -> (FieldItemDto) fieldGroup })
                .collect(Collectors.toList())
        assertEquals(1, subSectionFieldItemDtoList.size())

        FieldItemDto subSectionFieldItemDto = subSectionFieldItemDtoList[0]
        assertEquals(subSectionFieldItemDto.getFieldId(), 'SubSec_employmentStatus')
        assertEquals(6, subSectionFieldItemDto.getFieldGroups().size())

        List<HeaderTextDto> headerTextDtoList = subSectionFieldItemDto.getFieldGroups().stream()
                .filter({ fieldGroup -> (fieldGroup instanceof HeaderTextDto) })
                .map({ fieldGroup -> (HeaderTextDto) fieldGroup })
                .collect(Collectors.toList())
        assertEquals(2, headerTextDtoList.size())
        assertEquals(headerTextDtoList[0].getFieldId(), 'SubSec_employmentStatus')
        assertEquals(headerTextDtoList[1].getFieldId(), 'Q_1001')

        List<RepeatingQuestionGroupDto> repeatingQuestionGroupDtoList = subSectionFieldItemDto.getFieldGroups().stream()
                .filter({ fieldGroup -> (fieldGroup instanceof RepeatingQuestionGroupDto) })
                .map({ fieldGroup -> (RepeatingQuestionGroupDto) fieldGroup })
                .collect(Collectors.toList())
        assertEquals(4, repeatingQuestionGroupDtoList.size())
        assertEmployedStatusRepeatingQuestionGroupDto1(repeatingQuestionGroupDtoList[0])
        assertUnemployedStatusRepeatingQuestionGroupDto(repeatingQuestionGroupDtoList[1])
        assertEmployedStatusRepeatingQuestionGroupDto2(repeatingQuestionGroupDtoList[2])
        assertRetiredStatusRepeatingQuestionGroupDto(repeatingQuestionGroupDtoList[3])
    }


    private void assertEmployedStatusRepeatingQuestionGroupDto1(RepeatingQuestionGroupDto employedRepeatingQuestionGroupDto) {
        assertEquals(employedRepeatingQuestionGroupDto.key, 'employmentStatus-0')
        assertEquals(employedRepeatingQuestionGroupDto.answerIndex, 0)

        RepeatingQuestionGroupDto.RepeatingQuestionInfo repeatingQuestionInfo = employedRepeatingQuestionGroupDto.repeatingQuestionInfo
        assertNotNull(repeatingQuestionInfo)

        List<IFieldGroup> fieldGroups = repeatingQuestionInfo.fieldGroups
        assertNotNull(fieldGroups)
        assertEquals(fieldGroups.size(), 14)
        assertEquals(fieldGroups[0].getFieldId(), 'Q_1007')

        List<QuestionItemDto> questionItemDtoList = fieldGroups.stream()
                .filter({ fieldGroup -> (fieldGroup instanceof QuestionItemDto) })
                .map({ fieldGroup -> (QuestionItemDto) fieldGroup })
                .collect(Collectors.toList())
        assertEquals(13, questionItemDtoList.size())

        QuestionItemDto currentEmploymentStatusDto = questionItemDtoList[0]
        assertEquals(currentEmploymentStatusDto.key, 'currentEmploymentStatus')
        assertNotNull(currentEmploymentStatusDto.templateOption)
        assertEquals('What is your current employment status?', currentEmploymentStatusDto.templateOption.label)
        assertNotNull(currentEmploymentStatusDto.templateOption.attributes)
        assertEquals(currentEmploymentStatusDto.templateOption.attributes['answerIndex'], 0)
        assertEquals(currentEmploymentStatusDto.templateOption.attributes['questionId'], 'Q_1008')

        QuestionItemDto selfEmployedDto = questionItemDtoList[1]
        assertEquals(selfEmployedDto.key, 'selfEmployed')
        assertNotNull(selfEmployedDto.templateOption)
        assertEquals('Are you self-employed?', selfEmployedDto.templateOption.label)
        assertNotNull(selfEmployedDto.templateOption.attributes)
        assertEquals(selfEmployedDto.templateOption.attributes['answerIndex'], 0)
        assertEquals(selfEmployedDto.templateOption.attributes['questionId'], 'Q_1013')

        QuestionItemDto startDateOfCurrentEmploymentDto = questionItemDtoList[2]
        assertEquals(startDateOfCurrentEmploymentDto.key, 'startDateOfCurrentEmployment')
        assertNotNull(startDateOfCurrentEmploymentDto.templateOption)
        assertEquals('What date did you begin working for this employer?', startDateOfCurrentEmploymentDto.templateOption.label)
        assertNotNull(startDateOfCurrentEmploymentDto.templateOption.attributes)
        assertEquals(startDateOfCurrentEmploymentDto.templateOption.attributes['answerIndex'], 0)
        assertEquals(startDateOfCurrentEmploymentDto.templateOption.attributes['questionId'], 'Q_1014')

        QuestionItemDto employerCountryLocationDto = questionItemDtoList[3]
        assertEquals(employerCountryLocationDto.key, 'employerCountryLocation')
        assertNotNull(employerCountryLocationDto.templateOption)
        assertEquals('In what country is this employer located?', employerCountryLocationDto.templateOption.label)
        assertNotNull(employerCountryLocationDto.templateOption.attributes)
        assertEquals(employerCountryLocationDto.templateOption.attributes['answerIndex'], 0)
        assertEquals(employerCountryLocationDto.templateOption.attributes['questionId'], 'Q_1018')

        QuestionItemDto employerFullNameDto = questionItemDtoList[4]
        assertEquals(employerFullNameDto.key, 'employerFullName')
        assertNotNull(employerFullNameDto.templateOption)
        assertEquals('What is the full name of this employer?', employerFullNameDto.templateOption.label)
        assertNotNull(employerFullNameDto.templateOption.attributes)
        assertEquals(employerFullNameDto.templateOption.attributes['answerIndex'], 0)
        assertEquals(employerFullNameDto.templateOption.attributes['questionId'], 'Q_1019')

        QuestionItemDto employerStreetNumberAndNameDto = questionItemDtoList[5]
        assertEquals(employerStreetNumberAndNameDto.key, 'employerStreetNumberAndName')
        assertNotNull(employerStreetNumberAndNameDto.templateOption)
        assertEquals('Street Number and Name', employerStreetNumberAndNameDto.templateOption.label)
        assertNotNull(employerStreetNumberAndNameDto.templateOption.attributes)
        assertEquals(employerStreetNumberAndNameDto.templateOption.attributes['answerIndex'], 0)
        assertEquals(employerStreetNumberAndNameDto.templateOption.attributes['questionId'], 'Q_1020')
        assertEquals(employerStreetNumberAndNameDto.templateOption.placeholder, 'e.g., 123 Main Street')

        QuestionItemDto hasEmployerHavingSecondaryAddressDescriptionDto = questionItemDtoList[6]
        assertEquals(hasEmployerHavingSecondaryAddressDescriptionDto.key, 'hasEmployerHavingSecondaryAddressDescription')
        assertNotNull(hasEmployerHavingSecondaryAddressDescriptionDto.templateOption)
        assertEquals('Does that address have a secondary description (i.e. apartment, suite, or floor)?', hasEmployerHavingSecondaryAddressDescriptionDto.templateOption.label)
        assertNotNull(hasEmployerHavingSecondaryAddressDescriptionDto.templateOption.attributes)
        assertEquals(hasEmployerHavingSecondaryAddressDescriptionDto.templateOption.attributes['answerIndex'], 0)
        assertEquals(hasEmployerHavingSecondaryAddressDescriptionDto.templateOption.attributes['questionId'], 'Q_1021')

        QuestionItemDto employerSecondaryAddressDescriptionDto = questionItemDtoList[7]
        assertEquals(employerSecondaryAddressDescriptionDto.key, 'employerSecondaryAddressDescription')
        assertNotNull(employerSecondaryAddressDescriptionDto.templateOption)
        assertEquals('What is the secondary address description?', employerSecondaryAddressDescriptionDto.templateOption.label)
        assertNotNull(employerSecondaryAddressDescriptionDto.templateOption.attributes)
        assertEquals(employerSecondaryAddressDescriptionDto.templateOption.attributes['answerIndex'], 0)
        assertEquals(employerSecondaryAddressDescriptionDto.templateOption.attributes['questionId'], 'Q_1022')

        QuestionItemDto employerApartmentDto = questionItemDtoList[8]
        assertEquals(employerApartmentDto.key, 'employerApartment')
        assertNotNull(employerApartmentDto.templateOption)
        assertEquals('Apartment/Suite/Floor', employerApartmentDto.templateOption.label)
        assertNotNull(employerApartmentDto.templateOption.attributes)
        assertEquals(employerApartmentDto.templateOption.attributes['answerIndex'], 0)
        assertEquals(employerApartmentDto.templateOption.attributes['questionId'], 'Q_1023')
        assertEquals(employerApartmentDto.templateOption.placeholder, 'i.e. 3B, 18, 2nd')

        QuestionItemDto employerCityDto = questionItemDtoList[9]
        assertEquals(employerCityDto.key, 'employerCity')
        assertNotNull(employerCityDto.templateOption)
        assertEquals('City/Town/Village', employerCityDto.templateOption.label)
        assertNotNull(employerCityDto.templateOption.attributes)
        assertEquals(employerCityDto.templateOption.attributes['answerIndex'], 0)
        assertEquals(employerCityDto.templateOption.attributes['questionId'], 'Q_1024')

        QuestionItemDto employerStateDto = questionItemDtoList[10]
        assertEquals(employerStateDto.key, 'employerState')
        assertNotNull(employerStateDto.templateOption)
        assertEquals('State', employerStateDto.templateOption.label)
        assertNotNull(employerStateDto.templateOption.attributes)
        assertEquals(employerStateDto.templateOption.attributes['answerIndex'], 0)
        assertEquals(employerStateDto.templateOption.attributes['questionId'], 'Q_1025')

        QuestionItemDto employerZipCodeDto = questionItemDtoList[11]
        assertEquals(employerZipCodeDto.key, 'employerZipCode')
        assertNotNull(employerZipCodeDto.templateOption)
        assertEquals('ZIP Code', employerZipCodeDto.templateOption.label)
        assertNotNull(employerZipCodeDto.templateOption.attributes)
        assertEquals(employerZipCodeDto.templateOption.attributes['answerIndex'], 0)
        assertEquals(employerZipCodeDto.templateOption.attributes['questionId'], 'Q_1027')

        QuestionItemDto employmentOccupationDto = questionItemDtoList[12]
        assertEquals(employmentOccupationDto.key, 'employmentOccupation')
        assertNotNull(employmentOccupationDto.templateOption)
        assertEquals('What is your occupation at this employer?', employmentOccupationDto.templateOption.label)
        assertNotNull(employmentOccupationDto.templateOption.attributes)
        assertEquals(employmentOccupationDto.templateOption.attributes['answerIndex'], 0)
        assertEquals(employmentOccupationDto.templateOption.attributes['questionId'], 'Q_1029')
    }

    private void assertUnemployedStatusRepeatingQuestionGroupDto(RepeatingQuestionGroupDto unemployedRepeatingQuestionGroupDto) {
        assertEquals(unemployedRepeatingQuestionGroupDto.key, 'employmentStatus-1')
        assertEquals(unemployedRepeatingQuestionGroupDto.answerIndex, 1)

        RepeatingQuestionGroupDto.RepeatingQuestionInfo repeatingQuestionInfo = unemployedRepeatingQuestionGroupDto.repeatingQuestionInfo
        assertNotNull(repeatingQuestionInfo)

        List<IFieldGroup> fieldGroups = repeatingQuestionInfo.fieldGroups
        assertNotNull(fieldGroups)
        assertEquals(fieldGroups.size(), 4)
        assertEquals(fieldGroups[0].getFieldId(), 'Q_1007')

        List<QuestionItemDto> questionItemDtoList = fieldGroups.stream()
                .filter({ fieldGroup -> (fieldGroup instanceof QuestionItemDto) })
                .map({ fieldGroup -> (QuestionItemDto) fieldGroup })
                .collect(Collectors.toList())
        assertEquals(3, questionItemDtoList.size())

        QuestionItemDto currentEmploymentStatusDto = questionItemDtoList[0]
        assertEquals(currentEmploymentStatusDto.key, 'currentEmploymentStatus')
        assertNotNull(currentEmploymentStatusDto.templateOption)
        assertEquals('What was your previous employment status?', currentEmploymentStatusDto.templateOption.label)
        assertNotNull(currentEmploymentStatusDto.templateOption.attributes)
        assertEquals(currentEmploymentStatusDto.templateOption.attributes['answerIndex'], 1)
        assertEquals(currentEmploymentStatusDto.templateOption.attributes['questionId'], 'Q_1008')

        QuestionItemDto dateBecameUnemployedDto = questionItemDtoList[1]
        assertEquals(dateBecameUnemployedDto.key, 'dateBecameUnemployed')
        assertNotNull(dateBecameUnemployedDto.templateOption)
        assertEquals('What was the date you became unemployed?', dateBecameUnemployedDto.templateOption.label)
        assertNotNull(dateBecameUnemployedDto.templateOption.attributes)
        assertEquals(dateBecameUnemployedDto.templateOption.attributes['answerIndex'], 1)
        assertEquals(dateBecameUnemployedDto.templateOption.attributes['questionId'], 'Q_1009')

        QuestionItemDto lastDateOfUnemploymentDto = questionItemDtoList[2]
        assertEquals(lastDateOfUnemploymentDto.key, 'lastDateOfUnemployment')
        assertNotNull(lastDateOfUnemploymentDto.templateOption)
        assertEquals('What was the last date of your unemployment?', lastDateOfUnemploymentDto.templateOption.label)
        assertNotNull(lastDateOfUnemploymentDto.templateOption.attributes)
        assertEquals(lastDateOfUnemploymentDto.templateOption.attributes['answerIndex'], 1)
        assertEquals(lastDateOfUnemploymentDto.templateOption.attributes['questionId'], 'Q_1010')
    }


    private void assertEmployedStatusRepeatingQuestionGroupDto2(RepeatingQuestionGroupDto employedRepeatingQuestionGroupDto) {
        assertEquals(employedRepeatingQuestionGroupDto.key, 'employmentStatus-2')
        assertEquals(employedRepeatingQuestionGroupDto.answerIndex, 2)

        RepeatingQuestionGroupDto.RepeatingQuestionInfo repeatingQuestionInfo = employedRepeatingQuestionGroupDto.repeatingQuestionInfo
        assertNotNull(repeatingQuestionInfo)

        List<IFieldGroup> fieldGroups = repeatingQuestionInfo.fieldGroups
        assertNotNull(fieldGroups)
        assertEquals(fieldGroups.size(), 13)
        assertEquals(fieldGroups[0].getFieldId(), 'Q_1007')

        List<QuestionItemDto> questionItemDtoList = fieldGroups.stream()
                .filter({ fieldGroup -> (fieldGroup instanceof QuestionItemDto) })
                .map({ fieldGroup -> (QuestionItemDto) fieldGroup })
                .collect(Collectors.toList())
        assertEquals(12, questionItemDtoList.size())

        QuestionItemDto currentEmploymentStatusDto = questionItemDtoList[0]
        assertEquals(currentEmploymentStatusDto.key, 'currentEmploymentStatus')
        assertNotNull(currentEmploymentStatusDto.templateOption)
        assertEquals('What was your previous employment status?', currentEmploymentStatusDto.templateOption.label)
        assertNotNull(currentEmploymentStatusDto.templateOption.attributes)
        assertEquals(currentEmploymentStatusDto.templateOption.attributes['answerIndex'], 2)
        assertEquals(currentEmploymentStatusDto.templateOption.attributes['questionId'], 'Q_1008')

        QuestionItemDto selfEmployedDto = questionItemDtoList[1]
        assertEquals(selfEmployedDto.key, 'selfEmployed')
        assertNotNull(selfEmployedDto.templateOption)
        assertEquals('Were you self-employed?', selfEmployedDto.templateOption.label)
        assertNotNull(selfEmployedDto.templateOption.attributes)
        assertEquals(selfEmployedDto.templateOption.attributes['answerIndex'], 2)
        assertEquals(selfEmployedDto.templateOption.attributes['questionId'], 'Q_1013')

        QuestionItemDto startDateOfCurrentEmploymentDto = questionItemDtoList[2]
        assertEquals(startDateOfCurrentEmploymentDto.key, 'startDateOfCurrentEmployment')
        assertNotNull(startDateOfCurrentEmploymentDto.templateOption)
        assertEquals('What date did you begin working for this employer?', startDateOfCurrentEmploymentDto.templateOption.label)
        assertNotNull(startDateOfCurrentEmploymentDto.templateOption.attributes)
        assertEquals(startDateOfCurrentEmploymentDto.templateOption.attributes['answerIndex'], 2)
        assertEquals(startDateOfCurrentEmploymentDto.templateOption.attributes['questionId'], 'Q_1014')

        QuestionItemDto stillWorkingAtThisEmployerDto = questionItemDtoList[3]
        assertEquals(stillWorkingAtThisEmployerDto.key, 'stillWorkingAtThisEmployer')
        assertNotNull(stillWorkingAtThisEmployerDto.templateOption)
        assertEquals('Are you still working at this employer?', stillWorkingAtThisEmployerDto.templateOption.label)
        assertNotNull(stillWorkingAtThisEmployerDto.templateOption.attributes)
        assertEquals(stillWorkingAtThisEmployerDto.templateOption.attributes['answerIndex'], 2)
        assertEquals(stillWorkingAtThisEmployerDto.templateOption.attributes['questionId'], 'Q_1015')

        QuestionItemDto employerCountryLocationDto = questionItemDtoList[4]
        assertEquals(employerCountryLocationDto.key, 'employerCountryLocation')
        assertNotNull(employerCountryLocationDto.templateOption)
        assertEquals('In what country is this employer located?', employerCountryLocationDto.templateOption.label)
        assertNotNull(employerCountryLocationDto.templateOption.attributes)
        assertEquals(employerCountryLocationDto.templateOption.attributes['answerIndex'], 2)
        assertEquals(employerCountryLocationDto.templateOption.attributes['questionId'], 'Q_1018')

        QuestionItemDto employerFullNameDto = questionItemDtoList[5]
        assertEquals(employerFullNameDto.key, 'employerFullName')
        assertNotNull(employerFullNameDto.templateOption)
        assertEquals('What is the full name of this employer?', employerFullNameDto.templateOption.label)
        assertNotNull(employerFullNameDto.templateOption.attributes)
        assertEquals(employerFullNameDto.templateOption.attributes['answerIndex'], 2)
        assertEquals(employerFullNameDto.templateOption.attributes['questionId'], 'Q_1019')

        QuestionItemDto employerStreetNumberAndNameDto = questionItemDtoList[6]
        assertEquals(employerStreetNumberAndNameDto.key, 'employerStreetNumberAndName')
        assertNotNull(employerStreetNumberAndNameDto.templateOption)
        assertEquals('Street Number and Name', employerStreetNumberAndNameDto.templateOption.label)
        assertNotNull(employerStreetNumberAndNameDto.templateOption.attributes)
        assertEquals(employerStreetNumberAndNameDto.templateOption.attributes['answerIndex'], 2)
        assertEquals(employerStreetNumberAndNameDto.templateOption.attributes['questionId'], 'Q_1020')
        assertEquals(employerStreetNumberAndNameDto.templateOption.placeholder, 'e.g., 123 Main Street')

        QuestionItemDto hasEmployerHavingSecondaryAddressDescriptionDto = questionItemDtoList[7]
        assertEquals(hasEmployerHavingSecondaryAddressDescriptionDto.key, 'hasEmployerHavingSecondaryAddressDescription')
        assertNotNull(hasEmployerHavingSecondaryAddressDescriptionDto.templateOption)
        assertEquals('Does that address have a secondary description (i.e. apartment, suite, or floor)?', hasEmployerHavingSecondaryAddressDescriptionDto.templateOption.label)
        assertNotNull(hasEmployerHavingSecondaryAddressDescriptionDto.templateOption.attributes)
        assertEquals(hasEmployerHavingSecondaryAddressDescriptionDto.templateOption.attributes['answerIndex'], 2)
        assertEquals(hasEmployerHavingSecondaryAddressDescriptionDto.templateOption.attributes['questionId'], 'Q_1021')

        QuestionItemDto employerCityDto = questionItemDtoList[8]
        assertEquals(employerCityDto.key, 'employerCity')
        assertNotNull(employerCityDto.templateOption)
        assertEquals('City/Town/Village', employerCityDto.templateOption.label)
        assertNotNull(employerCityDto.templateOption.attributes)
        assertEquals(employerCityDto.templateOption.attributes['answerIndex'], 2)
        assertEquals(employerCityDto.templateOption.attributes['questionId'], 'Q_1024')

        QuestionItemDto employerTerritoryDto = questionItemDtoList[9]
        assertEquals(employerTerritoryDto.key, 'employerTerritory')
        assertNotNull(employerTerritoryDto.templateOption)
        assertEquals('Province/State/Territory/Prefecture/Parish', employerTerritoryDto.templateOption.label)
        assertNotNull(employerTerritoryDto.templateOption.attributes)
        assertEquals(employerTerritoryDto.templateOption.attributes['answerIndex'], 2)
        assertEquals(employerTerritoryDto.templateOption.attributes['questionId'], 'Q_1026')

        QuestionItemDto employerPostalCodeDto = questionItemDtoList[10]
        assertEquals(employerPostalCodeDto.key, 'employerPostalCode')
        assertNotNull(employerPostalCodeDto.templateOption)
        assertEquals('Postal Code', employerPostalCodeDto.templateOption.label)
        assertNotNull(employerPostalCodeDto.templateOption.attributes)
        assertEquals(employerPostalCodeDto.templateOption.attributes['answerIndex'], 2)
        assertEquals(employerPostalCodeDto.templateOption.attributes['questionId'], 'Q_1028')

        QuestionItemDto employmentOccupationDto = questionItemDtoList[11]
        assertEquals(employmentOccupationDto.key, 'employmentOccupation')
        assertNotNull(employmentOccupationDto.templateOption)
        assertEquals('What was your occupation at this employer?', employmentOccupationDto.templateOption.label)
        assertNotNull(employmentOccupationDto.templateOption.attributes)
        assertEquals(employmentOccupationDto.templateOption.attributes['answerIndex'], 2)
        assertEquals(employmentOccupationDto.templateOption.attributes['questionId'], 'Q_1029')
    }

    private void assertRetiredStatusRepeatingQuestionGroupDto(RepeatingQuestionGroupDto retiredRepeatingQuestionGroupDto) {
        assertEquals(retiredRepeatingQuestionGroupDto.key, 'employmentStatus-3')
        assertEquals(retiredRepeatingQuestionGroupDto.answerIndex, 3)

        RepeatingQuestionGroupDto.RepeatingQuestionInfo repeatingQuestionInfo = retiredRepeatingQuestionGroupDto.repeatingQuestionInfo
        assertNotNull(repeatingQuestionInfo)
        assertNotNull(repeatingQuestionInfo.templateOption)
        assertNotNull(repeatingQuestionInfo.templateOption.attributes)
        assertEquals(repeatingQuestionInfo.templateOption.attributes['answerIndex'], 3)
        assertEquals(repeatingQuestionInfo.templateOption.attributes['showAddButton'],true)
        assertEquals(repeatingQuestionInfo.templateOption.attributes['showRemoveButton'],true)
        assertEquals(repeatingQuestionInfo.templateOption.attributes['addButtonTitle'], 'Add Another Employment Status')

        List<IFieldGroup> fieldGroups = repeatingQuestionInfo.fieldGroups
        assertNotNull(fieldGroups)
        assertEquals(fieldGroups.size(), 4)
        assertEquals(fieldGroups[0].getFieldId(), 'Q_1007')

        List<QuestionItemDto> questionItemDtoList = fieldGroups.stream()
                .filter({ fieldGroup -> (fieldGroup instanceof QuestionItemDto) })
                .map({ fieldGroup -> (QuestionItemDto) fieldGroup })
                .collect(Collectors.toList())
        assertEquals(3, questionItemDtoList.size())

        QuestionItemDto currentEmploymentStatusDto = questionItemDtoList[0]
        assertEquals(currentEmploymentStatusDto.key, 'currentEmploymentStatus')
        assertNotNull(currentEmploymentStatusDto.templateOption)
        assertEquals('What was your previous employment status?', currentEmploymentStatusDto.templateOption.label)
        assertNotNull(currentEmploymentStatusDto.templateOption.attributes)
        assertEquals(currentEmploymentStatusDto.templateOption.attributes['answerIndex'], 3)
        assertEquals(currentEmploymentStatusDto.templateOption.attributes['questionId'], 'Q_1008')

        QuestionItemDto dateOfYourRetirementDto = questionItemDtoList[1]
        assertEquals(dateOfYourRetirementDto.key, 'dateOfYourRetirement')
        assertNotNull(dateOfYourRetirementDto.templateOption)
        assertEquals('What was the date of your retirement?', dateOfYourRetirementDto.templateOption.label)
        assertNotNull(dateOfYourRetirementDto.templateOption.attributes)
        assertEquals(dateOfYourRetirementDto.templateOption.attributes['answerIndex'], 3)
        assertEquals(dateOfYourRetirementDto.templateOption.attributes['questionId'], 'Q_1011')

        QuestionItemDto lastDateOfRetirementDto = questionItemDtoList[2]
        assertEquals(lastDateOfRetirementDto.key, 'lastDateOfRetirement')
        assertNotNull(lastDateOfRetirementDto.templateOption)
        assertEquals('What was the last date of your retirement?', lastDateOfRetirementDto.templateOption.label)
        assertNotNull(lastDateOfRetirementDto.templateOption.attributes)
        assertEquals(lastDateOfRetirementDto.templateOption.attributes['answerIndex'], 3)
        assertEquals(lastDateOfRetirementDto.templateOption.attributes['questionId'], 'Q_1012')
    }
}
