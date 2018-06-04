package app.content;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Instance {@code Language} představuje výčtový typ specifikující jazyky, které
 * mohou být používány v aplikaci. Může se tak jednat jak o jazyk aplikace
 * samotné, nebo jazyky úžívané ve slovnících.
 *
 * @author Oldřich Hradil
 */
public
        enum Language {
    BRITSKÁ_ANGLIČTINA, AMERICKÁ_ANGLIČTINA, ČEŠTINA, SLOVENŠTINA, NĚMČINA, RUŠTINA, NONE;

    /**
     * Vrací jazyk odpovádající zadanému stringu
     *
     * @param language String podoba jazyku
     * @return Hodnota jazyka ve výčtovém typu
     */
    public static
            Language getLang(String language) {
        switch (language.toLowerCase()) {
            case "angličtina":
                return BRITSKÁ_ANGLIČTINA;
            case "angličtina (spojené státy)":
                return AMERICKÁ_ANGLIČTINA;
            case "čeština":
                return ČEŠTINA;

            default:
                return NONE;
        }

    }

    /**
     *
     *
     * @return Seznam hodnot tohoto výčtové typu
     */
    public static
            ObservableList<String> getLanguages() {
        ObservableList<String> result = FXCollections.observableArrayList();
        result.add("angličtina");
        result.add("angličtina (spojené státy)");
        result.add("čeština");

        return result;
    }
}
