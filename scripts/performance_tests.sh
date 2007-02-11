#!/bin/bash

# java -Dcom.sun.management.jmxremote -Xmx100M -jar jars/xmpp-test.jar -script scripts/perform-xmpp-tests.xmpt
java -Xmx100M -cp jars/tigase-xmpp-testsuite.jar:libs/tigase-utils.jar:libs/tigase-xmltools.jar tigase.test.TestSuite -script scripts/perform-xmpp-tests.xmpt $*
