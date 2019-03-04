#!/bin/bash
#
# Tigase Test Suite - Tigase Test Suite - automated testing framework for Tigase Jabber/XMPP Server.
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

VERSION="2.0.0"

SETTINGS_FILE=`dirname ${0}`/tests-runner-settings.sh
[[ -f ${SETTINGS_FILE} ]] && source ${SETTINGS_FILE} \
  || {
	echo "Can't find settings file: ${SETTINGS_FILE} using defaults"
	server_dir="../tigase-server"
	server_ip="127.0.0.1"
	server_timeout=30

    DATABASES=("derby" "mysql" "pgsql" "mssql" mongodb)
    DATABASES_IPS=("127.0.0.1" "127.0.0.1" "127.0.0.1" "127.0.0.1" "127.0.0.1")
    IPS=("127.0.0.1" "127.0.0.1" "127.0.0.1" "127.0.0.1" "127.0.0.1")

	database="derby"
	database_host="localhost"

	db_user="tigase_test_user"
	db_pass="tigase_test_pass"
	db_name="tigase_test_db"

    db_root_user="root"
    db_root_pass="root"

	MS_MEM=100
	MX_MEM=1000
	SMALL_MS_MEM=10
	SMALL_MX_MEM=20
}
export MIN_MEM=$MS_MEM
export MAX_MEM=$MX_MEM

FUNCTIONS_FILE=`dirname ${0}`/tests-runner-functions.sh
[[ -f ${FUNCTIONS_FILE} ]] && source ${FUNCTIONS_FILE} \
  || { echo "Can't find functions file: ${FUNCTIONS_FILE}" ; exit 1 ; }


function usage() {
	echo "Run selected or all tests for Tigase server"
	echo "----"
	echo "Author: Artur Hefczyc"
	echo "----"
	echo "  --help|-h	This help message"
	echo "  --func [mysql|pgsql|derby|mssql|mongodb]"
	echo "              Run all functional tests for a single database configuration"
	echo "  --lmem [mysql|pgsql|derby|mssql|mongodb]"
	echo "              Run low memory tests for a single database configuration"
	echo "  --perf [mysql|pgsql|derby|mssql|mongodb]"
	echo "              Run all performance tests for a single database configuration"
	echo "  --stab [mysql|pgsql|derby|mssql|mongodb]"
	echo "              Run all stability tests for a single database"
	echo "              configuration"
	echo "  --func-all  Run all functional tests for all database"
	echo "              configurations"
	echo "  --lmem-all  Run low memory tests for all database"
	echo "              configurations"
	echo "  --perf-all  Run all performance tests for all database"
	echo "              configurations"
	echo "  --stab-all  Run all stability tests for all database"
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
	echo "  --small-mem|-sm            Run in small memory mode"
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
		--small-mem|-sm)
			export MIN_MEM=$SMALL_MS_MEM
			export MAX_MEM=$SMALL_MX_MEM
			shift
			;;
		*)
			found=0
			;;
	esac
done

case "${1}" in
	--func|--lmem|--perf|--stab)
		[[ -z ${2} ]] || database=${2}
		[[ -z ${3} ]] || server_dir=${3}
		[[ -z ${4} ]] || server_ip=${4}
		;;
	--func-all|--lmem-all|--perf-all|--stab-all|--all-tests)
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

server_binary="${server_dir}/jars/tigase-server.jar"
if [ -f "${server_dir}/jars/tigase-server-dist.jar" ] ; then
    server_binary="${server_dir}/jars/tigase-server-dist.jar"
fi

ver=`unzip -qc ${server_binary} META-INF/MANIFEST.MF | grep "Tigase-Version" | sed -e "s/Tigase-Version: \(.*\)/\\1/" | sed 's/[[:space:]]//'`

output_dir="${ROOT_DIR}files/static/tests/${ver}"

echo "Tigase server home directory: ${server_dir}"
echo "Version: ${ver}"
echo "Output dir: ${output_dir}"

case "${1}" in
	--help|-h)
		usage
		;;
	--func)
		run_test "func" ${database} ${server_dir} ${server_ip} ${database_host}
		;;
	--lmem)
		export MIN_MEM=$SMALL_MS_MEM
		export MAX_MEM=$SMALL_MX_MEM
		run_test "lmem" ${database} ${server_dir} ${server_ip} ${database_host}
		;;
	--func-all)
		cp -f func-rep.html_tmp func-rep.html
		echo "<tr><th>${ver}</th>" >> func-rep.html

        idx=0
		for database in ${TESTS[*]} ; do
		    run_test "func" ${database} ${server_dir} ${IPS[idx]} ${DATABASES_IPS[idx]}
		    idx=$(expr $idx + 1)
            sleep $(((${server_timeout} * 2)))
        done

		echo "</tr>" >> func-rep.html
		;;
	--lmem-all)
		export MIN_MEM=$SMALL_MS_MEM
		export MAX_MEM=$SMALL_MX_MEM
		cp -f lmem-rep.html_tmp lmem-rep.html
		echo "<tr><th>${ver}</th>" >> func-rep.html

        idx=0
		for database in ${TESTS[*]} ; do
		    run_test "lmem" ${database} ${server_dir} ${IPS[idx]} ${DATABASES_IPS[idx]}
		    idx=$(expr $idx + 1)
            sleep $(((${server_timeout} * 2)))
        done
		echo "</tr>" >> lmem-rep.html
		;;
	--perf)
		run_test "perf" ${database} ${server_dir} ${server_ip} ${database_host}
		;;
	--stab)
		run_test "stab" ${database} ${server_dir} ${server_ip} ${database_host}
		;;
	--perf-all)
		cp -f perf-rep.html_tmp perf-rep.html
		echo "<tr><th>${ver}</th>" >> perf-rep.html
        idx=0
		for database in ${TESTS[*]} ; do
		    run_test "perf" ${database} ${server_dir} ${IPS[idx]} ${DATABASES_IPS[idx]}
		    idx=$(expr $idx + 1)
            sleep $(((${server_timeout} * 2)))
        done
		echo "</tr>" >> perf-rep.html
		;;
	--stab-all)
		cp -f stab-rep.html_tmp stab-rep.html
		echo "<tr><th>${ver}</th>" >> stab-rep.html
		for database in ${TESTS[*]} ; do
		    run_test "stab" ${database} ${server_dir} ${IPS[idx]} ${DATABASES_IPS[idx]}
		    idx=$(expr $idx + 1)
            sleep $(((${server_timeout} * 2)))
        done
		echo "</tr>" >> stab-rep.html
		;;
	--all-tests)
		${0} --func-all ${server_dir}
		sleep $(((${server_timeout} * 2)))
		${0} --perf-all ${server_dir}
		#${0} --stab-all ${server_dir}
		;;
	--single)
		run_test "sing" ${database} ${server_dir} ${server_ip} ${database_host} ${2}
		;;
	--other)
		run_test "other" ${database} ${server_dir} ${server_ip} ${database_host} ${2}
		;;
	*)
		[[ -z "${1}" ]] || echo "Invalid command '$1'"
		usage
		exit 1
		;;
esac
