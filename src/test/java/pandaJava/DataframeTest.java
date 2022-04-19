package pandaJava;

import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataframeTest {

    private final Object[][] correctArray = generateCorrectArray();
    private final Object[][] incorrectArray = generateIncorrectArray();
    private final Dataframe d = new Dataframe(correctArray);

    @Test
    public void testValueFirstIndexOfFirstLine () {
        assertEquals(0, d.getLine(0).get(0));
    }

    @Test
    public void testValueFirstIndexOfFirstRow () {
        assertEquals(0, d.getRow("A").get(0));
    }

    @Test
    public void testSameValueFirstIndexRowAndLine () {
        assertEquals(d.getLine(0).get(0), d.getRow("A").get(0));
    }

    @Test
    public void testFirstLine () {
        List<Integer> l = new ArrayList<Integer>() {
            {
                add(0);
                add(1);
                add(2);
                add(3);
                add(4);
            }
        };
        Boolean ret = true;
        for(int i = 0; i < l.size(); i++) {
            if(!l.get(i).equals(d.getLine(0).get(i))) {
                ret = false;
            }
        }
        assertTrue(ret);
    }

    @Test
    public void testFirstRow () {
        List<Integer> l = new ArrayList<Integer>() {
            {
                add(0);
                add(5);
                add(10);
                add(15);
                add(20);
            }
        };
        Boolean ret = true;
        for(int i = 0; i < l.size(); i++) {
            if(!l.get(i).equals(d.getRow("A").get(i))) {
                ret = false;
            }
        }
        assertTrue(ret);
    }

    private Object[][] generateCorrectArray () {
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

    private Object[][] generateIncorrectArray () {
        int nb = 0;
        Object[][] test = new Object[5][5];
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