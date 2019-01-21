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

# java -Dcom.sun.management.jmxremote -Xmx100M -jar jars/xmpp-test.jar -script scripts/perform-xmpp-tests.xmpt
java -Xmx100M -cp jars/tigase-xmpp-testsuite.jar:libs/tigase-utils.jar:libs/tigase-xmltools.jar tigase.test.TestSuite -script scripts/perform-xmpp-tests.xmpt $*