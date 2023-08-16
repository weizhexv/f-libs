package com.jkqj;

import com.jkqj.wx.api.WxApi;
import com.jkqj.wx.api.WxApiImpl;
import com.jkqj.wx.api.WxFeature;
import com.jkqj.wx.api.model.AcCodeRequest;
import com.jkqj.wx.api.model.AcCodeResult;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
//    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

//    @Test
    public void test() throws IOException {

        OkHttpClient okHttpClient =  new OkHttpClient.Builder().connectTimeout(1000L, MILLISECONDS)
                .readTimeout(1000L, MILLISECONDS)
                .writeTimeout(1000L,MILLISECONDS)
                .build();

        WxApi wxApi = new WxApiImpl(okHttpClient);

        WxFeature  wxFeature = new WxFeature(wxApi,null,"wxba52371b1d6466cc","0b25900ecc83874be01010c16d54cb3c");

        AcCodeRequest request = new AcCodeRequest();
        request.setPage("http://a.reta-inc.com");
        request.setScene("target=abc");
        request.setCheckPath(false);
        request.setEnvVersion("develop");
        request.setWidth(1280);

        AcCodeResult result =  wxFeature.createAcCode(request);

        FileOutputStream fos = new FileOutputStream("/Users/hexiufeng/wx/a" + "." + result.getContentType());
        fos.write(result.getBuffer());
        fos.close();
        System.out.println(result);
    }

    @Test
    public void test1() {
        Integer x = null;

        boolean b = x instanceof Integer;

        System.out.println(b);
    }
}
