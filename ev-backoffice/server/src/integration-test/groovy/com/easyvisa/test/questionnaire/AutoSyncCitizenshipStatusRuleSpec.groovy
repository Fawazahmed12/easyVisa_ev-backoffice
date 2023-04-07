package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.CitizenshipStatus
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.SectionNodeInstance
import com.easyvisa.questionnaire.answering.SubSectionNodeInstance
import com.easyvisa.utils.AnswerListStub
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.TestMockUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import java.util.stream.Collectors

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

@Integration
class AutoSyncCitizenshipStatusRuleSpec extends TestMockUtils {

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

    void testCitizenshipStatusSyncQuestionnaire() throws Exception {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort                 : serverPort,
                                                                 attorneyService            : attorneyService,
                                                                 packageService             : packageService,
                                                                 adminService               : adminService,
                                                                 answerService              : answerService,
                                                                 packageQuestionnaireService: packageQuestionnaireService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)
                .buildUsersForPackageApplicants()
        Package sourcePackage = testHelper.aPackage
        Applicant petitionerApplicant = sourcePackage.petitioner.applicant
        List<Answer> greenCardHolderCitizenLegalStatusAnswerList = AnswerListStub.greenCardHolderCitizenLegalStatusAnswerList(sourcePackage.id, petitionerApplicant.id)
        testHelper.buildAnswers(greenCardHolderCitizenLegalStatusAnswerList);

        PackageTestBuilder packageCopyHelper = PackageTestBuilder.init(testHelper)
        packageCopyHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3, petitionerApplicant.id)
        Package petitionerCopiedPackage = packageCopyHelper.aPackage
        Applicant copiedPackagePetitionerApplicant = petitionerCopiedPackage.petitioner.applicant
        List<Answer> usCitizenLegalStatusAnswerList = AnswerListStub.usCitizenLegalStatusAnswerList(petitionerCopiedPackage.id, copiedPackagePetitionerApplicant.id)
        packageCopyHelper.buildAnswers(usCitizenLegalStatusAnswerList);


        when:
        String sectionId = 'Sec_legalStatusInUS'
        def sectionAnswerList = answerService.fetchAnswers(sourcePackage.id, petitionerApplicant.id)
        SectionNodeInstance petitionerSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(sourcePackage.id, petitionerApplicant.id, sectionId, sectionAnswerList)

        then:
        assertNotNull(sourcePackage)
        assertNotNull(sourcePackage.petitioner)
        assertEquals(CitizenshipStatus.U_S_CITIZEN, sourcePackage.petitioner.citizenshipStatus)
        assertNotNull(petitionerCopiedPackage)
        assertNotNull(petitionerCopiedPackage.petitioner)
        assertEquals(CitizenshipStatus.U_S_CITIZEN, petitionerCopiedPackage.petitioner.citizenshipStatus)
        assertNotNull(petitionerSectionInstance)
        assertLegalStatusSectionAnswers(petitionerSectionInstance)

        cleanup:
        testHelper.deletePackageOnly(false)
        packageCopyHelper.clean()
    }

    void testCitizenshipStatusLPRWarning() throws Exception {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort                 : serverPort,
                                                                 attorneyService            : attorneyService,
                                                                 packageService             : packageService,
                                                                 adminService               : adminService,
                                                                 answerService              : answerService,
                                                                 packageQuestionnaireService: packageQuestionnaireService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)
                .buildUsersForPackageApplicants()
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> greenCardHolderCitizenLegalStatusAnswerList = AnswerListStub.greenCardHolderCitizenLegalStatusAnswerList(aPackage.id, petitionerApplicant.id)
        testHelper.buildAnswers(greenCardHolderCitizenLegalStatusAnswerList);

        when:
        String sectionId = 'Sec_legalStatusInUS'
        def sectionAnswerList = answerService.fetchAnswers(aPackage.id, petitionerApplicant.id)
        SectionNodeInstance petitionerSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, petitionerApplicant.id, sectionId, sectionAnswerList)

        then:
        assertCitizenshipStatusWarning(aPackage, petitionerSectionInstance);

        cleanup:
        testHelper.deletePackageOnly(false)
    }

    private void assertCitizenshipStatusWarning(Package aPackage, SectionNodeInstance legalStatusInUSSectionInstance){
        List<EasyVisaNodeInstance> legalStatusInUSSectionInstanceList = legalStatusInUSSectionInstance.getChildren()
        assertEquals(2, legalStatusInUSSectionInstanceList.size())

        List<EasyVisaNodeInstance> visibleLegalStatusInUSSectionInstanceList = legalStatusInUSSectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())
        assertEquals(1, visibleLegalStatusInUSSectionInstanceList.size())

        SubSectionNodeInstance legalStatusInUSndGovtIDNosSubSectionInstance = (SubSectionNodeInstance) visibleLegalStatusInUSSectionInstanceList.stream()
                .filter({ x -> x.getDefinitionNode().getId().equals('SubSec_legalStatusInUSndGovtIDNos') })
                .findFirst()
                .orElse(null)
        assertNotNull(legalStatusInUSndGovtIDNosSubSectionInstance)

        List<EasyVisaNodeInstance> subsectionChildren = legalStatusInUSndGovtIDNosSubSectionInstance.getChildren()
        assertEquals(4, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() && (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(4, questionNodeInstanceList.size())

        QuestionNodeInstance legalStatusInUSQuestion = questionNodeInstanceList.get(0)
        assertEquals('legalStatusInUS1', legalStatusInUSQuestion.getName())
        Answer legalStatusInUSAnswer = legalStatusInUSQuestion.getAnswer()
        assertEquals('lawful_permanent_resident', legalStatusInUSAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_109', legalStatusInUSAnswer.getPath())
        assertCitizenshipStatusLPRWarning(aPackage, legalStatusInUSSectionInstance, legalStatusInUSAnswer)
    }

    private void assertCitizenshipStatusLPRWarning(Package aPackage, SectionNodeInstance sectionInstance, Answer legalStatusInUSAnswer) {
        assertNotNull(aPackage)
        assertNotNull(sectionInstance)
        Warning.withNewTransaction {
            Warning citizenshipStatusWarning = Warning.findByAPackageAndQuestionId(aPackage, 'Q_109')
            assertNotNull(citizenshipStatusWarning)
            assertNotNull(citizenshipStatusWarning.answer)
            assertEquals(citizenshipStatusWarning.answer.id, legalStatusInUSAnswer.id)
            assertNotNull(citizenshipStatusWarning.body)
        }
    }

    private void assertLegalStatusSectionAnswers(SectionNodeInstance legalStatusInUSSectionInstance) {
        List<EasyVisaNodeInstance> legalStatusInUSSectionInstanceList = legalStatusInUSSectionInstance.getChildren()
        assertEquals(2, legalStatusInUSSectionInstanceList.size())

        List<EasyVisaNodeInstance> visibleLegalStatusInUSSectionInstanceList = legalStatusInUSSectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())
        assertEquals(2, visibleLegalStatusInUSSectionInstanceList.size())

        assertLPRLegalStatusInUSndGovtIDNosSubSectionInstanceAnswers(visibleLegalStatusInUSSectionInstanceList)
    }

    private void assertLPRLegalStatusInUSndGovtIDNosSubSectionInstanceAnswers(List<EasyVisaNodeInstance> visibleLegalStatusInUSSectionInstanceList) {
        SubSectionNodeInstance legalStatusInUSndGovtIDNosSubSectionInstance = (SubSectionNodeInstance) visibleLegalStatusInUSSectionInstanceList.stream()
                .filter({ x -> x.getDefinitionNode().getId().equals('SubSec_legalStatusInUSndGovtIDNos') })
                .findFirst()
                .orElse(null)
        assertNotNull(legalStatusInUSndGovtIDNosSubSectionInstance)

        List<EasyVisaNodeInstance> subsectionChildren = legalStatusInUSndGovtIDNosSubSectionInstance.getChildren()
        assertEquals(4, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() && (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(4, questionNodeInstanceList.size())

        QuestionNodeInstance legalStatusInUSQuestion = questionNodeInstanceList.get(0)
        assertEquals('legalStatusInUS1', legalStatusInUSQuestion.getName())
        Answer legalStatusInUSAnswer = legalStatusInUSQuestion.getAnswer()
        assertEquals('united_states_citizen', legalStatusInUSAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_109', legalStatusInUSAnswer.getPath())


        QuestionNodeInstance haveYouEverHadAnAlienNumberQuestion = questionNodeInstanceList.get(1)
        assertEquals('everHadAlienNumber', haveYouEverHadAnAlienNumberQuestion.getName())
        Answer haveYouEverHadAnAlienNumberQuestionAnswer = haveYouEverHadAnAlienNumberQuestion.getAnswer()
        assertEquals('Yes', haveYouEverHadAnAlienNumberQuestionAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_6099', haveYouEverHadAnAlienNumberQuestionAnswer.getPath())

        assertEquals(1, haveYouEverHadAnAlienNumberQuestion.getChildren().size())
        List<QuestionNodeInstance> haveYouEverHadAnAlienNumberQuestionInstanceList = haveYouEverHadAnAlienNumberQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, haveYouEverHadAnAlienNumberQuestionInstanceList.size())

        QuestionNodeInstance alienNumberQuestion = haveYouEverHadAnAlienNumberQuestionInstanceList.get(0)
        assertEquals('alienRegistrationNumber', alienNumberQuestion.getName())
        Answer alienNumberNumberAnswer = alienNumberQuestion.getAnswer()
        assertEquals('944358001', alienNumberNumberAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_112', alienNumberNumberAnswer.getPath())

        QuestionNodeInstance doesThisPersonHaveSSNPetitionerQuestion = questionNodeInstanceList.get(2)
        assertEquals('doesThisPersonHaveSSNPetitioner', doesThisPersonHaveSSNPetitionerQuestion.getName())
        Answer doesThisPersonHaveSSNPetitionerQuestionAnswer = doesThisPersonHaveSSNPetitionerQuestion.getAnswer()
        assertEquals('Yes', doesThisPersonHaveSSNPetitionerQuestionAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_6097', doesThisPersonHaveSSNPetitionerQuestionAnswer.getPath())

        assertEquals(1, doesThisPersonHaveSSNPetitionerQuestion.getChildren().size())
        List<QuestionNodeInstance> doesThisPersonHaveSSNPetitionerQuestionInstanceList = doesThisPersonHaveSSNPetitionerQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, doesThisPersonHaveSSNPetitionerQuestionInstanceList.size())

        QuestionNodeInstance socialSecurityNumberQuestion = doesThisPersonHaveSSNPetitionerQuestionInstanceList.get(0)
        assertEquals('socialSecurityNumber', socialSecurityNumberQuestion.getName())
        Answer socialSecurityNumberAnswer = socialSecurityNumberQuestion.getAnswer()
        assertEquals('944355965', socialSecurityNumberAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_113', socialSecurityNumberAnswer.getPath())

        QuestionNodeInstance doYouHaveUSCISELISAccountNoQuestion = questionNodeInstanceList.get(3)
        assertEquals('doYouHaveUSCISELISAccountNo', doYouHaveUSCISELISAccountNoQuestion.getName())
        Answer doYouHaveUSCISELISAccountNoAnswer = doYouHaveUSCISELISAccountNoQuestion.getAnswer()
        assertEquals('Yes', doYouHaveUSCISELISAccountNoAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_114', doYouHaveUSCISELISAccountNoAnswer.getPath())

        assertEquals(1, doYouHaveUSCISELISAccountNoQuestion.getChildren().size())
        List<QuestionNodeInstance> doYouHaveUSCISELISAccountNoQuestionInstanceList = doYouHaveUSCISELISAccountNoQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, doYouHaveUSCISELISAccountNoQuestionInstanceList.size())

        QuestionNodeInstance uscisELISAccountNoQuestion = doYouHaveUSCISELISAccountNoQuestionInstanceList.get(0)
        assertEquals('uscisELISAccountNo', uscisELISAccountNoQuestion.getName())
        Answer uscisELISAccountNoAnswer = uscisELISAccountNoQuestion.getAnswer()
        assertEquals('MS8148938971', uscisELISAccountNoAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_115', uscisELISAccountNoAnswer.getPath())
    }
}
