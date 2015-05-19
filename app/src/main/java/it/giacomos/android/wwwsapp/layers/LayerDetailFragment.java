package it.giacomos.android.wwwsapp.layers;

import android.os.Bundle;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.R.id;
import it.giacomos.android.wwwsapp.R.layout;
import it.giacomos.android.wwwsapp.dummy.DummyContent;

/**
 * A fragment representing a single Layer detail screen. This fragment is either
 * contained in a {@link LayerListActivity} in two-pane mode (on tablets) or a
 * {@link LayerDetailActivity} on handsets.
 */
public class LayerDetailFragment extends Fragment 
{
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_DATA = "item_id";
	
	private LayerItemData mData;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public LayerDetailFragment() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_DATA)) 
		{
			FileUtils cache = new FileUtils();
			String layerName = getArguments().getString(ARG_ITEM_DATA);
			String xmlData = cache.loadFromStorage(LayerListActivity.CACHE_LIST_DIR + layerName + ".xml", 
					this.getActivity().getApplicationContext());
			mData = new XmlParser().parseLayerDescription(xmlData);
			Bitmap bmp = cache.loadBitmapFromStorage(LayerListActivity.CACHE_LIST_DIR + layerName + ".bmp", 
					this.getActivity().getApplicationContext());
			if(bmp != null)
				mData.icon = new BitmapDrawable(getActivity().getResources(), bmp);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_layer_detail,
				container, false);

		// Show the dummy content as text in a TextView.
		if (mData != null) {
			((TextView) rootView.findViewById(R.id.layer_detail))
					.setText(mData.name);
		}

		return rootView;
	}
}
