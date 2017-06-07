package com.loopj.android.http;

import org.apache.http.HttpHost;
import org.apache.http.conn.HttpHostConnectException;


import android.content.Context;
import android.os.Handler;

public class GyAsyncHttpClient extends AsyncHttpClient {

	private boolean isLogin = false;

	private Context context = null;

	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	public GyAsyncHttpClient (boolean fixNoHttpResponseException, int httpPort, int httpsPort) {
		super(fixNoHttpResponseException, httpPort, httpsPort);
		this.addHeader("user-agent", "android 6.0.1");
	}

    /**
     * Perform a HTTP POST request with parameters.
     *
     * @param url             the URL to send the request to.
     * @param params          additional POST parameters or files to send with the request.
     * @param responseHandler the response handler instance that should handle the response.
     * @return RequestHandle of future request process
     */
    public RequestHandle post(String url, RequestParams params, ResponseHandlerInterface responseHandler) {
		if (responseHandler != null && responseHandler instanceof TextHttpResponseHandler) {
			((TextHttpResponseHandler)responseHandler).setContext(this.context);
			((TextHttpResponseHandler)responseHandler).setLogin(this.isLogin);
			((TextHttpResponseHandler)responseHandler).setUrl(url);
			((TextHttpResponseHandler)responseHandler).setParams(params);
		}
		final ResponseHandlerInterface temp = responseHandler;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				temp.sendFailureMessage(0, null, null, new HttpHostConnectException(new HttpHost(UrlUtil.HOST), null));
			}
		}, 35000);
        return super.post(url, params, responseHandler);
    }

}
