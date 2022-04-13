package pandaJava;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataframeTest {

    private Dataframe dataframe;
    private final Object[][] correctArray = generateCorrectArray();
    private final Object[][] incorrectArray = generateIncorrectArray();


    @Test
    public void test_incorrectArrayForConstructor () {

    }

    @Test (expected = IndexOutOfBoundsException.class)
    public boolean test () {

    }

    private Object[][] generateCorrectArray () {
        int nb = 0;
        Object[][] test = new Object[3][3];
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                test[i][j] = nb;
                nb++;
            }
        }
        return test;
    }

    private Object[][] generateIncorrectArray () {
        int nb = 0;
        Object[][] test = new Object[3][3];
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                if(i == 0 && j==0)
                    test[i][j] = "intru";
                else
                    test[i][j] = nb;
                nb++;
            }
        }
        return test;
    }





}