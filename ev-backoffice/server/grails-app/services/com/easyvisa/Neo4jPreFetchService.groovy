package com.easyvisa

import com.easyvisa.questionnaire.QuestionnaireVersion
import com.easyvisa.questionnaire.model.*
import com.easyvisa.questionnaire.services.ContinuationSheetService
import com.easyvisa.questionnaire.services.DocumentService
import com.easyvisa.questionnaire.services.QuestionnaireService
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional
import groovy.transform.CompileDynamic
import org.springframework.beans.factory.annotation.Autowired

/*
 * Prefetch all the possible combinations from Neo4J
 * So the subsequent calls to the Neo4j would be faster
 */

@Transactional
@GrailsCompileStatic
class Neo4jPreFetchService {

    AsyncService asyncService

    @Autowired
    DocumentService documentService

    @Autowired
    QuestionnaireService questionnaireService

    @Autowired
    ContinuationSheetService continuationSheetService


    void preFetchAllNeo4jData() {
        this.preFetchNeo4jDataByQuestionnaireVersion()
    }

    private void preFetchNeo4jDataByQuestionnaireVersion() {
        int startIndex = 0
        List<QuestionnaireVersion> questionnaireVersionList = QuestionnaireVersion.findAll() as List<QuestionnaireVersion>
        this.prepareQuestVersionDependents(questionnaireVersionList, startIndex)
    }

    @CompileDynamic
    private void prepareQuestVersionDependents(List<QuestionnaireVersion> questionnaireVersionList, int currentRunningIndex) {
        if (questionnaireVersionList.size() < (currentRunningIndex + 1)) {
            return
        }

        String questVersion = questionnaireVersionList.get(currentRunningIndex).questVersion
        asyncService.runAsync(new Runnable() {
            @Override
            void run() {
                questionnaireService.fetchFormToSectionListMapper(questVersion)
                documentService.fetchFormToContinuationSheetListMapper(questVersion)
                List<Form> formList = questionnaireService.findAllForms(questVersion)
                formList.each { Form form ->
                    questionnaireService.fetchSectionsByForm(questVersion, form.id)
                    List<ContinuationSheet> continuationSheetList = continuationSheetService.fetchContinuationSheetsByForm(questVersion, form.id)
                    preFetchFormByContinuationSheet(questVersion, continuationSheetList)
                }

                List<BenefitCategory> benefitCategoryList = questionnaireService.findAllBenefitCategory(questVersion)
                preFetchSectionQuestionByBenefitCategoryAndSection(questVersion, benefitCategoryList)
                log.info(">>>>>>>>>>>>>>>> Sucessfully PreFetched Neo4J data for the QuestionnaireVersion [${questVersion}] >>>>>>>>>>>>>>>>")

                prepareQuestVersionDependents(questionnaireVersionList, ++currentRunningIndex)
            }
        }, "PreFetch Neo4J Data for QuestionnaireVersion [${questVersion}]")
    }

    private preFetchSectionQuestionByBenefitCategoryAndSection(String questVersion, List<BenefitCategory> benefitCategoryList) {
        List<ApplicantType> applicantTypeList = [ApplicantType.Petitioner, ApplicantType.Beneficiary]
        benefitCategoryList.each { BenefitCategory benefitCategory ->
            documentService.findFormsByBenefitCategory(questVersion, benefitCategory.id)
            applicantTypeList.each { ApplicantType applicantType ->
                documentService.documentsByBenefitCategory(questVersion, benefitCategory.id, applicantType)
                List<Section> sectionList = questionnaireService.sectionsByBenefitCategoryAndApplicantType(questVersion, benefitCategory.id, applicantType.name())
                sectionList.each { Section section ->
                    questionnaireService.sectionQuestionByBenefitCategoryAndSection(questVersion, benefitCategory.id, section.id)
                }
            }
        }
    }

    private preFetchFormByContinuationSheet(String questVersion, List<ContinuationSheet> continuationSheetList) {
        continuationSheetList.each { ContinuationSheet continuationSheet ->
            questionnaireService.fetchFormByContinuationSheet(questVersion, continuationSheet.id)
        }
    }
}
