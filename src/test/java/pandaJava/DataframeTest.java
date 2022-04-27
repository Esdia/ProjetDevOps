package pandaJava;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
        assertEquals(0, d.getColumn("A").get(0));
    }

    @Test
    public void testSameValueFirstIndexRowAndLine () {
        assertEquals(d.getLine(0).get(0), d.getColumn("A").get(0));
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
    public void testFirstColumn() {
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
            if(!l.get(i).equals(d.getColumn("A").get(i))) {
                ret = false;
            }
        }
        assertTrue(ret);
    }

    @Test
    public void testIntegerColumnC2() {
        String label = "age";
        for (Object e : d2.getColumn(label)) {
            assertTrue(e.getClass().getName().equals("java.lang.Integer") && d2.getColumnType(label).getName().equals("java.lang.Integer"));
        }
    }

    @Test
    public void testStringColumnC2() {
        String label = "nom";
        for (Object e : d2.getColumn(label)) {
            assertTrue(e.getClass().getName().equals("java.lang.String") && d2.getColumnType(label).getName().equals("java.lang.String"));
        }
    }

    @Test
    public void testBooleanColumnC2() {
        String label = "adulte";
        for (Object e : d2.getColumn(label)) {
            assertTrue(e.getClass().getName().equals("java.lang.Boolean") && d2.getColumnType(label).getName().equals("java.lang.Boolean"));
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
    public void testGetSubColumns() {
        Map<String, List<Object>> rows1 = d2.getSubColumns("nom");
        assertEquals("Fourier", rows1.get("nom").get(0));
        assertEquals("Lasalle", rows1.get("nom").get(1));
        Map<String, List<Object>> rows2 = d2.getSubColumns("nom", "age");
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
        Object[][] test = new Object[xSize][ySize];
        for(int i = 0; i < xSize; i++) {
            for(int j = 0; j < ySize; j++) {
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

    @Test
    void testLoc() {
        Dataframe dataframe = d2.loc(0);
        assertEquals(18, dataframe.getValue(0, "age"));
        assertEquals("Fourier", dataframe.getValue(0, "nom"));
        assertTrue((Boolean) dataframe.getValue(0, "adulte"));

        dataframe = d2.loc(1);
        assertEquals(13, dataframe.getValue(0, "age"));
        assertEquals("Lasalle", dataframe.getValue(0, "nom"));
        assertFalse((Boolean) dataframe.getValue(0, "adulte"));
    }

    @Test
    void testLoc1() {
        Dataframe dataframe = d2.loc(0, 1);
        assertEquals(18, dataframe.getValue(0, "age"));
        assertEquals("Fourier", dataframe.getValue(0, "nom"));
        assertTrue((Boolean) dataframe.getValue(0, "adulte"));

        assertEquals(13, dataframe.getValue(1, "age"));
        assertEquals("Lasalle", dataframe.getValue(1, "nom"));
        assertFalse((Boolean) dataframe.getValue(1, "adulte"));
    }

    @Test
    void testLoc2() {
        Dataframe dataframe = d2.loc("age");
        assertEquals(18, dataframe.getValue(0, "age"));
        assertEquals(13, dataframe.getValue(1, "age"));
    }

    @Test
    void testLoc3() {
        Dataframe dataframe = d2.loc("adulte", "nom");
        assertTrue((Boolean) dataframe.getValue(0, "adulte"));
        assertEquals("Fourier", dataframe.getValue(0, "nom"));

        assertFalse((Boolean) dataframe.getValue(1, "adulte"));
        assertEquals("Lasalle", dataframe.getValue(1, "nom"));
    }

    @Test
    void testLoc4() {
        Dataframe dataframe = d2.loc(1, "adulte");
        assertFalse((Boolean) dataframe.getValue(0, "adulte"));
    }

    @Test
    void testLoc5() {
        Dataframe dataframe = d2.loc(false, true);
        assertEquals(13, dataframe.getValue(0, "age"));
        assertEquals("Lasalle", dataframe.getValue(0, "nom"));
        assertFalse((Boolean) dataframe.getValue(0, "adulte"));

    }

    @Test
    void testLoc6() {
        Dataframe dataframe = d2.loc("age", x -> x >= 18);
        assertEquals(18, dataframe.getValue(0, "age"));
        assertEquals("Fourier", dataframe.getValue(0, "nom"));
        assertTrue((Boolean) dataframe.getValue(0, "adulte"));

        dataframe = d2.loc("age", x -> x < 18);
        assertEquals(13, dataframe.getValue(0, "age"));
        assertEquals("Lasalle", dataframe.getValue(0, "nom"));
        assertFalse((Boolean) dataframe.getValue(0, "adulte"));
    }
}