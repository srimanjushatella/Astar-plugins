/*
 Copyright (c) Ron Coleman

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package astar.util;

/**
 * Node class.
 */
public class Node {

    /** Unique id counter for all nodes */
    public static int idCount = 0;
    public static double SQRT_2 = Math.sqrt(2);
    
    protected int col;
    protected int row;
    
    /** This node's parent */
    protected Node parent;
    
    /** This node's child -- NOT GENERALLY USED since a node typically
     * will have many children.
     */
    protected Node child;
    
    /** The f(n) cost */
    protected double cost = 0;
    
    /** Steps from the start */
    protected double steps = 0;
    
    /** This node's unique id */
    protected int id = idCount++;
    
    /** This is the propensity to continue without zags */
    protected int inertia = 0;

    /**
     * Constructor.
     *
     * @param col Column coordinate.
     * @param row Row coordinate.
     */
    public Node(int col, int row) {
        this.col = col;
        this.row = row;
        this.cost = Long.MAX_VALUE;
//        this.id = idCount++;
    }

    /**
     * Constructor.
     * @param col Column coordinate.
     * @param row Row coordinate.
     * @param parent Parent node of node.
     */
    public Node(int col, int row, Node parent) {
        this(col, row);

        this.parent = parent;

//        parent.child = this;
        this.steps = parent.steps;

        // If we're traveling straight horizontally or vertically, add 1 to
        // the steps. However, if travelling diagonally, add sqrt(2).
        int dr = row - parent.getRow();
        int dc = col - parent.getCol();

        int dir = dr * dc;

        if (dir == 0) {
            this.steps += 1.0;
        } else {
            this.steps += SQRT_2;
        }
    }

    /**
     * Copy constructor -- sort of
     *
     * @param node Node to copy.
     */
    public Node(Node node) {
        this(node.col, node.row);
    }

    /**
     * Get the parent node.
     *
     * @return Parent node.
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Get cost of the node, namely, steps plus heuristic.
     *
     * @return Cost.
     */
    public double getCost() {
        return cost;
    }

    /**
     * Tests if this node equal to another node.
     *
     * @param node Node.
     * @return True, if two nodes equal, false otherwise.
     */
    public boolean equals(Node node) {
        return node.col == col && node.row == row;
    }

    /**
     * Gets the node at col, row if it exists going forward this node.
     *
     * @param col Column
     * @param row Row
     * @return
     */
    public Node getNode(int col, int row) {
        if (equals(col, row)) {
            return this;
        }

        Node anode = this.getChild();

        while (anode != null) {
            if (anode.equals(col, row)) {
                return anode;
            }

            anode = anode.getChild();
        }

        return null;
    }

    /**
     * Tests if the node is equal to the x, y coordinate.
     *
     * @param col
     * @param row
     * @return
     */
    public boolean equals(int col, int row) {
        return this.col == col && this.row == row;
    }

    /**
     * Get X coordinate of node.
     *
     * @return X coordinate.
     */
    public int getCol() {
        return col;
    }

    /**
     * Get Y coordinate of node.
     *
     * @return Y coordinate.
     */
    public int getRow() {
        return row;
    }

    /**
     * Get steps from parent.
     *
     * @return Steps from parent.
     */
    public double getSteps() {
        return steps;
    }

    /**
     * Set the parent node.
     *
     * @param parent Parent node.
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * Set cost of node.
     *
     * @param cost Cost of node.
     */
    public void setCost(double cost) {
        this.cost = cost;
    }

    /**
     * Print the node path.
     */
    public void print() {
        Node node = this;

        do {
            System.out.println("(" + node.getCol() + "," + node.getRow() + ")");

            node = node.getParent();
        } while (node != null);
    }

    /**
     * Gets the child of this node.
     * @return Node
     */
    public Node getChild() {
        return child;
    }

    /**
     * Gets the child of this node.
     * @param child Child node
     */
    public void setChild(Node child) {
        this.child = child;
    }

    /**
     * Gets the step of this node from the start.
     * @param steps Number of steps
     */
    public void setSteps(int steps) {
        this.steps = steps;
    }

    /**
     * Sets the row coordinate.
     * @param row Row coordinate
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Sets the col coordinate.
     * @param col Column  coordinate
     */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * Gets the unique id of this node.
     * @return Id
     */
    public int getId() {
        return this.id;
    }
    
    /**
     * Gets the nodes inertia.
     * @return Inertia
     */
    public int getInertia() {
        return inertia;
    }
    
    /**
     * Sets the inertia.
     * @param inertia Inertia
     */
    public void setInertia(int inertia) {
        this.inertia = inertia;
    }

    @Override
    public String toString() {
        String pars = "parent(none)";
        if (parent != null) {
            int parentCol = parent.getCol();
            int parentRow = parent.getRow();

            pars = "parent(col: " + parentCol + " row: " + parentRow + ")";
        }

        String chis = "child(none)";
        if (child != null) {
            int childCol = child.getCol();
            int childRow = child.getRow();

            chis = "child(col:" + childCol + " row:" + childRow + ")";
        }

        return "(col: " + col + " row: " + row + ") " + pars + " " + chis;
    }
}
