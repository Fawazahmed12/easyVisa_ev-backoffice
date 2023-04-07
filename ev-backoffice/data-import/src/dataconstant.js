


exports.QUESTIONNAIRE_TYPE_DATA = {
    SHELL:{
        'name':'SHELL',
        'fileName':'questionnaire-shell-data.cql',
        'endOfLine': ';',
        'generator':'cqlGenerator',
        'outputFileType':'CQL'
    },
    QUESTIONMAPPING:{
        'name':'QUESTIONMAPPING',
        'fileName':'petitioner-beneficiary-mapping.sql',
        'endOfLine': ';',
        'generator':'sqlGenerator',
        'outputFileType':'SQL',
        'idCount':0
    },
    I18N:{
        'name':'I18N',
        'fileName':'en.json',
        'endOfLine': ',',
        'generator':'jsonGenerator',
        'outputFileType':'JSON'
    }
};



exports.QUESTIONNAIRE_VERSIONS = [
    'quest_version_1',
    'quest_version_test',
    'quest_version_2',
    'quest_version_3'
];


exports.NODE_PROPERTY_TYPE = {
    ARRAY:'array',
    STRING:'string'
};
