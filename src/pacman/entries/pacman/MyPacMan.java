package pacman.entries.pacman;

import dataRecording.DataSaverLoader;
import dataRecording.DataTuple;
import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.lang.reflect.Array;
import java.util.*;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class MyPacMan extends Controller<MOVE> {
    double percentageOfTrainingData = 0.2;
    ArrayList<DataTuple> trainingData = new ArrayList<>();
    ArrayList<DataTuple> testData = new ArrayList<>();


    ArrayList<String> classOfAttributes = new ArrayList<>();
    HashMap<String, ArrayList<String>> attributes = new HashMap<String, ArrayList<String>>();

    public MyPacMan() {
        splitData();
        initClassifierInformation();
        generateTree(trainingData, attributes);
    }

    public void splitData() {
        DataTuple[] data = DataSaverLoader.LoadPacManData();
        ArrayList<DataTuple> allTheData = new ArrayList<>(Arrays.asList(data));
        int trainingSize = (int)(allTheData.size() * percentageOfTrainingData);

        Random random = new Random();

        // Go through a percentage of the total data and extract training data.
        for (int i = 0; i < trainingSize; i++) {
            int thisIndex = random.nextInt(allTheData.size());

            testData.add(allTheData.get(thisIndex));
            allTheData.remove(thisIndex);
        }

        // Set the remaining as the training data.
        trainingData = allTheData;
    }

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
//		attributes.put("blinkySameDir", yesOrNo);
//		attributes.put("inkySameDir", yesOrNo);
//		attributes.put("pinkySameDir", yesOrNo);
//		attributes.put("sueSameDir", yesOrNo);
//		attributes.put("isJunction", yesOrNo);

        classOfAttributes.add("UP");
        classOfAttributes.add("LEFT");
        classOfAttributes.add("DOWN");
        classOfAttributes.add("RIGHT");
        classOfAttributes.add("NONE");
    }

    public Node generateTree(ArrayList<DataTuple> trainingData, HashMap<String, ArrayList<String>> attributes) {
        // 1.
        Node node = new Node();

        //2.
        if (sameClass(trainingData)) {

            MOVE move = trainingData.get(0).DirectionChosen;
            node.setLabel(move.toString());
            node.setLeaf(true);
            return node;
        }

        //3.
        if (attributes.isEmpty()) {

            MOVE move = getMajorityClass(trainingData);
            node.setLabel(move.toString());
            node.setLeaf(true);
            return node;
        }

        //4.





        /////
        return new Node();
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

    static double log2(double x) {
        if(x == 0) return 0;
        float res = (float)(Math.log(x) / Math.log(2));
        return res;
    }

    public String chooseAttribute(ArrayList<DataTuple> data, HashMap<String,ArrayList<String>> attributes){
        double bestCount = Double.NEGATIVE_INFINITY;
        String bestAttribute = null;
        int totalTuples = data.size();
        int up = 0;
        int down = 0;
        int right = 0;
        int left = 0;
        int neutral = 0;

        //Counting total amount classes appear. Used to calc totalentropy
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

        //Calc total entropy for this dataset
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
                }
                //partly calc entropy for this attribute. When loop is done the calculation will be complete
                localCount = -log2(up/counter)*(up/counter)-log2(down/counter)*(down/counter)-log2(left/counter)*(left/counter)-log2(right/counter)*(right/counter)-log2(neutral/counter)*(neutral/counter);
                finalCount += -(counter/totalTuples)*localCount;

            }

            //Checks if entropy for current attribute is better than old.
            if(finalCount > bestCount){
                bestCount = finalCount;
                bestAttribute = tempAttributes.get(i);
            }
        }
        return bestAttribute;
    }

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



	private MOVE myMove=MOVE.NEUTRAL;
	
	public MOVE getMove(Game game, long timeDue) 
	{
		//Place your game logic here to play the game as Ms Pac-Man
		
		return myMove;
	}
}