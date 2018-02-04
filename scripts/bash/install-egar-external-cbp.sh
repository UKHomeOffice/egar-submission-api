#!/bin/sh
COMMIT="${1}"
git clone https://github.com/UKHomeOffice/egar-external-cbp.git
cd egar-external-cbp
git checkout $COMMIT
 
mvn clean install
 
cd ..
 
rm -rf egar-external-cbp
