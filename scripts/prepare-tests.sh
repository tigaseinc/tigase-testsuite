#!/bin/bash

#set -x

PROJECTS_DIR="/home/tigase/projects"
TMP_DIR="/tmp"

SRV_PACKAGES="xmltools utils extras muc pubsub server"

function build_xmltools() {
  ant clean jar-dist &> ${TMP_DIR}/xmltools-build.txt
  echo jars/tigase-xmltools.jar
}

function build_utils() {
  cp -f ../xmltools/jars/tigase-xmltools.jar libs/
  ant clean jar-dist &> ${TMP_DIR}/utils-build.txt
  echo jars/tigase-utils.jar
}

function build_server() {
  ant clean jar-dist &> ${TMP_DIR}/server-build.txt
  echo ""
}

function build_maven() {
  mvn clean package &> ${TMP_DIR}/$1-build.txt
  echo `find target -name "tigase-*"`
}

function build_extras() {
  build_maven extras
}

function build_muc() {
  build_maven muc
}

function build_pubsub() {
  build_maven pubsub
}

MY_DIR=`pwd`

cd ${PROJECTS_DIR}

# Build all server packages
for p in $SRV_PACKAGES ; do
  echo "Building package: $p"
  cd $p
  mkdir -p libs jars
  git pull
  JAR=`build_$p`
  echo "jar file: $JAR"
  if [ ! -z "${JAR}" ] ; then
    cp -f $JAR ../server/libs/tigase-$p.jar
  fi
  cd ..
done

# Copy over admin scripts
mkdir -p server/scripts/admin/
cp -f server/src/main/groovy/tigase/admin/*.groovy server/scripts/admin/

echo "Updating TTS"

cd testsuite
mkdir -p libs jars
cp ../xmltools/jars/tigase-xmltools.jar libs/
cp ../utils/jars/tigase-utils.jar libs/
git pull
ant clean jar &> ../testsuite-build.txt
cd ..

cd ${MY_DIR}
