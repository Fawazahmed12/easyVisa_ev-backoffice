var _ = require('lodash');
var fs = require('fs');
var path = require('path');
var Promise = require('bluebird');


var Constants = require('./dataconstant');
var QUESTIONNAIRE_TYPE_DATA = Constants.QUESTIONNAIRE_TYPE_DATA;
var NODE_PROPERTY_TYPE = Constants.NODE_PROPERTY_TYPE;

function replaceString(inputStr, replaceStr, replaceWith) {
    var changedString = inputStr.split(replaceStr).join(replaceWith);
    return changedString;
}


function getOutputDirectoryPath() {
    var currentFileDirName = __dirname;
    var rootDirectoryPath = getSubStringBefore(currentFileDirName, "src");
    var fullFilePath = path.resolve(rootDirectoryPath, "output");
    return fullFilePath;
}


function getAssetsDirectoryPath() {
    var currentFileDirName = __dirname;
    var rootDirectoryPath = getSubStringBefore(currentFileDirName, "src");
    var fullFilePath = path.resolve(rootDirectoryPath, "assets");
    return fullFilePath;
}


function getNeo4jUpdateFilePath() {
    var currentFileDirName = __dirname;
    var rootDirectoryPath = getSubStringBefore(currentFileDirName, "data-import");
    var fullFilePath = path.resolve(rootDirectoryPath, "server", "src", "main", "resources", "db", "neo4j_update.txt");
    return fullFilePath;
}


function getInputTypeSourceFilePath() {
    var currentFileDirName = __dirname;
    var rootDirectoryPath = getSubStringBefore(currentFileDirName, "data-import");
    var fullFilePath = path.resolve(rootDirectoryPath, "server", "src", "main", "resources", "uimeta", "input-type-source.json");
    return fullFilePath;
}


function replaceAll(str, find, replace) {
    return str.replace(new RegExp(find, "g"), replace);
}


function getColumnIndex(titleRowData, titleName) {
    var titleNameColumnIndex = titleRowData.indexOf(titleName);
    return titleNameColumnIndex;
}


function writeConvertedJsonData(outputFilename, outputCqlData) {
    return new Promise(function (resolve, reject) {
        var outputCqlDataStr = outputCqlData.join("\n");
        outputCqlDataStr = replaceAllUsingRegx(outputCqlDataStr, /\/n/g, " ");
        fs.writeFile(outputFilename, outputCqlDataStr, function (err) {
            if (err) {
                return reject(err);
            }
            return resolve();
        });
    });
}


function writeJsonDataFile(outputFilename, outputCqlData) {
    return new Promise(function (resolve, reject) {
        fs.writeFile(outputFilename, JSON.stringify(outputCqlData), function (err) {
            if (err) {
                return reject(err);
            }
            return resolve();
        });
    });
}


function generateSqlCommands(questionnaireDataType, versionName, sqlNodeName, csvColumnData, parsedData) {
    var titleRowData = parsedData[0];
    var csvModelData = {'order': {}};
    var sqlColumnNames = ["id", "version", "quest_version"];
    _.each(csvColumnData, function (columnData, columnName) {
        columnData.columnIndex = getColumnIndex(titleRowData, columnName);
        csvModelData[columnData.modelName] = columnData;
        sqlColumnNames.push(columnData.modelName);
    });


    var deleteStatement = 'DELETE FROM ' + sqlNodeName + '  WHERE quest_version=' + "'" + versionName + "';";
    var insertStatement = 'INSERT INTO ' + sqlNodeName + ' (' + sqlColumnNames.join(",") + ') VALUES';
    var cqlCommands = [deleteStatement, "/n", insertStatement];
    var orderIndex = 0;
    for (var index = 1; index < parsedData.length; index++) {
        var csvRowData = parsedData[index];
        var rowData = _.compact(csvRowData);
        if (rowData.length == 0) {// If the row is empty then add
            continue;
        }

        var cqlData = {};
        _.each(csvColumnData, function (columnData, columnName) {
            var propertyValue = csvRowData[columnData.columnIndex];
            if (_.isEmpty(propertyValue)) return;// If the column is empty for a selected row, then don't concatenate its value

            if (columnData.dataType == NODE_PROPERTY_TYPE.STRING) {
                cqlData[columnData.modelName] = "'" + propertyValue + "'";
            } else {
                cqlData[columnData.modelName] = propertyValue;
            }
        });
        cqlData["id"] = ++questionnaireDataType.idCount;
        cqlData["version"] = 1;
        cqlData["quest_version"] = "'" + versionName + "'";


        var props = [];
        _.each(sqlColumnNames, function (columnName) {
            props.push(cqlData[columnName]);
        });
        var cqlQueryString = "(" + props.join(",") + "),";
        cqlCommands.push(cqlQueryString);
    }

    var lastCqlQueryString = cqlCommands[cqlCommands.length - 1];
    cqlCommands[cqlCommands.length - 1] = lastCqlQueryString.substring(0, lastCqlQueryString.length - 1) + ";";
    cqlCommands.push("/n");
    cqlCommands.push("/n");
    cqlCommands.push("/n");
    return cqlCommands;
}


function generateCqlCommands(csvColumnData, parsedData, cqlNodeName, questionnaireTypeData) {
    var titleRowData = parsedData[0];
    var csvModelData = {'order': {}};
    _.each(csvColumnData, function (columnData, columnName) {
        columnData.columnIndex = getColumnIndex(titleRowData, columnName);
        csvModelData[columnData.modelName] = columnData;
    });


    var cqlCommands = [];
    var orderIndex = 0;
    for (var i = 1; i < parsedData.length; i++) {
        var csvRowData = parsedData[i];
        var rowData = _.compact(csvRowData);
        if (rowData.length == 0) {// If the row is empty then add
            cqlCommands.push("/n");
            continue;
        }

        var cqlData = {};
        _.each(csvColumnData, function (columnData, columnName) {
            var propertyValue = csvRowData[columnData.columnIndex];
            if (_.isEmpty(propertyValue)) return;// If the column is empty for a selected row, then don't concatenate its value

            if (columnData.dataType == NODE_PROPERTY_TYPE.ARRAY) {
                var propertyValues = propertyValue.split(",");
                cqlData[columnData.modelName] = propertyValues;
            } else {
                cqlData[columnData.modelName] = propertyValue;
            }
        });
        cqlData["order"] = ++orderIndex;
        var cqlDataStr = stringify(cqlData, csvModelData);

        var easyVisaId_Index = getColumnIndex(titleRowData, "EasyVisaId");
        var easyVisaId = csvRowData[easyVisaId_Index];
        var cqlQueryName = cqlNodeName + "_" + easyVisaId.toLowerCase();

        var cqlQueryString = "CREATE (" + cqlQueryName + ":" + cqlNodeName + "{" + cqlDataStr + "})" + questionnaireTypeData.endOfLine;
        cqlCommands.push(cqlQueryString);
    }

    cqlCommands.push("/n");
    cqlCommands.push("/n");
    cqlCommands.push("/n");
    return cqlCommands;
}


function generateJsonObj(csvColumnData, parsedData, objKey, objVal) {
    var titleRowData = parsedData[0];
    var csvModelData = {};
    _.each(csvColumnData, function (columnData, columnName) {
        columnData.columnIndex = getColumnIndex(titleRowData, columnName);
        csvModelData[columnData.modelName] = columnData;
    });

    var jsonObj = {};
    for (var i = 1; i < parsedData.length; i++) {
        var csvRowData = parsedData[i];
        var rowData = _.compact(csvRowData);
        if (rowData.length == 0) {// If the row is empty then add
            continue;
        }

        var cqlData = {};
        _.each(csvColumnData, function (columnData, columnName) {
            var propertyValue = csvRowData[columnData.columnIndex];
            if (_.isEmpty(propertyValue)) {
                return;// If the column is empty for a selected row, then don't concatenate its value
            }
            cqlData[columnName] = toReplaceSpecialCharacters(propertyValue);
        });
        jsonObj[cqlData[objKey]] = cqlData[objVal]
    }
    return jsonObj
}


function generateRelationshipCqlCommands(versionName, parsedData, questionnaireTypeData) {
    var titleRowData = parsedData[0];
    var from_NodeType_Index = getColumnIndex(titleRowData, "From_NodeType");
    var from_NodeId_Index = getColumnIndex(titleRowData, "From_NodeId");
    var relationshipType_Index = getColumnIndex(titleRowData, "RelationshipType");
    var to_NodeType_Index = getColumnIndex(titleRowData, "To_NodeType");
    var to_NodeId_Index = getColumnIndex(titleRowData, "To_NodeId");

    var cqlCommands = [];
    for (var i = 1; i < parsedData.length; i++) {
        var csvRowData = parsedData[i];
        var rowData = _.compact(csvRowData);
        if (rowData.length == 0) {// If the row is empty then add
            cqlCommands.push("/n");
            continue;
        }

        var from_NodeType = csvRowData[from_NodeType_Index];
        var from_NodeId = csvRowData[from_NodeId_Index];
        var relationshipType = csvRowData[relationshipType_Index];
        var to_NodeType = csvRowData[to_NodeType_Index];
        var to_NodeId = csvRowData[to_NodeId_Index];

        var fromNodeName = from_NodeType + "_" + from_NodeId.toLowerCase();
        var toNodeName = to_NodeType + "_" + to_NodeId.toLowerCase();

        var relationshipQuery = ":" + relationshipType;
        if (relationshipType.split('#').length == 2) { // yes#1  ->  yes{order:0}
            relationshipQuery = ":" + relationshipType.split('#')[0] + "{order:" + relationshipType.split('#')[1] + "}";
        }

        var fromNodeCqlQuery = "MATCH (" + fromNodeName + ":" + from_NodeType + "{easyVisaId:'" + from_NodeId + "',questVersion:'" + versionName + "'})";
        var toNodeCqlQuery = "MATCH (" + toNodeName + ":" + to_NodeType + "{easyVisaId:'" + to_NodeId + "',questVersion:'" + versionName + "'})";
        var relationshipCqlQuery = "CREATE (" + fromNodeName + ")-[" + relationshipQuery + "]->(" + toNodeName + ");";
        cqlCommands.push("/n");
        cqlCommands.push(fromNodeCqlQuery);
        cqlCommands.push(toNodeCqlQuery);
        cqlCommands.push(relationshipCqlQuery);
    }

    cqlCommands.push("/n");
    cqlCommands.push("/n");
    cqlCommands.push("/n");
    return cqlCommands;
}


function generateRemoveAllNodesCqlCommand() {
    var cqlQueryString = "MATCH (n) DETACH DELETE n;";
    var cqlCommands = [];
    cqlCommands.push(cqlQueryString);
    cqlCommands.push("/n");
    cqlCommands.push("/n");
    cqlCommands.push("/n");
    return cqlCommands;
}


////////////// private methods ////////
// Json stringify has escape quotes issue, so using a custom version
function stringify(obj, csvModelData) {
    var props = [];
    _.each(obj, function (value, key) {
        props.push([key, quote(value, csvModelData[key])].join(":"));
    });
    var formatStr = props.join(",");
    return toReplaceSpecialCharacters(formatStr);
}

function quote(data, modelData) {
    if (_.isArray(data)) {
        return JSON.stringify(data);
    }

    if (modelData.prefix) {
        data = (modelData.prefix + data)
    }

    return (isNaN(data) || (modelData.dataType == NODE_PROPERTY_TYPE.STRING)) ? '"' + data + '"' : data;
}


function getSubStringBefore(stringData, char) {
    if (stringData.indexOf(char) != -1) {
        var newSubstr = stringData.substring(0, stringData.indexOf(char));
        return newSubstr;
    }
    return stringData;
}


function replaceAllUsingRegx(str, regExp, replace) {
    return str.replace(regExp, replace);
}


/**
 * bh = big hifen
 * c =  comma
 * cb =  close brace
 * d =  dot
 * e =  equal
 * f =  forward slash
 * g =  greater than
 * h =  hifen
 * l =  leass than
 * ob =  open brace
 * pr =  percentage
 * pl =  plus
 * s =  space
 * sn =  starts with number
 */

function toReplaceSpecialCharacters(inputStr) {
    inputStr = replaceString(inputStr, "$e$", "é");//e-acute
    inputStr = replaceString(inputStr, "$sq$", "'");//single quote
    inputStr = replaceString(inputStr, "$dq$", "'");//double quote
    return inputStr;
}


function toCamelCase(inputStr) {
    if (!inputStr || inputStr.length == 0)
        return inputStr;

    var firstChar = inputStr[0].toLowerCase();
    var camelCaseStr = firstChar + inputStr.substring(1);
    return camelCaseStr;
}

/////////// Export Statements


exports.replaceString = replaceString;
exports.toReplaceSpecialCharacters = toReplaceSpecialCharacters;
exports.getAssetsDirectoryPath = getAssetsDirectoryPath;
exports.getOutputDirectoryPath = getOutputDirectoryPath;
exports.replaceAll = replaceAll;
exports.replaceAllUsingRegx = replaceAllUsingRegx;
exports.getColumnIndex = getColumnIndex;
exports.generateCqlCommands = generateCqlCommands;
exports.generateSqlCommands = generateSqlCommands;
exports.generateRelationshipCqlCommands = generateRelationshipCqlCommands;
exports.generateRemoveAllNodesCqlCommand = generateRemoveAllNodesCqlCommand;
exports.writeConvertedJsonData = writeConvertedJsonData;
exports.getNeo4jUpdateFilePath = getNeo4jUpdateFilePath;
exports.getInputTypeSourceFilePath = getInputTypeSourceFilePath;
exports.writeJsonDataFile = writeJsonDataFile;
exports.generateJsonObj = generateJsonObj;



