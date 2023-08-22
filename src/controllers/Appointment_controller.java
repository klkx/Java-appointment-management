package controllers;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import models.AddEditAppointment_model;
import models.AddEditCustomer_model;
import models.Appointment_model;
import utility.UtFuns;
import views.SceneOpt;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;

public class Appointment_controller implements Initializable {

    @FXML
    private TableView<Appointment_model> appt_table;

    @FXML
    private ComboBox<String> report_type_combox;

    @FXML
    private ComboBox<String> report_month_combox;

    @FXML
    private ComboBox<String> contact_combox;

    @FXML
    private ComboBox<String> customer_combox;

    private final List<String> months_list = Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");

    private ArrayList<String[]> contact_IdName_list = DBConnection.select_allRecords_byTableName("contacts");

    private ArrayList<String[]> customer_IdName_list = DBConnection.select_allRecords_byTableName("customers");


    /**
     * Switch the scene to home.
     * @param event is used for locating current scene level.
     */
    public void switchScene_toHomeScene(ActionEvent event) throws IOException {
        SceneOpt.switchScene(this.getClass(), event, "/views/homeView.fxml");
    }

    /**
     * SwitchScene_toAppointmentScene by listing all appointments
     * @param event is used for locating current scene level.
     */
    public void switchScene_toAppointmentScene_viewAllAppts(ActionEvent event) throws IOException {
        // view all appointments
        Appointment_model._appointmentView = "viewAll";
        SceneOpt.switchScene(this.getClass(), event, "/views/appointmentView.fxml");
    }

    /**
     * switchScene_toAppointmentScene by listing the current week appointments
     * @param event is used for locating current scene level.
     */
    public void switchScene_toAppointmentScene_viewCurrentWeekAppts(ActionEvent event) throws IOException {
        // view all appointments
        Appointment_model._appointmentView = "viewCurrentWeek";
        SceneOpt.switchScene(this.getClass(), event, "/views/appointmentView.fxml");
    }

    /**
     * switchScene_toAppointmentScene_viewCurrentMonthAppts by listing the current month appointments
     * @param event is used for locating current scene level.
     */
    public void switchScene_toAppointmentScene_viewCurrentMonthAppts(ActionEvent event) throws IOException {
        // view all appointments
        Appointment_model._appointmentView = "viewCurrentMonth";
        SceneOpt.switchScene(this.getClass(), event, "/views/appointmentView.fxml");
    }

    /**
     * switchScene_toAppointmentScene_viewCurrentMonthAppts by listing the appointments of a contact
     * @param event is used for locating current scene level.
     */
    public void switchScene_toAppointmentScene_viewByContact(ActionEvent event) throws IOException {
        // view the appointments by a certain contact
        models.Appointment_model._current_contact_combox_optVal = contact_combox.getValue();
        Appointment_model._appointmentView = "viewByContact";
        SceneOpt.switchScene(this.getClass(), event, "/views/appointmentView.fxml");
    }

    /**
     * switchScene_toAppointmentScene_viewCurrentMonthAppts by listing the appointments of a customer
     * @param event is used for locating current scene level.
     */
    public void switchScene_toAppointmentScene_viewByCustomer(ActionEvent event) throws IOException {
        // view the appointments by a certain customer
        models.Appointment_model._current_customer_combox_optVal = customer_combox.getValue();
        Appointment_model._appointmentView = "viewByCustomer";
        SceneOpt.switchScene(this.getClass(), event, "/views/appointmentView.fxml");
    }

    /**
     * switchScene_to the scene of making and editing appointment
     * @param event is used for locating current scene level.
     */
    public void switchScene_toApptAddEdit(ActionEvent event) throws IOException {
        // get button's id value
        final Node source = (Node) event.getSource();
        AddEditAppointment_model._operationMethod = source.getId();

        switch (source.getId()){
            case "apptAdd_button":
                SceneOpt.switchScene(this.getClass(), event, "/views/appointmentAddEditView.fxml");
                break;
            case "apptEdit_button":
                if(appt_table.getSelectionModel().getSelectedItems().size() >0){
                    SceneOpt.switchScene(this.getClass(), event, "/views/appointmentAddEditView.fxml");
                }else {
                    SceneOpt.showErrMssg("Error", "Please select an item.");
                }
        }
    }

    /**
     * toDeleteAppointment
     * @param event is used for locating current scene level.
     */
    public void toDeleteAppointment(ActionEvent event) throws IOException, SQLException {
        if(appt_table.getSelectionModel().getSelectedItems().size() >0){
            Alert anAlert = new Alert(Alert.AlertType.CONFIRMATION);
            anAlert.setTitle("Appointment Deletion Confirmation");
            anAlert.setContentText(String.format("Are you sure to delete the appointment, ID => %d, type => %s ?", AddEditAppointment_model.Appointment_ID, AddEditAppointment_model.Type));
            Optional<ButtonType> result = anAlert.showAndWait();
            ButtonType alert_button = result.orElse(ButtonType.CANCEL);
            if(alert_button == ButtonType.OK){
                DBConnection.delete_appointment(AddEditAppointment_model.Appointment_ID);
                SceneOpt.switchScene(this.getClass(), event, "/views/appointmentView.fxml");
            }else{
                System.out.println("The appointment deletion was cancelled.");
            }

        }else {
            SceneOpt.showErrMssg("Error", "Please select an appointment to delete.");
        }
    }

    /**
     * showReport_by Type and Month
     * @param event is used for locating current scene level.
     */
    public void showReport_byTypeMonth(ActionEvent event) throws IOException, SQLException {
        // get the data of month and type
        String str_theMonth = report_month_combox.getValue();
        int theMonth = months_list.indexOf(str_theMonth)+1;
        // setup a localDate
        //dates
        LocalDate ld_fstMonthDay = LocalDate.of(LocalDate.now().getYear(), theMonth, 1);
        LocalDate ld_lstMonthDay = LocalDate.of(ld_fstMonthDay.getYear(), theMonth, ld_fstMonthDay.lengthOfMonth());
        String dateFrom  = ld_fstMonthDay.toString();
        String dateTo  = ld_lstMonthDay.toString();
        String theType = report_type_combox.getValue();
        //get the data from the DB
        String theSql = String.format("select Type, Count(*) AS count from appointments where type = '%s' AND (Start BETWEEN '%s 00:00:00' AND '%s 23:59:59') group by 'Type'",
                theType, dateFrom, dateTo);

        //show the alert
        String theReport  = "";
        System.out.println("sql_report by type, month ==> "+ theSql);
        ResultSet appointments_rSet = DBConnection.get_selectData_resultSet(theSql);
        try{
            int count=0;
            while (appointments_rSet.next()){
                theReport = String.format("There is a total of %d for appointment type %s and the month %s. ", appointments_rSet.getInt("count"), appointments_rSet.getString("Type"), report_month_combox.getValue());
                count++;
            }
            if(count ==0){
                theReport = "There is no such record for the requirements.";
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        Alert anAlert = new Alert(Alert.AlertType.CONFIRMATION);
        anAlert.setTitle("The report of #appointments by type and month");
        anAlert.setContentText(theReport);
        anAlert.showAndWait();
    }

    /**
     * Set up table, UI, date,and etc.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Show all customers in the tableView:
        TableColumn<Appointment_model, Integer> Appt_IDCol = new TableColumn<Appointment_model, Integer>("Appointment ID");
        Appt_IDCol.setCellValueFactory(new PropertyValueFactory<Appointment_model, Integer>("Appointment_ID"));

        TableColumn<Appointment_model, String> TitleCol = new TableColumn<Appointment_model, String>("Title");
        TitleCol.setCellValueFactory(new PropertyValueFactory<Appointment_model, String>("Title"));

        TableColumn<Appointment_model, String> descriptionCol = new TableColumn<Appointment_model, String>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<Appointment_model, String>("Description"));

        TableColumn<Appointment_model, String> locationCol = new TableColumn<Appointment_model, String>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<Appointment_model, String>("Location"));

        TableColumn<Appointment_model, Integer> Contact_IDtCol = new TableColumn<Appointment_model, Integer>("Contact ID");
        Contact_IDtCol.setCellValueFactory(new PropertyValueFactory<Appointment_model, Integer>("Contact_ID"));

        TableColumn<Appointment_model, String> typeCol = new TableColumn<Appointment_model, String>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<Appointment_model, String>("Type"));

        TableColumn<Appointment_model, ZonedDateTime> local_startCol = new TableColumn<Appointment_model, ZonedDateTime>("Local Start");
        local_startCol.setCellValueFactory(new PropertyValueFactory<Appointment_model, ZonedDateTime>("local_Start"));

        TableColumn<Appointment_model, ZonedDateTime> local_endCol = new TableColumn<Appointment_model, ZonedDateTime>("Local End");
        local_endCol.setCellValueFactory(new PropertyValueFactory<Appointment_model, ZonedDateTime>("local_End"));

        TableColumn<Appointment_model, String> startCol = new TableColumn<Appointment_model, String>("UTC Start");
        startCol.setCellValueFactory(new PropertyValueFactory<Appointment_model, String>("Start"));

        TableColumn<Appointment_model, String> endCol = new TableColumn<Appointment_model, String>("UTC End");
        endCol.setCellValueFactory(new PropertyValueFactory<Appointment_model, String>("End"));

        TableColumn<Appointment_model, Integer> customer_IDCol = new TableColumn<Appointment_model, Integer>("Customer ID");
        customer_IDCol.setCellValueFactory(new PropertyValueFactory<Appointment_model, Integer>("Customer_ID"));

        TableColumn<Appointment_model, Integer> user_IDCol = new TableColumn<Appointment_model, Integer>("User ID");
        user_IDCol.setCellValueFactory(new PropertyValueFactory<Appointment_model, Integer>("User_ID"));

        //add columns:
        appt_table.getColumns().add(Appt_IDCol);
        appt_table.getColumns().add(TitleCol);
        appt_table.getColumns().add(descriptionCol);
        appt_table.getColumns().add(locationCol);
        appt_table.getColumns().add(Contact_IDtCol);
        appt_table.getColumns().add(typeCol);
        appt_table.getColumns().add(local_startCol);
        appt_table.getColumns().add(local_endCol);
        appt_table.getColumns().add(startCol);
        appt_table.getColumns().add(endCol);
        appt_table.getColumns().add(customer_IDCol);
        appt_table.getColumns().add(user_IDCol);
        appt_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //load the data to apptTableView
        String sqlSelect_appointments = "";
        // lambda expression used for an organized structure.
        switch (Appointment_model._appointmentView) {
            case "viewAll" ->
                    sqlSelect_appointments = "select * from appointments";
            case "viewCurrentWeek" ->{
                    String sql_fstWeekDate = UtFuns.currentWeekDay_UTC(1); //UTC
                    String sql_lstWeekDate = UtFuns.currentWeekDay_UTC(7); //UTC
                    sqlSelect_appointments = String.format("select * from appointments WHERE (Start BETWEEN '%s 00:00:00' AND '%s 23:59:59')", sql_fstWeekDate, sql_lstWeekDate);}
            case "viewCurrentMonth" ->{
                //String sql_fstMonthDate = UtFuns.fromLocalDateTime_toZonedDateTime(LocalDateTime.now().withDayOfMonth(1), "UTC").toString().split("T")[0];
                //String sql_lstMonthDate = UtFuns.fromLocalDateTime_toZonedDateTime(LocalDateTime.now().withDayOfMonth(LocalDate.now().lengthOfMonth()), "UTC").toString().split("T")[0];
                System.setProperty("user.timezone", "");
                TimeZone.setDefault(null);
                ZonedDateTime zoned_Month1stDay = LocalDateTime.now().withDayOfMonth(1).atZone(ZoneId.systemDefault());
                String sql_fstMonthDate = UtFuns.ZonedDateTime_changeZone(zoned_Month1stDay, "UTC").toString().split("T")[0];

                ZonedDateTime zoned_MonthLstDay = LocalDateTime.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atZone(ZoneId.systemDefault());
                String sql_lstMonthDate = UtFuns.ZonedDateTime_changeZone(zoned_MonthLstDay, "UTC").toString().split("T")[0];

                sqlSelect_appointments = String.format("select * from appointments WHERE (Start BETWEEN '%s 00:00:00' AND '%s 23:59:59')", sql_fstMonthDate, sql_lstMonthDate);
            }
            case "viewByContact" ->{
                contact_combox.setValue(models.Appointment_model._current_contact_combox_optVal);
                int Contact_ID = UtFuns.findTheID_from_arrayList(contact_IdName_list, models.Appointment_model._current_contact_combox_optVal);
                sqlSelect_appointments = String.format("select * from appointments WHERE Contact_ID = %s", Contact_ID);
            }
            case "viewByCustomer" ->{
                customer_combox.setValue(models.Appointment_model._current_customer_combox_optVal);
                int Customer_ID = UtFuns.findTheID_from_arrayList(customer_IdName_list, models.Appointment_model._current_customer_combox_optVal);
                sqlSelect_appointments = String.format("select * from appointments WHERE Customer_ID = %s", Customer_ID);
            }
        }
        System.out.println("sqlSelect_appointments ==> "+ sqlSelect_appointments);
        ResultSet appointments_rSet = DBConnection.get_selectData_resultSet(sqlSelect_appointments);
        try{
            while (appointments_rSet.next()){
                System.out.println("appt_id ==> " + appointments_rSet.getInt("appointment_ID"));
                System.out.println("sql_start ==> " + appointments_rSet.getTimestamp("start"));
                Appointment_model a_Appointment_model = new Appointment_model(
                        appointments_rSet.getInt("appointment_ID"),
                        appointments_rSet.getString("title"),
                        appointments_rSet.getString("description"),
                        appointments_rSet.getString("location"),
                        appointments_rSet.getInt("contact_ID"),
                        appointments_rSet.getString("type"),
                        appointments_rSet.getString("start"),
                        appointments_rSet.getString("end"),
                        appointments_rSet.getInt("customer_ID"),
                        appointments_rSet.getInt("user_ID")
                );
                appt_table.getItems().add(a_Appointment_model);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        //setup row click event
        appt_table.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                AddEditAppointment_model.Appointment_ID = appt_table.getSelectionModel().getSelectedItem().getAppointment_ID();
                AddEditAppointment_model.Title = appt_table.getSelectionModel().getSelectedItem().getTitle();
                AddEditAppointment_model.Description = appt_table.getSelectionModel().getSelectedItem().getDescription();
                AddEditAppointment_model.Location = appt_table.getSelectionModel().getSelectedItem().getLocation();
                AddEditAppointment_model.Contact_ID = appt_table.getSelectionModel().getSelectedItem().getContact_ID();
                AddEditAppointment_model.Type = appt_table.getSelectionModel().getSelectedItem().getType();
                AddEditAppointment_model.Start = Timestamp.valueOf(appt_table.getSelectionModel().getSelectedItem().getStart());
                AddEditAppointment_model.End = Timestamp.valueOf(appt_table.getSelectionModel().getSelectedItem().getEnd());
                AddEditAppointment_model.Customer_ID = appt_table.getSelectionModel().getSelectedItem().getCustomer_ID();
                AddEditAppointment_model.User_ID = appt_table.getSelectionModel().getSelectedItem().getUser_ID();
            }
        });

        // reports

        for (String[] aContactRecord_array : contact_IdName_list){
            contact_combox.getItems().add(aContactRecord_array[1]);
        }

        for (String[] aCustomerRecord_array : customer_IdName_list){
            customer_combox.getItems().add(aCustomerRecord_array[1]);
        }

        //report_type_combox.getItems().addAll("Planning Session","Appointment Type1", "Appointment Type2");
        ResultSet apptType_rSet = DBConnection.get_selectData_resultSet("SELECT DISTINCT Type FROM appointments");
        try{
            while (apptType_rSet.next()){
                report_type_combox.getItems().add(apptType_rSet.getString("Type"));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }



        report_month_combox.getItems().addAll("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");




    }




}
