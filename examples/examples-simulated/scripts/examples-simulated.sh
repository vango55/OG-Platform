#!/bin/bash

canonicalize() {
  local _TARGET _BASEDIR
  _TARGET="$0"
  readlink -f $_TARGET 2>/dev/null || (
    cd $(dirname "$_TARGET")
    _TARGET=$(basename "$_TARGET")

    while [ -L "$_TARGET" ]
    do
      _TARGET=$(readlink "$_TARGET")
      cd $(dirname "$_TARGET")
      _TARGET=$(basename "$_TARGET")
    done
    _BASEDIR=$(pwd -P)
    echo "$_BASEDIR/$_TARGET"
  )
}

BASENAME=${0##*/}
COMPONENT=${BASENAME%.sh}
BASEDIR="$(dirname "$(dirname "$(canonicalize "$0")")")"
SCRIPTDIR="${BASEDIR}/scripts"
cd "${BASEDIR}" || exit 1

. ${SCRIPTDIR}/project-utils.sh
. ${SCRIPTDIR}/java-utils.sh
. ${SCRIPTDIR}/componentserver-init-utils.sh

if [ ! -f "${BASEDIR}"/data/masterdb/hsqldb/example-db.properties ]; then
  if [ -x ${SCRIPTDIR}/init-${PROJECT}-db.sh ]; then
    ${SCRIPTDIR}/init-${PROJECT}-db.sh || exit 1
  else
    echo The ${PROJECT} database could not be found.
    echo Please run ${SCRIPTDIR}/init-${PROJECT}-db.sh to create and populate the database.
    echo Exiting immediately...
    exit 1
  fi
fi

# Read default configs
load_default_config

# Component specific default configs
CONFIG=classpath:fullstack/fullstack-examplessimulated-bin.properties
LOGBACK_CONFIG=jetty-logback.xml
# No need to use 4g in the examples
MEM_OPTS="-Xms512m -Xmx1024m -XX:MaxPermSize=256m"

# User customizations
load_component_config ${PROJECT} ${COMPONENT}

CLASSPATH="lib/${PROJECTJAR}"
if [ -d lib/override ]; then
  CLASSPATH="$(build_classpath lib/override):${CLASSPATH}"
fi
CLASSPATH="config:${CLASSPATH}"

RETVAL=0
case "$1" in
  start)
    start
    ;;
  stop)
    stop
    ;;
  status)
    status
    ;;
  debug)
    debug
    ;;
  showconfig)
    showconfig
    ;;
  restart|reload)
    stop
    start
    ;;
  *)
    echo "Usage: $0 {start|stop|restart|status|showconfig|debug|reload}"
esac

exit ${RETVAL}
