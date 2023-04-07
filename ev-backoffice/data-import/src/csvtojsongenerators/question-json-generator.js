var cqlGeneratorUtil = require('./../cqlgenerator.util.js');

function generateJsonFile(parsedData, outputJsonLangData) {
    outputJsonLangData['QUESTION'] = generateQuestionJsonObj(parsedData);
    outputJsonLangData['TOOLTIP'] = generateToolTipJsonObj(parsedData);
    outputJsonLangData['CONTEXTUAL_CLUE'] = generateContextualClueJsonObj(parsedData);
}

function generateQuestionJsonObj(parsedData) {
    var benefitCategoryColumnData = {
        "EasyVisaId": {'columnIndex': -1, 'modelName': "easyVisaId"},
        "DisplayText": {'columnIndex': -1, 'modelName': "displayText"}
    };
    return cqlGeneratorUtil.generateJsonObj(benefitCategoryColumnData, parsedData, "EasyVisaId", "DisplayText");
}


function generateToolTipJsonObj(parsedData) {
    var benefitCategoryColumnData = {
        "EasyVisaId": {'columnIndex': -1, 'modelName': "easyVisaId"},
        "Tooltip": {'columnIndex': -1, 'modelName': "tooltip"}
    };
    return cqlGeneratorUtil.generateJsonObj(benefitCategoryColumnData, parsedData, "EasyVisaId", "Tooltip");
}

function generateContextualClueJsonObj(parsedData) {
    var benefitCategoryColumnData = {
        "EasyVisaId": {'columnIndex': -1, 'modelName': "easyVisaId"},
        "ContextualClue": {'columnIndex': -1, 'modelName': "contextualClue"}
    };
    return cqlGeneratorUtil.generateJsonObj(benefitCategoryColumnData, parsedData, "EasyVisaId", "ContextualClue");
}

exports.generateJsonFile = generateJsonFile;