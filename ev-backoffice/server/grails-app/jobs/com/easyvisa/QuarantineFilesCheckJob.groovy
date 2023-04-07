package com.easyvisa

class QuarantineFilesCheckJob {
    static concurrent = false

    FileService fileService

    void execute() {
        log.info('Job: Started quarantine files check job')
        fileService.deleteOutdatedQuarantineFiles()
        log.info("Job: Finished quarantine files  check job")
    }

}
