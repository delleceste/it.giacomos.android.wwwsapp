package it.giacomos.android.wwwsapp.layers;


import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import it.giacomos.android.wwwsapp.layers.installService.InstallTaskState;

public class Loader 
{

	public ArrayList<LayerItemData> getCachedList(Context ctx)
	{
		ArrayList<LayerItemData> ret = new ArrayList<LayerItemData>();
		FileUtils cache = new FileUtils();
		String list = cache.loadFromStorage(LayerListActivity.CACHE_LIST_DIR + "layerlist.xml", ctx);
		if(!list.isEmpty())
		{
			XmlParser parser = new XmlParser();
			ArrayList<LayerItemData> alist = parser.parseLayerList(list);
			for(int i = 0; i < alist.size(); i++)
			{
				LayerItemData d = alist.get(i);
				String name = d.name;
				String s = cache.loadFromStorage(LayerListActivity.CACHE_LIST_DIR + name + ".xml", ctx);
				LayerItemData item = parser.parseLayerDescription(s);
				/* there is no version in the descriptive xml file. But we have the version in the d variable,
				 * which has been taken from layerlist.xml. The version information in layerlist.xml was directly
				 * obtained by the database.
				 */
				item.available_version = d.available_version;
				Bitmap bmp = cache.loadBitmapFromStorage(LayerListActivity.CACHE_LIST_DIR + name + ".bmp", ctx);
				if(bmp != null)
					item.icon = new BitmapDrawable(ctx.getResources(), bmp);
				ret.add(item);
			}
		}
		return ret;
	}

    public LayerItemData findCachedLayer(String layerName, Context ctx)
    {
        ArrayList<LayerItemData> layers = getCachedList(ctx);
        for(LayerItemData lid : layers)
            if(lid.name.compareTo(layerName) == 0)
                return lid;
        return null;
    }

    public LayerItemData findInstalledLayer(String layerName, Context ctx)
    {
        ArrayList<LayerItemData> installedLayers = getInstalledLayers(ctx);
        for(LayerItemData lid: installedLayers)
            if(lid.name.compareTo(layerName) == 0)
                return lid;
        return null;
    }

	public ArrayList<LayerItemData> getInstalledLayers(Context ctx)
	{
		ArrayList<LayerItemData> ret = new ArrayList<LayerItemData>();
		File filesDir = ctx.getFilesDir();
		String layersDirNam = filesDir.getAbsolutePath() + "/layers/";
		File layersDir = new File(layersDirNam);
		FileUtils cutils  = new FileUtils();
		if(!layersDir.exists())
			layersDir.mkdirs(); /* nothing else to do: no layers installed */
		else
		{
			final String layersDirName = LayerListActivity.LAYERS_DIR;
			FileUtils cache = new FileUtils();
			XmlParser parser = new XmlParser();
			ArrayList <String> layerNames = new ArrayList<String>();
			File[] files = layersDir.listFiles();
			for(int i = 0; i < files.length; i++)
			{
				File f = files[i].getAbsoluteFile();
				if(cutils.containsLayerInstallation(f) )
					layerNames.add(f.getName());
			}
			for(String fn : layerNames)
			{
				final String localizedLayerDescDirName = layersDirName + fn + "/localization/" +
						Locale.getDefault().getLanguage() + "/";
				String layerDescFilePath = localizedLayerDescDirName + fn + ".xml";
				String layerManifestFilePath = layersDirName + fn + "/" + fn + "_manifest.xml";
				String s = cache.loadFromStorage(layerDescFilePath, ctx);
				String mani = cache.loadFromStorage(layerManifestFilePath, ctx);
				LayerItemData item = parser.parseLayerDescription(s);
				item.installed_version = parser.getVersionFromManifest(mani);
				item.installed = true;
				item.installTaskState = InstallTaskState.INSTALL_COMPLETE;
				Bitmap bmp = cache.loadBitmapFromStorage(LayerListActivity.CACHE_LIST_DIR + fn + ".bmp", ctx);
				if(bmp != null)
					item.icon = new BitmapDrawable(ctx.getResources(), bmp);
				ret.add(item);
			}
		}
		return ret;
	}
}
