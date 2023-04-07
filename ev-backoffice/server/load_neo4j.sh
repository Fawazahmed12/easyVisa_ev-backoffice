#!/bin/bash

if ! type neo4j &>/dev/null; then
  echo "NEO4J IS NOT INSTALLED"
  exit 2
fi

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"

echo "STARTED LOAD NEO4J DATA..."

echo "DELETING ALL DATA FROM NEO4J ..."
cypher-shell -u neo4j -p easyvisa "MATCH (n) DETACH DELETE n;"

echo "LOADING DATA INTO NEO4J ..."
cd $DIR/../data-import/output
cat questionnaire-shell-data.cql | cypher-shell -u neo4j -p easyvisa
echo "...SUCCESSFULLY LOADED NEO4J DATA"


