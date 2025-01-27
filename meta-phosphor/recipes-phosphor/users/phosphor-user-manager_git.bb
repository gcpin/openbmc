SUMMARY = "Phosphor User Manager Daemon"
DESCRIPTION = "Daemon that does user management"
HOMEPAGE = "http://github.com/openbmc/phosphor-user-manager"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit autotools pkgconfig
inherit obmc-phosphor-dbus-service

DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-logging"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "bash"
DEPENDS += "boost"
DEPENDS += "nss-pam-ldapd"
DEPENDS += "systemd"
PACKAGE_BEFORE_PN = "phosphor-ldap"
RDEPENDS:${PN} += "bash"

inherit useradd

USERADD_PACKAGES = "${PN} phosphor-ldap"
DBUS_PACKAGES = "${USERADD_PACKAGES}"
# add groups needed for privilege maintenance
GROUPADD_PARAM:${PN} = "priv-admin; priv-operator; priv-user; priv-noaccess "
GROUPADD_PARAM:phosphor-ldap = "priv-admin; priv-operator; priv-user; priv-noaccess "

DBUS_SERVICE:${PN} += "xyz.openbmc_project.User.Manager.service"
FILES:phosphor-ldap += " \
        ${bindir}/phosphor-ldap-conf \
        ${bindir}/phosphor-ldap-mapper \
"
FILES:${PN} += " \
        ${datadir}/dbus-1 \
"
DBUS_SERVICE:phosphor-ldap = " \
        xyz.openbmc_project.Ldap.Config.service \
        xyz.openbmc_project.LDAP.PrivilegeMapper.service \
"
SRC_URI += "git://github.com/ibm-openbmc/phosphor-user-manager;nobranch=1"
SRCREV = "16ec770914d2be3c407aced14fbca46ac9824f83"
S = "${WORKDIR}/git"

EXTRA_OECONF:append = "enable_root_user_mgmt=no"

SRC_URI += "file://upgrade_ibm_service_account.sh"
FILES:${PN} += " /home/service/.profile "
do_install:append() {
  install -d ${D}/home/service
  echo "/usr/bin/sudo -i;exit" >${D}/home/service/.profile
  install -d ${D}${bindir}
  install -m 0755 ${WORKDIR}/upgrade_ibm_service_account.sh ${D}${bindir}/upgrade_ibm_service_account.sh
}
