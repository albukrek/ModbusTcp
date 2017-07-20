package derby;

import java.sql.*;

import ModbusTcp.Dialogs;


public class DerbyDB {
    private Connection connection = null;
    private String dbName = "Resources/WeightDB";
    private String connectionURL = "jdbc:derby:" + dbName; // + ";create=true";
    private Dialogs dialogs =new Dialogs();

    public DerbyDB() {
        connectDb();
    }


    void connectDb() {
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

    void closeDbConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            dialogs.msgError("Derby DB Error","There is a Problem closing ");
            e.printStackTrace();
        }
    }

/*
    ---------------------------------------------------------------------
                        Parameter Table
    ---------------------------------------------------------------------
*/

    private String getValue(int weightId, String name) throws SQLException {
        PreparedStatement preparedStatement;
        if (connection == null) {
            connectDb();
        }
        String query = "SELECT VALUE FROM PARAMETERS WHERE WEIGHT_ID = ? AND NAME = ?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, weightId);
        preparedStatement.setString(2, name);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next())
            return resultSet.getString("VALUE");
        else return "Error Reading";
    }


    public String getIp(int weightId) throws SQLException {
        return getValue(weightId, "IP");
    }


    public int getPort(int weightId) throws SQLException {
        return Integer.parseInt(getValue(weightId, "PORT"));
    }


    public int getSlaveId(int weightId) throws SQLException {
        return Integer.parseInt(getValue(weightId, "SLAVE_ID"));
    }

/*
    ---------------------------------------------------------------------
                        Weight Names Table
    ---------------------------------------------------------------------
*/

    public int getNumberOfWeights() throws SQLException {
        PreparedStatement preparedStatement;
        if (connection == null) {
            connectDb();
        }
        String query = "SELECT COUNT(*) FROM APP.WEIGHT_NAMES";
        preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next())
            return resultSet.getInt(1);
        else return 0;
    }

    /*
        ---------------------------------------------------------------------
                            CURRENT_WEIGHT Table
        ---------------------------------------------------------------------
    */
    public void setCurrentWeight(int weightId, long totalWeight) throws SQLException {
        PreparedStatement preparedStatement;
        if (connection == null) {
            connectDb();
        }
        String query = "UPDATE APP.CURRENT_WEIGHT  SET TOTAL_WEIGHT = ? WHERE WEIGHT_ID = ?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setLong(1, totalWeight);
        preparedStatement.setInt(2, weightId);
        preparedStatement.executeUpdate();
    }


    private ResultSet getLastWeightData() throws SQLException {
        PreparedStatement preparedStatement;
        if (connection == null) {
            connectDb();
        }
        //Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String query = "SELECT WEIGHT_ID, TOTAL_WEIGHT FROM CURRENT_WEIGHT ";
        preparedStatement = connection.prepareStatement(query);
        return preparedStatement.executeQuery();
    }

    /*
        ---------------------------------------------------------------------
                            TOTAL_PER_TIME Table
        ---------------------------------------------------------------------
    */
    private void insertTotalPerTime(int weightId, long totalWeight) throws SQLException {
        PreparedStatement preparedStatement;
        if (connection == null) {
            connectDb();
        }
        Timestamp timestamp = new Timestamp((System.currentTimeMillis()));
        String query = "INSERT INTO APP.TOTAL_PER_TIME (WEIGHT_ID, TOTAL, TIME_STAMP) VALUES(?, ?, ?)";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, weightId);
        preparedStatement.setLong(2, totalWeight);
        preparedStatement.setTimestamp(3, timestamp);
        preparedStatement.executeUpdate();

    }

    public void setTotalPerTime() throws SQLException {
        ResultSet res = getLastWeightData();
        while (res.next()) {
            insertTotalPerTime(res.getInt("WEIGHT_ID"), res.getLong("TOTAL_WEIGHT"));
        }
    }

    public ResultSet getTotalPerTime() throws SQLException {
        PreparedStatement preparedStatement;
        if (connection == null) {
            connectDb();
        }
        String query = "SELECT WEIGHT_ID, TOTAL, TIME_STAMP FROM APP.TOTAL_PER_TIME ";
        preparedStatement = connection.prepareStatement(query);
        return preparedStatement.executeQuery();
    }

    /*
        ---------------------------------------------------------------------
                            USER_PASSWORD Table
        ---------------------------------------------------------------------
    */
    public Boolean checkUserPassword(String userName, String password) throws SQLException {
        PreparedStatement preparedStatement;
        if (connection == null) {
            connectDb();
        }
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

