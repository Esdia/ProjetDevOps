package pandaJava;

import java.io.*;
import java.util.*;

/**
 * Classe DataFrame représentant des données sous formes de tableaux où les colonnes sont identifiés par un label et ne contiennet qu'un type d'objet.
 * Les lignes peuvent contenir plusieurs types d'objets et sont indexées par un entier.
 */
public class Dataframe {

    private Map<Integer, List<Object>> frameLines;
    private Map<String, List<Object>> frameColumns;
    private Map<String, Class> columnType;
    private Map<String, Integer> indexLabel;
    private Map<Integer, String> labelIndex;

    /**
     * Création d'un DataFrame à partir d'un tableau simple à deux dimensions.
     *
     * @param array Tableau d'objets à deux dimensions pouvant être convertis en dataFram. Les colonnes doivent donc avoir des types d'objet constant.
     * @throws MistypedRowException Quand le tableau rentré en argument est non convetible en dataFrame.
     */
    public Dataframe(Object[][] array) throws MistypedRowException {
        try {
            String label = "";
            this.frameColumns = new HashMap<>();
            this.frameLines = new HashMap<>();
            this.columnType = new HashMap<>();
            this.indexLabel = new HashMap<>();
            this.labelIndex = new HashMap<>();
            //Parcours des colonnes
            for (int j = 0; j < array[0].length; j++) {
                int value = j;
                StringBuilder sb = new StringBuilder(String.valueOf((char) ('A' + (value % 26))));
                //Boucle while générant des labels pour les colonnes en suivant l'ordre alphabétique.
                while ((value = (value / 26 - 1)) >= 0)
                    sb.append((char) ('A' + (value % 26)));
                label = sb.reverse().toString();
                this.frameColumns.put(label, new ArrayList<>());
                this.indexLabel.put(label, j);
                this.labelIndex.put(j, label);
                Object firstElemRow = array[0][j];
                this.columnType.put(label, Class.forName(firstElemRow.getClass().getName()));
                //Parcours des lignes
                for (int i = 0; i < array.length; i++) {
                    if (!this.frameLines.containsKey(i))
                        this.frameLines.put(i, new ArrayList<>());
                    Object e = array[i][j];
                    if (!(e.getClass().getName().equals(this.columnType.get(label).getName())))
                        throw new MistypedRowException();
                    else {
                        this.frameLines.get(i).add(array[i][j]);
                        this.frameColumns.get(label).add(array[i][j]);
                    }
                }
            }
        } catch (MistypedRowException | ClassNotFoundException e) {
            if (e.getClass().getName().equals("pandaJava.MistypedRowException"))
                throw new MistypedRowException("Mistyped array argument. A row has a unique type.", e);
            else
                e.printStackTrace();
        }
    }

    /**
     * Création d'un DataFrame à partir d'un fichier CSV. On considère qu'il ne peut y avoir que 3 types dans le fichier CSV : Integer, boolean et String.
     *
     * @param path Chemin vers le fichier CSV à convertir en DataFrame.
     */
    public Dataframe(String path) {
        try {
            this.frameColumns = new HashMap<>();
            this.frameLines = new HashMap<>();
            this.columnType = new HashMap<>();
            this.indexLabel = new HashMap<>();
            this.labelIndex = new HashMap<>();
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
                    for (int j = 0; j < labels.length; j++) {
                        this.frameColumns.put(labels[j], new ArrayList<>());
                        this.indexLabel.put(labels[j], j);
                        this.labelIndex.put(j, labels[j]);
                        this.columnType.put(labels[j], Class.forName("java.lang." + types[j]));
                    }
                    i++;
                }

                //Lignes après la deuxième (valeurs)
                else {
                    this.frameLines.put(lineIndex, new ArrayList<>());
                    String[] line = st.split(";");
                    for (int j = 0; j < line.length; j++) {
                        Class c = this.columnType.get(labels[j]); //On récupère la classe de la colonne
                        Object elem;
                        //On cast l'élément en String, en Int ou en booléen en fonction du type de la colonne.
                        if (c.getName().equals("java.lang.Integer"))
                            elem = Integer.valueOf(line[j]);
                        else if (c.getName().equals("java.lang.Boolean"))
                            elem = Boolean.valueOf(line[j]);
                        else
                            elem = line[j];
                        this.frameLines.get(lineIndex).add(elem);
                        this.frameColumns.get(labels[j]).add(elem);
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
     *
     * @param index Ligne de la cellule.
     * @param label Colonne de la cellule.
     * @return valeur de la cellule.
     */
    public Object getValue(int index, String label) {
        return this.frameLines.get(index).get(this.indexLabel.get(label));
    }

    /**
     * Renvoie la liste d'objets d'une colonne à partir de son label.
     *
     * @param label Label de la colonne.
     * @return une liste d'objets composant la colonne sélectionnée.
     */
    public List<Object> getColumn(String label) {
        return this.frameColumns.get(label);
    }

    /**
     * Renvoie la liste d'objets d'une ligne à partir de son index.
     *
     * @param index Index d'une ligne.
     * @return la liste d'objets composant la ligne sélectionnée.
     */
    public List<Object> getLine(int index) {
        return this.frameLines.get(index);
    }

    /**
     * Renvoie la classe des objets d'une colonne à partir de son label.
     *
     * @param label Label d'une colonne.
     * @return la classe à laquelle appartiennent les objets de la colonne.
     */
    public Class getColumnType(String label) {
        return this.columnType.get(label);
    }

    /**
     * Renvoie en sous-ensemble de lignes en fonction de deux index. Les lignes renvoyées sont comprises dans l'intervalle index1 et index2 avec les bornes incluses.
     * Le premier index ne peut pas être plus grand que le deuxième.
     *
     * @param index1 Index de la ligne correspondant à la borne inférieur de l'intervalle de lignes que l'on veut sélectionner.
     * @param index2 Index de la ligne correspondant à la borne supérieur de l'intervalle de lignes que l'on veut sélectionner.
     * @return sous ensemble de lignes.
     */
    public Map<Integer, List<Object>> getSubLines(int index1, int index2) {
        Map<Integer, List<Object>> lines = new HashMap<>();
        try {

            //Cas où les index en paramètre ne sont pas valides.
            if ((index2 < index1) || (index1 < 0) || (index2 > this.frameLines.keySet().size() - 1))
                throw new IllegalArgumentException();

            else {
                for (int key : this.frameLines.keySet()) {
                    if ((key >= index1) && (key <= index2)) {
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
     *
     * @param index Suite d'index correspondant à des lignes. La place du booléean correspond à l'index d'une ligne.
     * @return sous ensemble de lignes.
     */
    public Map<Integer, List<Object>> getSubLines(boolean... index) {
        Map<Integer, List<Object>> lines = new HashMap<>();
        int key = 0;
        try {
            //Il doit y avoir autant d'arguments que de lignes dans le dataframe.
            if (index.length != this.frameLines.keySet().size())
                throw new IllegalArgumentException();
            else {
                for (int i = 0; i < index.length; i++) {
                    if (index[i]) {
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
     *
     * @param label      Label correspondant à une colonne.
     * @param evaluation Expression booléenne évaluant la donnée d'une cellule du dataFrame. On peut par exemple sélectionner les valeurs x de la colonne pour x supérieur à 5.
     * @return sous ensemble de lignes.
     */
    public Map<Integer, List<Object>> getSubLines(String label, Evaluate evaluation) {
        Map<Integer, List<Object>> lines = new HashMap<>();
        try {
            if (!this.columnType.get(label).getName().equals("java.lang.Integer")) {
                throw new IllegalArgumentException();
            } else {
                int index = 0;
                for (int i : this.frameLines.keySet()) {
                    if (evaluation.evaluate((int) this.getValue(i, label))) {
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
     *
     * @param label Suite de labels correspondant à des colonnes.
     * @return sous ensemble de colonnes.
     */
    public Map<String, List<Object>> getSubColumns(String... label) {
        Map<String, List<Object>> columns = new HashMap<>();
        try {
            for (String l : label) {
                //Cas où au moins un des labels passé en paramètre n'existe pas.
                if (!this.frameColumns.keySet().contains(l)) {
                    throw new IllegalArgumentException();
                } else {
                    columns.put(l, this.frameColumns.get(l));
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
        return columns;
    }

    /**
     * Méthode permettant d'afficher un dataframe sur la console.
     * Pour le faire : System.out.println(myDataframe);
     *
     * @return le string contenant l'affichage souhaité
     */
    public String toString() {
        int i = 0;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%1s", " "));//For the first empty case
        for (String key : this.frameColumns.keySet()) {
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
     *
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
        for (String key : this.frameColumns.keySet()) {
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
     *
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
        for (String key : this.frameColumns.keySet()) {
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
    public boolean labelIsCorrect(String label) {
        if (getColumnType(label) == null) {
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
        int size = getColumn(label).size();
        float columnSum = dataframeSum(label);
        return (columnSum / size);
    }

    /**
     * Calcul la somme sur une colonne composée de chiffre Entier, Flottant, ...
     *
     * @param label Indique sur quelle colonne appliquer le calcul
     * @return la somme des éléments d'une colonne
     */
    public Float dataframeSum(String label) {
        labelIsCorrect(label);
        if (getColumnType(label).getSuperclass() == Number.class) {
            //C'est une classe qui a pour super classe la classe Number (Integer, Float, ...)
            List<Object> myObjects = getColumn(label);
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
        if (getColumnType(label).getSuperclass() == Number.class) {
            //C'est une classe qui a pour super classe la classe Number (Integer, Float, ...)
            List<Object> myObjects = getColumn(label);
            List<Float> myValues = new ArrayList<>();
            for (Object object : myObjects) {
                myValues.add(Float.valueOf((Integer) object));
            }
            return Collections.min(myValues);
        } else if (getColumnType(label) == String.class || getColumnType(label) == Character.class) {
            List<Object> myObjects = getColumn(label);
            List<String> myValues = new ArrayList<>();
            for (Object object : myObjects) {
                myValues.add(String.valueOf(object));
            }
            return Collections.min(myValues);
        } else {
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
        if (getColumnType(label).getSuperclass() == Number.class) {
            //C'est une classe qui a pour super classe la classe Number (Integer, Float, ...)
            List<Object> myObjects = getColumn(label);
            List<Float> myValues = new ArrayList<>();
            for (Object object : myObjects) {
                myValues.add(Float.valueOf((Integer) object));
            }
            return Collections.max(myValues);
        } else if (getColumnType(label) == String.class || getColumnType(label) == Character.class) {
            List<Object> myObjects = getColumn(label);
            List<String> myValues = new ArrayList<>();
            for (Object object : myObjects) {
                myValues.add(String.valueOf(object));
            }
            return Collections.max(myValues);
        } else {
            throw new IllegalArgumentException("The column contain unknown type values for max");
        }
    }

    /**
     * Cette méthode crée un nouveau DataFrame composé d'une liste de lignes données et des labels du DataFrame actuel.
     *
     * @param lines Les lignes qui forment le nouveau DataFrame
     */
    private Dataframe createNewDataframe(List<List<Object>> lines) {
        int height = lines.size();
        if (height == 0) {
            try {
                return new Dataframe(new Object[0][0]);
            } catch (MistypedRowException e) {
                /* Should never happen */
                e.printStackTrace();
            }
        }

        int width = lines.get(0).size();

        /* Convert the given lines to an object array */
        Object[][] array = new Object[height][width];
        for (int i = 0; i < height; i++) {
            array[i] = lines.get(i).toArray();
        }

        /* Create the DataFrame */
        Dataframe dataframe;
        try {
            dataframe = new Dataframe(array);
        } catch (MistypedRowException e) {
            /* Should never happen */
            e.printStackTrace();
            return null;
        }

        /* Replace the placeholder labels with the actual labels */
        for (Integer index : this.labelIndex.keySet()) {
            String label = this.labelIndex.get(index);

            String old_label = dataframe.labelIndex.get(index);
            dataframe.indexLabel.remove(old_label);
            dataframe.indexLabel.put(label, index);
            dataframe.labelIndex.put(index, label);

            List<Object> column = dataframe.frameColumns.get(old_label);
            dataframe.frameColumns.put(label, column);
            dataframe.frameColumns.remove(old_label);
        }

        return dataframe;
    }

    /**
     * Cette méthode crée un nouveau DataFrame composé d'une liste de lignes données, et qui ne contient que les colonnes
     * correspondant aux labels donnés
     *
     * @param lines        Les lignes qui forment le nouveau DataFrame
     * @param labelsToKeep Les labels des colonnes que l'on garde.
     * @return Le nouveau DataFrame
     */
    private Dataframe createNewDataframe(List<List<Object>> lines, List<String> labelsToKeep) {
        Dataframe dataframe = this.createNewDataframe(lines);
        assert dataframe != null;

        /* Remove the columns from the Dataframe and store the removed label's indexes */
        List<Integer> indexesToRemove = new Vector<>();
        for (String label : this.indexLabel.keySet()) {
            if (!labelsToKeep.contains(label)) {
                dataframe.frameColumns.remove(label);
                indexesToRemove.add(this.indexLabel.get(label));
            }
        }

        /* We sort the indexes in descending order to prevent issues when removing from the lines */
        indexesToRemove.sort(Integer::compareTo);
        Collections.reverse(indexesToRemove);

        /* In each line we remove the indexes corresponding to the removed columns to keep the data consistent */
        for (int i : indexesToRemove) {
            for (int j = 0; j < this.frameLines.size(); j++) {
                dataframe.frameLines.get(j).remove(i);
            }
        }

        /* We update the label index tables */
        dataframe.labelIndex.clear();
        dataframe.indexLabel.clear();
        List<Integer> keptLabelIndexes = new Vector<>();
        for (String label : labelsToKeep) {
            keptLabelIndexes.add(this.indexLabel.get(label));
        }
        keptLabelIndexes.sort(Integer::compareTo);
        for (int i = 0; i < keptLabelIndexes.size(); i++) {
            int old_index = keptLabelIndexes.get(i);
            String label = this.labelIndex.get(old_index);

            dataframe.indexLabel.put(label, i);
            dataframe.labelIndex.put(i, label);
        }

        return dataframe;
    }

    /**
     * Retourne un nouveau DataFrame qui ne contient que la ligne à l'indice donné.
     *
     * @param index L'indice de la ligne qu'on souhaite récupérer.
     * @return Un nouveau DataFrame qui ne contient que la ligne à l'indice donné.
     */
    public Dataframe loc(int index) {
        List<List<Object>> lines = new Vector<>();
        lines.add(this.getLine(index));

        return this.createNewDataframe(lines);
    }

    /**
     * Retourne un nouveau DataFrame qui ne contient que les lignes aux indices donnés.
     *
     * @param indexes Les indices des lignes qu'on souhaite récupérer.
     * @return Un nouveau DataFrame qui ne contient que les lignes aux indices donnés.
     */
    public Dataframe loc(int... indexes) {
        List<List<Object>> lines = new Vector<>();

        for (int i : indexes) {
            lines.add(this.getLine(i));
        }

        return this.createNewDataframe(lines);
    }

    /**
     * Retourne un nouveau DataFrame qui ne contient que la colonne correspondant au label donné.
     *
     * @param label Le label de la colonne qu'on souhaite récupérer.
     * @return Un nouveau DataFrame qui ne contient que la colonne correspondant au label donné.
     */
    public Dataframe loc(String label) {
        List<List<Object>> lines = new Vector<>(this.frameLines.values());
        List<String> labelsToKeep = new Vector<>();
        labelsToKeep.add(label);

        return this.createNewDataframe(lines, labelsToKeep);
    }

    /**
     * Retourne un nouveau DataFrame qui ne contient que les colonnes correspondant aux labels donnés.
     *
     * @param labels Les labels des colonnes qu'on souhaite récupérer.
     * @return Un nouveau DataFrame qui ne contient que les colonnes correspondant aux labels donnés.
     */
    public Dataframe loc(String... labels) {
        List<List<Object>> lines = new Vector<>(this.frameLines.values());
        List<String> labelsToKeep = new Vector<>(Arrays.asList(labels));
        return this.createNewDataframe(lines, labelsToKeep);
    }

    /**
     * Retourne un nouveau DataFrame filtré en ligne et en colonnes.
     * <p>
     * Équivalent à loc(index).loc(label)
     *
     * @param index L'indice de la ligne qu'on souhaite récupérer.
     * @param label Le label de la colonne qu'on souhaite récupérer.
     * @return Un nouveau DataFrame filtré en ligne et en colonnes.
     */
    public Dataframe loc(int index, String label) {
        return this.loc(index).loc(label);
    }

    /**
     * Retourne un nouveau DataFrame filtré en lignes.
     * <p>
     * Pour chaque indice i dans le tableau donné en argument, la ligne i du DataFrame n'est gardée que si le booléen
     * d'indice i dans le tableau vaut True.
     *
     * @param indexes Un tableau contenant autant de booléen que le DataFrame contient de lignes
     * @return Un nouveau DataFrame filtré en lignes.
     */
    public Dataframe loc(boolean... indexes) {
        Map<Integer, List<Object>> lines = this.getSubLines(indexes);

        List<List<Object>> _lines = new Vector<>(lines.values());
        return this.createNewDataframe(_lines);
    }

    /**
     * Retourne un nouveau DataFrame filtré par un prédicat.
     * <p>
     * Pour chaque ligne, la case dans la colonne correspondant au label donné est testée par le prédicat. La ligne est
     * conservée dans le nouveau DataFrame si le prédicat renvoie True.
     *
     * @param label      La colonne utilisée pour évaluer le prédicat
     * @param evaluation Le prédicat utilisé pour filtrer le DataFrame
     * @return Un nouveau DataFrame filtré par le prédicat.
     */
    public Dataframe loc(String label, Evaluate evaluation) {
        Map<Integer, List<Object>> lines = this.getSubLines(label, evaluation);

        List<List<Object>> _lines = new Vector<>(lines.values());
        return this.createNewDataframe(_lines);
    }
}
