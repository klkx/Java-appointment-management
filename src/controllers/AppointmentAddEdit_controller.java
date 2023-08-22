package controllers;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.AddEditAppointment_model;
import models.AddEditCustomer_model;
import models.Appointment_model;
import models.User_model;
import utility.UtFuns;
import views.SceneOpt;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;


public class AppointmentAddEdit_controller implements Initializable {

    @FXML
    private ComboBox<String> Contact_comboBox;

    @FXML
    private ComboBox<String> Customer_comboBox;

    @FXML
    private ComboBox<String> User_comboBox;

    @FXML
    private ComboBox<String> AppType_comboBox;



    @FXML
    private Label Appointment_ID_label;

    @FXML
    private TextField AppTitle_textField;

    @FXML
    private TextField AppDes_textField;

    @FXML
    private TextField AppLocation_textField;

    @FXML
    private DatePicker AppStartDate;

    @FXML
    private TextField AppStartTime_hour;

    @FXML
    private TextField AppStartTime_minute;

    @FXML
    private DatePicker AppEndDate;

    @FXML
    private TextField AppEndTime_hour;

    @FXML
    private TextField AppEndTime_minute;


    @FXML
    private Button appt_addEdit_button;

    public ArrayList<String[]> contact_IdName_list ;
    public ArrayList<String[]> customer_IdName_list;
    public ArrayList<String[]> user_IdName_list;






    /**
     * Set up table, UI, date,and etc.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // add items to contact, customer, users, type comboBoxes
        contact_IdName_list = DBConnection.select_allRecords_byTableName("contacts");
        customer_IdName_list = DBConnection.select_allRecords_byTableName("customers");
        user_IdName_list = DBConnection.select_allRecords_byTableName("users");
        for (String[] aContactRecord_array : contact_IdName_list){
            Contact_comboBox.getItems().add(aContactRecord_array[1]);
        }
        for (String[] aCustomerRecord_array : customer_IdName_list){
            Customer_comboBox.getItems().add(aCustomerRecord_array[1]);
        }
        for (String[] anUser_IdName_array : user_IdName_list){
            User_comboBox.getItems().add(anUser_IdName_array[1]);
        }
        AppType_comboBox.getItems().addAll("Planning Session","Appointment Type1", "Appointment Type2");

        //set up for customer add and edit methods
        switch (AddEditAppointment_model._operationMethod) {
            case "apptAdd_button" -> appt_addEdit_button.setText("Add");
            case "apptEdit_button" -> {
                appt_addEdit_button.setText("Edit");
                // fill out the fields with selected data
                Appointment_ID_label.setText(Integer.toString(AddEditAppointment_model.Appointment_ID));
                AppTitle_textField.setText(AddEditAppointment_model.Title);
                AppDes_textField.setText(AddEditAppointment_model.Description);
                AppLocation_textField.setText(AddEditAppointment_model.Location);
                //fill comboxes
                Contact_comboBox.setValue(UtFuns.findTheName_from_arrayList(contact_IdName_list, Integer.toString(AddEditAppointment_model.Contact_ID)));
                AppType_comboBox.setValue(AddEditAppointment_model.Type);
                Customer_comboBox.setValue(UtFuns.findTheName_from_arrayList(customer_IdName_list, Integer.toString(AddEditAppointment_model.Customer_ID)));
                User_comboBox.setValue(UtFuns.findTheName_from_arrayList(user_IdName_list, Integer.toString(AddEditAppointment_model.User_ID)));
                
                // fill dates and times
                ZonedDateTime Start_zoneDateTime = UtFuns.fromTimeStamp_toZonedDateTime(AddEditAppointment_model.Start, "UTC", ZoneId.systemDefault().toString());
                ZonedDateTime End_zoneDateTime = UtFuns.fromTimeStamp_toZonedDateTime(AddEditAppointment_model.End, "UTC", ZoneId.systemDefault().toString());

                AppStartDate.setValue(Start_zoneDateTime.toLocalDate());
                //AppEndDate.setValue(End_zoneDateTime.toLocalDate());
                AppStartTime_hour.setText(Integer.toString(Start_zoneDateTime.getHour()));
                AppStartTime_minute.setText(Integer.toString(Start_zoneDateTime.getMinute()));
                AppEndTime_hour.setText(Integer.toString(End_zoneDateTime.getHour()));
                AppEndTime_minute.setText(Integer.toString(End_zoneDateTime.getMinute()));
            }
        }


    }

    /**
     * switchScene_toAppointment scene
     * @param event is used for locating current scene level.
     */
    public void switchScene_toAppointment(ActionEvent event) throws IOException {
        SceneOpt.switchScene(this.getClass(), event, "/views/appointmentView.fxml");
    }

    /**
     * Prepare the input data to insert or edit a record to the database.
     * @param event is used for locating current scene level.
     */
    public void addEdit_appointment(ActionEvent event) throws SQLException, IOException {
        AddEditAppointment_model.Title = AppTitle_textField.getText();
        AddEditAppointment_model.Description = AppDes_textField.getText();
        AddEditAppointment_model.Location = AppLocation_textField.getText();
        AddEditAppointment_model.Type = AppType_comboBox.getValue();
        //collect datetime info and turn into a proper data type to the model
        LocalTime start_lt = LocalTime.of(Integer.parseInt(AppStartTime_hour.getText()), Integer.parseInt(AppStartTime_minute.getText()), 0);
        LocalDateTime start_ldt = LocalDateTime.of(AppStartDate.getValue(), start_lt);
        AddEditAppointment_model.Start = UtFuns.fromZonedDateTime_toTimestamp(UtFuns.fromLocalDateTime_toZonedDateTime(start_ldt, "UTC"));

        LocalTime end_lt = LocalTime.of(Integer.parseInt(AppEndTime_hour.getText()), Integer.parseInt(AppEndTime_minute.getText()), 0);
        LocalDateTime end_ldt = LocalDateTime.of(AppStartDate.getValue(), end_lt);
        AddEditAppointment_model.End = UtFuns.fromZonedDateTime_toTimestamp(UtFuns.fromLocalDateTime_toZonedDateTime(end_ldt, "UTC"));


        // EST8AM-10PM VALIDATION:
        if(!UtFuns.isBetween_EST8AMto10PM(AppStartDate.getValue(), start_ldt, end_ldt)){
            System.out.println("The time frame is not Between_EST8AMto10PM");
            SceneOpt.showErrMssg("Validation Error", "The time frame is not Between_EST8AMto10PM");
            return;
        }

        // Schedule time overlapping validation:
        ResultSet appointments_rSet = DBConnection.get_selectData_resultSet(String.format("select * from appointments WHERE Appointment_ID NOT IN (SELECT Appointment_ID FROM appointments WHERE Appointment_ID = %s)", Appointment_ID_label.getText()));
        try{
            while (appointments_rSet.next()){
                boolean overlapping  = UtFuns.isTheDateTime_betweenThem(
                        appointments_rSet.getTimestamp("start"),
                        appointments_rSet.getTimestamp("end"),
                        start_ldt, end_ldt);
                if(overlapping){
                    System.out.println("The schedule time frame is overlapping with other appointment, ID " + appointments_rSet.getInt("Appointment_ID"));
                    SceneOpt.showErrMssg("Validation Error", "The schedule time frame is overlapping with another appointment, ID " + appointments_rSet.getInt("Appointment_ID"));
                    return;
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        //Combox lookup id value; try to take combob's id-value data domain to upper level.
        AddEditAppointment_model.Contact_ID = UtFuns.findTheID_from_arrayList(contact_IdName_list, Contact_comboBox.getValue());
        AddEditAppointment_model.Customer_ID = UtFuns.findTheID_from_arrayList(customer_IdName_list,  Customer_comboBox.getValue());
        AddEditAppointment_model.User_ID = UtFuns.findTheID_from_arrayList(user_IdName_list,  User_comboBox.getValue());


        // DB:
        switch (appt_addEdit_button.getText()) {
            case "Add" -> {
                AddEditAppointment_model.Create_Date = UtFuns.fromLocalNowDateTime_toUTCTimestampDateTime();
                AddEditAppointment_model.Created_By = User_model.userName;
                AddEditAppointment_model.Last_Update = UtFuns.fromLocalNowDateTime_toUTCTimestampDateTime();
                AddEditAppointment_model.Last_Updated_By = User_model.userName;

                // call db.insert_customer with the data
                DBConnection.insert_update_appointment(
                        "insert",
                        0,
                        AddEditAppointment_model.Title,
                        AddEditAppointment_model.Description,
                        AddEditAppointment_model.Location,
                        AddEditAppointment_model.Type,
                        AddEditAppointment_model.Start,
                        AddEditAppointment_model.End,
                        AddEditAppointment_model.Create_Date,
                        AddEditAppointment_model.Created_By,
                        AddEditAppointment_model.Last_Update,
                        AddEditAppointment_model.Last_Updated_By,
                        AddEditAppointment_model.User_ID,
                        AddEditAppointment_model.Contact_ID,
                        AddEditAppointment_model.Customer_ID
                );
                SceneOpt.showConfirmMssg("Registered.", "The new customer has been registered.");
            }
            case "Edit" -> {
                AddEditAppointment_model.Last_Update = UtFuns.fromLocalNowDateTime_toUTCTimestampDateTime();
                AddEditAppointment_model.Last_Updated_By = User_model.userName;
                // If the inputs are different from AddEditCustomer_model's properties, update it to the property
                DBConnection.insert_update_appointment(
                        "update",
                        AddEditAppointment_model.Appointment_ID,
                        AddEditAppointment_model.Title,
                        AddEditAppointment_model.Description,
                        AddEditAppointment_model.Location,
                        AddEditAppointment_model.Type,
                        AddEditAppointment_model.Start,
                        AddEditAppointment_model.End,
                        null,
                        null,
                        AddEditAppointment_model.Last_Update,
                        AddEditAppointment_model.Last_Updated_By,
                        AddEditAppointment_model.User_ID,
                        AddEditAppointment_model.Contact_ID,
                        AddEditAppointment_model.Customer_ID
                );
                SceneOpt.showConfirmMssg("Updated.", "The customer has been updated.");
            }
        }
        // Scene back to customer
        SceneOpt.switchScene(this.getClass(), event, "/views/appointmentView.fxml");
    }


}


