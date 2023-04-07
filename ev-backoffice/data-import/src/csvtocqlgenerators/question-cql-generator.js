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
        "DataType": {'columnIndex': -1, 'modelName': "dataType"},
        "InputType": {'columnIndex': -1, 'modelName': "inputType"},
        "InputSourceType": {'columnIndex': -1, 'modelName': "inputSourceType"},
        "Actionable": {'columnIndex': -1, 'modelName': "actionable"},
        "Refresh": {'columnIndex': -1, 'modelName': "refresh"},
        "RuleClassName": {'columnIndex': -1, 'modelName': "ruleClassName"},
        "RuleParam": {'columnIndex': -1, 'modelName': "ruleParam"},
        "VisibilityRuleClassName": {'columnIndex': -1, 'modelName': "visibilityRuleClassName"},
        "VisibilityRuleParam": {'columnIndex': -1, 'modelName': "visibilityRuleParam"},
        "InputTypeSourceRule": {'columnIndex': -1, 'modelName': "inputTypeSourceRule"},
        "InputTypeSourceRuleParam": {'columnIndex': -1, 'modelName': "inputTypeSourceRuleParam"},
        "DisplayTextRule": {'columnIndex': -1, 'modelName': "displayTextRule"},
        "DisplayTextRuleParam": {'columnIndex': -1, 'modelName': "displayTextRuleParam"},
        "AttributeRule": {'columnIndex': -1, 'modelName': "attributeRule"},
        "AttributeRuleParam": {'columnIndex': -1, 'modelName': "attributeRuleParam", 'dataType':NODE_PROPERTY_TYPE.STRING},
        "AnswerCompletionValidationRule": {'columnIndex': -1, 'modelName': "answerCompletionValidationRule"},
        "AnswerCompletionValidationRuleParam": {'columnIndex': -1, 'modelName': "answerCompletionValidationRuleParam", 'dataType':NODE_PROPERTY_TYPE.STRING},
        "AnswerVisibilityValidationRule": {'columnIndex': -1, 'modelName': "answerVisibilityValidationRule"},
        "AnswerVisibilityValidationRuleParam": {'columnIndex': -1, 'modelName': "answerVisibilityValidationRuleParam"},
        "AnswerValidationRule": {'columnIndex': -1, 'modelName': "answerValidationRule"},
        "AnswerValidationRuleParam": {'columnIndex': -1, 'modelName': "answerValidationRuleParam"},
        "PdfFieldRelationshipRule": {'columnIndex': -1, 'modelName': "pdfFieldRelationshipRule"},
        "PdfFieldRelationshipRuleParam": {'columnIndex': -1, 'modelName': "pdfFieldRelationshipRuleParam"},
        "DefaultValue": {'columnIndex': -1, 'modelName': "defaultValue"},
        "Readonly": {'columnIndex': -1, 'modelName': "readonly"},
        "ExcludeFromPercentageCalculation": {'columnIndex': -1, 'modelName': "excludeFromPercentageCalculation"},
        "StyleClassName": {'columnIndex': -1, 'modelName': "styleClassName"},
        "WrapperName": {'columnIndex': -1, 'modelName': "wrapperName"},
        "ErrorMessage": {'columnIndex': -1, 'modelName': "errorMessage"},
        "ContextualClue": {'columnIndex': -1, 'modelName': "contextualClue"},
        "Tooltip": {'columnIndex': -1, 'modelName': "tooltip"},
        "TooltipRule": {'columnIndex': -1, 'modelName': "tooltipRule"}
    };
    return cqlGeneratorUtil.generateCqlCommands(benefitCategoryColumnData, parsedData, cqlNodeName, questionnaireDataType);
}

exports.generateCqlFile = generateCqlFile;
