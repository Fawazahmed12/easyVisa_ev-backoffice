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
        "DisplayText": {'columnIndex': -1, 'modelName': "displayText"},
        "DateLabel": {'columnIndex': -1, 'modelName': "dateLabel"},
        "ReminderRule": {'columnIndex': -1, 'modelName': "reminderRule"},
        "ReminderRuleParam": {'columnIndex': -1, 'modelName': "reminderRuleParam"}
    };
    return cqlGeneratorUtil.generateCqlCommands(documentHelpColumnData, parsedData, cqlNodeName, questionnaireDataType);
}


exports.generateCqlFile = generateCqlFile;
