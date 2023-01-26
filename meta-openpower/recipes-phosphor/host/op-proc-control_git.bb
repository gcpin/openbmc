SUMMARY = "OpenPower procedure control"
DESCRIPTION = "Provides procedures that run against the host chipset"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

S = "${WORKDIR}/git"

inherit meson obmc-phosphor-utils pkgconfig
inherit systemd

SRC_URI += "git://github.com/openbmc/openpower-proc-control;branch=master;protocol=https"
SRCREV = "bd1d7361824f7e9a86bd26344be6b12638c11e45"

DEPENDS += " \
        phosphor-logging \
        phosphor-dbus-interfaces \
        libgpiod \
        "

EXTRA_OEMESON += "-Dtests=disabled"

# For libpdbg, provided by the pdbg package
DEPENDS += "pdbg"

EXTRA_OEMESON = " \
        -Dtests=disabled \
        "
TEMPLATE = "pcie-poweroff@.service"
INSTANCE_FORMAT = "pcie-poweroff@{}.service"
INSTANCES = "${@compose_list(d, 'INSTANCE_FORMAT', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "${TEMPLATE} ${INSTANCES}"

SYSTEMD_SERVICE:${PN} +=  " \
                         xyz.openbmc_project.Control.Host.NMI.service \
                         op-stop-instructions@.service \
                         op-cfam-reset.service \
                         ${@bb.utils.contains('MACHINE_FEATURES', 'phal', 'set-spi-mux.service', '', d)} \
                         op-continue-mpreboot@.service \
                         op-enter-mpreboot@.service \
                         ${@bb.utils.contains('MACHINE_FEATURES', 'phal', 'phal-reinit-devtree.service', '', d)} \
                         ${@bb.utils.contains('MACHINE_FEATURES', 'phal', 'proc-pre-poweroff@.service', '', d)} \
                         ${@bb.utils.contains('MACHINE_FEATURES', 'phal', 'op-reset-host-check@.service', '', d)} \
                         ${@bb.utils.contains('MACHINE_FEATURES', 'phal', 'op-reset-host-clear.service', '', d)} \
                         ${@bb.utils.contains('MACHINE_FEATURES', 'phal', 'phal-import-devtree@.service', '', d)} \
                         ${@bb.utils.contains('MACHINE_FEATURES', 'phal', 'phal-export-devtree@.service', '', d)} \
                         ${@bb.utils.contains('MACHINE_FEATURES', 'phal', 'phal-create-boottime-guard-indicator.service', '', d)} \
                         ${@bb.utils.contains('MACHINE_FEATURES', 'phal', 'op-clear-sys-dump-active@.service', '', d)} \
                         ${@bb.utils.contains('MACHINE_FEATURES', 'phal', 'op-clock-data-logger@.service', '', d)} \
                         "
