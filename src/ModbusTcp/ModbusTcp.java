package ModbusTcp;


import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterRequest;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import queues.StruckRead;
import queues.StruckWrite;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class ModbusTcp implements Runnable {

    private static final int CLEAR_SCREEN_REGISTER = 9;
    private static final int OPERATION_REGISTER = 59;
    private static final int ZERO_CALIBRATION_VALUE = 0X21;
    private static final int CLEAR_SCREEN_VALUE = 0XA5;
    private static final int WORKING_STATE = 50;
    private static final int IN_WEIGHING_MOD_STATE = 0xff;
    private static final int WORK_AUTHORIZATION_REGISTER = 39;
    private static final int WORK_AUTHORIZATION_ON = 255;
    private static final int WORK_AUTHORIZATION_OFF = 0;
    //private static final int IN_ZERO_CALIBRATION_MOD_STATE = 0x4;


    /* Slave Head parameters */
    private InetAddress address = null; // slave's address
    private int port = Modbus.DEFAULT_PORT; // slave's port
    private int slaveId = 0; // Modbus Slave number
    private int ref = 0; //start reading Address
    private int count = 0; //number of registers to read

    /* Modbus Tcp Reading Parameters*/
    private TCPMasterConnection con = null;
    //private ReadMultipleRegistersResponse res = null;

    /* Runnable thread parameters */
    private boolean runThread = true;

    /* Queue object handler  */
    private ConcurrentLinkedQueue<StruckRead> queueRead;
    private ConcurrentLinkedQueue<StruckWrite> queueWrite;

    public ModbusTcp(String address, int port, int slaveId, int ref, int count, ConcurrentLinkedQueue<StruckRead> queueRead,
                     ConcurrentLinkedQueue<StruckWrite> queueWrite) {
        try {
            this.address = InetAddress.getByName(address);
            this.port = port;
            this.slaveId = slaveId;
            this.ref = ref;
            this.count = count;
            this.queueRead = queueRead;
            this.queueWrite = queueWrite;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        runThread = false;
    }

    private int[] getRegs(ReadMultipleRegistersResponse res, int fromReg, int toReg) {
        int regArray[] = new int[toReg + 1];
        regArray[0] = this.slaveId;
        int i = 1;
        for (int k = fromReg; k < fromReg + toReg; k++) {
            regArray[i++] = res.getRegisterValue(k);
        }
        return regArray;
    }

    private void startConnection() {
        con = new TCPMasterConnection(address);
        con.setPort(port);
        try {
            con.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ReadMultipleRegistersRequest getRequest(int ref, int count, int slaveId) {
        ReadMultipleRegistersRequest req;
        req = new ReadMultipleRegistersRequest();
        req.setReference(ref);
        req.setWordCount(count);
        req.setUnitID(slaveId);
        req.setDataLength(6);
        return req;
    }

// ------------      Write Register -----------------------
    private void do_Writing(StruckWrite struckWrite) {
        int slaveId = struckWrite.getSlaveId();
        switch (struckWrite.getWriteOperation()){
            case CLEAR_SCREEN:
                writeReg(slaveId, CLEAR_SCREEN_REGISTER, CLEAR_SCREEN_VALUE);
                break;
            case ZERO_CLIBRATION:
                writeReg(slaveId, OPERATION_REGISTER, ZERO_CALIBRATION_VALUE);
                waitForWorkingState(slaveId, IN_WEIGHING_MOD_STATE);
                break;
            case WORK_AUTHORIZATION:
                int state = readSingleReg(slaveId, WORK_AUTHORIZATION_REGISTER);
                int reg;
                if(state == WORK_AUTHORIZATION_ON) reg = WORK_AUTHORIZATION_OFF;
                                             else  reg = WORK_AUTHORIZATION_ON;
                 writeReg(slaveId,WORK_AUTHORIZATION_REGISTER,reg);
            case FREE_WRITE:
            writeReg(slaveId, struckWrite.getRegister(), struckWrite.getValue());
        }


    }

    private void writeReg(int slaveId, int reg, int data) {

        SimpleRegister regData = new SimpleRegister(data);
        WriteSingleRegisterRequest reqWrite = new WriteSingleRegisterRequest();
        reqWrite.setUnitID(slaveId);
        reqWrite.setReference(reg - 1);
        reqWrite.setRegister(regData);
        reqWrite.setDataLength(1);
        ModbusTCPTransaction transWrite = new ModbusTCPTransaction(this.con);
        transWrite.setRequest(reqWrite);
        try {
            transWrite.execute();
        } catch (ModbusException e) {
            e.printStackTrace();
        }
    }

    private void waitForWorkingState(int slaveId, int neededValue){
        int response = 0;
        while (response != IN_WEIGHING_MOD_STATE) try {
            response = readSingleReg(slaveId, WORKING_STATE);
            queueRead.add(new StruckRead().setWorkingStateStruck(slaveId, response));
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) { e.printStackTrace();}

        queueRead.add(new StruckRead().setWorkingStateStruck(slaveId, neededValue));
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

// ----------------------- Single register read -----------------------
    private int readSingleReg(int slaveId, int reg) {
        ModbusTCPTransaction transwrite;
        ReadMultipleRegistersRequest reqWrite;
        ReadMultipleRegistersResponse resWrite;

        reqWrite = new ReadMultipleRegistersRequest();
        reqWrite.setReference(reg - 1);
        reqWrite.setWordCount(1);
        reqWrite.setUnitID(slaveId);
        transwrite = new ModbusTCPTransaction(con);
        transwrite.setRequest(reqWrite);
        try {
            transwrite.execute();
        } catch (ModbusException e) {
            e.printStackTrace();
        }
        resWrite = (ReadMultipleRegistersResponse) transwrite.getResponse();
        //int response = resWrite.getRegisterValue(0);
        return resWrite.getRegisterValue(0);
    }

    @Override
    public void run() {
        ReadMultipleRegistersRequest req;
        ModbusTCPTransaction trans;

        startConnection();
        System.out.println(" Thread of Slave " + this.slaveId + " Started on Port: " + con.getPort()
                + " and IP Address: " + con.getAddress().toString());
        req = getRequest(ref, count, slaveId);
        trans = new ModbusTCPTransaction(con);
        trans.setRequest(req);
        while (runThread) {
            if (queueWrite.peek() != null) {
                if (queueWrite.peek().getSlaveId() == this.slaveId) {
                    do_Writing(queueWrite.poll());
                }
            }
            try {
                trans.execute();
            } catch (ModbusException e) {
                e.printStackTrace();
            }
            ReadMultipleRegistersResponse res = (ReadMultipleRegistersResponse) trans.getResponse();
            queueRead.add(new StruckRead().setArray( getRegs(res,0, 6)));
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        con.close();

    }
}
