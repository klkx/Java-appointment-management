package controllers;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import models.Appointment_model;
import models.User_model;
import utility.UtFuns;
import views.SceneOpt;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class Home_controller  {

    @FXML
    private Button login_report_button;

    /**
     * switchScene_toCustomerScene
     * @param event is used for locating current scene level.
     */
    public void switchScene_toCustomerScene(ActionEvent event) throws IOException {
        SceneOpt.switchScene(this.getClass(), event, "/views/customerView.fxml");
    }

    /**
     * switchScene_toCustomerScene
     * @param event is used for locating current scene level.
     */
    public void switchScene_toAppointmentScene(ActionEvent event) throws IOException {
        // view all appointments
        Appointment_model._appointmentView = "viewAll";
        SceneOpt.switchScene(this.getClass(), event, "/views/appointmentView.fxml");
    }

    /**
     * switchScene_toLoginScene
     * @param event is used for locating current scene level.
     */
    public void switchScene_toLoginScene(ActionEvent event) throws IOException {
        SceneOpt.switchScene(this.getClass(), event, "/views/loginView.fxml");
    }

    /**
     * show_login_report
     */
    public void show_login_report() throws IOException {
        String theMssg = UtFuns.read_theLoginFile();
        StringBuilder details = new StringBuilder();

        int failed_login_count = 0;
        String[] lines = theMssg.split("\n");
        for (String line : lines) {
            String[] oneLine = line.split(";");
            if (oneLine[0].contains("unsuccessfully")) {
                failed_login_count++;
                details.append(line).append("\n");
            }
        }
        theMssg = "You have unsuccessfully logged in for "+failed_login_count + " times." + "\n" + details;
        SceneOpt.showConfirmMssg("Unsuccessfully Login Activity Report", theMssg);
    }


}
