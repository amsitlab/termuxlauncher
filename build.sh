#!/data/data/com.termux/files/usr/bin/bash

## This script to build "Termux Launcher"
## "Termux Launcher" is android application to
## 	make  "Termux" as launcher without hack anything
##	of  "Termux" application.
## This apps will creating shell script located on
##	/sdcard/termuxlauncher/.apps-launcher
## Recomended: if you want use "launch" command to
##	launch app with cmmand on termux anytime.
##	write to your ~/.bashrc or your ~/bash_profile
##	like this:
##		source /storage/shared/termuxlauncher/.apps-launcher
##

## Author: Amsit (@amsitlab) <dezavue3@gmail.com>
## Thank to:
##	forwall (author of the termux)
##	sdrausty (refference to build apk on termux)
##	BlackHoleSecurity (community to learning code)
##

## Application name
APP_NAME="termuxlauncher"

APP_VERSION="1.1"

## main source directory
SRC_MAIN_DIR="app/src/main"

## java source directory
JAVA_SRC_DIR=$SRC_MAIN_DIR/java

## android.jar location
ANDROIDJAR=${PREFIX}/share/java/android.jar

ANDROIDMANIFEST=${SRC_MAIN_DIR}/AndroidManifest.xml

## generated binary directory
BIN_DIR="bin"

# java classes directory
CLASSES_DIR=${BIN_DIR}/classes


## Required packages
REQ_PKG=("ecj" "dx" "aapt" "apksigner")

## Requred package length
REQ_LEN=${#REQ_PKG[@]}

## Apk output diractory"
BUILD_APK_PATH=${EXTERNAL_STORAGE}/Download/buildAPKs

## Java file list
JAVA_FILE_LIST=`find ${JAVA_SRC_DIR} -type f -name \*.java`


KEYSTORE_DIR=".keystore"

KEY_PK8=${KEYSTORE_DIR}/key.pk8
X509_PEM=${KEYSTORE_DIR}/cert.x509.pem
REQ_PEM=${KEYSTORE_DIR}/req.pem
KEY_PEM=${KEYSTORE_DIR}/key.pem

APK=${APP_NAME}-${APP_VERSION}.apk
APK_UNSIGNED=${APP_NAME}.unsigned.apk
APK_ALIGNED=${APP_NAME}.aligned.apk


## Check required package and install if not installed
for((i=0; i<$REQ_LEN; i++));
do
	exists=`which ${REQ_PKG[$i]}`
	if [ -z "$exists" ]; then
		apt install -y ${REQ_PKG[$i]}
	fi
done

OSSL=openssl
if [[ "x${OSSL}" = "x" ]] ; then
  apt install -y openssl-tool
fi


## Make sure shared storage has set up
test ! -d ~/storage &&
	termux-setup-storage

##
test ! -d $BUILD_APK_PATH &&
	mkdir -p $BUILD_APK_PATH


rm -f $BIN_DIR

## Creating R.java
echo
echo "aapt begun: creating R.java"
echo
sleep 1
aapt package -v -f \
	-M ${ANDROIDMANIFEST} \
	-S ${SRC_MAIN_DIR}/res \
	-J ${JAVA_SRC_DIR} \
	-I ${ANDROIDJAR} \
	-m
sleep 1
echo

## Compile to classes
echo "ecj begun: compiling"
echo
sleep 1
ecj -verbose \
	-d $CLASSES_DIR \
	-classpath $ANDROIDJAR \
	-sourcepath $JAVA_SRC_DIR \
	$JAVA_FILE_LIST
sleep 1
echo

## Dexing all .class on ./obj path
echo "dx begun: creating ${BIN_DIR}/classes.dex"
echo
test ! -d $BIN_DIR &&
	mkdir -p $BIN_DIR
sleep 1
dx --dex --verbose --output ${BIN_DIR}/classes.dex $CLASSES_DIR
sleep 1
echo

## Creating unsigned appliaction file
echo "aapt bagun: creating ${BIN_DIR}/${APK_UNSIGNED}"
echo
sleep 1
aapt package -v -f \
	-M $ANDROIDMANIFEST \
	-S ${SRC_MAIN_DIR}/res \
	-F ${BIN_DIR}/${APK_UNSIGNED}
sleep 1
echo


if [[ ! -d $KEYSTORE_DIR ]] ; then
  echo "make dir ${KEYSTORE_DIR}"
  mkdir -p $KEYSTORE_DIR
fi

if [ ! -f "$X509_PEM" ] ; then
  openssl genrsa -out $KEY_PEM 2048

  openssl req -new -key $KEY_PEM -out $REQ_PEM

  openssl x509 -req -days 10000 -in $REQ_PEM \
    -signkey $KEY_PEM -out $X509_PEM

  openssl pkcs8 -topk8 -outform DER -in $KEY_PEM \
    -inform PEM -out $KEY_PK8 -nocrypt

fi


## Change to ./bin directory
echo "Go to ${BIN_DIR} directory"
echo
sleep 1


cd ${BIN_DIR}


## Adding classes.dex to application file
echo "aapt begun: adding classes.dex to ${APK_UNSIGNED}"
echo
sleep 1
aapt add -f ${APK_UNSIGNED} classes.dex
echo



echo "[zip aligned]"
zipalign 4 ${APK_UNSIGNED} ${APK_ALIGNED}





## Sign application file
echo "apksigner begun: creating ${APK}"
echo
sleep 1

apksigner sign \
  -in $APK_ALIGNED \
  -out $APK \
  -key ../${KEY_PK8} \
  -cert ../${X509_PEM}

sleep 1

## Change permission of signed appliaction file
echo "Change permission ${APK} to 644"
echo
sleep 1
chmod 644 ${APK}

## Removing unsigned aplication file
echo "Removing ${APK_UNSIGNED}"
echo
sleep 1
rm -fr ${APK_UNSIGNED}

## Copy application file
echo "Copy ${APK} to ${BUILD_APK_PATH}/${APK}"
echo
sleep 1
cp ${APK} ${BUILD_APK_PATH}/${APK}

## Go back to prev directory
echo "back to prev directory"
echo
sleep 1
cd ..

echo "You can install \"${APP_NAME}\" apps by clicking file located on ${BUILD_APK_PATH}/${APK} with your favorite file manager."
echo





