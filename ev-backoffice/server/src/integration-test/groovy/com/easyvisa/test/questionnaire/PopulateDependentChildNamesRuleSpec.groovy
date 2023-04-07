package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.*
import com.easyvisa.questionnaire.meta.InputSourceType
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.AnswerListStub
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.QuestionnaireTestDBSetupUtility
import com.easyvisa.utils.TestMockUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import spock.lang.Unroll
import org.junit.Ignore
import java.time.LocalDate
import java.time.Month
import java.util.stream.Collectors
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNull
import static org.junit.Assert.assertTrue

@Integration
@Ignore
// As of now we are removing Form_134.
class PopulateDependentChildNamesRuleSpec extends TestMockUtils {

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

    // TODO This test case randomly fails. This might be due to the way the test cases are using test packages (applicant package).
    // TODO Grails does some thing concurrently for performance reason. Need to be visited. Ashraf
    @Ignore
    void testPopulateDependentChildNamesRule() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListStub.populateDependentChildNamesRuleAnswerList(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        SectionNodeInstance familyInformationSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id,
                        petitionerApplicant.id, sectionId, answerList)

        then:
        assertNotNull(familyInformationSectionInstance)
        assertFamilyInformationSectionAnswers(familyInformationSectionInstance)

        cleanup:
        testHelper.clean()
    }

    @Unroll
    @Ignore
    void "test autoPopulateChildSelection Rule #scenario"() {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant


        when:

        List<Answer> answerList = AnswerListStub.autoPopulateDependentChildNamesRuleAnswerList(aPackage.id, petitionerApplicant.id, child1, child2, selection, dependant, manualAdd)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)


        then:

        List answers = answerService.fetchAnswers(aPackage.id, petitionerApplicant.id, [sectionId])

        SectionNodeInstance familyInformationSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id,
                        petitionerApplicant.id, sectionId, answers)


        assertNotNull(familyInformationSectionInstance)
        assertChildAndDependentSectionAnswers(familyInformationSectionInstance, dependant, selection, countChild)

        cleanup:
        testHelper.clean()

        where:
        scenario                        | child1                                      | child2                                | selection  | dependant                                          | countChild | manualAdd
        "Select First Child"            | ["Kane", "mid", "Williamson", "2016-07-24"] | ["Gary", "W", "Sobers", "2014-02-24"] | child1[0]  | child1                                             | 2          | false
        "Select Second Child"           | ["Kane", "mid", "Williamson", "2016-07-24"] | ["Gary", "W", "Sobers", "2014-02-24"] | child2[0]  | child2                                             | 2          | false
        "Select --None--"               | ["Kane", "mid", "Williamson", "2016-07-24"] | ["Gary", "W", "Sobers", "2014-02-24"] | "--None--" | [null, null, null, null]                           | 2          | false
        "Add Manual"                    | ["Kane", "mid", "Williamson", "2016-07-24"] | ["Gary", "W", "Sobers", "2014-02-24"] | "--None--" | ["ManualAdd", "MM", "ManualLast", "2015-02-24"]    | 2          | true
        "No existing child, Add Manual" | []                                          | []                                    | "--None--" | ["ManualAdd1", "MM1", "ManualLast1", "2015-02-24"] | 0          | true

    }


    private void assertChildAndDependentSectionAnswers(SectionNodeInstance familyInformationSectionInstance, List dependantToValidate, String selection, int countChild) {

        List<EasyVisaNodeInstance> familyInformationSectionInstanceList = familyInformationSectionInstance.getChildren()
        List<EasyVisaNodeInstance> visibleFamilyInformationSectionInstanceList = familyInformationSectionInstanceList.findAll { it.isVisibility() }

        //Get dependentsChildren Subsection
        SubSectionNodeInstance dependentsChildrenInstance = (SubSectionNodeInstance) visibleFamilyInformationSectionInstanceList.get(4)
       assertEquals('SubSec_dependentsChildren', dependentsChildrenInstance.getId())

        // DependentsChildren -> Children
        List<EasyVisaNodeInstance> subsectionChildren = dependentsChildrenInstance.getChildren()
        assertEquals(1, subsectionChildren.size())

        // anyDependentsWhoAreChildren
        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.findAll { ((it instanceof QuestionNodeInstance) && it.isVisibility()) }
        assertEquals(1, questionNodeInstanceList.size())
        QuestionNodeInstance anyDependentsWhoAreChildrenQuestion = questionNodeInstanceList.get(0)
        assertEquals('anyDependentsWhoAreChildren', anyDependentsWhoAreChildrenQuestion.getName())
        Answer anyDependentsWhoAreChildrenAnswer = anyDependentsWhoAreChildrenQuestion.getAnswer()
        assertEquals('Yes', anyDependentsWhoAreChildrenAnswer.getValue())

        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1268', anyDependentsWhoAreChildrenAnswer.getPath())

        assertEquals(2, anyDependentsWhoAreChildrenQuestion.getChildren().size())

        // Get RepeatingGroupInstance for dependentsChildren
        List<RepeatingQuestionGroupNodeInstance> repeatingGroupInstanceList = anyDependentsWhoAreChildrenQuestion.getChildren()?.findAll {
            it instanceof RepeatingQuestionGroupNodeInstance
        }
        assertEquals(1, repeatingGroupInstanceList.size())

        RepeatingQuestionGroupNodeInstance childInfoRepeatingGroupInstance = repeatingGroupInstanceList.get(0)

        assertEquals('RQG_dependentsChildren', childInfoRepeatingGroupInstance.getId())
        List<EasyVisaNodeInstance> repeatingGroupChildren = childInfoRepeatingGroupInstance.getChildren()
        assertEquals(7, repeatingGroupChildren.size())

        List<QuestionNodeInstance> child1QuestionNodeInstanceList = repeatingGroupChildren.findAll { (it instanceof QuestionNodeInstance) && it.isVisibility() }

        assertEquals(7, child1QuestionNodeInstanceList.size())


        QuestionNodeInstance selectChildToAutoFillDataQuestion = child1QuestionNodeInstanceList.get(0)
        assertEquals('selectChildToAutoFillData', selectChildToAutoFillDataQuestion.getName())
        Answer selectChildToAutoFillDataAnswer = selectChildToAutoFillDataQuestion.getAnswer()
        assertEquals(selection, selectChildToAutoFillDataAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1269/0', selectChildToAutoFillDataAnswer.getPath())

        InputSourceType selectChildToAutoFillDataInputSourceType = selectChildToAutoFillDataQuestion.getInputSourceType()
        assertEquals('PopulateDependentChildNamesRule', selectChildToAutoFillDataInputSourceType.getType())
        List<InputSourceType.ValueMap> inputSourceTypeValues = selectChildToAutoFillDataInputSourceType.values
        assertNotNull(inputSourceTypeValues)

        assertEquals(countChild + 1, inputSourceTypeValues.size())
        assertEquals("--None--", inputSourceTypeValues[0].value)


        QuestionNodeInstance dependentChildFirstNameQuestion = child1QuestionNodeInstanceList.get(1)
        assertEquals('dependentChildFirstName', dependentChildFirstNameQuestion.getName())
        Answer dependentChildFirstNameAnswer = dependentChildFirstNameQuestion.getAnswer()
        assertEquals(dependantToValidate[0], dependentChildFirstNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1270/0', dependentChildFirstNameAnswer.getPath())

        QuestionNodeInstance dependentChildMiddleNameQuestion = child1QuestionNodeInstanceList.get(2)
        assertEquals('dependentChildMiddleName', dependentChildMiddleNameQuestion.getName())
        Answer dependentChildMiddleNameAnswer = dependentChildMiddleNameQuestion.getAnswer()
        assertEquals(dependantToValidate[1], dependentChildMiddleNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1271/0', dependentChildMiddleNameAnswer.getPath())

        QuestionNodeInstance dependentChildLastNameQuestion = child1QuestionNodeInstanceList.get(3)
        assertEquals('dependentChildLastName', dependentChildLastNameQuestion.getName())
        Answer dependentChildLastNameAnswer = dependentChildLastNameQuestion.getAnswer()
        assertEquals(dependantToValidate[2], dependentChildLastNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1272/0', dependentChildLastNameAnswer.getPath())

        QuestionNodeInstance dependentChildDateOfBirthQuestion = child1QuestionNodeInstanceList.get(4)
        assertEquals('dependentChildDateOfBirth', dependentChildDateOfBirthQuestion.getName())
        Answer dependentChildDateOfBirthAnswer = dependentChildDateOfBirthQuestion.getAnswer()
        String dt
        if (dependantToValidate[3]) {
            dt = dependantToValidate[3] ? DateUtil.normalizeEasyVisaDateFormat(dependantToValidate[3]) : null
        }
        assertEquals(dt, dependentChildDateOfBirthAnswer.getValue())


        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1276/0', dependentChildDateOfBirthAnswer.getPath())

    }


    private void assertFamilyInformationSectionAnswers(SectionNodeInstance familyInformationSectionInstance) {
        List<EasyVisaNodeInstance> familyInformationSectionInstanceList = familyInformationSectionInstance.getChildren()
        assertEquals(8, familyInformationSectionInstanceList.size())

        List<EasyVisaNodeInstance> validFamilyInformationSectionInstanceList = familyInformationSectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())
        assertEquals(8, validFamilyInformationSectionInstanceList.size())
        assertFamilyInformationSectionInstanceAnswers(validFamilyInformationSectionInstanceList)
    }


    private void assertFamilyInformationSectionInstanceAnswers(List<EasyVisaNodeInstance> familyInformationSectionInstanceList) {
        assertMaritalStatusSubSectionInstanceAnswers(familyInformationSectionInstanceList)
        assertCurrentSpouseSubSectionInstanceAnswers(familyInformationSectionInstanceList)
        assertPriorSpousesSubSectionInstanceAnswers(familyInformationSectionInstanceList)
        assertChildrenInformationSubSectionInstanceAnswers(familyInformationSectionInstanceList)
        assertDependentsChildrenSubSectionInstanceAnswers(familyInformationSectionInstanceList)
    }


    private void assertMaritalStatusSubSectionInstanceAnswers(List<EasyVisaNodeInstance> familyInformationSectionInstanceList) {
        SubSectionNodeInstance maritalStatusInstance = (SubSectionNodeInstance) familyInformationSectionInstanceList.get(0)
        assertEquals('SubSec_maritalStatus', maritalStatusInstance.getId())
        List<EasyVisaNodeInstance> subsectionChildren = maritalStatusInstance.getChildren()
        assertEquals(3, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(3, questionNodeInstanceList.size())

        QuestionNodeInstance everBeenMarriedQuestion = questionNodeInstanceList.get(0)
        assertEquals('everBeenMarried', everBeenMarriedQuestion.getName())
        Answer everBeenMarriedAnswer = everBeenMarriedQuestion.getAnswer()
        assertEquals('Yes', everBeenMarriedAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_maritalStatus/Q_1201', everBeenMarriedAnswer.getPath())

        QuestionNodeInstance howManyTimesHaveYouBeenMarriedQuestion = questionNodeInstanceList.get(1)
        assertEquals('howManyTimesHaveYouBeenMarried', howManyTimesHaveYouBeenMarriedQuestion.getName())
        Answer howManyTimesHaveYouBeenMarriedAnswer = howManyTimesHaveYouBeenMarriedQuestion.getAnswer()
        assertEquals('2', howManyTimesHaveYouBeenMarriedAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_maritalStatus/Q_1202', howManyTimesHaveYouBeenMarriedAnswer.getPath())

        QuestionNodeInstance currentMaritalStatusQuestion = questionNodeInstanceList.get(2)
        assertEquals('currentMaritalStatus', currentMaritalStatusQuestion.getName())
        Answer currentMaritalStatusAnswer = currentMaritalStatusQuestion.getAnswer()
        assertEquals('Married', currentMaritalStatusAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_maritalStatus/Q_1204', currentMaritalStatusAnswer.getPath())

        /////////

        assertEquals(1, everBeenMarriedQuestion.getChildren().size())
        List<QuestionNodeInstance> everBeenMarriedQuestionInstanceList = everBeenMarriedQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, everBeenMarriedQuestionInstanceList.size())

        QuestionNodeInstance wasRecentMarriageAnulledQuestion = everBeenMarriedQuestionInstanceList.get(0)
        assertEquals('wasRecentMarriageAnulled', wasRecentMarriageAnulledQuestion.getName())
        Answer wasRecentMarriageAnulledAnswer = wasRecentMarriageAnulledQuestion.getAnswer()
        assertEquals('No', wasRecentMarriageAnulledAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_maritalStatus/Q_1203', wasRecentMarriageAnulledAnswer.getPath())
    }


    private void assertCurrentSpouseSubSectionInstanceAnswers(List<EasyVisaNodeInstance> familyInformationSectionInstanceList) {
        SubSectionNodeInstance currentSpouseInstance = (SubSectionNodeInstance) familyInformationSectionInstanceList.get(1)
        assertEquals('SubSec_currentSpouse', currentSpouseInstance.getId())
        List<EasyVisaNodeInstance> subsectionChildren = currentSpouseInstance.getChildren()
        assertEquals(9, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(9, questionNodeInstanceList.size())

        QuestionNodeInstance currentSpouseFirstNameQuestion = questionNodeInstanceList.get(0)
        assertEquals('currentSpouseFirstName', currentSpouseFirstNameQuestion.getName())
        Answer currentSpouseFirstNameAnswer = currentSpouseFirstNameQuestion.getAnswer()
        assertEquals('Trevor', currentSpouseFirstNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_currentSpouse/Q_1206', currentSpouseFirstNameAnswer.getPath())

        QuestionNodeInstance currentSpouseMiddleNameQuestion = questionNodeInstanceList.get(1)
        assertEquals('currentSpouseMiddleName', currentSpouseMiddleNameQuestion.getName())
        Answer currentSpouseMiddleNameAnswer = currentSpouseMiddleNameQuestion.getAnswer()
        assertEquals('Bayliss', currentSpouseMiddleNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_currentSpouse/Q_1207', currentSpouseMiddleNameAnswer.getPath())

        QuestionNodeInstance currentSpouseLastNameQuestion = questionNodeInstanceList.get(2)
        assertEquals('currentSpouseLastName', currentSpouseLastNameQuestion.getName())
        Answer currentSpouseLastNameAnswer = currentSpouseLastNameQuestion.getAnswer()
        assertEquals('John', currentSpouseLastNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_currentSpouse/Q_1208', currentSpouseLastNameAnswer.getPath())

        QuestionNodeInstance currentSpouseDateOfMarriageQuestion = questionNodeInstanceList.get(3)
        assertEquals('currentSpouseDateOfMarriage', currentSpouseDateOfMarriageQuestion.getName())
        Answer currentSpouseDateOfMarriageAnswer = currentSpouseDateOfMarriageQuestion.getAnswer()
        assertEquals(DateUtil.fromDate(LocalDate.of(2016, Month.JANUARY, 17)), currentSpouseDateOfMarriageAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_currentSpouse/Q_1209', currentSpouseDateOfMarriageAnswer.getPath())

        QuestionNodeInstance whatCountryDidThisMarriageQuestion = questionNodeInstanceList.get(4)
        assertEquals('whatCountryDidThisMarriage', whatCountryDidThisMarriageQuestion.getName())
        Answer whatCountryDidThisMarriageAnswer = whatCountryDidThisMarriageQuestion.getAnswer()
        assertEquals('US', whatCountryDidThisMarriageAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_currentSpouse/Q_1210', whatCountryDidThisMarriageAnswer.getPath())

        QuestionNodeInstance currentSpouseStreetNoAndNameQuestion = questionNodeInstanceList.get(5)
        assertEquals('currentSpouseStreetNoAndName', currentSpouseStreetNoAndNameQuestion.getName())
        Answer currentSpouseStreetNoAndNameAnswer = currentSpouseStreetNoAndNameQuestion.getAnswer()
        assertEquals('240 Hayat Regency', currentSpouseStreetNoAndNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_currentSpouse/Q_1211', currentSpouseStreetNoAndNameAnswer.getPath())

        QuestionNodeInstance currentSpouseAddressHasSecondaryDecsriptionQuestion = questionNodeInstanceList.get(6)
        assertEquals('currentSpouseAddressHasSecondaryDecsription', currentSpouseAddressHasSecondaryDecsriptionQuestion.getName())
        Answer currentSpouseAddressHasSecondaryDecsriptionAnswer = currentSpouseAddressHasSecondaryDecsriptionQuestion.getAnswer()
        assertEquals('No', currentSpouseAddressHasSecondaryDecsriptionAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_currentSpouse/Q_1212', currentSpouseAddressHasSecondaryDecsriptionAnswer.getPath())

        QuestionNodeInstance currentSpouseCityOrTownOrVillageQuestion = questionNodeInstanceList.get(7)
        assertEquals('currentSpouseCityOrTownOrVillage', currentSpouseCityOrTownOrVillageQuestion.getName())
        Answer currentSpouseCityOrTownOrVillageAnswer = currentSpouseCityOrTownOrVillageQuestion.getAnswer()
        assertEquals('San Francisco Bay Area', currentSpouseCityOrTownOrVillageAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_currentSpouse/Q_1215', currentSpouseCityOrTownOrVillageAnswer.getPath())

        QuestionNodeInstance wereYouMarriedPreviouslyQuestion = questionNodeInstanceList.get(8)
        assertEquals('wereYouMarriedPreviously', wereYouMarriedPreviouslyQuestion.getName())
        Answer wereYouMarriedPreviouslyAnswer = wereYouMarriedPreviouslyQuestion.getAnswer()
        assertEquals('Yes', wereYouMarriedPreviouslyAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_currentSpouse/Q_1220', wereYouMarriedPreviouslyAnswer.getPath())


        assertEquals(2, currentSpouseAddressHasSecondaryDecsriptionQuestion.getChildren().size())
        List<QuestionNodeInstance> currentSpouseAddressHasSecondaryDecsriptionQuestionInstanceList = currentSpouseAddressHasSecondaryDecsriptionQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(0, currentSpouseAddressHasSecondaryDecsriptionQuestionInstanceList.size())


        assertEquals(4, whatCountryDidThisMarriageQuestion.getChildren().size())
        List<QuestionNodeInstance> whatCountryDidThisMarriageQuestionInstanceList = whatCountryDidThisMarriageQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, whatCountryDidThisMarriageQuestionInstanceList.size())


        QuestionNodeInstance currentSpouseStateQuestion = whatCountryDidThisMarriageQuestionInstanceList.get(0)
        assertEquals('currentSpouseState', currentSpouseStateQuestion.getName())
        Answer currentSpouseStateAnswer = currentSpouseStateQuestion.getAnswer()
        assertEquals('California', currentSpouseStateAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_currentSpouse/Q_1216', currentSpouseStateAnswer.getPath())

        QuestionNodeInstance currentSpouseZipCodeQuestion = whatCountryDidThisMarriageQuestionInstanceList.get(1)
        assertEquals('currentSpouseZipCode', currentSpouseZipCodeQuestion.getName())
        Answer currentSpouseZipCodeAnswer = currentSpouseZipCodeQuestion.getAnswer()
        assertEquals('629001', currentSpouseZipCodeAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_currentSpouse/Q_1218', currentSpouseZipCodeAnswer.getPath())
    }


    private void assertPriorSpousesSubSectionInstanceAnswers(List<EasyVisaNodeInstance> familyInformationSectionInstanceList) {
        SubSectionNodeInstance priorSpousesInstance = (SubSectionNodeInstance) familyInformationSectionInstanceList.get(2)
        assertEquals('SubSec_priorSpouses', priorSpousesInstance.getId())
        List<EasyVisaNodeInstance> subsectionChildren = priorSpousesInstance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<RepeatingQuestionGroupNodeInstance> repeatingGroupInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof RepeatingQuestionGroupNodeInstance) })
                .map({ nodeInstance -> (RepeatingQuestionGroupNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, repeatingGroupInstanceList.size())

        RepeatingQuestionGroupNodeInstance priorSpouseRepeatingGroup = repeatingGroupInstanceList.get(0)
        assertEquals('RQG_priorSpouses', priorSpouseRepeatingGroup.getId())
        List<EasyVisaNodeInstance> repeatingGroupChildren = priorSpouseRepeatingGroup.getChildren()
        assertEquals(6, repeatingGroupChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = repeatingGroupChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(6, questionNodeInstanceList.size())


        QuestionNodeInstance priorSpouseFirstNameQuestion = questionNodeInstanceList.get(0)
        assertEquals('priorSpouseFirstName', priorSpouseFirstNameQuestion.getName())
        Answer priorSpouseFirstNameAnswer = priorSpouseFirstNameQuestion.getAnswer()
        assertEquals('Albert', priorSpouseFirstNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_priorSpouses/Q_1222/0', priorSpouseFirstNameAnswer.getPath())

        QuestionNodeInstance priorSpouseMiddleNameQuestion = questionNodeInstanceList.get(1)
        assertEquals('priorSpouseMiddleName', priorSpouseMiddleNameQuestion.getName())
        Answer priorSpouseMiddleNameAnswer = priorSpouseMiddleNameQuestion.getAnswer()
        assertEquals('Thomson', priorSpouseMiddleNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_priorSpouses/Q_1223/0', priorSpouseMiddleNameAnswer.getPath())

        QuestionNodeInstance priorSpouseLastNameQuestion = questionNodeInstanceList.get(2)
        assertEquals('priorSpouseLastName', priorSpouseLastNameQuestion.getName())
        Answer priorSpouseLastNameAnswer = priorSpouseLastNameQuestion.getAnswer()
        assertEquals('Joel', priorSpouseLastNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_priorSpouses/Q_1224/0', priorSpouseLastNameAnswer.getPath())

        QuestionNodeInstance howDisThisMarriageEndQuestion = questionNodeInstanceList.get(3)
        assertEquals('howDisThisMarriageEnd', howDisThisMarriageEndQuestion.getName())
        Answer howDisThisMarriageEndAnswer = howDisThisMarriageEndQuestion.getAnswer()
        assertEquals('Death', howDisThisMarriageEndAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_priorSpouses/Q_1225/0', howDisThisMarriageEndAnswer.getPath())

        QuestionNodeInstance priorSpouseDateOfMarriageQuestion = questionNodeInstanceList.get(4)
        assertEquals('priorSpouseDateOfMarriage', priorSpouseDateOfMarriageQuestion.getName())
        Answer priorSpouseDateOfMarriageAnswer = priorSpouseDateOfMarriageQuestion.getAnswer()
        assertEquals(DateUtil.fromDate(LocalDate.of(2008, Month.MAY, 5)), priorSpouseDateOfMarriageAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_priorSpouses/Q_1227/0', priorSpouseDateOfMarriageAnswer.getPath())

        QuestionNodeInstance priorSpouseEndDateOfMarriageQuestion = questionNodeInstanceList.get(5)
        assertEquals('priorSpouseEndDateOfMarriage', priorSpouseEndDateOfMarriageQuestion.getName())
        Answer priorSpouseEndDateOfMarriageAnswer = priorSpouseEndDateOfMarriageQuestion.getAnswer()
        assertEquals('', priorSpouseEndDateOfMarriageAnswer.getValue())
        //TODO... rule will autostore the value of q298 to q300, if answer for the question q297 is  'Death'
        //assertEquals(DateUtil.fromDate(LocalDate.of(2015, Month.FEBRUARY, 11)), priorSpouseEndDateOfMarriageAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_priorSpouses/Q_1228/0', priorSpouseEndDateOfMarriageAnswer.getPath())


        assertEquals(1, howDisThisMarriageEndQuestion.getChildren().size())
        List<QuestionNodeInstance> howDisThisMarriageEndQuestionInstanceList = howDisThisMarriageEndQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, howDisThisMarriageEndQuestionInstanceList.size())

        QuestionNodeInstance priorSpouseDateOfSpouseDeathQuestion = howDisThisMarriageEndQuestionInstanceList.get(0)
        assertEquals('priorSpouseDateOfSpouseDeath', priorSpouseDateOfSpouseDeathQuestion.getName())
        Answer priorSpouseDateOfSpouseDeathAnswer = priorSpouseDateOfSpouseDeathQuestion.getAnswer()
        assertEquals(DateUtil.fromDate(LocalDate.of(2015, Month.FEBRUARY, 11)), priorSpouseDateOfSpouseDeathAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_priorSpouses/Q_1226/0', priorSpouseDateOfSpouseDeathAnswer.getPath())
    }


    private void assertChildrenInformationSubSectionInstanceAnswers(List<EasyVisaNodeInstance> familyInformationSectionInstanceList) {
        SubSectionNodeInstance childrenInformationInstance = (SubSectionNodeInstance) familyInformationSectionInstanceList.get(5)
        assertEquals('SubSec_childrenInformation', childrenInformationInstance.getId())
        List<EasyVisaNodeInstance> subsectionChildren = childrenInformationInstance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())

        QuestionNodeInstance doYouHaveChildrenUnder18YearsQuestion = questionNodeInstanceList.get(0)
        assertEquals('doYouHaveChildrenUnder18Years', doYouHaveChildrenUnder18YearsQuestion.getName())
        Answer doYouHaveChildrenUnder18YearsAnswer = doYouHaveChildrenUnder18YearsQuestion.getAnswer()
        assertEquals('Yes', doYouHaveChildrenUnder18YearsAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1250', doYouHaveChildrenUnder18YearsAnswer.getPath())


        assertEquals(2, doYouHaveChildrenUnder18YearsQuestion.getChildren().size())
        List<RepeatingQuestionGroupNodeInstance> repeatingGroupInstanceList = doYouHaveChildrenUnder18YearsQuestion.getChildren().stream()
                .filter({ nodeInstance -> (nodeInstance instanceof RepeatingQuestionGroupNodeInstance) })
                .map({ nodeInstance -> (RepeatingQuestionGroupNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, repeatingGroupInstanceList.size())

        RepeatingQuestionGroupNodeInstance childInfoRepeatingGroupInstance1 = repeatingGroupInstanceList.get(0)
        assertChildInfoRepeatingGroupInstance1Answers(childInfoRepeatingGroupInstance1)

        RepeatingQuestionGroupNodeInstance childInfoRepeatingGroupInstance2 = repeatingGroupInstanceList.get(1)
        assertChildInfoRepeatingGroupInstance2Answers(childInfoRepeatingGroupInstance2)
    }


    private void assertChildInfoRepeatingGroupInstance1Answers(RepeatingQuestionGroupNodeInstance childInfoRepeatingGroupInstance1) {
        assertEquals('RQG_childrenInformation', childInfoRepeatingGroupInstance1.getId())
        List<EasyVisaNodeInstance> repeatingGroup1Children = childInfoRepeatingGroupInstance1.getChildren()
        assertEquals(10, repeatingGroup1Children.size())

        List<QuestionNodeInstance> child1QuestionNodeInstanceList = repeatingGroup1Children.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(10, child1QuestionNodeInstanceList.size())

        QuestionNodeInstance childrenInfoFirstNameQuestion = child1QuestionNodeInstanceList.get(0)
        assertEquals('childrenInfoFirstName', childrenInfoFirstNameQuestion.getName())
        Answer childrenInfoFirstNameAnswer = childrenInfoFirstNameQuestion.getAnswer()
        assertEquals('Kane', childrenInfoFirstNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1251/0', childrenInfoFirstNameAnswer.getPath())

        QuestionNodeInstance childrenInfoMiddleNameQuestion = child1QuestionNodeInstanceList.get(1)
        assertEquals('childrenInfoMiddleName', childrenInfoMiddleNameQuestion.getName())
        Answer childrenInfoMiddleNameAnswer = childrenInfoMiddleNameQuestion.getAnswer()
        assertEquals('Williamson', childrenInfoMiddleNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1252/0', childrenInfoMiddleNameAnswer.getPath())

        QuestionNodeInstance childrenInfoLastNameQuestion = child1QuestionNodeInstanceList.get(2)
        assertEquals('childrenInfoLastName', childrenInfoLastNameQuestion.getName())
        Answer childrenInfoLastNameAnswer = childrenInfoLastNameQuestion.getAnswer()
        assertEquals('Anna', childrenInfoLastNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1253/0', childrenInfoLastNameAnswer.getPath())

        QuestionNodeInstance childrenInfoGenderQuestion = child1QuestionNodeInstanceList.get(3)
        assertEquals('childrenInfoGender', childrenInfoGenderQuestion.getName())
        Answer childrenInfoGenderAnswer = childrenInfoGenderQuestion.getAnswer()
        assertEquals('Male', childrenInfoGenderAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1254/0', childrenInfoGenderAnswer.getPath())

        QuestionNodeInstance childrenInfoCountryOfBirthQuestion = child1QuestionNodeInstanceList.get(4)
        assertEquals('childrenInfoCountryOfBirth', childrenInfoCountryOfBirthQuestion.getName())
        Answer childrenInfoCountryOfBirthAnswer = childrenInfoCountryOfBirthQuestion.getAnswer()
        assertEquals('US', childrenInfoCountryOfBirthAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1255/0', childrenInfoCountryOfBirthAnswer.getPath())

        //todo check displayText for displayTextRule testcase
        QuestionNodeInstance childrenInfoDateOfBirthQuestion = child1QuestionNodeInstanceList.get(5)
        assertEquals('childrenInfoDateOfBirth', childrenInfoDateOfBirthQuestion.getName())
        Answer childrenInfoDateOfBirthAnswer = childrenInfoDateOfBirthQuestion.getAnswer()
        assertEquals(DateUtil.fromDate(LocalDate.of(2003, Month.FEBRUARY, 11)), childrenInfoDateOfBirthAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1256/0', childrenInfoDateOfBirthAnswer.getPath())

        QuestionNodeInstance childrenInfoCountryOfPhysicalAddressQuestion = child1QuestionNodeInstanceList.get(6)
        assertEquals('childrenInfoCountryOfPhysicalAddress', childrenInfoCountryOfPhysicalAddressQuestion.getName())
        Answer childrenInfoCountryOfPhysicalAddressAnswer = childrenInfoCountryOfPhysicalAddressQuestion.getAnswer()
        assertEquals('US', childrenInfoCountryOfPhysicalAddressAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1257/0', childrenInfoCountryOfPhysicalAddressAnswer.getPath())

        QuestionNodeInstance childrenInfoStreetNoAndNameQuestion = child1QuestionNodeInstanceList.get(7)
        assertEquals('childrenInfoStreetNoAndName', childrenInfoStreetNoAndNameQuestion.getName())
        Answer childrenInfoStreetNoAndNameAnswer = childrenInfoStreetNoAndNameQuestion.getAnswer()
        assertEquals('250 A Godown North Street', childrenInfoStreetNoAndNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1258/0', childrenInfoStreetNoAndNameAnswer.getPath())

        QuestionNodeInstance childrenInfoHasSecondaryAddressDescriptionQuestion = child1QuestionNodeInstanceList.get(8)
        assertEquals('childrenInfoHasSecondaryAddressDescription', childrenInfoHasSecondaryAddressDescriptionQuestion.getName())
        Answer childrenInfoHasSecondaryAddressDescriptionAnswer = childrenInfoHasSecondaryAddressDescriptionQuestion.getAnswer()
        assertEquals('No', childrenInfoHasSecondaryAddressDescriptionAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1259/0', childrenInfoHasSecondaryAddressDescriptionAnswer.getPath())

        QuestionNodeInstance childrenInfoCityOrTownOrVillageQuestion = child1QuestionNodeInstanceList.get(9)
        assertEquals('childrenInfoCityOrTownOrVillage', childrenInfoCityOrTownOrVillageQuestion.getName())
        Answer childrenInfoCityOrTownOrVillageAnswer = childrenInfoCityOrTownOrVillageQuestion.getAnswer()
        assertEquals('Chicago', childrenInfoCityOrTownOrVillageAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1262/0', childrenInfoCityOrTownOrVillageAnswer.getPath())

        /////////////

        assertEquals(2, childrenInfoHasSecondaryAddressDescriptionQuestion.getChildren().size())
        List<QuestionNodeInstance> childrenInfoAddressHasSecondaryDecsriptionQuestionInstanceList = childrenInfoHasSecondaryAddressDescriptionQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(0, childrenInfoAddressHasSecondaryDecsriptionQuestionInstanceList.size())

        /////////////

        assertEquals(4, childrenInfoCountryOfPhysicalAddressQuestion.getChildren().size())
        List<QuestionNodeInstance> childrenInfoCountryOfPhysicalAddressQuestionList = childrenInfoCountryOfPhysicalAddressQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, childrenInfoCountryOfPhysicalAddressQuestionList.size())


        QuestionNodeInstance childrenInfoStateQuestion = childrenInfoCountryOfPhysicalAddressQuestionList.get(0)
        assertEquals('childrenInfoState', childrenInfoStateQuestion.getName())
        Answer childrenInfoStateAnswer = childrenInfoStateQuestion.getAnswer()
        assertEquals('Illinois', childrenInfoStateAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1263/0', childrenInfoStateAnswer.getPath())

        QuestionNodeInstance childrenInfoZipCodeQuestion = childrenInfoCountryOfPhysicalAddressQuestionList.get(1)
        assertEquals('childrenInfoZipCode', childrenInfoZipCodeQuestion.getName())
        Answer childrenInfoZipCodeAnswer = childrenInfoZipCodeQuestion.getAnswer()
        assertEquals('60612', childrenInfoZipCodeAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1265/0', childrenInfoZipCodeAnswer.getPath())
    }


    private void assertChildInfoRepeatingGroupInstance2Answers(RepeatingQuestionGroupNodeInstance childInfoRepeatingGroupInstance2) {
        assertEquals('RQG_childrenInformation', childInfoRepeatingGroupInstance2.getId())
        List<EasyVisaNodeInstance> repeatingGroupChildren = childInfoRepeatingGroupInstance2.getChildren()
        assertEquals(10, repeatingGroupChildren.size())

        List<QuestionNodeInstance> child2QuestionNodeInstanceList = repeatingGroupChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(10, child2QuestionNodeInstanceList.size())

        QuestionNodeInstance childrenInfoFirstNameQuestion = child2QuestionNodeInstanceList.get(0)
        assertEquals('childrenInfoFirstName', childrenInfoFirstNameQuestion.getName())
        Answer childrenInfoFirstNameAnswer = childrenInfoFirstNameQuestion.getAnswer()
        assertEquals('Gary', childrenInfoFirstNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1251/1', childrenInfoFirstNameAnswer.getPath())

        QuestionNodeInstance childrenInfoMiddleNameQuestion = child2QuestionNodeInstanceList.get(1)
        assertEquals('childrenInfoMiddleName', childrenInfoMiddleNameQuestion.getName())
        Answer childrenInfoMiddleNameAnswer = childrenInfoMiddleNameQuestion.getAnswer()
        assertEquals('Raymond', childrenInfoMiddleNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1252/1', childrenInfoMiddleNameAnswer.getPath())

        QuestionNodeInstance childrenInfoLastNameQuestion = child2QuestionNodeInstanceList.get(2)
        assertEquals('childrenInfoLastName', childrenInfoLastNameQuestion.getName())
        Answer childrenInfoLastNameAnswer = childrenInfoLastNameQuestion.getAnswer()
        assertEquals('Stead', childrenInfoLastNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1253/1', childrenInfoLastNameAnswer.getPath())

        QuestionNodeInstance childrenInfoGenderQuestion = child2QuestionNodeInstanceList.get(3)
        assertEquals('childrenInfoGender', childrenInfoGenderQuestion.getName())
        Answer childrenInfoGenderAnswer = childrenInfoGenderQuestion.getAnswer()
        assertEquals('Male', childrenInfoGenderAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1254/1', childrenInfoGenderAnswer.getPath())

        QuestionNodeInstance childrenInfoCountryOfBirthQuestion = child2QuestionNodeInstanceList.get(4)
        assertEquals('childrenInfoCountryOfBirth', childrenInfoCountryOfBirthQuestion.getName())
        Answer childrenInfoCountryOfBirthAnswer = childrenInfoCountryOfBirthQuestion.getAnswer()
        assertEquals('US', childrenInfoCountryOfBirthAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1255/1', childrenInfoCountryOfBirthAnswer.getPath())

        //todo check displayText for displayTextRule testcase
        QuestionNodeInstance childrenInfoDateOfBirthQuestion = child2QuestionNodeInstanceList.get(5)
        assertEquals('childrenInfoDateOfBirth', childrenInfoDateOfBirthQuestion.getName())
        Answer childrenInfoDateOfBirthAnswer = childrenInfoDateOfBirthQuestion.getAnswer()
        assertEquals(DateUtil.fromDate(LocalDate.of(2008, Month.NOVEMBER, 22)), childrenInfoDateOfBirthAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1256/1', childrenInfoDateOfBirthAnswer.getPath())

        QuestionNodeInstance childrenInfoCountryOfPhysicalAddressQuestion = child2QuestionNodeInstanceList.get(6)
        assertEquals('childrenInfoCountryOfPhysicalAddress', childrenInfoCountryOfPhysicalAddressQuestion.getName())
        Answer childrenInfoCountryOfPhysicalAddressAnswer = childrenInfoCountryOfPhysicalAddressQuestion.getAnswer()
        assertEquals('US', childrenInfoCountryOfPhysicalAddressAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1257/1', childrenInfoCountryOfPhysicalAddressAnswer.getPath())

        QuestionNodeInstance childrenInfoStreetNoAndNameQuestion = child2QuestionNodeInstanceList.get(7)
        assertEquals('childrenInfoStreetNoAndName', childrenInfoStreetNoAndNameQuestion.getName())
        Answer childrenInfoStreetNoAndNameAnswer = childrenInfoStreetNoAndNameQuestion.getAnswer()
        assertEquals('12 Arms Villa', childrenInfoStreetNoAndNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1258/1', childrenInfoStreetNoAndNameAnswer.getPath())

        QuestionNodeInstance childrenInfoHasSecondaryAddressDescriptionQuestion = child2QuestionNodeInstanceList.get(8)
        assertEquals('childrenInfoHasSecondaryAddressDescription', childrenInfoHasSecondaryAddressDescriptionQuestion.getName())
        Answer childrenInfoHasSecondaryAddressDescriptionAnswer = childrenInfoHasSecondaryAddressDescriptionQuestion.getAnswer()
        assertEquals('Yes', childrenInfoHasSecondaryAddressDescriptionAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1259/1', childrenInfoHasSecondaryAddressDescriptionAnswer.getPath())

        QuestionNodeInstance childrenInfoCityOrTownOrVillageQuestion = child2QuestionNodeInstanceList.get(9)
        assertEquals('childrenInfoCityOrTownOrVillage', childrenInfoCityOrTownOrVillageQuestion.getName())
        Answer childrenInfoCityOrTownOrVillageAnswer = childrenInfoCityOrTownOrVillageQuestion.getAnswer()
        assertEquals('Los Angeles', childrenInfoCityOrTownOrVillageAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1262/1', childrenInfoCityOrTownOrVillageAnswer.getPath())

        /////////////

        assertEquals(2, childrenInfoHasSecondaryAddressDescriptionQuestion.getChildren().size())
        List<QuestionNodeInstance> childrenInfoAddressHasSecondaryDecsriptionQuestionInstanceList = childrenInfoHasSecondaryAddressDescriptionQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, childrenInfoAddressHasSecondaryDecsriptionQuestionInstanceList.size())

        QuestionNodeInstance childrenInfoSecondaryAddressDescriptionQuestion = childrenInfoAddressHasSecondaryDecsriptionQuestionInstanceList.get(0)
        assertEquals('childrenInfoSecondaryAddressDescription', childrenInfoSecondaryAddressDescriptionQuestion.getName())
        Answer childrenInfoSecondaryAddressDescriptionAnswer = childrenInfoSecondaryAddressDescriptionQuestion.getAnswer()
        assertEquals('Apartment', childrenInfoSecondaryAddressDescriptionAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1260/1', childrenInfoSecondaryAddressDescriptionAnswer.getPath())

        QuestionNodeInstance childrenInfoApartmentOrSuiteOrFloorQuestion = childrenInfoAddressHasSecondaryDecsriptionQuestionInstanceList.get(1)
        assertEquals('childrenInfoApartmentOrSuiteOrFloor', childrenInfoApartmentOrSuiteOrFloorQuestion.getName())
        Answer childrenInfoApartmentOrSuiteOrFloorAnswer = childrenInfoApartmentOrSuiteOrFloorQuestion.getAnswer()
        assertEquals('Samuel Apartments', childrenInfoApartmentOrSuiteOrFloorAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1261/1', childrenInfoApartmentOrSuiteOrFloorAnswer.getPath())

        /////////////

        assertEquals(4, childrenInfoCountryOfPhysicalAddressQuestion.getChildren().size())
        List<QuestionNodeInstance> childrenInfoCountryOfPhysicalAddressQuestionList = childrenInfoCountryOfPhysicalAddressQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, childrenInfoCountryOfPhysicalAddressQuestionList.size())

        QuestionNodeInstance childrenInfoStateQuestion = childrenInfoCountryOfPhysicalAddressQuestionList.get(0)
        assertEquals('childrenInfoState', childrenInfoStateQuestion.getName())
        Answer childrenInfoStateAnswer = childrenInfoStateQuestion.getAnswer()
        assertEquals('California', childrenInfoStateAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1263/1', childrenInfoStateAnswer.getPath())

        QuestionNodeInstance childrenInfoZipCodeQuestion = childrenInfoCountryOfPhysicalAddressQuestionList.get(1)
        assertEquals('childrenInfoZipCode', childrenInfoZipCodeQuestion.getName())
        Answer childrenInfoZipCodeAnswer = childrenInfoZipCodeQuestion.getAnswer()
        assertEquals('90001', childrenInfoZipCodeAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_childrenInformation/Q_1265/1', childrenInfoZipCodeAnswer.getPath())
    }


    private void assertDependentsChildrenSubSectionInstanceAnswers(List<EasyVisaNodeInstance> familyInformationSectionInstanceList) {
        SubSectionNodeInstance dependentsChildrenInstance = (SubSectionNodeInstance) familyInformationSectionInstanceList.get(6)
        assertEquals('SubSec_dependentsChildren', dependentsChildrenInstance.getId())
        List<EasyVisaNodeInstance> subsectionChildren = dependentsChildrenInstance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())


        QuestionNodeInstance anyDependentsWhoAreChildrenQuestion = questionNodeInstanceList.get(0)
        assertEquals('anyDependentsWhoAreChildren', anyDependentsWhoAreChildrenQuestion.getName())
        Answer anyDependentsWhoAreChildrenAnswer = anyDependentsWhoAreChildrenQuestion.getAnswer()
        assertEquals('Yes', anyDependentsWhoAreChildrenAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1268', anyDependentsWhoAreChildrenAnswer.getPath())


        assertEquals(2, anyDependentsWhoAreChildrenQuestion.getChildren().size())
        List<RepeatingQuestionGroupNodeInstance> repeatingGroupInstanceList = anyDependentsWhoAreChildrenQuestion.getChildren().stream()
                .filter({ nodeInstance -> (nodeInstance instanceof RepeatingQuestionGroupNodeInstance) })
                .map({ nodeInstance -> (RepeatingQuestionGroupNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, repeatingGroupInstanceList.size())

        RepeatingQuestionGroupNodeInstance childInfoRepeatingGroupInstance1 = repeatingGroupInstanceList.get(0)
        assertDependentChildRepeatingGroupInstance1Answers(childInfoRepeatingGroupInstance1)

        /*RepeatingQuestionGroupNodeInstance childInfoRepeatingGroupInstance2 = repeatingGroupInstanceList.get(1)
        assertDependentChildRepeatingGroupInstance1Answers(childInfoRepeatingGroupInstance2)*/
    }


    private void assertDependentChildRepeatingGroupInstance1Answers(RepeatingQuestionGroupNodeInstance childInfoRepeatingGroupInstance1) {
        assertEquals('RQG_dependentsChildren', childInfoRepeatingGroupInstance1.getId())
        List<EasyVisaNodeInstance> repeatingGroupChildren = childInfoRepeatingGroupInstance1.getChildren()
        assertEquals(14, repeatingGroupChildren.size())

        List<QuestionNodeInstance> child1QuestionNodeInstanceList = repeatingGroupChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(14, child1QuestionNodeInstanceList.size())


        QuestionNodeInstance selectChildToAutoFillDataQuestion = child1QuestionNodeInstanceList.get(0)
        assertEquals('selectChildToAutoFillData', selectChildToAutoFillDataQuestion.getName())
        Answer selectChildToAutoFillDataAnswer = selectChildToAutoFillDataQuestion.getAnswer()
        assertEquals('Gary', selectChildToAutoFillDataAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1269/0', selectChildToAutoFillDataAnswer.getPath())

        InputSourceType selectChildToAutoFillDataInputSourceType = selectChildToAutoFillDataQuestion.getInputSourceType()
        assertEquals('PopulateDependentChildNamesRule', selectChildToAutoFillDataInputSourceType.getType())
        List<InputSourceType.ValueMap> inputSourceTypeValues = selectChildToAutoFillDataInputSourceType.values
        assertNotNull(inputSourceTypeValues)
        assertEquals(2, inputSourceTypeValues.size())
        assertEquals('Kane', inputSourceTypeValues[0].value)
        assertEquals('Gary', inputSourceTypeValues[1].value)


        QuestionNodeInstance dependentChildFirstNameQuestion = child1QuestionNodeInstanceList.get(1)
        assertEquals('dependentChildFirstName', dependentChildFirstNameQuestion.getName())
        Answer dependentChildFirstNameAnswer = dependentChildFirstNameQuestion.getAnswer()
        assertEquals('Gary', dependentChildFirstNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1270/0', dependentChildFirstNameAnswer.getPath())

        QuestionNodeInstance dependentChildMiddleNameQuestion = child1QuestionNodeInstanceList.get(2)
        assertEquals('dependentChildMiddleName', dependentChildMiddleNameQuestion.getName())
        Answer dependentChildMiddleNameAnswer = dependentChildMiddleNameQuestion.getAnswer()
        assertEquals('Raymond', dependentChildMiddleNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1271/0', dependentChildMiddleNameAnswer.getPath())

        QuestionNodeInstance dependentChildLastNameQuestion = child1QuestionNodeInstanceList.get(3)
        assertEquals('dependentChildLastName', dependentChildLastNameQuestion.getName())
        Answer dependentChildLastNameAnswer = dependentChildLastNameQuestion.getAnswer()
        assertEquals('Stead', dependentChildLastNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1272/0', dependentChildLastNameAnswer.getPath())

        QuestionNodeInstance dependentChildGenderQuestion = child1QuestionNodeInstanceList.get(4)
        assertEquals('dependentChildGender', dependentChildGenderQuestion.getName())
        Answer dependentChildGenderAnswer = dependentChildGenderQuestion.getAnswer()
        assertEquals('Male', dependentChildGenderAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1273/0', dependentChildGenderAnswer.getPath())

        QuestionNodeInstance dependentChildCountryOfBirthQuestion = child1QuestionNodeInstanceList.get(5)
        assertEquals('dependentChildCountryOfBirth', dependentChildCountryOfBirthQuestion.getName())
        Answer dependentChildCountryOfBirthAnswer = dependentChildCountryOfBirthQuestion.getAnswer()
        assertEquals('US', dependentChildCountryOfBirthAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1274/0', dependentChildCountryOfBirthAnswer.getPath())

        QuestionNodeInstance dependentChildCountryOfCitizenshipQuestion = child1QuestionNodeInstanceList.get(6)
        assertEquals('dependentChildCountryOfCitizenship', dependentChildCountryOfCitizenshipQuestion.getName())
        Answer dependentChildCountryOfCitizenshipAnswer = dependentChildCountryOfCitizenshipQuestion.getAnswer()
        assertEquals('US', dependentChildCountryOfCitizenshipAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1275/0', dependentChildCountryOfCitizenshipAnswer.getPath())

        QuestionNodeInstance dependentChildDateOfBirthQuestion = child1QuestionNodeInstanceList.get(7)
        assertEquals('dependentChildDateOfBirth', dependentChildDateOfBirthQuestion.getName())
        Answer dependentChildDateOfBirthAnswer = dependentChildDateOfBirthQuestion.getAnswer()
        assertEquals(DateUtil.fromDate(LocalDate.of(2008, Month.NOVEMBER, 22)), dependentChildDateOfBirthAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1276/0', dependentChildDateOfBirthAnswer.getPath())

        QuestionNodeInstance dependentChildCountryOfPhysicalAddressQuestion = child1QuestionNodeInstanceList.get(8)
        assertEquals('dependentChildCountryOfPhysicalAddress', dependentChildCountryOfPhysicalAddressQuestion.getName())
        Answer dependentChildCountryOfPhysicalAddressAnswer = dependentChildCountryOfPhysicalAddressQuestion.getAnswer()
        assertEquals('US', dependentChildCountryOfPhysicalAddressAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1277/0', dependentChildCountryOfPhysicalAddressAnswer.getPath())

        QuestionNodeInstance dependentChildStreetNumberAndNameQuestion = child1QuestionNodeInstanceList.get(9)
        assertEquals('dependentChildStreetNumberAndName', dependentChildStreetNumberAndNameQuestion.getName())
        Answer dependentChildStreetNumberAndNameAnswer = dependentChildStreetNumberAndNameQuestion.getAnswer()
        assertEquals('12 Arms Villa', dependentChildStreetNumberAndNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1278/0', dependentChildStreetNumberAndNameAnswer.getPath())

        QuestionNodeInstance dependentChildHasSecondaryAddressDescriptionQuestion = child1QuestionNodeInstanceList.get(10)
        assertEquals('dependentChildHasSecondaryAddressDescription', dependentChildHasSecondaryAddressDescriptionQuestion.getName())
        Answer dependentChildHasSecondaryAddressDescriptionAnswer = dependentChildHasSecondaryAddressDescriptionQuestion.getAnswer()
        assertEquals('Yes', dependentChildHasSecondaryAddressDescriptionAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1279/0', dependentChildHasSecondaryAddressDescriptionAnswer.getPath())

        QuestionNodeInstance dependentChildCityOrTownOrVillageQuestion = child1QuestionNodeInstanceList.get(11)
        assertEquals('dependentChildCityOrTownOrVillage', dependentChildCityOrTownOrVillageQuestion.getName())
        Answer dependentChildCityOrTownOrVillageAnswer = dependentChildCityOrTownOrVillageQuestion.getAnswer()
        assertEquals('Los Angeles', dependentChildCityOrTownOrVillageAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1282/0', dependentChildCityOrTownOrVillageAnswer.getPath())

        QuestionNodeInstance dependentChildRelationshipToYouQuestion = child1QuestionNodeInstanceList.get(12)
        assertEquals('dependentChildRelationshipToYou', dependentChildRelationshipToYouQuestion.getName())
        Answer dependentChildRelationshipToYouAnswer = dependentChildRelationshipToYouQuestion.getAnswer()
        assertEquals('Child', dependentChildRelationshipToYouAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1287/0', dependentChildRelationshipToYouAnswer.getPath())

        QuestionNodeInstance dependentChildDegreeOfDependencyQuestion = child1QuestionNodeInstanceList.get(13)
        assertEquals('dependentChildDegreeOfDependency', dependentChildDegreeOfDependencyQuestion.getName())
        Answer dependentChildDegreeOfDependencyAnswer = dependentChildDegreeOfDependencyQuestion.getAnswer()
        assertEquals('Wholly Dependent', dependentChildDegreeOfDependencyAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1288/0', dependentChildDegreeOfDependencyAnswer.getPath())

        /////////////

        assertEquals(2, dependentChildHasSecondaryAddressDescriptionQuestion.getChildren().size())
        List<QuestionNodeInstance> dependentChildHasSecondaryAddressDescriptionQuestionInstanceList = dependentChildHasSecondaryAddressDescriptionQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, dependentChildHasSecondaryAddressDescriptionQuestionInstanceList.size())

        QuestionNodeInstance dependentChildSecondaryAddressDescriptionQuestion = dependentChildHasSecondaryAddressDescriptionQuestionInstanceList.get(0)
        assertEquals('dependentChildSecondaryAddressDescription', dependentChildSecondaryAddressDescriptionQuestion.getName())
        Answer dependentChildSecondaryAddressDescriptionAnswer = dependentChildSecondaryAddressDescriptionQuestion.getAnswer()
        assertEquals('Apartment', dependentChildSecondaryAddressDescriptionAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1280/0', dependentChildSecondaryAddressDescriptionAnswer.getPath())

        QuestionNodeInstance dependentChildApartmentOrSuiteOrFloorQuestion = dependentChildHasSecondaryAddressDescriptionQuestionInstanceList.get(1)
        assertEquals('dependentChildApartmentOrSuiteOrFloor', dependentChildApartmentOrSuiteOrFloorQuestion.getName())
        Answer dependentChildApartmentOrSuiteOrFloorAnswer = dependentChildApartmentOrSuiteOrFloorQuestion.getAnswer()
        assertEquals('Samuel Apartments', dependentChildApartmentOrSuiteOrFloorAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1281/0', dependentChildApartmentOrSuiteOrFloorAnswer.getPath())

        /////////////

        assertEquals(4, dependentChildCountryOfPhysicalAddressQuestion.getChildren().size())
        List<QuestionNodeInstance> dependentChildCountryOfPhysicalAddressQuestionList = dependentChildCountryOfPhysicalAddressQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, dependentChildCountryOfPhysicalAddressQuestionList.size())


        QuestionNodeInstance dependentChildStateQuestion = dependentChildCountryOfPhysicalAddressQuestionList.get(0)
        assertEquals('dependentChildState', dependentChildStateQuestion.getName())
        Answer dependentChildStateAnswer = dependentChildStateQuestion.getAnswer()
        assertEquals('California', dependentChildStateAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1283/0', dependentChildStateAnswer.getPath())

        QuestionNodeInstance dependentChildZipCodeQuestion = dependentChildCountryOfPhysicalAddressQuestionList.get(1)
        assertEquals('dependentChildZipCode', dependentChildZipCodeQuestion.getName())
        Answer dependentChildZipCodeAnswer = dependentChildZipCodeQuestion.getAnswer()
        assertEquals('90001', dependentChildZipCodeAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_dependentsChildren/Q_1285/0', dependentChildZipCodeAnswer.getPath())
    }

}
