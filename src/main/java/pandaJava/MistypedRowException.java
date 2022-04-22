package pandaJava;

public class MistypedRowException extends Exception {

    /**
     * Constructeur en fonction d'un message d'erreur et d'une erreur Throwable.
     * @param errorMessage
     * @param exception
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
