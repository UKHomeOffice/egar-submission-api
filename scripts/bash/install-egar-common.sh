#!/bin/sh
COMMIT="${1}"
git clone https://github.com/UKHomeOffice/egar-common.git
cd egar-common
git checkout $COMMIT
 
mvn clean install
 
cd ..
 
rm -rf egar-common
