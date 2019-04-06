package com.ibm.lge.fl.util.os;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import com.google.gson.JsonArray;

public class NetworkUtils {
	private JsonArray IPv4 = new JsonArray() ;
	private JsonArray IPv6 = new JsonArray() ;
	private JsonArray networkInterfaces = new JsonArray() ;
	private ArrayList<String> IPv4List = new ArrayList<String>();
	
	public NetworkUtils() {
		
		Enumeration<NetworkInterface> interfaces;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();

			while (interfaces.hasMoreElements()) {
				NetworkInterface current = interfaces.nextElement();
				networkInterfaces.add(current.toString()) ;
				if (current.isUp() && !current.isLoopback()	&& !current.isVirtual()) {					
					Enumeration<InetAddress> addresses = current.getInetAddresses();
					while (addresses.hasMoreElements()) {
						InetAddress current_addr = addresses.nextElement();
						if (!current_addr.isLoopbackAddress()) {
							if (current_addr instanceof Inet4Address) {
								String addr = current_addr.getHostAddress() ;
								IPv4.add(addr);
								IPv4List.add(addr) ;
							} else if (current_addr instanceof Inet6Address) {
								IPv6.add(current_addr.getHostAddress());
							}
						}
					}
				}
			}
		} catch (SocketException e) {
			
			e.printStackTrace();
		}
	}

	public JsonArray getIPv6() {
		return IPv6;
	}

	public JsonArray getNetworkInterfaces() {
		return networkInterfaces;
	}

	public JsonArray getIPv4() {
		return IPv4;
	}
	
	public ArrayList<String> getIPv4List() {
		return IPv4List ;
	}
	
	public String getMachineName() {
		String mn1 = System.getenv("COMPUTERNAME") ;
		if ((mn1 != null) && (! mn1.isEmpty())) {
			return mn1 ;
		} else {
			mn1 = System.getenv("HOSTNAME") ;
			if ((mn1 != null) && (! mn1.isEmpty())) {
				return mn1 ;
			} else {
				return null ;
			}
		}
	}
}
