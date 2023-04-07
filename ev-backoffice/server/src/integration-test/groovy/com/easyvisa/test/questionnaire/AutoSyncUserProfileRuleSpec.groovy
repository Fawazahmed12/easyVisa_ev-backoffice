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
import com.easyvisa.utils.TestMockUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import java.util.stream.Collectors

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

@Integration
class AutoSyncUserProfileRuleSpec extends TestMockUtils {

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

    void testPetitionerToPetitionerApplicantQuestionnaire() throws Exception {
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
                .buildAnswers()
        Package sourcePackage = testHelper.aPackage
        Applicant petitionerApplicant = sourcePackage.petitioner.applicant
        PackageTestBuilder packageCopyHelper = PackageTestBuilder.init(testHelper)
        packageCopyHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3, petitionerApplicant.id)
        Package petitionerCopiedPackage = packageCopyHelper.aPackage
        Applicant copiedPackagePetitionerApplicant = petitionerCopiedPackage.petitioner.applicant
        List<Answer> petitionerCopiedPackageNameAnswerList = AnswerListStub.nameSectionPetitionerAnswerList(petitionerCopiedPackage.id, copiedPackagePetitionerApplicant.id)
        packageCopyHelper.buildAnswers(petitionerCopiedPackageNameAnswerList);


        when:
        String sectionId = 'Sec_2'
        def sectionAnswerList = answerService.fetchAnswers(sourcePackage.id, petitionerApplicant.id)
        SectionNodeInstance petitionerSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(sourcePackage.id, petitionerApplicant.id, sectionId, sectionAnswerList)

        then:
        assertNotNull(sourcePackage)
        assertNotNull(sourcePackage.petitioner)
        assertNotNull(sourcePackage.petitioner.applicant)
        assertNotNull(petitionerCopiedPackage)
        assertNotNull(petitionerCopiedPackage.petitioner)
        assertNotNull(petitionerCopiedPackage.petitioner.applicant)
        assertNotNull(petitionerSectionInstance)
        assertPetitioner2PetitionerNameAnswers(petitionerSectionInstance)

        cleanup:
        testHelper.deletePackageOnly(false)
        packageCopyHelper.clean()
    }


    void testBeneficiaryToPetitionerApplicantQuestionnaire() throws Exception {
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
                .buildAnswers()
        Package sourcePackage = testHelper.aPackage
        Applicant principalApplicant = sourcePackage.principalBeneficiary
        PackageTestBuilder packageCopyHelper = PackageTestBuilder.init(testHelper)
        packageCopyHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3, principalApplicant.id)
        Package principalBeneficiaryCopiedPackage = packageCopyHelper.aPackage
        Applicant copiedPackagePetitionerApplicant = principalBeneficiaryCopiedPackage.petitioner.applicant
        List<Answer> principalBeneficiaryCopiedPackageNameAnswerList = AnswerListStub.nameSectionPetitionerAnswerList(principalBeneficiaryCopiedPackage.id, copiedPackagePetitionerApplicant.id)
        packageCopyHelper.buildAnswers(principalBeneficiaryCopiedPackageNameAnswerList);


        when:
        String sectionId = 'Sec_nameForBeneficiary'
        def sectionAnswerList = answerService.fetchAnswers(sourcePackage.id, principalApplicant.id)
        SectionNodeInstance petitionerSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(sourcePackage.id, principalApplicant.id, sectionId, sectionAnswerList)

        then:
        assertNotNull(sourcePackage)
        assertNotNull(sourcePackage.petitioner)
        assertNotNull(sourcePackage.petitioner.applicant)
        assertNotNull(principalBeneficiaryCopiedPackage)
        assertNotNull(principalBeneficiaryCopiedPackage.petitioner)
        assertNotNull(principalBeneficiaryCopiedPackage.petitioner.applicant)
        assertBeneficiary2PetitionerNameAnswers(petitionerSectionInstance)

        cleanup:
        testHelper.deletePackageOnly(true, false)
        packageCopyHelper.clean()
    }


    private void assertPetitioner2PetitionerNameAnswers(SectionNodeInstance petitionerSectionInstance) {
        List<EasyVisaNodeInstance> petitionerSubsectionInstanceList = petitionerSectionInstance.getChildren()
        assertEquals(2, petitionerSubsectionInstanceList.size())
        assertPetitioner2PetitionerSubSectionInstance1_Answers(petitionerSubsectionInstanceList)
    }

    private void assertPetitioner2PetitionerSubSectionInstance1_Answers(List<EasyVisaNodeInstance> petitionerSubsectionInstanceList) {
        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) petitionerSubsectionInstanceList.get(0)
        assertEquals('SubSec_5', subsection1Instance.getId())
        List<EasyVisaNodeInstance> subsectionChildren = subsection1Instance.getChildren()
        assertEquals(3, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(3, questionNodeInstanceList.size())

        QuestionNodeInstance firstNameQuestion = questionNodeInstanceList.get(0)
        assertEquals('firstName', firstNameQuestion.getName())
        Answer firstNameAnswer = firstNameQuestion.getAnswer()
        assertEquals('John', firstNameAnswer.getValue())
        assertEquals('Sec_2/SubSec_5/Q_32', firstNameAnswer.getPath())

        QuestionNodeInstance middleNameQuestion = questionNodeInstanceList.get(1)
        assertEquals('middleName', middleNameQuestion.getName())
        Answer middleNameAnswer = middleNameQuestion.getAnswer()
        assertEquals('Watson', middleNameAnswer.getValue())
        assertEquals('Sec_2/SubSec_5/Q_33', middleNameAnswer.getPath())

        QuestionNodeInstance familyNameQuestion = questionNodeInstanceList.get(2)
        assertEquals('familyName', familyNameQuestion.getName())
        Answer familyNameAnswer = familyNameQuestion.getAnswer()
        assertEquals('Alexa', familyNameAnswer.getValue())
        assertEquals('Sec_2/SubSec_5/Q_34', familyNameAnswer.getPath())
    }


    private void assertBeneficiary2PetitionerNameAnswers(SectionNodeInstance benefiarySectionInstance) {
        List<EasyVisaNodeInstance> beneficiarySubsectionInstanceList = benefiarySectionInstance.getChildren()
        assertEquals(2, beneficiarySubsectionInstanceList.size())
        assertBeneficiary2PetitionerSubSectionInstance1_Answers(beneficiarySubsectionInstanceList)
    }

    private void assertBeneficiary2PetitionerSubSectionInstance1_Answers(List<EasyVisaNodeInstance> beneficiarySubsectionInstanceList) {
        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) beneficiarySubsectionInstanceList.get(0)
        assertEquals('SubSec_currentLegalNameForBeneficiary', subsection1Instance.getId())
        List<EasyVisaNodeInstance> subsectionChildren = subsection1Instance.getChildren()
        assertEquals(3, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(3, questionNodeInstanceList.size())

        QuestionNodeInstance firstNameQuestion = questionNodeInstanceList.get(0)
        assertEquals('currentLegalNameGivenName', firstNameQuestion.getName())
        Answer firstNameAnswer = firstNameQuestion.getAnswer()
        assertEquals('John', firstNameAnswer.getValue())
        assertEquals('Sec_nameForBeneficiary/SubSec_currentLegalNameForBeneficiary/Q_1901', firstNameAnswer.getPath())

        QuestionNodeInstance middleNameQuestion = questionNodeInstanceList.get(1)
        assertEquals('currentLegalNameMiddleName', middleNameQuestion.getName())
        Answer middleNameAnswer = middleNameQuestion.getAnswer()
        assertEquals('Watson', middleNameAnswer.getValue())
        assertEquals('Sec_nameForBeneficiary/SubSec_currentLegalNameForBeneficiary/Q_1902', middleNameAnswer.getPath())

        QuestionNodeInstance familyNameQuestion = questionNodeInstanceList.get(2)
        assertEquals('currentLegalNameFamilyName', familyNameQuestion.getName())
        Answer familyNameAnswer = familyNameQuestion.getAnswer()
        assertEquals('Alexa', familyNameAnswer.getValue())
        assertEquals('Sec_nameForBeneficiary/SubSec_currentLegalNameForBeneficiary/Q_1903', familyNameAnswer.getPath())
    }
}
