package com.loopj.android.http.util;

import com.loopj.android.http.GyAsyncHttpClient;

import android.content.Context;
/**
 * 
 * @author WJ
 */
public class LibraryHttpsUtil { 
	private static final int HTTP_PORT = 18084;

	private static final int HTTPS_PORT = 18443;
    private static GyAsyncHttpClient client = new GyAsyncHttpClient(true, HTTP_PORT, HTTPS_PORT); // 实例
   
    static { 
   
        client.setTimeout(10000); // 设置链接超时，如果不设置，默认为10s 
    } 
   
  
    public static GyAsyncHttpClient getClient(Context context){ 
    	client.setContext(context);
        return client; 
    } 
   
}