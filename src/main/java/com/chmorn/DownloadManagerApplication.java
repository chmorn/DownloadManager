package com.chmorn;

import com.chmorn.javafx.DownloadView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author chenxu
 * @version 1.0
 * @className DownloadQqWebApplication
 * @description 启动类
 * @date 2021/7/21
 **/
@SpringBootApplication
@EnableSwagger2
public class DownloadManagerApplication extends AbstractJavaFxApplicationSupport {
    private static Logger logger = LoggerFactory.getLogger(DownloadManagerApplication.class);
    public static void main(String[] args) {
        //SpringApplication.run(DownloadManagerApplication.class,args);
        logger.info("启动开始喽……");
        launch(DownloadManagerApplication.class, DownloadView.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("定时下载");
        super.start(stage);
    }

    //修改图标icon，默认图标看父类
    @Override
    public Collection<Image> loadDefaultIcons() {
        return Arrays.asList(new Image(this.getClass().getResource("/com/chmorn/javafx/myicon-1.png").toExternalForm()));
    }

}
