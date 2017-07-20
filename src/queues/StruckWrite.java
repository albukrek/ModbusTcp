package queues;

import ModbusTcp.WriteOperation;

public class StruckWrite {
   private int slaveId;
   private WriteOperation writeOperation;
   // The following 2 parameters are for user test of the weighting head
   private int Register;
   private int value;

    public int getSlaveId() {
        return slaveId;
    }

    public void setSlaveId(int slaveId) {
        this.slaveId = slaveId;
    }

    public WriteOperation getWriteOperation() {
        return writeOperation;
    }

    public void setWriteOperation(WriteOperation writeOperation) {
        this.writeOperation = writeOperation;
    }

    public int getRegister() {
        return Register;
    }

    public void setRegister(int register) {
        Register = register;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}

