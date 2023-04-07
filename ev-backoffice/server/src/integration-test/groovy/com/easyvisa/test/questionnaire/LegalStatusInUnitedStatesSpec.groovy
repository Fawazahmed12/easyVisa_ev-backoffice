package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.SectionNodeInstance
import com.easyvisa.questionnaire.answering.SubSectionNodeInstance
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
class LegalStatusInUnitedStatesSpec extends TestMockUtils {

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

    void testUSCitizenLegalStatusQuestionnaire() throws Exception {
        given:
        String sectionId = 'Sec_legalStatusInUS'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer>  answerList = AnswerListStub.usCitizenLegalStatusAnswerList(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        SectionNodeInstance legalStatusInUSSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id,
                        petitionerApplicant.id, sectionId, answerList)

        then:
        assertNotNull(legalStatusInUSSectionInstance)
        assertUSCitizenLegalStatusAnswers(legalStatusInUSSectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertUSCitizenLegalStatusAnswers(SectionNodeInstance legalStatusInUSSectionInstance) {
        List<EasyVisaNodeInstance> legalStatusInUSSectionInstanceList = legalStatusInUSSectionInstance.getChildren()
        assertEquals(3, legalStatusInUSSectionInstanceList.size())

        List<EasyVisaNodeInstance> visibleLegalStatusInUSSectionInstanceList = legalStatusInUSSectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())
        assertEquals(2, visibleLegalStatusInUSSectionInstanceList.size())

        assertUSCitizenLegalStatusInUSndGovtIDNosSubSectionInstanceAnswers(visibleLegalStatusInUSSectionInstanceList)
        assertUSCitizenSubSectionInstanceAnswers(visibleLegalStatusInUSSectionInstanceList)
    }

    private void assertUSCitizenLegalStatusInUSndGovtIDNosSubSectionInstanceAnswers(List<EasyVisaNodeInstance> visibleLegalStatusInUSSectionInstanceList) {
        SubSectionNodeInstance legalStatusInUSndGovtIDNosSubSectionInstance = (SubSectionNodeInstance) visibleLegalStatusInUSSectionInstanceList.stream()
                .filter({ x -> x.getDefinitionNode().getId().equals('SubSec_legalStatusInUSndGovtIDNos') })
                .findFirst()
                .orElse(null)
        assertNotNull(legalStatusInUSndGovtIDNosSubSectionInstance)

        List<EasyVisaNodeInstance> subsectionChildren = legalStatusInUSndGovtIDNosSubSectionInstance.getChildren()
        assertEquals(4, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(4, questionNodeInstanceList.size())

        QuestionNodeInstance legalStatusInUSQuestion = questionNodeInstanceList.get(0)
        assertEquals('legalStatusInUS1', legalStatusInUSQuestion.getName())
        Answer legalStatusInUSAnswer = legalStatusInUSQuestion.getAnswer()
        assertEquals('united_states_citizen', legalStatusInUSAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_109', legalStatusInUSAnswer.getPath())

        QuestionNodeInstance everHadAlienNumberQuestion = questionNodeInstanceList.get(1)
        assertEquals('everHadAlienNumber', everHadAlienNumberQuestion.getName())
        Answer everHadAlienNumberAnswer = everHadAlienNumberQuestion.getAnswer()
        assertEquals('Yes', everHadAlienNumberAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_6099', everHadAlienNumberAnswer.getPath())

        List<QuestionNodeInstance> everHadAlienNumberQuestionInstanceList = everHadAlienNumberQuestion.getChildren().stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() && (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, everHadAlienNumberQuestionInstanceList.size())

        QuestionNodeInstance alienRegistrationNumberQuestion = everHadAlienNumberQuestionInstanceList.get(0)
        assertEquals('alienRegistrationNumber', alienRegistrationNumberQuestion.getName())
        Answer alienRegistrationNumberAnswer = alienRegistrationNumberQuestion.getAnswer()
        assertEquals('978893170', alienRegistrationNumberAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_112', alienRegistrationNumberAnswer.getPath())

        QuestionNodeInstance doYouHaveUSCISELISAccountNoQuestion = questionNodeInstanceList.get(3)
        assertEquals('doYouHaveUSCISELISAccountNo', doYouHaveUSCISELISAccountNoQuestion.getName())
        Answer doYouHaveUSCISELISAccountNoAnswer = doYouHaveUSCISELISAccountNoQuestion.getAnswer()
        assertEquals('No', doYouHaveUSCISELISAccountNoAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_114', doYouHaveUSCISELISAccountNoAnswer.getPath())

        assertEquals(1, doYouHaveUSCISELISAccountNoQuestion.getChildren().size())
        List<QuestionNodeInstance> doYouHaveUSCISELISAccountNoQuestionInstanceList = doYouHaveUSCISELISAccountNoQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(0, doYouHaveUSCISELISAccountNoQuestionInstanceList.size())
    }

    private void assertUSCitizenSubSectionInstanceAnswers(List<EasyVisaNodeInstance> visibleLegalStatusInUSSectionInstanceList) {
        SubSectionNodeInstance usCitizenSubSectionInstance = (SubSectionNodeInstance) visibleLegalStatusInUSSectionInstanceList.stream()
                .filter({ x -> x.getDefinitionNode().getId().equals('SubSec_usCitizens') })
                .findFirst()
                .orElse(null)
        assertNotNull(usCitizenSubSectionInstance)

        List<EasyVisaNodeInstance> subsectionChildren = usCitizenSubSectionInstance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())


        QuestionNodeInstance howCitizenshipAcquiredQuestion = questionNodeInstanceList.get(0)
        assertEquals('howCitizenshipAcquired', howCitizenshipAcquiredQuestion.getName())
        Answer howCitizenshipAcquiredAnswer = howCitizenshipAcquiredQuestion.getAnswer()
        assertEquals('naturalization', howCitizenshipAcquiredAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_usCitizens/Q_120', howCitizenshipAcquiredAnswer.getPath())

        assertEquals(5, howCitizenshipAcquiredQuestion.getChildren().size())
        List<QuestionNodeInstance> howCitizenshipAcquiredQuestionInstanceList = howCitizenshipAcquiredQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(4, howCitizenshipAcquiredQuestionInstanceList.size())

        QuestionNodeInstance haveYouObtainedCitizenshipCertificateNoQuestion = howCitizenshipAcquiredQuestionInstanceList.get(0)
        assertEquals('haveYouObtainedCitizenshipCertificateNo', haveYouObtainedCitizenshipCertificateNoQuestion.getName())
        Answer haveYouObtainedCitizenshipCertificateNoAnswer = haveYouObtainedCitizenshipCertificateNoQuestion.getAnswer()
        assertEquals('Yes', haveYouObtainedCitizenshipCertificateNoAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_usCitizens/Q_121', haveYouObtainedCitizenshipCertificateNoAnswer.getPath())

        QuestionNodeInstance citizenshipCertificateNoQuestion = howCitizenshipAcquiredQuestionInstanceList.get(1)
        assertEquals('citizenshipCertificateNo', citizenshipCertificateNoQuestion.getName())
        Answer citizenshipCertificateNoAnswer = citizenshipCertificateNoQuestion.getAnswer()
        assertEquals('3423123098992001', citizenshipCertificateNoAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_usCitizens/Q_122', citizenshipCertificateNoAnswer.getPath())

        QuestionNodeInstance whereCitizenshipIssuedQuestion = howCitizenshipAcquiredQuestionInstanceList.get(2)
        assertEquals('whereCitizenshipIssued', whereCitizenshipIssuedQuestion.getName())
        Answer whereCitizenshipIssuedAnswer = whereCitizenshipIssuedQuestion.getAnswer()
        assertEquals('Arizona', whereCitizenshipIssuedAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_usCitizens/Q_123', whereCitizenshipIssuedAnswer.getPath())

        QuestionNodeInstance whatDateCitizenshipCertificateIssuedQuestion = howCitizenshipAcquiredQuestionInstanceList.get(3)
        assertEquals('whatDateCitizenshipCertificateIssued', whatDateCitizenshipCertificateIssuedQuestion.getName())
        Answer whatDateCitizenshipCertificateIssuedAnswer = whatDateCitizenshipCertificateIssuedQuestion.getAnswer()
        assertEquals(DateUtil.fromDate(LocalDate.of(2016, Month.JANUARY, 17)), whatDateCitizenshipCertificateIssuedAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_usCitizens/Q_124', whatDateCitizenshipCertificateIssuedAnswer.getPath())
    }

    void testGreenCardHolderCitizenLegalStatusQuestionnaire() throws Exception {
        given:
        String sectionId = 'Sec_legalStatusInUS'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer>  answerList = AnswerListStub.greenCardHolderCitizenLegalStatusAnswerList(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        SectionNodeInstance legalStatusInUSSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id,
                        petitionerApplicant.id, sectionId, answerList)

        then:
        assertNotNull(legalStatusInUSSectionInstance)
        assertLPRLegalStatusAnswers(legalStatusInUSSectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertLPRLegalStatusAnswers(SectionNodeInstance legalStatusInUSSectionInstance) {
        List<EasyVisaNodeInstance> legalStatusInUSSectionInstanceList = legalStatusInUSSectionInstance.getChildren()
        assertEquals(3, legalStatusInUSSectionInstanceList.size())

        List<EasyVisaNodeInstance> visibleLegalStatusInUSSectionInstanceList = legalStatusInUSSectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())
        assertEquals(2, visibleLegalStatusInUSSectionInstanceList.size())

        assertLPRLegalStatusInUSndGovtIDNosSubSectionInstanceAnswers(visibleLegalStatusInUSSectionInstanceList)
        assertLPRSubSectionInstanceAnswers(visibleLegalStatusInUSSectionInstanceList)
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
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(4, questionNodeInstanceList.size())

        QuestionNodeInstance legalStatusInUSQuestion = questionNodeInstanceList.get(0)
        assertEquals('legalStatusInUS1', legalStatusInUSQuestion.getName())
        Answer legalStatusInUSAnswer = legalStatusInUSQuestion.getAnswer()
        assertEquals('lawful_permanent_resident', legalStatusInUSAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_109', legalStatusInUSAnswer.getPath())

        QuestionNodeInstance everHadAlienNumberQuestion = questionNodeInstanceList.get(1)
        assertEquals('everHadAlienNumber', everHadAlienNumberQuestion.getName())
        Answer everHadAlienNumberAnswer = everHadAlienNumberQuestion.getAnswer()
        assertEquals('Yes', everHadAlienNumberAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_6099', everHadAlienNumberAnswer.getPath())

        List<QuestionNodeInstance> everHadAlienNumberQuestionInstanceList = everHadAlienNumberQuestion.getChildren().stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() && (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, everHadAlienNumberQuestionInstanceList.size())

        QuestionNodeInstance alienRegistrationNumberQuestion = everHadAlienNumberQuestionInstanceList.get(0)
        assertEquals('alienRegistrationNumber', alienRegistrationNumberQuestion.getName())
        Answer alienRegistrationNumberAnswer = alienRegistrationNumberQuestion.getAnswer()
        assertEquals('944358001', alienRegistrationNumberAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_112', alienRegistrationNumberAnswer.getPath())

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

    private void assertLPRSubSectionInstanceAnswers(List<EasyVisaNodeInstance> visibleLegalStatusInUSSectionInstanceList) {
        SubSectionNodeInstance lawfulPermanentResidentSubSectionInstance = (SubSectionNodeInstance) visibleLegalStatusInUSSectionInstanceList.stream()
                .filter({ x -> x.getDefinitionNode().getId().equals('SubSec_lawfulPermanentResident') })
                .findFirst()
                .orElse(null)
        assertNotNull(lawfulPermanentResidentSubSectionInstance)

        List<EasyVisaNodeInstance> subsectionChildren = lawfulPermanentResidentSubSectionInstance.getChildren()
        assertEquals(5, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(5, questionNodeInstanceList.size())

        QuestionNodeInstance dateOfAdmissionForLPRQuestion = questionNodeInstanceList.get(0)
        assertEquals('dateOfAdmissionForLPR', dateOfAdmissionForLPRQuestion.getName())
        Answer dateOfAdmissionForLPRAnswer = dateOfAdmissionForLPRQuestion.getAnswer()
        assertEquals(DateUtil.fromDate(LocalDate.of(2016, Month.JANUARY, 17)), dateOfAdmissionForLPRAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_lawfulPermanentResident/Q_125', dateOfAdmissionForLPRAnswer.getPath())

        QuestionNodeInstance cityOfAdmissionForLPRQuestion = questionNodeInstanceList.get(1)
        assertEquals('cityOfAdmissionForLPR', cityOfAdmissionForLPRQuestion.getName())
        Answer cityOfAdmissionForLPRAnswer = cityOfAdmissionForLPRQuestion.getAnswer()
        assertEquals('Los Angeles', cityOfAdmissionForLPRAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_lawfulPermanentResident/Q_126', cityOfAdmissionForLPRAnswer.getPath())

        QuestionNodeInstance placeOfAdmissionForLPRQuestion = questionNodeInstanceList.get(2)
        assertEquals('placeOfAdmissionForLPR', placeOfAdmissionForLPRQuestion.getName())
        Answer placeOfAdmissionForLPRAnswer = placeOfAdmissionForLPRQuestion.getAnswer()
        assertEquals('California', placeOfAdmissionForLPRAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_lawfulPermanentResident/Q_127', placeOfAdmissionForLPRAnswer.getPath())

        QuestionNodeInstance classOfAdmissionQuestion = questionNodeInstanceList.get(3)
        assertEquals('classOfAdmission', classOfAdmissionQuestion.getName())
        Answer classOfAdmissionAnswer = classOfAdmissionQuestion.getAnswer()
        assertEquals('Eureka', classOfAdmissionAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_lawfulPermanentResident/Q_128', classOfAdmissionAnswer.getPath())

        QuestionNodeInstance howDidYouGainYourPermanentResidenceQuestion = questionNodeInstanceList.get(4)
        assertEquals('howDidYouGainYourPermanentResidence', howDidYouGainYourPermanentResidenceQuestion.getName())
        Answer howDidYouGainYourPermanentResidenceAnswer = howDidYouGainYourPermanentResidenceQuestion.getAnswer()
        assertEquals('Marriage to a United States Citizen', howDidYouGainYourPermanentResidenceAnswer.getValue())
        assertEquals('Sec_legalStatusInUS/SubSec_lawfulPermanentResident/Q_129', howDidYouGainYourPermanentResidenceAnswer.getPath())
    }
}
