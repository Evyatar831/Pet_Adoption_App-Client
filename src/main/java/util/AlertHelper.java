package util;


import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

import java.util.Optional;


public class AlertHelper {


    public static void showInformation(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }


    public static void showWarning(String title, String message) {
        showAlert(Alert.AlertType.WARNING, title, message);
    }


    public static void showError(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }


    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }


    public static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public static void showAlert(Alert.AlertType type, String title, String message, Window owner) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.showAndWait();
    }
}
