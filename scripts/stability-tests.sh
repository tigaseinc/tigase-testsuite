#!/bin/bash

java -Xmx100M -cp jars/tigase-xmpp-testsuite.jar:libs/tigase-utils.jar:libs/tigase-xmltools.jar tigase.test.TestSuite -script scripts/stability-xmpp-tests.xmpt
