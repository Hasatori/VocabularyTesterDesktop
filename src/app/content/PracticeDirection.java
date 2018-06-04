package app.content;

/**
 * Instnace {@code PracticeDirecetion} je výčtovým typem, který specifikuje
 * směry, ve kterých může probíhat zkoušení slovíček v relaci zkoušení.
 *
 * @author Oldřich Hradil
 */
public
        enum PracticeDirection {
    /**
     * LR=LeftToRight - zkoušení zleva doprava RL=RightToLeft - zkoušení zprava
     * doleva RND=Random - náhodné se bude přepínat mezi LR a RL
     */
    LR, RL;
}
