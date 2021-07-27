package com.chmorn.utils;

import com.chmorn.config.GlobalConfig;
import com.chmorn.model.CkeyModel;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLEncoder;

/**
 * @author chenxu
 * @version 1.0
 * @className WasmUtil
 * @description 获取ckey
 * @date 2021/7/21
 **/
public class WasmUtil {
    private static Logger logger = LoggerFactory.getLogger(WasmUtil.class);

    private static String nodePath = "D:/tools/nodejs/node-v12.16.2-win-x64/node.exe";

    public static void main(String[] args) throws IOException {
        getCkeyModel("https://v.qq.com/x/cover/gehpfier9upkqz5/i0038won4fq.html",null);
    }

    public static CkeyModel getCkeyModel(String url, GlobalConfig config) throws IOException {
        nodePath = config.getNodePath();
        String htmlName = url.substring(url.lastIndexOf("/")+1);
        String vid = htmlName.replace(".html","");
        CkeyModel ckeyModel = new CkeyModel();
        String tm = String.valueOf(System.currentTimeMillis()).substring(0,10);
        ckeyModel.setTm(tm);
        ckeyModel.setVid(vid);
        ckeyModel.setUrl(url);
        ckeyModel.setEncodeUrl(URLEncoder.encode(url,"utf-8"));

        String userPath = System.getProperty("user.dir").replaceAll("\\\\","/");
        String wasmPath = userPath+"/source/ckey.wasm";
        String examplePath = userPath+"/source/example.js";
        String tencentPath = userPath+"/source/tencent.js";
        File tencentFile = new File(tencentPath);
        if(tencentFile.exists()){
            tencentFile.delete();
        }
        tencentFile.createNewFile();

        BufferedReader br = new BufferedReader(new FileReader(examplePath));
        BufferedWriter bw = new BufferedWriter(new FileWriter(tencentFile));
        String line = null;
        while ((line = br.readLine())!=null){
            if(line.trim().startsWith("URL:")){
                line = "URL: \"https://v.qq.com/x/cover/gehpfier9upkqz5/"+htmlName+"\",";
            }
            if(line.trim().startsWith("var wasm_data")){
                line = "var wasm_data = fs.readFileSync('"+wasmPath+"')";
            }
            if(line.trim().startsWith("console.log(getckey")){
                line = "console.log(getckey('10201', '3.5.57', '"+vid+"', '', '1fcb9528b79f2065c9a281a7d554edd1', '"+tm+"'));";
            }
            bw.write(line+"\n");
        }
        bw.flush();
        bw.close();
        br.close();

        String command1 = nodePath + " " + tencentPath;
        logger.info("开始生成ckey..............."+command1);
        ProcessCommandUtil p1 = new ProcessCommandUtil(command1);
        p1.start();
        if(p1.isProcessSuccess()) {
            logger.info("生成ckey成功："+p1.getSuccessMsg());
            logger.info("生成ckey结束.................");
            ckeyModel.setcKey(p1.getSuccessMsg());
        }
        return ckeyModel;
    }


}
