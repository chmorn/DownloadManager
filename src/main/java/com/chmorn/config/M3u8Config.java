package com.chmorn.config;

import com.chmorn.iptv.M3u8Model;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenxu
 * @version 1.0
 * @className M3u8Config
 * @description 解析m3u8地址列表（application.m3u8）,只解析#或http开头的行
 * @date 2021/7/27
 **/
public class M3u8Config {
    public static void main(String[] args) throws IOException {
        String path = System.getProperty("user.dir");
        System.out.println(path);
    }
    private static List<M3u8Model> m3u8List;

    private M3u8Config(){

    }

    public static synchronized List<M3u8Model> getM3u8List() throws IOException {
        if(m3u8List == null){
            m3u8List = new ArrayList<M3u8Model>();
            //String path = M3u8Config.class.getResource("/").getPath();
            String path = System.getProperty("user.dir");
            BufferedReader br = new BufferedReader(new FileReader(path+ File.separator +"application.m3u8"));

            //InputStream is=M3u8Config.class.getResourceAsStream("application.m3u8");
            //BufferedReader br=new BufferedReader(new InputStreamReader(is));

//            Resource resource = new ClassPathResource("application.m3u8");
//            InputStream inputStream = resource.getInputStream();
//            BufferedReader br=new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            String name = null;
            String url = null;
            while((line=br.readLine())!=null){
                M3u8Model model = new M3u8Model();
                line = line.trim();
                //只解析#或http开头的行
                if (StringUtils.isNotEmpty(line)){
                    if(line.startsWith("#")){
                        name = line;
                    }else if(line.startsWith("http")){
                        url = line;
                    }
                    if(name!=null && url!=null){
                        model.setName(name);
                        model.setUrl(url);
                        m3u8List.add(model);
                        name = null;
                        url = null;
                    }
                }
            }
            return m3u8List;
        }
        return m3u8List;
    }

    public static synchronized List<String> getM3u8UrlString() throws IOException {
        List<M3u8Model> list = getM3u8List();
        if(list ==null || list.size()==0){
            return null;
        }
        List<String> result = new ArrayList<>();
        for (int i = 0; i <list.size(); i++) {
            result.add(list.get(i).toUrlString());
        }
        return result;
    }

}
