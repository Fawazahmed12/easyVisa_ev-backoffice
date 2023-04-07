var _ = require('lodash');
var async = require('async');
var Promise = require('bluebird');
var path = require('path');
var cqlGeneratorUtil = require('./../cqlgenerator.util.js');


function generateCqlFile(parsedData, cqlNodeName, versionName, questionnaireDataType, outputCqlFilePath) {
    var cqlCommands = generateCqlCommands(parsedData, cqlNodeName, questionnaireDataType);
    return cqlGeneratorUtil.writeConvertedJsonData(outputCqlFilePath, cqlCommands);
}


function generateCqlCommands(parsedData, cqlNodeName, questionnaireDataType) {
    var benefitCategoryColumnData = {
        "EasyVisaId": {'columnIndex': -1, 'modelName': "easyVisaId"},
        "QuestVersion": {'columnIndex': -1, 'modelName': "questVersion"},
        "Name": {'columnIndex': -1, 'modelName': "name"},
        "DisplayText": {'columnIndex': -1, 'modelName': "displayText"},
        "ShortName": {'columnIndex': -1, 'modelName': "shortName"},
        "ApplicantType": {'columnIndex': -1, 'modelName': "applicantType"},
        "SectionCompletionRule": {'columnIndex': -1, 'modelName': "sectionCompletionRule"},
        "SectionCompletionRuleParam": {'columnIndex': -1, 'modelName': "sectionCompletionRuleParam"},
        "SectionVisibilityRule": {'columnIndex': -1, 'modelName': "sectionVisibilityRule"},
        "SectionVisibilityRuleParam": {'columnIndex': -1, 'modelName': "sectionVisibilityRuleParam"},
        "CompletionPercentageRule": {'columnIndex': -1, 'modelName': "completionPercentageRule"},
        "CompletionPercentageRuleParam": {'columnIndex': -1, 'modelName': "completionPercentageRuleParam"}
    };
    return cqlGeneratorUtil.generateCqlCommands(benefitCategoryColumnData, parsedData, cqlNodeName, questionnaireDataType);
}


exports.generateCqlFile = generateCqlFile;