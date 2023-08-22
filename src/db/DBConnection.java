package db;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DBConnection {
    public static Connection connection;

    //All: Division name : Division ID
    public static Map<String, Integer> US_Divi_ID_map = new HashMap<String, Integer>();
    public static Map<String, Integer> UK_Divi_ID_map = new HashMap<String, Integer>();
    public static Map<String, Integer> Canada_Divi_ID_map = new HashMap<String, Integer>();



    public static void cnntion(){
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/client_schedule",
                    "sqlUser", "Passw0rd!"); //"sqlUser", "Passw0rd!


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static ResultSet get_selectData_resultSet(String queryStr){
        try {
            return connection.createStatement().executeQuery(queryStr);

        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Obtain data of first_level_divisions and store them by countries.
     */
    public static void set_divisionDataMap(){
        if(US_Divi_ID_map.isEmpty()){
            try {
                ResultSet resultSet_fDivisions = connection.createStatement().executeQuery("select Division_ID, Division, Country_ID from first_level_divisions");
                while (resultSet_fDivisions.next()){
                    int theCountryID =  resultSet_fDivisions.getInt("Country_ID");
                    String theDivision = resultSet_fDivisions.getString("Division");
                    int theDivisionID = resultSet_fDivisions.getInt("Division_ID");

                    switch (theCountryID) {
                        case 1 -> US_Divi_ID_map.put(theDivision, theDivisionID);
                        case 2 -> UK_Divi_ID_map.put(theDivision, theDivisionID);
                        case 3 -> Canada_Divi_ID_map.put(theDivision, theDivisionID);
                    }
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static int lookFor_divisionID(String country, String divisionName){
        switch (country){
            case "U.S":
                //iterate the us division map
                for (Map.Entry<String, Integer> entry : DBConnection.US_Divi_ID_map.entrySet()){
                    if(Objects.equals(entry.getKey(), divisionName)){
                        return entry.getValue();
                    }
                }
                break;
            case "UK":
                //iterate the us division map
                for (Map.Entry<String, Integer> entry : DBConnection.UK_Divi_ID_map.entrySet()){
                    if(Objects.equals(entry.getKey(), divisionName)){
                        return entry.getValue();
                    }
                }
                break;
            case "Canada":
                //iterate the us division map
                for (Map.Entry<String, Integer> entry : DBConnection.Canada_Divi_ID_map.entrySet()){
                    if(Objects.equals(entry.getKey(), divisionName)){
                        return entry.getValue();
                    }
                }
                break;
        }
        return 0;
    }


    public static String[] find_countryId_with_divisionID(int divisionID){
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("select Division, Country_ID from first_level_divisions where Division_ID="+Integer.toString(divisionID));
            while (resultSet.next()){
                return new String[]{resultSet.getString("Division"), Integer.toString(resultSet.getInt("Country_ID"))} ;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return new String[]{"0"};
    }

    /**
     * Return an arrayList that stores each string array of a record's id and its name for all the table records.
     * @param tableName The table name of the database.
     */
    public static ArrayList<String[]> select_allRecords_byTableName(String tableName){
        ArrayList<String[]> allRecords_arrayList = new ArrayList<String[]>();
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("select * from " + tableName);
            while (resultSet.next()){
                allRecords_arrayList.add(new String[]{Integer.toString(resultSet.getInt(1)), resultSet.getString(2)}); //1st col as ID, 2nd col as its name
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return allRecords_arrayList;
    }

    /**
     * Find a user in the user table and return an array with the user info. Return null if a user is not existed.
     * @param usrNme The string of a username.
     * @param pass The string of a password.
     */
    public static String[] find_anUsr_array(String usrNme, String pass){
        String[] usrInfoArray = new String[2];
        int counts = 0;
        try{
            ResultSet findUsr_result = connection.createStatement().executeQuery(String.format("select * from users where User_Name='%s' and Password='%s'", usrNme, pass));
            while (findUsr_result.next()){
                usrInfoArray[0] = findUsr_result.getString(1); //usrid
                usrInfoArray[1] = findUsr_result.getString(2); //usrnme
                counts++;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        if(counts ==0){
            return null;
        }else {
            return usrInfoArray;
        }
    }

    public static void insert_update_customer(String method, String Customer_Name, String Address, String Postal_Code, String Phone,
                                       Timestamp Create_Date, String Created_By,
                                       Timestamp Last_Update, String Last_Updated_By, int Division_ID, int Customer_ID) throws SQLException {
        String sql = switch (method) {
            case "insert" -> """
                    INSERT INTO `customers`
                    (`Customer_Name`,
                    `Address`,
                    `Postal_Code`,
                    `Phone`,
                    `Create_Date`,
                    `Created_By`,
                    `Last_Update`,
                    `Last_Updated_By`,
                    `Division_ID`)
                     VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)""";
            case "update" -> """
                    UPDATE `customers`
                    SET
                    `Customer_Name` = ?,
                    `Address` = ?,
                    `Postal_Code` = ?,
                    `Phone` = ?,
                    `Last_Update` = ?,
                    `Last_Updated_By` = ?,
                    `Division_ID` = ?
                    WHERE `Customer_ID` = ?""";
            default -> """
                    """;
        };

        System.out.println(sql);
        PreparedStatement statement = connection.prepareStatement(sql);
        switch (method) {
            case "insert" -> {
                System.out.println("sql set statement is in insert section");
                statement.setString(1, Customer_Name);
                statement.setString(2, Address);
                statement.setString(3, Postal_Code);
                statement.setString(4, Phone);
                statement.setTimestamp(5, Create_Date);
                statement.setString(6, Created_By);
                statement.setTimestamp(7, Last_Update);
                statement.setString(8, Last_Updated_By);
                statement.setInt(9, Division_ID);
            }
            case "update" -> {
                System.out.println("sql set statement is in update section");
                statement.setString(1, Customer_Name);
                statement.setString(2, Address);
                statement.setString(3, Postal_Code);
                statement.setString(4, Phone);
                statement.setTimestamp(5, Last_Update);
                statement.setString(6, Last_Updated_By);
                statement.setInt(7, Division_ID);
                statement.setInt(8, Customer_ID);
            }
        }



        int rowsInserted = statement.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println("A new customer was inserted successfully!");
        }
    }


    public static void insert_update_appointment(String method, int Appointment_ID,String Title, String Description, String Location, String Type,
                                              Timestamp StartDateTime, Timestamp EndDateTime, Timestamp Create_Date, String Created_By,
                                              Timestamp Last_Update, String Last_Update_By,
                                              int User_ID, int Contact_ID, int Customer_ID) throws SQLException {
        String sql = switch (method) {
            case "insert" -> """
                    INSERT INTO `appointments`
                    (`Title`,
                     `Description`,
                     `Location`,
                     `Type`,
                     `Start`,
                     `End`,
                     `Create_Date`,
                     `Created_By`,
                     `Last_Update`,
                     `Last_Updated_By`,
                     `Customer_ID`,
                     `User_ID`,
                     `Contact_ID`)
                     VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""";
            case "update" -> """
                    UPDATE `appointments`
                    SET
                    `Title` = ?,
                    `Description` = ?,
                    `Location` = ?,
                    `Type` = ?,
                    `Start` = ?,
                    `End` = ?,
                    `Last_Update` = ?,
                    `Last_Updated_By` = ?,
                    `Customer_ID` = ?,
                    `User_ID` = ?,
                    `Contact_ID` = ?
                    WHERE `Appointment_ID` = ?""";
            default -> """
                    """;
        };

        System.out.println(sql);
        PreparedStatement statement = connection.prepareStatement(sql);
        switch (method) {
            case "insert" -> {
                System.out.println("sql set statement is in insert section");
                statement.setString(1, Title);
                statement.setString(2, Description);
                statement.setString(3, Location);
                statement.setString(4, Type);
                statement.setTimestamp(5, StartDateTime);
                statement.setTimestamp(6, EndDateTime);
                statement.setTimestamp(7, Create_Date);
                statement.setString(8, Created_By);
                statement.setTimestamp(9, Last_Update);
                statement.setString(10, Last_Update_By);
                statement.setInt(11, Customer_ID);
                statement.setInt(12, User_ID);
                statement.setInt(13, Contact_ID);
            }
            case "update" -> {
                System.out.println("sql set statement is in update section");
                statement.setString(1, Title);
                statement.setString(2, Description);
                statement.setString(3, Location);
                statement.setString(4, Type);
                statement.setTimestamp(5, StartDateTime);
                statement.setTimestamp(6, EndDateTime);
                statement.setTimestamp(7, Last_Update);
                statement.setString(8, Last_Update_By);
                statement.setInt(9, Customer_ID);
                statement.setInt(10, User_ID);
                statement.setInt(11, Contact_ID);
                statement.setInt(12, Appointment_ID);
            }
        }


        System.out.println(statement);
        int rowsInserted = statement.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println("A new customer was inserted successfully!");
        }
    }

    public static void delete_customer(int cusID) throws SQLException {
        String sql = "DELETE FROM appointments WHERE Customer_ID=?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, cusID);
        int rowsDeleted = statement.executeUpdate();
        if (rowsDeleted > 0) {
            System.out.println("The customer's appointments was deleted successfully!");
        }

        sql = "DELETE FROM customers WHERE Customer_ID=?";
        statement = connection.prepareStatement(sql);
        statement.setInt(1, cusID);
        rowsDeleted = statement.executeUpdate();
        if (rowsDeleted > 0) {
            System.out.println("The customer was deleted successfully!");
        }
    }

    public static void delete_appointment(int apptID) throws SQLException{
        String sql = "DELETE FROM appointments WHERE Appointment_ID=?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, apptID);
        int rowsDeleted = statement.executeUpdate();
        if (rowsDeleted > 0) {
            System.out.println("The appointment was deleted successfully!");
        }
    }

    public static void showAllUsrs(){
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from users");

            while (resultSet.next()){
                System.out.println(resultSet.getString("User_Name"));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
