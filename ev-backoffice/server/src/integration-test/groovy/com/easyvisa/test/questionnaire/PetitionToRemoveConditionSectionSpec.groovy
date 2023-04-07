package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.SectionNodeInstance
import com.easyvisa.questionnaire.answering.SubSectionNodeInstance
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
class PetitionToRemoveConditionSectionSpec extends TestMockUtils {

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


    void testRemoveCondnCategoryQuestionnaireWithRelationshipAnswer() throws Exception {
        given:
        String sectionId = "Sec_basisPetitionToRemoveConditionsOnResidence"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.REMOVECOND)

        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.principalBeneficiary
        List<Answer> answerList =
                AnswerListStub.populatePetitionToRemoveConditionsOnResidenceAnswerList(aPackage.id, applicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id, sectionId,
                answerList)

        when:
        def sectionAnswerList = answerService.fetchAnswers(aPackage.id, applicant.id)
        SectionNodeInstance beneficiarySectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id,
                        sectionId, sectionAnswerList)

        then:
        assertNotNull(beneficiarySectionInstance)
        assertRemoveCondnBeneficiaryWithRelationshipAnswers(beneficiarySectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertRemoveCondnBeneficiaryWithRelationshipAnswers(SectionNodeInstance beneficiarySectionInstance) {
        List<EasyVisaNodeInstance> beneficiaryFamilyInformationSubsectionInstanceList = beneficiarySectionInstance.getChildren()
        assertEquals(1, beneficiaryFamilyInformationSubsectionInstanceList.size())

        SubSectionNodeInstance basisPetitionToRemoveConditionsSubsectionInstance = (SubSectionNodeInstance) beneficiaryFamilyInformationSubsectionInstanceList[0]
        assertEquals('SubSec_basisPetitionToRemoveConditionsOnResidence', basisPetitionToRemoveConditionsSubsectionInstance.getId())
        List<EasyVisaNodeInstance> basisPetitionToRemoveConditionsSubsectionChildren = basisPetitionToRemoveConditionsSubsectionInstance.getChildren()
        assertEquals(2, basisPetitionToRemoveConditionsSubsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = basisPetitionToRemoveConditionsSubsectionChildren.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() && (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, questionNodeInstanceList.size())

        QuestionNodeInstance personYouAreCurrentlyMarriedToTheSamePersonQuestion = questionNodeInstanceList[0]
        assertEquals('personYouAreCurrentlyMarriedToTheSamePerson', personYouAreCurrentlyMarriedToTheSamePersonQuestion.getName())
        Answer personYouAreCurrentlyMarriedToTheSamePersonAnswer = personYouAreCurrentlyMarriedToTheSamePersonQuestion.getAnswer()
        assertEquals('Yes', personYouAreCurrentlyMarriedToTheSamePersonAnswer.getValue())
        assertEquals('Sec_basisPetitionToRemoveConditionsOnResidence/SubSec_basisPetitionToRemoveConditionsOnResidence/Q_1701', personYouAreCurrentlyMarriedToTheSamePersonAnswer.getPath())

        QuestionNodeInstance filingToRemoveYourTemporaryResidenceQuestion = questionNodeInstanceList[1]
        assertEquals('filingToRemoveYourTemporaryResidence', filingToRemoveYourTemporaryResidenceQuestion.getName())
        Answer filingToRemoveYourTemporaryResidenceAnswer = filingToRemoveYourTemporaryResidenceQuestion.getAnswer()
        assertEquals('Individually', filingToRemoveYourTemporaryResidenceAnswer.getValue())
        assertEquals('Sec_basisPetitionToRemoveConditionsOnResidence/SubSec_basisPetitionToRemoveConditionsOnResidence/Q_1702', filingToRemoveYourTemporaryResidenceAnswer.getPath())


        assertEquals(6, filingToRemoveYourTemporaryResidenceQuestion.getChildren().size())
        List<QuestionNodeInstance> filingToRemoveYourTemporaryQuestionNodeInstanceList = filingToRemoveYourTemporaryResidenceQuestion.getChildren().stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() && (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(5, filingToRemoveYourTemporaryQuestionNodeInstanceList.size())
    }



    void testRemoveCondnCategoryQuestionnaireWithoutRelationshipAnswer() throws Exception {
        given:
        String sectionId = "Sec_basisPetitionToRemoveConditionsOnResidence"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.REMOVECOND)

        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.principalBeneficiary
        List<Answer> answerList =
                AnswerListStub.populatePetitionToRemoveConditionsOnResidenceAnswerList(aPackage.id, applicant.id, false)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id, sectionId,
                answerList)

        when:
        def sectionAnswerList = answerService.fetchAnswers(aPackage.id, applicant.id)
        SectionNodeInstance beneficiarySectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id,
                        sectionId, sectionAnswerList)

        then:
        assertNotNull(beneficiarySectionInstance)
        assertRemoveCondnBeneficiaryWithoutRelationshipAnswers(beneficiarySectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertRemoveCondnBeneficiaryWithoutRelationshipAnswers(SectionNodeInstance beneficiarySectionInstance) {
        List<EasyVisaNodeInstance> beneficiaryFamilyInformationSubsectionInstanceList = beneficiarySectionInstance.getChildren()
        assertEquals(1, beneficiaryFamilyInformationSubsectionInstanceList.size())

        SubSectionNodeInstance basisPetitionToRemoveConditionsSubsectionInstance = (SubSectionNodeInstance) beneficiaryFamilyInformationSubsectionInstanceList[0]
        assertEquals('SubSec_basisPetitionToRemoveConditionsOnResidence', basisPetitionToRemoveConditionsSubsectionInstance.getId())
        List<EasyVisaNodeInstance> basisPetitionToRemoveConditionsSubsectionChildren = basisPetitionToRemoveConditionsSubsectionInstance.getChildren()
        assertEquals(2, basisPetitionToRemoveConditionsSubsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = basisPetitionToRemoveConditionsSubsectionChildren.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() && (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())

        QuestionNodeInstance filingToRemoveYourTemporaryResidenceQuestion = questionNodeInstanceList[0]
        assertEquals('filingToRemoveYourTemporaryResidence', filingToRemoveYourTemporaryResidenceQuestion.getName())
        Answer filingToRemoveYourTemporaryResidenceAnswer = filingToRemoveYourTemporaryResidenceQuestion.getAnswer()
        assertEquals('Individually', filingToRemoveYourTemporaryResidenceAnswer.getValue())
        assertEquals('Sec_basisPetitionToRemoveConditionsOnResidence/SubSec_basisPetitionToRemoveConditionsOnResidence/Q_1702', filingToRemoveYourTemporaryResidenceAnswer.getPath())


        assertEquals(6, filingToRemoveYourTemporaryResidenceQuestion.getChildren().size())
        List<QuestionNodeInstance> filingToRemoveYourTemporaryQuestionNodeInstanceList = filingToRemoveYourTemporaryResidenceQuestion.getChildren().stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() && (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(5, filingToRemoveYourTemporaryQuestionNodeInstanceList.size())
    }
}

