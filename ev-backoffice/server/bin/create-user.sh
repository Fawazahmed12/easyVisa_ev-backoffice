#!/bin/bash

DB_NAME=${DB_NAME:-easyvisa_db}
DB_USER=${DB_USER:-easyvisa}
DB_PASSWORD=${DB_PASSWORD:-easyvisa}
export PGPASSWORD=${DB_PASSWORD:-easyvisa}

psql -U postgres -d postgres <<EOF
drop user if exists ${DB_USER};
drop role if exists ${DB_USER};
create role ${DB_USER} with login superuser password '${DB_PASSWORD}';
EOF