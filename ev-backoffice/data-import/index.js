'use strict';

var async = require('async');
var path = require('path');
var concat = require('concat-files');
var fs = require('fs');

var Constants = require('./src/dataconstant');
var cqlGeneratorUtil = require('./src/cqlgenerator.util');

var QUESTIONNAIRE_VERSIONS = Constants.QUESTIONNAIRE_VERSIONS;
var QUESTIONNAIRE_TYPE_DATA = Constants.QUESTIONNAIRE_TYPE_DATA;

async.series([
    function (next) {
        generateQuestionnaireDefaultLangFile(next);
    },
    function (next) {
        var questionnaireTypeData = QUESTIONNAIRE_TYPE_DATA.SHELL;
        generateQuestionnaireCqlDataFiles(questionnaireTypeData, next);
    }, function (next) {
        var outputQuestionnaireFiles = [];
        var questionnaireTypeData = QUESTIONNAIRE_TYPE_DATA.QUESTIONMAPPING;
        questionnaireTypeData.idCount = 0;
        generateQuestionnaireDataFiles(questionnaireTypeData, outputQuestionnaireFiles, next);
    }, addNeo4jUpdationDate], function (err) {
    if (err) {
        return console.error("ERROR occurred while generating cql/sql files.");
    }
    console.log("All CQL/SQL files generated successfully for all versions!");
});


function generateQuestionnaireCqlDataFiles(questionnaireTypeData, callback) {
    var outputQuestionnaireFiles = [];
    async.series([
        function (next) {
            generateResetAllNodeCqlFile(questionnaireTypeData, outputQuestionnaireFiles, next);
        }, function (next) {
            var questionnaireTypeData = QUESTIONNAIRE_TYPE_DATA.SHELL;
            generateQuestionnaireDataFiles(questionnaireTypeData, outputQuestionnaireFiles, next);
        }], callback);
}

function generateQuestionnaireDataFiles(questionnaireTypeData, outputQuestionnaireFiles, callback) {
    async.eachSeries(QUESTIONNAIRE_VERSIONS, function (versionName, nextIteration) {
        generateQuestionData(versionName, questionnaireTypeData, outputQuestionnaireFiles, nextIteration);
    }, function (err) {
        if (err) return callback(err);
        mergeOutputQuestionnaireFiles(questionnaireTypeData, outputQuestionnaireFiles, callback)
    });
}


function generateQuestionData(versionName, questionnaireDataType, outputQuestionnaireFiles, callback) {
    var dataImportGenerator = require('./src/' + questionnaireDataType.generator);
    dataImportGenerator.generate(questionnaireDataType, versionName, outputQuestionnaireFiles)
        .then(function () {
            console.log("All " + questionnaireDataType.outputFileType + " files generated successfully for the version: " + versionName + "!");
            return callback();
        }, function (err) {
            console.error("ERROR occurred while generating " + questionnaireDataType.outputFileType + " files  for the version: " + versionName);
            return callback(err);
        });
}


function generateResetAllNodeCqlFile(questionnaireDataType, outputCqlFiles, callback) {
    var cqlCommands = cqlGeneratorUtil.generateRemoveAllNodesCqlCommand();
    var fileName = 'RemoveAllNodes.cql';
    var assetsPath = cqlGeneratorUtil.getAssetsDirectoryPath();
    var outputCqlFilePath = path.resolve(assetsPath, "miscellaneous", "cql_files", fileName);
    cqlGeneratorUtil.writeConvertedJsonData(outputCqlFilePath, cqlCommands)
        .then(function () {
            console.log("CQL generated for the file '" + fileName + "'");
            outputCqlFiles.push(outputCqlFilePath);
            return callback();
        }, function (err) {
            console.error("ERROR occurred while generating cql file for '" + fileName + "'");
            return callback(err);
        });
}


function mergeOutputQuestionnaireFiles(questionnaireTypeData, outputQuestionnaireFiles, callback) {
    if (!outputQuestionnaireFiles.length) {
        return callback();
    }
    var ouputPath = cqlGeneratorUtil.getOutputDirectoryPath();
    var outputCqlFilePath = path.resolve(ouputPath, questionnaireTypeData.fileName);
    concat(outputQuestionnaireFiles, outputCqlFilePath, callback);
}


function addNeo4jUpdationDate(callback) {
    var fullFilePath = cqlGeneratorUtil.getNeo4jUpdateFilePath();
    fs.readFile(fullFilePath, 'utf-8', function (err, data) {
        if (err) return callback(err);

        var updatedDate = new Date();
        let newValue = updatedDate.toISOString();
        fs.writeFile(fullFilePath, newValue, 'utf-8', function (err, data) {
            if (err) return callback(err);
            console.log(`Added Neo4J update entry to ${fullFilePath}`);
            return callback();
        })
    });
}


function getAssetsDirectoryPath() {
    var currentFileDirName = __dirname;
    var rootDirectoryPath = getSubStringBefore(currentFileDirName, "src");
    var fullFilePath = path.resolve(rootDirectoryPath, "assets");
    return fullFilePath;
}


function generateQuestionnaireDefaultLangFile(callback) {
    var outputQuestionnaireFiles = [];
    var questionnaireTypeData = QUESTIONNAIRE_TYPE_DATA.I18N;
    generateQuestionnaireDataFiles(questionnaireTypeData, outputQuestionnaireFiles, callback);
}




