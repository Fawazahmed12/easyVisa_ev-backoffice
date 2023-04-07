package com.easyvisa.test.api

import com.easyvisa.AdminConfig
import com.easyvisa.AdminService
import com.easyvisa.AttorneyService
import com.easyvisa.Package
import com.easyvisa.ProfileService
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.TestUtils
import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import grails.testing.mixin.integration.Integration
import org.apache.http.HttpStatus
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import spock.lang.Specification

import static io.restassured.RestAssured.given
import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.CoreMatchers.is
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.*
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Integration
class TaxControllerSpec extends Specification {

    @Rule
    JUnitRestDocumentation restDocumentation =
            new JUnitRestDocumentation("build/generated-snippets")

    @Value('${local.server.port}')
    Integer serverPort

    @Autowired
    private ProfileService profileService
    @Autowired
    private AttorneyService attorneyService
    @Autowired
    private AdminService adminService

    protected RequestSpecification spec

    void setup() {
        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(this.restDocumentation)).build()
    }

    def testGetEstimatedTaxes() {
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort:serverPort,
                                                                 attorneyService:attorneyService,
                                                                 profileService: profileService])
        testHelper.buildPackageLegalRep()
                .buildPaymentMethodPackageLegalRep()
                .logInPackageLegalRep()
        BigDecimal reactivationFee
        Package.withNewTransaction {
            reactivationFee = TestUtils.randomNumber()
            AdminConfig config = adminService.adminSettingsForUpdate.adminConfig
            config.membershipReactivationFee = reactivationFee
            config.save(failOnError: true)
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('get-estimated-taxes',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('type').description('A type of calculation. Possible values: SIGNUP_FEE, MEMBERSHIP_REACTIVATION_FEE, IMMIGRATION_BENEFIT'),
                                fieldWithPath('packageId').description('Package ID. Uses with IMMIGRATION_BENEFIT only').optional(),
                                fieldWithPath('address').description('Billing address. Uses with SIGNUP_FEE only').optional(),
                                fieldWithPath('address.zipCode').description('Zip Code of the address').optional()
                        ),
                        responseFields(
                                fieldWithPath('subTotal').description('EV fee'),
                                fieldWithPath('estTax').description('Estimated taxes to be collected. 0 is possible'),
                                fieldWithPath('grandTotal').description('Total to be charged'),
                                fieldWithPath('credit').description('Credit(bonus) value. Will be 0 or negative'))))
                .when()
                .port(this.serverPort)
                .body("""{"type": "MEMBERSHIP_REACTIVATION_FEE", "packageId": 1, "address": {"zipCode": "02138"}}""")
                .post('/api/taxes')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('subTotal', equalTo(reactivationFee.floatValue()))
                .body('estTax', equalTo(BigDecimal.ZERO.intValue()))
                .body('grandTotal', equalTo(reactivationFee.floatValue()))
                .body('credit', equalTo(BigDecimal.ZERO.intValue()))

        cleanup:
        Package.withNewTransaction {
            AdminConfig config = adminService.adminSettingsForUpdate.adminConfig
            config.membershipReactivationFee = 0
            config.save(failOnError: true)
        }
        testHelper.deletePackageLegalRep()
                .deleteOrganization()
    }

}
