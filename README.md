# Termux Launcher (unofficial)

### Description
---------------
Set [Termux](https://github.com/termux/termux-app) as a launcher without changing anything in the original application.
This application just sets [Termux](https://github.com/termux/termux-app) as a launcher and does not modify/hack/replace the [Termux](https://github.com/termux/termux-app) application itself.
[Termux](https://github.com/termux/termux-app) application must be installed on your device before using this application.

### Installation
> If you don't want to build from source,
> I have an installable apk for you at [here](http://github.com/amsitlab/termuxlauncher/releases) .
>

Clone this repository to your local storage by typing:
```bash
git clone https://github.com/amsitlab/termuxlauncher.git
cd termuxlauncher
make
```
If You've set up shizuku and rish, make will attempt to install the app for you. If you didnt, you can still install the apk from your Downloads directory.

### Usage
--------------
To launch the application with command:
```bash
launch [appname]
```
write it to your *~/.bashrc* or *~/.bash_profile* :
```bash
source "${EXTERNAL_STORAGE}/termuxlauncher/.apps-launcher"
```
after you have built and installed this app.

### Features
-----------
- The original application was not hacked.
- You can launch the application by running a command on termux (with a little preliminary configuration).
- Rooting is not required.



### Success
-----------
built on Lenovo A1000

built on Huawei P20




### Maintainers
----------
- Amsit - @amsitlab (Author) <dezavue3@gmail.com>
- Olie @therealolie <theRealOlie@proton.me>




### Build with
--------------
- [Termux](https://github.com/termux/termux-app)
- [ecj](http://www.eclipse.org/jdt/core/)
- [dx](http://developer.android.com/tools/help/index.html)
- [aapt](http://elinux.org/Android_aapt)
- [apksigner](https://github.com/fornwall/apksigner)
- [make](https://www.gnu.org/software/make/manual/make.html)

### Note
--------
When you fork/recode this repo, you don't become the creator of this app.
You can fork/recode this repo, but please don't remove the author's name.

