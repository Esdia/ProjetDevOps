package pandaJava;

import java.io.*;
import java.util.*;

public class Dataframe {

    private Map<Integer, List<Object>> frameLines;
    private Map<String, List<Object>> frameRows;
    private Map<String, Class> rowType;
    private Map<String, Integer> indexLabel;

    /**
     * Créer un DataFrame à partir d'un tableau donné en paramètre.
     * @param array le tableau contenant les données pour la création du dataframe
     * @throws MistypedRowException Erreur déclenchée si une colonne a plusieurs types
     */
    public Dataframe(Object[][] array) throws MistypedRowException {
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
     * Créer un DataFrame à partir d'un fichier CSV. On considère que le CSV peut contenir 3 types : boolean, String et Integer.
     * @param path la chemin d'accès au fichier
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
     * Renvoie une valeur du DataFrame à un index et label donnés.
     * @param index
     * @param label
     * @return valeur
     */
    public Object getValue (int index, String label) {
        return this.frameLines.get(index).get(this.indexLabel.get(label));
    }


    /**
     * Méthode permmetant de récupérer une colonne d'un dataframe à partir d'un label.
     * @param label label correspondant à la colonne que l'on souhaite récupérer
     * @return la liste d'objets de cette colonne
     */
    public List<Object> getRow(String label) {
        return this.frameRows.get(label);
    }

    /**
     * Méthode permmetant de récupérer une ligne d'un dataframe à partir d'un index.
     * @param index index correspondant à la ligne que l'on souhaite récupérer
     * @return la liste d'objets de cette ligne
     */
    public List<Object> getLine(int index) {
        return this.frameLines.get(index);
    }

    /**
     * Methode permettant de récupérer le type d'une colonne indiquée en paramètre.
     * @param label la colonne pour laquel on souhaite savoir le type de ses valeurs
     * @return la classe des valeurs de cette colonne
     */
    public Class getRowType(String label) {
        return this.rowType.get(label);
    }

    /**
     * Renvvoie en sous-ensemble de lignes en fonction de deux index. Les lignes renvoyées sont comprises dans l'intervalle index1 et index2 avec les bornes incluses.
     * Le premier index ne peut pas être plus grand que le deuxième.
     * @param index1
     * @param index2
     * @return sous ensemble de lignes
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
     * @param index
     * @return sous ensemble de lignes
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
     * Par exemple pour les paramètres : (age,(x) -> x > 5); on va récupérer toutes les lignes où l'âge est supérieur à 5.
     * @param label
     * @param evaluation
     * @return sous ensemble de lignes
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
     * @param label
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

    /**
     * Méthode permettant d'afficher un dataframe sur la console.
     * Pour le faire : System.out.println(myDataframe);
     * @return le string contenant l'affichage souhaité
     */
    public String toString() {
        int i = 0;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%1s", " "));//For the first empty case
        for (String key : this.frameRows.keySet()) {
            sb.append(String.format("%8s", key));//Print column label (not in the dataframe)
        }
        sb.append('\n');
        //Parcours des lignes du dataframe
        while (this.frameLines.size() > i) {
            List<Object> line = getLine(i);
            sb.append(String.format("%1s", i));//Print column indices (not in the dataframe)
            for (Object elem : line) {
                sb.append(String.format("%8s", elem));//Print dataframe elements
            }
            sb.append('\n');
            i++;
        }
        return sb.toString();
    }

    /**
     * Affiche les premières lignes d'un dataframe
     * @param nbLines indique le nombre de lignes à afficher
     * @return un string contenant l'affichage souhaité
     * @throws Exception Un mauvais nombre de lignes a été transmis
     */
    public String toStringFirstLines(int nbLines) throws Exception {
        if (nbLines > this.frameLines.size()) {
            throw new IllegalArgumentException("Erreur : Il y a seulement : " + this.frameLines.size() + "lignes dans ce dataframe");
        } else if (nbLines <= 0) {
            throw new IllegalArgumentException("Veuillez choisir un nombre de lignes supérieur à 0");
        }
        int i = 0;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%1s", " "));//For the first empty case
        for (String key : this.frameRows.keySet()) {
            sb.append(String.format("%8s", key));//Print column label (not in the dataframe)
        }
        sb.append('\n');
        //Parcours des lignes du dataframe
        while (nbLines > i) {
            List<Object> line = getLine(i);
            sb.append(String.format("%1s", i));//Print column indices (not in the dataframe)
            for (Object elem : line) {
                sb.append(String.format("%8s", elem));//Print dataframe elements
            }
            sb.append('\n');
            i++;
        }
        return sb.toString();
    }

    /**
     * Affiche les derniers lignes d'un dataframe
     * @param nbLines indique le nombre de lignes à afficher
     * @return un string contenant l'affichage souhaité
     * @throws Exception Un mauvais nombre de lignes a été transmis
     */
    public String toStringLastLines(int nbLines) throws Exception {
        if (nbLines > this.frameLines.size()) {
            throw new IllegalArgumentException("Erreur : Il y a seulement : " + this.frameLines.size() + " lignes dans ce dataframe");
        } else if (nbLines <= 0) {
            throw new IllegalArgumentException("Veuillez choisir un nombre de lignes supérieur à 0");
        }
        int i = this.frameLines.size() - nbLines;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%1s", " "));//For the first empty case
        for (String key : this.frameRows.keySet()) {
            sb.append(String.format("%8s", key));//Print column label (not in the dataframe)
        }
        sb.append('\n');
        //Parcours des lignes du dataframe
        while (this.frameLines.size() > i) {
            List<Object> line = getLine(i);
            sb.append(String.format("%1s", i));//Print column indices (not in the dataframe)
            for (Object elem : line) {
                sb.append(String.format("%8s", elem));//Print dataframe elements
            }
            sb.append('\n');
            i++;
        }
        return sb.toString();
    }

    //Statistics Parts

    /**
     * Vérifie si le label donné en paramètre correspond bien à un label présent dans le dataframe
     *
     * @param label Indique la colonne du dataframe
     * @return vrai si le label entré correspond bien à un label présent dans le dataframe
     */
    public boolean labelIsCorrect(String label){
        if(getRowType(label) == null){
            throw new IllegalArgumentException("Incorrect label parameter");
        }
        return true;
    }

    /**
     * Calcul la moyenne sur une colonne donnée
     *
     * @param label Indique sur quelle colonne appliquer le calcul
     * @return La moyenne sur toutes les valeurs de la colonne
     */
    public Float dataframeMean(String label) {
        labelIsCorrect(label);
        int size = getRow(label).size();
        float columnSum = dataframeSum(label);
        return (columnSum/size);
    }

    /**
     * Calcul la somme sur une colonne composée de chiffre Entier, Flottant, ...
     *
     * @param label Indique sur quelle colonne appliquer le calcul
     * @return la somme des éléments d'une colonne
     */
    public Float dataframeSum(String label) {
        labelIsCorrect(label);
        if (getRowType(label).getSuperclass() == Number.class) {
            //C'est une classe qui a pour super classe la classe Number (Integer, Float, ...)
            List<Object> myObjects = getRow(label);
            List<Float> myValues = new ArrayList<>();
            for (Object object : myObjects) {
                myValues.add(Float.valueOf((Integer) object));
            }
            return myValues.stream().reduce(0f, Float::sum);
        } else {
            throw new IllegalArgumentException("The column does not contain number values");
        }
    }

    /**
     * Trouve la plus petite valeur d'une colonne. Si les valeurs sont de type String alors la valeur la plus petite est calculée
     * dans l'ordre alphabétique.
     *
     * @param label Indique sur quelle colonne appliquer le calcul
     * @return La valeur la plus petite
     */
    public Object dataframeMin(String label) {
        labelIsCorrect(label);
        if (getRowType(label).getSuperclass() == Number.class) {
            //C'est une classe qui a pour super classe la classe Number (Integer, Float, ...)
            List<Object> myObjects = getRow(label);
            List<Float> myValues = new ArrayList<>();
            for (Object object : myObjects) {
                myValues.add(Float.valueOf((Integer) object));
            }
            return Collections.min(myValues);
        } else if(getRowType(label) == String.class || getRowType(label) == Character.class)  {
            List<Object> myObjects = getRow(label);
            List<String> myValues = new ArrayList<>();
            for (Object object : myObjects) {
                myValues.add(String.valueOf(object));
            }
            return Collections.min(myValues);
        }else{
            throw new IllegalArgumentException("The column contain unknown type values for min");
        }
    }

    /**
     * Trouve la plus petite valeur d'une colonne. Si les valeurs sont de type String alors la valeur la plus petite est calculée
     * dans l'ordre alphabétique.
     *
     * @param label Indique sur quelle colonne appliquer le calcul
     * @return la valeur la plus grande
     */
    public Object dataframeMax(String label) {
        labelIsCorrect(label);
        if (getRowType(label).getSuperclass() == Number.class) {
            //C'est une classe qui a pour super classe la classe Number (Integer, Float, ...)
            List<Object> myObjects = getRow(label);
            List<Float> myValues = new ArrayList<>();
            for (Object object : myObjects) {
                myValues.add(Float.valueOf((Integer) object));
            }
            return Collections.max(myValues);
        } else if(getRowType(label) == String.class || getRowType(label) == Character.class) {
            List<Object> myObjects = getRow(label);
            List<String> myValues = new ArrayList<>();
            for (Object object : myObjects) {
                myValues.add(String.valueOf(object));
            }
            return Collections.max(myValues);
        }else{
            throw new IllegalArgumentException("The column contain unknown type values for max");
        }
    }

}
