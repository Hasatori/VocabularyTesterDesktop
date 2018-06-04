package app;



import javafx.scene.Parent;

/**
 * Interface specifikující potřebné metody, které by měl splňovat správce
 * obsahu. Objekty, které potřebují například přepínat mezi jednotlivými
 * scénami, které se mají vykreslovat do příslušného okna budou komunikovat
 * pouze s instnacemi toho interfacu. Instance toho interfacu spravují obsah,
 * který jim byl svěřen a objekty, kterými jsou spravovány se zavazují
 * informovat svého správce pokud došlo k jakékoliv změně a správce obsah
 * překreslí.
 *
 * @author Oldřich Hradil
 */
public
        interface ContentManager {

    /**
     * Metoda, která znovu načte datá jež správce spravuje.
     *
     *
     */
    void refresh();

    /**
     * Získává zdroj, do kterého jsou načítána data, která správce spravuje.
     *
     * @return Odkaz na objekt, do nejž budou načtena příslušná data. V rámci
     * zdroje samotného také vyhledáváme jednotlivé jeho části a ty jsou podle
     * potřeby naplňovány také.
     */
    Parent getSource();

    boolean tryCreateAChangeable(Object... values);

    boolean aChangeableExists(String name);
}
