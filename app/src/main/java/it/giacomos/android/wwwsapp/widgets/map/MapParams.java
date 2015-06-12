package it.giacomos.android.wwwsapp.widgets.map;

import android.location.Location;

import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by giacomo on 12/06/15.
 */
public class MapParams
{
    public MapParams(LatLngBounds b, Location l)
    {
        location = l;
        bounds = b;
    }
    public LatLngBounds bounds;
    public Location location;
}
