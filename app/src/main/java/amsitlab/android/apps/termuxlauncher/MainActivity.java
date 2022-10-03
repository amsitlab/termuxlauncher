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

import java.util.List;


public class MainActivity extends Activity
{
	public static final String LOG_TAG = "termux-applist";

	public static final String EXTERNAL_PATH_NAME = "termuxlauncher";

	public static final String EXTERNAL_ALIAS_FILE_NAME = ".apps-launcher";

	/** termux intent */
	private static Intent myIntent;

	/** Public external storage path name */
	private static File sExternalPath = Environment.getExternalStoragePublicDirectory(EXTERNAL_PATH_NAME);

	/** Alias file **/
	private static File sAliasFile;


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
		+ "\tcase \"$1\" in\n"
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
		+ "{{applications_list}}"
		+ "\n"
		+ "\t\t;;\n"
		+ "\t\t*)\n"
		+ "\t\tprintf \"Unknown command: type 'launch --help' for detail .\\n\"\n"
		+ "\t\treturn 1\n"
		+ "\t\t;;\n"
		+ "\tesac\n"
		+ "}";








	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		createAliasFile();
		createTermuxIntent();
		startActivity(myIntent);
	}


	@Override
	public void onResume()
	{
		super.onResume();
		createTermuxIntent();
		startActivity(myIntent);
	}


	@Override
	public void onPause()
	{
		super.onPause();
		createTermuxIntent();
		startActivity(myIntent);
	}



	private void createTermuxIntent()
	{
		if(null == myIntent)
		{
			myIntent = getPackageManager().getLaunchIntentForPackage("com.termux");

		}
	}


	private static void createExternalPath()
	{
		if(!sExternalPath.exists())
		{
			sExternalPath.mkdirs();
		}
	}



	private void createAliasFile()
	{
		sAliasFile = new File(sExternalPath,EXTERNAL_ALIAS_FILE_NAME);


		new Thread()
		{
		    public void run()
		    {

			try
			{
			    // Always up to date
			    if(sAliasFile.exists())
		  	    {
				sAliasFile.delete();
			    }

			    createExternalPath();
			    final FileOutputStream fos = new FileOutputStream(sAliasFile);
			    final PrintStream printer = new PrintStream(fos);
			    final PackageManager pm = getApplicationContext().getPackageManager();
			    List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
			    printer.print(LAUNCH_SCRIPT_COMMENT);
			    printer.print(LAUNCH_SCRIPT_START);

			    final StringBuilder appNameList = new StringBuilder();
			    for(ApplicationInfo pkg : packages)
			    {
				String pkgName = pkg.packageName;
				String originalAppName = pkg.loadLabel(pm).toString();
				String appName = originalAppName;
				Intent intent = pm.getLaunchIntentForPackage(pkgName);
				boolean isSystemApp = ((pkg.flags & ApplicationInfo.FLAG_SYSTEM) == 1) ? true : false;
				Log.d(LOG_TAG,"[" + intent + "] : [" + pkgName + "] : [" + isSystemApp + "] : [" + "] : [" + appName + "]");
				if(intent == null)
				{
					continue;
				}
				String componentName = intent.getComponent().flattenToShortString();
				appName = appName.replace("'","");
				appName = appName.replace("\"","");
				appName = appName.replace("(","");
				appName = appName.replace(")","");
				appName = appName.replace("&","");
				appName = appName.replace("{","");
				appName = appName.replace("}","");
				appName = appName.replace("$","");
				appName = appName.replace("!","");
				appName = appName.replace("<","");
				appName = appName.replace(">","");
				appName = appName.replace("#","");
				appName = appName.replace("+","");
				appName = appName.replace("*","");
				appName = appName.replace(" ","-");
				appName = appName.replace("\t","-");
				appName = appName.replace("\n","-");
				appNameList.append("\t\tprintf \"");
				appNameList.append( appName );
				appNameList.append("\\n");
				appNameList.append(appName.toLowerCase());

				appNameList.append("\\n\"\n");

				printer.print(""
					+ "\t\t"
					+ appName
					+ "|"
					+ appName.toLowerCase()
					+ ")\n"
					+ "\t\tam start -n '"
					+ componentName
					+ "' --user 0 &> /dev/null\n"
					+ "\t\tprintf \"Launch '"
					+ originalAppName.replace("\"","\\\"")
					+ "'\\n\"\n"
					+ "		;;\n"
				);



			    }


			    printer.print(
				LAUNCH_SCRIPT_END.replace("{{applications_list}}",appNameList.toString()));


			    printer.flush();
			    printer.close();
			    fos.flush();
			    fos.close();
			} catch(IOException ioe) {
				Log.e(LOG_TAG,"Could not write to " + sAliasFile.toString());
			}

		    }

		}.start();
	}


}
