#!/bin/bash


export DB_NAME=${TEST_DB_NAME:-easyvisa_test}
export DB_USER="easyvisa"
export DB_PASSWORD=${DB_PASSWORD:-easyvisa}

export ROOT_DIR=${ROOT_DIR:-./}
__dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
${__dir}/delete-db.sh
${__dir}/create-db.sh
