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
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.RepeatingQuestionGroupNodeInstance
import com.easyvisa.questionnaire.answering.SectionNodeInstance
import com.easyvisa.questionnaire.answering.SubSectionNodeInstance
import com.easyvisa.questionnaire.meta.InputSourceType
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
class DropdownListGeneratorSpec extends TestMockUtils {

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

    void testBiographicInformationSectionForMetricUnitQuestionnaire() throws Exception {
        given:
        String sectionId = "Sec_biographicInformationForBeneficiary"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListStub.metricUnitBiographicInformationAnswerList(aPackage.id, applicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id, sectionId,
                answerList)

        when:
        List<Answer> biographicAnswerList = this.answerService.fetchAnswers(aPackage.id, applicant.id, [sectionId])
        SectionNodeInstance sectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id,
                        sectionId, biographicAnswerList)
        assertNotNull(sectionInstance)

        then:
        assertMetricUnitBiographicInformationAnswers(sectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertMetricUnitBiographicInformationAnswers(SectionNodeInstance sectionInstance) {
        List<EasyVisaNodeInstance> subsectionInstanceList = sectionInstance.getChildren()
        assertEquals(6, subsectionInstanceList.size())

        assertEthnicitySubsectionAndItsAnswers(subsectionInstanceList)
        assertRaceSubsectionAndItsAnswers(subsectionInstanceList)
        assertWeightSubsectionAndItsAnswers(subsectionInstanceList)
        assertEyeColorSubsectionAndItsAnswers(subsectionInstanceList)
        assertHairColorSubsectionAndItsAnswers(subsectionInstanceList)
        assertHeightSubsectionAndItsAnswersByMetricUnit(subsectionInstanceList)
    }


    void testBiographicInformationSectionForImperialUnitQuestionnaire() throws Exception {
        given:
        String sectionId = "Sec_biographicInformationForBeneficiary"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListStub.imperialUnitBiographicInformationAnswerList(aPackage.id, applicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id, sectionId,
                answerList)

        when:
        List<Answer> biographicAnswerList = this.answerService.fetchAnswers(aPackage.id, applicant.id, [sectionId])
        SectionNodeInstance sectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id,
                        sectionId, biographicAnswerList)
        assertNotNull(sectionInstance)

        then:
        assertImperialUnitBiographicInformationAnswers(sectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertImperialUnitBiographicInformationAnswers(SectionNodeInstance sectionInstance) {
        List<EasyVisaNodeInstance> subsectionInstanceList = sectionInstance.getChildren()
        assertEquals(6, subsectionInstanceList.size())

        assertEthnicitySubsectionAndItsAnswers(subsectionInstanceList)
        assertRaceSubsectionAndItsAnswers(subsectionInstanceList)
        assertWeightSubsectionAndItsAnswersByImperialUnit(subsectionInstanceList)
        assertEyeColorSubsectionAndItsAnswers(subsectionInstanceList)
        assertHairColorSubsectionAndItsAnswers(subsectionInstanceList)
        assertHeightSubsectionAndItsAnswersByImperialUnit(subsectionInstanceList)
    }


    private void assertEthnicitySubsectionAndItsAnswers(List<EasyVisaNodeInstance> subsectionInstanceList) {
        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) subsectionInstanceList.get(0)
        assertEquals("Ethnicity (Select only one box)", subsection1Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = subsection1Instance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())

        QuestionNodeInstance ethnicityQuestion = questionNodeInstanceList.get(0)
        assertEquals("ethnicity", ethnicityQuestion.getName())
        Answer ethnicityAnswer = ethnicityQuestion.getAnswer()
        assertEquals("Hispanic or Latino", ethnicityAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_ethnicityForBeneficiary/Q_2301", ethnicityAnswer.getPath())
    }


    private void assertRaceSubsectionAndItsAnswers(List<EasyVisaNodeInstance> subsectionInstanceList) {
        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) subsectionInstanceList.get(1)
        assertEquals("Race (Select all applicable boxes)", subsection1Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = subsection1Instance.getChildren()
        assertEquals(5, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(5, questionNodeInstanceList.size())

        QuestionNodeInstance whiteQuestion = questionNodeInstanceList.get(0)
        assertEquals("white", whiteQuestion.getName())
        Answer whiteAnswer = whiteQuestion.getAnswer()
        assertEquals("False", whiteAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_raceForBeneficiary/Q_2303", whiteAnswer.getPath())

        QuestionNodeInstance asianQuestion = questionNodeInstanceList.get(1)
        assertEquals("asian", asianQuestion.getName())
        Answer asianAnswer = asianQuestion.getAnswer()
        assertEquals("False", asianAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_raceForBeneficiary/Q_2304", asianAnswer.getPath())

        QuestionNodeInstance blackOrAfricanAmericanQuestion = questionNodeInstanceList.get(2)
        assertEquals("blackOrAfricanAmerican", blackOrAfricanAmericanQuestion.getName())
        Answer blackOrAfricanAmericanAnswer = blackOrAfricanAmericanQuestion.getAnswer()
        assertEquals("False", blackOrAfricanAmericanAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_raceForBeneficiary/Q_2305", blackOrAfricanAmericanAnswer.getPath())

        QuestionNodeInstance americanIndianOrAlaskaNativeQuestion = questionNodeInstanceList.get(3)
        assertEquals("americanIndianOrAlaskaNative", americanIndianOrAlaskaNativeQuestion.getName())
        Answer americanIndianOrAlaskaNativeAnswer = americanIndianOrAlaskaNativeQuestion.getAnswer()
        assertEquals("True", americanIndianOrAlaskaNativeAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_raceForBeneficiary/Q_2306", americanIndianOrAlaskaNativeAnswer.getPath())

        QuestionNodeInstance nativeHawaiianOrOtherPacificIslanderQuestion = questionNodeInstanceList.get(4)
        assertEquals("nativeHawaiianOrOtherPacificIslander", nativeHawaiianOrOtherPacificIslanderQuestion.getName())
        Answer nativeHawaiianOrOtherPacificIslanderAnswer = nativeHawaiianOrOtherPacificIslanderQuestion.getAnswer()
        assertEquals("False", nativeHawaiianOrOtherPacificIslanderAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_raceForBeneficiary/Q_2307", nativeHawaiianOrOtherPacificIslanderAnswer.getPath())
    }

    private void assertWeightSubsectionAndItsAnswersByImperialUnit(List<EasyVisaNodeInstance> subsectionInstanceList) {
        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) subsectionInstanceList.get(3)
        assertEquals("Weight", subsection1Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = subsection1Instance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())

        QuestionNodeInstance unitsMeasureQuestion = questionNodeInstanceList.get(0)
        assertEquals("unitsMeasure", unitsMeasureQuestion.getName())
        Answer unitsMeasureAnswer = unitsMeasureQuestion.getAnswer()
        assertEquals("Imperial", unitsMeasureAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_weightForBeneficiary/Q_2314", unitsMeasureAnswer.getPath())

        ///////////

        assertEquals(2, unitsMeasureQuestion.getChildren().size())
        List<QuestionNodeInstance> unitsMeasureQuestionInstanceList = unitsMeasureQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, unitsMeasureQuestionInstanceList.size())

        QuestionNodeInstance lbsWeightQuestion = unitsMeasureQuestion.getChildren()[0]
        assertEquals(true, lbsWeightQuestion.isVisibility())
        assertEquals("lbsWeight", lbsWeightQuestion.getName())
        Answer lbsWeightAnswer = lbsWeightQuestion.getAnswer()
        assertEquals("236", lbsWeightAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_weightForBeneficiary/Q_2315", lbsWeightAnswer.getPath())

        QuestionNodeInstance metricWeightQuestion = unitsMeasureQuestion.getChildren()[1]
        assertEquals(false, metricWeightQuestion.isVisibility())
        assertEquals("metricWeight", metricWeightQuestion.getName())
        Answer metricWeightAnswer = metricWeightQuestion.getAnswer()
        assertEquals("107", metricWeightAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_weightForBeneficiary/Q_6010", metricWeightAnswer.getPath())
    }


    private void assertWeightSubsectionAndItsAnswers(List<EasyVisaNodeInstance> subsectionInstanceList) {
        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) subsectionInstanceList.get(3)
        assertEquals("Weight", subsection1Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = subsection1Instance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())

        QuestionNodeInstance unitsMeasureQuestion = questionNodeInstanceList.get(0)
        assertEquals("unitsMeasure", unitsMeasureQuestion.getName())
        Answer unitsMeasureAnswer = unitsMeasureQuestion.getAnswer()
        assertEquals("Metric", unitsMeasureAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_weightForBeneficiary/Q_2314", unitsMeasureAnswer.getPath())

        ///////////

        assertEquals(2, unitsMeasureQuestion.getChildren().size())
        List<QuestionNodeInstance> unitsMeasureQuestionInstanceList = unitsMeasureQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, unitsMeasureQuestionInstanceList.size())

        QuestionNodeInstance weightQuestion = unitsMeasureQuestionInstanceList.get(0)
        assertEquals("metricWeight", weightQuestion.getName())
        Answer weightAnswer = weightQuestion.getAnswer()
        assertEquals("84", weightAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_weightForBeneficiary/Q_6010", weightAnswer.getPath())
    }


    private void assertEyeColorSubsectionAndItsAnswers(List<EasyVisaNodeInstance> subsectionInstanceList) {
        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) subsectionInstanceList.get(4)
        assertEquals("Eye Color", subsection1Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = subsection1Instance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())

        QuestionNodeInstance eyeColorQuestion = questionNodeInstanceList.get(0)
        assertEquals("eyeColor", eyeColorQuestion.getName())
        Answer eyeColorAnswer = eyeColorQuestion.getAnswer()
        assertEquals("Hazel", eyeColorAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_eyeColorForBeneficiary/Q_2317", eyeColorAnswer.getPath())
    }


    private void assertHairColorSubsectionAndItsAnswers(List<EasyVisaNodeInstance> subsectionInstanceList) {
        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) subsectionInstanceList.get(5)
        assertEquals("Hair Color", subsection1Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = subsection1Instance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())

        QuestionNodeInstance hairColorQuestion = questionNodeInstanceList.get(0)
        assertEquals("hairColor", hairColorQuestion.getName())
        Answer hairColorAnswer = hairColorQuestion.getAnswer()
        assertEquals("Gray", hairColorAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_hairColorForBeneficiary/Q_2319", hairColorAnswer.getPath())
    }

    private void assertHeightSubsectionAndItsAnswersByMetricUnit(List<EasyVisaNodeInstance> subsectionInstanceList) {
        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) subsectionInstanceList.get(2)
        assertEquals("Height", subsection1Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = subsection1Instance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())

        QuestionNodeInstance unitsMeasureQuestion = questionNodeInstanceList.get(0)
        assertEquals("unitsMeasure", unitsMeasureQuestion.getName())
        Answer unitsMeasureAnswer = unitsMeasureQuestion.getAnswer()
        assertEquals("Metric", unitsMeasureAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_heightForBeneficiary/Q_2309", unitsMeasureAnswer.getPath())

        ///////////

        assertEquals(3, unitsMeasureQuestion.getChildren().size())
        List<QuestionNodeInstance> unitsMeasureQuestionInstanceList = unitsMeasureQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, unitsMeasureQuestionInstanceList.size())

        QuestionNodeInstance centimeterQuestion = unitsMeasureQuestionInstanceList.get(0);
        assertEquals("centimeters", centimeterQuestion.getName());
        Answer centimeterAnswer = centimeterQuestion.getAnswer();
        assertEquals("172", centimeterAnswer.getValue());
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_heightForBeneficiary/Q_2312", centimeterAnswer.getPath());
    }


    private void assertHeightSubsectionAndItsAnswersByImperialUnit(List<EasyVisaNodeInstance> subsectionInstanceList) {
        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) subsectionInstanceList.get(2)
        assertEquals("Height", subsection1Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = subsection1Instance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())

        QuestionNodeInstance unitsMeasureQuestion = questionNodeInstanceList.get(0)
        assertEquals("unitsMeasure", unitsMeasureQuestion.getName())
        Answer unitsMeasureAnswer = unitsMeasureQuestion.getAnswer()
        assertEquals("Imperial", unitsMeasureAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_heightForBeneficiary/Q_2309", unitsMeasureAnswer.getPath())

        ///////////

        assertEquals(3, unitsMeasureQuestion.getChildren().size())
        List<QuestionNodeInstance> unitsMeasureQuestionInstanceList = unitsMeasureQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, unitsMeasureQuestionInstanceList.size())

        QuestionNodeInstance feetQuestion = unitsMeasureQuestionInstanceList.get(0)
        assertEquals("feet", feetQuestion.getName())
        Answer feetAnswer = feetQuestion.getAnswer()
        assertEquals("5", feetAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_heightForBeneficiary/Q_2310", feetAnswer.getPath())

        //Drop down list goes from 0-8
        InputSourceType feetInputSourceType = feetQuestion.getInputSourceType();
        assertNotNull(feetInputSourceType);
        assertEquals(9, feetInputSourceType.getValues().size());

        QuestionNodeInstance inchesQuestion = unitsMeasureQuestionInstanceList.get(1)
        assertEquals("inches ", inchesQuestion.getName())
        Answer inchesAnswer = inchesQuestion.getAnswer()
        assertEquals("10", inchesAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_heightForBeneficiary/Q_2311", inchesAnswer.getPath())

        //Drop down list goes from 0-11
        InputSourceType inchesInputSourceType = inchesQuestion.getInputSourceType()
        assertNotNull(inchesInputSourceType)
        assertEquals(12, inchesInputSourceType.getValues().size())
    }


    void testPersonalInformationSectionForMetricUnitQuestionnaire() throws Exception {
        given:
        String sectionId = "Sec_personelInformationForBeneficiary"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.principalBeneficiary

        List<Answer> answerList = AnswerListStub.personelInformationAnswerList(aPackage.id, applicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id, sectionId,
                answerList)

        when:
        SectionNodeInstance sectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id,
                        sectionId, answerList)
        assertNotNull(sectionInstance)

        then:
        assertPersonelInformationAnswers(sectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertPersonelInformationAnswers(SectionNodeInstance sectionInstance) {
        List<EasyVisaNodeInstance> subsectionInstanceList = sectionInstance.getChildren()
        assertEquals(1, subsectionInstanceList.size())

        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) subsectionInstanceList.get(0)
        assertEquals("Personal Information", subsection1Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = subsection1Instance.getChildren()
        assertEquals(4, subsectionChildren.size())

        List<RepeatingQuestionGroupNodeInstance> personelInformationForBeneficiaryRepeatingInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof RepeatingQuestionGroupNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (RepeatingQuestionGroupNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, personelInformationForBeneficiaryRepeatingInstanceList.size())

        RepeatingQuestionGroupNodeInstance personelInformationFirstInstance = personelInformationForBeneficiaryRepeatingInstanceList[0];
        List<QuestionNodeInstance> personelInformationRepeatingQuestionNodeInstanceList = personelInformationFirstInstance.getChildren().stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, personelInformationRepeatingQuestionNodeInstanceList.size())

        QuestionNodeInstance countryOfCitizenshipQuestion = personelInformationRepeatingQuestionNodeInstanceList.get(0)
        assertEquals("countryOfCitizenship", countryOfCitizenshipQuestion.getName())
        Answer countryOfCitizenshipAnswer = countryOfCitizenshipQuestion.getAnswer()
        assertEquals("Algeria", countryOfCitizenshipAnswer.getValue())
        assertEquals("Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2401/0", countryOfCitizenshipAnswer.getPath())

        //Drop Down (List of all Countries) EXCEPT United States
        InputSourceType countryOfCitizenshipInputSourceType = countryOfCitizenshipQuestion.getInputSourceType()
        assertNotNull(countryOfCitizenshipInputSourceType)
        assertEquals(241, countryOfCitizenshipInputSourceType.getValues().size())
    }
}
