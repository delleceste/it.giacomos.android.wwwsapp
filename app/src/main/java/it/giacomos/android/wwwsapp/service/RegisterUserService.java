package it.giacomos.android.wwwsapp.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import it.giacomos.android.wwwsapp.PersonData;
import it.giacomos.android.wwwsapp.network.HttpPostParametrizer;
import it.giacomos.android.wwwsapp.network.Urls;

/**
 * Created by giacomo on 9/06/15.
 */
public class RegisterUserService
{
    private Context mContext;

    public RegisterUserService(PersonData mPersonData, String deviceId, Context ctx)
    {
        String url = new Urls().getRegisterUserUrl();
        String serviceName = "RegisterUserService";
        HttpPostParametrizer parametrizer = new HttpPostParametrizer();
        parametrizer.add("account", mPersonData.account);
        parametrizer.add("gplus_url", mPersonData.gplusUrl);
        parametrizer.add("display_name", mPersonData.name);
        parametrizer.add("device_id", deviceId);

        String params = parametrizer.toString();
        Intent service_intent = new Intent(ctx, PostDataService.class);
        service_intent.putExtra("params", params);
        service_intent.putExtra("url", url);
        service_intent.putExtra("serviceName", serviceName);

        Log.e("RegisterUserService", "starting service PostDataService for " + serviceName);
        ctx.startService(service_intent);
    }
}
