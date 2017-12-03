package compgc01;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

/**
 * Class that creates new scenes.
 * @author Team 3: Filippos Zofakis and Lucio D'Alessandro
 * @since 03.12.2017
 */
public class SceneCreator {

    // launching the new scene based on the .fxml file name passed in the argument as a String variable
    // building the scene and setting the value for the instance variable loader
    public static void launchScene (String sceneName, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(sceneName));
        Main.setRoot(loader.load());
        Scene scene = new Scene(Main.getRoot());
        // Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Main.getStage().setScene(scene);
        Main.getStage().show();
    }
}