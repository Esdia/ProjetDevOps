package pandaJava;

/**
 * Interface à implémenter pour évaluer une valeur dans une expression booléenne.
 */
public interface Evaluate {
    /**
     * Sert à tester une valeur passée en paramètre dans une expression booléenn.
     * @param value Valeur que l'on veut évaluer.
     * @return Résultat de l'évaluation booléenne.
     */
    boolean evaluate(int value);
}
