package pandaJava;


/**
 * Classe pour tester l'affichage "on peut la retirer après""
 */
public class Main {

    /**
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Test affichage d'un dataframe :");
        Dataframe myDataFrame = new Dataframe(generateCorrectArray());
        Dataframe myDataFrame2 = new Dataframe(generateCorrectArray2());
        myDataFrame.showAllDataFrame();
        //myDataFrame.showFirstLines(1);
        //myDataFrame.showLastLines(5);
        myDataFrame2.showAllDataFrame();
    }

    private static Object[][] generateCorrectArray () {
        int nb = 0;
        Object[][] test = new Object[5][5];
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                test[i][j] = nb;
                nb++;
            }
        }
        return test;
    }

    private static Object[][] generateCorrectArray2 () {
        int nb = 0;
        Object[][] test = new Object[5][5];
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                test[i][j] = (char)('A'+(nb % 26));
                nb++;
            }
        }
        return test;
    }

}
