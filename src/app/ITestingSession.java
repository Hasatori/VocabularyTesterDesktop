/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import app.content.PracticeDirection;
import javafx.collections.ObservableList;

/**
 *
 * @author admin
 */
public interface ITestingSession {

    public void deleteSelf();

    public boolean tryEditSelf(Object... values);

    @Override
    public String toString();

    public void nexWord();

    public void restart();

    public void createDictionaryFromWorst(ObservableList<SessionVocabulary> vocabularies, IUser user);

    boolean checkAnswer(IDictionary.Vocabulary vocabulary, String enteredVsalue);

    public void changeDirection();

    public ObservableList<SessionVocabulary> getResults();

    public String getName();

    public String getWordToKnow();

    public String getTranslation();

    public int getToDelete();

    public PracticeDirection getDirection();

    public String getSuccessRate();

    public boolean verify(String answer);

    interface SessionVocabulary {

        public String getFirstValue();

        public String getSecondValue();

        public Integer getRight();

        public Integer getWrong();

        public String getSuccessRate();
    }
}
