package com.loopj.android.http;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;




public class SSLSocketFactoryEx extends SSLSocketFactory {
    SSLContext sslContext = SSLContext.getInstance("TLS");
    public static final String P12_PWD = "123456";
    public static final String BKS_PWD = "123456";
    public SSLSocketFactoryEx(KeyStore truststore)
            throws NoSuchAlgorithmException, KeyManagementException,
            KeyStoreException, UnrecoverableKeyException {
        super(truststore);
        TrustManager tm = new X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain, String authType)
                    throws java.security.cert.CertificateException {

            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain, String authType)
                    throws java.security.cert.CertificateException {

            }
        };

        sslContext.init(null, new TrustManager[] { tm }, null);
    }

    public static SSLSocketFactory getSocketFactory() {  
        try {  
        	 KeyStore keyStore = KeyStore.getInstance("PKCS12");
        	  KeyStore trustStore = KeyStore.getInstance("bks");
            try {  
            	  keyStore.load(HttpsApplication.ins, P12_PWD.toCharArray());
                  trustStore.load(HttpsApplication.pks, BKS_PWD.toCharArray());
            }  
            finally {  
            	HttpsApplication.ins.close();  
            }  
            SSLSocketFactory factory = new SSLSocketFactory(keyStore, P12_PWD, trustStore);  
            return factory;  
        } catch (Throwable e) {  
//            Log.d(TAG, e.getMessage());  
            e.printStackTrace();  
        }  
        return null;  
    }  
    @Override
    public Socket createSocket(Socket socket, String host, int port,
            boolean autoClose) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(socket, host, port,
                autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }
}