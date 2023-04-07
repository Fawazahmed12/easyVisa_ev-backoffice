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
class RelationshipToPetitionerFianceQuestionSpec extends TestMockUtils {

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

    void testFianceQuestionsCountrySelectionUSQuestionnaire() throws Exception {
        given:
        String sectionId = 'Sec_relationshipToPetitioner'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListStub.relationshipToPetitionerAnswerList1(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def sectionAnswerList = answerService.fetchAnswers(aPackage.id, petitionerApplicant.id)
        SectionNodeInstance relationshipToPetitionerSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id,
                        petitionerApplicant.id, sectionId, sectionAnswerList)
        assertNotNull(relationshipToPetitionerSectionInstance)
        relationshipToPetitionerSectionInstance.sortChildren()

        then:
        assertNotNull(relationshipToPetitionerSectionInstance)
        assertRelationshipToPetitionerAnswersForCountryUS(relationshipToPetitionerSectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertRelationshipToPetitionerAnswersForCountryUS(SectionNodeInstance relationshipToPetitionerSectionInstance) {

        List<EasyVisaNodeInstance> relationshipToPetitionerSectionInstanceList = relationshipToPetitionerSectionInstance.getChildren()
        assertEquals(1, relationshipToPetitionerSectionInstanceList.size())

        List<EasyVisaNodeInstance> visibleRelationshipToPetitionerSectionInstanceList = relationshipToPetitionerSectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())
        assertEquals(1, visibleRelationshipToPetitionerSectionInstanceList.size())


        SubSectionNodeInstance fianceQuestionsSubSectionInstance = (SubSectionNodeInstance) visibleRelationshipToPetitionerSectionInstanceList[0]
        assertNotNull(fianceQuestionsSubSectionInstance)

        List<EasyVisaNodeInstance> subsectionChildren = fianceQuestionsSubSectionInstance.getChildren()
        assertEquals(10, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(10, questionNodeInstanceList.size())

        QuestionNodeInstance countryIMBLocatedQuestion = questionNodeInstanceList.get(5)
        assertEquals('countryIMBLocated', countryIMBLocatedQuestion.getName())
        Answer countryIMBLocatedAnswer = countryIMBLocatedQuestion.getAnswer()
        assertEquals('United States', countryIMBLocatedAnswer.getValue())
        assertEquals('Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1361', countryIMBLocatedAnswer.getPath())


        assertEquals(4, countryIMBLocatedQuestion.getChildren().size())
        List<QuestionNodeInstance> fianceQuestionsInstanceList = countryIMBLocatedQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, fianceQuestionsInstanceList.size())

        QuestionNodeInstance stateQuestion = fianceQuestionsInstanceList.get(0)
        assertEquals('fianceQuestionState', stateQuestion.getName())
        Answer stateAnswer = stateQuestion.getAnswer()
        assertEquals('Colorado', stateAnswer.getValue())
        assertEquals('Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1367', stateAnswer.getPath())

        QuestionNodeInstance zipCodeQuestion = fianceQuestionsInstanceList.get(1)
        assertEquals('fianceQuestionZipCode', zipCodeQuestion.getName())
        Answer zipCodeAnswer = zipCodeQuestion.getAnswer()
        assertEquals('80011', zipCodeAnswer.getValue())
        assertEquals('Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1369', zipCodeAnswer.getPath())

    }

    void testFianceQuestionsCountrySelectionOthersQuestionnaire() throws Exception {
        given:
        String sectionId = 'Sec_relationshipToPetitioner'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListStub.relationshipToPetitionerAnswerList2(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def sectionAnswerList = answerService.fetchAnswers(aPackage.id, petitionerApplicant.id)
        SectionNodeInstance relationshipToPetitionerSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id,
                        petitionerApplicant.id, sectionId, sectionAnswerList)
        assertNotNull(relationshipToPetitionerSectionInstance)
        relationshipToPetitionerSectionInstance.sortChildren()

        then:
        assertNotNull(relationshipToPetitionerSectionInstance)
        assertRelationshipToPetitionerAnswersForCountryOthers(relationshipToPetitionerSectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertRelationshipToPetitionerAnswersForCountryOthers(SectionNodeInstance relationshipToPetitionerSectionInstance) {

        List<EasyVisaNodeInstance> relationshipToPetitionerSectionInstanceList = relationshipToPetitionerSectionInstance.getChildren()
        assertEquals(1, relationshipToPetitionerSectionInstanceList.size())

        List<EasyVisaNodeInstance> visibleRelationshipToPetitionerSectionInstanceList = relationshipToPetitionerSectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())
        assertEquals(1, visibleRelationshipToPetitionerSectionInstanceList.size())


        SubSectionNodeInstance fianceQuestionsSubSectionInstance = (SubSectionNodeInstance) visibleRelationshipToPetitionerSectionInstanceList[0]
        assertNotNull(fianceQuestionsSubSectionInstance)

        List<EasyVisaNodeInstance> subsectionChildren = fianceQuestionsSubSectionInstance.getChildren()
        assertEquals(10, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(10, questionNodeInstanceList.size())

        QuestionNodeInstance countryIMBLocatedQuestion = questionNodeInstanceList.get(5)
        assertEquals('countryIMBLocated', countryIMBLocatedQuestion.getName())
        Answer countryIMBLocatedAnswer = countryIMBLocatedQuestion.getAnswer()
        assertEquals('India', countryIMBLocatedAnswer.getValue())
        assertEquals('Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1361', countryIMBLocatedAnswer.getPath())


        assertEquals(4, countryIMBLocatedQuestion.getChildren().size())
        List<QuestionNodeInstance> fianceQuestionsInstanceList = countryIMBLocatedQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, fianceQuestionsInstanceList.size())

        QuestionNodeInstance stateQuestion = fianceQuestionsInstanceList.get(0)
        assertEquals('fianceQuestionProvinceTerritoryPrefectureParish', stateQuestion.getName())
        Answer stateAnswer = stateQuestion.getAnswer()
        assertEquals('Tamilnadu', stateAnswer.getValue())
        assertEquals('Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1368', stateAnswer.getPath())

        QuestionNodeInstance zipCodeQuestion = fianceQuestionsInstanceList.get(1)
        assertEquals('fianceQuestionPostalCode', zipCodeQuestion.getName())
        Answer zipCodeAnswer = zipCodeQuestion.getAnswer()
        assertEquals('600005', zipCodeAnswer.getValue())
        assertEquals('Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1370', zipCodeAnswer.getPath())
    }

}
