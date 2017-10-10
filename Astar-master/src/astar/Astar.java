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
package astar;

import astar.aes.World;
import astar.heuristic.Euclidean;
import astar.plugin.ILevelGenerator;
import astar.pcg.Basic;
import astar.pcg.Wells;
import astar.plugin.IModel;
import astar.util.Helper;
import astar.util.Node;
import static astar.util.Constant.*;
import astar.model.Standard;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import astar.plugin.IEstimator;

/**
 * Main class to implement A* path finding.
 * @author Ron Coleman
 */
public class Astar {          
    public final double SQRT_2 = Math.sqrt(2);

    public final static boolean PRIORITY_STRAIGHT = false;
    public final static int NO_LIMIT = 10000;
    
    protected IEstimator estimator = new Euclidean(); 
    protected ILevelGenerator levelGenerator = new Wells();
    protected IModel model = new Standard();
    
    protected BufferedReader reader;
    protected int width;
    protected int height;
    protected char[][] tileMap;
    protected int goalCol = -1;
    protected int goalRow = -1;
    protected int startCol = -1;
    protected int startRow = -1;
    protected ArrayList<Node> openList = new ArrayList<>();
    protected ArrayList<Node> closedList = new ArrayList<>();    
    protected Node goal;
    protected Node start;
    protected int level = 5;
    protected int seed = 0;
    protected boolean debug;

    // Col, row offsets relative to current position in map.
    // These allow A* look in its neighborhood.
    protected int[][] neighborOffsets = {
        { -1,  0 },   // W  neighbor
        { -1, -1 },   // NW "
        {  0, -1 },   // N  "
        {  1, -1 },   // NE "
        {  1,  0 },   // E  "
        {  1,  1 },   // SE "
        {  0,  1 },   // S  "
        { -1,  1 }    // SW "
    };

    // Next offset index
    protected int indOffset;
    
    /**
     * Constructor
     */
    public Astar() {
        initConfig();
        
        initLevel();
    }
    
    /**
     * Constructor.
     * @param name File name.
    */
    public Astar(String name) {
        this();
        
        try {
          reader = new BufferedReader(new FileReader(name));
        }
        catch(FileNotFoundException e) {

        }
    }

    /**
     * Constructor.
     * @param tileMap Tile map.
     * @param srcCol Source x in world
     * @param srcRow Source y in world
     * @param goalCol Destination x in world
     * @param goalRow Destination y in world
     */
    public Astar(char[][] tileMap, int srcCol, int srcRow, int goalCol, int goalRow) {
        this();
        
        this.tileMap = tileMap;
        this.startCol = srcCol;
        this.startRow = srcRow;
        this.goalCol = goalCol;
        this.goalRow = goalRow;
        this.width = tileMap[0].length;
        this.height = tileMap.length;
    }
    /**
     * Initializes the configuration.
     */
    protected final void initConfig() {
        // Clear the node count
        Node.idCount = 0;
        
        String value = System.getProperty("astar.debug");
        if(value != null && value.equals("true"))
            debug = true;
            
        value = System.getProperty("astar.seed");
        if (value != null) {
            seed = Integer.parseInt(value);
        }

        value = System.getProperty("astar.level");
        if (value != null) {
            level = Integer.parseInt(value);
        }
        
        String className = System.getProperty("astar.heuristic");
        if(className != null) {
            try {
                Class<?> cl = Class.forName(className);
                 estimator = (IEstimator) cl.newInstance();
                 
            } catch (ClassNotFoundException ex) {
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(Astar.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        className = System.getProperty("astar.model");
        if(className != null) {
            try {
                Class<?> cl = Class.forName(className);
                 model = (IModel) cl.newInstance();
                 
            } catch (ClassNotFoundException ex) {
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(Astar.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Initializes the level.
     */
    protected final void initLevel() {
        // Get a level generator
        String className = System.getProperty("astar.lg");
        
        if (className == null)
            className = "astar.pcg.Wells";

        try {
            Class<?> clzz = Class.forName(className);

            this.levelGenerator = (ILevelGenerator) clzz.newInstance();
            
            this.levelGenerator.init(seed);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | SecurityException ex) {
            System.err.println("bad level generator");
            System.exit(1);
        }
        
        // Generate the level
        this.tileMap = levelGenerator.generateLevel(level);
        
        this.width = tileMap[0].length;
        
        this.height = tileMap.length;
        
        // Identify the start and destination
        for(int row=0; row < tileMap.length; row++) {
            for(int col=0; col < tileMap[0].length; col++) {
                char tile = tileMap[row][col];
                
                switch(tile) {
                    case World.PLAYER_START_TILE:
                        this.startCol = col;
                        this.startRow = row;
                        break;
                        
                    case World.GATEWAY_TILE:
                        this.goalCol = col;
                        this.goalRow = row;
                        break;
                }

            }
        } 

        if (this.startCol < 0 && this.startRow < 0 && this.goalCol >= 0 && this.goalRow >= 0) {
            System.err.println("bad tile map");
            System.exit(1);
        }  
        
        // Initialize the model
        model.init(tileMap);
    }
    
    /**
     * Begins the search.
     */
    public void begin() {
        goal = start = null;
    }
    
    /**
     * Find path find source to goal.
     * @return Destination node if path found, null if no path found.
     */
    public Node find() {
        goal = new Node(goalCol, goalRow);
        start = new Node(startCol, startRow);

        moveToOpen(start);

        while(!openList.isEmpty()) {
            Node curNode = getLowestCostNode();

            if(curNode.equals(goal))
                return relink(curNode);

            moveToClosed(curNode);

            // Reset the adjacency state
            reset();

            do {
                // Get next adjacent to current node
                Node adjNode = getAdjacent(curNode);

                if(adjNode == null)
                    break;

                double heuristic = calculateHeuristic(adjNode,goal);
                                
                double cost = adjNode.getSteps();
                
                cost += model.shape(heuristic, curNode, adjNode);                   

                adjNode.setCost(cost);
                
                openList.add(adjNode);
                
                if(Node.idCount > Integer.MAX_VALUE)
                	return null;
                
            } while(true);
        }
        
        return null;
    }
    
    /**
     * Relink the child nodes properly since the child references
     * are leftover references from scanning the adjacent nodes.
     * @param path
     * @return
     */
    protected Node relink(Node path) {
    	Node anode = path;
        
    	Node child = null;
        
    	while(anode != null) {
    		anode.setChild(child);
                
    		child = anode;
                
    		anode = anode.getParent();
    	}
    	
    	return path;
    }
    
    /**
     * Get next adjacent node relative to parent node.
     * @param parent Parent to this node
     * @return Next adjacent node.
     */
    protected Node getAdjacent(Node parent) {
        int x = parent.getCol();
        int y = parent.getRow();

        while(indOffset < neighborOffsets.length) {
            int adjX = x + neighborOffsets[indOffset][0];
            int adjY = y + neighborOffsets[indOffset][1];

            indOffset++;

            if(adjX < 0 || adjX >= width || adjY < 0 || adjY >= height)
                continue;

            if(onOpenList(adjX, adjY) || onClosedList(adjX, adjY) || Helper.isObstacle(tileMap, adjX, adjY))
                continue;

            Node node = new Node(adjX, adjY, parent);
            
            return node;
        }

        return null;
    }

    /**
     * Move node to open list.
     * @param node Node to put on open list.
     */
    protected void moveToOpen(Node node) {
        openList.add(node);
    }

    /** Determines if node on open list.
     * @param node Node to test.
     * @return True if node is on open list.
     */
    protected boolean onOpenList(Node node) {
      return onOpenList(node.getCol(),node.getRow());
    }

    /** Determines if node at x, y on open list.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return True if node at coordinate on open list.
     */
    protected boolean onOpenList(int x,int y) {
        ListIterator iter = openList.listIterator();

        while(iter.hasNext()) {
            Node candidate = (Node)iter.next();
            if(candidate.getCol() == x && candidate.getRow() == y) {
              return true;
            }
        }

        return false;
    }

    /**
     * Determines if node on closed list.
     * @param node Node to test.
     * @return True if node on closed list.
     */
    protected boolean onClosedList(Node node) {
        return onClosedList(node.getCol(),node.getRow());
    }

    /**
     * Determines if node at x, y on closed list.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return True if node at coordinate on closed list.
     */
    protected boolean onClosedList(int x,int y) {
        ListIterator iter = closedList.listIterator();

        while(iter.hasNext()) {
            Node candidate = (Node)iter.next();
            if(candidate.getCol() == x && candidate.getRow() == y) {
              return true;
            }
        }

        return false;
    }

    /**
     * Determines if node is an obstacle.
     * @param node Node to test.
     * @return True if node an obstacle.
     */
    protected boolean isObstacle(Node node) {
      return Helper.isObstacle(tileMap, node.getCol(),node.getRow());
    }


    /**
     * Move node to closed list.
     * @param node Node to move to closed list.
     */
    protected void moveToClosed(Node node) {
        ListIterator iter = openList.listIterator();

        while(iter.hasNext()) {
            Node candidate = (Node)iter.next();
            
            if(candidate == node) {
                iter.remove();
                
                closedList.add(node);
            }
        }
    }

    /**
     * Calculate heuristic part of cost using default geometry.
     * @param adj Adjacent node.
     * @param goal Destination node.
     * @return Distance.
     */
    protected double calculateHeuristic(Node adj, Node goal) {
        double dist = estimator.distance(adj, goal);
        
        return dist;
    }
    
    /** Find lowest cost node.
     *  Note: could be improved if nodes added using insertion sort.
     *  @return Node with lowest cost.
     */
    protected Node getLowestCostNode() {
        ListIterator iter = openList.listIterator();
        
        double minCost = Double.MAX_VALUE;
        
        Node minNode = null;
        
        while(iter.hasNext()) {
            Node node = (Node)iter.next();
            
            double cost = node.getCost();
            
            if(cost < minCost) {
                minCost = cost;
                
                minNode = node;
            }
        }
        
        return minNode;
    }

    /**
     * Reset the offset index.
     */
    protected void reset() {
        indOffset = 0;
    }

    /**
     * Gets the start node.
     * @return Start node
     */
    public Node getStart() {
        return start;
    }
    
    /**
     * Gets the goal node.
     * @return Goal node
     */
    public Node getGoal() {
        return goal;
    }
    
    /**
     * Gets the 2D tile map.
     * @return map[y][x] or map[row][col]
     */
    public char[][] getTileMap() {
    	return tileMap;
    }
    
    /**
     * Gets the open list of nodes. 
     * @return Open list
     */
    public ArrayList<Node> getOpen() {
        return this.openList;
    }
    
    /**
     * Gets the closed list.
     * @return Close list
     */
    public ArrayList<Node> getClosed() {
        return this.closedList;
    }
    
    /**
     * Gets the heuristic estimator or "geometry".
     * @return Estimator
     */
    public IEstimator getEstimator() {
        return this.estimator;
    }
    
    /**
     * Gets the model.
     * @return Model
     */
    public IModel getModel() {
        return model;
    }
    
    /**
     * Load the tile map from a file.
     * Must invoke constructor with file parameter before invoking this method.
     */
    public void loadMap() {
        try {
            StringTokenizer dims = new StringTokenizer(reader.readLine());
        	
            width = Integer.parseInt(dims.nextToken());

            height = Integer.parseInt(dims.nextToken());

            tileMap = new char[height][width];

            for(int k=0; k < height; k++) {
                String srow = reader.readLine();

                if(srow.length() != width)
                    throw new Exception("bad row width");

                for(int j=0; j < width; j++){
                  char sym = srow.charAt(j);
                  tileMap[k][j] = sym;

                  if(sym == SYM_DEST) {
                      goalCol = j;
                      goalRow = k;
                  }

                  if(sym == SYM_SRC) {
                      startCol = j;
                      startRow = k;
                  }
                }
            }
        }
        catch(Exception e) {
          System.err.println(e);
        }
    }

    /**
     * Main method (for debugging).
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      long totalt = 0;
      
      for(int j=0; j < 1000; j++) {
        
        Basic lg = new Basic();
        lg.init(101);
        
        lg.layoutSrcDest();
        
        //lg.print();

        Node.idCount = 0;
        Astar astar0 =
            new Astar(lg.getMap(),lg.getSrcX(),lg.getSrcY(),lg.getDestX(),lg.getDestY());

//        Node node0 = astar0.find();
        
        Node.idCount = 0;
        
        lg.layoutBarriers();

//        double dx = Math.abs(lg.getSrcX()-lg.getDestX());
//        double dy = Math.abs(lg.getSrcY()-lg.getDestY());
        
        long t0 = System.currentTimeMillis();        
        long t1 = System.currentTimeMillis();
        
        totalt += (t1 - t0);
      }
      
      System.out.println("runtime = "+totalt);
    }
}
