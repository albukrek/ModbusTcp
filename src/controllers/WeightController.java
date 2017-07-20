package controllers;

import ModbusTcp.FillTable;
import ModbusTcp.ModbusTcp;
import ModbusTcp.TotalPerTime;
import ModbusTcp.WriteOperation;
import com.jfoenix.controls.JFXButton;
import derby.DerbyDB;
import excel.ExcelExport;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import queues.ReadQueue;
import queues.StruckRead;
import queues.StruckWrite;
import services.WeightDbService;

import java.lang.reflect.Field;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class WeightController implements Initializable, ControlledScreen {

    //Save Parent
    private ScreensController myController;

    // creates thread pool with 6 thread
    private final ExecutorService service = Executors.newFixedThreadPool(6);

    //Units (Kg., Ton) from the screen
    private String currentUnit = "";

    //Supplement to complete objects on the screen
    private static final String[] measureUnit = new String[]{" kg", " t", " m/s", " t/h"};

    private final DecimalFormat df = new DecimalFormat("#.00");

    //Connect to Derby db
    private DerbyDB derbyDB = new DerbyDB();
    //Data From table TOTAL_PER_TIME
    private ObservableList<TotalPerTime> data;
    //ExportExcel Class for saving msExcel file
    private ExcelExport excelExport;

    private int numberOfWeights;

    // Create Queue of array list of registers
    private ConcurrentLinkedQueue<StruckRead> queueRead = new ConcurrentLinkedQueue<>();
    // Create Queue of array list to write registers
    private ConcurrentLinkedQueue<StruckWrite> queueWrite = new ConcurrentLinkedQueue<>();

    // Runnable thread to read data from weight heads to the queue
    private ModbusTcp readModbus1;
    private ModbusTcp readModbus2;
    // Runnable thread to read data from queue for UI use
    private ReadQueue readQueue;

    private final int IN_WEIGHING_MOD = 0xff;
    private FillTable fillTable = new FillTable();

    /* Connect FXML objects to this Controller
        --------------------------------------------  */



    @FXML
    private Button cls1;

    @FXML
    private Button zer1;

    @FXML
    private Button auth1;

    @FXML
    private Button cls2;

    @FXML
    private Button zer2;

    @FXML
    private Button auth2;

    @FXML
    private Button cls3;

    @FXML
    private Button zer3;

    @FXML
    private Button auth3;

    @FXML
    private Text totalWTxt0;

    @FXML
    private Text flowTxt0;

    @FXML
    private Text speedTxt0;

    @FXML
    private Text totalWTxt1;

    @FXML
    private Text flowTxt1;

    @FXML
    private Text speedTxt1;

    @FXML
    private Text totalWTxt2;

    @FXML
    private Text flowTxt2;

    @FXML
    private Text speedTxt2;

    @FXML
    private List<Text> totalWList;

    @FXML
    private List<Text> flowList;

    @FXML
    private List<Text> speedList;

    @FXML
    private RadioButton tonRB;

    @FXML
    private RadioButton kgRB;

    @FXML
    private ToggleGroup unitGr;

    @FXML
    private Text displayTxt;

    @FXML
    private TextField sendText;

    @FXML
    private Button sendButton;

    @FXML
    private JFXButton connectButton;


    public void setScreenParent(ScreensController screenParent){
        myController = screenParent;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTotalWeightList();
        setFlowList();
        setSpeedList();
        GetUnitToggle();
        sendButton.setDisable(true);
        sendText.setDisable(true);
        //loadDataFromDatabase();
    }


    private void setTotalWeightList() {
        totalWList = new ArrayList<>();
        for(int i =0 ; i< 3 ; i++){
            try {
                Field field = getClass().getDeclaredField("totalWTxt"+i);
                boolean wasAccessible = field.isAccessible();
                field.setAccessible(true);
                Text text = (Text) field.get(this);
                field.setAccessible(wasAccessible);
                totalWList.add(text);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
    private void setFlowList(){
        flowList = new ArrayList<>();
        for(int i =0 ; i< 3 ; i++){
            try {
                Field field = getClass().getDeclaredField("flowTxt"+i);
                boolean wasAccessible = field.isAccessible();
                field.setAccessible(true);
                Text text = (Text) field.get(this);
                field.setAccessible(wasAccessible);
                flowList.add(text);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void setSpeedList(){
        speedList = new ArrayList<>();
        for(int i =0 ; i< 3 ; i++){
            try {
                Field field = getClass().getDeclaredField("speedTxt"+i);
                boolean wasAccessible = field.isAccessible();
                field.setAccessible(true);
                Text text = (Text) field.get(this);
                field.setAccessible(wasAccessible);
                speedList.add(text);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void clearScale(ActionEvent event) {
        int slaveNumber = Integer.parseInt(((Button)event.getSource()).getId().substring(3));
        StruckWrite struckWrite = new StruckWrite();
        struckWrite.setSlaveId(slaveNumber);
        struckWrite.setWriteOperation(WriteOperation.CLEAR_SCREEN);
        struckWrite.setRegister(0);
        struckWrite.setValue(0);
        queueWrite.add(struckWrite);
    }

    @FXML
    private void zeroCalibration(ActionEvent event) {
        int slaveNumber = Integer.parseInt(((Button)event.getSource()).getId().substring(3));
        StruckWrite struckWrite = new StruckWrite();
        struckWrite.setSlaveId(slaveNumber);
        struckWrite.setWriteOperation(WriteOperation.ZERO_CLIBRATION);
        struckWrite.setRegister(0);
        struckWrite.setValue(0);
        queueWrite.add(struckWrite);
    }

    @FXML
    private void workAuthorization(ActionEvent event) {
        int slaveNumber = Integer.parseInt(((Button)event.getSource()).getId().substring(4));
        StruckWrite struckWrite = new StruckWrite();
        struckWrite.setSlaveId(slaveNumber);
        struckWrite.setWriteOperation(WriteOperation.WORK_AUTHORIZATION);
        struckWrite.setRegister(0);
        struckWrite.setValue(0);
        queueWrite.add(struckWrite);
    }

    @FXML
    private void GetUnitToggle() {
        RadioButton selectedRB = (RadioButton) unitGr.getSelectedToggle();
        currentUnit = selectedRB.getText();
    }

    @FXML
    private void getTextToSend() {
        String inStr = sendText.getText();
        int[] arrayCommand = Arrays.stream(inStr.substring(0, inStr.length()).split(":"))
                .mapToInt(Integer::parseInt).toArray();
        StruckWrite struckWrite = new StruckWrite();
        struckWrite.setSlaveId(arrayCommand[0]);
        struckWrite.setWriteOperation(WriteOperation.FREE_WRITE);
        struckWrite.setRegister(arrayCommand[1]);
        struckWrite.setValue(arrayCommand[2]);
        queueWrite.add(struckWrite);
        sendText.setText("");
    }

    @FXML
    private void startStopConnection() {
            if (connectButton.getText().equals("Connect")) {
                startConnection();
                sendButton.setDisable(false);
                sendText.setDisable(false);
                connectButton.setText("Disconnect");
            } else if (connectButton.getText().equals("Disconnect")) {
                stopConnection();
                sendButton.setDisable(true);
                sendText.setDisable(true);
                connectButton.setText("Connect");
            }
        }

    @FXML
    void appExit() {
        System.exit(0);
    }

    @FXML
    private void goToTables(){
        // Go to Tables Screen
        myController.setScreen(ScreenNames.screen3ID);
    }

    private void initThreads() {

        try {

            readModbus1 = new ModbusTcp(derbyDB.getIp(1), derbyDB.getPort(1),
                    derbyDB.getSlaveId(1), 0, 6, queueRead, queueWrite);
            readModbus2 = new ModbusTcp(derbyDB.getIp(2), derbyDB.getPort(2),
                    derbyDB.getSlaveId(2), 0, 6, queueRead, queueWrite);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        readQueue = new ReadQueue(queueRead, this);
    }

    private void startConnection() {

        WeightDbService weightDbService = new WeightDbService();
        try {
            weightDbService.startService();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            numberOfWeights = derbyDB.getNumberOfWeights();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initThreads();
        service.submit(readModbus1);
        service.submit(readModbus2);
        service.submit(readQueue);

    }

    private void stopConnection() {
        readModbus1.stop();
        readModbus2.stop();
        readQueue.stop();
        try {
            service.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setWorkStateMassage(int slaveId, int response) {
        if (response != IN_WEIGHING_MOD) {
            displayTxt.setText("Slave" + slaveId + "- System is Calibrating Please wait! (" + response + ")");
            totalWList.get(slaveId - 1).setText("Please wait!");
        } else {
            displayTxt.setText("");
        }
    }

    public void editFxController(int slaveId, long totalWeight, float speed, float flow) {

        try {
            derbyDB.setCurrentWeight(slaveId + 1, totalWeight);
            // userPassword.setTotalPerTime();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Fill FX controllers: totalWList
        if (currentUnit.equals("kg"))
            totalWList.get(slaveId).setText(totalWeight + measureUnit[0]);
        else
            totalWList.get(slaveId).setText(String.valueOf(df.format((float) totalWeight / 1000)) + measureUnit[1]);

        //Fill FX controllers: speedList, flowList
        speedList.get(slaveId).setText(speed + measureUnit[2]);
        flowList.get(slaveId).setText(flow + measureUnit[3]);

    }
} //end Controller
