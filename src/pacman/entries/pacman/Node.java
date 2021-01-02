package pacman.entries.pacman;

import pacman.game.Constants;

import java.util.HashMap;
import java.util.Map;

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

    public void addChild(Node n, String value){
        children.put(value,n);
    }

    public void print(String indent) {

        if (this.isLeaf) {
            System.out.print(indent);
            System.out.println("  └─ Return " + label);
        }
        Map.Entry<String, Node>[] nodes = children.entrySet().toArray(new Map.Entry[0]);
        for (int i = 0; i < nodes.length; i++) {
            System.out.print(indent);
            if (i == nodes.length - 1) {
                System.out.println("└─ \"" + label + "\" = " + nodes[i].getKey() + ":");
                nodes[i].getValue().print(indent + "    ");
            } else {
                System.out.println("├─ \"" + label + "\" = " + nodes[i].getKey() + ":");
                nodes[i].getValue().print(indent + (char)0x007C +"   ");
            }
        }
    }
}
