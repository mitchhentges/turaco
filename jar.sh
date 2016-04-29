#!/bin/bash

mkdir -p out/production/turaco
mkdir -p jar
javac -d out/production/turaco/ src/**/*.java
cd out/production/turaco
jar -cvfe ../../../jar/turaco.jar bexpred.BExpred **/*.class
cd ../../../