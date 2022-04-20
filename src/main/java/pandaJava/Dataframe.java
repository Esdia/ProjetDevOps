package pandaJava;

import java.io.*;
import java.util.*;

public class Dataframe {

    private Map<Integer, List<Object>> frameLines;
    private Map<String, List<Object>> frameRows;
    private Map<String, Class> rowType;

    public Dataframe (Object[][] array) {
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
                        throw new IllegalArgumentException();
                    else {
                        this.frameLines.get(i).add(array[i][j]);
                        this.frameRows.get(label).add(array[i][j]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO verifier que le fichier est correct ? : au moins 3 lignes
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
            int i = 0;
            int lineIndex = 0;
            while ((st = br.readLine()) != null) {
                if (i == 0) {
                    types = st.split(";");
                    i++;
                }

                else if (i == 1) {
                    labels = st.split(";");
                    for(int j = 0; j < labels.length; j++) {
                        this.frameRows.put(labels[j], new ArrayList<>());
                        this.rowType.put(labels[j], Class.forName("java.lang."+types[j]));
                    }
                    i++;
                }

                else {
                    this.frameLines.put(lineIndex, new ArrayList<>());
                    String[] line = st.split(";");
                    for(int j = 0; j < line.length; j++) {
                        Class c = this.rowType.get(labels[j]); //On récupère la classe de la colonne
                        String toBeCasted = line[j]; //Le string à caster vers la classe de la colonne
                        Object elem = c.cast(toBeCasted); //Cast de String vers classe de la colonne
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

    public Map<String, Class> getRowType () {
        return this.rowType;
    }

    public void showAllDataFrame(){
        int i = 0;
        int j = 0;
        System.out.printf("%1s"," ");//For the first empty case
        while(getLine(0).size() > j){
            System.out.printf("%8s","col"+(j+1));//Print column label (not in the dataframe)
            j++;
        }
        System.out.println();
        //Parcours des lignes du dataframe
        while(this.frameLines.size() > i) {
            List<Object> line = getLine(i);
            System.out.printf("%1s",i);//Print column indices (not in the dataframe)
            for (Object elem : line) {
                System.out.printf("%8s",elem);//Print dataframe elements
            }
            System.out.println();
            i++;
        }
    }

    public void showFirstLines(int nbLines) throws Exception {
        if(nbLines > this.frameLines.size()){
            throw new Exception("Erreur : Il y a seulement : " + this.frameLines.size() + "lignes dans ce dataframe");
        }else if(nbLines<=0){
            throw new Exception("Veuillez choisir un nombre de lignes supérieur à 0");
        }
        int i = 0;
        int j = 0;
        System.out.printf("%1s"," ");//For the first empty case
        while(getLine(0).size() > j){
            System.out.printf("%8s","col"+(j+1));//Print column label (not in the dataframe)
            j++;
        }
        System.out.println();
        //Parcours des lignes du dataframe
        while(nbLines > i) {
            List<Object> line = getLine(i);
            System.out.printf("%1s",i);//Print column indices (not in the dataframe)
            for (Object elem : line) {
                System.out.printf("%8s",elem);//Print dataframe elements
            }
            System.out.println();
            i++;
        }
    }

    public void showLastLines(int nbLines) throws Exception {
        if(nbLines > this.frameLines.size()){
            throw new Exception("Erreur : Il y a seulement : " + this.frameLines.size() + " lignes dans ce dataframe");
        }else if(nbLines<=0){
            throw new Exception("Veuillez choisir un nombre de lignes supérieur à 0");
        }
        int i = this.frameLines.size()-nbLines;
        int j = 0;
        System.out.printf("%1s"," ");//For the first empty case
        while(getLine(0).size() > j){
            System.out.printf("%8s","col"+(j+1));//Print column label (not in the dataframe)
            j++;
        }
        System.out.println();
        //Parcours des lignes du dataframe
        while(this.frameLines.size() > i) {
            List<Object> line = getLine(i);
            System.out.printf("%1s",i);//Print column indices (not in the dataframe)
            for (Object elem : line) {
                System.out.printf("%8s",elem);//Print dataframe elements
            }
            System.out.println();
            i++;
        }
    }
}
