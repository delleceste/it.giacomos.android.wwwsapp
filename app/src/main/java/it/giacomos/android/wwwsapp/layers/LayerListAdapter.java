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
import android.widget.ImageButton;
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
		public ImageButton buttonInstall, buttonUpgrade, buttonDelete;
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

	public LayerItemData update(LayerItemData d, int copyMode)
	{
		LayerItemData otherD = findItemData(d.name);
		if(otherD != null)
		{
			Log.e("LayerListAdapter.update", " layer " + d.name + " found. UPDATING");
			boolean dataChanged = otherD.selectiveCopyFrom(d, copyMode);
			if(dataChanged)
				notifyDataSetChanged();
            return otherD;
		}
		else
		{
			Log.e("LayerListAdapter.update", " layer " + d.name + " not found. ADDING");
			add(d);
            return d;
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
			mViewHolder.buttonInstall = (ImageButton) itemView.findViewById(R.id.buttonInstall);
			mViewHolder.buttonUpgrade = (ImageButton) itemView.findViewById(R.id.buttonUpgrade);
            mViewHolder.buttonDelete = (ImageButton) itemView.findViewById(R.id.buttonDelete);
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
		/* upgrade ? */
        Log.e("LayerListAdapter.getView", "install prog  " + d.install_progress + " layer " + d.name + " installed " + d.installed);

		mViewHolder.title.setText(d.title);
		mViewHolder.desc.setText(d.short_desc);
		mViewHolder.image.setBackgroundDrawable(d.icon);
        mViewHolder.buttonInstall.setImageResource(android.R.drawable.ic_menu_add);
        mViewHolder.buttonInstall.setTag(R.id.listItemInstallOrCancelButtonState, R.string.install);
		
		if(d.install_progress < 100)
		{
            mViewHolder.buttonInstall.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            mViewHolder.buttonInstall.setTag(R.id.listItemInstallOrCancelButtonState, R.string.cancel_button);
			mViewHolder.buttonUpgrade.setVisibility(View.GONE);
            mViewHolder.buttonDelete.setVisibility(View.GONE);
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
			else
				mViewHolder.availableVerTextView.setText("la concha de la lora : " + d.available_version  + " installed " + d.installed_version);

			if(d.installed)
			{
                mViewHolder.buttonInstall.setVisibility(View.GONE);
                mViewHolder.buttonDelete.setVisibility(View.VISIBLE);
				mViewHolder.installedVerTextView.setVisibility(View.VISIBLE);
			}
			else if(!d.installed)
			{
                mViewHolder.buttonInstall.setVisibility(View.VISIBLE);
                mViewHolder.buttonDelete.setVisibility(View.GONE);
				mViewHolder.installedVerTextView.setVisibility(View.GONE);

			}

			if(d.available_version > d.installed_version && d.installed)
				mViewHolder.buttonUpgrade.setVisibility(View.VISIBLE);
			else
				mViewHolder.buttonUpgrade.setVisibility(View.GONE);
		}

		mViewHolder.buttonInstall.setOnClickListener(this);
		mViewHolder.buttonUpgrade.setOnClickListener(this);
        mViewHolder.buttonDelete.setOnClickListener(this);
		mViewHolder.buttonInstall.setTag(position);
		mViewHolder.buttonUpgrade.setTag(position);
        mViewHolder.buttonDelete.setTag(position);
		return itemView;
	}

	public ArrayList<String> dumpProgressToString()
	{
		ArrayList<String> repr = new ArrayList<String>();
		String encoded;
		try {
			for(int i = 0;  i < this.getCount(); i++)
			{
		        ByteArrayOutputStream out = new ByteArrayOutputStream();
				LayerItemData l = this.getItem(i);
                Log.e("LayerListAdapter>dumping", "name " + l.name + " prog " + l.install_progress + " inst " + l.installed);
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
							d.restoreProgressInformation(fields);
                            Log.e("LayerListAdapter.restoreProgressFromString", "restored name " + d.name + " inst " + d.installed + " prog " + d.install_progress);
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
			Log.e("onClick", " clicked on id " + v.getId());
            ImageButton b = (ImageButton) v;
			LayerItemData d = this.getItem(Integer.parseInt(b.getTag().toString()));

			if(!d.isValid())
				return;
			if(v.getId() == R.id.buttonInstall ||v.getId() == R.id.buttonUpgrade )
			{
				mLayerActionListener.onActionRequested(d.name, ACTION_DOWNLOAD);
			}
			else if(v.getId() == R.id.buttonInstall && (int) v.getTag(R.id.listItemInstallOrCancelButtonState) == R.string.cancel_button )
			{
				mLayerActionListener.onActionRequested(d.name, ACTION_CANCEL_DOWNLOAD);
			}
			else if(v.getId() == R.id.buttonDelete) {
                mLayerActionListener.onActionRequested(d.name, ACTION_REMOVE);
            }

	}
}
