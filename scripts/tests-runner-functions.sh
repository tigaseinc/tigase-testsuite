#!/bin/bash

# This file contains functions definition used
# in all other scripts.

_classpath="jars/tigase-xmpp-testsuite.jar:libs/tigase-utils.jar:libs/tigase-xmltools.jar"
_properties="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -Dcom.sun.management.jmxremote"
_options=" -server -Xmx100M"

function db_reload_mysql() {

	[[ -z ${1} ]] && local _src_dir="${server_dir}" || local _src_dir=${1}
	[[ -z ${2} ]] && local _db_name="${db_name}" || local _db_name=${2}
	[[ -z ${3} ]] && local _db_user="${db_user}" || local _db_user=${3}
	[[ -z ${4} ]] && local _db_pass="${db_pass}" || local _db_pass=${4}

	mysqladmin -u ${_db_user} -p${_db_pass} -f drop ${_db_name}
	mysqladmin -u ${_db_user} -p${_db_pass} create ${_db_name}
	mysql -u ${_db_user} -p${_db_pass} ${_db_name} \
		< ${_src_dir}/database/mysql-schema.sql
}

function db_reload_pgsql() {

	[[ -z ${1} ]] && local _src_dir="${server_dir}" || local _src_dir=${1}
	[[ -z ${2} ]] && local _db_name="${db_name}" || local _db_name=${2}
	[[ -z ${3} ]] && local _db_user="${db_user}" || local _db_user=${3}

	dropdb -U ${_db_user} ${_db_name}
	createdb -U ${_db_user} ${_db_name}
	psql -q -U ${_db_user} -d ${_db_name} \
		-f ${_src_dir}/database/postgresql-schema.sql
}

function fs_prepare_files() {
	rm -f etc/*.xml
	cp -f func-rep.html_tmp func-rep.html
	cp -f perf-rep.html_tmp perf-rep.html
}

function tig_start_server() {

	[[ -z ${1} ]] && local _src_dir="../server" || local _src_dir=${1}
	[[ -z ${2} ]] && local _config_file="etc/tigase-mysql.conf" \
		|| local _config_file=${2}

	rm -f ${_src_dir}/logs/tigase.pid
	killall java
	sleep 2
	${_src_dir}/scripts/tigase.sh start ${_config_file}

}

function tig_stop_server() {

	[[ -z ${1} ]] && local _src_dir="../server" || local _src_dir=${1}
	[[ -z ${2} ]] && local _config_file="etc/tigase-mysql.conf" \
		|| local _config_file=${2}

	${_src_dir}/scripts/tigase.sh stop ${_config_file}
	sleep 2
	killall java

}

function ts_start() {

	[[ -z ${1} ]] && local _test_script="scripts/all-xmpp-tests.xmpt" \
		|| local _test_script=${1}
	[[ -z ${2} ]] && local _server_ip="" || local _server_ip="-serverip ${2}"
	[[ -z ${3} ]] && local _output_file="" \
		|| local _output_file="-output-file ${3}"

	java ${_options} ${_properties} -cp "${_classpath}" \
		tigase.test.TestSuite -script ${_test_script} \
		${_output_file} ${_server_ip}

}

function run_test() {

	[[ -z ${1} ]] && local _test_type="func" || local _test_type=${1}
	[[ -z ${2} ]] && local _database=${database} || local _database=${2}
	[[ -z ${3} ]] && local _server_dir=${server_dir} || local _server_dir=${3}
	[[ -z ${4} ]] && local _server_ip=${server_ip} || local _server_ip=${4}

	local _output_dir="${output_dir}/${_test_type}/${_database}"

	case ${_test_type} in
		func)
			local _output_file="${_output_dir}/functional-tests.html"
			local _script_file="scripts/all-xmpp-tests.xmpt"
			;;
		perf)
			local _output_file="${_output_dir}/performance-tests.html"
			local _script_file="scripts/perform-xmpp-tests.xmpt"
			;;
		*)
			echo "Unsupported test type: '${_test_type}'"
			usage
			exit 1
			;;
	esac

	case ${_database} in
		mysql|sm-mysql)
			db_reload_mysql
			;;
		pgsql)
			db_reload_pgsql
			;;
		xmldb)
			rm -f user-repository.xml
			;;
		*)
			echo "Not supported database: '${database}'"
			usage
			exit 1
			;;
	esac
	fs_prepare_files
	sleep 1
	tig_start_server ${_server_dir} "etc/tigase-${_database}.conf"
	sleep 2
	ts_start scripts/add-admin.xmpt ${_server_ip}
	sleep 1
	mkdir -p "${_output_dir}"
	echo -e "\nRunning: ${ver}-${_database} test, IP ${_server_ip}..."
	start_test=`date +%s`
	ts_start ${_script_file} ${_server_ip} ${_output_file}
	end_test=`date +%s`
	total_time=$((end_test-start_test))
	total_str=`date -u -d @$total_time +%H:%M:%S`
	echo "<td><A href=\"/${dr}/func/${t}/functional-tests.html\">${total_str}</A></td>" >> "${_test_type}-rep.html"
	echo "Test finished after: ${total_str}"
	sleep 1
	tig_stop_server ${_server_dir} "etc/tigase-${_database}.conf"

}

function run_functional_test() {

	[[ -z ${1} ]] && local _database=${database} || local _database=${1}
	[[ -z ${2} ]] && local _server_dir=${server_dir} || local _server_dir=${2}
	[[ -z ${3} ]] && local _server_ip=${server_ip} || local _server_ip=${3}

	run_test "func" ${_database} ${_server_dir} ${_server_ip}

}

function run_performance_test() {

	[[ -z ${1} ]] && local _database=${database} || local _database=${1}
	[[ -z ${2} ]] && local _server_dir=${server_dir} || local _server_dir=${2}
	[[ -z ${3} ]] && local _server_ip=${server_ip} || local _server_ip=${3}

	run_test "perf" ${_database} ${_server_dir} ${_server_ip}

}
