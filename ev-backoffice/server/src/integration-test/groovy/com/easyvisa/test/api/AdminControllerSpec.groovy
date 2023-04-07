package com.easyvisa.test.api

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.utils.PackageTestBuilder
import grails.gorm.transactions.Rollback
import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import grails.testing.mixin.integration.Integration
import grails.util.Holders
import org.apache.http.HttpStatus
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import spock.lang.Specification

import static io.restassured.RestAssured.given
import static org.hamcrest.CoreMatchers.*
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.*
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Rollback
@Integration
class AdminControllerSpec extends Specification {

    @Rule
    JUnitRestDocumentation restDocumentation =
            new JUnitRestDocumentation("build/generated-snippets")

    @Value('${local.server.port}')
    Integer serverPort

    @Autowired
    ProfileService profileService
    @Autowired
    AttorneyService attorneyService
    @Autowired
    AdminService adminService

    protected RequestSpecification spec

    void setup() {
        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(this.restDocumentation)).build()
    }

    def "test owner can update fee config"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .buildPackageLegalRepEvRoles()
                .logInPackageLegalRep()

        Organization.withNewTransaction {
            testHelper.organization.refresh().easyVisaId = Holders.config.easyvisa.blessedOrganizationEVId
            testHelper.organization.save(failOnError: true)

        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('fee-setting-update',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('signupFee').description('Signup fee'),
                                fieldWithPath('maintenanceFee').description('Maintenance fee'),
                                fieldWithPath('cloudStorageFee').description('Cloud Storage fee'),
                                fieldWithPath('membershipReactivationFee').description('Membership reactivation fee'),
                                fieldWithPath('referralBonus').description('Referral bonus'),
                                fieldWithPath('signupDiscount').description('Signup discount'),
                                fieldWithPath('articleBonus').description('Article bonus'),
                                fieldWithPath('supportEmail').description('Support email'),
                                fieldWithPath('contactPhone').description('Contact phone')
                        ),
                        responseFields(
                                fieldWithPath('signupFee').description('Signup fee'),
                                fieldWithPath('maintenanceFee').description('Maintenance fee'),
                                fieldWithPath('cloudStorageFee').description('Cloud Storage fee'),
                                fieldWithPath('membershipReactivationFee').description('Membership reactivation fee'),
                                fieldWithPath('referralBonus').description('Referral bonus'),
                                fieldWithPath('signupDiscount').description('Signup discount'),
                                fieldWithPath('articleBonus').description('Article bonus'),
                                fieldWithPath('supportEmail').description('Support email'),
                                fieldWithPath('contactPhone').description('Contact phone'),
                        )))
                .body('{ "signupFee": 100,'
                        + ' "maintenanceFee": 200,'
                        + ' "cloudStorageFee": 300,'
                        + ' "membershipReactivationFee": 500,'
                        + ' "referralBonus": 600,'
                        + ' "signupDiscount": 700,'
                        + ' "supportEmail": "support@easyvisa.com",'
                        + ' "contactPhone": "+1-1800-9898",'
                        + ' "articleBonus": 800 }')
                .when()
                .port(this.serverPort)
                .post('/api/admin-config')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        cleanup:
        AdminConfig.withNewTransaction {
            AdminConfig config = adminService.adminSettingsForUpdate.adminConfig
            config.with {
                signupFee = 0
                maintenanceFee = 0
                cloudStorageFee = 0
                membershipReactivationFee = 0
                referralBonus = 0
                signupDiscount = 0
                articleBonus = 0
                contactPhone = ''
                supportEmail = ''
            }
            config.save(failOnError: true)
        }
        testHelper.clean()
    }

    def "Fee config can be accessed without login"() {
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('fee-setting-get',
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath('signupFee').description('Signup fee'),
                                fieldWithPath('maintenanceFee').description('Maintenance fee'),
                                fieldWithPath('cloudStorageFee').description('Cloud storage fee'),
                                fieldWithPath('membershipReactivationFee').description('Membership reactivation fee'),
                                fieldWithPath('referralBonus').description('Referral bonus'),
                                fieldWithPath('signupDiscount').description('Signup discount'),
                                fieldWithPath('articleBonus').description('Article bonus'),
                                fieldWithPath('supportEmail').description('Support email'),
                                fieldWithPath('contactPhone').description('Contact phone'),

                        )))
                .when()
                .port(this.serverPort)
                .get('/api/public/admin-config')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
    }

    def "test owner can update govt fee config"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .buildPackageLegalRepEvRoles()
                .logInPackageLegalRep()

        Organization.withNewTransaction {
            testHelper.organization.refresh().easyVisaId = Holders.config.easyvisa.blessedOrganizationEVId
            testHelper.organization.save(failOnError: true)

        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('gov-fee-update',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('i129f').description('I–129F Petition for Alien Fiancé(e)'),
                                fieldWithPath('i130').description('I-130 Petition for Alien Relative'),
                                fieldWithPath('i360').description('I–360 Petition for Amerasian Widow(er) or Special Immigrant'),
                                fieldWithPath('i485').description('I–485 Application to Register Permanent Residence or Adjust Status'),
                                fieldWithPath('i485_14').description('I-485 Application to Register Permanent Residence or Adjust Status(certain applicants under the age of 14 years)'),
                                fieldWithPath('i600_600a').description('I–600/600A Petition to Classify Orphan as an Immediate Relative/Application for Advance Petition Processing of Orphan Petition'),
                                fieldWithPath('i601').description('I-601 Application for Waiver of Ground of Excludability'),
                                fieldWithPath('i601a').description('I–601A Application for Provisional Unlawful Presence Waiver'),
                                fieldWithPath('i751').description('I–751 Petition to Remove Conditions on Residence'),
                                fieldWithPath('i765').description('I–765 Application for Employment Authorization'),
                                fieldWithPath('n400').description('N–400 Application for Naturalization'),
                                fieldWithPath('n600_n600k').description('N–600/N–600K Application for Certificate of Citizenship'),
                                fieldWithPath('biometricServiceFee').description('Biometric Services Fee')),
                        responseFields(
                                fieldWithPath('i129f').description('I–129F Petition for Alien Fiancé(e)'),
                                fieldWithPath('i130').description('I-130 Petition for Alien Relative'),
                                fieldWithPath('i360').description('I–360 Petition for Amerasian Widow(er) or Special Immigrant'),
                                fieldWithPath('i485').description('I–485 Application to Register Permanent Residence or Adjust Status'),
                                fieldWithPath('i485_14').description('I-485 Application to Register Permanent Residence or Adjust Status(certain applicants under the age of 14 years)'),
                                fieldWithPath('i600_600a').description('I–600/600A Petition to Classify Orphan as an Immediate Relative/Application for Advance Petition Processing of Orphan Petition'),
                                fieldWithPath('i601').description('I-601 Application for Waiver of Ground of Excludability'),
                                fieldWithPath('i601a').description('I–601A Application for Provisional Unlawful Presence Waiver'),
                                fieldWithPath('i751').description('I–751 Petition to Remove Conditions on Residence'),
                                fieldWithPath('i765').description('I–765 Application for Employment Authorization'),
                                fieldWithPath('n400').description('N–400 Application for Naturalization'),
                                fieldWithPath('n600_n600k').description('N–600/N–600K Application for Certificate of Citizenship'),
                                fieldWithPath('biometricServiceFee').description('Biometric Services Fee')
                        )))
                .body('{ "i129f": 100,'
                        + ' "i360": 200,'
                        + ' "i130": 300,'
                        + ' "i485": 400,'
                        + ' "i485_14": 500,'
                        + ' "i600_600a": 600,'
                        + ' "i601": 100,'
                        + ' "i601a": 400,'
                        + ' "i751": 1200,'
                        + ' "n400": 1200,'
                        + ' "n600_n600k": 900,'
                        + ' "biometricServiceFee": 1300,'
                        + ' "i765": 800 }')
                .when()
                .port(this.serverPort)
                .post('/api/admin-config/government-fees')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        cleanup:
        AdminConfig.withNewTransaction {
            AdminConfig config = adminService.adminSettingsForUpdate.adminConfig
            config.with {
                i129f = 0
                i130 = 0
                i360 = 0
                i485 = 0
                i485_14 = 0
                i600_600a = 0
                i601 = 0
                i601a = 0
                i751 = 0
                i765 = 0
                n400 = 0
                n600_n600k = 0
                biometricServiceFee = 0
            }
            config.save(failOnError: true)
        }
        testHelper.clean()
    }

    def testGetGovFee() {
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('gov-fee-get',
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath('i129f').description('I–129F Petition for Alien Fiancé(e)'),
                                fieldWithPath('i130').description('I-130 Petition for Alien Relative'),
                                fieldWithPath('i360').description('I–360 Petition for Amerasian Widow(er) or Special Immigrant'),
                                fieldWithPath('i485').description('I–485 Application to Register Permanent Residence or Adjust Status'),
                                fieldWithPath('i485_14').description('I-485 Application to Register Permanent Residence or Adjust Status(certain applicants under the age of 14 years)'),
                                fieldWithPath('i600_600a').description('I–600/600A Petition to Classify Orphan as an Immediate Relative/Application for Advance Petition Processing of Orphan Petition'),
                                fieldWithPath('i601').description('I-601 Application for Waiver of Ground of Excludability'),
                                fieldWithPath('i601a').description('I–601A Application for Provisional Unlawful Presence Waiver'),
                                fieldWithPath('i751').description('I–751 Petition to Remove Conditions on Residence'),
                                fieldWithPath('i765').description('I–765 Application for Employment Authorization'),
                                fieldWithPath('n400').description('N–400 Application for Naturalization'),
                                fieldWithPath('n600_n600k').description('N–600/N–600K Application for Certificate of Citizenship'),
                                fieldWithPath('biometricServiceFee').description('Biometric Services Fee')
                        )))
                .when()
                .port(this.serverPort)
                .get('/api/public/admin-config/government-fees')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('i129f', notNullValue())
                .body('i130', notNullValue())
                .body('i360', notNullValue())
    }

    def testFeeScheduleUpdate() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .buildPackageLegalRepEvRoles()
                .logInPackageLegalRep()

        Organization.withNewTransaction {
            testHelper.organization.refresh().easyVisaId = Holders.config.easyvisa.blessedOrganizationEVId
            testHelper.organization.save(failOnError: true)

        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('admin-fee-schedule-update',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('feeSchedule').description('Fee schedule to set'),
                                fieldWithPath('feeSchedule[].benefitCategory').description('benefit category, e.g. IR1'),
                                fieldWithPath('feeSchedule[].amount').description('fee to the benefit category')
                        ),
                        responseFields(
                                fieldWithPath('[].benefitCategory').description('Benefit category'),
                                fieldWithPath('[].amount').description('Fee'),
                                fieldWithPath('[].id').description('Id')
                        )))
                .body('''{
"feeSchedule": [{
"benefitCategory": "IR1",
"amount": 100
},{
"benefitCategory": "IR2",
"amount": 200
},{
"benefitCategory": "IR5",
"amount": 300
},{
"benefitCategory": "F1_A",
"amount": 400
},{
"benefitCategory": "F2_A",
"amount": 500
},{
"benefitCategory": "F3_A",
"amount": 600
},{
"benefitCategory": "F4_A",
"amount": 700
},{
"benefitCategory": "F1_B",
"amount": 400
},{
"benefitCategory": "F2_B",
"amount": 500
},{
"benefitCategory": "F3_B",
"amount": 600
},{
"benefitCategory": "F4_B",
"amount": 700
},{
"benefitCategory": "K1K3",
"amount": 800
},{
"benefitCategory": "K2K4",
"amount": 900
},{
"benefitCategory": "NATURALIZATION",
"amount": 1000
},{
"benefitCategory": "LPRSPOUSE",
"amount": 1100
},{
"benefitCategory": "LPRCHILD",
"amount": 1200
},{
"benefitCategory": "SIX01",
"amount": 1300
},{
"benefitCategory": "SIX01A",
"amount": 1400
},{
"benefitCategory": "EAD",
"amount": 1500
},{
"benefitCategory": "DISABILITY",
"amount": 1600
},{
"benefitCategory": "REMOVECOND",
"amount": 1700
}]}''')
                .when()
                .port(this.serverPort)
                .post('/api/admin-config/fee-schedule')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('benefitCategory[0]', is(ImmigrationBenefitCategory.IR1.name()))
                .body('amount[0]', is(100))
                .body('benefitCategory[1]', is(ImmigrationBenefitCategory.IR2.name()))
                .body('amount[1]', is(200))

        cleanup:
        AdminConfig.withNewTransaction {
            adminService.adminSettingsForUpdate.adminConfig.attorney.feeSchedule.each {
                it.amount = 0
                it.save(failOnError: true)
            }
        }
        testHelper.clean()
    }

    def testGetFeeSchedule() {
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('get-admin-fee-schedule',
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath('[].benefitCategory').description('Benefit category'),
                                fieldWithPath('[].amount').description('Fee'),
                                fieldWithPath('[].id').description('Id')
                        )))
                .when()
                .port(this.serverPort)
                .get('/api/public/admin-config/fee-schedule')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('.', isA(List.class))
    }

    def testSendEvAlerts() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .buildPackageLegalRepEvRoles()
                .logInPackageLegalRep()

        Organization.withNewTransaction {
            testHelper.organization.refresh().easyVisaId = Holders.config.easyvisa.blessedOrganizationEVId
            testHelper.organization.save(failOnError: true)

        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('send-ev-alerts',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('subject').description('Email subject'),
                                fieldWithPath('sendTo').description('Group of recipients'),
                                fieldWithPath('source').description('Email source'),
                                fieldWithPath('body').description('Email body')
                        )))
                .body("""{
"subject": "subject",
"sendTo": ["ATTORNEYS"],
"source": "ev_source",
"body": "body"}""")
                .when()
                .port(this.serverPort)
                .post('/api/admin/alerts')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_NO_CONTENT))

        List<Alert> alerts
        Alert alert
        Alert.withNewTransaction {
            LegalRepresentative legalRep = testHelper.packageLegalRepresentative.refresh()
            alerts = Alert.findAllBySource('ev_source')
            alert = alerts.find { it.recipient = legalRep.user }
        }

        assert !alerts.isEmpty()
        assert 'subject' == alert.subject
        assert 'ev_source' == alert.source
        assert 'body' == alert.body

        cleanup:
        testHelper.clean()
    }

}
