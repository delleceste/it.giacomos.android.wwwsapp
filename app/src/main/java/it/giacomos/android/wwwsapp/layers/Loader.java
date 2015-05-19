package it.giacomos.android.wwwsapp.layers;


import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

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
				String title = d.name;
				String s = cache.loadFromStorage(LayerListActivity.CACHE_LIST_DIR + title + ".xml", ctx);
				LayerItemData item = parser.parseLayerDescription(s);
				Log.e("Loader.load", "parsed " + item.name + ", " + item.short_desc + " ver " + item.available_version);
				Bitmap bmp = cache.loadBitmapFromStorage(LayerListActivity.CACHE_LIST_DIR + title + ".bmp", ctx);
				if(bmp != null)
					item.icon = new BitmapDrawable(ctx.getResources(), bmp);
				ret.add(item);
			}
		}
		return ret;
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
				File f = files[i];
				if(f.isDirectory())
					layerNames.add(f.getName());
			}
			for(String fn : layerNames)
			{
				Log.e("Loader.getInstalledLayers", "found layer " + fn);
				final String localizedLayerDescDirName = layersDirName + fn + "/localization/" +
						Locale.getDefault().getLanguage() + "/";
				String layerDescFilePath = localizedLayerDescDirName + fn + ".xml";
				String layerManifestFilePath = layersDirName + fn + "/" + fn + "_manifest.xml";
				String s = cache.loadFromStorage(layerDescFilePath, ctx);
				String mani = cache.loadFromStorage(layerManifestFilePath, ctx);
				LayerItemData item = parser.parseLayerDescription(s);
				item.installed_version = parser.getVersionFromManifest(mani);
				item.installed = true;
				Bitmap bmp = cache.loadBitmapFromStorage(LayerListActivity.CACHE_LIST_DIR + fn + ".bmp", ctx);
				if(bmp != null)
					item.icon = new BitmapDrawable(ctx.getResources(), bmp);
				ret.add(item);
			}
		}
		return ret;
	}

	public ArrayList<LayerItemData> mergeInstalledAndAvailableLayers(
			ArrayList<LayerItemData> installedLayers,
			ArrayList<LayerItemData> availableLayers) 
			{
		ArrayList<LayerItemData> merged = new ArrayList<LayerItemData>();
		for(LayerItemData installedLayer : availableLayers)
		{

		}


		return merged;
			}
}
