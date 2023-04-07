var _ = require('lodash');

var Constants = require('./../dataconstant.js');
var NODE_PROPERTY_TYPE = Constants.NODE_PROPERTY_TYPE;
var cqlGeneratorUtil = require('./../cqlgenerator.util.js');


function generateCqlFile(parsedData, cqlNodeName, versionName, questionnaireDataType, outputCqlFilePath) {
    var cqlCommands = generateCqlCommands(parsedData, cqlNodeName, questionnaireDataType);
    return cqlGeneratorUtil.writeConvertedJsonData(outputCqlFilePath, cqlCommands);
}


function generateCqlCommands(parsedData, cqlNodeName, questionnaireDataType) {
    var continuationSheetColumnData = {
        "EasyVisaId": {'columnIndex': -1, 'modelName': "easyVisaId"},
        "QuestVersion": {'columnIndex': -1, 'modelName': "questVersion"},
        "ApplicantType": {'columnIndex': -1, 'modelName': "applicantType"},
        "SheetNumber": {'columnIndex': -1, 'modelName': "sheetNumber"},
        "SheetName": {'columnIndex': -1, 'modelName': "sheetName"},
        "Page": {'columnIndex': -1, 'modelName': "page", 'prefix': "#"},
        "Part": {'columnIndex': -1, 'modelName': "part", 'prefix': "#"},
        "Item": {'columnIndex': -1, 'modelName': "item", 'prefix': "#"},
        "DisplayName": {'columnIndex': -1, 'modelName': "displayName"}
    };
    return cqlGeneratorUtil.generateCqlCommands(continuationSheetColumnData, parsedData, cqlNodeName, questionnaireDataType);
}

exports.generateCqlFile = generateCqlFile;
