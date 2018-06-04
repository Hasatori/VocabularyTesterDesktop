package app.gui;

import app.IUser;
import app.access.Database;
import app.ContentManager;
import app.contentManagement.DictionariesManager;
import app.contentManagement.PracticeManager;
import app.main.Main;
import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * Instance {@code NavigationBar} představuje přepínače scén. Pracuje s jevištěm
 * a oslovuje jednotlivé správce obsahuje a podle požedavku mezi nimi přepíná.
 * Má dvě podoby, jedna je přihašovací a druhá se zobrazuje při přihlášení
 * uživatele.
 *
 * @author Oldřich Hradil
 */
public
        class NavigationBar extends HBox {

    /**
     * Scéna, na které je navigační lišta vykreslována.
     */
    private final
            Scene scene;
    /**
     * Tlačítka, která slouží pro navigaci v aplikaci a také pro specifikaci
     * aktruálně načtené stránky.
     */
    private
            Button dictionaries, practice, logout, registration, webApp,
            currentPage;

    /**
     * Zdroj představující grafickou podobu naviagační lišti.
     */
    private final
            HBox source;

    /**
     * Vyvtoří navigační lištu pro přihlášeného uživatele.
     *
     * @param stage Jeviště aplikace
     * @param scene Scéna
     * @param user Přihlášený uživatel. Předává jej jednotivým správcům obsahu.
     * @throws IOException
     */
    NavigationBar(Stage stage, Scene scene, Pane contentHolder, IUser user) throws IOException {
        this.scene = scene;

        this.source = FXMLLoader.load(app.Src.class.getResource("gui/navigationBarLogged.fxml"));
        VBox header = (VBox) scene.lookup("#header");
        header.getChildren().clear();
        header.getChildren().add(source);

        dictionaries = (Button) source.lookup("#dictionaries");
        dictionaries.setOnAction(event -> {
            try {
                DictionariesManager dictionariesManager = new DictionariesManager(user);

                dictionaries.setTextFill(Color.BLACK);
                currentPage.setTextFill(Color.rgb(255, 255, 255, 0.8));
                currentPage = dictionaries;
                this.switchContent(dictionariesManager, contentHolder);
            }
            catch (IOException ex) {
                Logger.getLogger(NavigationBar.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (SQLException ex) {
                Logger.getLogger(NavigationBar.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        this.setHover(dictionaries);
        practice = (Button) source.lookup("#testingSessions");
        practice.setOnAction(event -> {
            try {
                PracticeManager practiceManager = new PracticeManager(user);
                practice.setTextFill(Color.BLACK);
                currentPage.setTextFill(Color.rgb(255, 255, 255, 0.8));
                currentPage = practice;
                this.switchContent(practiceManager, contentHolder);
            }
            catch (IOException ex) {
                Logger.getLogger(NavigationBar.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (SQLException ex) {
                Logger.getLogger(NavigationBar.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        this.setHover(practice);
        logout = (Button) source.lookup("#logout");
        logout.setOnAction(event -> {
            user.logout();
            Database.getInstance().disconnect();
            try {
                SceneBuilder.getInstance().buildLoginScene(stage);

            }
            catch (IOException ex) {
                Logger.getLogger(NavigationBar.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        );
        this.setHover(logout);
        currentPage = dictionaries;
        dictionaries.fire();

    }

    /**
     * Vytvoří základní verzi navigační lišty, pro nepřihlášeného uživatele.
     *
     * @param scene Scéna na které se navigační lišta vykreslována.
     * @throws IOException
     */
    NavigationBar(Scene scene) throws IOException {
        this.scene = scene;

        this.source = FXMLLoader.load(app.Src.class.getResource("gui/navigationBarLogin.fxml"));
        VBox header = (VBox) scene.lookup("#header");
        header.getChildren().add(source);

        webApp = (Button) source.lookup("#webApp");

        final
                WebView browser = new WebView();
        final
                WebEngine webEngine = browser.getEngine();

        if (webApp != null) {
            webApp.setOnAction((event) -> {
                try {
                    Desktop.getDesktop().browse(new URL("https://eso.vse.cz/~hrao01/").toURI());
                }
                catch (MalformedURLException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (URISyntaxException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
        this.setHover(webApp);
        registration = (Button) source.lookup("#registration");
        if (registration != null) {
            registration.setOnAction((event) -> {
                try {
                    Desktop.getDesktop().browse(new URL("https://eso.vse.cz/~hrao01/registration").toURI());
                }
                catch (MalformedURLException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (URISyntaxException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
        this.setHover(registration);
    }

    /**
     * Přepne vykreslovaný obsah. Do objektu, který je držitelem obsahu vloží
     * zdroj správce obsahu.
     *
     * @param contentManager Správce obsahu jehož zdroj budeme potřebovat
     * @param contentHolder Držitel obsahu, do nějž přidáme zdroj správce.
     */
    private
            void switchContent(ContentManager contentManager, Pane contentHolder) {
        contentHolder.getChildren().clear();

        contentHolder.getChildren().add(contentManager.getSource());

    }

    /**
     * Nastaví změnu tlačítka navigační lišty po přejetí myší.
     *
     * @param button Tlačítko, na které se má metoda aplikovat.
     */
    private
            void setHover(Button button) {
        button.setOnMouseEntered(event -> {
            button.setTextFill(Color.BLACK);
        });
        button.setOnMouseExited(event -> {
            if (button != currentPage) {
                button.setTextFill(Color.rgb(255, 255, 255, 0.8));
            }

        });

    }

}
