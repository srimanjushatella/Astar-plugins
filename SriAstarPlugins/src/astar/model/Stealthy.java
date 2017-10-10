/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package astar.model;

import astar.plugin.IModel;
import static astar.util.Helper.tracksWall;
import astar.util.Node;

/**
 * This class implements the stealthy model.
 * @author TSM
 */
public class Stealthy implements IModel {
    
    protected char[][] tileMap;
    /**
    * Initializes the model with the map.
    * It is invoked once before the start of pathfinding.
    * @param tileMap Map with tileMap[row][col].
    */
    @Override
    public void init(char[][] tileMap) {
        this.tileMap = tileMap;
    }
    
    /**
     * Adjusts the heuristic estimate based on where the algorithm is.
     * @param heuristic Heuristic estimate
     * @param curNode Current node
     * @param adjNode Adjacent neighboring node in free-space. 
     * @return Shaped heuristic estimate.
     */
    @Override
    public double shape(double heuristic, Node curNode, Node adjNode) {
         if (tracksWall(tileMap, adjNode) || tracksWall(tileMap, curNode)) {
             //reduction of heuristic results to move the agent along the wall.
             heuristic -= heuristic * 0.75; 
         }
        return heuristic;
    }

    @Override
    public void complete(Node curNode) {
    }
    
}
