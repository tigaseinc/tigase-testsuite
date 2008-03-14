#!/bin/bash
##
##  Tigase Jabber/XMPP Test Suite
##  Copyright (C) 2004-2007 "Artur Hefczyc" <artur.hefczyc@tigase.org>
##
##  This program is free software: you can redistribute it and/or modify
##  it under the terms of the GNU General Public License as published by
##  the Free Software Foundation version 3 of the License.
##
##  This program is distributed in the hope that it will be useful,
##  but WITHOUT ANY WARRANTY; without even the implied warranty of
##  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
##  GNU General Public License for more details.
##
##  You should have received a copy of the GNU General Public License
##  along with this program. Look for COPYING file in the top folder.
##  If not, see http://www.gnu.org/licenses/.
##
##  $Rev: $
##  Last modified by $Author: $
##  $Date: $
##

VERSION="2.0.0"

SETTINGS_FILE=`dirname ${0}`/tests-runner-settings.sh
[[ -f ${SETTINGS_FILE} ]] && source ${SETTINGS_FILE} \
  || {
	echo "Can't find settings file: ${SETTINGS_FILE} using defaults"
	server_dir="../server"
	database="xmldb"
	server_ip="127.0.0.1"
}

FUNCTIONS_FILE=`dirname ${0}`/tests-runner-functions.sh
[[ -f ${FUNCTIONS_FILE} ]] && source ${FUNCTIONS_FILE} \
  || { echo "Can't find functions file: ${FUNCTIONS_FILE}" ; exit 1 ; }


function usage() {
	echo "Run selected or all tests for Tigase server"
	echo "----"
	echo "Author: Artur Hefczyc <artur_hefczyc@vnu.co.uk>"
	echo "Version: ${VERSION}"
	echo "----"
	echo "  --help|-h	This help message"
	echo "  --func [mysql|pgsql|xmldb|sm-mysql]"
	echo "              Run all functional tests for single database"
	echo "		          configuration"
	echo "  --perf [mysql|pgsql|xmldb|sm-mysql]"
	echo "              Run all performance tests for single database"
	echo "              configuration"
	echo "  --func-all  Run all functional tests for all database"
	echo "              configurations"
	echo "  --perf-all  Run all performance tests for all database"
	echo "              configurations"
	echo "  --all-tests Run all functionality and performance tests for"
	echo "              database configurations"
	echo "  --single test_file.cot"
	echo "  --other script_file.xmpt"
	echo "----"
	echo "  Special parameters only at the beginning of the parameters list"
	echo "  --debug|-d                 Turns on debug mode"
	echo "  --skip-db-relad|-no-db     Turns off reloading database"
	echo "  --skip-server|-no-serv     Turns off Tigase server start"
	echo "-----------"
	echo "  Other possible parameters are in following order:"
	echo "  [server-dir] [server-ip]"
}

found=1
while [ "${found}" == "1" ] ; do
	case "${1}" in
    --debug|-d)
			set -x
			shift
			;;
		--skip-db-relad|-no-db)
			export SKIP_DB_RELOAD=1
			shift
			;;
		--skip-server|-no-serv)
			export SKIP_SERVER_START=1
			shift
			;;
		*)
			found=0
			;;
	esac
done


case "${1}" in
	--func|--perf)
		[[ -z ${2} ]] || database=${2}
		[[ -z ${3} ]] || server_dir=${3}
		[[ -z ${4} ]] || server_ip=${4}
		;;
	--func-all|--perf-all|--all-tests)
		[[ -z ${2} ]] || server_dir=${2}
		[[ -z ${3} ]] || server_ip=${3}
		;;
	--single|--other)
		[[ -z ${3} ]] || server_dir=${3}
		[[ -z ${4} ]] || server_ip=${4}
		;;
	*)
		[[ -z "${1}" ]] || echo "Invalid command '$1'"
		usage
		exit 1
		;;
esac

ver=`grep Tigase-Version: ${server_dir}/MANIFEST.MF | tail -n 1 | sed -e "s/Tigase-Version: \(.*\)/\1/"`
output_dir="files/static/tests/${ver}"

echo "Tigase server home directory: ${server_dir}"
echo "Version: ${ver}"

case "${1}" in
	--help|-h)
		usage
		;;
	--func)
		run_functional_test ${database} ${server_dir} ${server_ip}
		;;
	--func-all)
		cp -f func-rep.html_tmp func-rep.html
		echo "<tr><th>${ver}</th>" >> func-rep.html
		run_functional_test xmldb ${server_dir} ${IPS[0]}
		run_functional_test mysql ${server_dir} ${IPS[1]}
		run_functional_test pgsql ${server_dir} ${IPS[2]}
		#run_functional_test sm-mysql ${server_dir} ${IPS[3]}
		echo "</tr>" >> func-rep.html
		;;
	--perf)
		run_performance_test ${database} ${server_dir} ${server_ip}
		;;
	--perf-all)
		cp -f perf-rep.html_tmp perf-rep.html
		echo "<tr><th>${ver}</th>" >> perf-rep.html
		run_performance_test xmldb ${server_dir} ${IPS[0]}
		run_performance_test mysql ${server_dir} ${IPS[1]}
		run_performance_test pgsql ${server_dir} ${IPS[2]}
		#run_performance_test sm-mysql ${server_dir} ${IPS[3]}
		echo "</tr>" >> perf-rep.html
		;;
	--all-tests)
		${0} --func-all ${server_dir}
		${0} --perf-all ${server_dir}
		;;
	--single)
		run_single_test ${database} ${server_dir} ${server_ip} ${2}
		;;
	--other)
		run_other_test ${database} ${server_dir} ${server_ip} ${2}
		;;
	*)
		[[ -z "${1}" ]] || echo "Invalid command '$1'"
		usage
		exit 1
		;;
esac
