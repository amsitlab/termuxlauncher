#!/data/data/com.termux/files/usr/bin/bash

## This acript to build "Termux Launcher"
## "Termux Launcher" is android application to
## 	make  "Termux" as launcher without hack anything
##	of  "Termux" application.
## This apps will creating shell script located on
##	/sdcard/termuxlauncher/.apps
## You must source shell script on 
##	/sdcard/termuxlauncher/.apps 
##	for launch apps with command on termux
## Recomended: if you want use "launch" command to
##	launch app with cmmand on termux anytime.
##	write to your ~/.bashrc or your ~/bash_profile
##	like this:
##		source /storage/shared/termuxlauncher/.apps-launcher
##

## Author: Amsit (@amsitlab) <dezavue3@gmail.com>
## Thank to:
##	forwall (author of the termux)
##	sdrausty (my inspiration to build apk on termux)##	BlackHoleSecurity (my comunity to learning program)

## Application name
APP_NAME="termuxlauncher"

## Required packages
REQ_PKG=("ecj" "dx" "aapt" "apksigner")

## Requred package length
REQ_LEN=${#REQ_PKG[@]}

## Apk output diractory"
BUILD_APK_PATH=${EXTERNAL_STORAGE}/Download/buildAPKs

## Java file list
JAVA_FILE_LIST=`find src/main/java -type f -name \*.java`
 
## Check required package and install if not installed
for((i=0; i<$REQ_LEN; i++));
do
	exists=`which ${REQ_PKG[$i]}`
	if [ -z "$exists" ]; then
		apt install -y ${REQ_PKG[$i]}
	fi
done

## Make sure shared storage has set up
#test ! -d ~/storage &&
#	termux-setup-storage

##
test ! -d $BUILD_APK_PATH &&
	mkdir -p $BUILD_APK_PATH

## Creating R.java
echo 
echo "aapt bugun: creating R.java"
echo
sleep 1
aapt package -v -f \
	-M AndroidManifest.xml \
	-S res \
	-J src/main/java \
	-I ${PREFIX}/share/java/android.jar \
	-m
sleep 1
echo

## Compile to classes
echo "ecj begun: compiling"
echo
sleep 1
ecj -verbose \
	-d ./obj \
	-classpath ${PREFIX}/share/java/android.jar \
	-sourcepath src/main/java \
	$JAVA_FILE_LIST
sleep 1
echo

## Dexing all .class on ./obj path
echo "dx begun: creating bin/classes.dex"
echo
test ! -d ./bin &&
	mkdir bin
sleep 1
dx --dex --verbose --output ./bin/classes.dex ./obj
sleep 1
echo

## Creating unsigned appliaction file
echo "aapt bagun: creating ./bin/${APP_NAME}-unsigned.apk"
echo
sleep 1
aapt package -v -f \
	-M AndroidManifest.xml \
	-S ./res \
	-F ./bin/${APP_NAME}-unsigned.apk
sleep 1
echo

## Chang to ./bin directory
echo "change directory to ./bin"
echo
sleep 1
cd ./bin


## Adding classes.dex to application file
echo "aapt begun: adding classes.dex to ${APP_NAME}-unsigned.apk"
echo
sleep 1
aapt add -f ${APP_NAME}-unsigned.apk classes.dex
echo


## Sign application file
echo "apksigner begun: creating ${APP_NAME}.apk"
echo
sleep 1

apksigner ../${APP_NAME}-debug.key \
	${APP_NAME}-unsigned.apk \
	${APP_NAME}.apk
sleep 1

## Change permission of signed appliaction file
echo "Change permission ${APP_NAME}.apk to 644"
echo
sleep 1
chmod 644 ${APP_NAME}.apk

## Removing unsigned aplication file
echo "Removing ${APP_NAME}-unsigned.apk"
echo
sleep 1
rm -fr ${APP_NAME}-unsigned.apk

## Copy application file
echo "Copy ${APP_NAME}.apk to ${BUILD_APK_PATH}/${APP_NAME}.apk"
echo
sleep 1
cp ${APP_NAME}.apk ${BUILD_APK_PATH}/${APP_NAME}.apk

## Go back to prev directory
echo "back to prev directory"
echo
sleep 1
cd ..

echo "You can install \"${APP_NAME}\" apps by clicking file located on ${BUILD_APK_PATH}/${APP_NAME}.apk with your favorite file manager"
echo





