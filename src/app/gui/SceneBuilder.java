package app.gui;

import app.ISceneBuilder;
import app.IUser;
import app.access.User;
import app.main.Main;
import app.main.ErrorHandler;
import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * Instnace {@code SceneBuilder} představuje stavitele, který na požádání
 * vybuduje požadovanou scénu. Pracuje přitom s jeviště aplikace.
 *
 * @author Oldřich Hradil
 */
public class SceneBuilder implements ISceneBuilder {

    /**
     * Stavitelem vybudovaná scéna
     */
    private Scene scene;

    /**
     * Jediná instance této třídy.
     */
    private static final SceneBuilder SINGLETON = new SceneBuilder();

    /**
     * Soukromý konstruktor
     */
    private SceneBuilder() {

    }

    /**
     *
     * @return Jediná instance této třídy
     */
    public static SceneBuilder getInstance() {
        return SINGLETON;
    }

    /**
     * Vybuduje přihlašovací scénu. Vytvoří navigační lištu, specifikuje metody
     * tlačítek a odkazů přihlašovací scény.
     *
     * @param stage Jeviště aplikace
     * @throws IOException Vyhození výjmky
     *
     */
    @Override
    public void buildLoginScene(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(app.Src.class.getResource("gui/main.fxml"));

        this.scene = new Scene(root, stage.getWidth(), stage.getHeight());

        Parent mainContent = FXMLLoader.load(app.Src.class.getResource("gui/login.fxml"));
        Pane contentHolder = (Pane) scene.lookup("#contentHolder");
        contentHolder.getChildren().add(mainContent);
        VBox header = (VBox) scene.lookup("#header");
        header.getChildren().clear();
        new NavigationBar(scene);
        Button submit = (Button) mainContent.lookup("#login");
        TextField emailTextField = (TextField) mainContent.lookup("#emailTextField");
        TextField passwordTextField = (TextField) mainContent.lookup("#passwordTextField");
        submit.setOnAction(event -> {
            IUser s = new User(null, null, null);
            IUser user = s.tryLogin(emailTextField.getText(), passwordTextField.getText());
            if (user != null) {
                Parent root2;
                try {

                    buildLoggedScene(stage, contentHolder, user);
                } catch (IOException ex) {
                    new ErrorHandler("Nastala chyba", "Chyba při spojení s databází!");
                }
            }

        });
        Hyperlink link = (Hyperlink) scene.lookup("#forgottenPassword");
        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();

        if (link != null) {
            link.setOnAction((event) -> {
                try {
                    Desktop.getDesktop().browse(new URL("https://eso.vse.cz/~hrao01/forgottenPassword").toURI());
                } catch (MalformedURLException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                } catch (URISyntaxException | IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }

        stage.setScene(scene);

        stage.show();

    }

    /**
     * Vybuduje scénu pro přihlášeného uživatele. Tato metoda není veřejná
     * jelikož je volána z přihlašovací scény, která je také budována touto
     * třídou.
     *
     * @param stage Jeviště aplikace.
     * @throws IOException
     */
    private void buildLoggedScene(Stage stage, Pane contentHolder, IUser user) throws IOException {
        new NavigationBar(stage, scene, contentHolder, user);
        stage.setScene(scene);

    }

}
