package it.giacomos.android.wwwsapp.layers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.layers.installService.InstallTaskState;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;



public class LayerListAdapter extends ArrayAdapter<LayerItemData> implements OnClickListener 
{
	private final Context context;
	private LayerActionListener mLayerActionListener;
	public static final int ACTION_DOWNLOAD = 0;
	public static final int ACTION_CANCEL_DOWNLOAD = 1;
	public static final int ACTION_REMOVE = 2;

	private final int LIST_ADAPTER_POS = 0;

	static class ViewHolder {
		public TextView title, desc;
		public Button buttonInstallRemove, buttonUpgrade;
		public ImageView image;
		public ProgressBar progressBar;
		public TextView installedVerTextView, availableVerTextView;
	}

	public LayerListAdapter(Context context, LayerActionListener lal) 
	{
		super(context, R.layout.layer_list_item, new ArrayList<LayerItemData>());
		this.context = context;
		mLayerActionListener = lal;
	}

	LayerItemData findItemData(String name)
	{
		for(int i = 0; i < getCount(); i++)
		{
			LayerItemData d = getItem(i);
			if(name.compareTo(d.name) == 0)
				return d;
		}
		return null;
	}
	
	public void update(LayerItemData d)
	{
		LayerItemData otherD = findItemData(d.name);
		if(otherD != null)
		{
			Log.e("LayerListAdapter.update", " layer " + d.name + " found. UPDATING");
			boolean dataChanged = otherD.selectiveCopyFrom(d);
			if(dataChanged)
				notifyDataSetChanged();
		}
		else
		{
			Log.e("LayerListAdapter.update", " layer " + d.name + " not found. ADDING");
			add(d);
		}
	}

	public void updateProgress(String layerName, int installProgress, InstallTaskState instState)
	{
		LayerItemData d = findItemData(layerName);
		if(d != null && d.updateProgress(installProgress, instState))
			notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View itemView, ViewGroup parent) 
	{
		ViewHolder  mViewHolder = null; 

		if(itemView == null)
		{
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			itemView = inflater.inflate(R.layout.layer_list_item, parent, false);
			mViewHolder = new ViewHolder();
			mViewHolder.title  = (TextView) itemView.findViewById(R.id.title);
			mViewHolder.desc = (TextView) itemView.findViewById(R.id.description);
			mViewHolder.image = (ImageView) itemView.findViewById(R.id.icon);
			mViewHolder.buttonInstallRemove = (Button) itemView.findViewById(R.id.button);
			mViewHolder.buttonUpgrade = (Button) itemView.findViewById(R.id.buttonUpgrade);
			mViewHolder.progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
			mViewHolder.installedVerTextView = (TextView) itemView.findViewById(R.id.tvInstalledVersion);
			mViewHolder.availableVerTextView = (TextView) itemView.findViewById(R.id.tvAvailableVersion);
			itemView.setTag(mViewHolder);
		}
		else
		{
			mViewHolder  = (ViewHolder) itemView.getTag();
		}

		/* updates, if present */
		LayerItemData d = this.getItem(position);
		mViewHolder.title.setText(d.title);
		mViewHolder.desc.setText(d.short_desc);
		mViewHolder.image.setBackgroundDrawable(d.icon);
		
		if(d.install_progress < 100)
		{
			String installingVersion = context.getString(R.string.installing_version) + " ";
			String downloadingVersion = context.getString(R.string.downloading_version) + " ";
			mViewHolder.availableVerTextView.setVisibility(View.GONE);
			mViewHolder.installedVerTextView.setVisibility(View.VISIBLE);
			if(d.installState == InstallTaskState.DOWNLOADING)
				mViewHolder.installedVerTextView.setText(downloadingVersion + 
						String.valueOf(d.available_version) + "[" + d.install_progress + "%]");
			else if(d.installState == InstallTaskState.INSTALLING)
				mViewHolder.installedVerTextView.setText(installingVersion + 
						String.valueOf(d.available_version) + "[" + d.install_progress + "%]");
			mViewHolder.progressBar.setVisibility(View.VISIBLE);
			mViewHolder.progressBar.setProgress(d.install_progress);
			mViewHolder.buttonInstallRemove.setText(R.string.cancel_button);
		}
		else
		{
			mViewHolder.progressBar.setVisibility(View.GONE);
			mViewHolder.availableVerTextView.setVisibility(View.VISIBLE);

			String updateTo = context.getString(R.string.update_to) + " ";
			String instVersion = context.getString(R.string.installed_version) + " ";
			String installVersion = context.getString(R.string.install_version) + " ";

			mViewHolder.installedVerTextView.setText(instVersion + String.valueOf(d.installed_version));

			if(d.available_version == d.installed_version && d.installed)
				mViewHolder.availableVerTextView.setText(R.string.uptodate);
			else if(d.installed_version < d.available_version && d.installed)
				mViewHolder.availableVerTextView.setText(updateTo + String.valueOf(d.available_version));
			else if(!d.installed)
				mViewHolder.availableVerTextView.setText(installVersion + String.valueOf(d.available_version));

			if(d.installed)
			{
				mViewHolder.buttonInstallRemove.setText(R.string.delete);
				mViewHolder.installedVerTextView.setVisibility(View.VISIBLE);
			}
			else if(!d.installed)
			{
				mViewHolder.buttonInstallRemove.setText(R.string.install);
				mViewHolder.installedVerTextView.setVisibility(View.GONE);
			}
			/* upgrade ? */
			if(d.available_version > d.installed_version && d.installed)
				mViewHolder.buttonUpgrade.setVisibility(View.VISIBLE);
			else
				mViewHolder.buttonUpgrade.setVisibility(View.GONE);
		}

		mViewHolder.buttonInstallRemove.setOnClickListener(this);
		mViewHolder.buttonUpgrade.setOnClickListener(this);
		mViewHolder.buttonInstallRemove.setTag(position);
		mViewHolder.buttonUpgrade.setTag(position);

		return itemView;
	}

	public ArrayList<String> dumpProgressToString()
	{
		ArrayList<String> repr = new ArrayList<String>();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		String encoded;
		try {
			for(int i = 0;  i < this.getCount(); i++)
			{
				LayerItemData l = this.getItem(i);
				new ObjectOutputStream(out).writeObject(l.progressInformationToStringArray());
				encoded = Base64.encodeToString(out.toByteArray(), Base64.DEFAULT);
				repr.add(encoded);
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return repr;
	}
	
	public void restoreProgressFromString(ArrayList<String> progressState)
	{
		
		for(int i = 0; i < progressState.size(); i++)
		{
			byte[] encodedData = Base64.decode(progressState.get(i).getBytes(), Base64.DEFAULT);
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new ByteArrayInputStream(encodedData));
				try {
					String[] fields;
					fields = (String []) ois.readObject();
					if(fields.length == 5)
					{
						LayerItemData d = findItemData(fields[0]);
						if(d != null)
						{
							Log.e("LayerListAdapter.restoreProgressFromString", "restoring from " + fields);
							d.restoreProgressInformation(fields);
						}
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ois.close();
			} catch (StreamCorruptedException e) {
				Log.e("LayerListAdapter.restoreProgressFromString", e.getLocalizedMessage());
			} catch (IOException e) {
				Log.e("LayerListAdapter.restoreProgressFromString", e.getLocalizedMessage());
			}
			
		}
	}
	
	@Override
	public void onClick(View v) 
	{
		/* same action for upgrade or install) */
		if(v.getId() == R.id.button || v.getId() == R.id.buttonUpgrade)
		{
			Log.e("onClick", " clicked on id " + v.getId());
			Button b = (Button) v;
			LayerItemData d = this.getItem(Integer.parseInt(b.getTag().toString()));
			if(!d.isValid())
				return;
			if( (b.getText().toString().compareTo(context.getString(R.string.install)) == 0) 
					|| v.getId() == R.id.buttonUpgrade)
			{
				mLayerActionListener.onActionRequested(d.name, ACTION_DOWNLOAD);
			}
			else if(b.getText().toString().compareTo(context.getString(R.string.cancel_button)) == 0)
			{
				mLayerActionListener.onActionRequested(d.name, ACTION_CANCEL_DOWNLOAD);
			}
			else if(b.getText().toString().compareTo(context.getString(R.string.delete)) == 0)
			{
				mLayerActionListener.onActionRequested(d.name, ACTION_REMOVE);
			}
		}
	}
}
