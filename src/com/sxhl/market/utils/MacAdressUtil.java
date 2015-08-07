package com.sxhl.market.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class MacAdressUtil {

    
	
	
	/**
	 * 获取wifi网卡的物理地址
	 * @author wenfuqiang
	 * */
	public static String getWifiMacAddress(Context mContext) {
	    String macAddress = "";// wifi物理地址
	    WifiManager wifi = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
		if (wifi == null) {
			return "";
		}
		WifiInfo info = wifi.getConnectionInfo();
		macAddress = info.getMacAddress();
		return macAddress;
	}
	
	
    public static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }

                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ip;
    }

    
    
    /**
     * 获取以太网的物理地址
     * */
    @SuppressLint("NewApi")
	public static String getMacAddress() /* throws UnknownHostException */{
        String strMacAddr = null;
        try {
            InetAddress ip = getLocalInetAddress();

            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append('-');
                }

                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return strMacAddr;
    }
    
    
    /***
     * @author wenfuqiang
     * 获取蓝牙的mac地址
     * */
    public static String getBluetoothMacAdress(){
    	BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	if(null == bluetoothAdapter){
    		return "";
    	}
    	return bluetoothAdapter.getAddress();
    }

	
	

}
