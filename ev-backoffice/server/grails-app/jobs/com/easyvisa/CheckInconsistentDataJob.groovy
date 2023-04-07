package com.easyvisa

class CheckInconsistentDataJob {
    static concurrent = false

    DataInspectionService dataInspectionService

    void execute() {
        log.info('Job: Started checking inconsistent data job')
        dataInspectionService.validateData()
        log.info('Job: Finished checking inconsistent data job')
    }

}
