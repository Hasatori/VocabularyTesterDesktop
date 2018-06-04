package app.content;

import app.ContentManager;
import app.IDictionary;
import app.access.Database;
import app.contentManagement.DictionariesManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Instance {@code Dictionary} představuje slovník uživatele. Slovník obsahuje
 * slovíčka, může být upravován, je součástí relací zkoušení.
 *
 * @author Oldřich Hradil
 */
public class Dictionary extends AChangeable implements IDictionary {

    /**
     * Identifikační číslo slovníku. Je používáno při komunikaci s databázi.
     */
    public final Integer id;
    /**
     * Název slovníku
     */
    public final String name;
    /**
     * Jazyky, které slovník používá.
     */
    public final Language firstLanguage, secondLanguage;

    /**
     * Seznam slovíček, která tento slovník obsahuje.
     */
    private ObservableList<IDictionary.Vocabulary> vocabularies;

    /**
     * Konstrukto třídy. Definuje potřebné atributy.
     *
     * @param id Identifikační číslo slovníku
     * @param name Název slovníku
     * @param firstLanguage První jazyk, která slovník používá
     * @param secondLanguage Druhý jazyk, která slovník používá
     * @param contentManager Správce obsahu, který vykresluje jednotlivé
     * slovníky
     */
    public Dictionary(Integer id, String name, Language firstLanguage, Language secondLanguage, ContentManager contentManager) {
        super(contentManager);
        this.id = id;
        this.name = name;
        this.firstLanguage = firstLanguage;
        this.secondLanguage = secondLanguage;

    }

    //################################# GETTERS, SETTERS #################################//
    /**
     * Vrací seznam slovíček, které se nacházejí ve slovníku
     *
     * @return Seznam slovníků
     */
    @Override
    public ObservableList<IDictionary.Vocabulary> getVocabularies() {
        setVocabularies();
        return this.vocabularies;

    }

    /**
     * Z databáze načte slovíčka obsažená v tomto slovníku.
     */
    private void setVocabularies() {
        this.vocabularies = FXCollections.observableArrayList();
        PreparedStatement stmt = null;
        System.out.println("\n POKOUŠÍM SE NAČÍST SLOVÍČKA SLOVNÍKU:" + this.getName() + "\n");
        try {
            Connection connection = Database.getInstance().getConnection();
            stmt = connection.prepareStatement("SELECT first_value,second_value "
                    + "from contains_of where id_dictionary=?");
            stmt.setInt(1, this.id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Vocabulary vocabulary = new Vocabulary(rs.getString("first_value"),
                        rs.getString("second_value"));
                System.out.println(vocabulary.getFirstValue() + "=" + vocabulary.getSecondValue());
                vocabularies.add(vocabulary);
            }
            System.out.println("\nSLOVÍČKA NAČTENA\n");
        } catch (SQLException ex) {
            Logger.getLogger(Dictionary.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Integer getId() {
        return id;
    }

    /**
     *
     * @return Název slovníku
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     *
     * @return Jazyky obsažené ve slovníku
     */
    @Override
    public String getLanguages() {
        return firstLanguage.toString() + "-" + secondLanguage;
    }

    /**
     *
     * @return První jazyk slovníku
     */
    @Override
    public String getFirstLanguage() {
        return firstLanguage.toString();
    }

    /**
     *
     * @return Druhý jazyk slovníku
     */
    @Override
    public String getSecondLanguage() {
        return secondLanguage.toString();
    }
//################################# CUSTOM CLASS METHODS #################################//

    /**
     * Vymaže zadané slovíčko. K mazání dochází pouze na úrovní databáze a
     * následně je informován správce obsahu, který znovuvykreslí data a změna
     * se projeví.
     *
     * @param vocabulary Slovíčko jež má být vymazáno
     */
    @Override
    public void deleteVocabulary(IDictionary.Vocabulary vocabulary) {
        PreparedStatement stmt = null;
        System.out.println("\n POKUS O VYMAZÁNÍ SLOVÍČKA "
                + vocabulary.getFirstValue() + " " + vocabulary.getSecondValue()
                + " SLOVNÍKU " + this.getName()
                + "");
        try {
            Connection connection = Database.getInstance().getConnection();
            stmt = connection.prepareStatement("DELETE FROM contains_of WHERE "
                    + "id_dictionary=? and first_value=? and second_value=?");

            stmt.setInt(1, this.id);
            stmt.setString(2, vocabulary.getFirstValue());
            stmt.setString(3, vocabulary.getSecondValue());

            stmt.executeUpdate();

            System.out.println("\nSLOVÍČKO VYMAZÁNO\n");
        } catch (SQLException ex) {
            Logger.getLogger(Dictionary.class.getName()).log(Level.SEVERE, null, ex);
        }
        contentManager.refresh();

    }

    /**
     * Upraví slovíčko. K úpravě dochází pouze na úrovní databáze a následně je
     * informován správce obsahu, který znovuvykreslí data a změna se projeví.
     *
     * @param vocabulary Původní slovíčko.
     * @param firstValue Nová první hodntoa slovíčka
     * @param secondValue Nová druhá hodnota slovíčka
     * @return
     */
    @Override
    public boolean tryEditVocabulary(IDictionary.Vocabulary vocabulary, String firstValue, String secondValue) {
        PreparedStatement stmt = null;
        System.out.println("\n POKUS O ÚPRAVU SLOVÍČKA "
                + vocabulary.getFirstValue() + " " + vocabulary.getSecondValue()
                + " SLOVNÍKU " + this.getName()
                + "");
        if (vocExists(firstValue, secondValue)) {
            return false;
        } else {
            try {
                Connection connection = Database.getInstance().getConnection();
                stmt = connection.prepareStatement("SELECT count(*) FROM vocabulary"
                        + " WHERE first_value=? AND second_value=?");

                stmt.setString(1, firstValue);
                stmt.setString(2, secondValue);

                System.out.println(stmt.toString());
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    if (rs.getInt("count(*)") == 0) {
                        stmt = connection.prepareStatement("INSERT INTO "
                                + "vocabulary(first_value,second_value,f_val_source,"
                                + "s_val_source) VALUES(?,?,?,?)");

                        stmt.setString(1, firstValue);
                        stmt.setString(2, secondValue);
                        stmt.setString(3, this.getFirstLanguage());
                        stmt.setString(4, this.getSecondLanguage());
                        System.out.println(stmt.toString());
                        stmt.executeUpdate();
                    }

                }

                stmt = connection.prepareStatement("UPDATE contains_of SET first_value=? ,"
                        + "second_value=? where id_dictionary=? and first_value=? and "
                        + "second_value=?");

                stmt.setString(1, firstValue);
                stmt.setString(2, secondValue);
                stmt.setInt(3, this.id);
                stmt.setString(4, vocabulary.getFirstValue());
                stmt.setString(5, vocabulary.getSecondValue());
                System.out.println(stmt);
                stmt.executeUpdate();

            } catch (SQLException ex) {
                Logger.getLogger(Dictionary.class.getName()).log(Level.SEVERE, null, ex);
            }
            contentManager.refresh();
            return true;
        }
    }

    /**
     * Přidá slovíčko do databbáze. K přidání dochází pouze na úrovní databáze a
     * následně je informován správce obsahu, který znovuvykreslí data a změna
     * se projeví.
     *
     * @param firstValue První hodnota nového slovíčka
     * @param secondValue Druhá hodnota nového slovíčka
     * @return
     */
    public boolean tryAddVocabulary(String firstValue, String secondValue) {
        PreparedStatement stmt1 = null;
        if (vocExists(firstValue, secondValue)) {
            return false;
        } else {
            try {
                Connection connection = Database.getInstance().getConnection();

                stmt1 = connection.prepareStatement("select count(*) from vocabulary "
                        + "where first_value=? and second_value=?");
                stmt1.setString(1, firstValue);
                stmt1.setString(2, secondValue);
                System.out.println(stmt1.toString());
                ResultSet rs = stmt1.executeQuery();

                while (rs.next()) {
                    if (rs.getInt("count(*)") == 0) {
                        PreparedStatement stmt;
                        stmt = connection.prepareStatement("INSERT INTO "
                                + "vocabulary(first_value,second_value,f_val_source,"
                                + "s_val_source) VALUES(?,?,?,?)");

                        stmt.setString(1, firstValue);
                        stmt.setString(2, secondValue);
                        stmt.setString(3, this.getFirstLanguage());
                        stmt.setString(4, this.getSecondLanguage());
                        System.out.println(stmt.toString());
                        stmt.executeUpdate();

                    }
                    stmt1 = connection.prepareStatement("INSERT INTO contains_of "
                            + "(first_value,second_value,id_dictionary) VALUES "
                            + "(?,?,?) ");

                    stmt1.setString(1, firstValue);
                    stmt1.setString(2, secondValue);
                    stmt1.setInt(3, this.id);

                    stmt1.executeUpdate();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Dictionary.class.getName()).log(Level.SEVERE, null, ex);
            }
            contentManager.refresh();
            return true;
        }
    }

    private boolean vocExists(String firstValue, String secondValue) {
        ObservableList<IDictionary.Vocabulary> vocabularies = this.getVocabularies();
        for (int i = 0; i < vocabularies.size(); i++) {
            if (vocabularies.get(i).getFirstValue().equals(firstValue)
                    && vocabularies.get(i).getSecondValue().equals(secondValue)) {
                return true;
            }

        }
        return false;
    }

//################################# OVERRIDEN METHODS #################################//
    /**
     * Vymaže sama sebe z databáze. K mazání dochází pouze na úrovní databáze a
     * následně je informován správce obsahu, který znovuvykreslí data a změna
     * se projeví.
     */
    @Override

    public void deleteSelf() {

        PreparedStatement stmt = null;
        System.out.println("\n POKUS O VYMAZÁNÍ SLOVNÍKU:" + this.getName() + "\n");
        try {
            Connection connection = Database.getInstance().getConnection();
            stmt = connection.prepareStatement("DELETE FROM dictionary WHERE id_dictionary=?");

            stmt.setInt(1, this.id);
            System.out.println(stmt.toString());
            stmt.executeUpdate();

            System.out.println("\nSLOVNÍK VYMAZÁN\n");
        } catch (SQLException ex) {
            Logger.getLogger(Dictionary.class.getName()).log(Level.SEVERE, null, ex);
        }
        contentManager.refresh();
    }

    /**
     * Úpraví sama sebe. K úpravě dochází pouze na úrovní databáze a následně je
     * informován správce obsahu, který znovuvykreslí data a změna se projeví.
     *
     * @param values Pole hodnot, které mají být upraveny. Je potřeba udržovat
     * je v přesné pořadí a přetypovat je na atributy odpovádající hodnotě
     * atributu této instance. Pole je zvoleno z toho důvodu že se budou hodnoty
     * a počet atributů instancí AChangeable lišit.
     * @return
     */
    @Override

    public boolean tryEditSelf(Object... values) {
        String name = (String) values[0];
        String firstLanguage = (String) values[1];
        String secondLanguage = (String) values[2];

        PreparedStatement stmt = null;
        DictionariesManager dictionariesManager = (DictionariesManager) contentManager;
        if (dictionariesManager.aChangeableExists(name)) {
            return false;
        } else {
            try {
                Connection connection = Database.getInstance().getConnection();
                stmt = connection.prepareStatement("UPDATE dictionary SET dictionary_name=? ,"
                        + "first_lang=? ,second_lang=? where id_dictionary=?");

                stmt.setString(1, name);
                stmt.setString(2, firstLanguage);
                stmt.setString(3, secondLanguage);
                stmt.setInt(4, this.id);

                stmt.executeUpdate();

                System.out.println("\nSLOVNÍK UPRAVEN\n");
            } catch (SQLException ex) {
                Logger.getLogger(Dictionary.class.getName()).log(Level.SEVERE, null, ex);
            }

            contentManager.refresh();
            return true;
        }

    }

    @Override
    public String toString() {
        return this.name + "->" + this.firstLanguage + "-" + this.secondLanguage;
    }

    //################################# INNER CLASSES #################################//
    /**
     * Vnitřní třída reprezentující slovíčko ve slovníku. Vnitřní třída byl
     * zvolena proto, že třída Vocabulary nebude existovat sama o sobě, ale vždy
     * bude součástí slovníku.
     */
    public class Vocabulary implements IDictionary.Vocabulary {

        /**
         * První a dtuhá hodnota reprezentující slovíčko.
         */
        String firstValue, secondValue;

        /**
         * Konstruktor slovíčka. Definuje jeho atributy.
         *
         * @param firstValue První hodntoa slovíčka
         * @param secondValue Druhá hodnota slovíčka
         */
        public Vocabulary(String firstValue, String secondValue) {
            this.firstValue = firstValue;
            this.secondValue = secondValue;
        }

        /**
         *
         *
         * @return První hodnota slovíčka.
         */
        @Override
        public String getFirstValue() {
            return this.firstValue;
        }

        /**
         *
         * @return Druhá hodnota slovíčka.
         */
        @Override
        public String getSecondValue() {
            return this.secondValue;
        }

    }

}
