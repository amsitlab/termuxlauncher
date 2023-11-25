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
```
Go to the repo's root directory:
```bash
cd termuxlauncher
```
Read the [Build](#Build) section.


### Build
---------
- Make sure you have cloned this repo to your local storage.
- Make sure you have located on root directory of this repo.
- Make sure *build.sh* on this root repository has executable permission.
run this script on root directory of this repository:
```bash
chmod +x build.sh 
```

and run this script to build:
```bash
./build.sh

```




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

### Feature
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
- [ecj](http://www.eclipse.org/jdt/core/)  (termux package)
- [dx](http://developer.android.com/tools/help/index.html) (termux package)
- [aapt](http://elinux.org/Android_aapt) (termux package)
- [apksigner](https://github.com/fornwall/apksigner) (termux package)

### Note
--------
When you fork/recode this repo, you do not become a creator of this app.
You can fork/recode this repo, but please do not remove the author's name.

