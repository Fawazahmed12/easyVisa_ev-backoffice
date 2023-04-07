var cqlGeneratorUtil = require('./../cqlgenerator.util.js');


function generateCqlFile(parsedData, cqlNodeName, versionName, questionnaireDataType, outputCqlFilePath) {
    var cqlCommands = generateCqlCommands(parsedData, cqlNodeName, questionnaireDataType);
    return cqlGeneratorUtil.writeConvertedJsonData(outputCqlFilePath, cqlCommands);
}


function generateCqlCommands(parsedData, cqlNodeName, questionnaireDataType) {
    var documentHelpColumnData = {
        "EasyVisaId": {'columnIndex': -1, 'modelName': "easyVisaId"},
        "QuestVersion": {'columnIndex': -1, 'modelName': "questVersion"},
        "Name": {'columnIndex': -1, 'modelName': "name"},
        "Tooltip": {'columnIndex': -1, 'modelName': "tooltip"}
    };
    return cqlGeneratorUtil.generateCqlCommands(documentHelpColumnData, parsedData, cqlNodeName, questionnaireDataType);
}


exports.generateCqlFile = generateCqlFile;