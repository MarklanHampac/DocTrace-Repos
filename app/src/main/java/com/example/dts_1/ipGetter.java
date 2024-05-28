package com.example.dts_1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class ipGetter {
    private final Context context;
    private String address1;
    private String address2;

    public ipGetter(Context context) {
        this.context = context;
    }

    public String getWifiIpAddress() {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            address1 = formatIpAddress(ipAddress);
            address2 = "192.168.8.110:5000"; //hardcoded IP

            return address2;
        }
        return null;
    }

    @SuppressLint("DefaultLocale")
    private String formatIpAddress(int ipAddress) {
        return String.format("%d.%d.%d.%d",
                (ipAddress & 0xff),
                (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff),
                (ipAddress >> 24 & 0xff));
    }
}
