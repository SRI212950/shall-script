#!/bin/bash
# tomcat-install.sh
# Usage: sudo ./tomcat-install.sh
# Targets: RHEL/CentOS style systems (yum).

set -euo pipefail

TOMCAT_VERSION="9.0.111"
TOMCAT_DIST="apache-tomcat-${TOMCAT_VERSION}"
TOMCAT_ZIP="${TOMCAT_DIST}.zip"
TOMCAT_URL="https://dlcdn.apache.org/tomcat/tomcat-9/v${TOMCAT_VERSION}/bin/${TOMCAT_ZIP}"
INSTALL_DIR="/opt"
TOMCAT_HOME="${INSTALL_DIR}/${TOMCAT_DIST}"
SYMLINK_START="/usr/bin/tomcat-start"
SYMLINK_STOP="/usr/bin/tomcat-stop"
TOMCAT_USER="tomcat"
TOMCAT_GROUP="tomcat"
TOMCAT_UI_USER="krishna"
TOMCAT_UI_PASS="krishna"
BACKUP_DIR="/root/tomcat-backups-$(date +%Y%m%d%H%M%S)"

echo "Starting Tomcat install script..."
if [ "$(id -u)" -ne 0 ]; then
  echo "Please run as root (sudo)." >&2
  exit 1
fi

mkdir -p "${BACKUP_DIR}"
cd "${INSTALL_DIR}"

echo "Installing required packages (wget, unzip, java)..."
yum install -y wget unzip
yum install -y java-21-openjdk-devel

echo "Creating tomcat user/group (if not exist)..."
if ! id -u "${TOMCAT_USER}" &>/dev/null; then
  groupadd -f "${TOMCAT_GROUP}"
  useradd -r -s /sbin/nologin -g "${TOMCAT_GROUP}" -d "${TOMCAT_HOME}" "${TOMCAT_USER}" || true
fi

echo "Downloading Tomcat ${TOMCAT_VERSION}..."
if [ ! -f "${TOMCAT_ZIP}" ]; then
  wget "${TOMCAT_URL}" -O "${TOMCAT_ZIP}"
fi

echo "Unzipping and setting ownership..."
if [ -d "${TOMCAT_HOME}" ]; then
  mv "${TOMCAT_HOME}" "${BACKUP_DIR}/"
fi
unzip -q "${TOMCAT_ZIP}" -d "${INSTALL_DIR}"
chmod +x "${TOMCAT_HOME}/bin/"*.sh
chown -R "${TOMCAT_USER}:${TOMCAT_GROUP}" "${TOMCAT_HOME}"

ln -sf "${TOMCAT_HOME}/bin/startup.sh" "${SYMLINK_START}"
ln -sf "${TOMCAT_HOME}/bin/shutdown.sh" "${SYMLINK_STOP}"
chmod +x "${SYMLINK_START}" "${SYMLINK_STOP}"

MANAGER_CONTEXT="${TOMCAT_HOME}/webapps/manager/META-INF/context.xml"
HOSTMANAGER_CONTEXT="${TOMCAT_HOME}/webapps/host-manager/META-INF/context.xml"
CONF_USERS="${TOMCAT_HOME}/conf/tomcat-users.xml"

comment_valve() {
  local file="$1"
  if [ -f "${file}" ]; then
    sed -i.bak -r '/<Valve[^>]*RemoteAddr[^>]*>/ {
      s/^/<!-- /
      s/$/ -->/
    }' "${file}"
    rm -f "${file}.bak"
  fi
}

comment_valve "${MANAGER_CONTEXT}"
comment_valve "${HOSTMANAGER_CONTEXT}"

if [ -f "${CONF_USERS}" ]; then
  if ! grep -q "username=\"${TOMCAT_UI_USER}\"" "${CONF_USERS}"; then
    sed -i '/<\/tomcat-users>/i \
<user username=\"'"${TOMCAT_UI_USER}"'\" password=\"'"${TOMCAT_UI_PASS}"'\" roles=\"manager-gui,manager-script,admin-gui\"/>\
' "${CONF_USERS}"
  fi
fi

SERVICE_FILE="/etc/systemd/system/tomcat.service"
if [ ! -f "${SERVICE_FILE}" ]; then
  cat > "${SERVICE_FILE}" <<EOF
[Unit]
Description=Apache Tomcat Web Application Container
After=network.target

[Service]
Type=forking
User=${TOMCAT_USER}
Group=${TOMCAT_GROUP}
Environment="JAVA_HOME=$(dirname $(dirname $(readlink -f $(command -v java))))"
Environment="CATALINA_PID=${TOMCAT_HOME}/temp/tomcat.pid"
Environment="CATALINA_HOME=${TOMCAT_HOME}"
Environment="CATALINA_BASE=${TOMCAT_HOME}"
ExecStart=${TOMCAT_HOME}/bin/startup.sh
ExecStop=${TOMCAT_HOME}/bin/shutdown.sh
Restart=on-abort

[Install]
WantedBy=multi-user.target
EOF
  systemctl daemon-reload
fi

su -s /bin/bash -c "${TOMCAT_HOME}/bin/startup.sh" "${TOMCAT_USER}" || true

echo "Install complete."
