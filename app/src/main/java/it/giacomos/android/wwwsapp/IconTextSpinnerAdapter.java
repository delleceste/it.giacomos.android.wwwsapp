package it.giacomos.android.wwwsapp;

import it.giacomos.android.wwwsapp.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class IconTextSpinnerAdapter extends ArrayAdapter<String> {

	private Activity mActivity;

	private ArrayList<Bitmap> arr_images;

	public IconTextSpinnerAdapter(Context context, int resource,
			String[] strings, Activity activity) 
	{
		super(context, resource, strings);
		arr_images = new ArrayList<Bitmap>();
		mActivity = (Activity) activity;
	}

    public IconTextSpinnerAdapter(Context context, int resource , Activity activity)
	{
		super(context, resource);
		arr_images = new ArrayList<Bitmap>();
		mActivity = (Activity) activity;
	}

	public void add(String item, Bitmap icon)
	{
		this.add(item);
		arr_images.add(icon);
	}

	@Override
	public View getDropDownView(int position, View convertView,ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	public void setImages(ArrayList<Bitmap> imgs)
	{
		arr_images = imgs;
	}
	
	public View getCustomView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = mActivity.getLayoutInflater();
		View row=inflater.inflate(R.layout.post_icon_text_spinner, parent, false);
		TextView label=(TextView)row.findViewById(R.id.text);
		label.setText(getItem(position));

		if(arr_images != null && arr_images.size() > position)
		{
			ImageView icon = (ImageView) row.findViewById(R.id.icon);
			icon.setImageBitmap(arr_images.get(position));
		}

		return row;
	}

}
