package pandaJava;

import java.io.*;
import java.util.*;

public class Dataframe {

    private Map<Integer, List<Object>> frameLines;
    private Map<String, List<Object>> frameRows;
    private Map<String, Class> rowType;

    public Dataframe (Object[][] array) throws MistypedRowException {
        try {
            String label = "";
            this.frameRows = new HashMap<>();
            this.frameLines = new HashMap<>();
            this.rowType = new HashMap<>();
            //Parcours des colonnes
            for(int j = 0; j < array[0].length; j++) {
                int value = j;
                StringBuilder sb = new StringBuilder(String.valueOf((char)('A'+(value % 26))));
                //Boucle while générant des labels pour les colonnes en suivant l'ordre alphabétique.
                while((value = (value/26-1)) >= 0)
                    sb.append((char)('A'+(value % 26)));
                label = sb.reverse().toString();
                this.frameRows.put(label, new ArrayList<>());
                Object firstElemRow = array[0][j];
                this.rowType.put(label, Class.forName(firstElemRow.getClass().getName()));
                //Parcours des lignes
                for(int i = 0; i < array.length; i++) {
                    if(!this.frameLines.containsKey(i))
                        this.frameLines.put(i, new ArrayList<>());
                    Object e = array[i][j];
                    if(!(e.getClass().getName().equals(this.rowType.get(label).getName())))
                        throw new MistypedRowException();
                    else {
                        this.frameLines.get(i).add(array[i][j]);
                        this.frameRows.get(label).add(array[i][j]);
                    }
                }
            }
        } catch (MistypedRowException | ClassNotFoundException e) {
            System.out.println(e.getClass().getName());
            if(e.getClass().getName().equals("pandaJava.MistypedRowException"))
                throw new MistypedRowException("Mistyped array argument. A row has a unique type.", e);
            else
                e.printStackTrace();
        }
    }

    /**
     * Create a DataFrame from a CSV file. We consider the CSV file to have only 3 types : boolean, String and Integer.
     * @param path
     */
    public Dataframe (String path) {
        try {
            this.frameRows = new HashMap<>();
            this.frameLines = new HashMap<>();
            this.rowType = new HashMap<>();
            File file = new File(path);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st = "";
            String[] types = new String[0];
            String[] labels = new String[0];
            int i = 0; //Sert à différencier les 2 premières lignes des suivantes.
            int lineIndex = 0;
            while ((st = br.readLine()) != null) {
                //Première ligne (types)
                if (i == 0) {
                    types = st.split(";");
                    i++;
                }
                //Deuxième ligne (labels)
                else if (i == 1) {
                    labels = st.split(";");
                    for(int j = 0; j < labels.length; j++) {
                        this.frameRows.put(labels[j], new ArrayList<>());
                        this.rowType.put(labels[j], Class.forName("java.lang."+types[j]));
                    }
                    i++;
                }

                //Lignes après la deuxième (valeurs)
                else {
                    this.frameLines.put(lineIndex, new ArrayList<>());
                    String[] line = st.split(";");
                    for(int j = 0; j < line.length; j++) {
                        Class c = this.rowType.get(labels[j]); //On récupère la classe de la colonne
                        Object elem;
                        //On cast l'élément en String, en Int ou en booléen en fonction du type de la colonne.
                        if(c.getName().equals("java.lang.Integer"))
                            elem = Integer.valueOf(line[j]);
                        else
                            if (c.getName().equals("java.lang.Boolean"))
                                elem = Boolean.valueOf(line[j]);
                        else
                            elem = line[j];
                        this.frameLines.get(lineIndex).add(elem);
                        this.frameRows.get(labels[j]).add(elem);
                    }
                    lineIndex++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Object> getRow (String label) {
        return this.frameRows.get(label);
    }

    public List<Object> getLine (int index) {
        return this.frameLines.get(index);
    }

    public Class getRowType (String label) {
        return this.rowType.get(label);
    }
}
