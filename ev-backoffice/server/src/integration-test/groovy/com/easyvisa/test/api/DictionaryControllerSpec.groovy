package com.easyvisa.test.api

import com.easyvisa.AttorneyService
import com.easyvisa.ProfileService
import com.easyvisa.utils.PackageTestBuilder
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
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Integration
class DictionaryControllerSpec extends Specification {

    @Rule
    JUnitRestDocumentation restDocumentation =
            new JUnitRestDocumentation("build/generated-snippets")

    @Value('${local.server.port}')
    Integer serverPort

    @Autowired
    private ProfileService profileService

    @Autowired
    private AttorneyService attorneyService

    protected RequestSpecification spec

    void setup() {
        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(this.restDocumentation)).build()
    }

    def testGetBenefitCategories() {
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('get-benefit-categories',
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath('noPetitionerBenefitGroups').description('Benefit groups for no-petitioner packages'),
                                fieldWithPath('noPetitionerBenefitGroups[].value').description('Benefit group ID (enum value)'),
                                fieldWithPath('noPetitionerBenefitGroups[].label').description('Benefit group display name'),
                                fieldWithPath('noPetitionerBenefitGroups[].shortName').description('Short name'),
                                fieldWithPath('noPetitionerBenefitGroups[].note').description('Additional Note'),
                                subsectionWithPath('benefitGroupsWithDerivatives').description('Benefit groups for usual packages with Derivatives (Petitioner + Principle Beneficiaries + Derivative Beneficiary)'),
                                subsectionWithPath('benefitGroupsNoDerivatives').description('Benefit groups for usual packages with no Derivatives (Petitioner + Beneficiary)'),
                                fieldWithPath('benefitCategories').description('Benefit categories'),
                                subsectionWithPath('searchGroups').description('Benefit categories for displaying on Search Pop Up'),
                                subsectionWithPath('disabledLPRCategories').description('Disabled Primary Beneficiary categories for LPR petitioner'),
                                subsectionWithPath('disabledUSCitizenCategories').description('Disabled Primary Beneficiary categories for US citizen petitioner'),
                                subsectionWithPath('disabledUSDerivativeCategories').description('Disabled Benefit Categories for Derivative Applicants of US Petitioner'),
                                subsectionWithPath('disabledLPRDerivativeCategories').description('Disabled Benefit Categories for Derivative Applicants of LPR Petitioner'),
                                fieldWithPath('benefitCategories[].value').description('Benefit category ID (enum value)'),
                                fieldWithPath('benefitCategories[].label').description('Benefit category abbreviation'),
                                fieldWithPath('benefitCategories[].fullLabel').description('Benefit category display name'),
                                fieldWithPath('benefitCategories[].benefitGroup').description('Benefit group ID (enum value)'),
                                fieldWithPath('benefitCategories[].searchLabel').description('Label to display on search page'),
                                fieldWithPath('benefitCategories[].note').description('Additional note').optional(),
                                fieldWithPath('benefitCategories[].disabled').description('Not active to use flag'))))
                .when()
                .port(this.serverPort)
                .get('/api/benefits')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('noPetitionerBenefitGroups.size()', equalTo(2))
                .body('benefitGroupsWithDerivatives.size()', equalTo(2))
                .body('benefitGroupsNoDerivatives.size()', equalTo(4))
                .body('searchGroups.size()', equalTo(8))
                .body('benefitCategories.size()', equalTo(21))
                .body('disabledLPRCategories.size()', equalTo(14))
                .body('disabledUSCitizenCategories.size()', equalTo(3))
                .body('disabledUSDerivativeCategories.size()', equalTo(2))
                .body('disabledLPRDerivativeCategories.size()', equalTo(5))

        cleanup:
        testHelper.clean()
    }

}
