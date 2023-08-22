package models;

import utility.UtFuns;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

public class Appointment_model {
    public static String _appointmentView; // viewAll, viewCurrentWeek, viewCurrentMonth
    public static String _current_contact_combox_optVal;
    public static String _current_customer_combox_optVal;

    public ZonedDateTime local_Start;

    public ZonedDateTime local_End;

    public int Appointment_ID;
    public String  Title;
    public String Description;
    public String Location;
    public int Contact_ID;
    public String Type;
    public final String Start;
    public final String End;
    public int Customer_ID;
    public int User_ID;



    public Appointment_model(int appointment_ID, String title, String description, String location, int contact_ID,
                             String type, String utc_start, String utc_end, int customer_ID, int user_ID) {


        Appointment_ID = appointment_ID;
        Title = title;
        Description = description;
        Location = location;
        Contact_ID = contact_ID;
        Type = type;
        Start = utc_start;
        End = utc_end;
        Customer_ID = customer_ID;
        User_ID = user_ID;
        System.setProperty("user.timezone", "");
        TimeZone.setDefault(null);
        local_Start = UtFuns.fromTimeStamp_toZonedDateTime(Timestamp.valueOf(Start), "UTC", ZoneId.systemDefault().toString());
        local_End  = UtFuns.fromTimeStamp_toZonedDateTime(Timestamp.valueOf(End), "UTC", ZoneId.systemDefault().toString());

        System.out.println("the input of appointment_ID ==>" + appointment_ID);
        System.out.println("the input of utc_start ==>" + utc_start);
        System.out.println("the value of var_utcStart ==>" + Start);
    }



    public ZonedDateTime getLocal_Start() {
        return local_Start;
    }

    public ZonedDateTime getLocal_End() {
        return local_End;
    }


    public int getAppointment_ID() {
        return Appointment_ID;
    }

    public void setAppointment_ID(int appointment_ID) {
        Appointment_ID = appointment_ID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public int getContact_ID() {
        return Contact_ID;
    }

    public void setContact_ID(int contact_ID) {
        Contact_ID = contact_ID;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getStart() {
        return Start;
    }

    //public void setStart(Timestamp start) {
    //    Start = start;
    //}

    public String getEnd() {
        return End;
    }

    //public void setEnd(Timestamp end) {
       // End = end;
    //}

    public int getCustomer_ID() {
        return Customer_ID;
    }

    public void setCustomer_ID(int customer_ID) {
        Customer_ID = customer_ID;
    }

    public int getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(int user_ID) {
        User_ID = user_ID;
    }
}
