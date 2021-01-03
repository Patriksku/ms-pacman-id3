package pacman.entries.pacman;

import dataRecording.DataSaverLoader;
import dataRecording.DataTuple;
import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.*;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class MyPacMan extends Controller<MOVE> {
    double percentageOfTestData = 0.25; // Ratio of test and training data.
    ArrayList<DataTuple> trainingData = new ArrayList<>();
    ArrayList<DataTuple> testData = new ArrayList<>();
    Node node;

    ArrayList<String> classOfAttributes = new ArrayList<>();
    HashMap<String, ArrayList<String>> attributes = new HashMap<String, ArrayList<String>>();

    public MyPacMan() {
        splitData();
        initClassifierInformation();
        node = generateTree(trainingData, attributes);
        printTrainingAccuracy();
    }

    public Node getTree(){
        return node;
    }

    // Divides the total data into a training and a testing chunk,
    // which are independent of each other.
    public void splitData() {
        DataTuple[] data = DataSaverLoader.LoadPacManData();
        ArrayList<DataTuple> allTheData = new ArrayList<>(Arrays.asList(data));

        // The test-size specified will be how large the test data will become.
        int testSize = (int)(allTheData.size() * percentageOfTestData);

        // Introducing randomness in the tuple-selection, so we get more fair and
        // unbiased training and testing.
        Random random = new Random();

        // Go through a percentage of the total data and extract test data.
        for (int i = 0; i < testSize; i++) {
            int thisIndex = random.nextInt(allTheData.size());

            testData.add(allTheData.get(thisIndex));
            allTheData.remove(thisIndex);
        }

        // Set the remaining as the training data.
        trainingData = new ArrayList<DataTuple>(Arrays.asList(DataSaverLoader.LoadPacManData()));
        trainingData = cleanData(trainingData);
    }

    // Cleans up the data by removing unwanted tuples, thus increasing the quality of the data.
    public ArrayList<DataTuple> cleanData(ArrayList<DataTuple> data){
        ArrayList<DataTuple> newData = new ArrayList<DataTuple>();
        int removedCounter = 0;

        // Loop through alla tuples
        for(DataTuple nD: data){

            // Remove tuples which contain illegal moves -> Moving into obstacles.
            System.out.println(nD.getAttributeValue("moveLeft"));
            if(nD.getAttributeValue("moveLeft").equals("NO") && nD.DirectionChosen.toString().equals("LEFT")){
                removedCounter++;

            }else if(nD.getAttributeValue("moveRight").equals("NO") && nD.DirectionChosen.toString().equals("RIGHT")){
                removedCounter++;

            }else if(nD.getAttributeValue("moveUp").equals("NO") && nD.DirectionChosen.toString().equals("UP")){
                removedCounter++;

            }else if(nD.getAttributeValue("moveDown").equals("NO") && nD.DirectionChosen.toString().equals("DOWN")){
                removedCounter++;

            }
            else if(nD.DirectionChosen.toString().equals("NEUTRAL")){
                removedCounter++;

                // If this tuple was "clean" -> Add it to the clean list.
            }else{
                newData.add(nD);
            }
        }
        System.out.println(removedCounter);
        return newData;
    }

    // Creates the neccessary data containers, such as attributes classes and their possible values.
    public void initClassifierInformation() {

        ArrayList<String> yesOrNo = new ArrayList<String>();
        yesOrNo.add("YES");
        yesOrNo.add("NO");

        ArrayList<String> discreteDistance = new ArrayList<String>();
        discreteDistance.add("VERY_LOW");
        discreteDistance.add("LOW");
        discreteDistance.add("MEDIUM");
        discreteDistance.add("HIGH");
        discreteDistance.add("VERY_HIGH");
        discreteDistance.add("NONE");

        ArrayList<String> directions = new ArrayList<String>();
        directions.add("RIGHT");
        directions.add("LEFT");
        directions.add("UP");
        directions.add("DOWN");
        directions.add("NEUTRAL");

        // Add attributes
        attributes.put("isBlinkyEdible", yesOrNo);
        attributes.put("isInkyEdible", yesOrNo);
        attributes.put("isPinkyEdible", yesOrNo);
        attributes.put("isSueEdible", yesOrNo);
        attributes.put("blinkyDist", discreteDistance);
        attributes.put("inkyDist", discreteDistance);
        attributes.put("pinkyDist", discreteDistance);
        attributes.put("sueDist", discreteDistance);
        attributes.put("blinkyDir", directions);
        attributes.put("inkyDir", directions);
        attributes.put("pinkyDir", directions);
        attributes.put("sueDir", directions);
        //attributes.put("blinkySameDir", yesOrNo);
        //attributes.put("inkySameDir", yesOrNo);
        //attributes.put("pinkySameDir", yesOrNo);
        //attributes.put("sueSameDir", yesOrNo);
        attributes.put("isJunction", yesOrNo);
        attributes.put("moveLeft",yesOrNo);
        attributes.put("moveRight",yesOrNo);
        attributes.put("moveUp",yesOrNo);
        attributes.put("moveDown",yesOrNo);
        attributes.put("pacmanPosition",discreteDistance);
        attributes.put("lastMove", directions);
        attributes.put("closestPillDir", directions);
        //attributes.put("closestPillDist", discreteDistance);

        // Classifier attributes which correspond to moves (leaf nodes).
        classOfAttributes.add("UP");
        classOfAttributes.add("LEFT");
        classOfAttributes.add("DOWN");
        classOfAttributes.add("RIGHT");
        classOfAttributes.add("NONE");
    }

    public Node generateTree(ArrayList<DataTuple> trainingData, HashMap<String, ArrayList<String>> attributes) {
        // 1. "Create node N"
        Node node = new Node();

        //2. "If every tuple in D has the same class C, return N as a leaf node labeled as C."
        if (sameClass(trainingData)) {
            MOVE move = trainingData.get(0).DirectionChosen;
            node.setLabel(move.toString());
            if(node.label == null){
                System.out.println(121);
            }
            node.setLeaf(true);
            return node;
        }

        //3. "Otherwise, if the attribute list is empty, return N as a leaf node labeled with themajority class in D."
        else if (attributes.isEmpty()) {
            MOVE move = getMajorityClass(trainingData);
            node.setLabel(move.toString());
            if(node.label == null){
                System.out.println(121);
            }
            node.setLeaf(true);
            return node;
        }

        //4. "Otherwise:"
        //4.1 "Call the attribute selection method on D and the attribute list, in order to choose the current attribute A:"
        else{
            // Attribute selection using ID3 info gain.
            String attribute = chooseAttribute(trainingData, attributes);

            // Clone the attribute hashmap.
            HashMap<String, ArrayList<String>> cloneAttributes = (HashMap<String, ArrayList<String>>) attributes.clone();
            ArrayList<String> attributeValues = cloneAttributes.get(attribute);

            // 4.2 "Label N as A and remove A from the attribute list"
            // Label the current node as the attribute selected.
            node.label = attribute;
            cloneAttributes.remove(attribute);

            // 4.3 "For each value aj in attribute A"
            for(int i = 0; i < attributeValues.size(); i++){

                ArrayList<DataTuple> subset = new ArrayList<DataTuple>();

                // Loop through training data.
                for(DataTuple data: trainingData){
                    if(data.getAttributeValue(attribute).equals(attributeValues.get(i))){

                        // 4.3 a. "Separate all tuples in D so that attribute A takes the value aj, creating the subset Dj."
                        subset.add(data);
                    }
                }

                // 4.3 b. "If Dj is empty, add a child node to N labeled with the majority class in D."
                if(subset.size() == 0){
                    Node newNode = new Node();
                    newNode.label = this.getMajorityClass(trainingData).toString();
                    newNode.setLeaf(true);
                    node.addChild(newNode, attributeValues.get(i));

                    // 4.3 c. "Otherwise,
                    // Add the resulting node from calling Generate_Tree(Dj, attribute) as a child node to N.
                }else{
                    // Recursive call.
                    node.addChild(generateTree(subset, cloneAttributes), attributeValues.get(i));
                }
            }
        }
        node.print(" ");
        return node;
    }

    // Checks if every tuple in D has the same class -> Returns true if so.
    public boolean sameClass(ArrayList<DataTuple> trainingData) {
        MOVE move = trainingData.get(0).DirectionChosen;

        for (int i = 0; i < trainingData.size(); i++) {
            if (move != trainingData.get(i).DirectionChosen) {
                return false;
            }
        }
        return true;
    }

    // log2 adaptation helper-method.
    static double log2(double x) {
        if(x == 0) return 0;
        float res = (float)(Math.log(x) / Math.log(2));
        return res;
    }

    // Attribute for choosing the "best" attribute -> Attribute Selection method, using information gain.
    public String chooseAttribute(ArrayList<DataTuple> data, HashMap<String,ArrayList<String>> attributes){

        double bestCount = Double.NEGATIVE_INFINITY;
        String bestAttribute = null;
        int totalTuples = data.size();
        int up = 0;
        int down = 0;
        int right = 0;
        int left = 0;
        int neutral = 0;

        //Counting total amount classes appear. Used to calc total entropy.
        for (int i = 0; i < data.size(); i++){
            switch (data.get(i).DirectionChosen){
                case UP:
                    up++;
                    break;
                case DOWN:
                    down++;
                    break;
                case LEFT:
                    left++;
                    break;
                case RIGHT:
                    right++;
                    break;
                case NEUTRAL:
                    neutral++;
                    break;
            }
        }

        // Calc total entropy for this dataset.
        double totalEntropy = -log2(up/totalTuples)*(up/totalTuples)-log2(down/totalTuples)*(down/totalTuples)-log2(left/totalTuples)*(left/totalTuples)-log2(right/totalTuples)*(right/totalTuples)-log2(neutral/totalTuples)*(neutral/totalTuples);

        // looping through all possible attributes
        ArrayList<String> tempAttributes = new ArrayList<String>(attributes.keySet());
        for(int i = 0; i < tempAttributes.size(); i++){

            //Looping through all possible values for the attributes
            double finalCount = totalEntropy;
            for (int k = 0; k < attributes.get(tempAttributes.get(i)).size(); k++){

                up = 0;
                down = 0;
                right = 0;
                left = 0;
                neutral = 0;
                int counter= 0;
                double localCount = 0;

                //looping through all tuples
                for(DataTuple d: data){

                    // advances counters if a tuple with attribute matching current loop value is found. Saves class as well
                    if(d.getAttributeValue(tempAttributes.get(i)).equals(attributes.get(tempAttributes.get(i)).get(k))){
                        counter++;
                        switch (d.DirectionChosen) {
                            case UP:
                                up++;
                                break;
                            case DOWN:
                                down++;
                                break;
                            case LEFT:
                                left++;
                                break;
                            case RIGHT:
                                right++;
                                break;
                            case NEUTRAL:
                                neutral++;
                                break;
                        }
                    }
                }

                if (counter != 0) {
                    //partly calc entropy for this attribute. When loop is done the calculation will be complete
                    localCount = -log2(up/counter)*(up/counter)-log2(down/counter)*(down/counter)-log2(left/counter)*(left/counter)-log2(right/counter)*(right/counter)-log2(neutral/counter)*(neutral/counter);
                    finalCount += -(counter/totalTuples)*localCount;
                }
            }

            //Checks if entropy for current attribute is better than old.
            if(finalCount > bestCount){
                bestCount = finalCount;
                bestAttribute = tempAttributes.get(i);
            }
        }
        return bestAttribute;
    }

    // Returns the majority class attribute from the training data.
    public MOVE getMajorityClass(ArrayList<DataTuple> trainingData) {

        int UP = 0;
        int LEFT = 0;
        int DOWN = 0;
        int RIGHT = 0;
        int NEUTRAL = 0;

        for (int i = 0; i < trainingData.size(); i++) {

            if ("UP".equals(trainingData.get(i).DirectionChosen.toString())) {
                UP++;
            } else if ("LEFT".equals(trainingData.get(i).DirectionChosen.toString())) {
                LEFT++;
            } else if ("DOWN".equals(trainingData.get(i).DirectionChosen.toString())) {
                DOWN++;
            } else if ("RIGHT".equals(trainingData.get(i).DirectionChosen.toString())) {
                RIGHT++;
            } else {
                NEUTRAL++;
            }
        }

        int bestMoveINT = UP;
        MOVE bestMove = MOVE.UP;

        if (LEFT >= bestMoveINT) {
            bestMoveINT = LEFT;
            bestMove = MOVE.LEFT;
        }

        if (DOWN >= bestMoveINT) {
            bestMoveINT = DOWN;
            bestMove = MOVE.DOWN;
        }

        if (RIGHT >= bestMoveINT) {
            bestMoveINT = RIGHT;
            bestMove = MOVE.RIGHT;
        }

        if (NEUTRAL >= bestMoveINT) {
            bestMove = MOVE.NEUTRAL;
        }

        return bestMove;
    }

    // Prints the training accuracy, by comparing all correct moves, as per the tree's class attribute of each
    // tuple (MOVE), with the generated move.
    public void printTrainingAccuracy() {
        double accuracy = 0;
        double correctMoves = 0;

        MOVE correctMove = null;
        MOVE generatedMove = null;

        // Loops through all the test data.
        for (int i = 0; i < testData.size(); i++) {
            correctMove = testData.get(i).DirectionChosen;

            // Start with the root.
            Node node = this.getTree();
            String move = "";

            // Loop until we find the generated move.
            while (true) {
                if (node.isLeaf) {
                    move = MOVE.valueOf(node.label).toString();
                    generatedMove = MOVE.valueOf(move);
                    break;
                }

                // List of values of a non-leafs children.
                ArrayList<String> values = new ArrayList<String>(node.children.keySet());

                // The attribute value of the current test data tuple.
                String currentAttributeValue = testData.get(i).getAttributeValue(node.label);

                // Loop through all the values and check if there is a match with the current attribute value.
                for (String value: values) {
                    if (value.equals(currentAttributeValue)) {
                        // If yes, then this is the generated move.
                        node = node.children.get(value);
                        break;
                    }
                }
            }

            // Compare correct move with the generated move.
            if (correctMove.toString().equals(generatedMove.toString())) {
                correctMoves++;
            }
        }

        // Calculate percentage of correct moves as compared to the trained model.
        accuracy = (correctMoves / testData.size());
        System.out.println("ACCURACY: " + accuracy);
    }

    // Method for returning the move that Ms. Pacman will do.
    public MOVE getMove(Game game, long timeDue) {

        // Represents this instance of the game as a tuple of data.
        DataTuple data = new DataTuple(game, null);
        Node node = this.getTree();
        String move = "";

        // Loop through tree until we find the move.
        while (true) {
            if (node.isLeaf) {
                move = node.label;
                break;
            }

            // List of values of a non-leafs children.
            ArrayList<String> values = new ArrayList<String>(node.children.keySet());

            // The attribute value of the current data tuple.
            String currentAttributeValue = data.getAttributeValue(node.label);

            // Loop through all the values and check if there is a match with the current attribute value.
            for (String value: values) {
                if (value.equals(currentAttributeValue)) {
                    node = node.children.get(value);
                    break;
                }
            }
        }

        // Returns the move, based on the data tuple, derived from the tree.
        System.out.println(move);
        return MOVE.valueOf(move);
    }

    public static void main(String[] args) {
        new MyPacMan();
    }
}