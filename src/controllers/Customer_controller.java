package controllers;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import models.Customer_model;
import models.AddEditCustomer_model;
import views.SceneOpt;
import db.DBConnection;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;


import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.ResourceBundle;

public class Customer_controller implements Initializable {

    @FXML
    private TableView<Customer_model> cus_table;

    /**
     * switchScene_toHomeScene
     * @param event is used for locating current scene level.
     */
    public void switchScene_toHomeScene(ActionEvent event) throws IOException {
        SceneOpt.switchScene(this.getClass(), event, "/views/homeView.fxml");
    }

    /**
     * switchScene_toCustomerAddEdit
     * @param event is used for locating current scene level.
     */
    public void switchScene_toCustomerAddEdit(ActionEvent event) throws IOException {
        // get button's id value
        final Node source = (Node) event.getSource();
        AddEditCustomer_model._operationMethod = source.getId();

        switch (source.getId()){
            case "customerAdd_button":
                SceneOpt.switchScene(this.getClass(), event, "/views/customerAddEditView.fxml");
                break;
            case "customerEdit_button":
                if(cus_table.getSelectionModel().getSelectedItems().size() >0){
                    SceneOpt.switchScene(this.getClass(), event, "/views/customerAddEditView.fxml");
                }else {
                    SceneOpt.showErrMssg("Error", "Please select an item.");
                }
        }
    }

    /**
     * to Delete a Customer
     * @param event is used for locating current scene level.
     */
    public void toDeleteCustomer(ActionEvent event) throws IOException, SQLException {
        if(cus_table.getSelectionModel().getSelectedItems().size() >0){
            Alert anAlert = new Alert(Alert.AlertType.CONFIRMATION);
            anAlert.setTitle("Customer Deletion Confirmation");
            anAlert.setContentText(String.format("Are you sure to delete the customer, ID => %d, name => %s ?", AddEditCustomer_model.Customer_ID, AddEditCustomer_model.Customer_Name));         // Optional<ButtonType> alertResult = noUsrAlert.showAndWait();
            Optional<ButtonType> result = anAlert.showAndWait();
            ButtonType alert_button = result.orElse(ButtonType.CANCEL);
            if(alert_button == ButtonType.OK){
                DBConnection.delete_customer(AddEditCustomer_model.Customer_ID);
                SceneOpt.switchScene(this.getClass(), event, "/views/customerView.fxml");
            }else{
                System.out.println("The customer deletion was cancelled.");
            }

        }else {
            SceneOpt.showErrMssg("Error", "Please select a customer to delete.");
        }
    }

    /**
     * Set up table, UI, date,and etc.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Show all customers in the tableView:
        TableColumn<Customer_model, Integer> Customer_IDCol = new TableColumn<Customer_model, Integer>("Customer ID");
        Customer_IDCol.setCellValueFactory(new PropertyValueFactory<Customer_model, Integer>("Customer_ID"));

        TableColumn<Customer_model, String> cusNmeCol = new TableColumn<Customer_model, String>("Customer Name");
        cusNmeCol.setCellValueFactory(new PropertyValueFactory<Customer_model, String>("Customer_Name"));

        TableColumn<Customer_model, String> addressCol = new TableColumn<Customer_model, String>("Address");
        addressCol.setCellValueFactory(new PropertyValueFactory<Customer_model, String>("Address"));

        TableColumn<Customer_model, String> postalCodeCol = new TableColumn<Customer_model, String>("Postal Code");
        postalCodeCol.setCellValueFactory(new PropertyValueFactory<Customer_model, String>("Postal_Code"));

        TableColumn<Customer_model, String> PhoneCol = new TableColumn<Customer_model, String>("Phone");
        PhoneCol.setCellValueFactory(new PropertyValueFactory<Customer_model, String>("Phone"));

        TableColumn<Customer_model, Timestamp> Create_DateCol = new TableColumn<Customer_model, Timestamp>("Create Date");
        Create_DateCol.setCellValueFactory(new PropertyValueFactory<Customer_model, Timestamp>("Create_Date"));

        TableColumn<Customer_model, String> Created_ByCol = new TableColumn<Customer_model, String>("Created By");
        Created_ByCol.setCellValueFactory(new PropertyValueFactory<Customer_model, String>("Created_By"));

        TableColumn<Customer_model, Timestamp> LastUpdateCol = new TableColumn<Customer_model, Timestamp>("Last Update");
        LastUpdateCol.setCellValueFactory(new PropertyValueFactory<Customer_model, Timestamp>("Last_Update"));

        TableColumn<Customer_model, String> Last_Updated_ByCol = new TableColumn<Customer_model, String>("Last Updated By");
        Last_Updated_ByCol.setCellValueFactory(new PropertyValueFactory<Customer_model, String>("Last_Updated_By"));

        TableColumn<Customer_model, Integer> Division_IDCol = new TableColumn<Customer_model, Integer>("Division ID");
        Division_IDCol.setCellValueFactory(new PropertyValueFactory<Customer_model, Integer>("Division_ID"));

        //add columns:
        cus_table.getColumns().add(Customer_IDCol);
        cus_table.getColumns().add(cusNmeCol);
        cus_table.getColumns().add(addressCol);
        cus_table.getColumns().add(postalCodeCol);
        cus_table.getColumns().add(PhoneCol);
        cus_table.getColumns().add(Create_DateCol);
        cus_table.getColumns().add(Created_ByCol);
        cus_table.getColumns().add(LastUpdateCol);
        cus_table.getColumns().add(Last_Updated_ByCol);
        cus_table.getColumns().add(Division_IDCol);
        cus_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //load the data to cusTable
        ResultSet customers_rSet = DBConnection.get_selectData_resultSet("select * from customers");
        try{
            while (customers_rSet.next()){
                customers_rSet.getString(1);
                cus_table.getItems().add(new Customer_model(
                        customers_rSet.getInt("Customer_ID"),
                        customers_rSet.getString("Customer_Name"),
                        customers_rSet.getString("Address"),
                        customers_rSet.getString("Postal_Code"),
                        customers_rSet.getString("Phone"),
                        customers_rSet.getTimestamp("Create_Date"),
                        customers_rSet.getString("Created_By"),
                        customers_rSet.getTimestamp("Last_Update"),
                        customers_rSet.getString("Last_Updated_By"),
                        customers_rSet.getInt("Division_ID")
                ));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        //setup row click event
        cus_table.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                AddEditCustomer_model.Customer_ID = cus_table.getSelectionModel().getSelectedItem().getCustomer_ID();
                AddEditCustomer_model.Customer_Name = cus_table.getSelectionModel().getSelectedItem().getCustomer_Name();
                AddEditCustomer_model.Address = cus_table.getSelectionModel().getSelectedItem().getAddress();
                AddEditCustomer_model.Postal_Code = cus_table.getSelectionModel().getSelectedItem().getPostal_Code();
                AddEditCustomer_model.Phone = cus_table.getSelectionModel().getSelectedItem().getPhone();
                AddEditCustomer_model.Division_ID = cus_table.getSelectionModel().getSelectedItem().getDivision_ID();
            }
        });
    }




}
