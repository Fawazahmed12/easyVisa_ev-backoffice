package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.*
import com.easyvisa.utils.AnswerListStub
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.QuestionnaireTestDBSetupUtility
import com.easyvisa.utils.TestMockUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import spock.lang.Ignore
import spock.lang.Unroll

import java.util.stream.Collectors

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

@Integration
class PovertyGuidelineCalculationSpec extends TestMockUtils {

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

    @Unroll
    void "Test #povertyLabel Poverty Guideline for #benCat (#state) -  Household size=#hhSize, Military=#military"() {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])

        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, benCat)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListStub.povertyGuideLineAnswerList(aPackage.id, petitionerApplicant.id, hhSize, military, state, asset)

        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        List<Answer> sectionAnswerList = answerService.fetchAnswers(aPackage.id, petitionerApplicant.id)
        SectionNodeInstance familyInformationSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id,
                        petitionerApplicant.id, sectionId, sectionAnswerList)

        then:
        assertNotNull(familyInformationSectionInstance)
        assertPovertyGuidelineRuleFires(familyInformationSectionInstance, hhDepSize, labelVisibility)


        cleanup:
        testHelper.clean()

        where:
        benCat                         | hhSize | military | state        | asset   | income | hhDepSize | povertyLabel | labelVisibility
        ImmigrationBenefitCategory.IR1 | "1"    | "no"     | "Alaska"     | "56474" | 0      | 9         | "Below"      | true
        ImmigrationBenefitCategory.IR1 | "1"    | "yes"    | "Alaska"     | "56474" | 0      | 9         | "Above"      | false
        ImmigrationBenefitCategory.IR1 | "2"    | "yes"    | "Alaska"     | "56474" | 0      | 9         | "Below"      | true
        ImmigrationBenefitCategory.IR1 | "1"    | "no"     | "California" | "56474" | 0      | 9         | "Above"      | false
        ImmigrationBenefitCategory.IR1 | "1"    | "no"     | "California" | "40774" | 0      | 9         | "Below"      | true
        ImmigrationBenefitCategory.IR1 | "1"    | "yes"    | "California" | "40774" | 0      | 9         | "Above"      | false
        ImmigrationBenefitCategory.IR1 | "2"    | "no"     | "California" | "56474" | 0      | 9         | "Below"      | true
        ImmigrationBenefitCategory.IR5 | "1"    | "no"     | "Alaska"     | "56474" | 0      | 9         | "Below"      | true
        ImmigrationBenefitCategory.IR5 | "1"    | "yes"    | "Alaska"     | "56474" | 0      | 9         | "Below"      | true
        ImmigrationBenefitCategory.IR5 | "1"    | "yes"    | "Alaska"     | "84954" | 0      | 9         | "Above"      | false



    }

    private void assertPovertyGuidelineRuleFires(SectionNodeInstance familyInformationSectionInstance, int hhDepSize, Boolean labelVisibility) {

        List<EasyVisaNodeInstance> familyInformationSectionInstanceList = familyInformationSectionInstance.getChildren()
        int familySectionListSize = familyInformationSectionInstanceList.size()
        assertEquals(hhDepSize, familySectionListSize)
        assertEquals("Sec_familyInformation", familyInformationSectionInstance.id)

        // Validate SubSec_householdSizeDependents exists and is visible
        SubSectionNodeInstance householdSizeDependentsInstance = (SubSectionNodeInstance) familyInformationSectionInstanceList[familySectionListSize - 2]
        assertEquals('SubSec_householdSizeDependents', householdSizeDependentsInstance.id)
        assertEquals(true, householdSizeDependentsInstance.visibility)

        // validate Error message appears or not based on poverty guideline threshold
        List<EasyVisaNodeInstance> subsectionChildren = householdSizeDependentsInstance.getChildren()
        assertEquals(4, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(4, questionNodeInstanceList.size())

        QuestionNodeInstance needAdditionalIncomeOrAssetsLabelQuestion = questionNodeInstanceList[1]
        assertEquals('needAdditionalIncomeOrAssetsLabel', needAdditionalIncomeOrAssetsLabelQuestion.getName())
        assertEquals(labelVisibility, needAdditionalIncomeOrAssetsLabelQuestion.visibility)
        Answer needAdditionalIncomeOrAssetsLabelAnswer = needAdditionalIncomeOrAssetsLabelQuestion.getAnswer()
        assertEquals('You need additional income or assets in order to sponsor your relative(s).', needAdditionalIncomeOrAssetsLabelQuestion.getDisplayText())
        assertEquals('Sec_familyInformation/SubSec_householdSizeDependents/Q_1312', needAdditionalIncomeOrAssetsLabelAnswer.getPath())
    }


    void testBelowPovertyGuidelineQuestionnaire() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListStub.belowPovertyGuidelineAnswerList(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        List<Answer> sectionAnswerList = answerService.fetchAnswers(aPackage.id, petitionerApplicant.id)
        SectionNodeInstance familyInformationSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id,
                        petitionerApplicant.id, sectionId, sectionAnswerList)

        then:
        assertNotNull(familyInformationSectionInstance)
        assertFamilyInformationSectionAnswersForBelowPovertyGuideline(familyInformationSectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertFamilyInformationSectionAnswersForBelowPovertyGuideline(SectionNodeInstance familyInformationSectionInstance) {
        List<EasyVisaNodeInstance> familyInformationSectionInstanceList = familyInformationSectionInstance.getChildren()
        assertEquals(9, familyInformationSectionInstanceList.size())

        List<EasyVisaNodeInstance> visibleFamilyInformationSectionInstanceList = familyInformationSectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())
        assertEquals(8, visibleFamilyInformationSectionInstanceList.size())

        assertHouseholdSizeDependentsSubSectionInstanceAnswersForBelowPovertyGuideline(visibleFamilyInformationSectionInstanceList)
        assertHouseholdIncomeSubSectionInstanceAnswersForBelowPovertyGuideline(visibleFamilyInformationSectionInstanceList)
    }


    private void assertHouseholdSizeDependentsSubSectionInstanceAnswersForBelowPovertyGuideline(List<EasyVisaNodeInstance> familyInformationSectionInstanceList) {
        SubSectionNodeInstance householdSizeDependentsInstance = (SubSectionNodeInstance) familyInformationSectionInstanceList[6]
        assertEquals('SubSec_householdSizeDependents', householdSizeDependentsInstance.getId())
        List<EasyVisaNodeInstance> subsectionChildren = householdSizeDependentsInstance.getChildren()
        assertEquals(4, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) /*&& nodeInstance.isVisibility()*/) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(4, questionNodeInstanceList.size())


        QuestionNodeInstance howManyLPRWhomYouAreCurrentlyObligatedQuestion = questionNodeInstanceList[0]
        assertEquals('howManyLPRWhomYouAreCurrentlyObligated', howManyLPRWhomYouAreCurrentlyObligatedQuestion.getName())
        Answer howManyLPRWhomYouAreCurrentlyObligatedAnswer = howManyLPRWhomYouAreCurrentlyObligatedQuestion.getAnswer()
        assertEquals('3', howManyLPRWhomYouAreCurrentlyObligatedAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_householdSizeDependents/Q_1311', howManyLPRWhomYouAreCurrentlyObligatedAnswer.getPath())

        QuestionNodeInstance needAdditionalIncomeOrAssetsLabelQuestion = questionNodeInstanceList[1]
        assertEquals('needAdditionalIncomeOrAssetsLabel', needAdditionalIncomeOrAssetsLabelQuestion.getName())
        Answer needAdditionalIncomeOrAssetsLabelAnswer = needAdditionalIncomeOrAssetsLabelQuestion.getAnswer()
        assertEquals('You need additional income or assets in order to sponsor your relative(s).', needAdditionalIncomeOrAssetsLabelQuestion.getDisplayText())
        assertEquals('Sec_familyInformation/SubSec_householdSizeDependents/Q_1312', needAdditionalIncomeOrAssetsLabelAnswer.getPath())

        QuestionNodeInstance doYouHaveAnotherPersonWillingToSponserQuestion = questionNodeInstanceList[2]
        assertEquals('doYouHaveAnotherPersonWillingToSponser', doYouHaveAnotherPersonWillingToSponserQuestion.getName())
        Answer doYouHaveAnotherPersonWillingToSponserAnswer = doYouHaveAnotherPersonWillingToSponserQuestion.getAnswer()
        assertEquals('No', doYouHaveAnotherPersonWillingToSponserAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_householdSizeDependents/Q_1313', doYouHaveAnotherPersonWillingToSponserAnswer.getPath())

        QuestionNodeInstance doYouHaveAnySiblingsParentsOrAdultChildrenQuestion = questionNodeInstanceList[3]
        assertEquals('doYouHaveAnySiblingsParentsOrAdultChildren', doYouHaveAnySiblingsParentsOrAdultChildrenQuestion.getName())
        Answer doYouHaveAnySiblingsParentsOrAdultChildrenAnswer = doYouHaveAnySiblingsParentsOrAdultChildrenQuestion.getAnswer()
        assertEquals('Yes', doYouHaveAnySiblingsParentsOrAdultChildrenAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_householdSizeDependents/Q_1314', doYouHaveAnySiblingsParentsOrAdultChildrenAnswer.getPath())
    }


    private void assertHouseholdIncomeSubSectionInstanceAnswersForBelowPovertyGuideline(List<EasyVisaNodeInstance> familyInformationSectionInstanceList) {
        SubSectionNodeInstance householdIncomeInstance = (SubSectionNodeInstance) familyInformationSectionInstanceList[7]
        assertEquals('SubSec_householdIncome', householdIncomeInstance.getId())
        List<EasyVisaNodeInstance> subsectionChildren = householdIncomeInstance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList1 = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList1.size())


        QuestionNodeInstance doYouHaveAnySiblingInYourSameResidenceQuestion = questionNodeInstanceList1[0]
        assertEquals('doYouHaveAnySiblingInYourSameResidence', doYouHaveAnySiblingInYourSameResidenceQuestion.getName())
        Answer doYouHaveAnySiblingInYourSameResidenceAnswer = doYouHaveAnySiblingInYourSameResidenceQuestion.getAnswer()
        assertEquals('Yes', doYouHaveAnySiblingInYourSameResidenceAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_householdIncome/Q_1316', doYouHaveAnySiblingInYourSameResidenceAnswer.getPath())


        List<RepeatingQuestionGroupNodeInstance> repeatingGroupInstanceList = doYouHaveAnySiblingInYourSameResidenceQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof RepeatingQuestionGroupNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (RepeatingQuestionGroupNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, repeatingGroupInstanceList.size())


        RepeatingQuestionGroupNodeInstance householdIncomeRepeatingGroup = repeatingGroupInstanceList[0]
        assertEquals('RQG_householdIncome', householdIncomeRepeatingGroup.getId())
        List<EasyVisaNodeInstance> repeatingGroupChildren = householdIncomeRepeatingGroup.getChildren()
        assertEquals(8, repeatingGroupChildren.size())
        List<QuestionNodeInstance> questionNodeInstanceList = repeatingGroupChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(8, questionNodeInstanceList.size())


        QuestionNodeInstance householdIncomeFirstNameQuestion = questionNodeInstanceList[0]
        assertEquals('householdIncomeFirstName', householdIncomeFirstNameQuestion.getName())
        Answer householdIncomeFirstNameAnswer = householdIncomeFirstNameQuestion.getAnswer()
        assertEquals('Stephen', householdIncomeFirstNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_householdIncome/Q_1317/0', householdIncomeFirstNameAnswer.getPath())


        QuestionNodeInstance householdIncomeMiddleNameQuestion = questionNodeInstanceList[1]
        assertEquals('householdIncomeMiddleName', householdIncomeMiddleNameQuestion.getName())
        Answer householdIncomeMiddleNameAnswer = householdIncomeMiddleNameQuestion.getAnswer()
        assertEquals('Mark', householdIncomeMiddleNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_householdIncome/Q_1318/0', householdIncomeMiddleNameAnswer.getPath())


        QuestionNodeInstance householdIncomeLastNameQuestion = questionNodeInstanceList[2]
        assertEquals('householdIncomeLastName', householdIncomeLastNameQuestion.getName())
        Answer householdIncomeLastNameAnswer = householdIncomeLastNameQuestion.getAnswer()
        assertEquals('Waugh', householdIncomeLastNameAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_householdIncome/Q_1319/0', householdIncomeLastNameAnswer.getPath())


        QuestionNodeInstance householdIncomeRelationshipToYouQuestion = questionNodeInstanceList[3]
        assertEquals('householdIncomeRelationshipToYou', householdIncomeRelationshipToYouQuestion.getName())
        Answer householdIncomeRelationshipToYouAnswer = householdIncomeRelationshipToYouQuestion.getAnswer()
        assertEquals('Sibling', householdIncomeRelationshipToYouAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_householdIncome/Q_1320/0', householdIncomeRelationshipToYouAnswer.getPath())


        QuestionNodeInstance householdIncomeAnnualIncomeQuestion = questionNodeInstanceList[4]
        assertEquals('householdIncomeAnnualIncome', householdIncomeAnnualIncomeQuestion.getName())
        Answer householdIncomeAnnualIncomeAnswer = householdIncomeAnnualIncomeQuestion.getAnswer()
        assertEquals('60000', householdIncomeAnnualIncomeAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_householdIncome/Q_1321/0', householdIncomeAnnualIncomeAnswer.getPath())
    }

    ////////////


    void testAbovePovertyGuidelineQuestionnaire() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListStub.abovePovertyGuidelineAnswerList(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        SectionNodeInstance familyInformationSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, petitionerApplicant.id, sectionId, answerList)

        then:
        assertNotNull(familyInformationSectionInstance)
        assertFamilyInformationSectionAnswersForAbovePovertyGuideline(familyInformationSectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertFamilyInformationSectionAnswersForAbovePovertyGuideline(SectionNodeInstance familyInformationSectionInstance) {
        List<EasyVisaNodeInstance> familyInformationSectionInstanceList = familyInformationSectionInstance.getChildren()
        assertEquals(9, familyInformationSectionInstanceList.size())

        List<EasyVisaNodeInstance> visibleFamilyInformationSectionInstanceList = familyInformationSectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())
        assertEquals(8, visibleFamilyInformationSectionInstanceList.size())

        assertHouseholdSizeDependentsSubSectionInstanceAnswersForAbovePovertyGuideline(visibleFamilyInformationSectionInstanceList)
    }


    private void assertHouseholdSizeDependentsSubSectionInstanceAnswersForAbovePovertyGuideline(List<EasyVisaNodeInstance> familyInformationSectionInstanceList) {
        SubSectionNodeInstance householdSizeDependentsInstance = (SubSectionNodeInstance) familyInformationSectionInstanceList[6]
        assertEquals('SubSec_householdSizeDependents', householdSizeDependentsInstance.getId())
        List<EasyVisaNodeInstance> subsectionChildren = householdSizeDependentsInstance.getChildren()
        assertEquals(4, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())

        QuestionNodeInstance howManyLPRWhomYouAreCurrentlyObligatedQuestion = questionNodeInstanceList[0]
        assertEquals('howManyLPRWhomYouAreCurrentlyObligated', howManyLPRWhomYouAreCurrentlyObligatedQuestion.getName())
        Answer howManyLPRWhomYouAreCurrentlyObligatedAnswer = howManyLPRWhomYouAreCurrentlyObligatedQuestion.getAnswer()
        assertEquals('2', howManyLPRWhomYouAreCurrentlyObligatedAnswer.getValue())
        assertEquals('Sec_familyInformation/SubSec_householdSizeDependents/Q_1311', howManyLPRWhomYouAreCurrentlyObligatedAnswer.getPath())
    }
}
