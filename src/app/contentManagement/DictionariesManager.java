package app.contentManagement;

import app.access.Database;
import app.content.AChangeable;
import app.content.Dictionary;
import app.content.Language;
import app.ContentManager;
import app.IDictionary;
import app.IUser;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import java.sql.Connection;
import app.main.ErrorHandler;

/**
 * Instance {@code DictionariesManager} představuje třídu starající se o správu
 * slovníků a slovíček přihlášeného uživatele.
 *
 * @author Oldřich Hradil
 */
@SuppressWarnings("unchecked")
public
        class DictionariesManager implements ContentManager {

    /**
     * Seznam slovníků, které tento objekt spravuje
     */
    private
            ObservableList<IDictionary> dictionaries;

    private final
            IUser user;
    /**
     * Zdroj, do kterého bude správe vykreslovat data
     */
    public final
            Parent source;

    /**
     * Tabulka, ve které budou zobrazovány spravované slovníky
     */
    private
            TableView<IDictionary> dictionariesTable;
    /**
     * Tabulka, do které budou zobrazována slovíčka vybraného slovníku
     */
    private
            TableView<IDictionary.Vocabulary> vocabulariesTable;
    /**
     * Slovník aktuálně vybraný v tabulce slovníků
     */
    private
            IDictionary selectedDictionary;
    /**
     * Slovíčko aktuálně vybrané v tabulce slovíček
     */
    private
            IDictionary.Vocabulary selectedVocabulary;
    /**
     * Tlačítka která budou sloužit k mazáni, editaci a přidávání slovníků a
     * slovíček.
     */
    private
            Button deleteDictionary, editDictionary, addDictionary,
            deleteVocabulary, editVocabulary, addVocabulary;

    private
            Label dictionaryName;

    /**
     * Konstruktor vytvářející instance správce. Deklaruje příslušné atributy.
     * Volá potřebné funkce pro načtení dat z databáze, jejich přenos do tabulek
     * a nastavení tabulek samotných.
     *
     * @param user Přihlášený uživatel.
     * @throws IOException Chybové hlášení
     * @throws SQLException Chybové hlášení
     */
    public
            DictionariesManager(IUser user) throws IOException, SQLException {
        this.user = user;
        this.source = FXMLLoader.load(app.Src.class.getResource("gui/dictionaries.fxml"));
        setComponents(false);

    }

    public
            DictionariesManager(IUser user, Boolean test) {
        this.user = user;
        this.source = null;
        setComponents(test);

    }

    //################################# GETTERS, SETTERS #################################//
    /**
     *
     * @param source
     * @param user
     * @throws SQLException
     */
    private
            void setComponents(boolean test) {
        if (!test) {

            dictionariesTable = (TableView<IDictionary>) source.lookup("#dictionariesTable");
            vocabulariesTable = ((TableView<IDictionary.Vocabulary>) source.lookup("#vocabulariesTable"));
            deleteDictionary = (Button) source.lookup("#deleteDictionaryButton");
            editDictionary = (Button) source.lookup("#editDictionaryButton");
            deleteVocabulary = (Button) source.lookup("#deleteVocabularyButton");
            editVocabulary = (Button) source.lookup("#editVocabularyButton");
            addDictionary = (Button) source.lookup("#addDictionary");
            addVocabulary = (Button) source.lookup("#addVocabulary");

            dictionaryName = (Label) source.lookup("#dictionaryName");
        }
        else {
            dictionariesTable = new TableView<>();
            vocabulariesTable = new TableView<>();
            deleteDictionary = new Button();
            editDictionary = new Button();
            deleteVocabulary = new Button();
            editVocabulary = new Button();
            addDictionary = new Button();
            addVocabulary = new Button();

            dictionaryName = new Label();
        }
        setColumns();
        dictionaries = FXCollections.observableArrayList();
        try {
            setDictionaries(user.getId());
        }
        catch (SQLException ex) {
            Logger.getLogger(DictionariesManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        dictionariesTable.getItems().addAll(dictionaries);
        ObservableList<IDictionary> rows = dictionariesTable.getItems();

        dictionariesTable.setRowFactory(tv -> {
            TableRow<IDictionary> row = new TableRow<>();

            row.setOnMouseClicked((MouseEvent event) -> {
                selectedDictionary = row.getItem();
                dictionaryName.setText(row.getItem().getName());
                vocabulariesTable.getColumns().clear();
                vocabulariesTable.getItems().clear();
                TableColumn<IDictionary.Vocabulary, String> firstLanguage = new TableColumn<IDictionary.Vocabulary, String>(row.getItem().getFirstLanguage());
                TableColumn<IDictionary.Vocabulary, String> secondLanguage = new TableColumn<IDictionary.Vocabulary, String>(row.getItem().getSecondLanguage());
                firstLanguage.setCellValueFactory(new PropertyValueFactory<IDictionary.Vocabulary, String>("FirstValue"));
                secondLanguage.setCellValueFactory(new PropertyValueFactory<IDictionary.Vocabulary, String>("SecondValue"));
                this.vocabulariesTable.getColumns().add(firstLanguage);
                this.vocabulariesTable.getColumns().add(secondLanguage);
                firstLanguage.prefWidthProperty().bind(dictionariesTable.widthProperty().multiply(0.5));
                secondLanguage.prefWidthProperty().bind(dictionariesTable.widthProperty().multiply(0.5));
                vocabulariesTable.getItems().addAll(row.getItem().getVocabularies());
            });

            return row;
        });
        vocabulariesTable.setRowFactory(tv -> {
            TableRow<IDictionary.Vocabulary> row = new TableRow<>();

            row.setOnMouseClicked((MouseEvent event) -> {
                selectedVocabulary = row.getItem();

            });

            return row;
        });

        dictionariesTable.getSelectionModel().select(1);

        deleteDictionary.setOnMouseClicked((MouseEvent event) -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Mazání slovníku");
            alert.setContentText("Opravdu chcete vymazat tento slovník?\n"
                    + selectedDictionary.getName() + " " + selectedDictionary.getLanguages());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                selectedDictionary.deleteSelf();
            }
            else {
                alert.close();
            }

        });

        editDictionary.setOnMouseClicked((MouseEvent event) -> {
            new CustomDialog(selectedDictionary, "Úprava slovníků", "Uprava slovníku " + selectedDictionary.getName() + "=>" + selectedDictionary.getLanguages());
        });

        deleteVocabulary.setOnMouseClicked((MouseEvent event) -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Mazání slovíčka");
            alert.setContentText("Opravdu chcete toto slovíčko?\n"
                    + "Slovník:" + selectedDictionary.getName() + " "
                    + selectedVocabulary.getFirstValue() + " " + selectedVocabulary.getSecondValue());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                selectedDictionary.deleteVocabulary(selectedVocabulary);

            }
            else {
                alert.close();
            }

        });

        editVocabulary.setOnMouseClicked((MouseEvent event) -> {
            new CustomDialog(selectedVocabulary, selectedDictionary, "Úprava slovíčka slovníku " + selectedDictionary.getName());
        });

        addDictionary.setOnMouseClicked((MouseEvent event) -> {
            new CustomDialog("Tvorba slovníku", this);
        });

        addVocabulary.setOnMouseClicked((MouseEvent event) -> {
            new CustomDialog(selectedDictionary, "Vytváření slovíčka slovníku " + selectedDictionary.getName());
        });
        if (!dictionariesTable.getItems().isEmpty()) {
            selectedDictionary = dictionariesTable.getItems().get(0);
        }
    }

    /**
     * Vytvoří a nastaví sloupce tabulek. Vytvoří jejich instance, specifikuje
     * jejich šířku a tovární třídu pro hodnotu buňěk těchto sloupců a přidá do
     * tabulek.
     */
    private
            void setColumns() {
        TableColumn<IDictionary, String> dictionaryName = new TableColumn<IDictionary, String>("Název");

        TableColumn<IDictionary, String> dictionaryLanguages = new TableColumn<IDictionary, String>("Jazyky");

        dictionaryName.setCellValueFactory(new PropertyValueFactory<IDictionary, String>("Name"));
        dictionaryLanguages.setCellValueFactory(new PropertyValueFactory<IDictionary, String>("Languages"));

        this.dictionariesTable.getColumns().add(dictionaryName);
        this.dictionariesTable.getColumns().add(dictionaryLanguages);

        dictionaryName.prefWidthProperty().bind(dictionariesTable.widthProperty().multiply(0.5));
        dictionaryLanguages.prefWidthProperty().bind(dictionariesTable.widthProperty().multiply(0.5));

    }

    /**
     * Naplní seznam slovníku daty. Tato metoda je využívána zejména při
     * znovunačítaní dat nebo při inicializaci správce.
     *
     * @param id Identifikační číslo uživatele.
     * @param Type Typ uživatele.
     * @throws SQLException
     */
    private
            void setDictionaries(Integer id)
            throws SQLException {

        PreparedStatement stmt = null;
        System.out.println("\nPOKOUŠÍM SE NAČÍST SLOVNÍKY:\n");
        try {
            stmt = Database.getInstance().getConnection().prepareStatement(
                    "select id_dictionary,dictionary_name,first_lang,"
                    + "second_lang "
                    + "from dictionary where id=? ");
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                IDictionary dictionary = new Dictionary(rs.getInt("id_dictionary"),
                        rs.getString("dictionary_name"), Language.getLang(rs.getString("first_lang")),
                        Language.getLang(rs.getString("second_lang")), this);

                dictionaries.add(dictionary);
                System.out.println(dictionary.getName() + " " + dictionary.getLanguages());

            }
            System.out.println("\nSLOVNÍKY NAČTENY\n");
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
//################################# OVERRIDEN METHODS #################################//

    /**
     *
     * @return Zdroj, do kterého mají být načítána data.
     */
    @Override
    public
            Parent getSource() {
        return source;
    }

    /**
     * Znovu načte vykreslovaná data. Neboli vyprázdní příslušné tabulky a
     * zavolá potřebné funkce pro jejich naplnění.
     */
    @Override
    public
            void refresh() {
        try {

            dictionariesTable.getItems().clear();
            vocabulariesTable.getItems().clear();
            dictionaries.clear();
            setDictionaries(user.getId());
            dictionariesTable.getItems().addAll(dictionaries);

        }
        catch (SQLException ex) {
            Logger.getLogger(DictionariesManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Pokusí se přidat nový slovník.
     *
     * @param values Seznam hodnot, které jsou potřebné pro vytvoření nového
     * slovníku.
     * @return Pokud je přidání úspěšně vrací true
     */
    @Override
    public
            boolean tryCreateAChangeable(Object... values) {
        for (int i = 0; i < values.length; i++) {
            System.out.println(values[i]);
        }
        String name = (String) values[0];
        String firstLanguage = (String) values[1];
        String secondLanguage = (String) values[2];
        PreparedStatement stmt = null;
        Connection connection = Database.getInstance().getConnection();

        try {

            stmt = connection.prepareStatement("select "
                    + "count(*) from dictionary "
                    + "where dictionary_name=? and id=?");

            stmt.setString(1, name);
            stmt.setInt(2, user.getId());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                if (rs.getInt("count(*)") == 0) {

                    stmt = connection.prepareStatement("INSERT INTO dictionary"
                            + "(id,dictionary_name,first_lang,second_lang) "
                            + "VALUES(?,?,?,?)");
                    stmt.setInt(1, user.getId());
                    stmt.setString(2, name);
                    stmt.setString(3, firstLanguage);
                    stmt.setString(4, secondLanguage);
                    System.out.println(stmt.toString());
                    stmt.executeUpdate();

                    System.out.println("\nSLOVNÍK VYTVOŘEN\n");
                }
                else {
                    return false;
                }
            }

        }
        catch (SQLException ex) {
            Logger.getLogger(AChangeable.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.refresh();
        return true;
    }

    /**
     * Zjistí zda instance objektu který spravuje existuje.
     *
     * @param dictionaryName Název slovníku. Zjištujeme zda už slovník s tímto
     * názvem existuje. Musíme dát pozor na kontrolu toho zda se nejdená o právě
     * vybraný slovník. Tento slovník musíme vyřadit z validace.
     * @return Pokud nalezne tak vrací true.
     */
    @Override
    public
            boolean aChangeableExists(String dictionaryName) {
        this.refresh();

        ObservableList<IDictionary> dictionaries = dictionariesTable.getItems();
        for (int i = 0; i < dictionaries.size(); i++) {
            if (dictionaries.get(i).getName().equals(dictionaryName)) {
                if (selectedDictionary != null && !(selectedDictionary.getName().equals(dictionaryName))) {
                    return true;
                }
                return true;
            }
        }
        return false;

    }

}
