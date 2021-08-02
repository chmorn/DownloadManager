package com.chmorn.javafx;


import com.browniebytes.javafx.control.DateTimePicker;
import com.chmorn.base.ApiCode;
import com.chmorn.base.ApiResult;
import com.chmorn.config.M3u8Config;
import com.chmorn.iptv.M3u8Model;
import com.chmorn.model.RequestModel;
import com.chmorn.model.ResponseModel;
import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@FXMLController
public class DownloadController implements Initializable {

    @FXML
    private AnchorPane container;
    @FXML
    private ChoiceBox m3u8box;
    @FXML
    private TextField downloadDir;
    @FXML
    private DateTimePicker startDatePicker;
    @FXML
    private DateTimePicker stopDatePicker;

    @FXML
    private TableView mDownloadTable;
    @FXML
    private TableColumn downloadId;
    @FXML
    private TableColumn m3u8url;
    @FXML
    private TableColumn distPath;
    @FXML
    private TableColumn timeStart;
    @FXML
    private TableColumn timeEnd;
    @FXML
    private TableColumn stateValue;
    @FXML
    private TableColumn stopDownload;

    @Autowired
    private RestTemplate restTemplate = new RestTemplate();

    private static String iptv = "http://localhost:9001";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        downloadDir.setEditable(false);
        try {
            intiM3u8ChoiceBox(m3u8box);
        } catch (IOException e) {
            e.printStackTrace();
        }
        AnchorPane panel = new AnchorPane();

        downloadId.setCellValueFactory(new PropertyValueFactory<>("downloadId"));
        m3u8url.setCellValueFactory(new PropertyValueFactory<>("m3u8url"));
        distPath.setCellValueFactory(new PropertyValueFactory<>("distPath"));
        timeStart.setCellValueFactory(new PropertyValueFactory<>("timeStart"));
        timeEnd.setCellValueFactory(new PropertyValueFactory<>("timeEnd"));
        stateValue.setCellValueFactory(new PropertyValueFactory<>("stateValue"));
        stopDownload.setCellFactory((col)->{
            TableCell<ResponseModel, String> cell = new TableCell<ResponseModel, String>(){
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    Button button1 = new Button("停止");
                    button1.setStyle("-fx-background-color: #00bcff;-fx-text-fill: #ffffff");
                    button1.setOnMouseClicked((col) -> {
                        //获取list列表中的位置，进而获取列表对应的信息数据
                        ResponseModel selectData = (ResponseModel) mDownloadTable.getItems().get(getIndex());

                        //按钮事件自己添加
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("停止提示框");
                        alert.setHeaderText(null);
                        alert.setContentText("正在停止id("+selectData.getDownloadId()+")的下载，请勿操作！");
                        alert.showAndWait();
                        ApiResult result = restTemplate.getForObject(iptv+"/iptv/stopDownload?downloadId="+selectData.getDownloadId(), ApiResult.class);
                        //System.out.println(result);
                        String rescode = result.getCode();
                        if(rescode.equals(ApiCode.SUCC.getCode())){
                            alert.setTitle("停止提示框");
                            alert.setHeaderText(null);
                            alert.setContentText("停止成功");
                            alert.showAndWait();
                        }else{
                            alert.setTitle("停止提示框");
                            alert.setHeaderText(null);
                            alert.setContentText("停止失败:"+result.getMsg());
                            alert.showAndWait();
                        }
                        refreshGrid();
                    });

                    if (empty) {
                        //如果此列为空默认不添加元素
                        setText(null);
                        setGraphic(null);
                    } else {
                        this.setGraphic(button1);
                    }
                }
            };
            return cell;
        });

    }

    @FXML
    protected void startDownloadButtonAction(ActionEvent event) {
        ProgressBar progressBar = new ProgressBar();

        DirectoryChooser file=new DirectoryChooser();
        String start = startDatePicker.getTextField().getText();
        String stop = stopDatePicker.getTextField().getText();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        if(m3u8box.getValue().toString().startsWith("default")){
            alert.setContentText("请先择下载地址！");
            alert.showAndWait();
            return;
        }
        if(downloadDir.getText()==null || downloadDir.getText().length()==0){
            alert.setContentText("请先择保存目录！");
            alert.showAndWait();
            return;
        }

        RequestModel model = new RequestModel(m3u8box.getValue().toString(),downloadDir.getText(),start,stop);
        ApiResult result = restTemplate.postForObject(iptv+"/iptv/download",model, ApiResult.class);
        //System.out.println(result);
        String rescode = result.getCode();
        if(rescode.equals(ApiCode.SUCC.getCode())){
            alert.setContentText("成功！");
            alert.showAndWait();
            refreshGrid();
        }else{
            alert.setContentText("失败："+result.getMsg());
            alert.showAndWait();
            //return;
        }
    }

    @FXML
    protected void chooseDirectoryButtonAction(ActionEvent event) {
        //文件夹选择器
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("请选择保存目录！");
        File file = directoryChooser.showDialog(container.getScene().getWindow());//这个file就是选择的文件夹了
        String path = file.getPath();
        //System.out.println(path);
        downloadDir.setText(path);
        //文件选择器
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Open Resource File");
//        fileChooser.showOpenDialog(stage);
    }

    /**
     * 初始化m3u8地址选择框
    **/
    private void intiM3u8ChoiceBox(ChoiceBox m3u8box) throws IOException {
        m3u8box.getItems().add(new M3u8Model("default","----------------------------------------------------------请选择----------------------------------------------------------").toUrlString());
        m3u8box.getItems().addAll(M3u8Config.getM3u8UrlString());
        m3u8box.getSelectionModel().selectFirst();
    }

    /**
     * 刷新下载列表展示
     * 说明：UI刷新频率过快会发生异常：Exception in thread "JavaFX Application Thread"
     * javafx提供了Platform.runLater用于解决该问题
    **/
    public void refreshGrid(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ApiResult result = restTemplate.getForObject(iptv+"/iptv/getDownloadList", ApiResult.class);
                //System.out.println(result);
                String rescode = result.getCode();
                if(rescode.equals(ApiCode.SUCC.getCode())){
                    ObservableList<ResponseModel> observableList = FXCollections.observableArrayList();
                    JSONArray arr = JSONArray.fromObject(result.getData());
                    for (int i = 0; i < arr.size(); i++) {
                        ResponseModel responseModel = new ResponseModel();
                        responseModel.setDownloadId(JSONObject.fromObject(arr.getJSONObject(i)).getInt("downloadId"));
                        responseModel.setM3u8url(JSONObject.fromObject(arr.getJSONObject(i)).getString("m3u8url"));
                        responseModel.setDistPath(JSONObject.fromObject(arr.getJSONObject(i)).getString("distPath"));
                        responseModel.setTimeStart(JSONObject.fromObject(arr.getJSONObject(i)).getString("timeStart"));
                        responseModel.setTimeEnd(JSONObject.fromObject(arr.getJSONObject(i)).getString("timeEnd"));
                        int state = JSONObject.fromObject(arr.getJSONObject(i)).getInt("state");
                        responseModel.setStateValue(state==0?"正常":"停止");
                        observableList.add(responseModel);
                    }
                    mDownloadTable.setItems(observableList);
                }
            }
        });


    }

}
