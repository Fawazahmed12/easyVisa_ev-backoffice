#!/bin/bash

DB_NAME=${DB_NAME:-easyvisa_test}
DB_USER=${DB_USER:-easyvisa_test}
DB_PASSWORD=${DB_PASSWORD:-easyvisa}
export PGPASSWORD=$DB_PASSWORD

psql -U ${DB_USER} -d postgres <<EOF
drop database if exists ${DB_NAME};
EOF