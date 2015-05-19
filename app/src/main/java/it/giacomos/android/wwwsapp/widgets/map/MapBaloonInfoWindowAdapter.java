package it.giacomos.android.wwwsapp.widgets.map;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import it.giacomos.android.wwwsapp.R;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.util.Log;
import android.view.View;

public class MapBaloonInfoWindowAdapter implements GoogleMap.InfoWindowAdapter 
{

	public enum Type { OBSERVATIONS, WEBCAM }

	private View mView;
	
	public MapBaloonInfoWindowAdapter(Activity activity) 
	{
		mView = activity.getLayoutInflater().inflate(R.layout.mapbaloon, null);
		mType = Type.OBSERVATIONS;
	}
	
	@Override
	public View getInfoContents(Marker marker) 
	{
		setText(marker.getSnippet());
		setTitle(marker.getTitle());
		return mView;
	}

	@Override
	public View getInfoWindow(Marker arg0) 
	{
		return null;
	}
	
	public Type getType()
	{
		return mType;
	}
	
	public void setType(Type t)
	{
		mType = t;
	}

	public void setTitle(String t)
	{
		TextView tv = (TextView) mView.findViewById(R.id.baloon_title);
		if(tv != null)
		{
			tv.setText(t);
		}
	}
	
	public void setText(String t)
	{
		TextView tv = (TextView) mView.findViewById(R.id.baloon_text);
		if(tv != null)
		{
			tv.setMinLines(t.split("\n").length);
			tv.setMaxLines(t.split("\n").length);
			tv.setText(t);
		}
	}
	
	public void setIcon(Drawable dra)
	{
		ImageView iv = (ImageView) mView.findViewById(R.id.baloon_icon);
		iv.setScaleType(ScaleType.CENTER_CROP);
		iv.setBackgroundDrawable(dra);
	}
	
	private Type mType;


	
}
