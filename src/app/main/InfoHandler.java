/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.main;

import javafx.scene.control.Alert;

/**
 *
 * @author hradi
 */
public
        class InfoHandler extends Alert {

    public
            InfoHandler(String title, String contentText) {
        super(AlertType.INFORMATION);
        this.setTitle(title);
        this.setContentText(contentText);
        this.showAndWait();
    }

}
