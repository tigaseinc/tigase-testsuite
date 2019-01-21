#!/bin/bash
#
# Tigase Test Suite - Automated testing framework for Tigase Jabber/XMPP Server.
# Copyright (C) 2005 Tigase, Inc. (office@tigase.com)
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, version 3 of the License.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program. Look for COPYING file in the top folder.
# If not, see http://www.gnu.org/licenses/.
#


#set -x

if [ "${PROJECTS_DIR}" == "" ] ; then
  PROJECTS_DIR="/home/tigase/projects"
fi

echo "Project home directory: ${PROJECTS_DIR}"

if [ "${1}" == "" ] ; then
	TEST_VERSION="master"
else
	TEST_VERSION="${1}"
fi

MY_DIR=`pwd`

cd ${PROJECTS_DIR}

#get or update sources
echo "Preparing server version: ${TEST_VERSION}"
if [ ! -e tigase-server ] ; then
	git clone https://repository.tigase.org/git/tigase-server.git
else
	cd tigase-server
	git fetch origin
	cd ..
fi

#prepare specific version
cd tigase-server
git reset --hard -q origin/master
git checkout -q -f ${TEST_VERSION}
cd ..

echo "Preparing tigase testsuite"
if [ ! -e tigase-testsuite ] ; then
        git clone https://repository.tigase.org/git/tigase-testsuite.git
else
		cd tigase-testsuite
        git pull
        git checkout master
		cd ..
fi

#clean previous builds
echo "Cleaning previous builds"
rm -rf tigase-server/pack/*
rm -rf tigase-server/target/*

#create packages
echo "Creating server package"
cd tigase-server
mvn -f modules/master/pom.xml  clean package
cd ..

if [ -e tigase-server-dist ] ; then
	rm -rf tigase-server-dist || exit -1
fi

mkdir tigase-server-dist || exit -1

tar --strip-components=1 -xf tigase-server/pack/tigase-server-*-dist-max.tar.gz -C tigase-server-dist

cd tigase-testsuite
mvn clean package

cd ${MY_DIR}