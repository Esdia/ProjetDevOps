package pandaJava;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DataframeTest {

    private final Object[][] correctArray = generateCorrectArray();
    private final Object[][] incorrectArray = generateIncorrectArray();
    private static String path;
    private final Dataframe d = new Dataframe(correctArray);
    private final Dataframe d2 = new Dataframe(path);
    private static final int xSize = 5;
    private static final int ySize = 5;

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
    public void testCorrectGetSubLines () {
        int index1 = 0;
        int index2 = 2;
        int j = 0;
        Map<Integer, List<Object>> lines = d.getSubLines(index1, index2);
        for(int i = index1; i <= index2; i++) {
            for(Object e : lines.get(i)) {
                assertEquals(e, d.getLine(i).get(j));
                j++;
            }
            j = 0;
        }
    }

    @Test
    public void testIncorrectGetSubLines1 () {
        int index1 = 0;
        int index2 = 5;
        assertThrows(IllegalArgumentException.class, () -> d.getSubLines(index1, index2));
    }

    @Test
    public void testIncorrectGetSubLines2 () {
        int index1 = 3;
        int index2 = 1;
        assertThrows(IllegalArgumentException.class, () -> d.getSubLines(index1, index2));
    }

    @Test
    public void testIncorrectGetSubLines3 () {
        int index1 = -6;
        int index2 = 3;
        assertThrows(IllegalArgumentException.class, () -> d.getSubLines(index1, index2));
    }

    @Test
    public void testCorrectGetSubLinesBool () {
        int j = 0;
        Map<Integer, List<Object>> lines = d.getSubLines(true, false, true, false, true);
        for(int i = 0; i < lines.size(); i++) {
            for(Object e : lines.get(i)) {
                assertEquals(e, d.getLine(i*2).get(j));
                j++;
            }
            j = 0;
        }
    }

    @Test
    public void testGetSubLinesBoolEmpty () {
        Map<Integer, List<Object>> lines = d.getSubLines(false, false, false, false, false);
        assertEquals(0, lines.size());
    }

    @Test
    public void testIncorrectGetSubLinesBool () {
        assertThrows(IllegalArgumentException.class, () -> d.getSubLines(true));
        assertThrows(IllegalArgumentException.class, () -> d.getSubLines(true, true, false, true, true, true, false));
    }

    @Test
    public void testGetValue () {
        assertEquals("Lasalle", d2.getValue(1, "nom"));
        assertEquals(13, d2.getValue(1, "age"));
        assertEquals(11, d.getValue(2, "B"));
        assertEquals(0, d.getValue(0, "A"));
    }

    @Test
    public void testGetSubRows () {
        Map<String, List<Object>> rows1 = d2.getSubRows("nom");
        assertEquals("Fourier", rows1.get("nom").get(0));
        assertEquals("Lasalle", rows1.get("nom").get(1));
        Map<String, List<Object>> rows2 = d2.getSubRows("nom", "age");
        assertEquals(13, rows2.get("age").get(1));
        assertEquals(18, rows2.get("age").get(0));
        assertEquals("Lasalle", rows2.get("nom").get(1));
        assertEquals("Fourier", rows2.get("nom").get(0));
    }

    @Test
    public void testGetSubLinesLambda () {
        Map<Integer, List<Object>> lines1 = d2.getSubLines("age", (x) -> x < 18);
        assertEquals("Lasalle", lines1.get(0).get(1));
        Map<Integer, List<Object>> lines2 = d2.getSubLines("age", (x) -> x == 13);
        assertEquals(false, lines2.get(0).get(2));
        Map<Integer, List<Object>> lines3 = d2.getSubLines("age", (x) -> x > 10);
        assertEquals(13, lines3.get(1).get(0));
    }

    @Test
    public void testIncorrectGetSubLinesLambda () {
        assertThrows(IllegalArgumentException.class, () -> d2.getSubLines("nom", (x) -> x < 18));
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

    private Object[][] generateCorrectArray () {
        int nb = 0;
        Object[][] test = new Object[xSize][ySize];
        for(int i = 0; i < xSize; i++) {
            for(int j = 0; j < ySize; j++) {
                test[i][j] = nb;
                nb++;
            }
        }
        return test;
    }

    private Object[][] generateIncorrectArray () {
        int nb = 0;
        Object[][] test = new Object[xSize][ySize];
        for(int i = 0; i < xSize; i++) {
            for(int j = 0; j < ySize; j++) {
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