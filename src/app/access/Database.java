package app.access;

import app.IDatabase;
import app.main.ErrorHandler;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.ini4j.Ini;
import org.ini4j.Profile;

/**
 * Třída sloužící pro připojení k databázi. Jedná se o Singleton, který
 * využívají všechny objekty, které se potřebujíc spojit s databází.
 *
 * @author Oldřich Hradil
 */
public
        class Database implements IDatabase {

    /**
     * Připojení k databázi
     */
    private
            Connection connection;

    /**
     * Jediná instance této třídy
     */
    private static final
            Database DATABASE = new Database();

    /**
     * Soukromý konstruktor.
     */
    private
            Database() {

    }

    /**
     * Získává jedinou instanci této třídy
     *
     * @return Jediná instance této třidy
     */
    public static
            Database getInstance() {
        return DATABASE;
    }

    /**
     * Připojí se k databázi. Potřebné přihlašovací údaje získává z příslušného
     * konfiguračního souboru.
     */
    private
            void connect() {
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//        }
//        catch (ClassNotFoundException e) {
//            System.out.println(e.getMessage());
//        }
        try {
            Ini configFile = new Ini(this.getClass().getResourceAsStream("config.ini"));
            String hostname = configFile.get("database", "hostname");
            String portnumber = configFile.get("database", "portnumber");
            String username = configFile.get("database", "username");
            String password = configFile.get("database", "password");
            String dbname = configFile.get("database", "dbname");

            connection = DriverManager
                    .getConnection("jdbc:mysql://" + hostname /*+ ":" + portnumber*/ + "/" + dbname + "?user=" + username + "&password=" + password + "&useUnicode=true&characterEncoding=UTF-8");

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        if (connection != null) {
            System.out.println("####################################"
                    + "\nSPOJENÍ S DATABÁZÍ NAVÁZÁNO\n"
                    + "####################################");

        }
        else {

            new ErrorHandler("Nastala chyba", "Chyba při spojení s databází!");
            System.out.println("####################################"
                    + "\nSPOJENÍ NEBYLO NAVÁZÁNO!\n"
                    + "####################################");

        }

    }

    /**
     * Získává připojení k databázi. Metoda pokračuje dokud připojení k databázi
     * nezíská.
     *
     * @return Připojení k databázi
     */
    @Override
    public
            Connection getConnection() {
        if (connection == null) {
            connect();
        }
        return this.connection;
    }

    /**
     * Odpojí se od databáze, neboli zničí atribut připojení.
     */
    @Override
    public
            void disconnect() {
        this.connection = null;

    }

}
