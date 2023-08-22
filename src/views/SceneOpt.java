package views;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneOpt {
    /**
     * Switches the current scene to another one.
     * @param TheClass is this.getClass().
     * @param event is a ActionEvent event.
     * @param viewFilePath is the path string of a view file.
     * @throws IOException IO output
     */
    public static void switchScene(Class<?> TheClass, ActionEvent event, String viewFilePath) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(TheClass.getResource(viewFilePath));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // (Stage) login_button.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void showErrMssg(String mssgTitle, String errMsg){
        Alert anAlert = new Alert(Alert.AlertType.ERROR);
        anAlert.setTitle(mssgTitle);
        anAlert.setContentText(errMsg);         // Optional<ButtonType> alertResult = noUsrAlert.showAndWait();
        anAlert.showAndWait();
    }

    public static void showConfirmMssg(String Title, String msg){
        Alert anAlert = new Alert(Alert.AlertType.CONFIRMATION);
        anAlert.setTitle(Title);
        anAlert.setContentText(msg);         // Optional<ButtonType> alertResult = noUsrAlert.showAndWait();
        anAlert.showAndWait();
    }
}
