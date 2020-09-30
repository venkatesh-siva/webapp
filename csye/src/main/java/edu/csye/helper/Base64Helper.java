package edu.csye.helper;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Helper {

	public static String convertToSting(String baseValue) throws UnsupportedEncodingException {
		
		//if (baseValue != null && baseValue.toLowerCase().startsWith("basic")) {
		    // Authorization: Basic base64credentials
		    String base64Credentials = baseValue.substring("Basic".length()).trim();
		    byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
		    String credentials = new String(credDecoded, StandardCharsets.UTF_8);
		    // credentials = username:password
		    final String[] values = credentials.split(":", 2);
		//}
		
		
		//Base64.Decoder decoder= Base64.getDecoder(); 
		return credentials;	
	}
	
	public static String getUserName(String decoded) {
		String [] values = decoded.split(":",2);
		return values[0];
	}
	
	public static String getPassword(String decoded) {
		String [] values = decoded.split(":",2);
		return values[1];
	}
}
