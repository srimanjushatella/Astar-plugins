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

import astar.aes.World;

/**
 * This class implements helper methods.
 * @author Ron Coleman
 */
public class Helper {

    /**
     * Determines if node at column, row is an obstacle.
     * @param tileMap
     * @param col Column coordinate.
     * @param row Row coordinate.
     * @return True if node an obstacle.
     */
    public static boolean isObstacle(char[][] tileMap, int col, int row) {
        if(row < 0 || col < 0 || row >= tileMap.length || col >= tileMap[0].length)
            return false;
        
        char sym = tileMap[row][col];

        return sym == World.WALL_TILE;
    }
    
    /**
     * Returns true if this node is adjacent to an obstacle.
     * @param tileMap Tile map of the world
     * @param node Node in world
     * @return True if node is up against wall or obstacle
     */
    public static boolean tracksWall(char[][] tileMap, Node node) {
    	int col = node.getCol();
    	int row = node.getRow();
    	
    	return Helper.isObstacle(tileMap, col-1, row) ||
    	       Helper.isObstacle(tileMap, col+1, row) ||
    	       Helper.isObstacle(tileMap, col, row-1) ||
    	       Helper.isObstacle(tileMap, col, row+1);
    }
}
