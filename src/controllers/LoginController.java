package controllers;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import derby.UserPasswordDerby;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class LoginController implements Initializable, ControlledScreen {

    private ScreensController myController;

    //Derby DB User_Password Table
    private UserPasswordDerby userPassword = new UserPasswordDerby();

    @FXML
    private JFXTextField userTxt;

    @FXML
    private JFXPasswordField passTxt;

    @FXML
    private Text massageTxt;


    public void setScreenParent(ScreensController screenParent){
        myController = screenParent;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
          }

    @FXML
    void loginToMain()  throws IOException, SQLException {
        massageTxt.setText("");
        if(userPassword.checkUserPassword(userTxt.getText(), passTxt.getText())) {
            // Go to MainScreen
            myController.setScreen(ScreenNames.screen2ID);

        } else {
            massageTxt.setText("Username or Password not exists!");
        }
    }

    @FXML
    void appExit() {
        System.exit(0);
    }

    @FXML
    void clearMassage() {
        massageTxt.setText("");
    }
}// End of LoginController
