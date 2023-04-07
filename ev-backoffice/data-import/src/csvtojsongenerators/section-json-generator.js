var cqlGeneratorUtil = require('./../cqlgenerator.util.js');


function generateJsonFile(parsedData, outputJsonLangData) {
    outputJsonLangData['SECTION'] = generateJsonObj(parsedData);
    outputJsonLangData['SECTION_MENU'] = generateMenuJsonObj(parsedData);
}


function generateJsonObj(parsedData) {
    var benefitCategoryColumnData = {
        "EasyVisaId": {'columnIndex': -1, 'modelName': "easyVisaId"},
        "DisplayText": {'columnIndex': -1, 'modelName': "displayText"}
    };
    return cqlGeneratorUtil.generateJsonObj(benefitCategoryColumnData, parsedData, "EasyVisaId", "DisplayText");
}

function generateMenuJsonObj(parsedData) {
    var benefitCategoryColumnData = {
        "EasyVisaId": {'columnIndex': -1, 'modelName': "easyVisaId"},
        "ShortName": {'columnIndex': -1, 'modelName': "shortName"}
    };
    return cqlGeneratorUtil.generateJsonObj(benefitCategoryColumnData, parsedData, "EasyVisaId", "ShortName");
}


exports.generateJsonFile = generateJsonFile;