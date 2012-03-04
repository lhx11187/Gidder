package net.antoniy.gidder.dns;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.antoniy.gidder.ui.util.GidderCommons;
import net.antoniy.gidder.ui.util.PrefsConstants;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class DynamicDNSManager {

	private final static String TAG = DynamicDNSManager.class.getSimpleName();

	private final static int PROVIDER_INDEX_DYNDNS = 0;
	private final static int PROVIDER_INDEX_NOIP = 1;
	
	private final Context context;
	private final SharedPreferences prefs;
	
	public DynamicDNSManager(Context context) {
		this.context = context;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public void update() {
		final boolean active = prefs.getBoolean(PrefsConstants.DYNDNS_ACTIVE.getKey(), false);
		final String hostname = prefs.getString(PrefsConstants.DYNDNS_DOMAIN.getKey(), "");
		final int providerIndex = prefs.getInt(PrefsConstants.DYNDNS_PROVIDER_INDEX.getKey(), -1);
		final String username = prefs.getString(PrefsConstants.DYNDNS_USERNAME.getKey(), "");
		final String password = prefs.getString(PrefsConstants.DYNDNS_PASSWORD.getKey(), "");
		
		if(!active) {
			return;
		}

		if("".equals(username.trim()) || "".equals(password.trim()) || "".equals(hostname.trim())) {
			Toast.makeText(context, "Dynamic DNS information is NOT valid!", Toast.LENGTH_SHORT);
		}
		
		final String address = GidderCommons.getCurrentWifiIpAddress(context);
		
		if(providerIndex == PROVIDER_INDEX_NOIP) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					Looper.prepare();
					try {
						DynamicDNSFactory.createNoIpStrategy(context).update(
								URLEncoder.encode(hostname, "UTF-8"), 
								URLEncoder.encode(address, "UTF-8"), 
								URLEncoder.encode(username), 
								URLEncoder.encode(password));
					} catch (UnsupportedEncodingException e) {
						Log.e(TAG, "Problem using UTF-8 encoding.", e);
					}
				}
			}).start();
		} else if(providerIndex == PROVIDER_INDEX_DYNDNS) {
			// TODO: implement dyndns integration
		}
	}
}
 