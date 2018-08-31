# Termux Launcher (unofficial)

### Description
---------------
Make [Termux](https://github.com/termux/termux-app) as launcher without hack anything of the original application.
This application just make [Termux](https://github.com/termux/termux-app) as launcher, not modify/hack/replace [Termux](https://github.com/termux/termux-app) application.
[Termux](https://github.com/termux/termux-app) application must be installed on your device before use this application.



### Installation
> If you do not build from source,
> I have installable apk for you at [here](http://github.com/amsitlab/termuxlauncher/releases) .
>

Clone this repository to your local storage by typing:
```bash
git clone https://github.com/amsitlab/termuxlauncher.git
```
Goto root directory of this repo:
```bash
cd termuxlauncher
```
Read [Build](#Build) section.


### Build
---------
- Make sure you has clone this repo to your local storage.
- Make sure you located on root directory of this repo.
- Make sure *build.sh* on this root repository have exacutable permission.
run this script on root directory of this repository:
```bash
chmod +x build.sh 
```

and run this script to build:
```bash
./build.sh

```




### Recomended
--------------
To launch application with command:
```bash
launch [appname]
```
write it to your *~/.bashrc* or *~/.bash_profile* :
```bash
source "${EXTERNAL_STORAGE}/termuxlauncher/.apps-launcher"
```
after build and install this app.

### Feature
-----------
- Not hack original application.
- Launch application with command on termux (with little bit configure).
- Not root required.



### Success
-----------
build on Lenovo A1000




### Author
----------
- Amsit (@amsitlab) <dezavue3@gmail.com>




### Build with
--------------
- [Termux](https://github.com/termux/termux-app)
- [ecj](http://www.eclipse.org/jdt/core/)  (termux package)
- [dx](http://developer.android.com/tools/help/index.html) (termux package)
- [aapt](http://elinux.org/Android_aapt) (termux package)
- [apksigner](https://github.com/fornwall/apksigner) (termux package)

### Note
--------
When you fork/recode this repo , that is not make this app was created by yourself.
You can fork/recode this repo. but please do not remove author name.

