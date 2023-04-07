package com.easyvisa.test.questionnaire

import com.easyvisa.AdminService
import com.easyvisa.AnswerService
import com.easyvisa.Applicant
import com.easyvisa.AttorneyService
import com.easyvisa.PackageQuestionnaireService
import com.easyvisa.PackageService
import com.easyvisa.PaymentService
import com.easyvisa.ProfileService
import com.easyvisa.TaxService
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.SectionNodeInstance
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
class AddressHistoryBeneficiarySpec extends TestMockUtils {

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

    void testAddressHistoryBeneficiaryQuestionnaire() throws Exception {
        given:
        String sectionId = "Sec_addressHistoryForBeneficiary"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Long packageId = testHelper.aPackage.id
        Applicant applicant = testHelper.aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListStub.addressHistoryBeneficaryAnswerList(packageId, applicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, packageId, applicant.id, sectionId,
                answerList)

        when:
        def sectionAnswerList = answerService.fetchAnswers(packageId, applicant.id)
        SectionNodeInstance addressHistorySectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(packageId, applicant.id, sectionId,
                        sectionAnswerList)

        then:
        assertNotNull(addressHistorySectionInstance)
        assertAddressHistoryAnswers(addressHistorySectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertAddressHistoryAnswers(SectionNodeInstance addressHistorySectionInstance) {
        List<EasyVisaNodeInstance> addressHistorySectionInstanceList = addressHistorySectionInstance.getChildren()
        assertEquals(5, addressHistorySectionInstanceList.size())

        List<EasyVisaNodeInstance> visibleAddressHistorySectionInstanceList = addressHistorySectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())
        assertEquals(5, visibleAddressHistorySectionInstanceList.size())

        assertEquals("SubSec_currentPhysicalAddressForBeneficiary", visibleAddressHistorySectionInstanceList[0].id)
        assertEquals("Current Physical Address", visibleAddressHistorySectionInstanceList[0].displayText)

        assertEquals("SubSec_previousPhysicalAddressForBeneficiary", visibleAddressHistorySectionInstanceList[1].id)
        assertEquals("Previous Physical Addresses", visibleAddressHistorySectionInstanceList[1].displayText)

        assertEquals("SubSec_currentMailingAddressForBeneficiary", visibleAddressHistorySectionInstanceList[2].id)
        assertEquals("Current Mailing Address", visibleAddressHistorySectionInstanceList[2].displayText)

        assertEquals("SubSec_addressWhereYouIntendToLiveInUSForBeneficiary", visibleAddressHistorySectionInstanceList[3].id)
        assertEquals("Address Where You Intend to Live in U.S.", visibleAddressHistorySectionInstanceList[3].displayText)

        assertEquals("SubSec_physicalAddressAbroadForBeneficiary", visibleAddressHistorySectionInstanceList[4].id)
        assertEquals("Physical Address Abroad", visibleAddressHistorySectionInstanceList[4].displayText)
    }
}
