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

# This file contains functions definition used
# in all other scripts.

_classpath="jars/tigase-testsuite.jar:libs/tigase-utils.jar:libs/tigase-xmltools.jar"
_properties="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -Dcom.sun.management.jmxremote"
#_options=" -agentlib:yjpagent -server -Xmx400M"
_options=" -server -Xmx600M"

function get_database_uri() {
	[[ -z ${1} ]] && local _db_type="${db_type}" || local _db_type=${1}
	[[ -z ${2} ]] && local _database_host="${database_host}" || local _database_host=${2}
	[[ -z ${3} ]] && local _src_dir="${server_dir}" || local _src_dir=${3}
	[[ -z ${4} ]] && local _db_name="${db_name}" || local _db_name=${4}
	[[ -z ${5} ]] && local _db_user="${db_user}" || local _db_user=${5}
	[[ -z ${6} ]] && local _db_pass="${db_pass}" || local _db_pass=${6}
	[[ -z ${7} ]] && local _db_root_user="${db_root_user}" || local _db_root_user=${7}
	[[ -z ${8} ]] && local _db_root_pass="${db_root_pass}" || local _db_root_pass=${8}

    case ${_db_type} in
        mysql)
            export JDBC_URI="jdbc:mysql://${_database_host}/${_db_name}?user=${_db_user}&password=${_db_pass}&useSSL=false&useUnicode=true"
            ;;
        pgsql)
            export JDBC_URI="jdbc:postgresql://${_database_host}/${_db_name}?user=${_db_user}&password=${_db_pass}"
            ;;
        mssql)
            export JDBC_URI="jdbc:jtds:sqlserver://${_database_host}:1433;databaseName=${_db_name};user=${_db_user};password=${_db_pass};schema=dbo;lastUpdateCount=false"
            ;;
        derby)
            export JDBC_URI="jdbc:derby:"`pwd`"/${_db_name};create=true"
            ;;
        mongodb)
            [[ -z ${_db_user} ]] && export JDBC_URI="mongodb://${_database_host}/${_db_name}" ||  export JDBC_URI="mongodb://${_db_user}:${_db_pass}@${_database_host}/${_db_name}"
            ;;
        *)
            echo "Unknown db: ${_db_type}"
            ;;

    esac

	echo "JDBC uri: ${JDBC_URI}"

}

function db_reload_sql() {

	[[ -z ${1} ]] && local _db_type="${db_type}" || local _db_type=${1}
	[[ -z ${2} ]] && local _database_host="${database_host}" || local _database_host=${2}
	[[ -z ${3} ]] && local _src_dir="${server_dir}" || local _src_dir=${3}
	[[ -z ${4} ]] && local _db_name="${db_name}" || local _db_name=${4}
	[[ -z ${5} ]] && local _db_user="${db_user}" || local _db_user=${5}
	[[ -z ${6} ]] && local _db_pass="${db_pass}" || local _db_pass=${6}
	[[ -z ${7} ]] && local _db_root_user="${db_root_user}" || local _db_root_user=${7}
	[[ -z ${8} ]] && local _db_root_pass="${db_root_pass}" || local _db_root_pass=${8}

	tts_dir=`pwd`
	cd ${_src_dir}

	if [[ "${_db_type}" == "derby" ]] ; then
		_db_name="${tts_dir}/${_db_name}"
	fi


    ./scripts/tigase.sh destroy-schema etc/tigase.conf -T ${_db_type} -D ${_db_name} -H ${_database_host} -U ${_db_user} -P ${_db_pass} -R ${_db_root_user} -A ${_db_root_pass}

    ./scripts/tigase.sh install-schema etc/tigase.conf -T ${_db_type} -D ${_db_name} -H ${_database_host} -U ${_db_user} -P ${_db_pass} -R ${_db_root_user} -A ${_db_root_pass}

	cd ${tts_dir}
}

function db_reload_mongodb() {

	[[ -z ${2} ]] && local _db_name="${db_name}" || local _db_name=${2}

	mongo ${_db_name} --eval "printjson(db.dropDatabase())"

}

function db_reload_derby() {

	[[ -z ${1} ]] && local _src_dir="${server_dir}" || local _src_dir=${1}
	[[ -z ${2} ]] && local _db_name="${db_name}" || local _db_name=${2}

	if [ -z ${_db_name} ] ; then
		echo "No DB name set - Stopping - This would cause the attempt to delete /"
		exit 1
	fi

	rm -fr ${_db_name}/
	tts_dir=`pwd`

	cd ${_src_dir}

	DB_TYPE=derby
	DATABASE_PATH=${tts_dir}/${_db_name}

	java -cp "jars/*" tigase.db.util.DBSchemaLoader -L ALL -T ${DB_TYPE} -D ${DATABASE_PATH}

	# load component schemas
	java -cp "jars/*" tigase.db.util.DBSchemaLoader -L ALL -T ${DB_TYPE} -D ${DATABASE_PATH} -F database/${DB_TYPE}-message-archiving-schema-1.3.0.sql,database/${DB_TYPE}-pubsub-schema-3.3.0.sql,database/${DB_TYPE}-muc-schema-2.5.0.sql,database/${DB_TYPE}-socks5-schema.sql

	export JDBC_URI="$(java -cp "jars/*" tigase.db.util.DBSchemaLoader --getURI -L OFF -T ${DB_TYPE} -D ${DATABASE_PATH})"

	cd ${tts_dir}

}

function tig_start_server() {

	[[ -z ${1} ]] && local _src_dir="../server" || local _src_dir=${1}
	[[ -z ${2} ]] && local _config_file="etc/tigase-mysql.conf" || local _config_file=${2}

    cp etc/externalComponentItems ${_src_dir}/etc/ 2>/dev/null || :

	rm -f ${_src_dir}/logs/tigase.pid
	#killall java
	sleep 2
	${_src_dir}/scripts/tigase.sh clear ${_config_file}
	${_src_dir}/scripts/tigase.sh start ${_config_file}
}

function tig_stop_server() {

	[[ -z ${1} ]] && local _src_dir="../server" || local _src_dir=${1}
	[[ -z ${2} ]] && local _config_file="etc/tigase-mysql.conf" \
		|| local _config_file=${2}

	${_src_dir}/scripts/tigase.sh stop ${_config_file}
	sleep 2
	#killall java

}

function ts_start() {

	[[ -z ${1} ]] && local _test_script="scripts/all-xmpp-tests.xmpt" \
		|| local _test_script=${1}
	[[ -z ${2} ]] && local _server_ip="" || local _server_ip="-serverip ${2}"
	[[ -z ${3} ]] && local _output_file="" \
		|| local _output_file="-output-file ${3}"
	[[ -z ${4} ]] && local _extra_par_1="" || local _extra_par_1=${4}
	[[ -z ${5} ]] && local _extra_par_2="" || local _extra_par_2=${5}
	[[ -z ${6} ]] && local _extra_par_3="" || local _extra_par_3=${6}

	${JAVA_HOME}/bin/java ${_options} ${_properties} -cp "${_classpath}" \
		tigase.test.TestSuite -script ${_test_script} ${_output_file} \
		${_server_ip} ${_extra_par_1} ${_extra_par_2} ${_extra_par_3}

}

function run_test() {

	[[ -z ${1} ]] && local _test_type="func" || local _test_type=${1}
	[[ -z ${2} ]] && local _database=${database} || local _database=${2}
	[[ -z ${3} ]] && local _server_dir=${server_dir} || local _server_dir=${3}
	[[ -z ${4} ]] && local _server_ip=${server_ip} || local _server_ip=${4}
	[[ -z ${5} ]] && local _database_host=${database_host} || local _database_host=${5}
	[[ -z ${6} ]] && local _extra_par="" || local _extra_par=${6}

	local _output_dir="${output_dir}/${_test_type}/${_database}"

	[[ -z ${server_timeout} ]] && server_timeout=15

	case ${_test_type} in
		func)
			local _output_file="${_output_dir}/functional-tests.html"
			local _script_file="scripts/all-xmpp-tests.xmpt"
			;;
		lmem)
			local _output_file="${_output_dir}/low-memory-tests.html"
			local _script_file="scripts/all-xmpp-small-mem.xmpt"
			;;
		perf)
			local _output_file="${_output_dir}/performance-tests.html"
			local _script_file="scripts/perform-xmpp-tests.xmpt"
			;;
		stab)
			local _output_file="${_output_dir}/stability-tests.html"
			local _script_file="scripts/stab-xmpp-tests.xmpt"
			;;
		sing)
			local _output_file="${_output_dir}/single-test.html"
			local _script_file="scripts/single-xmpp-test.xmpt"
			local _extra_params="-source-file ${_extra_par}"
			;;
		other)
			local _output_dir="${_output_dir}/"`basename ${_extra_par} .xmpt`
			local _output_file="${_output_dir}/"`basename ${_extra_par} .xmpt`".html"
			local _script_file="${_extra_par}"
			;;
		*)
			echo "Unsupported test type: '${_test_type}'"
			usage
			exit 1
			;;
	esac

	echo "Test type:        ${_test_type}"
	echo "Database:         ${_database}"
	echo "Database IP:      ${_database_host}"
	echo "Server directory: ${_server_dir}"
	echo "Server IP:        ${_server_ip}"
	echo "Extra parameters: ${_extra_par}"

    # to clear variables from other tests
    unset JDBC_URI

	if [ -z "${SKIP_DB_RELOAD}" ] ; then
	  echo "Re-creating database: ${_database}"
		case ${_database} in
			mysql|derby|mongodb)
 				db_reload_sql ${_database} ${_database_host}
 				;;
			pgsql)
				db_reload_sql postgresql ${_database_host}
				;;
			mssql)
				db_reload_sql sqlserver ${_database_host}
				;;
			*)
				echo "Not supported database: '${database}'"
				usage
				exit 1
				;;
		esac
	else
		echo "Skipped database reloading."
	fi

	get_database_uri ${_database} ${_database_host}

	sleep 1
	if [ -z "${SKIP_SERVER_START}" ] ; then
		tig_start_server ${_server_dir} "etc/tigase-${_database}.conf"

        _PID=$(cat ${_server_dir}/logs/tigase.pid)

        sleep $(((${server_timeout} / 5)))

        counter=$(((${server_timeout} * 15)))
        while [ $counter -gt 0 ] ; do
            if ! ps -p"${_PID}" -o "pid=" >/dev/null 2>&1; then
                echo "Process is NOT running... output of ${_server_dir}/logs/tigase-console.log";

                cat ${_server_dir}/logs/tigase-console.log

                ${_server_dir}/scripts/tigase.sh stop ${_config_file}
                return
            fi

            if ! nc -z ${_server_ip} 5222 ; then
                echo -ne "waiting for server: ${counter}\r"
                sleep $(((${server_timeout} / 10)))
                counter=$((counter-5))
            else
                break;
            fi
        done

	else
		echo "Skipped Tigase server starting."
	fi

	if [ -z "${SKIP_DB_RELOAD}" ] ; then
		ts_start scripts/add-admin.xmpt ${_server_ip}
		sleep 1
	else
		echo "Skipped adming account reloading."
	fi

	mkdir -p "${_output_dir}"

	echo -e "\nRunning: ${ver}-${_database} test, IP ${_server_ip}..."
	start_test=`date +%s`
	ts_start ${_script_file} ${_server_ip} ${_output_file} ${_extra_params}
	end_test=`date +%s`
	total_time=$((end_test-start_test))

	if [[ "$(uname -s)" == "Darwin" ]] ; then
		total_str=`date -u -r $total_time +%H:%M:%S`
	else
        total_str=`date -u -d @$total_time +%H:%M:%S`
	fi

	echo "<td><a href=\"/${_output_file}\">${total_str}</a></td>" >> "${_test_type}-rep.html"
	echo "Test finished after: ${total_str}"

	sleep 1

	if [ -z "${SKIP_SERVER_START}" ] ; then
        tig_stop_server ${_server_dir} "etc/tigase-${_database}.conf"
    fi

    echo -e "\n\n\n\nAfter test server logs from: ${_server_dir}/logs/tigase-console.log";
    cat ${_server_dir}/logs/tigase-console.log


}
