package ModbusTcp;

import derby.DerbyDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

import ModbusTcp.Dialogs;

public class FillTable {
   ObservableList<TotalPerTime> data;
   private DerbyDB derbyDB = new DerbyDB();
   private Dialogs dialogs = new Dialogs();

    public ObservableList<TotalPerTime> fromDb(){
       ResultSet resultSet;
       boolean foundData = false;
       data = FXCollections.observableArrayList();
       try {
           resultSet = derbyDB.getTotalPerTime();
//           int i = 0;
           while (resultSet.next()){
               foundData = true;
               data.add(new TotalPerTime(resultSet.getInt("WEIGHT_ID"),
                       resultSet.getLong("TOTAL"), resultSet.getTimestamp("TIME_STAMP")));
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
       if(foundData){
           int maxLine = data.size();
//           dialogs.msgInformational("Table Updated", "Found " + maxLine + " rows in the table.");
           System.out.println("maxLine: " + maxLine);
       }

       return data;
   }

}
