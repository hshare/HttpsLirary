/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.loopj.android.http;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;

import android.content.Context;
import android.util.Log;

/**
 * Used to intercept and handle the responses from requests made using {@link AsyncHttpClient}. The
 * {@link #onSuccess(int, org.apache.http.Header[], String)} method is designed to be anonymously
 * overridden with your own response handling code. <p>&nbsp;</p> Additionally, you can override the
 * {@link #onFailure(int, org.apache.http.Header[], String, Throwable)}, {@link #onStart()}, and
 * {@link #onFinish()} methods as required. <p>&nbsp;</p> For example: <p>&nbsp;</p>
 * <pre>
 * AsyncHttpClient client = new AsyncHttpClient();
 * client.get("http://www.google.com", new TextHttpResponseHandler() {
 *     &#064;Override
 *     public void onStart() {
 *         // Initiated the request
 *     }
 *
 *     &#064;Override
 *     public void onSuccess(String responseBody) {
 *         // Successfully got a response
 *     }
 *
 *     &#064;Override
 *     public void onFailure(String responseBody, Throwable e) {
 *         // Response failed :(
 *     }
 *
 *     &#064;Override
 *     public void onFinish() {
 *         // Completed the request (either success or failure)
 *     }
 * });
 * </pre>
 */
public abstract class TextHttpResponseHandler extends AsyncHttpResponseHandler {

	private static final String LOG_TAG = "TextHttpResponseHandler";

    // added by yinjiaolong 当usertoken失效的时候重新login 20151201 start
    private boolean isLogin = false;
	private Context context = null;
	private String url = null;
	private RequestParams params = null;

	/** 请求状态 */
	private int status = STATUS_INIT;

	/** 状态：初始化 */
	private static final int STATUS_INIT = 0;

	/** 状态：失败 */
	private static final int STATUS_FAILURE = 1;
	
	/** 状态：成功 */
	private static final int STATUS_SUCCESS = 2;
	
	/** 状态：完成 */
	private static final int STATUS_COMPLETE = 3;

	public int getStatus() {
		return status;
	}

    public void setUrl(String url) {
		this.url = url;
	}

	public void setParams(RequestParams params) {
		this.params = params;
	}

    public String getUrl() {
		return url;
	}

    public RequestParams getParams() {
		return params;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}

    public void setContext(Context context) {
		this.context = context;
	}

    /**
     * Creates new instance with default UTF-8 encoding
     */
    public TextHttpResponseHandler(boolean isLogin) {
        this(DEFAULT_CHARSET);
        this.isLogin = isLogin;
    }
	// added by yinjiaolong 当usertoken失效的时候重新login 20151201 start

    /**
     * Creates new instance with default UTF-8 encoding
     */
    public TextHttpResponseHandler() {
        this(DEFAULT_CHARSET);
    }

    /**
     * Creates new instance with given string encoding
     *
     * @param encoding String encoding, see {@link #setCharset(String)}
     */
    public TextHttpResponseHandler(String encoding) {
        super();
        setCharset(encoding);
    }

    /**
     * Called when request fails
     *
     * @param statusCode     http response status line
     * @param headers        response headers if any
     * @param responseString string response of given charset
     * @param throwable      throwable returned when processing request
     */
    public abstract void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable);

    /**
     * Called when request succeeds
     *
     * @param statusCode     http response status line
     * @param headers        response headers if any
     * @param responseString string response of given charset
     */
    public abstract void onSuccess(int statusCode, Header[] headers, String responseString);

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBytes) {
    	if (this.status != STATUS_INIT) {
    		return;
    	}
    	if (responseBytes.length == 0) {
    		this.status = STATUS_FAILURE;
    		onFailure(statusCode, headers, getResponseString(responseBytes, getCharset()), null);
    	} else {
        	this.status = STATUS_SUCCESS;
            onSuccess(statusCode, headers, getResponseString(responseBytes, getCharset()));    		
    	}
    	this.status = STATUS_COMPLETE;
    	// added by yinjiaolong 离线的时候读取数据缓存 20151208 end
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBytes, Throwable throwable) {
    	// added by yinjiaolong 离线的时候读取数据缓存 20151208 start
    	if (this.status != STATUS_INIT) {
    		return;
    	} else {
    		this.status = STATUS_FAILURE;
    	}
    	// added by yinjiaolong 离线的时候读取数据缓存 20151208 end
        onFailure(statusCode, headers, getResponseString(responseBytes, getCharset()), throwable);
    	// added by yinjiaolong 离线的时候读取数据缓存 20151208 start
    	this.status = STATUS_COMPLETE;
    	// added by yinjiaolong 离线的时候读取数据缓存 20151208 end
    }

    /**
     * Attempts to encode response bytes as string of set encoding
     *
     * @param charset     charset to create string with
     * @param stringBytes response bytes
     * @return String of set encoding or null
     */
    public static String getResponseString(byte[] stringBytes, String charset) {
        try {
            String toReturn = (stringBytes == null) ? null : new String(stringBytes, charset);
            if (toReturn != null && toReturn.startsWith(UTF8_BOM)) {
                return toReturn.substring(1);
            }
            return toReturn;
        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_TAG, "Encoding response into string failed", e);
            return null;
        }
    }
}
