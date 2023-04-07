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
        "Name": {'columnIndex': -1, 'modelName': "name", 'prefix': "#"},
        "DisplayText": {'columnIndex': -1, 'modelName': "displayText"},
        "ApplicantType": {'columnIndex': -1, 'modelName': "applicantType"},
        "PdfForm": {'columnIndex': -1, 'modelName': "pdfForm"},
        "EditionDate": {'columnIndex': -1, 'modelName': "editionDate"},
        "ExpirationDate": {'columnIndex': -1, 'modelName': "expirationDate"}
    };
    return cqlGeneratorUtil.generateCqlCommands(benefitCategoryColumnData, parsedData, cqlNodeName, questionnaireDataType);
}


exports.generateCqlFile = generateCqlFile;
