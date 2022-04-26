package pandaJava;

/**
 * Exception quand un tableau à deux dimensions ne peut être conveti en DataFrame.
 */
public class MistypedRowException extends Exception {

    /**
     * Constructeur en fonction d'un message d'erreur et d'une erreur Throwable.
     * @param errorMessage Message affiché quand l'exception est levée.
     * @param exception Exception récupérée.
     */
    public MistypedRowException (String errorMessage, Throwable exception) {
        super(errorMessage, exception);
    }

    /**
     * Constructeur par défaut.
     */
    public MistypedRowException () {
        super();
    }
}
