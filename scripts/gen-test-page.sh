#!/bin/bash

TEST_REPORTS="mysql pgsql derby"
TEST_TYPES="func lmem"
declare -A MAIN_FILE
MAIN_FILE=( [func]="functional-tests.html" [lmem]="low-memory-tests.html" )

function echoindex() {

  echo "$1" >> ${INDEX_DIR}/index.html

}

function gettesttime() {

  testtime=`grep "Total test time" $1 | sed -e "s/[^0-9]\+\([0-9]\+\)[^0-9]\+\([0-9]\+\)[^0-9]\+\([0-9]\+\)[^0-9]\+\([0-9]\+\).*/\1:\2:\3:\4/"`
  echo "${testtime}"

}

SRC_DIR="scripts/tests-page"
if [ -z "${INDEX_DIR}" ]; then
	INDEX_DIR="/home/tigase/public_html"
fi

if [[ ${INDEX_DIR} = *night* ]]
then
	INDEX_START="index-start-nightly.txt"
else
    INDEX_START="index-start.txt"
fi
INDEX_END="index-end.txt"
REPORTS_LOC="files/static/tests"

FAILED=false
FAIL_CNT=0

ALL_REPORTS_DIRS=`ls -1 ${INDEX_DIR}/${REPORTS_LOC} | sort -r`

cat ${SRC_DIR}/${INDEX_START} > ${INDEX_DIR}/index.html

for tt in ${TEST_TYPES} ; do
FIRST=true

  cat ${SRC_DIR}/${tt}-start.txt >> ${INDEX_DIR}/index.html

  cat ${SRC_DIR}/headers-start.txt >> ${INDEX_DIR}/index.html

  for tr in ${TEST_REPORTS} ; do

    cat ${SRC_DIR}/${tr}-start.txt >> ${INDEX_DIR}/index.html

  done

  cat ${SRC_DIR}/headers-end.txt >> ${INDEX_DIR}/index.html

  for ard in ${ALL_REPORTS_DIRS} ; do

      IS_TEST=""

      for tr in ${TEST_REPORTS} ; do

        MFILE1="${INDEX_DIR}/${REPORTS_LOC}/${ard}/${tt}/${tr}/"${MAIN_FILE[${tt}]}
        MFILE2="${INDEX_DIR}/${REPORTS_LOC}/${ard}/${tt}/${tr}-auth/functional-tests.html"
        MFILE3="${INDEX_DIR}/${REPORTS_LOC}/${ard}/${tt}/${tr}-custom/functional-tests.html"
        
        if [ -f ${MFILE1} ] || [ -f ${MFILE2} ] || [ -f ${MFILE3} ] ; then
          IS_TEST="${IS_TEST}${tr}"
        fi

      done      

      if [ ! -z ${IS_TEST} ] ; then

      echoindex "<tr><th>${ard}</th>"

      bgcolor="#90FF90"

      for tr in ${TEST_REPORTS} ; do

        MFILE1="${REPORTS_LOC}/${ard}/${tt}/${tr}/"${MAIN_FILE[${tt}]}
        MFILE2="${REPORTS_LOC}/${ard}/${tt}/${tr}-auth/"${MAIN_FILE[${tt}]}
		MFILE3="${REPORTS_LOC}/${ard}/${tt}/${tr}-custom/"${MAIN_FILE[${tt}]}

        MFILES="${MFILE1} ${MFILE2} ${MFILE3}"

        for mf in ${MFILES} ; do

          if [ -f ${INDEX_DIR}/${mf} ] ; then
            bgcolor="#90FF90"
            if grep "FAILURE" ${INDEX_DIR}/${mf} > /dev/null ; then
              bgcolor="FF9090"
            fi
            SUCCESS=`grep -c success ${INDEX_DIR}/${mf}`
            FAILURE=`grep -ic failure ${INDEX_DIR}/${mf} || true`
            FAIL_CNT=$((${FAIL_CNT}+${FAILURE})) || true

            ttime=`gettesttime ${INDEX_DIR}/${mf}`
            echoindex "<td class=\"rtecenter\" bgcolor=\"${bgcolor}\"><a href=\"${mf}\">${SUCCESS} x Pass, ${FAILURE} x Fail, $((${SUCCESS} + ${FAILURE})) x Total in ${ttime}</a></td>"
          else
            echoindex "<td class=\"rtecenter\"> --- </td>"
          fi


        done
        
      done

	#check only latest version
	if ${FIRST} ; then
	  if [[ ${FAIL_CNT} > 0 ]] || ${FAILED}  ; then 
		FAILED=true
	  fi
	  echo ${FAIL_CNT}
		FIRST=false
	fi

      echoindex "</tr>"

      fi

  done

  cat ${SRC_DIR}/table-end.txt >> ${INDEX_DIR}/index.html
  
done

cat ${SRC_DIR}/${INDEX_END} >> ${INDEX_DIR}/index.html

if  ${FAILED} ; then
	echo "ERROR: BUILD FAILED"
	exit 1
else
	echo "BUILD SUCCEEDED"
fi
