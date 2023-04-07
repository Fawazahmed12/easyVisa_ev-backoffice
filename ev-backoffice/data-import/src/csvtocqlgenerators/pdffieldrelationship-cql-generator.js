var _ = require('lodash');
var async = require('async');
var Promise = require('bluebird');
var path = require('path');
var cqlGeneratorUtil = require('./../cqlgenerator.util.js');

function generateCqlFile(parsedData, cqlNodeName, versionName, questionnaireDataType, outputCqlFilePath) {
    var cqlCommands = cqlGeneratorUtil.generateRelationshipCqlCommands(versionName, parsedData, questionnaireDataType);
    return cqlGeneratorUtil.writeConvertedJsonData(outputCqlFilePath, cqlCommands);
}


exports.generateCqlFile = generateCqlFile;