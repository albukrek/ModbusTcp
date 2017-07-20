package controllers;

import ModbusTcp.FillTable;
import ModbusTcp.TotalPerTime;
import excel.ExcelExport;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import ModbusTcp.Dialogs;

public class TablesController implements Initializable, ControlledScreen {

    private ScreensController myController;

    //Data From table TOTAL_PER_TIME
    private ObservableList<TotalPerTime> data;
    //ExportExcel Class for saving msExcel file
    //private ExcelExport excelExport;

    private FillTable fillTable = new FillTable();
    Dialogs dialogs = new Dialogs();

    @FXML
    private ListView<String> tableList;
    @FXML
    private TableView<TotalPerTime> tableWeightTotal = new TableView<>();
    @FXML
    private TableColumn<TotalPerTime, Integer> colWeightId;
    @FXML
    private TableColumn<TotalPerTime, Long> colTotal;
    @FXML
    private TableColumn<TotalPerTime, String> colTimestamp;
    @FXML
    private TableView<?> table2 = new TableView<>();

    @FXML
    private TableView<?> table3 = new TableView<>();

    public void setScreenParent(ScreensController screenParent){
        myController = screenParent;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tableList.getItems().addAll("Weight Total","Table2","Table3");
        tableList.getSelectionModel().selectFirst();
        tableWeightTotal.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        loadDataFromDatabase();
    }

    @FXML
    private void exportToExcel() {
        ExcelExport excelExport = new ExcelExport(data);
        String fileName = getUserPath() + File.separator + "TotalWorkBook";
        excelExport.excelTotalPerTime(fileName, "Total");
        //dialogs.msgInformational("Export file","Export was done");
        //System.out.println("Export Done!");

    }

    private String getUserPath() {
        Path path = Paths.get(System.getProperty("user.home"));
        DirectoryChooser directoryChooser = new DirectoryChooser();

        directoryChooser.setTitle("Choose Directory");
        directoryChooser.setInitialDirectory(new File(path.toString()));
        File dir = directoryChooser.showDialog(null);
        if (dir == null) {

            dialogs.msgConfirmation("Saving file to...", "File  will be saved in: "+ path.toString());
            return path.toString();
        } else {
            dialogs.msgInformational("Saving file to...", "File  will be saved in: "+ dir.toString());
            return dir.toString();
        }
    }

    @FXML
    private void loadDataFromDatabase() {
        data = fillTable.fromDb();
        if (data.size() > 0) {
            colWeightId.setCellValueFactory(new PropertyValueFactory<>("weightId"));
            colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
            colTimestamp.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
            tableWeightTotal.setItems(data);
        }
    }

    @FXML
    void getTable(MouseEvent event) {
        String tableName =  ((ListView)event.getSource()).getSelectionModel().getSelectedItem().toString();
        tableWeightTotal.setVisible(false);
        table2.setVisible(false);
        table3.setVisible(false);
        switch (tableName){
            case "Weight Total":
                loadDataFromDatabase();
                tableWeightTotal.setVisible(true);
                break;
            case "Table2":
                table2.setVisible(true);
                break;
            case "Table3":
                table3.setVisible(true);
                break;
        }
    }

    @FXML
    private void appExit() {
        System.exit(0);
    }

    @FXML
    private void goToMain(){
        // Go to MainScreen
        myController.setScreen(ScreenNames.screen2ID);
    }
}
