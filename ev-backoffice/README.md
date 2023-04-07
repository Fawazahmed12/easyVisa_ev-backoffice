# Development Environment Setup #

### Install the following ###
 
* sdkman to manage your JDK and Grails versions on Mac or Unix like systems - `curl -s "https://get.sdkman.io" | bash` 
* Java/JDK 11 
* Grails 4.0.11 https://grails.org/download.html
* Postgresql 10.18 
* Node & NPM https://docs.npmjs.com/getting-started/installing-node
* Angular CLI https://github.com/angular/angular-cli#installation
  * Neo4j 4.1.10
      * Linux/Mac - https://neo4j.com/download-thanks/?edition=community&release=4.1.10&flavour=unix
      * Windows - https://neo4j.com/download-thanks/?edition=community&release=4.1.10&flavour=winzip

    Note - Neo4j can also be run from docker container in case you are using an M1 chip Mac

Project contains the following files and directories:

    client/
    gradle/
    gradlew
    gradlew.bat
    server/
    settings.gradle

The entire client (AngularJS) application lives in the /client folder and the entire server application lives in the /server folder.

### Database Setup ###

* Run `bin/create-user.sh`. This will create the default user (easyvisa) with correct roles. 
* Run `bin/create-db.sh`. This will create a database with correct permissions.

When this is complete, ask a team member for the latest backup of the development or QA server. You will restore this 
with psql.

**Bash:**

`psql DATABASENAME < BACKUP_FILE_NAME.sql`

**Windows:**

`psql.exe -U USERNAME -d DATABASENAME -f  BACKUP_FILE_NAME.sql`

### Application Configuration Setup ###

* After cloning the project, make a copy of `sample-my-config.yml` and name it as `my-config.yml`.
* Change the config values in `my-config.yml` if not using default values for db config.

### Neo4j Setup ###
* To setup a neo4j in your machine, follow the steps from the below link

    https://www.dropbox.com/home/EasyVisa%20-%20New/EasyVisa%20Development%20Documents?preview=Neo4j+Instructions.docx
    
* Make sure that neo4j is running on your machine
* Update `my-config.yml` with neo4j URL and the credentials


###  IntelliJ Graph DB Plugin ###
To install graph database support on your intellij, follow the steps
   
    Goto: Preferences -> Plugins
    
    Press:'Browse Repositories' -> Search 'Graph database support', will find the plugin with version 2.5.1
    
    Install and Restart your Intellij 


### Load/Update data into neo4j ###
Execute the following shell-script to load the latest neo4j data

    ./server/load_neo4j.sh
    
    
        
This script does the following actions: 
1. Delete the entire existing data ( all nodes and its relationships )
2. Load the CQL data `data-import/questionnaire-shell-data.cql` into the neo4j database.
Note - It takes upto 20 minutes on slower systems to load this data.
         
### Running the Application ###

To execute the server side application only, you can execute the bootRun task in the server project. go to the server directory and run:

    ../gradlew bootRun

Server application is running at http://localhost:8080

If getting any issues while running the app, use following command to clean the build:

    ../gradlew clean

The same can be done for the client application. go to the client directory and run:

    ../gradlew bootRun

Client application is running at http://localhost:4200

### Running the Tests of server application ###

Test environment also uses a config file for db and other configuration. Tests will look for a config file named `test-config.yml` in the server directory and use it. So make a copy of 
either `my-config.yml` or `sample-my-config.yml`. name it as `test-config.yml` and change the values if needed. after that, you can use following commands for running tests from the server directory - 

* `../gradlew test` - for running unit tests.

* `../gradlew iT` - for running integration tests.

* `grails test-app` or `../gradlew test iT` for running all tests

For running tests related to neo4j code, load the `questionnaire-graph.cql` into neo4j database. It can be done by browsing to http://localhost:7474/browser/ and pasting the cql queries. 

### Generating the API Documentation ###

To generate the api documentation, run the integration tests first. Then run the following command from server directory -

* `../gradlew asciidoctor`
    
* This will generate an api document `index.html` at following location inside server directory - `build/asciidoc/html5/index.html`
 

