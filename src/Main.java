import controllers.ScreenNames;
import controllers.ScreensController;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
  //  ScreenNames ScreenNames;

    @Override
    public void start(Stage primaryStage){
        ScreensController mainContainer = new ScreensController();
        mainContainer.loadScreen(ScreenNames.screen1ID, ScreenNames.screen1File);
        mainContainer.loadScreen(ScreenNames.screen2ID, ScreenNames.screen2File);
        mainContainer.loadScreen(ScreenNames.screen3ID, ScreenNames.screen3File);

        // Set First Screen
        mainContainer.setScreen(ScreenNames.screen1ID);

        Group root = new Group();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("L I B R A  -  Modbus Tcp/IP");
        primaryStage.getIcons().add(new Image("file:/icons/libra.png"));
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> System.exit(0));

    }


    public static void main(String[] args) {
        launch(args);
    }
}
