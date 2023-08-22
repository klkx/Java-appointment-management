package utility;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;


public class UtFuns {

    /**
     * Returns a ZonedDateTime from a LocalDateTime.
     * @param ldt is a LocalDateTime.
     * @param zoneID is a string of a zone ID.
     */
    public static ZonedDateTime fromLocalDateTime_toZonedDateTime(LocalDateTime ldt, String zoneID){
        System.setProperty("user.timezone", "");
        TimeZone.setDefault(null);
        ZonedDateTime ldtZoned = ldt.atZone(ZoneId.systemDefault());
        return ZonedDateTime_changeZone(ldtZoned, zoneID);
    }
    /**
     * Returns a ZonedDateTime for a different zone.
     * @param aZonedDateTime is a ZonedDateTime.
     * @param zoneID is a string of a zone ID.
     */
    public static ZonedDateTime ZonedDateTime_changeZone(ZonedDateTime aZonedDateTime, String zoneID){
        return aZonedDateTime.withZoneSameInstant(ZoneId.of(zoneID));
    }

    /**
     * Returns a Timestamp from a ZonedDateTime.
     * @param aZonedDateTime is a ZonedDateTime.
     */
    public static Timestamp fromZonedDateTime_toTimestamp(ZonedDateTime aZonedDateTime){
        return Timestamp.valueOf(aZonedDateTime.toLocalDateTime());
    }
    /**
     * Returns a ZonedDateTime from Timestamp. LocalDateTime will be adjusted based on the zoneID.
     * @param aTimestamp is a Timestamp.
     * @param ZoneID_from is the string of the current timezone of the orginal date.
     * @param ZoneID_to is the string of the timezone you want to change to.
     */
    public static ZonedDateTime fromTimeStamp_toZonedDateTime(Timestamp aTimestamp, String ZoneID_from, String ZoneID_to){  // "America/New_York"

        LocalDateTime ldt = aTimestamp.toLocalDateTime(); // LocalDateTime ldt = LocalDateTime.parse(dateInString, DateTimeFormatter.ofPattern(DATE_FORMAT));
        // ldt + atZone  = ZonedDateTime
        ZonedDateTime zonedDateTime_from = ldt.atZone(ZoneId.of(ZoneID_from));
        // from ZonedDateTime to another ZonedDateTime
        return zonedDateTime_from.withZoneSameInstant(ZoneId.of(ZoneID_to));
    }

    /**
     * Returns a string in a format from ZonedDateTime.
     * @param aZonedDateTime is a a ZonedDateTime.
     */
    public static String zonedDateTime_string(ZonedDateTime aZonedDateTime){ // return a string of zonedDateTime in a format
        String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter format = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return format.format(aZonedDateTime);
    }

    /**
     * Returns a UTC Timestamp from the current LocalNowDateTime.
     */
    public static Timestamp fromLocalNowDateTime_toUTCTimestampDateTime(){
        ZonedDateTime UTCDateTime = fromLocalDateTime_toZonedDateTime(LocalDateTime.now(), "UTC");
        return fromZonedDateTime_toTimestamp(UTCDateTime);
    }

    /**
     * Returns a date string of a week date.
     * @param whichWeekDay is an integer to indicate which weekday you want.
     */
    public static String currentWeekDay_UTC(int whichWeekDay){ //return a date string based on the weekday-th number of the current week
        TemporalField fieldUS = WeekFields.of(Locale.US).dayOfWeek(); //set week format as USA
        LocalDate weekDay =  LocalDate.now().with(fieldUS, whichWeekDay); // pick the first day of the week
        // make the weekday to a UTC datetime
        ZonedDateTime theDate = UtFuns.fromLocalDateTime_toZonedDateTime(LocalDateTime.of(weekDay, LocalTime.of(0, 0, 0)), "UTC");
        return theDate.toString().split("T")[0]; // take date string only
    }

    /**
     * Returns a boolean to indicate whether the appointment start dateTime is between EST 8AM and 10PM.
     * @param startLocalDate is the LocalDate of the appointment's start date.
     * @param start_ldt is the LocalDateTime of the appointment's start LocalDateTime.
     * @param end_ldt is the LocalDateTime of the appointment's end LocalDateTime.
     */
    public static boolean isBetween_EST8AMto10PM(LocalDate startLocalDate, LocalDateTime start_ldt, LocalDateTime end_ldt){
        boolean theResult = false;
        // EST 8am and 10pm
        LocalTime lt_8am = LocalTime.of(8, 0, 0);
        LocalDateTime ldt_8am = LocalDateTime.of(startLocalDate, lt_8am);
        ZonedDateTime EST_8am_dt = ldt_8am.atZone(ZoneId.of("US/Eastern"));

        LocalTime lt_10pm = LocalTime.of(22, 0, 0);
        LocalDateTime ldt_10pm = LocalDateTime.of(startLocalDate, lt_10pm);
        ZonedDateTime EST_10pm_dt = ldt_10pm.atZone(ZoneId.of("US/Eastern"));


        //make start and end dateTime into EST
        ZonedDateTime EST_Start = fromLocalDateTime_toZonedDateTime(start_ldt, "US/Eastern");
        ZonedDateTime EST_end = fromLocalDateTime_toZonedDateTime(end_ldt, "US/Eastern");

        //compare
        if (EST_Start.isAfter(EST_8am_dt) && EST_end.isBefore(EST_10pm_dt)) {
            theResult = true;
        }

        return theResult;
    }

    /**
     * Returns a boolean to indicate whether the Timestamp is between a period of a time.
     * @param sql_startDT is the Timestamp of the appointment's start dateTime from the database.
     * @param sql_endDT is the Timestamp of the appointment's end dateTime from the database.
     * @param start_ldt is the LocalDateTime of the appointment's start LocalDateTime.
     * @param end_ldt is the LocalDateTime of the appointment's end LocalDateTime.
     */
    public static boolean isTheDateTime_betweenThem(Timestamp sql_startDT, Timestamp sql_endDT, LocalDateTime start_ldt, LocalDateTime end_ldt){
        boolean theResult = false;
        // make all DT to UTC ZonedDateTime
        ZonedDateTime db_start_ZonedDateTime = sql_startDT.toLocalDateTime().atZone(ZoneId.of("UTC"));
        ZonedDateTime db_end_ZonedDateTime = sql_endDT.toLocalDateTime().atZone(ZoneId.of("UTC"));

        ZonedDateTime start_ZonedDateTime = fromLocalDateTime_toZonedDateTime(start_ldt, "UTC");
        ZonedDateTime end_ZonedDateTime = fromLocalDateTime_toZonedDateTime(end_ldt, "UTC");


        //local start and end DT is not between the db start and end DT

        if (start_ZonedDateTime.equals(db_end_ZonedDateTime) || end_ZonedDateTime.equals(db_start_ZonedDateTime)) { // start dt is between the record's time frame
            System.out.println("start or end dt is on another record's start or end time =>" + start_ZonedDateTime);
            return true;
        }
        if (start_ZonedDateTime.equals(db_start_ZonedDateTime) || end_ZonedDateTime.equals(db_end_ZonedDateTime)) { // start dt is between the record's time frame
            System.out.println("start or end dt is on another record's start or end time =>" + start_ZonedDateTime);
            return true;
        }
        if (start_ZonedDateTime.isAfter(db_start_ZonedDateTime) && start_ZonedDateTime.isBefore(db_end_ZonedDateTime)) { // start dt is between the record's time frame
            System.out.println("start dt is between the record's time frame =>" + start_ZonedDateTime);
            return true;
        }
        if (end_ZonedDateTime.isAfter(db_start_ZonedDateTime) && end_ZonedDateTime.isBefore(db_end_ZonedDateTime)) { // end dt is between the record's time frame
            System.out.println("end dt is between the record's time frame =>" + end_ZonedDateTime);
            return true;
        }

        return theResult;
    }

        /**
         * Returns an int ID by the name within an arrayList.
         * @param ID_name_arrayList is an arrayList of a string array that contains ID and name.
         * @param theName_for_theID is string of the name associated with the ID you will be looking for.
         */
    public static int findTheID_from_arrayList(ArrayList<String[]> ID_name_arrayList, String theName_for_theID){
        for(String[] IDnme_itm : ID_name_arrayList){  // IDnme_itm[0] => ID; IDnme_itm[1] => name
            if(Objects.equals(IDnme_itm[1], theName_for_theID)){
                return Integer.parseInt(IDnme_itm[0]);
            }
        }
        return 0;
    }

    /**
     * Returns the name by the id within an arrayList.
     * @param ID_name_arrayList is an arrayList of a string array that contains ID and name.
     * @param theID_for_theName is string of the name associated with the ID you will be looking for.
     */
    public static String findTheName_from_arrayList(ArrayList<String[]> ID_name_arrayList, String theID_for_theName){
        for(String[] IDnme_itm : ID_name_arrayList){  // IDnme_itm[0] => ID; IDnme_itm[1] => name
            if(Objects.equals(IDnme_itm[0], theID_for_theName)){
                return IDnme_itm[1];
            }
        }
        return "";
    }

    /**
     * Wirte a string to Login_activity.txt.
     * @param content is the string as an input to the file.
     */
    public static void write_theLoginFile(String content) throws IOException {
        Path filePath = Paths.get(System.getProperty("user.dir"), "Login_activity.txt");
        System.out.println("file path ==>" + filePath);
        Files.writeString(filePath, content+"\n", StandardOpenOption.APPEND);
    }

    /**
     * Returen a string from Login_activity.txt.
     */
    public static String read_theLoginFile() throws IOException {
        Path filePath = Paths.get(System.getProperty("user.dir"), "Login_activity.txt");
        return Files.readString(filePath);
    }

}
