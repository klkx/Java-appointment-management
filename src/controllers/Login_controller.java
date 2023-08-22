package controllers;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.User_model;
import db.DBConnection;
import utility.UtFuns;
import views.SceneOpt;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class Login_controller implements Initializable {
    @FXML
    private Label loginUsr_label;

    @FXML
    private Label loginPass_label;

    @FXML
    private Button login_button;

    @FXML
    private Label label_timezone;

    @FXML
    private Label timezoneDes_label;




    @FXML
    private TextField input_usrnme;

    @FXML
    private TextField input_pass;

    private final Map<String, String[]> text_lang_map = Map.ofEntries(
            new AbstractMap.SimpleEntry<String, String[]>("loginUsr_label", new String[]{"User:", "Utilisateur/Utilisateur:"}),
            new AbstractMap.SimpleEntry<String, String[]>("loginPass_label", new String[]{"Password:", "Mot de passe:"}),
            new AbstractMap.SimpleEntry<String, String[]>("login_button", new String[]{"Login", "Connexion"}),
            new AbstractMap.SimpleEntry<String, String[]>("timezoneDes_label", new String[]{"Time Zone:", "Fuseau horaire:"})
    );


    public void setText_basedOnLan(){
        int which_lang_index = Locale.getDefault().toString().equals("fr_FR")? 1:0;
        loginUsr_label.setText(text_lang_map.get("loginUsr_label")[which_lang_index]);
        loginPass_label.setText(text_lang_map.get("loginPass_label")[which_lang_index]);
        login_button.setText(text_lang_map.get("login_button")[which_lang_index]);
        label_timezone.setText(text_lang_map.get("label_timezone")[which_lang_index]);
    }

    /**
     * Alert incoming appointment in 15 Mins after log in.
     */
    public void alert_appt_in15Mins(){
        // now dateTime
        LocalDateTime the_nowLdt = LocalDateTime.now();
        System.out.println("the_nowLdt for alert_appt_in15Mins==>"+ the_nowLdt);
        // at15mins later datetime
        LocalDateTime the_15ldt = the_nowLdt.plusMinutes(15);;
        // both into UTC timestamp and the string
        ZonedDateTime the_nowDT_zoned = UtFuns.fromLocalDateTime_toZonedDateTime(the_nowLdt, "UTC");
        Timestamp the_nowDT_tsp = UtFuns.fromZonedDateTime_toTimestamp(the_nowDT_zoned);

        ZonedDateTime the_15DT_zoned = UtFuns.fromLocalDateTime_toZonedDateTime(the_15ldt, "UTC");
        Timestamp the_15DT_tsp = UtFuns.fromZonedDateTime_toTimestamp(the_15DT_zoned);

        // setup the sql to get data from the DB
        String the_sql = String.format("select Appointment_ID, Start from appointments where (Start BETWEEN '%s' AND '%s')", the_nowDT_tsp.toString(), the_15DT_tsp.toString());
        System.out.println("sqlSelect_in15m_appointments ==> "+ the_sql);
        ResultSet appointments_rSet = DBConnection.get_selectData_resultSet(the_sql);
        StringBuilder theMssg;
        if(Locale.getDefault().toString().equals("fr_FR")){
            theMssg = new StringBuilder("Les rendez-vous suivants auront lieu dans 15 minutes:");
        }else {
            theMssg = new StringBuilder("The following appointments will happen in 15 minutes: ");
        }

        int count = 0;
        try{
            while (appointments_rSet.next()){
                Timestamp tsp_date = appointments_rSet.getTimestamp("Start");
                ZonedDateTime zoned_ldt = UtFuns.fromTimeStamp_toZonedDateTime(tsp_date, "UTC", ZoneId.systemDefault().toString());

                if(Locale.getDefault().toString().equals("fr_FR")){
                    theMssg.append(String.format("ID de rendez-vous: %d; DateHeure de début: %s \n", appointments_rSet.getInt("appointment_ID"), zoned_ldt.toString()));
                }else {
                    theMssg.append(String.format("Appointment ID: %d; Start DateTime: %s \n", appointments_rSet.getInt("appointment_ID"), zoned_ldt.toString()));
                }
                count++;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        // organize the DB data and show the mssg
        if(count == 0){

            if(Locale.getDefault().toString().equals("fr_FR")){
                SceneOpt.showConfirmMssg("L'alerte Rendez-vous en 15 minutes", "Il n'y a pas de rendez-vous dans 15 minutes");
            }else {
                SceneOpt.showConfirmMssg("The Appointments in 15 Minutes Alert", "There is no appointment in 15 minutes");
            }
        }else {

            if(Locale.getDefault().toString().equals("fr_FR")){
                SceneOpt.showConfirmMssg("L'alerte Rendez-vous en 15 minutes", String.valueOf(theMssg));
            }else {
                SceneOpt.showConfirmMssg("The Appointments in 15 Minutes Alert", String.valueOf(theMssg));
            }
        }
    }

    /**
     * Prepare the input data to insert or edit a record to the database.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.setProperty("user.timezone", "");
        TimeZone.setDefault(null);
        label_timezone.setText(ZoneId.systemDefault().toString());

        int which_lang_index = Locale.getDefault().toString().equals("fr_FR")? 1:0;
        loginUsr_label.setText(text_lang_map.get("loginUsr_label")[which_lang_index]);
        loginPass_label.setText(text_lang_map.get("loginPass_label")[which_lang_index]);
        login_button.setText(text_lang_map.get("login_button")[which_lang_index]);
        timezoneDes_label.setText(text_lang_map.get("timezoneDes_label")[which_lang_index]);
    }


    /**
     * Switches scene to homepage.
     * @param event cuurent event.
     * @throws IOException IO output
     */
    public void switchScene_login(ActionEvent event) throws IOException {
        //Get usr info
        String usrNme = input_usrnme.getText();
        String usrPass = input_pass.getText();
        //ResultSet find_anUsr
        String[] theUsr_array = DBConnection.find_anUsr_array(usrNme, usrPass);
        String login_log_str = "";
        if (theUsr_array != null){ //to login and switch scene
            alert_appt_in15Mins();

            System.out.println("got the usr:"); System.out.println(theUsr_array[0]);
            User_model.update_usrModel("in", theUsr_array);
            SceneOpt.switchScene(this.getClass(), event, "/views/homeView.fxml");

            login_log_str = String.format("Login successfully; user id; %s; user name; %s; dateTime; %s", theUsr_array[0], theUsr_array[1], LocalDateTime.now().toString());
            try {
                UtFuns.write_theLoginFile(login_log_str);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else { // shows the error message of user not existed
            login_log_str = String.format("Login unsuccessfully; user id; ; user name; ; dateTime; %s", LocalDateTime.now().toString());
            try {
                UtFuns.write_theLoginFile(login_log_str);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String errTitle = Locale.getDefault().toString().equals("fr_FR")? "Alerte de connexion utilisateur":"User Login Alert";
            String errMsg = Locale.getDefault().toString().equals("fr_FR")? "L'utilisateur n'est pas encore enregistré ou le mot de passe est incorrect.":"The user is not registered yet or the password is incorrect.";
            SceneOpt.showErrMssg(errTitle, errMsg);
        }
    }
}