#!/bin/bash
kill -9 `ps aex | grep catalina | grep -v "skip catalina" | awk '{print $1}'` &> /dev/null
