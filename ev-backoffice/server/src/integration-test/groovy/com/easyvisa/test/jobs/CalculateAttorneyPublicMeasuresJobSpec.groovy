package com.easyvisa.test.jobs

import com.easyvisa.*
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.TestMockUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired

@Integration
class CalculateAttorneyPublicMeasuresJobSpec extends TestMockUtils {

    @Autowired
    private AttorneyService attorneyService
    @Autowired
    private PackageService packageService
    @Autowired
    private ProfileService profileService

    private CalculateAttorneyPublicMeasuresJob calculateAttorneyPublicMeasuresJob

    void setup() {
        calculateAttorneyPublicMeasuresJob = new CalculateAttorneyPublicMeasuresJob()
        calculateAttorneyPublicMeasuresJob.attorneyService = attorneyService
    }

    void testCalculate() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildNoPetitionerLeadPackage()
                .buildUsersForPackageApplicants()
                .buildPackageLegalRepReviewByBeneficiary()
                .buildPackageLegalRepReviewByBeneficiary(4)
                .buildPackageLegalRepReviewByBeneficiary(4)
                .buildPackageLegalLicensedRegion()
                .buildPackageLegalRepArticleBonus()
                .buildPackageLegalRepArticleBonus()
                .buildPackageLegalRepArticleBonus(new Date(), Boolean.FALSE)
                .buildNoPackageLegalRep()

        expect:
        runJob()

        LegalRepresentative calculatedAttorney
        LegalRepresentative emptyCalculationAttorney
        LegalRepresentative.withNewTransaction {
            calculatedAttorney = LegalRepresentative.get(testHelper.packageLegalRepresentative.id)
            emptyCalculationAttorney = LegalRepresentative.get(testHelper.legalRepresentativeNoPackage.id)
        }
        assert 0 == calculatedAttorney.publicNoOfReviews
        assert BigDecimal.ZERO == calculatedAttorney.publicAvgReviewRating
        assert 0 == calculatedAttorney.publicNoOfApprovedArticles
        assert 2 == calculatedAttorney.publicMaxYearsLicensed

        assert 0 == emptyCalculationAttorney.publicNoOfReviews
        assert BigDecimal.ZERO == emptyCalculationAttorney.publicAvgReviewRating
        assert 0 == emptyCalculationAttorney.publicNoOfApprovedArticles
        assert 0 == emptyCalculationAttorney.publicMaxYearsLicensed

        cleanup:
        testHelper.deletePackageOnly()
                .deletePackageLegalRep()
                .deleteNoPackageLegalRep()
                .deleteOrganization()
    }

    private void runJob() {
        LegalRepresentative.withNewTransaction {
            calculateAttorneyPublicMeasuresJob.execute()
            Boolean.TRUE
        }
    }

}
