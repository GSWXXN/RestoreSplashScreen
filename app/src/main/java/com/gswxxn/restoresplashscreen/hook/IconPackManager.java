package com.gswxxn.restoresplashscreen.hook;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.*;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class IconPackManager
{
    //@Inject
    //private android.app.Application mContext;

    private Context mContext;

    public void setContext (Context c) {
        mContext = c;
    }

    public class IconPack {
        public String packageName;
        public String name;

        private boolean mLoaded = false;
        private final HashMap<String, String> mPackagesDrawables = new HashMap<>();

        private final List<Bitmap> mBackImages = new ArrayList<>();
        private int totalIcons;

        Resources iconPackres = null;

        public void load() {
            // load appfilter.xml from the icon pack package
            PackageManager pm = mContext.getPackageManager();
            try {
                XmlPullParser xpp = null;

                iconPackres = pm.getResourcesForApplication(packageName);
                int appfilterid = iconPackres.getIdentifier("appfilter", "xml", packageName);
                if (appfilterid > 0) {
                    xpp = iconPackres.getXml(appfilterid);
                } else {
                    // no resource found, try to open it from assests folder
                    try {
                        InputStream appfilterstream = iconPackres.getAssets().open("appfilter.xml");

                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        factory.setNamespaceAware(true);
                        xpp = factory.newPullParser();
                        xpp.setInput(appfilterstream, "utf-8");
                    } catch (IOException e1) {
                        //Ln.d("No appfilter.xml file");
                    }
                }

                if (xpp != null) {
                    int eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) {
                            if (xpp.getName().equals("iconback")) {
                                for (int i = 0; i < xpp.getAttributeCount(); i++) {
                                    if (xpp.getAttributeName(i).startsWith("img")) {
                                        String drawableName = xpp.getAttributeValue(i);
                                        Bitmap iconback = loadBitmap(drawableName);
                                        if (iconback != null)
                                            mBackImages.add(iconback);
                                    }
                                }
                            } else if (xpp.getName().equals("iconmask")) {
                                if (xpp.getAttributeCount() > 0 && xpp.getAttributeName(0).equals("img1")) {
                                    String drawableName = xpp.getAttributeValue(0);
                                    Bitmap mMaskImage = loadBitmap(drawableName);
                                }
                            } else if (xpp.getName().equals("iconupon")) {
                                if (xpp.getAttributeCount() > 0 && xpp.getAttributeName(0).equals("img1")) {
                                    String drawableName = xpp.getAttributeValue(0);
                                    Bitmap mFrontImage = loadBitmap(drawableName);
                                }
                            } else if (xpp.getName().equals("scale")) {
                                // mFactor
                                if (xpp.getAttributeCount() > 0 && xpp.getAttributeName(0).equals("factor")) {
                                    float mFactor = Float.parseFloat(xpp.getAttributeValue(0));
                                }
                            } else if (xpp.getName().equals("item")) {
                                String componentName = null;
                                String drawableName = null;

                                for (int i = 0; i < xpp.getAttributeCount(); i++) {
                                    if (xpp.getAttributeName(i).equals("component")) {
                                        componentName = xpp.getAttributeValue(i);
                                    } else if (xpp.getAttributeName(i).equals("drawable")) {
                                        drawableName = xpp.getAttributeValue(i);
                                    }
                                }
                                if (!mPackagesDrawables.containsKey(componentName)) {
                                    mPackagesDrawables.put(componentName, drawableName);
                                    totalIcons = totalIcons + 1;
                                }
                            }
                        }
                        eventType = xpp.next();
                    }
                }
                mLoaded = true;
            } catch (PackageManager.NameNotFoundException e) {
                //Ln.d("Cannot load icon pack");
            } catch (XmlPullParserException e) {
                //Ln.d("Cannot parse icon pack appfilter.xml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private Bitmap loadBitmap(String drawableName) {
            int id = iconPackres.getIdentifier(drawableName, "drawable", packageName);
            if (id > 0) {
                Drawable bitmap = iconPackres.getDrawable(id);
                if (bitmap instanceof BitmapDrawable)
                    return ((BitmapDrawable) bitmap).getBitmap();
            }
            return null;
        }

        public Bitmap getIconForPackage(String appPackageName) {
            if (!mLoaded)
                load();

            PackageManager pm = mContext.getPackageManager();
            Intent launchIntent = pm.getLaunchIntentForPackage(appPackageName);
            String componentName = null;
            if (launchIntent != null)
                componentName = pm.getLaunchIntentForPackage(appPackageName).getComponent().toString();
            String drawable = mPackagesDrawables.get(componentName);
            if (drawable != null) {
                return loadBitmap(drawable);
            } else {
                // try to get a resource with the component filename
                if (componentName != null) {
                    int start = componentName.indexOf("{") + 1;
                    int end = componentName.indexOf("}", start);
                    if (end > start) {
                        drawable = componentName.substring(start, end).toLowerCase(Locale.getDefault()).replace(".", "_").replace("/", "_");
                        if (iconPackres.getIdentifier(drawable, "drawable", packageName) > 0)
                            return loadBitmap(drawable);
                    }
                }
            }
            return null;
        }
    }


    // ApplicationName PackageName
    public static HashMap<String, String> getAvailableIconPacks(Context context)
    {
        HashMap<String, String> iconPacks = new HashMap<>();
        iconPacks.put("None", "None");

        // find apps with intent-filter "com.gau.go.launcherex.theme" and return build the HashMap
        PackageManager pm = context.getPackageManager();

        List<ResolveInfo> adwLauncherThemes = pm.queryIntentActivities(new Intent("org.adw.launcher.THEMES"), PackageManager.GET_META_DATA);
        List<ResolveInfo> goLauncherThemes = pm.queryIntentActivities(new Intent("com.gau.go.launcherex.theme"), PackageManager.GET_META_DATA);

        // merge those lists
        List<ResolveInfo> rInfo = new ArrayList<ResolveInfo>(adwLauncherThemes);
        rInfo.addAll(goLauncherThemes);

        for(ResolveInfo ri  : rInfo)
        {
            String packageName = ri.activityInfo.packageName;
            try
            {
                ApplicationInfo ai = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                String appName  = context.getPackageManager().getApplicationLabel(ai).toString();
                iconPacks.put(appName, packageName);
            }
            catch (PackageManager.NameNotFoundException e)
            {
                // shouldn't happen
                e.printStackTrace();
            }
        }

        return iconPacks;
    }
}
