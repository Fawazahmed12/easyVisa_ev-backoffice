#!/bin/bash

if ! type neo4j &> /dev/null;
  then
    echo "NEO4J IS NOT INSTALLED"
    exit 2
fi

echo "STEP:1 STARTED NEO4J DATA GENERATION"
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd $DIR
node index.js

echo "STEP:2 STARTED LOADING CQL DATA INTO DATABASE"
cat output/questionnaire-shell-data.cql | time cypher-shell -u neo4j -p easyvisa

echo "STEP:3 INCREMENT THE NEO4J CURRENT VERSION VALUE"
NEO4J_CURRENT_FILE_PATH=$DIR/../server/src/main/resources/neo4j/neo4j_current_version.txt
NEO4J_CURRENT_VERSION=$(grep "^current_version=" $NEO4J_CURRENT_FILE_PATH | awk -F"=" '{ print $2 }')
NEO4J_NEW_VERSION=$(( NEO4J_CURRENT_VERSION + 1 ))
#Replace the update version info into the file
var1="^current_version=$NEO4J_CURRENT_VERSION"
var2="current_version=$NEO4J_NEW_VERSION"
sed -i '' -e "s/$var1/$var2/g" $NEO4J_CURRENT_FILE_PATH

echo "SUCCESSFULLY COMPLETED THE NEO4J LOAD FOR THE VERSION:$NEO4J_NEW_VERSION"
