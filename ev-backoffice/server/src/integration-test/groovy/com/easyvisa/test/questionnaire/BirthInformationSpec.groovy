package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.CitizenshipStatus
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
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
class BirthInformationSpec extends TestMockUtils {

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

    //  IR1 category has the following forms : I-130,  I-130A,  I-485,  I-693,  I-864
    // Here Form  I-130 has not child questions for 'Country of Birth' question, but 'I-864' has dependent questions based on its answer
    // Hence we should show dependent questions based on the answer
    void testIR1CategoryBirthInformationQuestionnaire() throws Exception {
        given:
        String sectionId = "Sec_birthInformation"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.client
        List<Answer> answerList = AnswerListStub.petitionerBirthInformationAnswerList(aPackage.id, applicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id, sectionId,
                answerList)

        when:
        List<Answer> birthInformationSectionAnswerList = this.answerService.fetchAnswers(aPackage.id, applicant.id, [sectionId])
        SectionNodeInstance sectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id,
                        sectionId, birthInformationSectionAnswerList)
        assertNotNull(sectionInstance)

        then:
        assertIR1CategoryBirthInformation(aPackage, sectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertIR1CategoryBirthInformation(Package aPackage, SectionNodeInstance sectionInstance) {
        List<SubSectionNodeInstance> subsectionInstanceList = sectionInstance.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof SubSectionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (SubSectionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, subsectionInstanceList.size())

        SubSectionNodeInstance bithInformationSubSecInstance = (SubSectionNodeInstance) subsectionInstanceList.get(0)
        assertNotNull(bithInformationSubSecInstance)
        assertEquals("SubSec_birthInformation", bithInformationSubSecInstance.getId())

        List<QuestionNodeInstance> birthInformationQuestionInstanceList = bithInformationSubSecInstance.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(4, birthInformationQuestionInstanceList.size())


        QuestionNodeInstance countryOfBirthQuestion = birthInformationQuestionInstanceList.get(2)
        assertEquals("countryofBirth", countryOfBirthQuestion.getName())
        Answer countryOfBirthAnswer = countryOfBirthQuestion.getAnswer()
        assertEquals("United States", countryOfBirthAnswer.getValue())
        assertEquals("Sec_birthInformation/SubSec_birthInformation/Q_89", countryOfBirthAnswer.getPath())


        List<QuestionNodeInstance> countryOfBirthQuestionInstanceList = countryOfBirthQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, countryOfBirthQuestionInstanceList.size())


        QuestionNodeInstance stateQuestion = countryOfBirthQuestionInstanceList.get(0)
        assertEquals("state", stateQuestion.getName())
        Answer stateAnswer = stateQuestion.getAnswer()
        assertEquals("Florida", stateAnswer.getValue())
        assertEquals("Sec_birthInformation/SubSec_birthInformation/Q_91", stateAnswer.getPath());
    }

    //  F2_A category has the following forms : I-130,  I-130A
    // Here Form  I-130 has not child questions for 'Country of Birth' question
    // Hence we should NOT ask any dependent questions irrecspective of the answer
    void testF2ACategorySponsorshipRelationshipQuestionnaire() throws Exception {
        given:
        String sectionId = "Sec_birthInformation"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.F2_A,
                null, null, CitizenshipStatus.LPR)

        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.client
        List<Answer> answerList = AnswerListStub.petitionerBirthInformationAnswerList(aPackage.id, applicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id, sectionId,
                answerList)

        when:
        List<Answer> birthInformationSectionAnswerList = this.answerService.fetchAnswers(aPackage.id, applicant.id, [sectionId])
        SectionNodeInstance sectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id,
                        sectionId, birthInformationSectionAnswerList)
        assertNotNull(sectionInstance)

        then:
        assertF2ACategoryBirthInformation(aPackage, sectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertF2ACategoryBirthInformation(Package aPackage, SectionNodeInstance sectionInstance) {
        List<SubSectionNodeInstance> subsectionInstanceList = sectionInstance.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof SubSectionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (SubSectionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, subsectionInstanceList.size())

        SubSectionNodeInstance bithInformationSubSecInstance = (SubSectionNodeInstance) subsectionInstanceList.get(0)
        assertNotNull(bithInformationSubSecInstance)
        assertEquals("SubSec_birthInformation", bithInformationSubSecInstance.getId())

        List<QuestionNodeInstance> birthInformationQuestionInstanceList = bithInformationSubSecInstance.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(4, birthInformationQuestionInstanceList.size())


        QuestionNodeInstance countryOfBirthQuestion = birthInformationQuestionInstanceList.get(2)
        assertEquals("countryofBirth", countryOfBirthQuestion.getName())
        Answer countryOfBirthAnswer = countryOfBirthQuestion.getAnswer()
        assertEquals("United States", countryOfBirthAnswer.getValue())
        assertEquals("Sec_birthInformation/SubSec_birthInformation/Q_89", countryOfBirthAnswer.getPath())


        List<QuestionNodeInstance> countryOfBirthQuestionInstanceList = countryOfBirthQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(0, countryOfBirthQuestionInstanceList.size())
    }
}
