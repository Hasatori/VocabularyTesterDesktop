package app.contentManagement;

import app.access.Database;
import app.content.AChangeable;
import app.IDictionary;
import app.content.Language;
import app.ContentManager;
import app.ITestingSession;
import app.IUser;
import app.content.Dictionary;
import app.content.TestingSession;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;

@SuppressWarnings("unchecked")
/**
 * Instance {@code PracticeManager} představuje správce relací zkoušení.
 * Vykresluje jednotlivé relace zkoušení do příslušných částí a na požádání
 * spravovaných objektů znovunačte vykreslovaná data.
 *
 * @author Oldřich Hradil
 * @version
 */
public
        class PracticeManager implements ContentManager {

    private final
            IUser user;

    /**
     * Seznam relací zkoušení, které spravuje
     */
    private
            ObservableList<ITestingSession> testingSessions;
    /**
     * Tabulka, do které se vypisují jednotlivé relace zkoušení
     */
    private
            TableView<ITestingSession> testingSessionsTable;
    /**
     * Textové pole pro překlad zkoušeného slovíčka
     */
    private
            TextField answerField;
    /**
     * Štitek pro slovo, které máme přeložit a také pro zobrazení úšpešnosti v
     * dané relaci zkoušení.
     */
    private
            Label wordToKnowLabel, successRate, wordToKnowField;
    /**
     * Tlačítka pro zobrazení nápovědi, editování, mazání zapnutí a vytvoření
     * relace zkoušení.
     */
    private
            Button help, editITestingSession, deleteITestingSession,
            startITestingSession, createITestingSession, changeDirection, submit,
            tryAgain, continuePractice;
    private
            Tooltip tooltip;
    /**
     * Akturálně vybraná relace zkoušení.
     */
    private
            ITestingSession selectedITestingSession;

    /**
     * Zdroj, do kterého mají být vyreslována data.
     */
    public final
            Parent source;

    /**
     * Konstruktor. Deklaruje příslušné atributy a spouští veškeré potřebné
     * funkce pro načtění relací zkoušení.
     *
     * @param user Přihlášený uživatel
     * @throws IOException Chybové hlášení
     * @throws SQLException Chybové hlášení
     *
     *
     */
    public
            PracticeManager(IUser user) throws IOException, SQLException {
        this.user = user;
        this.source = FXMLLoader.load(app.Src.class.getResource("gui/testingSessions.fxml"));
        setComponents(false);
    }

    public
            PracticeManager(IUser user, Boolean test) throws SQLException {
        this.user = user;
        this.source = null;
        setComponents(test);
    }
    //################################# PRIVATE METHODS #################################//

    private
            void sendAnswer() {
        if (selectedITestingSession.verify(answerField.getText())) {
            answerField.setStyle("-fx-background-color:#28A745");

            tryAgain.setVisible(false);
        }
        else {
            answerField.setStyle("-fx-background-color:#DC3545");
            tryAgain.setVisible(true);
        }
        submit.setVisible(false);
        continuePractice.setVisible(true);
        successRate.setText("Úspěšnost: " + selectedITestingSession.getSuccessRate() + "%");

    }

    private
            void continuePractice() {
        selectedITestingSession.nexWord();
        String wordToKnow = selectedITestingSession.getWordToKnow();
        if (wordToKnow == null) {
            new CustomDialog(selectedITestingSession.getResults(), selectedITestingSession, "Výsledky zkoušení",
                    "Výsledky zkoušení z relace zkoušení:" + selectedITestingSession.getName());

        }
        else {

            answerField.setText("");
            answerField.setStyle("-fx-background-color:white");

            wordToKnowField.setText(wordToKnow);
            continuePractice.setVisible(false);
            tryAgain.setVisible(false);
            submit.setVisible(true);
            successRate.setText("Úspěšnost: " + selectedITestingSession.getSuccessRate() + "%");
        }

    }

    //################################# GETTERS, SETTERS #################################//
    private
            void setComponents(Boolean test) throws SQLException {
        if (!test) {
            testingSessionsTable = (TableView<ITestingSession>) source.lookup("#testingSessionsTable");
            editITestingSession = (Button) source.lookup("#editTestingSession");
            deleteITestingSession = (Button) source.lookup("#deleteTestingSession");
            startITestingSession = (Button) source.lookup("#startTestingSession");
            createITestingSession = (Button) source.lookup("#createTestingSession");
            changeDirection = (Button) source.lookup("#changeDirection");
            wordToKnowField = (Label) source.lookup("#wordToKnow");
            answerField = (TextField) source.lookup("#translation");
            submit = (Button) source.lookup("#submit");
            tryAgain = (Button) source.lookup("#tryAgain");
            continuePractice = (Button) source.lookup("#continuePractice");
            successRate = (Label) source.lookup("#successRate");
            help = (Button) source.lookup("#help");
        }
        else {
            testingSessionsTable = new TableView<>();
            editITestingSession = new Button();
            deleteITestingSession = new Button();
            startITestingSession = new Button();
            createITestingSession = new Button();
            changeDirection = new Button();
            wordToKnowField = new Label();
            answerField = new TextField();
            submit = new Button();
            tryAgain = new Button();
            continuePractice = new Button();
            successRate = new Label();
            help = new Button();
        }
        testingSessions = FXCollections.observableArrayList();
        setTestingSessions(user.getId());
        setColumns();

        testingSessionsTable.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

        testingSessionsTable.getItems().addAll(testingSessions);
        testingSessionsTable.setRowFactory(tv -> {
            TableRow<ITestingSession> row = new TableRow<>();

            row.setOnMouseClicked((MouseEvent event) -> {
                selectedITestingSession = row.getItem();

            });

            return row;
        });

        deleteITestingSession.setOnMouseClicked((MouseEvent event) -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Mazání relace zkoušení");
            alert.setContentText("Opravdu chcete toto tuto relaci zkoušení?\n"
                    + "Slovník:" + selectedITestingSession.getName());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {

                selectedITestingSession.deleteSelf();

            }
            else {
                alert.close();
            }

        });
        editITestingSession.setOnMouseClicked((MouseEvent event) -> {
            new CustomDialog(selectedITestingSession, "Úprava relace zkoušení");
        });

        startITestingSession.setOnMouseClicked((MouseEvent event) -> {
            continuePractice();
        });
        createITestingSession.setOnMouseClicked((MouseEvent event) -> {
            new CustomDialog("Vytváření relace zkoušení", this);
        });

        startITestingSession.setOnMouseClicked((MouseEvent event) -> {
            continuePractice();
        });

        changeDirection.setOnMouseClicked((MouseEvent event) -> {
            selectedITestingSession.changeDirection();
            wordToKnowField.setText(selectedITestingSession.getWordToKnow());

        });

        submit.setOnMouseClicked((MouseEvent event) -> {
            sendAnswer();
        });

        tryAgain.setVisible(false);
        tryAgain.setOnMouseClicked((MouseEvent event) -> {
            sendAnswer();
        });

        continuePractice.setVisible(false);
        continuePractice.setOnMouseClicked((MouseEvent event) -> {
            continuePractice();
        });

        help.setOnMouseEntered(a -> {
            tooltip = new Tooltip(selectedITestingSession.getTranslation());
            help.setTooltip(tooltip);
            tooltip.show(help, a.getScreenX(), a.getScreenY() + 15);
        });
        help.setOnMouseExited(a -> {
            tooltip.hide();
        });
        if (!(testingSessionsTable.getItems().isEmpty())) {
            selectedITestingSession = testingSessionsTable.getItems().get(0);
        }
    }

    /**
     *
     * @return Zdroj, do kterého jsou vykreslována data.
     */
    @Override
    public
            Parent getSource() {
        return this.source;
    }

    /**
     * Vytvoří a nastaví sloupce tabulek. Vytvoří jejich instance, specifikuje
     * jejich šířku a tovární třídu pro hodnotu buňěk těchto sloupců a přidá do
     * tabulek.
     */
    private
            void setColumns() {
        TableColumn<ITestingSession, String> testingSessionName = new TableColumn<ITestingSession, String>("Název");

        TableColumn<ITestingSession, String> showSuccess = new TableColumn<ITestingSession, String>("Zobrazovat úpěšnost");
        TableColumn<ITestingSession, Integer> acceptAfter = new TableColumn<ITestingSession, Integer>("Schválit po");
        testingSessionName.setCellValueFactory(new PropertyValueFactory<ITestingSession, String>("Name"));
        showSuccess.setCellValueFactory(new PropertyValueFactory<ITestingSession, String>("ShowSuccessRate"));
        acceptAfter.setCellValueFactory(new PropertyValueFactory<ITestingSession, Integer>("ToDelete"));

        this.testingSessionsTable.getColumns().add(testingSessionName);
        this.testingSessionsTable.getColumns().add(showSuccess);
        this.testingSessionsTable.getColumns().add(acceptAfter);

        testingSessionName.prefWidthProperty().bind(testingSessionsTable.widthProperty().multiply(0.20));
        showSuccess.prefWidthProperty().bind(testingSessionsTable.widthProperty().multiply(0.55));
        acceptAfter.prefWidthProperty().bind(testingSessionsTable.widthProperty().multiply(0.25));
    }

    /**
     * Naplní seznam relací zkoušení daty. Tato metoda je využívána zejména při
     * znovunačítaní dat nebo při inicializaci správce.
     *
     * @param id Identifikační číslo přihlášeného uživatele
     * @throws SQLException
     */
    private
            void setTestingSessions(Integer id) throws SQLException {

        PreparedStatement stmt = null;
        System.out.println("\nPOKOUŠÍM SE NAČÍST RELACE ZKOUŠENÍ:\n");
        try {
            stmt = Database.getInstance().getConnection().prepareStatement(
                    "SELECT * FROM practice where ID=?");
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ITestingSession testing_session
                        = new TestingSession(rs.getInt("id_practice"), rs.getString("practice_name"),
                                rs.getInt("accept_after"), this);

                testingSessions.add(testing_session);
                System.out.println(testing_session.getName() + " " + testing_session.getToDelete());

            }
            System.out.println("\nRELACE ZKOUŠENÍ NAČTENY\n");
        }
        catch (SQLException e) {
            System.out.println("NASTALA CHYBA: " + e.getMessage());
        }
        finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    /**
     * Vrací seznam slovníků.
     *
     * @return Seznam slovníků
     */
    public
            ObservableList<IDictionary> getDictionaries() {
        PreparedStatement stmt = null;

        ObservableList<IDictionary> dictionaries = FXCollections.observableArrayList();
        try {
            stmt = Database.getInstance().getConnection().prepareStatement(
                    "select * from dictionary where id=? ");

            stmt.setInt(1, user.getId());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                IDictionary dictionary = new Dictionary(rs.getInt("id_dictionary"),
                        rs.getString("dictionary_name"), Language.getLang(rs.getString("first_lang")),
                        Language.getLang(rs.getString("second_lang")), this);
                dictionaries.add(dictionary);
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(PracticeManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dictionaries;
    }

    //################################# OVERRIDEN METHODS #################################//
    /**
     * Spustí potřebné funkce pro znovunačtení dat, která spravuje. Tato funkce
     * je volána spravovanými objekty, při jakékoliv změně.
     */
    @Override
    public
            void refresh() {
        try {

            testingSessionsTable.getItems().clear();
            testingSessions.clear();
            setTestingSessions(user.getId());

            testingSessionsTable.getItems().addAll(testingSessions);
            successRate.setText("Úspěšnost: ");
            wordToKnowField.setText("");

            answerField.setText(null);
            answerField.setStyle("-fx-background-color:white");
        }
        catch (SQLException ex) {
            Logger.getLogger(DictionariesManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Pokusí se vytvořit novou instanci relace zkoušení.
     *
     * @param values Pole hodnot potebných pro vytvoření relace zkoušení
     * @return Pokud je přidání úspěšné vrací true
     */
    @Override
    public
            boolean tryCreateAChangeable(Object... values) {
        for (Object value : values) {
            System.out.println(value.toString());

        }
        String name = (String) values[0];
        Integer toDelete = (Integer) values[1];

        ObservableList<IDictionary> dictionaries = (ObservableList<IDictionary>) values[2];
        Connection connection = Database.getInstance().getConnection();
        if (aChangeableExists(name)) {
            return false;
        }
        else {
//            new InfoHandler("Tvorba relace zkoušení", "Tvorba relace zkoušení může chvíli trval,"
//                    + "bude proto probíhat na pozadí.");
//            new Thread(new Runnable() {
//                @Override
//                public
//                        void run() {
            PreparedStatement stmt = null;
            try {

                stmt = connection.prepareStatement("select "
                        + "count(*) from practice "
                        + "where practice_name=? and id=?");

                stmt.setString(1, name);
                stmt.setInt(2, user.getId());

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    if (rs.getInt("count(*)") == 0) {

                        stmt = connection.prepareStatement("INSERT INTO practice"
                                + " (id,practice_name,accept_after) "
                                + "VALUES(?,?,?)");
                        stmt.setInt(1, user.getId());
                        stmt.setString(2, name);

                        stmt.setInt(3, toDelete);
                        System.out.println(stmt.toString());
                        stmt.executeUpdate();

                        stmt = connection.prepareStatement("select "
                                + "id_practice from practice "
                                + "where practice_name=? and id=?");

                        stmt.setString(1, name);
                        stmt.setInt(2, user.getId());
                        rs = stmt.executeQuery();
                        while (rs.next()) {
                            Integer practiceId = rs.getInt("id_practice");
                            dictionaries.forEach(a -> {

                                ObservableList<IDictionary.Vocabulary> vocabularies = a.getVocabularies();
                                vocabularies.forEach(b -> {
                                    try {
                                        PreparedStatement stmt2 = connection.prepareStatement("INSERT INTO practice_content"
                                                + " (id_practice,id_dictionary,first_value,second_value)"
                                                + " VALUES(?,?,?,?)");
                                        stmt2.setInt(1, practiceId);
                                        stmt2.setInt(2, a.getId());
                                        stmt2.setString(3, b.getFirstValue());
                                        stmt2.setString(4, b.getSecondValue());
                                        stmt2.executeUpdate();
                                    }
                                    catch (SQLException ex) {
                                        Logger.getLogger(PracticeManager.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                });
                            });
                        }
                        System.out.println("\nRELACE ZKOUŠENÍ VYTVOŘENA\n");
                        PracticeManager.this.refresh();
                    }
                    else {
                        return false;

                    }

                }

            }
            catch (SQLException ex) {
                Logger.getLogger(AChangeable.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

//            }).start();
        return true;

    }

    /**
     * Zjistí zda relace zkoušení už existuje
     *
     * @param name Název relace zkoušení. Zjištujeme zda už takováto relace
     * zkoušení existuje. Musíme dávat pozor abychom z kontroly vyřadili i
     * aktuálně vybranou relaci zkoušení.
     * @return Pokud relace s tímto názvem existuje, vrací true.
     */
    @Override
    public
            boolean aChangeableExists(String name) {
        this.refresh();

        ObservableList<ITestingSession> dictionaries = testingSessionsTable.getItems();
        for (int i = 0; i < dictionaries.size(); i++) {
            if (dictionaries.get(i).getName().equals(name)) {
                if (selectedITestingSession != null && !(selectedITestingSession.getName().equals(name))) {
                    return true;
                }
                return true;
            }
        }
        return false;
    }
}
