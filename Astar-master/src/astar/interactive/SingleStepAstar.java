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
package astar.interactive;

import astar.Astar;
import astar.util.Node;

/**
 * This class implements a single step A*.
 * @author Ron Coleman
 */
public class SingleStepAstar extends Astar {
    
    public SingleStepAstar() {
        super();
    } 
    
    public Node find1() {
        if(start == null && goal == null) {
            goal = new Node(goalCol, goalRow);
            start = new Node(startCol, startRow);

            moveToOpen(start);
        }

        while (!openList.isEmpty()) {
            Node curNode = getLowestCostNode();

            if (curNode.equals(goal)) {
                return relink(curNode);
            }

            moveToClosed(curNode);

            // Reset the adjacency state
            reset();

            // Put all the adjacent nodes on the open list of possibilities
            do {
                // Get next adjacent to current node
                Node adjNode = getAdjacent(curNode);

                // If there are no more adjacents, we're done
                if (adjNode == null) {
                    break;
                }

                double heuristic = calculateHeuristic(adjNode, goal);

                double cost = adjNode.getSteps();
                
                cost += model.shape(heuristic, curNode, adjNode);
                
                adjNode.setCost(cost);

                openList.add(adjNode);

            } while (true);
            
            return curNode;
        }
        
        return null;
    }
}
