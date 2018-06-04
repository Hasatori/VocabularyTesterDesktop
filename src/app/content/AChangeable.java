package app.content;

import app.ContentManager;
import app.IDictionary;

/**
 * Instance {@code AChangeable} reprezentují objekty, které jsou proměnlivé,
 * neboli lze je mazat, editovat nebo vytvířet nové. Instance komunikuje s
 * databází. Také musí znát svého správce obsahu, které při jakékoliv změně
 * informuje a on znovu vykreslí data, která spravuje.
 *
 * @author Oldřich Hradil
 */
public abstract class AChangeable {

    // Správce obsahu. Instance jej musejí informovat při změně a on překreslí data, která spravuje
    final ContentManager contentManager;

    /**
     * Konstruktor AChangeable, definuje jednotlivé atributy
     *
     * @param contentManager správce obsahu příslušné instance AChangeable
     */
    public AChangeable(ContentManager contentManager) {

        this.contentManager = contentManager;
    }

    /**
     * Vymaže sama sebe.
     */
    abstract void deleteSelf();

    /**
     * Upraví své hodnoty
     *
     * @param values Seznam objektů, které budou měneny v rámci objektu.
     */
    abstract boolean tryEditSelf(Object... values);

}
