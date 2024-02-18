package amsitlab.android.apps.termuxlauncher;


import android.os.Bundle;
import android.os.Environment;

import android.app.Activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import android.util.Log;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;


public class MainActivity extends Activity
{
	public static final String LOG_TAG = "termux-applist";

	public static final String EXTERNAL_PATH_NAME = "termuxlauncher";

	public static final String EXTERNAL_LAUNCH_FILE_NAME = ".apps-launcher";
	public static final String EXTERNAL_ALIAS_FILE_NAME = "aliasses.txt";

	private static final String LAUNCH_SCRIPT_COMMENT = ""
		+ "## This script created by Termux Launcher.\n"
		+ "## Do not change anything !!!\n"
		+ "## Your changes will be override!!!\n\n"
		+ "## Recomended:\n"
		+ "##\tsource this file to your\n"
		+ "##\t~/.bashrc or ~/.bash_profile to launch app with\n"
		+ "##\tcommand on termux by calling\n"
		+ "##\t'launch [appname]'\n\n"
		+ "## Author : Amsit (@amsitlab) <dezavue3@gmail.com>\n\n\n\n";

	private static final String LAUNCH_SCRIPT_START = ""
		+ "launch(){\n"
		+ "\tcmd=\"$(echo \"$1\" | tr 'A-Z' 'a-z' )\"\n"
		+ "\tname=''\n"
		+ "\tpkg=''\n"
		+ "\tcase \"$cmd\" in\n"
		+ "\t\t--help|-h)\n"
		+ "\t\tprintf \"Usage:\\n\"\n"
		+ "\t\tprintf \"\\tlaunch [appname]\\n\"\n"
		+ "\t\tprintf \"\\t\\tLaunching application\\n\"\n"
		+ "\t\tprintf \"\\t\\tType 'launch --list' to view list of applications name.\\n\"\n"
		+ "\t\tprintf \"\\t\\tExample: launch whatsapp\\n\\n\"\n"
		+ "\t\tprintf \"\\tlaunch [--list|-l]\\n\"\n"
		+ "\t\tprintf \"\\t\\tDisplaying available apps\\n\\n\"\n"
		+ "\t\tprintf \"\\tlaunch [--help|-h]\\n\"\n"
		+ "\t\tprintf \"\\t\\tDisplaying this message and exit .\\n\\n\"\n"
		+ "\t\t;;\n";

	private static final String LAUNCH_SCRIPT_END = ""
		+ "\t\t--list|-l)\n"
		+ "\t\tprintf \"{{applications_list}}\"\n"
		+ "\n"
		+ "\t\t;;\n"
		+ "\t\t*)\n"
		+ "\t\tprintf \"Unknown command: type 'launch --help' for detail.\\n\"\n"
		+ "\t\treturn 1\n"
		+ "\t\t;;\n"
		+ "\tesac\n"
		+ "\tif [ \"$name\" ]; then\n"
		+ "\t\tam start -n \"$pkg\" --user 0 >/dev/null 2>&1\n"
		+ "\t\techo Launching \\'\"$name\"\\'\n"
		+ "\tfi\n"
		+ "}";

	/** termux intent **/
	private static Intent myIntent;

	/** Public external storage path name **/
	private static File sExternalPath = Environment.getExternalStoragePublicDirectory(EXTERNAL_PATH_NAME);

	/** Launch file **/
	private static File sLaunchFile;
	private static File sFinalFile;

	/** user's alias file **/
	private static File sAliasFile;
	private static List<String> aliassedAppNames;
	private static List<String> aliasses;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		onAll();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		onAll();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		onAll();
	}

	private void onAll()
	{
		try {
			readAliasFile();
		} catch(Exception e) {
			Log.e(LOG_TAG,"Could not Read from " + sAliasFile.toString());
		}
		new Thread(){
			public void run(){
				createLaunchFile();
			}
		}.start();
		createTermuxIntent();
		startActivity(myIntent);

	}

	private void createTermuxIntent()
	{
		if(null == myIntent)
			myIntent = getPackageManager().getLaunchIntentForPackage("com.termux");
	}

	private static void createExternalPath()
	{
		if(!sExternalPath.exists()){
			sExternalPath.mkdirs();
		}
	}

	private static void readAliasFile()
	{
		sAliasFile = new File(sExternalPath,EXTERNAL_ALIAS_FILE_NAME);
		aliassedAppNames = new ArrayList<String>();
		aliasses = new ArrayList<String>();
		if(!sAliasFile.exists())
			return;
		try{
			Scanner reader;
			reader = new Scanner(sAliasFile);
			while(reader.hasNextLine()) {
				String line = reader.nextLine();
				String trimmed = line.trim();
				if(trimmed.charAt(0)=='#')
					continue;
				String[] data = trimmed.split("=",2);
				aliasses.add(data[0]);
				aliassedAppNames.add(data[1]);
			}
			reader.close();
		} catch (IOException e) {
			Log.e(LOG_TAG,"couldnt read from" + EXTERNAL_ALIAS_FILE_NAME);
		}
		return;

	}

	private String getAliassedName(String appName,String pkgName)
	{
		for(int i=0; i<aliassedAppNames.size();i++){
			if(!appName.equals(aliassedAppNames.get(i).toLowerCase()))
				continue;
			return aliasses.get(i);
		}
		return appName;

	}

	private void createLaunchFile()
	{
		try{
			sLaunchFile = new File(sExternalPath,".tmp");
			sFinalFile = new File(sExternalPath,EXTERNAL_LAUNCH_FILE_NAME);


			// Always up to date
			if(sLaunchFile.exists())
				sLaunchFile.delete();

			createExternalPath();
			final FileOutputStream fos = new FileOutputStream(sLaunchFile);
			final PrintStream printer = new PrintStream(fos);
			final PackageManager pm = getApplicationContext().getPackageManager();
			List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
			printer.print(LAUNCH_SCRIPT_COMMENT);
			printer.print(LAUNCH_SCRIPT_START);

			final StringBuilder appNameList = new StringBuilder();
			for(ApplicationInfo pkg : packages){
				String pkgName = pkg.packageName;
				String originalAppName = pkg.loadLabel(pm).toString();
				String appName = originalAppName.toLowerCase();
				appName = appName.split(":")[0];
				Intent intent = pm.getLaunchIntentForPackage(pkgName);
				boolean isSystemApp = ((pkg.flags & ApplicationInfo.FLAG_SYSTEM) == 1) ? true : false;
				Log.d(LOG_TAG,"[" + intent + "] : [" + pkgName + "] : [" + isSystemApp + "] : [" + "] : [" + appName + "]");
				if(intent == null)
					continue;
				String componentName = intent.getComponent().flattenToShortString();
				String toRemove = "'\"()&{}$!<>#+*";
				for(int i=0;i<toRemove.length();i++)
					appName = appName.replace(""+toRemove.charAt(i),"");
				toRemove = " \n\t";
				for(int i=0;i<toRemove.length();i++)
					appName = appName.replace(""+toRemove.charAt(i),"-");
				String aliassedName = getAliassedName(appName,pkgName);
				if(aliassedName.equals(""))
					continue;
				appNameList.append( aliassedName );
				appNameList.append("\\n");
				printer.print(appName
						+ ")\n"
						+ "\t\tname=\"" + originalAppName.replace("\"","\\\"") + "\"\n"
						+ "\t\tpkg=\"" + componentName + "\"\n"
						+ "\t\t;;\n"
					     );

			}
			printer.print(LAUNCH_SCRIPT_END.replace("{{applications_list}}",appNameList.toString()));
			printer.flush();
			printer.close();
			fos.flush();
			fos.close();
			sLaunchFile.renameTo(sFinalFile);
		} catch(IOException ioe) {
			Log.e(LOG_TAG,"Could not write to " + sLaunchFile.toString());
		}

	}
}
