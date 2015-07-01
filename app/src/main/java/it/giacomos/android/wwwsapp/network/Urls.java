package it.giacomos.android.wwwsapp.network;

import java.lang.String;

public class Urls {
	public Urls() { }
	
	public String layersListUrl()
	{
		return "http://www.giacomos.it/wwwsapp/get_layers_list.php";
	}
	
	public String layerDescUrl()
	{
		return "http://www.giacomos.it/wwwsapp/get_layer_desc.php";
	}

	public String layerBitmapUrl()
	{
		return "http://www.giacomos.it/wwwsapp/get_layer_bitmap.php";
	}

	public String reportServiceUrl()
	{
		return  "http://www.giacomos.it/wwwsapp/report.php";
	}

	public String getAppStoreUrl() {
		return "";
	}

	public String postReportUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRemovePostUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPostReportRequestUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUpdateMyLocationUrl() {
		return "http://www.giacomos.it/wwwsapp/update_my_location.php";
	}

	public String layerDownloadUrl() {
		return "http://www.giacomos.it/wwwsapp/fetch_layer.php";
	}


	public String getRegisterUserUrl()
	{
		return "http://www.giacomos.it/wwwsapp/register_user.php";
	}

	public String getRegisterGCMTokenUrl()
	{
		return "http://www.giacomos.it/wwwsapp/register_gcm.php";
	}
}
