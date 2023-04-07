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
        "DisplayOrderChildren": {'columnIndex': -1, 'modelName': "displayOrderChildren"},
        "RuleClassName": {'columnIndex': -1, 'modelName': "ruleClassName"},
        "RuleParam": {'columnIndex': -1, 'modelName': "ruleParam"},
        "WrapperName": {'columnIndex': -1, 'modelName': "wrapperName"},
        "StyleClassName": {'columnIndex': -1, 'modelName': "styleClassName"}
    };
    return cqlGeneratorUtil.generateCqlCommands(benefitCategoryColumnData, parsedData, cqlNodeName, questionnaireDataType);
}

exports.generateCqlFile = generateCqlFile;