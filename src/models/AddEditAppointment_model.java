package models;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class AddEditAppointment_model {
    public static String _operationMethod; //add or edit

    public static int Appointment_ID;
    public static String  Title;
    public static String Description;
    public static String Location;
    public static int Contact_ID;
    public static String Type;
    public static Timestamp Start;
    public static Timestamp End;
    public static int Customer_ID;
    public static int User_ID;

    public static Timestamp Create_Date;
    public static String Created_By;
    public static Timestamp Last_Update;
    public static String Last_Updated_By;
}
