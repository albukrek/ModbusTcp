package ModbusTcp;

import javafx.beans.property.*;

import java.sql.Timestamp;

/**
 *
 * @author admin
 */
public class TotalPerTime {

    private  IntegerProperty weightId;
    private  LongProperty total;
    private  StringProperty timestamp;

    public TotalPerTime() {
    }

    //Default constructor
    public TotalPerTime(int weightId, long total, Timestamp timestemp) {
        this.weightId = new SimpleIntegerProperty(weightId);
        this.total = new SimpleLongProperty(total);
        this.timestamp = new SimpleStringProperty(timestemp.toString());
     }

    //-------------- Getters ------------------------------------------
    public Integer getWeightId() {
        return weightIdProperty().get();
    }

    public Long getTotal() {
        return totalProperty().get();
    }

    public String getTimestamp(){
        return timestampProperty().get();
    }

    //-------------- Setters ------------------------------------------
    public void setWeightId(Integer value) {
        weightIdProperty().set(value);
    }

    public void setTotal(Long value) {
        totalProperty().set(value);
    }

    public void setTimestamp(Timestamp value){
        timestampProperty().set(value.toString());
    }

    //-------------- Property values ----------------------------------
    public IntegerProperty weightIdProperty() {
        if (weightId == null) weightId = new SimpleIntegerProperty(this, "weightId");
        return weightId;
    }

    public LongProperty totalProperty() {
        if (total == null) total = new SimpleLongProperty(this, "total");
        return total;
    }

    public StringProperty timestampProperty() {
        if (timestamp == null) timestamp = new SimpleStringProperty(this, "timestamp");
        return timestamp;
    }

    public String[] getHeaders(){
        return new String[]{"WEIGHT ID", "TOTAL", "TIMESTAMP"};
    }
}
