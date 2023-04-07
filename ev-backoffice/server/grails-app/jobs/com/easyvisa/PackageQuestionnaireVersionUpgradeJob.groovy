package com.easyvisa

class PackageQuestionnaireVersionUpgradeJob {

    PackageQuestionnaireVersionService packageQuestionnaireVersionService

    def execute() {
        log.info('Job: Started Package Questionnaire Version Upgrade job')
        packageQuestionnaireVersionService.upgradePackageQuestionnaireVersionIfNeeded()
        log.info('Job: Finished Package Questionnaire Version Upgrade job')
    }
}
