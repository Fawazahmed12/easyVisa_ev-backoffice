# Development Environment Setup #

### Install the following ###
* Node & NPM https://docs.npmjs.com/getting-started/installing-node


Project contains the following files and directories:

    cql_generator/assets/
    cql_generator/output/
    cql_generator/src/
    cql_generator/index.js
    cql_generator/package.json

All the input csv files are placed in the /assets/csv_files folder and thegenerated output cql files are located in the /output folder.


### Running the Application ###

*  To generate the cql data for web-console, goto the directory cql_generator/ and run the following command

       npm run webconsole

   The above command will generate the output  **_questionnaire-webconsole-data.cql_** inside the cql_generator/output/ folder.

 

* To generate the cql data for neo4j-shell, goto the directory cql_generator/ and run the following command

      npm run shell

    The above command will generate the output file **_questionnaire-shell-data.cql_** inside the cql_generator/output/ folder.

 

