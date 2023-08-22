package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import db.DBConnection;
import models.AddEditCustomer_model;
import models.User_model;
import utility.UtFuns;
import views.SceneOpt;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;


public class CustomerAddEdit_controller implements Initializable {

    @FXML
    private ComboBox<String> fDivision_comboBox;

    @FXML
    private ComboBox<String> country_comboBox;

    @FXML
    private Label Customer_ID_label;

    @FXML
    private TextField Customer_Name_textField;

    @FXML
    private TextField Address_textField;

    @FXML
    private TextField Postal_Code_textField;

    @FXML
    private TextField Phone_textField;

    @FXML
    private Button customer_addEdit_button;


    /**
     * A listener to update_first Division combo box.
     */
    public void update_fDivision_combobox(){
        fDivision_comboBox.getItems().clear();
        switch (country_comboBox.getValue()){
            case "U.S":
                //iterate the us division map
                for (Map.Entry<String, Integer> entry : DBConnection.US_Divi_ID_map.entrySet()){
                    fDivision_comboBox.getItems().add(entry.getKey());
                }
                break;
            case "UK":
                //iterate the us division map
                for (Map.Entry<String, Integer> entry : DBConnection.UK_Divi_ID_map.entrySet()){
                    fDivision_comboBox.getItems().add(entry.getKey());
                }
                break;
            case "Canada":
                //iterate the us division map
                for (Map.Entry<String, Integer> entry : DBConnection.Canada_Divi_ID_map.entrySet()){
                    fDivision_comboBox.getItems().add(entry.getKey());
                }
                break;
        }
    }

    /**
     * Set up table, UI, date,and etc.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // add items to country comboBox
        country_comboBox.setPromptText("Select the country");
        country_comboBox.getItems().addAll("U.S", "UK", "Canada");
        DBConnection.set_divisionDataMap();
        // lambda expression used for an organized structure of the parameter on setting the event listener on the country combo box value change
        country_comboBox.setOnAction(e -> {
            update_fDivision_combobox();
        });
        // set up for customer add and edit methods
        switch (AddEditCustomer_model._operationMethod){
            case "customerAdd_button":
                customer_addEdit_button.setText("Add");
                break;
            case "customerEdit_button":
                customer_addEdit_button.setText("Edit");
                // fill out the fields with selected data
                Customer_ID_label.setText(Integer.toString(AddEditCustomer_model.Customer_ID));
                Customer_Name_textField.setText(AddEditCustomer_model.Customer_Name);
                Address_textField.setText(AddEditCustomer_model.Address);
                Postal_Code_textField.setText(AddEditCustomer_model.Postal_Code);
                Phone_textField.setText(AddEditCustomer_model.Phone);
                //use division id to find country id and state name
                int theCountry_id = Integer.parseInt(DBConnection.find_countryId_with_divisionID(AddEditCustomer_model.Division_ID)[1]);
                String theDivisionNme = DBConnection.find_countryId_with_divisionID(AddEditCustomer_model.Division_ID)[0];
                // use country id to select country combox value
                switch (theCountry_id) {
                    case 1 -> country_comboBox.setValue("U.S");
                    case 2 -> country_comboBox.setValue("UK");
                    case 3 -> country_comboBox.setValue("Canada");
                }
                //use state name to select state combox value
                update_fDivision_combobox();
                fDivision_comboBox.setValue(theDivisionNme);
                break;
        }


    }

    /**
     * switchScene_toCustomer
     * @param event is used for locating current scene level.
     */
    public void switchScene_toCustomer(ActionEvent event) throws IOException {
        SceneOpt.switchScene(this.getClass(), event, "/views/customerView.fxml");
    }

    /**
     * Prepare the input data to insert or edit a record to the database.
     * @param event is used for locating current scene level.
     */
    public void addEdit_customer(ActionEvent event) throws SQLException, IOException {
        // update AddEditCustomer_model's properties with the inputs
        AddEditCustomer_model.Customer_Name = Customer_Name_textField.getText();
        AddEditCustomer_model.Address = Address_textField.getText();
        AddEditCustomer_model.Postal_Code = Postal_Code_textField.getText();
        AddEditCustomer_model.Division_ID = DBConnection.lookFor_divisionID(country_comboBox.getValue(), fDivision_comboBox.getValue());
        AddEditCustomer_model.Phone = Phone_textField.getText();

        // DB:
        switch (customer_addEdit_button.getText()){
            case "Add":
                AddEditCustomer_model.Create_Date = UtFuns.fromLocalNowDateTime_toUTCTimestampDateTime();
                AddEditCustomer_model.Created_By = User_model.userName;
                AddEditCustomer_model.Last_Update = UtFuns.fromLocalNowDateTime_toUTCTimestampDateTime();
                AddEditCustomer_model.Last_Updated_By = User_model.userName;
                // call db.insert_customer with the data
                DBConnection.insert_update_customer(
                        "insert",
                        AddEditCustomer_model.Customer_Name,
                        AddEditCustomer_model.Address,
                        AddEditCustomer_model.Postal_Code,
                        AddEditCustomer_model.Phone,
                        AddEditCustomer_model.Create_Date,
                        AddEditCustomer_model.Created_By,
                        AddEditCustomer_model.Last_Update,
                        AddEditCustomer_model.Last_Updated_By,
                        AddEditCustomer_model.Division_ID, 0
                );
                SceneOpt.showConfirmMssg("Registered.", "The new customer has been registered.");
                break;
            case "Edit":
                AddEditCustomer_model.Last_Update = UtFuns.fromLocalNowDateTime_toUTCTimestampDateTime();
                AddEditCustomer_model.Last_Updated_By = User_model.userName;
                // If the inputs are different from AddEditCustomer_model's properties, update it to the property
                DBConnection.insert_update_customer(
                        "update",
                        AddEditCustomer_model.Customer_Name,
                        AddEditCustomer_model.Address,
                        AddEditCustomer_model.Postal_Code,
                        AddEditCustomer_model.Phone,
                        null,
                        null,
                        AddEditCustomer_model.Last_Update,
                        AddEditCustomer_model.Last_Updated_By,
                        AddEditCustomer_model.Division_ID, Integer.parseInt(Customer_ID_label.getText())
                );
                SceneOpt.showConfirmMssg("Updated.", "The customer has been updated.");
                break;
        }
        // Scene back to customer
        SceneOpt.switchScene(this.getClass(), event, "/views/customerView.fxml");
    }

}


