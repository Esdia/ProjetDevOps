package pandaJava;

public class MistypedRowException extends Exception {

    public MistypedRowException (String errorMessage, Throwable exception) {
        super(errorMessage, exception);
    }

    public MistypedRowException () {
        super();
    }
}
