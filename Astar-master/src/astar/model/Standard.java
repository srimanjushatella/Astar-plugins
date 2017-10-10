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
package astar.model;

import astar.plugin.IModel;
import astar.util.Node;

/**
 * This class implements the standard model.
 * @author Ron Coleman
 */
public class Standard implements IModel {
    protected char[][] tileMap = null;

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
     * This method is invoked for each neighboring node of curNode in free-space.
     * For level 5 worlds, it may be invoked several hundred times during pathfinding.
     * Typically this method uses the tile map to determine if the immediate,
     * local path (i.e., curNode and adjNode and curNode.parent) is
     * changing in its 2nd derivative. Note the adjNode is the node we're adding
     * to open-list and it is this node's heuristic we shape, if necessary.
     * curNode is current lowest cost node and already on the open-list, soon to
     * be transferred on the closed-list.
     * @param heuristic Heuristic estimate
     * @param curNode Current node
     * @param adjNode Adjacent neighboring node in free-space. 
     * @return Shaped heuristic estimate.
     */
    @Override
    public double shape(double heuristic, Node curNode, Node adjNode) {
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
    }
}
