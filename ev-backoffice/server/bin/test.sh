#!/bin/bash


export DB_NAME="easyvisa_test"
export DB_USER="easyvisa"
export DB_PASSWORD="easyvisa"
export ROOT_DIR=${ROOT_DIR:-./}
bin/setup-test-db.sh
../gradlew clean; ../gradlew -Dgrails.env=test test iT
