package app.main;

import javafx.scene.control.Alert;

/**
 * Instance {@code ErrorHandler} představují třídy sloužící k výpisu jakýchkoliv
 * chyb, které nastaly při běhu aplikace.
 *
 * @author Oldřich Hradil
 */
public class ErrorHandler extends Alert {

    /**
     * Konstruktor chyby
     *
     * @param title Titulek chyby
     * @param contentText Hlavní text chyby
     */
    public ErrorHandler(String title, String contentText) {
        super(AlertType.ERROR);
        this.setTitle(title);
        this.setContentText(contentText);
        this.showAndWait();
    }

}
