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
    ArrayList<String> attributes = new ArrayList<>();

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
        // Add attributes
        attributes.add("mazeIndex");
        attributes.add("currentLevel");
        attributes.add("pacmanPosition");
        attributes.add("pacmanLivesLeft");
        attributes.add("totalGameTime");
        attributes.add("currentLevelTime");
        attributes.add("numOfPillsLeft");
        attributes.add("numOfPowerPillsLeft");
        attributes.add("isBlinkyEdible");
        attributes.add("isInkyEdible");
        attributes.add("isPinkyEdible");
        attributes.add("isSueEdible");
        attributes.add("blinkyDist");
        attributes.add("inkyDist");
        attributes.add("pinkyDist");
        attributes.add("sueDist");
        attributes.add("blinkyDir");
        attributes.add("inkyDir");
        attributes.add("pinkyDir");
        attributes.add("sueDir");
        attributes.add("numberOfNodesInLevel");
        attributes.add("numberOfTotalPillsInLevel");
        attributes.add("numberOfTotalPowerPillsInLevel");

        classOfAttributes.add("UP");
        classOfAttributes.add("LEFT");
        classOfAttributes.add("DOWN");
        classOfAttributes.add("RIGHT");
        classOfAttributes.add("NONE");
    }

    public Node generateTree(ArrayList<DataTuple> trainingData, ArrayList<String> attributes) {
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
        MOVE bestMove = null;

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