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
        "FieldExpression": {'columnIndex': -1, 'modelName': "fieldExpressions", 'dataType':NODE_PROPERTY_TYPE.ARRAY},
        "FieldType": {'columnIndex': -1, 'modelName': "fieldType"},
        "FormFieldCount": {'columnIndex': -1, 'modelName': "formFieldCount"},
        "ContinuationSheetRule": {'columnIndex': -1, 'modelName': "continuationSheetRule"},
        "OwnFormRule": {'columnIndex': -1, 'modelName': "ownFormRule"},
        "ContinuationSheetNodeId": {'columnIndex': -1, 'modelName': "continuationSheetNodeId"},
        "RuleClassName": {'columnIndex': -1, 'modelName': "ruleClassName"},
        "RuleParam": {'columnIndex': -1, 'modelName': "ruleParam"}
    };
    return cqlGeneratorUtil.generateCqlCommands(benefitCategoryColumnData, parsedData, cqlNodeName, questionnaireDataType);
}

exports.generateCqlFile = generateCqlFile;
