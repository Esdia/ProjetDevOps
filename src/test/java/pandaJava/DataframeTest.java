package pandaJava;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataframeTest {

    private final Object[][] correctArray = generateCorrectArray();
    private final Object[][] incorrectArray = generateIncorrectArray();
    private static String path;
    private final Dataframe d = new Dataframe(correctArray);
    private final Dataframe d2 = new Dataframe(path);


    private DataframeTest() throws MistypedRowException {}

    @BeforeAll
    public static void createCSVTestFile () {
        path = "./src/test/resources/CSVTest.txt";
        try {
            File myObj = new File(path);

            if (myObj.createNewFile()) {
                FileWriter myWriter = new FileWriter(path);
                myWriter.write("Integer;String;Boolean\n" +
                        "age;nom;adulte\n" +
                        "18;Fourier;true\n" +
                        "13;Lasalle;false\n");
                myWriter.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
    public void testMisTypedRowException1 () {
        assertThrows(MistypedRowException.class, () -> new Dataframe(this.incorrectArray));
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

    @Test
    public void testIntegerRowC2 () {
        String label = "age";
        for (Object e : d2.getRow(label)) {
            assertTrue(e.getClass().getName().equals("java.lang.Integer") && d2.getRowType(label).getName().equals("java.lang.Integer"));
        }
    }

    @Test
    public void testStringRowC2 () {
        String label = "nom";
        for (Object e : d2.getRow(label)) {
            assertTrue(e.getClass().getName().equals("java.lang.String") && d2.getRowType(label).getName().equals("java.lang.String"));
        }
    }

    @Test
    public void testBooleanRowC2 () {
        String label = "adulte";
        for (Object e : d2.getRow(label)) {
            assertTrue(e.getClass().getName().equals("java.lang.Boolean") && d2.getRowType(label).getName().equals("java.lang.Boolean"));
        }
    }

    @Test
    public void testValuesLineC2 () {
        int age = 18;
        String nom = "Fourier";
        boolean adulte = true;
        for(int i = 0; i < d2.getLine(0).size(); i++) {
            if(i == 1)
                assertTrue(d2.getLine(0).get(0).equals(age));
            if(i == 2)
                assertTrue(d2.getLine(0).get(1).equals(nom));
            else
                assertTrue(d2.getLine(0).get(2).equals(adulte));
        }
        assertTrue(d2.getLine(0).size() == 3);
    }


    @Test
    public void testToString() throws MistypedRowException {
        String expected = "        A       B       C       D       E\n" +
                "0       0       1       2       3       4\n" +
                "1       5       6       7       8       9\n" +
                "2      10      11      12      13      14\n" +
                "3      15      16      17      18      19\n" +
                "4      20      21      22      23      24\n";
        Dataframe myDataFrame = new Dataframe(generateCorrectArray());
        assertEquals(myDataFrame.toString(), expected);
    }

    @Test
    public void testToStringFirstLines() throws Exception {
        String expected = "        A       B       C       D       E\n" +
                "0       0       1       2       3       4\n" +
                "1       5       6       7       8       9\n";
        Dataframe myDataFrame = new Dataframe(generateCorrectArray());
        assertEquals(myDataFrame.toStringFirstLines(2), expected);
    }

    @Test
    public void testToStringFirstLinesErrorArgumentTooBig() throws Exception {
        Dataframe myDataFrame = new Dataframe(generateCorrectArray());
        assertThrows(IllegalArgumentException.class, () -> myDataFrame.toStringFirstLines(6));
    }

    @Test
    public void testToStringFirstLinesErrorArgumentBelowOne() throws Exception {
        Dataframe myDataFrame = new Dataframe(generateCorrectArray());
        assertThrows(IllegalArgumentException.class, () -> myDataFrame.toStringFirstLines(-1));
    }

    @Test
    public void testToStringLastLines() throws Exception {
        String expected = "        A       B       C       D       E\n" +
                "2      10      11      12      13      14\n" +
                "3      15      16      17      18      19\n" +
                "4      20      21      22      23      24\n";
        Dataframe myDataFrame = new Dataframe(generateCorrectArray());
        assertEquals(myDataFrame.toStringLastLines(3), expected);
    }

    @Test
    public void testToStringLastLinesErrorArgumentTooBig() throws Exception {
        Dataframe myDataFrame = new Dataframe(generateCorrectArray());
        assertThrows(IllegalArgumentException.class, () -> myDataFrame.toStringLastLines(45));
    }

    @Test
    public void testToStringLastLinesErrorArgumentBelowOne() throws Exception {
        Dataframe myDataFrame = new Dataframe(generateCorrectArray());
        assertThrows(IllegalArgumentException.class, () -> myDataFrame.toStringLastLines(0));
    }

    @Test
    public void testLabelIsCorrect() throws Exception {
        Dataframe myDataFrame = new Dataframe(generateCorrectArray());
        assertEquals(myDataFrame.labelIsCorrect("A"),true);
    }

    @Test
    public void testLabelIsNotCorrect() throws Exception {
        Dataframe myDataFrame = new Dataframe(generateCorrectArray());
        assertThrows(IllegalArgumentException.class, () -> myDataFrame.labelIsCorrect("1"));
    }


    @Test
    public void testDataFrameMeanInteger() throws Exception {
        Dataframe myDataFrame = new Dataframe(generateCorrectArray());
        assertEquals(myDataFrame.dataframeMean("A"),10.0f);
    }


    @Test
    public void testDataFrameMeanString() throws Exception {
        Dataframe myDataFrame = new Dataframe(generateCorrectArrayString());
        assertThrows(IllegalArgumentException.class, () -> myDataFrame.dataframeMean("A"));
    }


    @Test
    public void testDataFrameMinInteger() throws Exception {
        Dataframe myDataFrame = new Dataframe(generateCorrectArray());
        assertEquals(myDataFrame.dataframeMin("A"),0.0f);
    }

    @Test
    public void testDataFrameMinString() throws Exception {
        Dataframe myDataFrame = new Dataframe(generateCorrectArrayString());
        assertEquals(myDataFrame.dataframeMin("A"),"AA");
    }

    @Test
    public void testDataFrameMinBool() throws Exception {
        Dataframe myDataFrame = new Dataframe(generateCorrectArrayBool());
        assertThrows(IllegalArgumentException.class, () -> myDataFrame.dataframeMean("A"));
    }


    @Test
    public void testDataFrameMaxInteger() throws Exception {
        Dataframe myDataFrame = new Dataframe(generateCorrectArray());
        assertEquals(myDataFrame.dataframeMax("A"),20.0f);
    }

    @Test
    public void testDataFrameMaxString() throws Exception {
        Dataframe myDataFrame = new Dataframe(generateCorrectArrayString());
        assertEquals(myDataFrame.dataframeMax("A"),"EA");
    }

    @Test
    public void testDataFrameMaxBool() throws Exception {
        Dataframe myDataFrame = new Dataframe(generateCorrectArrayBool());
        assertThrows(IllegalArgumentException.class, () -> myDataFrame.dataframeMax("A"));
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

    private Object[][] generateCorrectArrayString () {
        String nb;
        Object[][] test = new Object[5][5];

        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                nb = String.valueOf((char) ('A' + (i % 26))) + (char) ('A' + (j % 26));
                test[i][j] = nb;
            }
        }
        return test;
    }

    private Object[][] generateCorrectArrayChar () {
        char nb;
        Object[][] test = new Object[5][5];

        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                nb = (char) ('A' + (i % 26));
                test[i][j] = nb;
            }
        }
        return test;
    }

    private Object[][] generateCorrectArrayBool () {
        boolean nb;
        Object[][] test = new Object[5][5];

        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                nb = (i % 2 == 0);
                test[i][j] = nb;
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