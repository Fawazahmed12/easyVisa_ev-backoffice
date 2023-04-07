var cqlGeneratorUtil = require('./../cqlgenerator.util.js');


function generateJsonFile(parsedData, outputJsonLangData) {
    outputJsonLangData['SUBSECTION'] = generateJsonObj(parsedData);
}


function generateJsonObj(parsedData) {
    var benefitCategoryColumnData = {
        "EasyVisaId": {'columnIndex': -1, 'modelName': "easyVisaId"},
        "DisplayText": {'columnIndex': -1, 'modelName': "displayText"}
    };
    return cqlGeneratorUtil.generateJsonObj(benefitCategoryColumnData, parsedData, "EasyVisaId", "DisplayText");
}

exports.generateJsonFile = generateJsonFile;