package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.SectionNodeInstance
import com.easyvisa.questionnaire.answering.SubSectionNodeInstance
import com.easyvisa.questionnaire.dto.*
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
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

@Integration
class AutoSyncApplicantDOBRuleSpec extends TestMockUtils {

    @Autowired
    private PackageQuestionnaireService packageQuestionnaireService
    @Autowired
    private PackageService packageService
    @Autowired
    private AnswerService answerService
    @Autowired
    private ApplicantService applicantService
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

    void testPetitionerAutoSyncDOBRule() throws Exception {
        given:
        String sectionId = "Sec_birthInformation"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)
                .buildUsersForPackageApplicants()

        Package aPackage = testHelper.aPackage
        Petitioner petitioner = aPackage.petitioner
        Applicant petitionerApplicant = petitioner.applicant

        def answerList = AnswerListStub.petitionerBirthInformationAnswerList(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def sectionAnswerList = answerService.fetchAnswers(aPackage.id, petitionerApplicant.id)
        SectionNodeInstance birthInformationSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id,
                        petitionerApplicant.id, sectionId, sectionAnswerList)

        then:
        assertNotNull(birthInformationSectionInstance)
        assertPetitionerBirthInformationAnswers(birthInformationSectionInstance, petitioner)

        cleanup:
        testHelper.clean()
    }

    private void assertPetitionerBirthInformationAnswers(SectionNodeInstance birthInformationSectionInstance, Petitioner petitioner) {
        List<EasyVisaNodeInstance> birthInformationSectionInstanceList = birthInformationSectionInstance.getChildren()
        assertEquals(1, birthInformationSectionInstanceList.size())

        List<EasyVisaNodeInstance> visibleBirthInformationSectionInstanceList = birthInformationSectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())
        assertEquals(1, visibleBirthInformationSectionInstanceList.size())

        assertPetitionerBirthInformationSubSectionInstanceAnswers((SubSectionNodeInstance) visibleBirthInformationSectionInstanceList[0], petitioner)
    }

    private void assertPetitionerBirthInformationSubSectionInstanceAnswers(SubSectionNodeInstance birthInformationSubSectionInstance, Petitioner petitioner) {
        List<EasyVisaNodeInstance> subsectionChildren = birthInformationSubSectionInstance.getChildren()
        assertEquals(4, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility() })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(4, questionNodeInstanceList.size())

        QuestionNodeInstance dateofBirthQuestion = questionNodeInstanceList[1]
        assertEquals("dateofBirth", dateofBirthQuestion.getName())
        Answer dateofBirthAnswer = dateofBirthQuestion.getAnswer()
        assertEquals(DateUtil.fromDate(LocalDate.of(1988, Month.FEBRUARY, 11)), dateofBirthAnswer.getValue())
        assertEquals("Sec_birthInformation/SubSec_birthInformation/Q_88", dateofBirthAnswer.getPath())

        assertNotNull(petitioner)
        Profile profile = petitioner.profile
        assertNotNull(profile)
        Applicant petitionerApplicant = this.applicantService.findApplicant(profile.email)
        assertNotNull(petitionerApplicant)
        Date dateOfBirth = petitionerApplicant.dateOfBirth
        assertNotNull(dateOfBirth)
        assertEquals(DateUtil.fromDate(dateOfBirth), dateofBirthAnswer.getValue())
    }

    void testBeneficiaryAutoSyncDOBRule() throws Exception {
        given:
        String sectionId = "Sec_birthInformationForBeneficiary"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)
                .buildUsersForPackageApplicants()

        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListStub.beneficiaryBirthInformationAnswerList(aPackage.id, applicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id, sectionId,
                answerList)

        when:
        List<Answer> sectionAnswerList = answerService.fetchAnswers(aPackage.id, applicant.id)
        SectionNodeInstance birthInformationSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id,
                        sectionId, sectionAnswerList)

        then:
        assertNotNull(birthInformationSectionInstance)
        assertBeneficiaryBirthInformationAnswers(birthInformationSectionInstance, applicant)

        cleanup:
        testHelper.clean()
    }

    private void assertBeneficiaryBirthInformationAnswers(SectionNodeInstance birthInformationSectionInstance, Applicant applicant) {
        List<EasyVisaNodeInstance> birthInformationSectionInstanceList = birthInformationSectionInstance.getChildren()
        assertEquals(1, birthInformationSectionInstanceList.size())

        List<EasyVisaNodeInstance> visibleBirthInformationSectionInstanceList = birthInformationSectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())
        assertEquals(1, visibleBirthInformationSectionInstanceList.size())

        assertBeneficiaryBirthInformationSubSectionInstanceAnswers((SubSectionNodeInstance) visibleBirthInformationSectionInstanceList[0], applicant)
    }


    private void assertBeneficiaryBirthInformationSubSectionInstanceAnswers(SubSectionNodeInstance birthInformationSubSectionInstance, Applicant applicant) {
        List<EasyVisaNodeInstance> subsectionChildren = birthInformationSubSectionInstance.getChildren()
        assertEquals(4, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility() })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(4, questionNodeInstanceList.size())

        QuestionNodeInstance dateofBirthQuestion = questionNodeInstanceList[1]
        assertEquals("dateofBirth", dateofBirthQuestion.getName())
        Answer dateofBirthAnswer = dateofBirthQuestion.getAnswer()
        assertEquals(DateUtil.fromDate(LocalDate.of(2000, Month.JANUARY, 17)), dateofBirthAnswer.getValue())
        assertEquals("Sec_birthInformationForBeneficiary/SubSec_birthInformationForBeneficiary/Q_2202", dateofBirthAnswer.getPath())

        assertNotNull(applicant)
        Profile profile = applicant.profile
        assertNotNull(profile)
        Applicant benficiaryApplicant = applicantService.findApplicant(profile.email)
        assertNotNull(benficiaryApplicant)
        Date dateOfBirth = benficiaryApplicant.dateOfBirth
        assertNotNull(dateOfBirth)
        assertEquals(DateUtil.fromDate(dateOfBirth), dateofBirthAnswer.getValue())
    }

    void testPetitionerDateOfBirthAttributeRule() throws Exception {
        given:
        String sectionId = "Sec_birthInformation"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)
                .buildUsersForPackageApplicants()

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListStub.petitionerBirthInformationAnswerList(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        FieldItemDto fieldItemDto =
                packageQuestionnaireService.fetchFormlyQuestionnaire(aPackage.id, petitionerApplicant.id, sectionId)

        then:
        assertNotNull(fieldItemDto)
        assertPetitionerBirthInformationFieldItems(fieldItemDto)

        cleanup:
        testHelper.clean()
    }

    private void assertPetitionerBirthInformationFieldItems(FieldItemDto sectionFieldItemDto) {
        assertEquals(sectionFieldItemDto.getFieldId(), "Sec_birthInformation")

        List<IFieldGroup> subSectionFieldGroups = sectionFieldItemDto.getFieldGroups()
        assertEquals(1, subSectionFieldGroups.size())

        List<FieldItemDto> subSectionFieldItemDtoList = subSectionFieldGroups.stream()
                .filter({ fieldGroup -> (fieldGroup instanceof FieldItemDto) })
                .map({ fieldGroup -> (FieldItemDto) fieldGroup })
                .collect(Collectors.toList())
        assertEquals(1, subSectionFieldItemDtoList.size())

        FieldItemDto subSectionFieldItemDto = subSectionFieldItemDtoList[0]
        assertEquals(subSectionFieldItemDto.getFieldId(), "SubSec_birthInformation")
        assertEquals(6, subSectionFieldItemDto.getFieldGroups().size())

        List<HeaderTextDto> headerTextDtoList = subSectionFieldItemDto.getFieldGroups().stream()
                .filter({ fieldGroup -> (fieldGroup instanceof HeaderTextDto) })
                .map({ fieldGroup -> (HeaderTextDto) fieldGroup })
                .collect(Collectors.toList())
        assertEquals(1, headerTextDtoList.size())
        assertEquals(headerTextDtoList[0].getFieldId(), "SubSec_birthInformation")

        List<QuestionItemDto> questionItemDtoList = subSectionFieldItemDto.getFieldGroups().stream()
                .filter({ fieldGroup -> (fieldGroup instanceof QuestionItemDto) })
                .map({ fieldGroup -> (QuestionItemDto) fieldGroup })
                .collect(Collectors.toList())
        assertEquals(5, questionItemDtoList.size())

        QuestionItemDto dateOfBirthQuestionDto = questionItemDtoList[1]
        assertEquals("dateofBirth", dateOfBirthQuestionDto.getKey())
        TemplateOption templateOption = dateOfBirthQuestionDto.getTemplateOption()
        assertNotNull(templateOption)
        Map templateOptionAttributes = templateOption.getAttributes()
        assertNotNull(templateOptionAttributes)

        LocalDate today = LocalDate.now()
        LocalDate minimumLocalDate = today.minus(120, ChronoUnit.YEARS)
        LocalDate maximumLocalDate = today.minus(18, ChronoUnit.YEARS)

        String minimumDate = templateOptionAttributes.get(TemplateOptionAttributes.MINIMUMDATE.getValue())
        assertNotNull(minimumDate)
        assertEquals(DateUtil.fromDate(minimumLocalDate), minimumDate)

        String maximumDate = templateOptionAttributes.get(TemplateOptionAttributes.MAXIMUMDATE.getValue())
        assertNotNull(maximumDate)
        assertEquals(DateUtil.fromDate(maximumLocalDate), maximumDate)
    }
}
