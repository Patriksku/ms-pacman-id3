package pacman.entries.pacman;

import pacman.game.Constants;

import java.util.HashMap;

public class Node {

    boolean isLeaf = false;
    String label;
    HashMap<String, Node> children = new HashMap<>();


    public Node() {}

    public void setLabel(String label) {
        this.label = label;
    }

    public void setLeaf(Boolean value) {
        isLeaf = value;
    }
}
