package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.PackageStatus
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.SectionNodeInstance
import com.easyvisa.questionnaire.answering.SubSectionNodeInstance
import com.easyvisa.utils.AnswerListStub
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.QuestionnaireTestDBSetupUtility
import com.easyvisa.utils.TestMockUtils
import com.easyvisa.utils.TestUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import java.util.stream.Collectors

import static org.junit.Assert.*

@Integration
class InAdmissibilityWarningSpec extends TestMockUtils {

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

    void testInadmissibilityAndOtherLegalIssueQuestionnaire() throws Exception {
        given:
        String sectionId = 'Sec_inadmissibilityAndOtherLegalIssues'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.principalBeneficiary
        List<Answer>  answerList = AnswerListStub.inadmissibilityAndOtherLegalIssueAnswerList(aPackage.id, applicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id, sectionId,
                answerList)

        when:
        SectionNodeInstance sectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id, applicant.id,
                        sectionId, answerList)
        assertNotNull(sectionInstance)
        sectionInstance.sortChildren()

        then:
        assertInadmissibilityAndOtherLegalIssueAnswers(testHelper, sectionInstance)

        cleanup:
        TestUtils.delayCurrentThread()
        testHelper.clean()
    }

    private void assertInadmissibilityAndOtherLegalIssueAnswers(PackageTestBuilder testHelper, SectionNodeInstance inadmissibilityAndOtherLegalIssueSectionInstance) {
        List<EasyVisaNodeInstance> inadmissibilityAndOtherLegalIssueSectionInstanceList = inadmissibilityAndOtherLegalIssueSectionInstance.getChildren()
        assertEquals(6, inadmissibilityAndOtherLegalIssueSectionInstanceList.size())

        List<EasyVisaNodeInstance> visibleInadmissibilityAndOtherLegalIssueSectionInstanceList = inadmissibilityAndOtherLegalIssueSectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())
        assertEquals(6, visibleInadmissibilityAndOtherLegalIssueSectionInstanceList.size())

        assertInadmissibilityAndOtherLegalIssueSubSectionInstanceAnswers(testHelper, visibleInadmissibilityAndOtherLegalIssueSectionInstanceList)
    }

    private void assertInadmissibilityAndOtherLegalIssueSubSectionInstanceAnswers(PackageTestBuilder testHelper,
                                                                                  List<EasyVisaNodeInstance> inadmissibilityAndOtherLegalIssueSectionInstanceList) {
        SubSectionNodeInstance immigrationHistoryGeneralInstance = (SubSectionNodeInstance) inadmissibilityAndOtherLegalIssueSectionInstanceList[1]
        assertEquals('SubSec_immigrationHistoryGeneral', immigrationHistoryGeneralInstance.getId())
        List<EasyVisaNodeInstance> subsectionChildren = immigrationHistoryGeneralInstance.getChildren()
        assertEquals(15, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(15, questionNodeInstanceList.size())

        QuestionNodeInstance everBeenAMemberInAnyPartsOfUSQuestion = questionNodeInstanceList[0]
        assertEquals('immigrationHistoryGeneral_everBeenAMemberInAnyPartsOfUS', everBeenAMemberInAnyPartsOfUSQuestion.getName())
        Answer everBeenAMemberInAnyPartsOfUSAnswer = everBeenAMemberInAnyPartsOfUSQuestion.getAnswer()
        assertEquals('No', everBeenAMemberInAnyPartsOfUSAnswer.getValue())
        assertEquals('Sec_inadmissibilityAndOtherLegalIssues/SubSec_immigrationHistoryGeneral/Q_3029', everBeenAMemberInAnyPartsOfUSAnswer.getPath())

        QuestionNodeInstance everBeenDeniedAdmissionToUSQuestion = questionNodeInstanceList[1]
        assertEquals('immigrationHistoryGeneral_everBeenDeniedAdmissionToUS', everBeenDeniedAdmissionToUSQuestion.getName())
        Answer everBeenDeniedAdmissionToUSAnswer = everBeenDeniedAdmissionToUSQuestion.getAnswer()
        assertEquals('Yes', everBeenDeniedAdmissionToUSAnswer.getValue())
        assertEquals('Sec_inadmissibilityAndOtherLegalIssues/SubSec_immigrationHistoryGeneral/Q_3038', everBeenDeniedAdmissionToUSAnswer.getPath())

        QuestionNodeInstance everBeenDeniedVisaToUSQuestion = questionNodeInstanceList[2]
        assertEquals('immigrationHistoryGeneral_everBeenDeniedVisaToUS', everBeenDeniedVisaToUSQuestion.getName())
        Answer everBeenDeniedVisaToUSAnswer = everBeenDeniedVisaToUSQuestion.getAnswer()
        assertEquals('Yes', everBeenDeniedVisaToUSAnswer.getValue())
        assertEquals('Sec_inadmissibilityAndOtherLegalIssues/SubSec_immigrationHistoryGeneral/Q_3040', everBeenDeniedVisaToUSAnswer.getPath())

//        TestUtils.delayCurrentThread(40000)
//        Warning.withNewTransaction {
//            Package aPackage = testHelper.aPackage
//            Applicant applicant = aPackage.principalBeneficiary
//            assertNotEquals(aPackage.status, PackageStatus.BLOCKED)
//            assertEquals(2, Warning.countByAPackageAndApplicant(aPackage, applicant))
//
//            Warning everBeenDeniedAdmissionToUSDocumentAction = Warning.findByQuestionId(everBeenDeniedAdmissionToUSQuestion.getId())
//            assertEquals(getWarningMessage('Have you EVER been denied admission to the United States?'), everBeenDeniedAdmissionToUSDocumentAction.body)
//
//            Warning everBeenDeniedVisaToUSDocumentAction = Warning.findByQuestionId(everBeenDeniedVisaToUSQuestion.getId())
//            assertEquals(getWarningMessage('Have you EVER been denied a visa to the United States? '), everBeenDeniedVisaToUSDocumentAction.body)
//        }
    }

    private String getWarningMessage(String question) {
        """<div>
    <p>
        Your client petitioner-first petitioner-last answered "Yes" to the question "<font color="red"><b><i>${question}</i></b></font>".
    </p>

    <p>
        Here is your client's contact information:<br/>
        Daytime phone number: None<br/>
        Mobile phone number: None
    </p>

    <p>
        Best regards,<br/>
        The EasyVisa Team
    </p>
</div>
"""
    }

}
