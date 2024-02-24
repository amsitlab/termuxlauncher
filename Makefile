APP_NAME := termuxlauncher
APP_VERSION := 1.2
## main source directory
SRC_MAIN_DIR := app/src/main
## java source directory
JAVA_SRC_DIR := ${SRC_MAIN_DIR}/java
## android.jar location
ANDROIDJAR := ${PREFIX}/share/java/android.jar
ANDROIDMANIFEST := ${SRC_MAIN_DIR}/AndroidManifest.xml
## generated binary directory
BIN_DIR := bin
# java classes directory
CLASSES_DIR := ${BIN_DIR}/classes
## Required packages
REQ_PKG := ecj dx aapt apksigner openssl
## Apk output diractory"
BUILD_APK_PATH := ${EXTERNAL_STORAGE}/Download/buildAPKs
## Java file list
JAVA_FILE_LIST := $(shell find ${JAVA_SRC_DIR} -type f -name \*.java)
KEYSTORE_DIR := .keystore
KEY_PK8 := ${KEYSTORE_DIR}/key.pk8
X509_PEM := ${KEYSTORE_DIR}/cert.x509.pem
REQ_PEM := ${KEYSTORE_DIR}/req.pem
KEY_PEM := ${KEYSTORE_DIR}/key.pem
APK := ${APP_NAME}-${APP_VERSION}.apk
APK_UNSIGNED := ${APP_NAME}.unsigned.apk
APK_ALIGNED := ${APP_NAME}.aligned.apk
OSSL := openssl

.PHONY: all clean build install depcheck

all: build
	make install

depcheck:
	for x in ${REQ_PEM}; do \
		[ "$$(command -v "$$x")" ] || \
		pkg install "$$x"; \
	done
	[ "$$(command -v "openssl")" ] || pkg install openssl-tool

clean: 
	rm -rf ${BIN_DIR}

install:
	printf "%s\n%s\n%s\n" \
		"/system/bin/cp ${BUILD_APK_PATH}/${APK} /data/local/tmp/install.apk" \
		"/system/bin/pm install -g /data/local/tmp/install.apk" \
		| rish

build: clean ${JAVA_FILE_LIST} ${X509_PEM} ${KEYSTORE_DIR} ${BIN_DIR}
	aapt package -v -f \
		-M ${ANDROIDMANIFEST} \
		-S ${SRC_MAIN_DIR}/res \
		-J ${JAVA_SRC_DIR} \
		-I ${ANDROIDJAR} \
		-m
	ecj -verbose \
		-d ${CLASSES_DIR} \
		-classpath ${ANDROIDJAR} \
		-sourcepath ${JAVA_SRC_DIR} \
		${JAVA_FILE_LIST}
	dx --dex --verbose --output ${BIN_DIR}/classes.dex ${CLASSES_DIR}
	aapt package -v -f \
		-M ${ANDROIDMANIFEST} \
		-S ${SRC_MAIN_DIR}/res \
		-F ${BIN_DIR}/${APK_UNSIGNED}
	mkdir -p "${BUILD_APK_PATH}"
	cd ${BIN_DIR}; \
		aapt add -f ${APK_UNSIGNED} classes.dex; \
		zipalign 4 ${APK_UNSIGNED} ${APK_ALIGNED}; \
		apksigner sign \
			-in ${APK_ALIGNED} \
			-out ${APK} \
			-key ../${KEY_PK8} \
			-cert ../${X509_PEM}; \
		chmod 644 ${APK}; \
		cp ${APK} ${BUILD_APK_PATH}/${APK};

~/storage:
	termux-setup-storage

${BIN_DIR}:
	mkdir -p "${BIN_DIR}"

${KEYSTORE_DIR}:
	mkdir -p "${KEYSTORE_DIR}"


${X509_PEM}:
	openssl genrsa -out ${KEY_PEM} 2048
	openssl req -new -key ${KEY_PEM} -out ${REQ_PEM}
	openssl x509 -req -days 10000 -in ${REQ_PEM} \
		-signkey ${KEY_PEM} -out ${X509_PEM}
	openssl pkcs8 -topk8 -outform DER -in ${KEY_PEM} \
		-inform PEM -out ${KEY_PK8} -nocrypt

