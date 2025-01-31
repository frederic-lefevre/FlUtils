/*
 * MIT License

Copyright (c) 2017, 2025 Frederic Lefevre

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.fl.util.os;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class NetworkUtils {
	
	private ArrayNode IPv4 = JsonNodeFactory.instance.arrayNode();
	private ArrayNode IPv6 = JsonNodeFactory.instance.arrayNode();
	private ArrayNode otherAddresses = JsonNodeFactory.instance.arrayNode();
	private ArrayNode networkInterfaces = JsonNodeFactory.instance.arrayNode();
	private List<String> IPv4List = new ArrayList<String>();
	
	public NetworkUtils(boolean withLookup) {
		
		Enumeration<NetworkInterface> interfaces;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();

			while (interfaces.hasMoreElements()) {
				NetworkInterface current = interfaces.nextElement();
				networkInterfaces.add(current.toString()) ;
				if (current.isUp() && !current.isLoopback()	&& !current.isVirtual()) {					
					Enumeration<InetAddress> addresses = current.getInetAddresses();
					while (addresses.hasMoreElements()) {
						ObjectNode currAddrHost = JsonNodeFactory.instance.objectNode();
						InetAddress current_addr = addresses.nextElement();
						if (!current_addr.isLoopbackAddress()) {
							if (withLookup) {
								String hostName = current_addr.getHostName() ;
								if (hostName == null) {
									hostName = "" ;
								}
								currAddrHost.put("Hostname", hostName);
							}
							
							String addr = current_addr.getHostAddress() ;							
							currAddrHost.put("IPaddress", addr);
							if (current_addr instanceof Inet4Address) {
								IPv4.add(currAddrHost);
								IPv4List.add(addr) ;
							} else if (current_addr instanceof Inet6Address) {
								IPv6.add(currAddrHost);
							} else {
								otherAddresses.add(currAddrHost);
							}
						}
					}
				}
			}
		} catch (SocketException e) {
			
			e.printStackTrace();
		}
	}

	public ArrayNode getIPv6() {
		return IPv6;
	}

	public ArrayNode getNetworkInterfaces() {
		return networkInterfaces;
	}

	public ArrayNode getIPv4() {
		return IPv4;
	}
	
	public ArrayNode getOtherAddresses() {
		return otherAddresses;
	}

	public List<String> getIPv4List() {
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
