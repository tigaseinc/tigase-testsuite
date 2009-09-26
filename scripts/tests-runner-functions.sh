#!/bin/bash
##
##  Tigase XMPP/Jabber Test Suite
##  Copyright (C) 2004-2009 "Artur Hefczyc" <artur.hefczyc@tigase.org>
##
##  This program is free software: you can redistribute it and/or modify
##  it under the terms of the GNU General Public License as published by
##  the Free Software Foundation, either version 3 of the License.
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

# This file contains functions definition used
# in all other scripts.

_classpath="jars/tigase-xmpp-testsuite.jar:libs/tigase-utils.jar:libs/tigase-xmltools.jar"
_properties="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -Dcom.sun.management.jmxremote"
#_options=" -agentlib:yjpagent -server -Xmx400M"
_options=" -server -Xmx600M"

function db_reload_mysql() {

	[[ -z ${1} ]] && local _src_dir="${server_dir}" || local _src_dir=${1}
	[[ -z ${2} ]] && local _db_name="${db_name}" || local _db_name=${2}
	[[ -z ${3} ]] && local _db_user="${db_user}" || local _db_user=${3}
	[[ -z ${4} ]] && local _db_pass="${db_pass}" || local _db_pass=${4}

	mysqladmin -u root -p${_db_pass} -f drop ${_db_name}
	mysqladmin -u root -p${_db_pass} create ${_db_name}
        echo "GRANT ALL ON ${_db_name}.* TO ${_db_user}@'%' \
                IDENTIFIED BY '${_db_pass}'; \
                FLUSH PRIVILEGES;" | mysql -u root -p${_db_pass} mysql
        echo "GRANT ALL ON ${_db_name}.* TO ${_db_user}@'localhost' \
                IDENTIFIED BY '${_db_pass}'; \
                FLUSH PRIVILEGES;" | mysql -u root -p${_db_pass} mysql
        echo "GRANT ALL ON ${_db_name}.* TO ${_db_user} \
                IDENTIFIED BY '${_db_pass}'; \
                FLUSH PRIVILEGES;" | mysql -u root -p${_db_pass} mysql
        echo "GRANT SELECT, INSERT, UPDATE ON mysql.proc TO ${_db_user}@'%'; \
                FLUSH PRIVILEGES;" | mysql -u root -p${_db_pass} mysql
        echo "GRANT SELECT, INSERT, UPDATE ON mysql.proc TO ${_db_user}@'localhost'; \
                FLUSH PRIVILEGES;" | mysql -u root -p${_db_pass} mysql
        echo "GRANT SELECT, INSERT, UPDATE ON mysql.proc TO ${_db_user}; \
                FLUSH PRIVILEGES;" | mysql -u root -p${_db_pass} mysql
	mysql -N -u ${_db_user} -p${_db_pass} ${_db_name} \
		< database/mysql-schema.sql
	mysql -N -u ${_db_user} -p${_db_pass} ${_db_name} \
		< database/mysql-schema-upgrade-to-4.sql
}

function db_reload_pgsql() {

	[[ -z ${1} ]] && local _src_dir="${server_dir}" || local _src_dir=${1}
	[[ -z ${2} ]] && local _db_name="${db_name}" || local _db_name=${2}
	[[ -z ${3} ]] && local _db_user="${db_user}" || local _db_user=${3}

	dropdb -U ${_db_user} ${_db_name}
	createdb -U ${_db_user} ${_db_name}
	psql -q -U ${_db_user} -d ${_db_name} \
		-f database/postgresql-schema.sql
	psql -q -U ${_db_user} -d ${_db_name} \
		-f database/postgresql-schema-upgrade-to-4.sql
}

function fs_prepare_files() {
	rm -f etc/*.xml
	cp -f ${server_dir}/database/* database/
}

function tig_start_server() {

	[[ -z ${1} ]] && local _src_dir="../server" || local _src_dir=${1}
	[[ -z ${2} ]] && local _config_file="etc/tigase-mysql.conf" \
		|| local _config_file=${2}

	rm -f ${_src_dir}/logs/tigase.pid
	#killall java
	sleep 2
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
	[[ -z ${5} ]] && local _extra_par="" || local _extra_par=${5}

	local _output_dir="${output_dir}/${_test_type}/${_database}"

	server_timeout=15

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
			local _output_file="${_output_dir}/other-test.html"
			local _script_file="${_extra_par}"
			server_timeout=15
			;;
		*)
			echo "Unsupported test type: '${_test_type}'"
			usage
			exit 1
			;;
	esac

	echo "Test type:        ${_test_type}"
	echo "Database:         ${_database}"
	echo "Server directory: ${_server_dir}"
	echo "Server IP:        ${_server_ip}"
	echo "Extra parameters: ${_extra_par}"

	fs_prepare_files

	if [ -z "${SKIP_DB_RELOAD}" ] ; then
		case ${_database} in
			mysql|mysql-auth|sm-mysql|mysql-custom)
				db_reload_mysql
				;;
			pgsql|pgsql-auth|pgsql-custom)
				db_reload_pgsql
				;;
			derby|derby-auth|derby-custom)
				rm -fr tigasetest/
				java -Dij.protocol=jdbc:derby: -Dij.database="tigasetest;create=true" \
					-Dderby.system.home=`pwd` \
					-cp libs/derby.jar:libs/derbytools.jar:${_server_dir}/jars/tigase-server.jar \
					org.apache.derby.tools.ij database/derby-schema-4.sql > /dev/null
				java -Dij.protocol=jdbc:derby: -Dij.database="tigasetest" \
					-Dderby.system.home=`pwd` \
					-cp libs/derby.jar:libs/derbytools.jar:${_server_dir}/jars/tigase-server.jar \
					org.apache.derby.tools.ij database/derby-schema-4-sp.schema > /dev/null
				java -Dij.protocol=jdbc:derby: -Dij.database="tigasetest" \
					-Dderby.system.home=`pwd` \
					-cp libs/derby.jar:libs/derbytools.jar:${_server_dir}/jars/tigase-server.jar \
					org.apache.derby.tools.ij database/derby-schema-4-props.sql > /dev/null
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

	sleep 1
	if [ -z "${SKIP_SERVER_START}" ] ; then
		tig_start_server ${_server_dir} "etc/tigase-${_database}.conf"
		sleep ${server_timeout}
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
	start_test=`gdate +%s`
	ts_start ${_script_file} ${_server_ip} ${_output_file} ${_extra_params}
	end_test=`gdate +%s`
	total_time=$((end_test-start_test))
	total_str=`gdate -u -d @$total_time +%H:%M:%S`
	echo "<td><a href=\"/${_output_file}\">${total_str}</a></td>" >> "${_test_type}-rep.html"
	echo "Test finished after: ${total_str}"
	sleep 1
	if [ -z "${SKIP_SERVER_START}" ] ; then
	        tig_stop_server ${_server_dir} "etc/tigase-${_database}.conf"
        fi

}

function run_functional_test() {

	[[ -z ${1} ]] && local _database=${database} || local _database=${1}
	[[ -z ${2} ]] && local _server_dir=${server_dir} || local _server_dir=${2}
	[[ -z ${3} ]] && local _server_ip=${server_ip} || local _server_ip=${3}

	run_test "func" ${_database} ${_server_dir} ${_server_ip}

}

function run_low_memory_test() {

	[[ -z ${1} ]] && local _database=${database} || local _database=${1}
	[[ -z ${2} ]] && local _server_dir=${server_dir} || local _server_dir=${2}
	[[ -z ${3} ]] && local _server_ip=${server_ip} || local _server_ip=${3}

	run_test "lmem" ${_database} ${_server_dir} ${_server_ip}

}

function run_performance_test() {

	[[ -z ${1} ]] && local _database=${database} || local _database=${1}
	[[ -z ${2} ]] && local _server_dir=${server_dir} || local _server_dir=${2}
	[[ -z ${3} ]] && local _server_ip=${server_ip} || local _server_ip=${3}

	run_test "perf" ${_database} ${_server_dir} ${_server_ip}

}

function run_stability_test() {

	[[ -z ${1} ]] && local _database=${database} || local _database=${1}
	[[ -z ${2} ]] && local _server_dir=${server_dir} || local _server_dir=${2}
	[[ -z ${3} ]] && local _server_ip=${server_ip} || local _server_ip=${3}

	run_test "stab" ${_database} ${_server_dir} ${_server_ip}

}

function run_single_test() {

	[[ -z ${1} ]] && local _database=${database} || local _database=${1}
	[[ -z ${2} ]] && local _server_dir=${server_dir} || local _server_dir=${2}
	[[ -z ${3} ]] && local _server_ip=${server_ip} || local _server_ip=${3}
	[[ -z ${4} ]] && local _stanza_file="" || local _stanza_file=${4}

	run_test "sing" ${_database} ${_server_dir} ${_server_ip} ${_stanza_file}

}

function run_other_test() {

	[[ -z ${1} ]] && local _database=${database} || local _database=${1}
	[[ -z ${2} ]] && local _server_dir=${server_dir} || local _server_dir=${2}
	[[ -z ${3} ]] && local _server_ip=${server_ip} || local _server_ip=${3}
	[[ -z ${4} ]] && local _script_file="scripts/load-xmpp-test.xmpt" \
		|| local _script_file=${4}

	run_test "other" ${_database} ${_server_dir} ${_server_ip} ${_script_file}

}
