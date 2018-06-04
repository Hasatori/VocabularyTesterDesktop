package app.contentManagement;

import app.IDictionary;
import app.ITestingSession;
import app.content.Language;

import java.util.Optional;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import app.main.ErrorHandler;

/**
 * Instance {@code CustomDialo} představují vlastní dialogová okna, která budou
 * potřeba při úprave instancí AChangeable
 *
 * @author Oldřich Hradil
 */
class CustomDialog extends Dialog<Pair<String, String>> {

//################################# SLOVNÍKY #################################//
    /**
     * Konstruktor vytvářející dialogové okno pro editaci vybraného slovníku
     *
     * @param dictionary Slovník, jež má být editován
     * @param title Titulek zobrazen na horní liště okna
     * @param headerText Hlavní text, který je vypisován do oknas
     */
    CustomDialog(IDictionary dictionary, String title, String headerText) {
        this.setTitle(title);
        this.setHeaderText(headerText);
        ButtonType editButton = new ButtonType("Editovat", ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(editButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField name = new TextField();
        name.setText(dictionary.getName());
        ComboBox<String> firstLanguage = new ComboBox<>(Language.getLanguages());
        ComboBox<String> secondLanguage = new ComboBox<>(Language.getLanguages());
        firstLanguage.getSelectionModel().select(dictionary.getFirstLanguage());
        secondLanguage.getSelectionModel().select(dictionary.getSecondLanguage());
        grid.add(new Label("Název:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("První jazyk:"), 0, 1);
        grid.add(firstLanguage, 1, 1);
        grid.add(new Label("Druhý jazyk:"), 0, 2);
        grid.add(secondLanguage, 1, 2);

        this.getDialogPane().setContent(grid);

        this.setResultConverter(dialogButton -> {
            if (dialogButton == editButton) {
                if (!dictionary.tryEditSelf(name.getText(), firstLanguage.getSelectionModel().getSelectedItem(), secondLanguage.getSelectionModel().getSelectedItem())) {
                    new ErrorHandler("Editace slovníku", "Slovník s tímto názvem"
                            + " už existuje");
                    this.showAndWait();
                }
            }
            return null;
        });
        Optional<Pair<String, String>> result = this.showAndWait();

    }

    /**
     * Konstruktor vytvářející dialogové okno pro vytvoření nového slovníku.
     *
     * @param title Titulek dialogového okna
     * @param dictionariesManager Správce slovníků
     */
    CustomDialog(String title, DictionariesManager dictionariesManager) {
        this.setTitle(title);

        ButtonType editButton = new ButtonType("Vytvořit", ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(editButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField name = new TextField();

        ComboBox<String> firstLanguage = new ComboBox<>(Language.getLanguages());

        ComboBox<String> secondLanguage = new ComboBox<>(Language.getLanguages());

        grid.add(new Label("Název:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("První jazyk:"), 0, 1);
        grid.add(firstLanguage, 1, 1);
        grid.add(new Label("Druhý jazyk:"), 0, 2);
        grid.add(secondLanguage, 1, 2);

        this.getDialogPane().setContent(grid);

        this.setResultConverter(dialogButton -> {
            if (dialogButton == editButton) {

                if (!dictionariesManager.tryCreateAChangeable(name.getText(), firstLanguage.getSelectionModel().getSelectedItem(),
                        secondLanguage.getSelectionModel().getSelectedItem())) {
                    new ErrorHandler("Vytváření slovníku", "Slovník s tímto názvem"
                            + " už existuje");
                    this.showAndWait();
                }
            }
            return null;
        });
        Optional<Pair<String, String>> result = this.showAndWait();

    }

    /**
     * Konstuktor vytvářející dialogové okno pro tvorbu slovíčka slovníku.
     *
     * @param dictionary Slovník do nějž se slovíčko přidávává.
     * @param title Titulek okna.
     */
    CustomDialog(IDictionary dictionary, String title) {
        this.setTitle(title);

        ButtonType editButton = new ButtonType("Vytvořit", ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(editButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField firstValue = new TextField();

        TextField secondValue = new TextField();

        grid.add(new Label("Prvník hodnota:"), 0, 0);
        grid.add(firstValue, 1, 0);
        grid.add(new Label("Druhá hodnota:"), 0, 1);
        grid.add(secondValue, 1, 1);

        this.getDialogPane().setContent(grid);

        this.setResultConverter(dialogButton -> {
            if (dialogButton == editButton) {
                if (!dictionary.tryAddVocabulary(firstValue.getText(), secondValue.getText())) {
                    new ErrorHandler("Tvorba slovíčka slovníku " + dictionary.getName(), "Toto slovíčko je "
                            + "již obsaženo v tomto slovníku.");
                    this.showAndWait();
                }
            }
            return null;
        });
        Optional<Pair<String, String>> result = this.showAndWait();

    }

    /**
     * Konstruktor vytvářející dialogové okno pro editaci vybraného slovíčka
     * slovníku
     *
     * @param vocabulary Slovíčko, které chceme editovat
     * @param dictionary Slovník, ve kterém se dané slovíčko nachází
     * @param title Titulek zobrazen na horní liště okna
     */
    CustomDialog(IDictionary.Vocabulary vocabulary,
            IDictionary dictionary, String title
    ) {
        this.setTitle(title);

        ButtonType editButton = new ButtonType("Editovat", ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(editButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField firstValue = new TextField();
        firstValue.setText(vocabulary.getFirstValue());
        TextField secondValue = new TextField();
        secondValue.setText(vocabulary.getSecondValue());
        grid.add(new Label("Název:"), 0, 0);
        grid.add(firstValue, 1, 0);
        grid.add(new Label("První hodnota:"), 0, 1);
        grid.add(secondValue, 1, 1);

        this.getDialogPane().setContent(grid);

        this.setResultConverter(dialogButton -> {
            if (dialogButton == editButton) {
                if (!dictionary.tryEditVocabulary(vocabulary, firstValue.getText(), secondValue.getText())) {
                    new ErrorHandler("Editace slovíčka slovníku " + dictionary.getName(), "Toto slovíčko je "
                            + "již obsaženo v tomto slovníku.");
                    this.showAndWait();
                }
            }
            return null;
        });
        Optional<Pair<String, String>> result = this.showAndWait();

    }
//################################# ZKOUŠENÍ #################################//

    /**
     * Kostruktor vytvářející dialogové oknow pro vytvoření relace zkoušení
     *
     * @param title Titulek dialogového okna
     * @param practiceManager Správce relací zkoušení
     */
    CustomDialog(String title, PracticeManager practiceManager) {
        this.setTitle(title);

        ButtonType editButton = new ButtonType("Vytvořit", ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(editButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ObservableList<IDictionary> observableList = practiceManager.getDictionaries();

        ListView<IDictionary> listView = new ListView<>(observableList);

        listView.setMaxHeight(
                200);
        listView.setMinWidth(500);
        listView.getSelectionModel()
                .setSelectionMode(SelectionMode.MULTIPLE);

        TextField name = new TextField();

        TextField acceptAfter = new TextField();

        grid.add(
                new Label("Název:"), 0, 0);
        grid.add(name,
                1, 0);
        grid.add(
                new Label("Po kolika vyřadit z relace zkoušení:"), 0, 1);
        grid.add(acceptAfter,
                1, 1);

        grid.add(
                new Label("Slovníky pro zkoušení"), 2, 0);
        grid.add(listView,
                2, 1);

        this.getDialogPane()
                .setContent(grid);

        this.setResultConverter(dialogButton
                -> {

            if (dialogButton == editButton) {
                try {

                    if (listView.getSelectionModel().getSelectedItems().size() == 0) {

                        new ErrorHandler("Vytváření slovníku", "Nevybrali jste žádný slovník");
                        this.showAndWait();
                    }
                    else if (!practiceManager.tryCreateAChangeable(name.getText(),
                            Integer.parseInt(acceptAfter.getText()), listView.getSelectionModel().getSelectedItems())) {
                        new ErrorHandler("Vytváření relace zkoušení", "Relace "
                                + "zkoušení s tímto názvem"
                                + " už existuje");
                        this.showAndWait();
                    }

                }
                catch (NumberFormatException e) {
                    new ErrorHandler("Vytváření relace zkoušení", "Špatný formát");
                    this.showAndWait();

                }

            }
            return null;
        }
        );
        Optional<Pair<String, String>> result = this.showAndWait();

    }

    /**
     * Konstruktor vytvářející dialogové okno pro editaci vybrané relace
     * zkoušení
     *
     * @param testing_session Relace zkoušení jež chceme upravovat
     * @param title Titulek zobrazen na horní liště okna
     */
    CustomDialog(ITestingSession testing_session,
            String title
    ) {
        this.setTitle(title);

        ButtonType editButton = new ButtonType("Editovat", ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(editButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField name = new TextField();
        name.setText(testing_session.getName());

        TextField acceptAfter = new TextField();
        acceptAfter.setText(String.valueOf(testing_session.getToDelete()));
        grid.add(new Label("Název:"), 0, 0);
        grid.add(name, 1, 0);

        grid.add(new Label("Vyřadit po:"), 1, 1);
        grid.add(acceptAfter, 1, 2);
        this.getDialogPane().setContent(grid);

        this.setResultConverter(dialogButton -> {
            if (dialogButton == editButton) {

                if (!testing_session.tryEditSelf(name.getText(), Integer.parseInt(acceptAfter.getText()))) {
                    new ErrorHandler("Editace relace zkoušení", "Relace "
                            + "zkoušení s tímto názvem"
                            + " už existuje");
                    this.showAndWait();
                }
            }
            return null;
        });
        Optional<Pair<String, String>> result = this.showAndWait();

    }

    /**
     * Konstruktor vytvářející okno zobrazující vysledek vybrané relace
     * zkoušení.
     *
     * @param vocabularies Seznam slovíček relace zkoušení
     * @param testingSession Relace zkoušení
     * @param title Titule okna
     * @param header Nadpis okna
     */
    CustomDialog(ObservableList<ITestingSession.SessionVocabulary> vocabularies, ITestingSession testingSession, String title, String header) {
        this.setTitle(title);
        this.setHeaderText(header);
        ButtonType restartButton = new ButtonType("Restartovat relaci zkoušení", ButtonData.OK_DONE);
        ButtonType fromWorstButton = new ButtonType("Z nejméně úspěšných vytvořit slovník", ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Zavřít", ButtonData.CANCEL_CLOSE);
        this.getDialogPane().getButtonTypes().addAll(fromWorstButton, restartButton, cancel);

        TableView<ITestingSession.SessionVocabulary> resultTable = new TableView<>();
        resultTable.setMinWidth(700);
        resultTable.setMaxHeight(300);
        TableColumn<ITestingSession.SessionVocabulary, String> firstValue = new TableColumn<>("První hodnota");
        TableColumn<ITestingSession.SessionVocabulary, String> secondValue = new TableColumn<>("Druhá hodnota");
        TableColumn<ITestingSession.SessionVocabulary, Integer> right = new TableColumn<>("Správně");
        TableColumn<ITestingSession.SessionVocabulary, Integer> wrong = new TableColumn<>("Špatně");
        TableColumn<ITestingSession.SessionVocabulary, String> successRate = new TableColumn<>("Úspěšnost");
        firstValue.setCellValueFactory(new PropertyValueFactory<>("FirstValue"));
        secondValue.setCellValueFactory(new PropertyValueFactory<>("SecondValue"));
        right.setCellValueFactory(new PropertyValueFactory<>("Right"));
        wrong.setCellValueFactory(new PropertyValueFactory<>("Wrong"));
        successRate.setCellValueFactory(new PropertyValueFactory<>("SuccessRate"));
        resultTable.getColumns().add(firstValue);
        resultTable.getColumns().add(secondValue);
        resultTable.getColumns().add(right);
        resultTable.getColumns().add(wrong);
        resultTable.getColumns().add(successRate);

        firstValue.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.20));
        secondValue.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.20));
        right.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.20));
        wrong.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.20));
        successRate.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.20));
        vocabularies.forEach(access -> {
            System.out.println(access.getFirstValue());
        });
        resultTable.getItems().addAll(vocabularies);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(resultTable, 0, 0);
        this.getDialogPane().setContent(grid);
        this.setResultConverter(dialogButton -> {
            if (dialogButton == restartButton) {
                testingSession.restart();
            }
            else if (dialogButton == fromWorstButton) {
                new ErrorHandler("Není podpora", "Tato funkce zatím není podporována");
                this.showAndWait();
            }
            return null;
        });
        Optional<Pair<String, String>> result = this.showAndWait();
    }

}
