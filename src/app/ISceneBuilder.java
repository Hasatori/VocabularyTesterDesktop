/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.io.IOException;
import javafx.stage.Stage;

/**
 *
 * @author hradi
 */
public
        interface ISceneBuilder {

    public
            void buildLoginScene(Stage stage) throws IOException;

}
