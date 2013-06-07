#!/bin/bash

# Read config properties (USERNAME, PASSWORD, DATABASE, TABLE)
. ./readConfig.sh

echo "Please enter MySQL root password to create database."

mysql -u root -p <<EOI
CREATE DATABASE IF NOT EXISTS ${DATABASE};

GRANT ALL ON ${DATABASE}.* TO ${USERNAME}@localhost IDENTIFIED BY "${PASSWORD}";
GRANT ALL ON ${DATABASE}.* TO ${USERNAME}@localhost.localdomain IDENTIFIED BY "${PASSWORD}";
EOI


exit $?
