/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import javafx.collections.ObservableList;

/**
 *
 * @author admin
 */
public interface IDictionary {

    public ObservableList<Vocabulary> getVocabularies();

    public String getName();

    public Integer getId();

    public String getLanguages();

    public String getFirstLanguage();

    public String getSecondLanguage();

    public void deleteVocabulary(Vocabulary vocabulary);

    public boolean tryEditVocabulary(Vocabulary vocabulary, String firstValue, String secondValue);

    public boolean tryAddVocabulary(String firstValue, String secondValue);

    public void deleteSelf();

    public boolean tryEditSelf(Object... values);

    @Override
    public String toString();

    interface Vocabulary {

        public String getFirstValue();

        public String getSecondValue();
    }
}
