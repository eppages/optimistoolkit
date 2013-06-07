#!/bin/bash

# Read config properties (USERNAME, PASSWORD, DATABASE, TABLE)
. ./readConfig.sh

echo "Running as root to remove database ${DATABASE}."

echo ${DATABASE}.${TABLE};

mysql -u root -p <<EOI
DROP TABLE IF EXISTS ${DATABASE}.${TABLE};
DROP DATABASE ${DATABASE};
EOI
