#!/bin/bash

# Read config properties (USERNAME, PASSWORD, DATABASE, TABLE)
. ./readConfig.sh

mysql -u ${USERNAME} -p${PASSWORD} --execute="SELECT * FROM ${DATABASE}.${TABLE};"

