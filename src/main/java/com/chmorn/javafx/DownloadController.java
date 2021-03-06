package com.chmorn.javafx;


import com.browniebytes.javafx.control.DateTimePicker;
import com.chmorn.base.ApiCode;
import com.chmorn.base.ApiResult;
import com.chmorn.base.DownloadStateEnum;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@FXMLController
public class DownloadController implements Initializable {

    private static Logger logger = LoggerFactory.getLogger(DownloadController.class);

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
                    Button button1 = new Button("??????");
                    button1.setStyle("-fx-background-color: #00bcff;-fx-text-fill: #ffffff");
                    button1.setOnMouseClicked((col) -> {
                        //??????list????????????????????????????????????????????????????????????
                        ResponseModel selectData = (ResponseModel) mDownloadTable.getItems().get(getIndex());

                        //????????????????????????
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("???????????????");
                        alert.setHeaderText(null);
                        alert.setContentText("????????????id("+selectData.getDownloadId()+")???????????????????????????");
                        alert.showAndWait();
                        ApiResult result = restTemplate.getForObject(iptv+"/iptv/stopDownload?downloadId="+selectData.getDownloadId(), ApiResult.class);
                        //System.out.println(result);
                        String rescode = result.getCode();
                        if(rescode.equals(ApiCode.SUCC.getCode())){
                            alert.setTitle("???????????????");
                            alert.setHeaderText(null);
                            alert.setContentText("????????????:"+result.getMsg());
                            alert.showAndWait();
                        }else{
                            alert.setTitle("???????????????");
                            alert.setHeaderText(null);
                            alert.setContentText("????????????:"+result.getMsg());
                            alert.showAndWait();
                        }
                        refreshGrid();
                    });

                    if (empty) {
                        //???????????????????????????????????????
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

    //????????????
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
            alert.setContentText("????????????????????????");
            alert.showAndWait();
            return;
        }
        if(downloadDir.getText()==null || downloadDir.getText().length()==0){
            alert.setContentText("????????????????????????");
            alert.showAndWait();
            return;
        }

        RequestModel model = new RequestModel(m3u8box.getValue().toString(),downloadDir.getText(),start,stop);
        ApiResult result = restTemplate.postForObject(iptv+"/iptv/download",model, ApiResult.class);
        //System.out.println(result);
        String rescode = result.getCode();
        if(rescode.equals(ApiCode.SUCC.getCode())){
            alert.setContentText("?????????");
            alert.showAndWait();
            refreshGrid();
        }else{
            alert.setContentText("?????????"+result.getMsg());
            alert.showAndWait();
            //return;
        }
    }

    //?????????????????????
    @FXML
    protected void chooseDirectoryButtonAction(ActionEvent event) {
        //??????????????????
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("????????????????????????");
        File file = directoryChooser.showDialog(container.getScene().getWindow());//??????file???????????????????????????
        String path = file.getPath();
        //System.out.println(path);
        downloadDir.setText(path);
        //???????????????
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Open Resource File");
//        fileChooser.showOpenDialog(stage);
    }

    //????????????????????????
    @FXML
    protected void refreshDownloadButtonAction(ActionEvent event){
        logger.info("????????????????????????????????????????????????");
        refreshGrid();
    }

    /**
     * ?????????m3u8???????????????
    **/
    private void intiM3u8ChoiceBox(ChoiceBox m3u8box) throws IOException {
        m3u8box.getItems().add(new M3u8Model("default","----------------------------------------------------------?????????----------------------------------------------------------").toUrlString());
        m3u8box.getItems().addAll(M3u8Config.getM3u8UrlString());
        m3u8box.getSelectionModel().selectFirst();
    }

    /**
     * ????????????????????????
     * ?????????UI????????????????????????????????????Exception in thread "JavaFX Application Thread"
     * javafx?????????Platform.runLater?????????????????????
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
                        responseModel.setStateValue(DownloadStateEnum.getMsg(state));
                        observableList.add(responseModel);
                    }
                    mDownloadTable.setItems(observableList);
                }
            }
        });


    }

}
