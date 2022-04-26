package pandaJava;

import java.io.*;
import java.util.*;

/**
 * Classe DataFrame représentant des données sous formes de tableaux où les colonnes sont identifiés par un label et ne contiennet qu'un type d'objet.
 * Les lignes peuvent contenir plusieurs types d'objets et sont indexées par un entier.
 */
public class Dataframe {

    private Map<Integer, List<Object>> frameLines;
    private Map<String, List<Object>> frameRows;
    private Map<String, Class> rowType;
    private Map<String, Integer> indexLabel;

    /**
     * Création d'un DataFrame à partir d'un tableau simple à deux dimensions.
     * @param array Tableau d'objets à deux dimensions pouvant être convertis en dataFram. Les colonnes doivent donc avoir des types d'objet constant.
     * @throws MistypedRowException Quand le tableau rentré en argument est non convetible en dataFrame.
     */
    public Dataframe (Object[][] array) throws MistypedRowException {
        try {
            String label = "";
            this.frameRows = new HashMap<>();
            this.frameLines = new HashMap<>();
            this.rowType = new HashMap<>();
            this.indexLabel = new HashMap<>();
            //Parcours des colonnes
            for(int j = 0; j < array[0].length; j++) {
                int value = j;
                StringBuilder sb = new StringBuilder(String.valueOf((char)('A'+(value % 26))));
                //Boucle while générant des labels pour les colonnes en suivant l'ordre alphabétique.
                while((value = (value/26-1)) >= 0)
                    sb.append((char)('A'+(value % 26)));
                label = sb.reverse().toString();
                this.frameRows.put(label, new ArrayList<>());
                this.indexLabel.put(label, j);
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
            if(e.getClass().getName().equals("pandaJava.MistypedRowException"))
                throw new MistypedRowException("Mistyped array argument. A row has a unique type.", e);
            else
                e.printStackTrace();
        }
    }

    /**
     * Création d'un DataFrame à partir d'un fichier CSV. On considère qu'il ne peut y avoir que 3 types dans le fichier CSV : Integer, boolean et String.
     * @param path Chemin vers le fichier CSV à convertir en DataFrame.
     */
    public Dataframe (String path) {
        try {
            this.frameRows = new HashMap<>();
            this.frameLines = new HashMap<>();
            this.rowType = new HashMap<>();
            this.indexLabel = new HashMap<>();
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
                        this.indexLabel.put(labels[j], j);
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

    /**
     * Renvoie la valeur d'une cellule du DataFrame à un index et label donnés.
     * @param index Ligne de la cellule.
     * @param label Colonne de la cellule.
     * @return valeur de la cellule.
     */
    public Object getValue (int index, String label) {
        return this.frameLines.get(index).get(this.indexLabel.get(label));
    }

    /**
     * Renvoie la liste d'objets d'une colonne à partir de son label.
     * @param label Label de la colonne.
     * @return une liste d'objets composant le colonne sélectionnée.
     */
    public List<Object> getRow (String label) {
        return this.frameRows.get(label);
    }

    /**
     * Renvoie la liste d'objets d'une ligne à partir de son index.
     * @param index Index d'une ligne.
     * @return la liste d'objets composant la ligne sélectionnée.
     */
    public List<Object> getLine (int index) {
        return this.frameLines.get(index);
    }

    /**
     * Renvoie la classe des objets d'une colonne à partir de son label.
     * @param label Label d'une colonne.
     * @return la classe à laquelle appartiennent les objets de la colonne.
     */
    public Class getRowType (String label) {
        return this.rowType.get(label);
    }

    /**
     * Renvvoie en sous-ensemble de lignes en fonction de deux index. Les lignes renvoyées sont comprises dans l'intervalle index1 et index2 avec les bornes incluses.
     * Le premier index ne peut pas être plus grand que le deuxième.
     * @param index1 Index de la ligne correspondant à la borne inférieur de l'intervalle de lignes que l'on veut sélectionner.
     * @param index2 Index de la ligne correspondant à la borne supérieur de l'intervalle de lignes que l'on veut sélectionner.
     * @return sous ensemble de lignes.
     */
    public Map<Integer, List<Object>> getSubLines (int index1, int index2) {
        Map<Integer, List<Object>> lines = new HashMap<>();
        try {

            //Cas où les index en paramètre ne sont pas valides.
            if((index2 < index1) || (index1 < 0) || (index2 > this.frameLines.keySet().size()-1))
                throw new IllegalArgumentException();

            else {
                for(int key : this.frameLines.keySet()) {
                    if((key >= index1) && (key <= index2)) {
                        lines.put(key, this.frameLines.get(key));
                    }
                }
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
        return lines;
    }


    /**
     * Renvoie un sous ensemble de lignes en fonction de booléens.
     * Chaque booléen passé en paramètre correspond à un index et on récupère la ligne si l'index correspondant est à true en paramètre.
     * Par exemple si le premier pramètre est à true, on garde la ligne d'index 0.
     * @param index Suite d'index correspondant à des lignes. La place du booléean correspond à l'index d'une ligne.
     * @return sous ensemble de lignes.
     */
    public Map<Integer, List<Object>> getSubLines (boolean... index) {
        Map<Integer, List<Object>> lines = new HashMap<>();
        int key = 0;
        try {
            //Il doit y avoir autant d'arguments que de lignes dans le dataframe.
            if (index.length != this.frameLines.keySet().size())
                throw new IllegalArgumentException();
            else {
                for(int i = 0; i < index.length; i++) {
                    if(index[i]) {
                        lines.put(key, this.frameLines.get(i));
                        key++;
                    }
                }
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
        return lines;
    }

    /**
     * Renvoie un sous ensemble de lignes en fonction d'un label et d'une expression lambda. Filte les lignes en fonction des valeurs d'une colonne.
     * Par exemple pour les paramètres : (age,(x) -&gt; x &gt; 5); on va récupérer toutes les lignes où l'âge est supérieur à 5.
     * @param label Label correspondant à une colonne.
     * @param evaluation Expression booléenne évaluant la donnée d'une cellule du dataFrame. On peut par exemple sélectionner les valeurs x de la colonne pour x supérieur à 5.
     * @return sous ensemble de lignes.
     */
    public Map<Integer, List<Object>> getSubLines (String label, Evaluate evaluation) {
        Map<Integer, List<Object>> lines = new HashMap<>();
        try {
            if(!this.rowType.get(label).getName().equals("java.lang.Integer")) {
                throw new IllegalArgumentException();
            }
            else {
                int index = 0;
                for(int i : this.frameLines.keySet()) {
                    if(evaluation.evaluate((int)this.getValue(i, label))) {
                        lines.put(index, this.frameLines.get(i));
                        index++;
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();

        }
        return lines;
    }

    /**
     * Renvoie en sous ensemble de colonnes en fonction de labels.
     * Toutes les colonnes qui ont un label correspondant à un de ceux en paramètre est ajoutée au sous-ensemble de retour.
     * @param label Suite de labels correspondant à des colonnes.
     * @return sous ensemble de colonnes.
     */
    public Map<String, List<Object>> getSubRows (String... label) {
        Map<String, List<Object>> rows = new HashMap<>();
        try {
            for(String l : label) {
                //Cas où au moins un des labels passé en paramètre n'existe pas.
                if (!this.frameRows.keySet().contains(l)) {
                    throw new IllegalArgumentException();
                }
                else {
                    rows.put(l, this.frameRows.get(l));
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
        return rows;
    }

    public String toString(){
        int i = 0;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%1s"," "));//For the first empty case
        for (String key : this.frameRows.keySet() ) {
            sb.append(String.format("%8s", key));//Print column label (not in the dataframe)
        }
        sb.append('\n');
        //Parcours des lignes du dataframe
        while(this.frameLines.size() > i) {
            List<Object> line = getLine(i);
            sb.append(String.format("%1s",i));//Print column indices (not in the dataframe)
            for (Object elem : line) {
                sb.append(String.format("%8s",elem));//Print dataframe elements
            }
            sb.append('\n');
            i++;
        }
        return sb.toString();
    }

    public String toStringFirstLines(int nbLines) throws Exception {
        if(nbLines > this.frameLines.size()){
            throw new IllegalArgumentException("Erreur : Il y a seulement : " + this.frameLines.size() + "lignes dans ce dataframe");
        }else if(nbLines<=0){
            throw new IllegalArgumentException("Veuillez choisir un nombre de lignes supérieur à 0");
        }
        int i = 0;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%1s"," "));//For the first empty case
        for (String key : this.frameRows.keySet() ) {
            sb.append(String.format("%8s", key));//Print column label (not in the dataframe)
        }
        sb.append('\n');
        //Parcours des lignes du dataframe
        while(nbLines > i) {
            List<Object> line = getLine(i);
            sb.append(String.format("%1s",i));//Print column indices (not in the dataframe)
            for (Object elem : line) {
                sb.append(String.format("%8s",elem));//Print dataframe elements
            }
            sb.append('\n');
            i++;
        }
        return sb.toString();
    }

    public String toStringLastLines(int nbLines) throws Exception {
        if(nbLines > this.frameLines.size()){
            throw new IllegalArgumentException("Erreur : Il y a seulement : " + this.frameLines.size() + " lignes dans ce dataframe");
        }else if(nbLines<=0){
            throw new IllegalArgumentException("Veuillez choisir un nombre de lignes supérieur à 0");
        }
        int i = this.frameLines.size()-nbLines;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%1s"," "));//For the first empty case
        for (String key : this.frameRows.keySet() ) {
            sb.append(String.format("%8s", key));//Print column label (not in the dataframe)
        }
        sb.append('\n');
        //Parcours des lignes du dataframe
        while(this.frameLines.size() > i) {
            List<Object> line = getLine(i);
            sb.append(String.format("%1s",i));//Print column indices (not in the dataframe)
            for (Object elem : line) {
                sb.append(String.format("%8s",elem));//Print dataframe elements
            }
            sb.append('\n');
            i++;
        }
        return sb.toString();
    }
  
}
