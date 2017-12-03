package compgc01;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.json.simple.JSONObject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

/**
 * The controller for the Films Scene.
 * 
 * @author Team 3: Filippos Zofakis and Lucio D'Alessandro
 * @since 03.12.2017
 */
public class ManageFilmsController {

    private File selectedImage;
    
    @FXML
    Button backButton;
    @FXML
    Text newFilmTitle, newFilmDescription, newFilmStartDate, newFilmEndDate, newFilmTime;
    @FXML
    TextArea filmDescription;
    @FXML
    DatePicker filmStartDate, filmEndDate;
    @FXML
    TextField filmTitle;
    @FXML
    ComboBox<String> filmTime;
    @FXML
    ImageView uploadedFilmPoster;

    @FXML
    void initialize() throws IOException {

        ObservableList<String> obsList = FXCollections.observableArrayList("14:00", "15:00", "16:00", "17:00", "18:00",
                "19:00", "20:00", "21:00", "22:00", "23:00");
        filmTime.setItems(obsList);
        File file = new File(URLDecoder.decode(Main.getPath() + "res/images/filmImages/DefaultFilmPoster.png", "UTF-8"));
        Image img = SwingFXUtils.toFXImage(ImageIO.read(file), null);
        uploadedFilmPoster.setImage(img);    }

    @FXML
    public void backToPrevScene(ActionEvent event) throws IOException {

        SceneCreator.launchScene("UserScene.fxml", event);
    }

    @FXML
    public void updateDateAndTime(ActionEvent e) {

        switch (((Node) e.getSource()).getId()) {
        case "filmStartDate":
            newFilmStartDate.setText(filmStartDate.getValue().toString());
            break;
        case "filmEndDate":
            newFilmEndDate.setText(filmEndDate.getValue().toString());
            break;
        case "filmTime":
            newFilmTime.setText(filmTime.getValue().toString());
            break;
        }
    }

    @FXML
    public void updateFilmText(KeyEvent e) {

        switch (((Node) e.getSource()).getId()) {
        case "filmTitle":
            if (filmTitle.getText().length() > 20) {
                filmTitle.setEditable(false);
            }
            break;
        case "filmDescription":
            if (filmDescription.getText().length() > 220) {
                filmDescription.setEditable(false);
            }
            break;
        }

        if (e.getCode().equals(KeyCode.BACK_SPACE)) {
            filmTitle.setEditable(true);
            filmDescription.setEditable(true);
        }

        switch (((Node) e.getSource()).getId()) {
        case "filmTitle":
            newFilmTitle.setText(filmTitle.getText());
            break;
        case "filmDescription":
            newFilmDescription.setText(filmDescription.getText());
            break;
        }
    }

    @FXML
    public void uploadImageClick(ActionEvent event) throws IOException {

        try {
            FileChooser fc = new FileChooser();
            selectedImage = fc.showOpenDialog(null);
            // checking that input file is not null and handling the exception
            if (selectedImage == null)
                return;
            else {
                Image img = SwingFXUtils.toFXImage(ImageIO.read(selectedImage), null);

                uploadedFilmPoster.setImage(img);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    @FXML
    public void storeFilmInfo(ActionEvent event) {

        try {
            /*
            System.out.println(filmTitle.getText());
            System.out.println(filmDescription.getText());
            System.out.println(newFilmStartDate.getText());
            System.out.println(newFilmEndDate.getText());
            System.out.println(newFilmTime.getText());
             */

            validateFilmInput();

            // Creating JSON files
            JSONObject films = Main.readJSONFile("filmsJSON.txt");
            JSONObject filmToAdd = new JSONObject();
            filmToAdd.put("title", filmTitle.getText());
            filmToAdd.put("description", filmDescription.getText());
            filmToAdd.put("startDate", newFilmStartDate.getText());
            filmToAdd.put("endDate", newFilmEndDate.getText());
            filmToAdd.put("time", newFilmTime.getText());
            films.put(filmTitle.getText(), filmToAdd);
            // System.out.println(films.toJSONString());

            // storing film in JSON file
            String path = URLDecoder.decode(Main.getPath() + "res/filmsJSON.txt", "UTF-8");
            // System.out.println(path);
            PrintWriter writer = new PrintWriter( new File(path));
            writer.print(films.toJSONString());
            writer.close();
            
            // storing film poster in film images folder
            String folderPath = URLDecoder.decode(Main.getPath() + "res/images/filmImages/", "UTF-8");
            File uploads = new File(folderPath);
            File file = new File(uploads, filmTitle.getText() + ".png");
            InputStream input = Files.newInputStream(selectedImage.toPath());
            Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            // confirmation alert to inform the employee of the newly added film
            Alert alert = new Alert(AlertType.INFORMATION,
                    "The Film " + filmTitle.getText() + " has been added!", ButtonType.OK);
            alert.showAndWait();

            // close alert on click and restore all fields to empty
            if (alert.getResult() == ButtonType.OK) {
                alert.close();
                filmDescription.setText("");
                filmTitle.setText("");
                filmStartDate.setPromptText("yyyy-mm-dd");
                filmEndDate.setPromptText("yyyy-mm-dd");
                filmTime.setPromptText("hh:mm");
            }
            // SceneCreator.launchScene("UserScene.fxml", event);
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(AlertType.WARNING, "File Not Found!", ButtonType.OK);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                alert.close();
            }
        } catch (IOException e) {
            Alert alert = new Alert(AlertType.WARNING, "Error: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                alert.close();
            }
        } catch (InvalidFilmInputException e) {
            Alert alert = new Alert(AlertType.WARNING, e.getMessage(), ButtonType.OK);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                alert.close();
            }
        }
    }

    // custom exception to be thrown if at least one field is empty or if end date is before start date
    @SuppressWarnings("unlikely-arg-type")
    void validateFilmInput() throws InvalidFilmInputException {

        try {
            if (filmTitle.getText().equals("") || 
                    filmDescription.getText().equals("") || 
                    filmStartDate.getValue().equals("yyyy-mm-dd") || 
                    filmEndDate.getValue().equals("yyyy-mm-dd") || 
                    filmTime.getValue().equals("hh:mm"))
                throw new InvalidFilmInputException("Please complete all fields!");

            if (filmStartDate.getValue().compareTo(filmEndDate.getValue()) > 0)
                throw new InvalidFilmInputException("End date cannot be before start date!");
        }
        catch (NullPointerException e) {
            throw new InvalidFilmInputException("Please complete all fields!");
        }
    }
}

class InvalidFilmInputException extends Exception {

    private static final long serialVersionUID = 1L;

    InvalidFilmInputException(String s) {
        super(s);
    }
}