/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package astar.model;

import astar.plugin.IModel;
import astar.util.Helper;
import astar.util.Node;

/**
 * This class implements the Aesthetic model.
 *
 * @author TSM
 */
public class Pretty implements IModel {

    protected char[][] tileMap = null;

    /**
     * Initializes the model with the map. It is invoked once before the start
     * of pathfinding.
     *
     * @param tileMap Map with tileMap[row][col].
     */
    @Override
    public void init(char[][] tileMap) {
        this.tileMap = tileMap;
    }

    /**
     * Adjusts the heuristic estimate based on where the algorithm is.
     *
     * @param heuristic Heuristic estimate
     * @param curNode Current node
     * @param adjNode Adjacent neighboring node in free-space.
     * @return Shaped heuristic estimate.
     */
    @Override
    public double shape(double heuristic, Node curNode, Node adjNode) {
        boolean isZagging = false;
        boolean isTracking = Helper.tracksWall(tileMap, adjNode);

        if (curNode.getParent() != null) {
            Node parentNode = curNode.getParent();

            int dCol1 = adjNode.getCol() - curNode.getCol();
            int dCol2 = curNode.getCol() - parentNode.getCol();
            int dRow1 = adjNode.getRow() - curNode.getRow();
            int dRow2 = curNode.getRow() - parentNode.getRow();

            // The zigzag path occurs if either of these consitions are held true
            // (A(i-2).column - A(i-1).column) != (A(i-1).column - A(i).column) or
            // (A(i-2).row - A(i-1).row) != (A(i-1).row - A(i).row)
            isZagging = (dCol1 != dCol2 || dRow1 != dRow2);
        }
        if (isZagging == false) {
            //The inertia gets incremented by 1 if there is no zigzag.
            adjNode.setInertia(1 + curNode.getInertia());
        }

        //The experimental value of Tau is 8.
        if (adjNode.getInertia() >= 8) {
            return heuristic;
        }

        if (!isZagging && !isTracking) {
            return heuristic;
        }
        //increased penalty on the agent when it zigzags.     
        if (isZagging && isTracking) {
            return heuristic + 130;
        }
        //increased penalty whennever the agent tracks a wall.
        if (!isZagging && isTracking) {
            return heuristic + 100;
        }

        if (isZagging && !isTracking) {
            return heuristic + 2;
        }

        return heuristic;
    }

     /**
     * Completes the model.
     * This method is invoked exactly once when we reach the goal.
     * The curNode uses backward pointing links (i.e., parent attributes) to
     * find the start node which will have a null parent. Thus, this method has
     * access to the entire path and can "tweak" it as needed.
     * @param curNode Current (or goal node)
     */
    @Override
    public void complete(Node curNode) {
        while (curNode.getParent().getParent().getParent() != null) {
            fixBridges(curNode);
            curNode = curNode.getParent();
        }
    }
    
     /**
     * This method fixes the bridges in the path of the agent.
     */
    public void fixBridges(Node curNode) {
        Node parent = curNode.getParent();
        Node grandParent = parent.getParent();

        Node greatGrandParent = grandParent.getParent();
        if (curNode.getRow() == greatGrandParent.getRow()) {

            parent.setRow(curNode.getRow());
            grandParent.setRow(curNode.getRow());
        } else if (curNode.getCol() == greatGrandParent.getCol()) {
            parent.setCol(curNode.getCol());
            grandParent.setCol(curNode.getCol());

        }
    }
}
