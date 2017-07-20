package queues;

import ModbusTcp.ConvertData;
import controllers.WeightController;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class ReadQueue implements Runnable {


    /* Runnable thread parameters */
    private boolean runThread = true;

    /* Queue object handler  */
    private ConcurrentLinkedQueue<StruckRead> queueRead;
    /* Get hold of the Controller class*/
    private WeightController controller = null;


    public ReadQueue(ConcurrentLinkedQueue<StruckRead> queueRead, WeightController controller){
        this.queueRead = queueRead;
        this.controller = controller;

    }

    public void stop() {
        runThread = false;
    }

    @Override
    public void run() {
         /* Object to manipulate registers data to actual meaningful values */
        ConvertData convertData = new ConvertData();

        System.out.println("ReadQueue Thread Started");
        while (runThread) {
            StruckRead struckRead = queueRead.poll();
            if (struckRead != null) {
                if (struckRead.getInputText() != struckRead.INPUT_TEXT) {
                    long totalWeight = convertData.toLong(struckRead.getTotalWeightLow(),
                                       struckRead.getTotalWeighHigh());
                    float speed = convertData.toFloat(struckRead.getSpeedLow(), struckRead.getSpeedHigh());
                    float flow = convertData.toFloat(struckRead.getFlowLow(), struckRead.getFlowHigh());
                    controller.editFxController(struckRead.getSlaveId() -1, totalWeight, speed, flow);
                } else
                    controller.setWorkStateMassage(struckRead.getSlaveId(), struckRead.getResponse());
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
