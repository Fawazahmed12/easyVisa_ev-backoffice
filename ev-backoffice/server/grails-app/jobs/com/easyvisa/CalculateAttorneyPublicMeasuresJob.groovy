package com.easyvisa

class CalculateAttorneyPublicMeasuresJob {
    static concurrent = false

    AttorneyService attorneyService
    void execute() {
        log.info('Job: Started job to calculate attorney public measures')
        Integer total = attorneyService.calculateAttorneyPublicMeasures()
        log.info("Job: Finished job to calculate attorney public measures. Data was recalculated for ${total} attorneys")
    }

}
