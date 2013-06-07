#!/bin/bash

LOG_FILES=""

# SLA Server
LOG_FILES+=" optimis-sla-server/logs/catalina.out"

tail -f ${LOG_FILES} 
