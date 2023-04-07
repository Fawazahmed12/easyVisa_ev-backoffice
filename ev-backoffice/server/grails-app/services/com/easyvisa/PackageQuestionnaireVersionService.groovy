package com.easyvisa

import com.easyvisa.enums.DisplayTextLanguage
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.PackageStatus
import com.easyvisa.questionnaire.PackageQuestionnaireVersion
import com.easyvisa.questionnaire.QuestionnaireVersion
import com.easyvisa.questionnaire.util.DateUtil
import grails.compiler.GrailsCompileStatic
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import groovy.transform.TypeCheckingMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.time.LocalDate

@Service
@GrailsCompileStatic
    class PackageQuestionnaireVersionService {

    AsyncService asyncService

    @Autowired
    PackageQuestionnaireService packageQuestionnaireService

    @Autowired
    SectionCompletionStatusService sectionCompletionStatusService

    @Transactional
    void createNewVersion(Package aPackage) {
        QuestionnaireVersion latestQuestionnaireVersion = this.getLatestQuestionnaireVersion()
        this.upgradeToLatestQuestionnaireVersion(aPackage, latestQuestionnaireVersion)
    }

    void upgradePackageToLatestQuestionnaireVersion(Package aPackage, QuestionnaireVersion latestQuestionnaireVersion) {
        def packageSections = this.packageQuestionnaireService.fetchPackageSections(aPackage.id)
        this.upgradeToLatestQuestionnaireVersion(aPackage, latestQuestionnaireVersion)
        this.packageQuestionnaireService.addDefaultQuestionnaireVersionAnswers(aPackage, latestQuestionnaireVersion)
        sectionCompletionStatusService.updateSectionCompletionStatusAndRemoveZombieAnswers(aPackage, packageSections)
    }


    @Transactional
    QuestionnaireVersion getLatestQuestionnaireVersion() {
        List<QuestionnaireVersion> questionnaireVersionList = QuestionnaireVersion.findAll([max: 1, sort: 'dateCreated', order: 'desc'])
        QuestionnaireVersion latestQuestionnaireVersion = questionnaireVersionList.size() ? questionnaireVersionList.first() : null
        return latestQuestionnaireVersion
    }


    @Transactional
    private void upgradeToLatestQuestionnaireVersion(Package aPackage, QuestionnaireVersion latestQuestionnaireVersion) {
        PackageQuestionnaireVersion.findAllByAPackageAndLatest(aPackage, true)
                .each {
                    PackageQuestionnaireVersion packageQuestionnaireVersion = it as PackageQuestionnaireVersion
                    packageQuestionnaireVersion.latest = false
                    packageQuestionnaireVersion.save(failOnError: true, flush: true)
                }

        PackageQuestionnaireVersion packageQuestionnaireVersion = new PackageQuestionnaireVersion(
                aPackage: aPackage, questionnaireVersion: latestQuestionnaireVersion, latest: true)
        packageQuestionnaireVersion.save(failOnError: true, flush: true)
    }


    @Transactional
    PackageQuestionnaireVersion findLatestByPackage(Package aPackage) {
        PackageQuestionnaireVersion latestPackageQuestionnaireVersion = PackageQuestionnaireVersion.findByAPackageAndLatest(aPackage, true)
        return latestPackageQuestionnaireVersion
    }

    void upgradePackageQuestionnaireVersionIfNeeded() {
        QuestionnaireVersion questionnaireVersion = this.getLatestQuestionnaireVersion()
        log.info("Updrading all packages's questionnaire version to : ${questionnaireVersion.questVersion}")
        List<PackageQuestionnaireVersion> packageQuestionnaireVersionList = PackageQuestionnaireVersion.findAllByQuestionnaireVersionAndLatest(questionnaireVersion, true)
        Date packageCreatedDate = this.packageCreationStartDate()
        List<Package> packageList = Package.findAllByDateCreatedGreaterThanAndStatusInList(packageCreatedDate, [PackageStatus.OPEN, PackageStatus.BLOCKED])
        packageList.each { aPackage ->
            PackageQuestionnaireVersion packageQuestionnaireVersion = packageQuestionnaireVersionList.find { it.aPackage == aPackage && it.latest }
            if (!packageQuestionnaireVersion) {
                log.info("Updrading aPackage: ${aPackage.id} to the questVersion: " + questionnaireVersion.questVersion)
                this.upgradePackageToLatestQuestionnaireVersion(aPackage, questionnaireVersion)
            }
        }
    }


    @GrailsCompileStatic(TypeCheckingMode.SKIP)
    void updateCompletionStatusOfAllPackages(ImmigrationBenefitCategory immigrationBenefitCategory){
        QuestionnaireVersion questionnaireVersion = this.getLatestQuestionnaireVersion()
       asyncService.runAsync({
           updateSectionCompletionStatusOfLatestPackageQuestionnaireVersion(immigrationBenefitCategory, questionnaireVersion)
       }, "Update SectionCompletion Status of all the Packages in the QuestVersion [${questionnaireVersion.questVersion}] which has the benefitCategory: ${immigrationBenefitCategory.easyVisaId}.")
    }


    @GrailsCompileStatic(TypeCheckingMode.SKIP)
    private void updateSectionCompletionStatusOfLatestPackageQuestionnaireVersion(ImmigrationBenefitCategory immigrationBenefitCategory, QuestionnaireVersion questionnaireVersion){
        List<Package> packages = PackageQuestionnaireVersion.findAllByQuestionnaireVersion(questionnaireVersion)
                .collect { packageQuestionnaireVersion -> packageQuestionnaireVersion.aPackage }
                .findAll { Package aPackage -> (aPackage.getDirectBenefit().category == immigrationBenefitCategory) }
        packages.each { Package aPackage ->
            def packageSections = this.packageQuestionnaireService.fetchPackageSections(aPackage.id)
            sectionCompletionStatusService.updateSectionCompletionStatusAndRemoveZombieAnswers(aPackage, packageSections)
            this.packageQuestionnaireService.answerSaveCompletionHandler([:], aPackage.id, LocalDate.now(), DisplayTextLanguage.EN)
        }
    }
    
    @GrailsCompileStatic(TypeCheckingMode.SKIP)
    void updateSectionCompletionStatusOfAllPackagesWhichHasDerivative(){
        List<Package> packages = Package.findAllByStatus(PackageStatus.OPEN);
        List<Package> derivateBeneficaryPackages = packages.findAll{it.getDerivativeBenefits().size() > 0}
        derivateBeneficaryPackages.each { Package aPackage ->
            def packageSections = this.packageQuestionnaireService.fetchPackageSections(aPackage.id)
            sectionCompletionStatusService.updateSectionCompletionStatusAndRemoveZombieAnswers(aPackage, packageSections)
            this.packageQuestionnaireService.answerSaveCompletionHandler([:], aPackage.id, LocalDate.now(), DisplayTextLanguage.EN)
        }

    }


    private Date packageCreationStartDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.YEAR, 2020);
        Date packageCreatedDate = cal.getTime();
        return packageCreatedDate;
    }
}
