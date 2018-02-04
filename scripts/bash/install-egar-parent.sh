#!/bin/sh
COMMIT="${1}"
git clone https://github.com/UKHomeOffice/egar-parent.git
cd egar-parent
git checkout $COMMIT
 
mvn clean install
 
cd ..
 
rm -rf egar-parent
