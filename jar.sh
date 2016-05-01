#!/bin/bash

function check_error
{
    local status=$?
    if [ $status -ne 0 ]; then
        echo "* $1"
        exit $status
   fi
}

mkdir -p out/production/turaco
mkdir -p jar
javac -d out/production/turaco/ src/**/*.java
check_error "Javac failed"

cd out/production/turaco
jar -cvfe ../../../jar/turaco.jar bexpred.BExpred **/*.class
check_error "Creating jar failed"
cd ../../../