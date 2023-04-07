package com.easyvisa.questionnaire.services

import com.easyvisa.enums.DisplayTextLanguage
import com.easyvisa.enums.QuestionnaireDisplayNodeType
import com.easyvisa.questionnaire.QuestionnaireVersion
import grails.gorm.transactions.Transactional
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service
import org.springframework.util.ResourceUtils

@Service
@Slf4j
class QuestionnaireTranslationService {

    private Map questionnaireTranslatorVersionMapper;

    @Transactional
    void loadQuestionnaireTranslatorMeta() {
        this.questionnaireTranslatorVersionMapper = [:]
        QuestionnaireVersion.findAll().each {
            String questVersion = it.questVersion;
            this.questionnaireTranslatorVersionMapper[questVersion] = this.loadQuestionnaireTranslatorDataByVersion(questVersion);
        }
        log.info("Questionnaire Translator Meta loaded Successfully for All Versions");
    }

    private Map loadQuestionnaireTranslatorDataByVersion(String questVersion) {
        Map<DisplayTextLanguage, Map<String, String>> questionnaireTranslatorMapper = [:]
        DisplayTextLanguage.values().each { DisplayTextLanguage displayTextLanguage ->
            String filePath = "questionnaire/${questVersion}/${displayTextLanguage.languageCode}.json"
            questionnaireTranslatorMapper[displayTextLanguage] = this.loadTranslatorFile(filePath);
        }
        log.info("Questionnaire Translator JSON loaded successfully for the version: ${questVersion}");
        return questionnaireTranslatorMapper;
    }


    private Map<String, Map<String, String>> loadTranslatorFile(String filePath) {
        try {
            File file = ResourceUtils.getFile("classpath:${filePath}");
            JsonSlurper jsonSlurper = new JsonSlurper()
            Map result = (Map) jsonSlurper.parse(file)
            Map<String, String> questionnaireTranslatorMap = [:]
            for (Map.Entry entry : result.entrySet()) {
                QuestionnaireDisplayNodeType displayTextType = (QuestionnaireDisplayNodeType) entry.getKey()
                Map<String, String> displayTextData = (Map) entry.getValue()
                Map<String, String> displayTextMapper = [:]
                for (Map.Entry displayTextEntry : displayTextData.entrySet()) {
                    displayTextMapper[displayTextEntry.getKey() as String] = displayTextEntry.getValue() as String
                }
                questionnaireTranslatorMap[displayTextType] = displayTextMapper
            }
            return questionnaireTranslatorMap
        } catch (Exception e) {
            log.error("Error occurred while loading '${filePath}' translation json file from QuestionnaireTranslationService", e)
            throw new RuntimeException("Failed to load ${filePath} file from QuestionnaireTranslationService");
        }
    }

    String getTranslatorValue(String questVersion, QuestionnaireDisplayNodeType displayNodeType, String nodeId, DisplayTextLanguage displayTextLanguage) {
        Map<DisplayTextLanguage, Map<String, String>> questionnaireTranslatorMapper = this.questionnaireTranslatorVersionMapper[questVersion] ?: [:]
        Map<String, String> questionnaireTranslatorMap = questionnaireTranslatorMapper[displayTextLanguage] ?: [:]
        Map<String, String> displayTextMapper = questionnaireTranslatorMap[displayNodeType] ?: [:]
        return displayTextMapper[nodeId]
    }
}
