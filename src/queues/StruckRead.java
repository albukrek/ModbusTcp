package queues;


public class StruckRead {
    public final int INPUT_TEXT =196424;

    private int slaveId;
    private int totalWeightLow;
    private int totalWeighHigh;
    private int speedLow;
    private int speedHigh;
    private int flowLow;
    private int flowHigh;


   public StruckRead setArray(int headStruck[]) {
        this.slaveId = headStruck[0];
        this.totalWeightLow = headStruck[1];
        this.totalWeighHigh = headStruck[2];
        this.speedLow = headStruck[3];
        this.speedHigh = headStruck[4];
        this.flowLow = headStruck[5];
        this.flowHigh = headStruck[6];

        return this;
    }

    public StruckRead setWorkingStateStruck(int slaveId, int response) {
        this.slaveId = slaveId;
        this.totalWeightLow = 0;
        this.totalWeighHigh = response;
        this.speedLow = 0;
        this.speedHigh = 0;
        this.flowLow = 0;
        this.flowHigh = 196424;

        return this;
    }

    public int getSlaveId() {
        return slaveId;
    }

    public int getTotalWeightLow() {
        return totalWeightLow;
    }

    public int getTotalWeighHigh() {
        return totalWeighHigh;
    }

    public int getSpeedLow() {
        return speedLow;
    }

    public int getSpeedHigh() {
        return speedHigh;
    }

    public int getFlowLow() {
        return flowLow;
    }

    public int getFlowHigh() {
        return flowHigh;
    }

    ////--------------------------
    public int getResponse() {
        return totalWeighHigh;
    }

    public int getInputText() {
        return flowHigh;
    }
}
