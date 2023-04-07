#!/bin/bash

DB_NAME=${DB_NAME:-easyvisa_db}
DB_USER=${DB_USER:-easyvisa}
DB_PASSWORD=${DB_PASSWORD:-easyvisa}
export PGPASSWORD=$DB_PASSWORD

psql -U ${DB_USER} -d postgres <<EOF
create database ${DB_NAME} with owner ${DB_USER};
EOF