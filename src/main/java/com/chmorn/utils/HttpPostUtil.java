package com.chmorn.utils;

import java.io.IOException;
import java.io.Serializable;

import com.chmorn.model.CkeyModel;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpPostUtil implements Serializable {

    private static Logger logger = LoggerFactory.getLogger(HttpPostUtil.class);

    private static final long   serialVersionUID = 1L;
    private static final String CHAR_SET         = "UTF-8";
    private static final String CONTENT_TYPE     = "application/json";

    private static final String proxyUrl = "https://vd.l.qq.com/proxyhttp";

    
    /**
     * 请求接口
     * @param requestUrl 接口请求地址
     * @param json 
     * @return	UTF-8解码返回数据
     */
    @SuppressWarnings({ "deprecation", "resource" })
    private static JSONObject doPostRequestJSON(String requestUrl, JSONObject json) {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(requestUrl);
        JSONObject response = null;
        try {
            StringEntity string = new StringEntity(json.toString(),CHAR_SET);
            string.setContentEncoding(CHAR_SET);
            string.setContentType(CONTENT_TYPE);
            post.setEntity(string);
            HttpResponse res = client.execute(post);
            System.err.println("http status is " + res.getStatusLine().getStatusCode());
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = res.getEntity();
                //UTF-8解码返回数据
                String result = EntityUtils.toString(entity,CHAR_SET);
                response = JSONObject.fromObject(result);
            }else{
                response = new JSONObject();
                try {
                    response.put("code", -1);
                    response.put("msg", "请求系统异常");
                } catch (JSONException e1) {}
            }
        } catch (IOException e) {
            response = new JSONObject();
            try {
            	response.put("code", -1);
                response.put("msg", "请求系统异常");
            } catch (JSONException e1) {}
            e.printStackTrace();
        } catch (JSONException e) {
            response = new JSONObject();
            try {
            	response.put("code", -1);
                response.put("msg", "请求系统异常");
            } catch (JSONException e1) {}
            e.printStackTrace();
        }

        return response;
    }
    

    private static JSONObject doPost(String requestUrl, JSONObject json) {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(requestUrl);
        JSONObject response = null;
        try {
        	post.addHeader("accept", "application/json, text/javascript, */*; q=0.01");
        	post.addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.164 Safari/537.36");
        	post.addHeader("cookie", "pgv_pvi=7519770624; pgv_pvid=8084283636; RK=9MQcqciGGL; ptcz=b257d68bc7f48ee232055a4cc677cf5e3e42fce18bd9e3ea44c8d67129c31246; eas_sid=U1L6x116q2C2o7Z5d021m7z9t7; tvfe_boss_uuid=264b1c7d91a72c1d; appuser=7DBB9DBE94FF9421; LZTturn=325; ufc=r24_1_1626796159_1626709939; pgv_info=ssid=s9037600415; LPPBturn=610; LPSJturn=397; LZIturn=840; LVINturn=859; LPHLSturn=353; LPDFturn=39; lv_play_index=39; o_minduid=");
            StringEntity string = new StringEntity(json.toString(),CHAR_SET);
            string.setContentEncoding(CHAR_SET);
            string.setContentType(CONTENT_TYPE);
            post.setEntity(string);
            HttpResponse res = client.execute(post);
            System.err.println("http status is " + res.getStatusLine().getStatusCode());
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = res.getEntity();
                //UTF-8解码返回数据
                String result = EntityUtils.toString(entity,CHAR_SET);
                response = JSONObject.fromObject(result);
            }else{
                response = new JSONObject();
                try {
                    response.put("code", -1);
                    response.put("msg", "请求系统异常");
                } catch (JSONException e1) {}
            }
        } catch (IOException e) {
            response = new JSONObject();
            try {
            	response.put("code", -1);
                response.put("msg", "请求系统异常");
            } catch (JSONException e1) {}
            e.printStackTrace();
        } catch (JSONException e) {
            response = new JSONObject();
            try {
            	response.put("code", -1);
                response.put("msg", "请求系统异常");
            } catch (JSONException e1) {}
            e.printStackTrace();
        }

        return response;
    }

    public static JSONObject doPost(CkeyModel ckeyModel) {
        JSONObject js = new JSONObject();
        js.put("buid", "vinfoad");
        String logintoken="%7B%22main_login%22%3A%22wx%22%2C%22openid%22%3A%22oXw7q0BzKoJqU5srxczHsrODoUHE%22%2C%22appid%22%3A%22wxa75efa648b60994b%22%2C%22access_token%22%3A%2247_5ZfxDI2eQ-gD-pgYoBmkj-0kkx2MeRMPW0CaGjCyc5miTaANfa0ktWzk6kdv635N2w8LZg5YadpZk1kv7bRBLQ%22%2C%22vuserid%22%3A%22747930663%22%2C%22vusession%22%3A%22MW3UBt6_K7AEIlPWt1H5Zw..%22%7D";
        String vinfoparam = "spsrt=1&charge=0&defaultfmt=auto&otype=ojson&guid=2ef2c0d358a97d2133db7c2ded8284f2&flowid=6badd0fdcf1833249af55b90b71715ec_10201&platform=10201&sdtfrom=v1010&defnpayver=1&appVer=3.5.57&host=v.qq.com&ehost="+ckeyModel.getEncodeUrl()+"&refer=v.qq.com&sphttps=1&tm="+ckeyModel.getTm()+"&spwm=4&logintoken="+logintoken+"&vid="+ckeyModel.getVid()+"&defn=fhd&fhdswitch=0&show1080p=1&isHLS=1&dtype=3&sphls=2&spgzip=1&dlver=2&drm=32&hdcp=1&spau=1&spaudio=15&defsrc=2&encryptVer=9.1&cKey="+ckeyModel.getcKey()+"&fp2p=1&spadseg=3";
        js.put("vinfoparam",vinfoparam);
        JSONObject response = doPost(proxyUrl,js);
        logger.info(response.toString());
        return response;
    }


}
