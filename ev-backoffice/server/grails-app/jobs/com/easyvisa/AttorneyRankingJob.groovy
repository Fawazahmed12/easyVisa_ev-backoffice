package com.easyvisa

import static com.easyvisa.AttorneyService.BASE
import static com.easyvisa.AttorneyService.RECENT
import static com.easyvisa.AttorneyService.TOP

import com.easyvisa.job.RankScore

import org.springframework.beans.factory.annotation.Value

class AttorneyRankingJob {
    static concurrent = false

    private final static int MAX_PAGE_DB_SIZE = 10000

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

    AttorneyService attorneyService

    void execute() {
        log.info('Job: Started nightly job for ranking attorneys')
        attorneyService.cleanAttorneyRanking()
        Map<String, RankScore> ranks = ranks
        Integer offset = 0
        Integer total = 0
        List<Long> attorneys = getAttorneys(offset)
        //getting attorneys in pagination way, due to there can be a lot of attorneys in the db
        while (attorneys) {
            total += attorneys.size()
            attorneys.each {
                attorneyService.calcRank(it, ranks)
            }
            offset += MAX_PAGE_DB_SIZE
            attorneys = getAttorneys(offset)
        }
        log.info("Job: Finished nightly job for ranking attorneys. Touched attorneys = ${total}")
    }

    private Map<String, RankScore> getRanks() {
        [(BASE):new RankScore(pointsLifetime:basePointsLifetime, points30:basePoints30, points31To90:basePoints31To90, points91To180:basePoints91To180),
         (RECENT):new RankScore(pointsLifetime:recentPointsLifetime, points30:recentPoints30, points31To90:recentPoints31To90, points91To180:recentPoints91To180),
         (TOP):new RankScore(pointsLifetime:topPointsLifetime, points30:topPoints30, points31To90:topPoints31To90, points91To180:topPoints91To180)]
    }

    private List<Long> getAttorneys(Integer offset) {
        LegalRepresentative.createCriteria().list {
            createAlias('profile', 'p')
            projections {
                property('id')
            }
            isNotNull('p.user')
            firstResult(offset)
            maxResults(MAX_PAGE_DB_SIZE)
        } as List<Long>
    }

}
