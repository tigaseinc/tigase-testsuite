#!/bin/bash

java -Dcom.sun.management.jmxremote -Xmx100M -jar jars/xmpp-test.jar -script scripts/all-xmpp-tests.xmpt
