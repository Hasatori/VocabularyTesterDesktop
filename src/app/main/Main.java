package app.main;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import app.ISceneBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import app.gui.SceneBuilder;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;

/**
 * Instance {@code Main} představuje hlavní třídu, která spouští celou aplikaci.
 * Využívá stavitele scén, kterému předává své jeviště.
 *
 * @author Oldřich Hradil
 */
public class Main extends Application {

    public Main() {
    }
    /**
     * Stavite scén.
     */
    private static final ISceneBuilder SCENE_BUILDER = SceneBuilder.getInstance();

    /**
     * Startovací metoda. Využívá stavitele scén a předává mu své jeviště pro
     * vybudování potřebných scén.
     *
     * @param stage Hlavní a jediné jeviště aplikace.
     *
     */
    @Override
    public void start(Stage stage) throws Exception {

        stage.setMinWidth(1000);
        stage.setMinHeight(700);
        SCENE_BUILDER.buildLoginScene(stage);
        Stage editConfigStage = new Stage();

    }

    public static void main(String[] args) {
        launch(args);
        Platform.exit();
    }

}
