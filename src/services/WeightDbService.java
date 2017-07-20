package services;

import derby.DerbyDB;
import javafx.util.Duration;

import java.sql.SQLException;

public class WeightDbService {
    DerbyDB derbyDB = new DerbyDB();
    public void startService() throws Exception {
        WriteWeightService writeWeightService = new WriteWeightService();
        writeWeightService.setDelay(Duration.seconds(120));
        writeWeightService.setPeriod(Duration.seconds(120));
        // @todo talk with Yaniv about the  writeWeightService.setBackoffStrategy();
        writeWeightService.setOnSucceeded(event -> {
            try {
                derbyDB.setTotalPerTime();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        writeWeightService.start();
    }
}
