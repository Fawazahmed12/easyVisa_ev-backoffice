var _ = require('lodash');
var async = require('async');
var Promise = require('bluebird');
var path = require('path');
var cqlGeneratorUtil = require('./../cqlgenerator.util.js');
var Constants = require('./../dataconstant.js');
var NODE_PROPERTY_TYPE = Constants.NODE_PROPERTY_TYPE;

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
        "AddButtonTitle": {'columnIndex': -1, 'modelName': "addButtonTitle"},
        "ResetRuleName": {'columnIndex': -1, 'modelName': "resetRuleName"},
        "ResetRuleParam": {'columnIndex': -1, 'modelName': "resetRuleParam"},
        "RuleClassName": {'columnIndex': -1, 'modelName': "ruleClassName"},
        "RuleParam": {'columnIndex': -1, 'modelName': "ruleParam"},
        "VisibilityRuleClassName": {'columnIndex': -1, 'modelName': "visibilityRuleClassName"},
        "VisibilityRuleParam": {'columnIndex': -1, 'modelName': "visibilityRuleParam"},
        "LifeCycleRule": {'columnIndex': -1, 'modelName': "lifeCycleRule"},
        "LifeCycleRuleParam": {'columnIndex': -1, 'modelName': "lifeCycleRuleParam"},
        "AttributeRule": {'columnIndex': -1, 'modelName': "attributeRule"},
        "AttributeRuleParam": {'columnIndex': -1, 'modelName': "attributeRuleParam", 'dataType':NODE_PROPERTY_TYPE.STRING},
        "DisplayTextRule": {'columnIndex': -1, 'modelName': "displayTextRule"},
        "DisplayTextRuleParam": {'columnIndex': -1, 'modelName': "displayTextRuleParam"},
        "WrapperName": {'columnIndex': -1, 'modelName': "wrapperName"},
        "StyleClassName": {'columnIndex': -1, 'modelName': "styleClassName"}
    };
    return cqlGeneratorUtil.generateCqlCommands(benefitCategoryColumnData, parsedData, cqlNodeName, questionnaireDataType);
}

exports.generateCqlFile = generateCqlFile;