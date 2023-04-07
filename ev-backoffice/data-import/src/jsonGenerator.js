var _ = require('lodash');
var async = require('async');
var fs = require('fs');
var path = require('path');
var csv = require('csv');
var Promise = require('bluebird');
var cqlGeneratorUtil = require('./cqlgenerator.util');

var jsonGeneratorMapper = {
    "Section": "section-json-generator.js",
    "SubSection": "subsection-json-generator.js",
    "Question": "question-json-generator.js",
    "RepeatingQuestionGroup": "repeatingquestiongroup-json-generator.js"
};


function generateJsonFile(questionnaireDataType, versionName, outputJsonFiles) {
    return generateInputTypeSourceFromJSON()
        .then((outputJsonLangData) => {
            return generateJsonFilesFromCSV(versionName, outputJsonLangData).then(function (outputJsonLangData) {
                createOutputJsonFile(versionName, outputJsonLangData);
            });
        })

}

function createOutputJsonFile(versionName, outputJsonLangData) {
    var assetsPath = cqlGeneratorUtil.getAssetsDirectoryPath();
    var outputJsonFilePath = path.resolve(assetsPath, versionName, 'json_files', "en.json");
    return cqlGeneratorUtil.writeJsonDataFile(outputJsonFilePath, outputJsonLangData);
}


function generateJsonFilesFromCSV(versionName, outputJsonLangData) {
    return new Promise(function (resolve, reject) {
        var jsonGeneratorFunction = _.partial(readAndProcessCsvFileIntoJsonFile, versionName, outputJsonLangData);
        var csvFileNames = getCSVFileNames();
        async.eachSeries(csvFileNames, jsonGeneratorFunction, function iteratorDone(err) {
            if (err) return reject(err);
            return resolve(outputJsonLangData);
        });
    });
}


function generateInputTypeSourceFromJSON() {

    return new Promise(function (resolve, reject) {

        async.waterfall([
            function (next) {
                var jsonFilePath = cqlGeneratorUtil.getInputTypeSourceFilePath();
                fs.readFile(jsonFilePath, 'utf8', function (err, data) {
                    if (err) {
                        return next(err);
                    }
                    var inputSourceTypeJson = JSON.parse(data);
                    next(null, inputSourceTypeJson);
                });
            },
            function (parsedDataList, next) {
                var inputTypeSourceLang = {
                    'Not Applicable': "Not Applicable"
                };
                var index = 0;
                parsedDataList.forEach((inputSourceData) => {
                    var values = inputSourceData.values || [];
                    values.forEach((data) => {
                        if (inputTypeSourceLang[data.value] && data.label != inputTypeSourceLang[data.value]) {
                            console.error(`${++index}. Key '${data.value}' already exists with value: '${inputTypeSourceLang[data.value]}'`)
                            console.error(`New value for the Key '${data.value}' is '${data.label}'`)
                            console.error("");
                        }
                        inputTypeSourceLang[data.value] = data.label;
                    });
                });
                next(null, inputTypeSourceLang);
            }
        ], (err, data) => {
            if (err) {
                return reject(err);
            }

            var outputJsonLangData = {
                'INPUT_SOURCE_TYPE': data
            };
            return resolve(outputJsonLangData);
        });
    });
}


///////////////////////////


function readAndProcessCsvFileIntoJsonFile(versionName, outputJsonLangData, csvFileName, callback) {
    async.waterfall([
        function (next) {
            var assetsPath = cqlGeneratorUtil.getAssetsDirectoryPath();
            var csvFilePath = path.resolve(assetsPath, versionName, 'csv_files', csvFileName + ".csv");
            fs.exists(csvFilePath, function (exists) {
                return next(null, exists, csvFilePath);
            });
        },
        function (exists, csvFilePath, next) {
            if (exists) {
                readCsvFile(csvFilePath, next);
            } else {
                next(null, []);
            }
        },
        function (parsedData, next) {
            if (parsedData && parsedData.length) {
                generateJsonFromCsv(csvFileName, parsedData, outputJsonLangData);
            }
            next();
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


function generateJsonFromCsv(fileName, parsedData, outputJsonLangData) {
    var jsonGeneratorName = jsonGeneratorMapper[fileName];
    var jsonGeneratorClass = require('./csvtojsongenerators/' + jsonGeneratorName);
    jsonGeneratorClass.generateJsonFile(parsedData, outputJsonLangData);
}

function getCSVFileNames() {
    return Object.keys(jsonGeneratorMapper);
}


exports.generate = generateJsonFile;
