package org.sigrok.androidutils;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class SigrokApplication extends Application {

	private static final String JNI_LIBS_RESOURCE_ID_META =
	    "org.sigrok.androidutils.jni_libs_resource_id";
	private static final String DEVICE_FILTER_RESOURCE_ID_META =
	    "org.sigrok.androidutils.device_filter_resource_id";

	public SigrokApplication()
	{
	}

	public static void initSigrok(Context context)
	{
	    ApplicationInfo appInfo = context.getApplicationInfo();
	    Environment.initEnvironment(appInfo.sourceDir);
	    UsbHelper.setContext(context);
	    try {
		appInfo = context.getPackageManager().
		    getApplicationInfo(context.getPackageName(),
				       PackageManager.GET_META_DATA);
	    } catch(PackageManager.NameNotFoundException exc) {
	    }
	    if (appInfo.metaData != null &&
		appInfo.metaData.containsKey(JNI_LIBS_RESOURCE_ID_META)) {
		int resId = appInfo.metaData.getInt(JNI_LIBS_RESOURCE_ID_META);
		String[] libs = context.getResources().getStringArray(resId);
		int numLibs = libs.length;
		for (int i = 0; i < numLibs; i++) {
		    String libName = libs[i];
		    System.loadLibrary(libName);
		}
	    }
	}

	public static UsbSupplicant createUsbSupplicant(Context context)
	{
	    ApplicationInfo appInfo = context.getApplicationInfo();
	    try {
		appInfo = context.getPackageManager().
		    getApplicationInfo(context.getPackageName(),
				       PackageManager.GET_META_DATA);
	    } catch(PackageManager.NameNotFoundException exc) {
	    }
	    if (appInfo.metaData != null &&
		appInfo.metaData.containsKey(DEVICE_FILTER_RESOURCE_ID_META)) {
		int resId = appInfo.metaData.getInt(DEVICE_FILTER_RESOURCE_ID_META);
		return new UsbSupplicant(context, resId);
	    }
	    Log.e("SigrokApplication", "Can't create UsbSupplicant (resource id missing)");
	    return null;
	}

	@Override
	public void onCreate()
	{
	    super.onCreate();
	    initSigrok(getApplicationContext());
	}
}
