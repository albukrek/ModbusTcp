package services;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

public class WriteWeightService extends ScheduledService<Object> {
    @Override
    protected Task<Object> createTask() {
        return new Task<Object>() {
            protected Integer call() {
                return null;
            }
        };
    }
}
