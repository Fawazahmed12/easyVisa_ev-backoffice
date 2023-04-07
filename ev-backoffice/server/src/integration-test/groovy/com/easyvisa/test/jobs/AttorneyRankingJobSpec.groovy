package com.easyvisa.test.jobs

import com.easyvisa.*
import com.easyvisa.utils.PackageTestBuilder
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import spock.lang.Specification

@Integration
class AttorneyRankingJobSpec extends Specification {

    @Value('${easyvisa.attorney.rank.base.pointsLifetime}')
    Integer basePointsLifetime
    @Value('${easyvisa.attorney.rank.base.points30}')
    Integer basePoints30
    @Value('${easyvisa.attorney.rank.base.points31To90}')
    Integer basePoints31To90
    @Value('${easyvisa.attorney.rank.base.points91To180}')
    Integer basePoints91To180
    @Value('${easyvisa.attorney.rank.recent.pointsLifetime}')
    Integer recentPointsLifetime
    @Value('${easyvisa.attorney.rank.recent.points30}')
    Integer recentPoints30
    @Value('${easyvisa.attorney.rank.recent.points31To90}')
    Integer recentPoints31To90
    @Value('${easyvisa.attorney.rank.recent.points91To180}')
    Integer recentPoints91To180
    @Value('${easyvisa.attorney.rank.top.pointsLifetime}')
    Integer topPointsLifetime
    @Value('${easyvisa.attorney.rank.top.points30}')
    Integer topPoints30
    @Value('${easyvisa.attorney.rank.top.points31To90}')
    Integer topPoints31To90
    @Value('${easyvisa.attorney.rank.top.points91To180}')
    Integer topPoints91To180

    @Autowired
    private AttorneyService attorneyService
    @Autowired
    private OrganizationService organizationService

    @Autowired
    private ProfileService profileService

    private AttorneyRankingJob rankingJob

    void setup() {
        rankingJob = new AttorneyRankingJob()
        rankingJob.attorneyService = attorneyService
        rankingJob.basePointsLifetime = basePointsLifetime
        rankingJob.basePoints30 = basePoints30
        rankingJob.basePoints31To90 = basePoints31To90
        rankingJob.basePoints91To180 = basePoints91To180
        rankingJob.recentPointsLifetime = recentPointsLifetime
        rankingJob.recentPoints30 = recentPoints30
        rankingJob.recentPoints31To90 = recentPoints31To90
        rankingJob.recentPoints91To180 = recentPoints91To180
        rankingJob.topPointsLifetime = topPointsLifetime
        rankingJob.topPoints30 = topPoints30
        rankingJob.topPoints31To90 = topPoints31To90
        rankingJob.topPoints91To180 = topPoints91To180
    }

    void testRankingJob() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([attorneyService    : attorneyService,
                                                                 organizationService: organizationService,
                                                                 profileService : profileService])
        Calendar cal35 = Calendar.getInstance()
        cal35.add(Calendar.DAY_OF_MONTH, -35)
        Calendar cal100 = Calendar.getInstance()
        cal100.add(Calendar.DAY_OF_MONTH, -100)
        testHelper.buildPackageLegalRep()
                .buildNoPackageLegalRep()
                .buildPackageLegalRepArticleBonus()
                .buildPackageLegalRepArticleBonus(cal35.time)
                .buildPackageLegalRepArticleBonus(cal100.time)
                .buildPackageLegalRepArticleBonus(new Date(), Boolean.FALSE)

        expect:
        runJob()

        LegalRepresentative attorney
        LegalRepresentative attorney1
        LegalRepresentative.withNewTransaction {
            attorney = testHelper.packageLegalRepresentative.refresh()
            attorney1 = testHelper.legalRepresentativeNoPackage.refresh()
        }

        assert 475 == attorney.topContributorScore
        assert 750 == attorney.baseContributorScore
        assert 480 == attorney.recentContributorScore
        assert [0f, 1f].contains(attorney.randomScore)

        assert 0 == attorney1.topContributorScore
        assert 0 == attorney1.baseContributorScore
        assert 0 == attorney1.recentContributorScore
        assert [0f, 1f].contains(attorney1.randomScore)

        cleanup:
        testHelper.deletePackageLegalRep()
                .deleteNoPackageLegalRep()
                .deleteOrganization()
    }

    private void runJob() {
        AccountTransaction.withNewTransaction {
            rankingJob.execute()
            Boolean.TRUE
        }
    }

}
