package app.content;

import app.ContentManager;
import app.IDictionary;
import app.ITestingSession;
import app.IUser;
import app.access.User;
import app.access.Database;
import app.contentManagement.PracticeManager;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Instance {@code TestingSession} představuje relaci zkoušení. Je tvořena
 * slovníky uživatele.
 *
 *
 * @author Oldřich Hradil
 */
public class TestingSession extends AChangeable implements ITestingSession {

    /**
     * Identifikační číslo relace zkoušení uložene v databázi.
     */
    public final Integer id;
    /**
     * Název relace zkoušení.
     */
    public final String name;

    /**
     * Seznam slovníků, ze kterých he relace zkoušení tvořena.
     */
    private LinkedList<Dictionary> dictionaries;
    /**
     * Seznam slovíček, která jsou součástí relace zkoušení.
     */
    private LinkedList<Dictionary.Vocabulary> vocabularies;
    /**
     * Počet správných odpovědí něž dojde vyřazení slovíčka z relace zkoušení
     */
    public final int toDelete;

    /**
     * Specifikuje v jakém směru zkoušení probíhá.
     */
    private app.content.PracticeDirection direction;

    private String wordToKnow, translation;

    /**
     * Vytváří relaci zkoušení.
     *
     * @param id Idetifikační číslo relace zkoušení
     * @param name Název relace zkoušení
     * @param toDelete Po kolika správných odpovědích bude slovíčko vyřezeno z
     * relace zkoušení
     * @param showSuccessRate Bude zobrazena úspěšnost
     * @param showHelp Bude zobrazena možnost nápovědy
     * @param contentManager Správce obsahu relací zkoušení
     */
    public TestingSession(int id, String name, int toDelete, ContentManager contentManager
    ) {
        super(contentManager);
        this.id = id;
        this.name = name;
        this.toDelete = toDelete;

        this.direction = app.content.PracticeDirection.LR;

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
        System.out.println("\n POKUS O VYMAZÁNÍ RELACE ZKOUŠENÍ:" + this.getName() + "\n");
        try {
            Connection connection = Database.getInstance().getConnection();
            stmt = connection.prepareStatement("DELETE FROM practice WHERE id_practice=?");

            stmt.setInt(1, this.id);
            System.out.println(stmt.toString());
            stmt.executeUpdate();

            System.out.println("\nRELACE ZKOUŠENÍ VYMAZÁNA\n");
        } catch (SQLException ex) {
            Logger.getLogger(Dictionary.class.getName()).log(Level.SEVERE, null, ex);
        }
        contentManager.refresh();
    }

    /**
     * Upraví sama sebe z databáze. K mazání dochází pouze na úrovní databáze a
     * následně je informován správce obsahu, který znovuvykreslí data a změna
     * se projeví.
     */
    @Override

    public boolean tryEditSelf(Object... values) {
        String name = (String) values[0];
        Integer toDelete = (Integer) values[1];
        Boolean showSuccessRate = (Boolean) values[2];

        PreparedStatement stmt = null;
        PracticeManager contentManager = (PracticeManager) this.contentManager;
        if (contentManager.aChangeableExists(name)) {
            return false;
        } else {
            try {

                Connection connection = Database.getInstance().getConnection();
                stmt = connection.prepareStatement("UPDATE practice SET practice_name=? ,accept_after=? where id_practice=?");

                stmt.setString(1, name);

                stmt.setInt(2, toDelete);
                stmt.setInt(3, this.id);

                stmt.executeUpdate();

                System.out.println("\nSLOVNÍK UPRAVEN\n");
            } catch (SQLException ex) {
                Logger.getLogger(Dictionary.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        contentManager.refresh();
        return true;
    }

    @Override
    public String toString() {
        return this.getName() + " " + this.getDirection() + " " + this.getToDelete();
    }

    //################################# CUSTOM METHODS #################################//
    /**
     * Vybera následující slovo pro zkoušení.
     */
    @Override
    public void nexWord() {
        PreparedStatement stmt = null;
        PracticeManager contentManager = (PracticeManager) this.contentManager;
        String[] result = new String[2];
        this.translation = null;
        this.wordToKnow = null;
        try {

            stmt = Database.getInstance().getConnection().prepareStatement("select first_value,second_value from practice_content "
                    + "where id_practice=? and right_answers<? ORDER BY RAND() LIMIT 1");
            stmt.setInt(1, this.id);
            stmt.setInt(2, this.toDelete);

            ResultSet rs = stmt.executeQuery();
            System.out.println(stmt.toString());
            while (rs.next()) {
                System.out.println(rs.getString("first_value") != null);
                if (rs.getString("first_value") != null) {

                    switch (this.direction) {
                        case LR:
                            this.wordToKnow = rs.getString("first_value");
                            this.translation = rs.getString("second_value");
                            break;
                        case RL:
                            this.translation = rs.getString("second_value");
                            this.wordToKnow = rs.getString("first_value");
                            break;

                    }

                }

            }
            System.out.println("\nSLOVNÍK UPRAVEN\n");
        } catch (SQLException ex) {
            Logger.getLogger(Dictionary.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Restartuje relaci zkoušení.
     */
    @Override
    public void restart() {
        try {

            PreparedStatement stmt = Database.getInstance().getConnection().prepareStatement("UPDATE practice_content set right_answers=0 ,wrong_answers=0 "
                    + "where id_practice=?");
            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(TestingSession.class.getName()).log(Level.SEVERE, null, ex);
        }
        contentManager.refresh();
    }

    /**
     * Ze slovíček, ve kterých uživatel hodně chybovat vytvoří samostatný
     * slovník. Hranice bude stanovena na základě úspěšnostiu.
     *
     * @param vocabularies
     * @param user
     */
    @Override
    public void createDictionaryFromWorst(ObservableList<ITestingSession.SessionVocabulary> vocabularies, IUser user) {
        try {

            PreparedStatement stmt = Database.getInstance().getConnection().
                    prepareStatement("INSERT INTO dictionary(dictionary_name,id,first_lang,second_lang)"
                            + " VALUES(?,?,?,?)");

            stmt.setString(1, "Worst_from" + this.name);
            stmt.setInt(2, user.getId());
            stmt.setString(3, "MIXED");
            stmt.setString(3, "MIXED");
            stmt.executeUpdate();

        } catch (SQLException ex) {

        }

    }

    /**
     * Zkontroluje zda je zadané slovíčko správně
     *
     * @param vocabulary Aktuálně zkoušené slovíčko
     * @param enteredVsalue
     * @return
     */
    @Override
    public boolean checkAnswer(IDictionary.Vocabulary vocabulary, String enteredVsalue) {
        return false;
    }

    /**
     * Změní směr, kterým zkoušení probíhá
     */
    @Override
    public void changeDirection() {
        String translationOrigin = this.translation;
        switch (this.direction) {
            case LR:
                this.direction = app.content.PracticeDirection.RL;

                break;
            case RL:
                this.direction = app.content.PracticeDirection.LR;

                break;

        }
        this.translation = wordToKnow;
        this.wordToKnow = translationOrigin;
    }

    //################################# GETTERS, SETTERS #################################//
    /**
     * Vrací výsledky z této relace zkoušení. Tato metodá je volána pokud byla
     * relace zkoušení ukončena.
     *
     * @return
     */
    @Override
    public ObservableList<ITestingSession.SessionVocabulary> getResults() {
        ObservableList<ITestingSession.SessionVocabulary> result = FXCollections.observableArrayList();
        try {

            PreparedStatement stmt = Database.getInstance().getConnection().prepareStatement("select first_value,second_value,right_answers,wrong_answers from practice_content "
                    + "where id_practice=?");
            stmt.setInt(1, this.id);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Integer right, wrong;
                right = rs.getInt("right_answers");
                wrong = rs.getInt("wrong_answers");

                double successRate = (right / (wrong + right)) * 100;
                ITestingSession.SessionVocabulary vocabulary = new SessionVocabulary(rs.getString("first_value"), rs.getString("second_value"),
                        rs.getInt("right_answers"), rs.getInt("wrong_answers"), String.format("%.2f", successRate) + "%");
                result.add(vocabulary);
            }
        } catch (SQLException ex) {

        }
        return result;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getWordToKnow() {
        return wordToKnow;

    }

    @Override
    public String getTranslation() {
        return translation;
    }

    @Override
    public int getToDelete() {
        return toDelete;
    }

    @Override
    public PracticeDirection getDirection() {
        return this.direction;
    }

    @Override
    public String getSuccessRate() {
        double successRate = 0;
        try {
            PreparedStatement stmt = Database.getInstance().getConnection().prepareStatement(""
                    + "SELECT sum(right_answers),sum(wrong_answers)"
                    + " from practice_content where id_practice=?");
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                double right, wrong;
                right = rs.getInt("sum(right_answers)");
                wrong = rs.getInt("sum(wrong_answers)");

                successRate = (right / (wrong + right)) * 100;

            }
        } catch (SQLException ex) {
            Logger.getLogger(TestingSession.class.getName()).log(Level.SEVERE, null, ex);

        }
        return String.format("%.2f", successRate);
    }

    @Override
    public boolean verify(String answer) {
        System.out.println("Slovíčko:" + this.wordToKnow + "\nPřeklad:" + this.translation + "\nOdpověď:"
                + answer);
        PreparedStatement stmt = null;

        try {

            if (answer.trim().equals(this.translation.trim())) {

                stmt = Database.getInstance().getConnection().prepareStatement("UPDATE practice_content "
                        + "set right_answers=right_answers + 1 where id_practice=? "
                        + "and first_value=? and second_value=?  ");
                stmt.setInt(1, this.id);
                if (direction == PracticeDirection.LR) {
                    stmt.setString(2, wordToKnow);
                    stmt.setString(3, translation);
                } else {
                    stmt.setString(2, translation);
                    stmt.setString(3, wordToKnow);
                }

                stmt.executeUpdate();

                return true;
            } else {
                stmt = Database.getInstance().getConnection().prepareStatement("UPDATE practice_content "
                        + "set wrong_answers=wrong_answers + 1 where id_practice=? "
                        + "and first_value=? and second_value=?  ");
                stmt.setInt(1, this.id);
                if (direction == PracticeDirection.LR) {
                    stmt.setString(2, wordToKnow);
                    stmt.setString(3, translation);
                } else {
                    stmt.setString(2, translation);
                    stmt.setString(3, wordToKnow);
                }

                stmt.executeUpdate();
                return false;

            }
        } catch (SQLException ex) {
            Logger.getLogger(TestingSession.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
//################################# INNER CLASSES #################################//

    public class SessionVocabulary implements ITestingSession.SessionVocabulary {

        public String firstValue, secondValue, successRate;
        public Integer right, wrong;

        public SessionVocabulary(String firstValue, String secondValue, Integer right,
                Integer wrong, String successRate) {
            this.firstValue = firstValue;
            this.secondValue = secondValue;
            this.right = right;
            this.wrong = wrong;
            this.successRate = successRate;

        }

        @Override
        public String getFirstValue() {
            return firstValue;
        }

        @Override
        public String getSecondValue() {
            return secondValue;
        }

        @Override
        public Integer getRight() {
            return right;
        }

        @Override
        public Integer getWrong() {
            return wrong;
        }

        @Override
        public String getSuccessRate() {
            return successRate;
        }

    }
}
