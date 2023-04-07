var _ = require('lodash');
var async = require('async');
var Promise = require('bluebird');
var path = require('path');
var cqlGeneratorUtil = require('./../cqlgenerator.util.js');


function generateSqlFile(questionnaireDataType, parsedData, versionName, outputCqlFilePath) {
    var cqlCommands = generateCqlCommands(questionnaireDataType, parsedData, versionName);
    return cqlGeneratorUtil.writeConvertedJsonData(outputCqlFilePath, cqlCommands);
}


function generateCqlCommands(questionnaireDataType, parsedData, versionName) {
    var sqlNodeName = 'petitioner_beneficiary_mapping';
    var benefitCategoryColumnData = {
        "Petitioner_SectionNodeId": {'columnIndex': -1, 'modelName': "petitioner_section_nodeid", 'dataType':'string'},
        "Petitioner_SubSectionNodeId": {'columnIndex': -1, 'modelName': "petitioner_subsection_nodeid", 'dataType':'string'},
        "Petitioner_QuestionNodeId": {'columnIndex': -1, 'modelName': "petitioner_question_nodeid", 'dataType':'string'},
        "Petitioner_RepeatingQuestionGroup": {'columnIndex': -1, 'modelName': "petitioner_repeatingquestiongroup"},
        "Beneficiary_SectionNodeId": {'columnIndex': -1, 'modelName': "beneficiary_section_nodeid", 'dataType':'string'},
        "Beneficiary_SubSectionNodeId": {'columnIndex': -1, 'modelName': "beneficiary_subsection_nodeid", 'dataType':'string'},
        "Beneficiary_QuestionNodeId": {'columnIndex': -1, 'modelName': "beneficiary_question_nodeid", 'dataType':'string'},
        "Beneficiary_RepeatingQuestionGroup": {'columnIndex': -1, 'modelName': "beneficiary_repeatingquestiongroup"}
    };
    return cqlGeneratorUtil.generateSqlCommands(questionnaireDataType, versionName, sqlNodeName, benefitCategoryColumnData, parsedData);
}

exports.generateSqlFile = generateSqlFile;