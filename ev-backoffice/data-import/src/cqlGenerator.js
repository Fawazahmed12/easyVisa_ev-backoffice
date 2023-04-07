var _ = require('lodash');
var async = require('async');
var fs = require('fs');
var path = require('path');
var csv = require('csv');
var concat = require('concat-files');
var Promise = require('bluebird');
var cqlGeneratorUtil = require('./cqlgenerator.util');

var Constants = require('./dataconstant');
var QUESTIONNAIRE_TYPE_DATA = Constants.QUESTIONNAIRE_TYPE_DATA;

var cqlGeneratorMapper = {
    "BenefitCategory": "benefitcategory-cql-generator.js",
    "Form": "form-cql-generator.js",
    "Section": "section-cql-generator.js",
    "SubSection": "subsection-cql-generator.js",
    "FormSubSection": "formsubsection-cql-generator.js",
    "TerminalNode": "terminal-cql-geneartor.js",
    "DocumentActionNode": "documentactionnode-cql-geneartor.js",
    "RepeatingQuestionGroup": "repeatingquestiongroup-cql-generator.js",
    "Question": "question-cql-generator.js",
    "NodeRelationship": "noderelationship-cql-generator.js",
    "ContinuationSheet": "continuationsheet-cql-generator.js",
    "PdfField": "pdffield-cql-generator.js",
    "FormQuestion": "formquestion-cql-generator.js",
    "PdfFieldRelationship": "pdffieldrelationship-cql-generator.js",
    "Document": "document-cql-generator.js",
    "DocumentHelp": "documenthelp-cql-generator.js",
    "MilestoneType": "milestonetype-cql-generator.js",
    "DocumentRelationship": "documentrelationship-cql-generator.js"
};


function generateCqlFile(questionnaireDataType, versionName, outputCqlFiles) {
    return generateCqlFilesFromCSV(questionnaireDataType, versionName, outputCqlFiles);
}


function generateCqlFilesFromCSV(questionnaireDataType, versionName, outputCqlFiles) {
    return new Promise(function (resolve, reject) {
        var cqlGeneratorFunction = _.partial(readAndProcessCsvFileIntoCqlFile, questionnaireDataType, versionName, outputCqlFiles);
        var csvFileNames = getCSVFileNames();
        async.eachSeries(csvFileNames, cqlGeneratorFunction, function iteratorDone(err) {
            if (err) return reject(err);
            return resolve(outputCqlFiles);
        });
    });
}


///////////////////////////



function readAndProcessCsvFileIntoCqlFile(questionnaireDataType, versionName, oututCqlFiles, csvFileName, callback) {
    async.waterfall([
        function (next) {
            var assetsPath = cqlGeneratorUtil.getAssetsDirectoryPath();
            var csvFilePath = path.resolve(assetsPath, versionName, 'csv_files', csvFileName + ".csv");
            fs.exists(csvFilePath, (exists) => {
                return next(null, exists, csvFilePath);
            });
        },
        function (exists, csvFilePath, next) {
            if(exists) {
                readCsvFile(csvFilePath, next);
            }else {
                next(null, []);
            }
        },
        function (parsedData, next) {
            if(parsedData && parsedData.length){
                generateCqlFromCsv(csvFileName, versionName, parsedData, questionnaireDataType, oututCqlFiles, next);
            } else {
                next();
            }
        }
    ], callback);
}


function readCsvFile(csvFilePath, callback) {
    fs.readFile(csvFilePath, 'utf8', function (err, data) {
        if (err) {
            return callback(err);
        }
        csv.parse(data, function (err, parsedData) {
            callback(err, parsedData);
        });
    });
}


function generateCqlFromCsv(fileName, versionName, parsedData, questionnaireDataType, oututCqlFiles, callback) {
    var cqlGeneratorName = cqlGeneratorMapper[fileName];
    var assetsPath = cqlGeneratorUtil.getAssetsDirectoryPath();
    var outputCqlFilePath = path.resolve(assetsPath, versionName, 'cql_files', fileName + ".cql");

    var cqlGeneratorClass = require('./csvtocqlgenerators/' + cqlGeneratorName);
    cqlGeneratorClass.generateCqlFile(parsedData, fileName, versionName, questionnaireDataType, outputCqlFilePath)
        .then(function () {
            console.log("CQL file '" + fileName + "' generated for the version: "+versionName);
            oututCqlFiles.push(outputCqlFilePath);
            return callback();
        }, function (err) {
            console.error("ERROR occurred while generating cql file '" + fileName + "' for the version: "+versionName);
            return callback(err);
        });
}

function getCSVFileNames() {
    return Object.keys(cqlGeneratorMapper);
}


exports.generate = generateCqlFile;
