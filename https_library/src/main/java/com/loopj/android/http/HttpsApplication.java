package com.loopj.android.http;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Application;

public class HttpsApplication {
    public static InputStream ins = null;
    public static InputStream pks = null;

    public static void init(Application app, String s1, String s2, String s3, String s4) {
        try {
            ins = app.getResources().getAssets().open(s1);
            String insStr = InputStreamUtils.InputStreamTOString(ins);
            insStr = EncoderUtil.get3DESDecrypt(insStr, s2);
            ins = InputStreamUtils.StringTOInputStream(insStr);


            pks = app.getResources().getAssets().open(s3);
            String pksStr = InputStreamUtils.InputStreamTOString(pks);
            pksStr = EncoderUtil.get3DESDecrypt(pksStr, s4);
            pks = InputStreamUtils.StringTOInputStream(pksStr);

        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
