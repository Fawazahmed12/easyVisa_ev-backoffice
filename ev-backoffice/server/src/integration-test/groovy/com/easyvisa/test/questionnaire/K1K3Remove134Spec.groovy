package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.DisplayTextLanguage
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.SectionNodeInstance
import com.easyvisa.questionnaire.model.ApplicantType
import com.easyvisa.questionnaire.services.QuestionnaireService
import com.easyvisa.utils.AnswerListStub
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.TestMockUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import spock.lang.Ignore

import java.time.LocalDate

import static org.junit.Assert.*

@Integration
class K1K3Remove134Spec extends TestMockUtils {

    @Autowired
    private PackageQuestionnaireService packageQuestionnaireService

    @Autowired
    private QuestionnaireService questionnaireService

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

    //FIXME Enable after removing 134
    @Ignore
    void "Test K1K3 does not have #134 Sections"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant

        List I134SpecificSections = ['Sec_incomeHistory', 'Sec_assets', 'Sec_supportAndContributions']

        ImmigrationBenefitCategory directBenefitCategory = aPackage.directBenefit.category
        String benefitCategoryId = directBenefitCategory.getEasyVisaId()

        when:

        List sections = questionnaireService.sectionsByBenefitCategoryAndApplicantType('quest_version_3', benefitCategoryId, ApplicantType.Petitioner.name())

        then:
        // quest_version_2
        // [Sec_1, Sec_2, Sec_addressHistory, Sec_contactInformation, Sec_birthInformation, Sec_biographicInformation, Sec_legalStatusInUS, Sec_incomeHistory, Sec_assets, Sec_employmentHistory, Sec_criminalAndCivilHistory, Sec_familyInformation, Sec_relationshipToPetitioner, Sec_supportAndContributions]
        // quest_version_3
        // [Sec_1, Sec_2, Sec_addressHistory, Sec_contactInformation, Sec_birthInformation, Sec_biographicInformation, Sec_legalStatusInUS, Sec_employmentHistory, Sec_criminalAndCivilHistory, Sec_familyInformation, Sec_relationshipToPetitioner]

        assertNotNull(sections)
        assertTrue(sections.size() == 11)


        // Section List does not contain sections only in #134
        assertFalse(sections*.id?.containsAll(I134SpecificSections))

        when:
        // Map of Sections and Subsections in 129F required for BC_K1K3

        List<String> secIds = sections*.id
        List<Answer> ans = AnswerListStub.answerList(aPackage.id, petitionerApplicant.id)

        // Sec_1 should include only two children used in 129F
        // Previous Immigration, id=SubSec_1, name=previousImmigrationFilledForAnotherPerson
        // Sponsorship Relationship, id=SubSec_4, name="sponsorshipRelationship"

        // Should not include "Previously Submitted Affidavit's of Support", "SubSec_3"
        def secSubSecValidationMap =
                [
                        Sec_1                       : [size: 2, subSec: ['SubSec_1', 'SubSec_4']],
                        Sec_2                       : [size: 2, subSec: ['SubSec_5', 'SubSec_6']],
                        Sec_addressHistory          : [size: 4, subSec: ['SubSec_currentPhysicalAddress', 'SubSec_previousPhysicalAddress', 'SubSec_residedSince18', 'SubSec_currentMailingAddress']],
                        Sec_contactInformation      : [size: 4, subSec: ['SubSec_mobilePhoneNumber', 'SubSec_daytimeAndHomePhoneNumber', 'SubSec_officePhoneNumber', 'SubSec_email']],
                        Sec_birthInformation        : [size: 1, subSec: ['SubSec_birthInformation']],
                        Sec_biographicInformation   : [size: 6, subSec: ['SubSec_ethnicity', 'SubSec_race', 'SubSec_height', 'SubSec_weight', 'SubSec_eyeColor', 'SubSec_hairColor']],
                        Sec_legalStatusInUS         : [size: 2, subSec: ['SubSec_legalStatusInUSndGovtIDNos', 'SubSec_usCitizens']],
                        Sec_employmentHistory       : [size: 1, subSec: ['SubSec_employmentStatus']],
                        Sec_criminalAndCivilHistory : [size: 1, subSec: ['SubSec_criminalCivilConvictions']],
                        Sec_familyInformation       : [size: 5, subSec: ['SubSec_maritalStatus', 'SubSec_priorSpouses', 'SubSec_parent1', 'SubSec_parent2', 'SubSec_childrenInformation']],
                        Sec_relationshipToPetitioner: [size: 1, subSec: ['SubSec_fianceQuestions']]
                ]

        def subSecQuestValidationMap = [
                SubSec_1                         : [size: 1, qid: ['Q_1'], qname: ['everFilledPetition']],
                SubSec_4                         : [size: 1, qid: ['Q_27'], qname: ['beneficiaryRelatedToYou']],
                SubSec_5                         : [size: 3, qid: ['Q_32', 'Q_33', 'Q_34'], qname: ['firstName', 'middleName', 'familyName']],
                SubSec_6                         : [size: 2, qid: ['Q_6001', 'Q_35'], qname: ['otherNamesUsedLabel', 'firstMiddleLast']],
                SubSec_currentPhysicalAddress    : [size: 6, qid: ['Q_42', 'Q_43', 'Q_44', 'Q_47', 'Q_52', 'Q_6000'], qname: ['currentPhysicalAddress', 'streetNumberAndName', 'addressHaveASecondaryDescription', 'cityTownVillage', 'moveIntoThisAddress', 'moveOutOfThisAddress']],
                SubSec_previousPhysicalAddress   : [size: 0, qid: [], qname: []],
                SubSec_residedSince18            : [size: 2, qid: ['Q_6007', 'Q_6008'], qname: ['everLivedInUSSince18', 'everLivedInForeignSince18']],
                SubSec_currentMailingAddress     : [size: 2, qid: ['Q_68', 'Q_69'], qname: ['careOfName', 'currentMailingAddress']],
                SubSec_mobilePhoneNumber         : [size: 2, qid: ['Q_80', 'Q_81'], qname: ['country', 'phoneNumber']],
                SubSec_daytimeAndHomePhoneNumber : [size: 2, qid: ['Q_82', 'Q_83'], qname: ['country', 'phoneNumber']],
                SubSec_officePhoneNumber         : [size: 2, qid: ['Q_84', 'Q_85'], qname: ['country', 'phoneNumber']],
                SubSec_email                     : [size: 1, qid: ['Q_86'], qname: ['emailAddress']],
                SubSec_birthInformation          : [size: 4, qid: ['Q_87', 'Q_88', 'Q_89', 'Q_90'], qname: ['gender', 'dateofBirth', 'countryofBirth', 'cityTownVillage']],
                SubSec_ethnicity                 : [size: 1, qid: ['Q_95'], qname: ['ethnicity']],
                SubSec_race                      : [size: 5, qid: ['Q_96', 'Q_97', 'Q_98', 'Q_99', 'Q_100'], qname: ['white', 'asian', 'blackOrAfricanAmerican', 'americanIndianOrAlaskaNative', 'nativeHawaiianOrOtherPacificIslander']],
                SubSec_height                    : [size: 1, qid: ['Q_101'], qname: ['unitsMeasure']],
                SubSec_weight                    : [size: 1, qid: ['Q_105'], qname: ['unitsMeasure']],
                SubSec_eyeColor                  : [size: 1, qid: ['Q_107'], qname: ['eyeColor']],
                SubSec_hairColor                 : [size: 1, qid: ['Q_108'], qname: ['hairColor']],
                SubSec_legalStatusInUSndGovtIDNos: [size: 4, qid: ['Q_109', 'Q_6099', 'Q_6097', 'Q_114'], qname: ['legalStatusInUS1', 'everHadAlienNumber', 'doesThisPersonHaveSSNPetitioner', 'doYouHaveUSCISELISAccountNo']],
                SubSec_usCitizens                : [size: 1, qid: ['Q_120'], qname: ['howCitizenshipAcquired']],
                SubSec_employmentStatus          : [size: 1, qid: ['Q_1001'], qname: ['employmentStatusInfoLabel']],
                SubSec_criminalCivilConvictions  : [size: 11, qid: ['Q_6005', 'Q_1115', 'Q_1119', 'Q_1124', 'Q_1130', 'Q_1139', 'Q_1148', 'Q_1149', 'Q_1150', 'Q_1151', 'Q_1152'], qname: ['criminalCivilConvictionsLabel', 'arrestedCitedChargedIndicted', 'arrestedOrConvictedForDomesticViolence', 'temporaryPermanentProtection', 'arrestedConvictedHomicideEtc', 'arrestedConvictedThreeOrMoreTimes', 'requestingWaivers', 'multipleFilerNoPermanentRestrainingOrders', 'multipleFilerPriorPermanentRestrainingOrders', 'multipleFilerWithAPriorPermanentRestrainingOrder', 'mySpouseOrIAmNotAMultipleFiler']],
                SubSec_maritalStatus             : [size: 2, qid: ['Q_1201', 'Q_1204'], qname: ['everBeenMarried', 'currentMaritalStatus']],
                SubSec_priorSpouses              : [size: 0, qid: [], qname: []],
                SubSec_parent1                   : [size: 9, qid: ['Q_6006', 'Q_1230', 'Q_1231', 'Q_1232', 'Q_1233', 'Q_1234', 'Q_1236', 'Q_1237', 'Q_1238'], qname: ['parent1Label', 'parent1FirstName', 'parent1MiddleName', 'parent1LastName', 'parent1Gender', 'parent1CountryOfBirth', 'parent1DateOfBirth', 'parent1CityOrTownOrVillageOfResidence', 'parent1CountryOfResidence']],
                SubSec_parent2                   : [size: 8, qid: ['Q_1240', 'Q_1241', 'Q_1242', 'Q_1243', 'Q_1244', 'Q_1246', 'Q_1247', 'Q_1248'], qname: ['parent2FirstName', 'parent2MiddleName', 'parent2LastName', 'parent2Gender', 'parent2CountryOfBirth', 'parent2DateOfBirth', 'parent2CityOrTownOrVillageOfResidence', 'parent2CountryOfResidence']],
                SubSec_childrenInformation       : [size: 1, qid: ['Q_1250'], qname: ['doYouHaveChildrenUnder18Years']],
                SubSec_fianceQuestions           : [size: 10, qid: ['Q_1351', 'Q_1352', 'Q_1353', 'Q_1354', 'Q_1355', 'Q_1361', 'Q_1362', 'Q_1363', 'Q_1366', 'Q_1371'], qname: ['fianceRelated', 'degreeOfRelationship', 'metInPersonDuringTwoYears', 'circumstancesOfYourPersonMeeting', 'meetFianceServices', 'countryIMBLocated', 'fianceQuestionStreetNumberName', 'fianceQuestionAddressSecondaryDescription', 'fianceQuestionCityTownVillage', 'fianceQuestionDaytimeTelephoneNumber']]

        ]

        then:
        SectionNodeInstance sni
        secIds.each { sectionId ->
            sni = questionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, petitionerApplicant.id, sectionId, ans, benefitCategoryId, DisplayTextLanguage.EN, LocalDate.now())

            assertEquals(secSubSecValidationMap[sectionId]['size'], sni.children.size())
            assertTrue(secSubSecValidationMap[sectionId]['subSec'].containsAll(sni.children*.id))

            // assert questions in each subsection
            List<EasyVisaNodeInstance> subSectionList = sni.children
            subSectionList.each { subsection ->


                assertEquals("${subsection.id} size does not match", subSecQuestValidationMap[subsection.id]['size'], subsection.children.size())
                assertTrue("${subsection.id} Question Id List does not match", subSecQuestValidationMap[subsection.id]['qid']?.containsAll(subsection.children*.id))
                assertTrue("${subsection.id} Question Name List does not match", subSecQuestValidationMap[subsection.id]['qname']?.containsAll(subsection.children*.name))

            }
        }

        cleanup:
        testHelper.clean()

    }

}
