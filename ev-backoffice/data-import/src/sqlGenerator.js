var _ = require('lodash');
var async = require('async');
var fs = require('fs');
var path = require('path');
var csv = require('csv');
var concat = require('concat-files');
var Promise = require('bluebird');

var cqlGeneratorUtil = require('./cqlgenerator.util');
var Constants = require('./dataconstant');


var sqlGeneratorMapper = {
    "PetitionerBeneficiaryMapping": "petitionerbeneficiarymapping-sql-generator.js"
};


function generateSqlFile(questionnaireDataType, versionName, outputSqlFiles) {
    return generateSqlFilesFromCSV(questionnaireDataType, versionName, outputSqlFiles)
}


function generateSqlFilesFromCSV(questionnaireDataType, versionName, outputSqlFiles) {
    return new Promise(function (resolve, reject) {
        var sqlGeneratorFunction = _.partial(readAndProcessCsvFileIntoSqlFile, questionnaireDataType, versionName, outputSqlFiles);
        var csvFileNames = getCSVFileNames();
        async.eachSeries(csvFileNames, sqlGeneratorFunction, function iteratorDone(err) {
            if (err) return reject(err);
            return resolve(outputSqlFiles);
        });
    });
}


function readAndProcessCsvFileIntoSqlFile(questionnaireDataType, versionName, oututCqlFiles, csvFileName, callback) {
    async.waterfall([
        function (next) {
            var assetsPath = cqlGeneratorUtil.getAssetsDirectoryPath();
            var csvFilePath = path.resolve(assetsPath, versionName, 'csv_files', csvFileName + ".csv");
            readCsvFile(csvFilePath, versionName, next);
        },
        function (parsedData, versionName, next) {
            generateCqlFromCsv(questionnaireDataType, versionName, csvFileName, parsedData, oututCqlFiles, next);
        }
    ], callback);
}


function readCsvFile(csvFilePath, versionName, callback) {
    fs.readFile(csvFilePath, 'utf8', function (err, data) {
        if (err) {
            return callback(err);
        }
        csv.parse(data, function (err, parsedData) {
            callback(err, parsedData, versionName);
        });
    });
}


function generateCqlFromCsv(questionnaireDataType, versionName, fileName, parsedData, oututCqlFiles, callback) {
    var sqlGeneratorName = sqlGeneratorMapper[fileName];
    var assetsPath = cqlGeneratorUtil.getAssetsDirectoryPath();
    var outputCqlFilePath = path.resolve(assetsPath, versionName, 'sql_files', fileName + ".sql");

    var cqlGeneratorClass = require('./csvtosqlgenerators/' + sqlGeneratorName);
    cqlGeneratorClass.generateSqlFile(questionnaireDataType, parsedData, versionName, outputCqlFilePath)
        .then(function () {
            console.log("SQL file '" + fileName + "' generated for the version: "+versionName);
            oututCqlFiles.push(outputCqlFilePath);
            return callback();
        }, function (err) {
            console.error("ERROR occurred while generating sql file '" + fileName + "' for the version: "+versionName);
            return callback(err);
        });
}


function getCSVFileNames() {
    return Object.keys(sqlGeneratorMapper);
}


exports.generate = generateSqlFile;