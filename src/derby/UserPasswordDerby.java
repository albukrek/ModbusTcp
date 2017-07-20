package derby;

import java.sql.*;
import ModbusTcp.Dialogs;


public class UserPasswordDerby {
    private Connection connection = null;
    private String dbName = "Resources/WeightDB";
    private String connectionURL = "jdbc:derby:" + dbName; // + ";create=true";
    private Dialogs dialogs =new Dialogs();

    private void connectDb() {
        try {
            connection = DriverManager.getConnection(connectionURL);
        } catch (SQLException e) {
            dialogs.msgError("Derby DB Error",
                    "The program will stop immediately !",
                    "A Problem occurred while opening " + connectionURL + ", Please contact Your administrator.");
             System.exit(1);
            // e.printStackTrace();
        }
    }

    private void closeDbConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            dialogs.msgError("Derby DB Error","There is a Problem closing ");
            e.printStackTrace();
        }
    }


    /*
        ---------------------------------------------------------------------
                            USER_PASSWORD Table
        ---------------------------------------------------------------------
    */
    public Boolean checkUserPassword(String userName, String password) throws SQLException {
        PreparedStatement preparedStatement;
        connectDb();

        String query = "SELECT COUNT(USER_NAME) FROM APP.USER_PASSWORD WHERE USER_NAME = ? AND PASSWORD = ?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, userName);
        preparedStatement.setString(2, password);

        ResultSet resultSet = preparedStatement.executeQuery();
        int rowCount =0;
        if(resultSet.next()) {
            rowCount = resultSet.getInt(1);
        }
        closeDbConnection();
        if(rowCount>0) {
            return true;
        }else  return  false;
    }
}

