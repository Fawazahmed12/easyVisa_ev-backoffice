var cqlGeneratorUtil = require('./../cqlgenerator.util.js');


function generateJsonFile(parsedData, outputJsonLangData) {
    outputJsonLangData['REPEATING_QUESTION_GROUP'] = generateJsonObj(parsedData);
    outputJsonLangData['RQG_ADD_BTN_TITLE'] = generateAddButtonTitleJsonObj(parsedData)
}


function generateJsonObj(parsedData) {
    var benefitCategoryColumnData = {
        "EasyVisaId": {'columnIndex': -1, 'modelName': "easyVisaId"},
        "DisplayText": {'columnIndex': -1, 'modelName': "displayText"}
    };
    return cqlGeneratorUtil.generateJsonObj(benefitCategoryColumnData, parsedData, "EasyVisaId", "DisplayText");
}

function generateAddButtonTitleJsonObj(parsedData) {
    var benefitCategoryColumnData = {
        "EasyVisaId": {'columnIndex': -1, 'modelName': "easyVisaId"},
        "AddButtonTitle": {'columnIndex': -1, 'modelName': "addButtonTitle"}
    };
    return cqlGeneratorUtil.generateJsonObj(benefitCategoryColumnData, parsedData, "EasyVisaId", "AddButtonTitle");
}

exports.generateJsonFile = generateJsonFile;