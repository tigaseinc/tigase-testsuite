#!/bin/bash

java -Dcom.sun.management.jmxremote -Xmx100M -cp jars/tigase-xmpp-testsuite.jar:libs/tigase-utils.jar:libs/tigase-xmltools.jar tigase.test.TestSuite -script scripts/all-xmpp-tests.xmpt
