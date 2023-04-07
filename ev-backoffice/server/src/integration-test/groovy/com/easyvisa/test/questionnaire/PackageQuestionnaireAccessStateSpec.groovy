package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.TestMockUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

@Integration
class PackageQuestionnaireAccessStateSpec extends TestMockUtils {

    @Autowired
    private PackageQuestionnaireService packageQuestionnaireService
    @Autowired
    private PackageService packageService
    @Autowired
    private AnswerService answerService
    @Autowired
    private AttorneyService attorneyService
    @Autowired
    private PdfPopulationService pdfPopulationService
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


    void testLeanPackageQuestionnaireAccessStateAsAttorney() throws Exception {
        given:
        PackageTestBuilder packageLeadHelper = PackageTestBuilder.init([
                serverPort                 : serverPort,
                adminService               : adminService,
                attorneyService            : attorneyService,
                packageService             : packageService,
                answerService              : answerService,
                packageQuestionnaireService: packageQuestionnaireService,
                profileService : profileService])
        packageLeadHelper.buildPetitionerAndBeneficiaryLeadPackage(true, ImmigrationBenefitCategory.IR1)
                .buildUsersForPackageApplicants()
                .logInPackageLegalRep();
        Package leadPackage = packageLeadHelper.aPackage;
        LegalRepresentative packageLegalRepresentative = packageLeadHelper.packageLegalRepresentative;


        when:
        def questionnaireAccessState = this.packageQuestionnaireService.fetchQuestionnaireAccessState(leadPackage, packageLegalRepresentative.user);

        then:
        assertNotNull(leadPackage)
        assertNotNull(questionnaireAccessState)
        assertEquals(questionnaireAccessState.access, false);
        assertEquals(questionnaireAccessState.readOnly, true);

        cleanup:
        packageLeadHelper.clean()
    }


    void testOpenPackageQuestionnaireAccessStateAsDirectBeneficiary() throws Exception {
        given:
        PackageTestBuilder packageOpenHelper = PackageTestBuilder.init([
                serverPort                 : serverPort,
                adminService               : adminService,
                attorneyService            : attorneyService,
                packageService             : packageService,
                answerService              : answerService,
                packageQuestionnaireService: packageQuestionnaireService,
                profileService : profileService])
        packageOpenHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)
                .buildUsersForPackageApplicants()
                .logInPackageDirectBeneficiary();
        Package openPackage = packageOpenHelper.aPackage;
        Applicant principalBeneficiary = openPackage.principalBeneficiary;

        when:
        def questionnaireAccessState = this.packageQuestionnaireService.fetchQuestionnaireAccessState(openPackage, principalBeneficiary.user);

        then:
        assertNotNull(openPackage)
        assertNotNull(questionnaireAccessState)
        assertEquals(questionnaireAccessState.access, true);
        assertEquals(questionnaireAccessState.readOnly, false);

        cleanup:
        packageOpenHelper.clean()
    }


    void testOpenPackageQuestionnaireAccessStateAsTrainee() throws Exception {
        given:
        PackageTestBuilder packageOpenHelper = PackageTestBuilder.init([
                serverPort                 : serverPort,
                adminService               : adminService,
                attorneyService            : attorneyService,
                packageService             : packageService,
                answerService              : answerService,
                packageQuestionnaireService: packageQuestionnaireService,
                profileService : profileService])
        packageOpenHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)
                .buildUsersForPackageApplicants()
                .buildTrainee()
                .logInTrainee();
        Package openPackage = packageOpenHelper.aPackage;
        Employee trainee = packageOpenHelper.trainee;

        when:
        def questionnaireAccessState = this.packageQuestionnaireService.fetchQuestionnaireAccessState(openPackage, trainee.user);

        then:
        assertNotNull(openPackage)
        assertNotNull(questionnaireAccessState)
        assertEquals(questionnaireAccessState.access, true);
        assertEquals(questionnaireAccessState.readOnly, true);

        cleanup:
        packageOpenHelper.clean()
    }


    void testOpenPackageQuestionnaireAccessStateAsInActiveOrganizationUser() throws Exception {
        given:
        PackageTestBuilder packageOpenHelper = PackageTestBuilder.init([
                serverPort                 : serverPort,
                adminService               : adminService,
                attorneyService            : attorneyService,
                packageService             : packageService,
                answerService              : answerService,
                packageQuestionnaireService: packageQuestionnaireService,
                profileService : profileService])
        packageOpenHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)
                .buildUsersForPackageApplicants()
                .makePackageRepresentativeInactiveInOrg()
                .logInPackageLegalRep();
        Package openPackage = packageOpenHelper.aPackage;
        LegalRepresentative packageLegalRepresentative = packageOpenHelper.packageLegalRepresentative;

        when:
        def questionnaireAccessState = this.packageQuestionnaireService.fetchQuestionnaireAccessState(openPackage, packageLegalRepresentative.user);

        then:
        assertNotNull(openPackage)
        assertNotNull(questionnaireAccessState)
        assertEquals(questionnaireAccessState.access, false);
        assertEquals(questionnaireAccessState.readOnly, true);

        cleanup:
        packageOpenHelper.clean()
    }
}
