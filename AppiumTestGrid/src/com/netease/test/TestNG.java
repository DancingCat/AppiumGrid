package com.netease.test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.testng.TestNGUtils;
import org.testng.reporters.jq.TestNgXmlPanel;

public class TestNG {
	public static void main(String[] args) {
		
		try {
			System.out.println(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
