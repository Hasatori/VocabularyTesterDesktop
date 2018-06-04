package app.access;

import app.IDatabase;
import app.IUser;
import app.main.ErrorHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Instance s představuje uživatele, který se přihlašuje k databázi. Jedná se o
 * Singleton. Jednotlivé atributy se však mohou měnit, ale mohou být nastavovány
 * pouze sama sebou po úspešném přihlášení. K au
 *
 * @author Oldřich Hradil
 */
public class User implements IUser {

    // Identifikační číslo přihlášeného uživatele, je používáno při dotazování databáze
    private Integer id;
    // Jméno a emailová adresa přihlášeného uživatele
    private String name, email;

    /**
     * Konstruktor uživatele. Nastavuje základní identifikační informace. Pokud
     * není uživatel přihlášen mají všechny hodnotu null.
     *
     * @param id Identifikační číslo uživatele
     * @param name Jméno uživatele
     * @param email Email uživatele
     * @param type Typ uživatele
     */
    public User(Integer id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;

    }

    /**
     * Odhlásí uživatele, neboli nastavi identifikační atributy zpět na hodnotu
     * null.
     */
    @Override
    public void logout() {
        this.id = null;
        this.name = null;
        this.email = null;

    }

    /**
     * Převede atributy uživatele na string hodnotu.
     *
     * @return String reprezentace uživatele.
     */
    @Override
    public String toString() {
        return "\n################\n"
                + "ID:" + this.id + "\n"
                + "JMÉNO:" + this.name + "\n"
                + "EMAIL:" + this.email + "\n"
                + "\n################\n";
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    /**
     * Pokusí se přihlásit uživatele podle daného hesla a emailu. Tato metoda je
     * využívána pouze přihlašovacím oknem.
     *
     * @param email Emailová adresa zadáváná do přihlašovacího formuláře
     * @param password Heslo zadávané do přihlašovacího formuláře.
     * @return Pokud byla autorizace úspěšná vrací instanci uživatele, jinak
     * vrací null
     */
    @Override
    public User tryLogin(String email, String password) {
        Connection connection = Database.getInstance().getConnection();
        PreparedStatement stmt = null;

        try {

            stmt = connection.prepareStatement("SELECT id,password,name,email,active"
                    + " from client  where email=? LIMIT 1");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            String hashedPassword, type, name;
            Integer id, active;
            while (rs.next()) {
                hashedPassword = rs.getString("password");

                id = rs.getInt("id");

                active = rs.getInt("active");

                if (!BCrypt.checkpw(password, hashedPassword)) {

                    new ErrorHandler("Aurorizace", "Nesprávné heslo!");
                    return null;
                } else {
                    User user = new User(id, "Hasatori", email);
                    System.out.println("\nUživatel:" + user.toString()
                    );
                    return user;
                }

            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());

        }
        new ErrorHandler("Aurorizace", "Nesprávný email!");
        return null;
    }

}
