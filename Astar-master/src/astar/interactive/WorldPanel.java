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

import astar.aes.World;
import astar.util.Node;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 * This class implements the main world panel which renders the pathfinding state.
 * @author Ron Coleman
 */
public class WorldPanel extends JPanel implements MouseListener, MouseMotionListener {
    public final int CELL_SIZE = 12;
    public final int CELL_INSET = 3;
    
    public final Color COLOR_CLOSED = new Color(204, 153, 255); //Color.MAGENTA;
    public final Color COLOR_STEP = new Color(0,204,102);
    public final Color COLOR_DEST = Color.RED;
    public final Color COLOR_WALL = new Color(64, 64, 64);
    
    private final int rowCount;
    private final int colCount;
   
    // Base x,y give the upper left corner of the world
    private int baseX = 0;
    private int baseY = 0;
    
    // Last base x,y is the last base x,y after releasing the mouse; this
    // allows relative movements.
    private int lastBaseX;
    private int lastBaseY;
    private int touchX;
    private int touchY;
    private char[][] map;
    private Node head;
    private Node start;
    private Node dest;
    private ArrayList<Node> open = new ArrayList<>();
    private ArrayList<Node> closed = new ArrayList<>();
    
    public WorldPanel(char[][] map) {
        this.map = map;
        
        this.rowCount = map.length;
        this.colCount = map[0].length;
        
        init();
    }
    
    public WorldPanel(int rowCount, int colCount) {
        super();
        
        this.rowCount = rowCount;
        this.colCount = colCount;
        
        init();
    }
    
    private void init() {
        addMouseListener(this);
        
        addMouseMotionListener(this);
        
        render();
    }
    
    public void update(Node head, ArrayList<Node> open, ArrayList<Node> closed) {
        this.head = head;
        
        this.open = open;
        
        this.closed = closed;
    }
    
    public void render() {
        repaint();
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // Render the world without steps        
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                char tile = map[row][col];

                int x = translateColToX(col);
                int y = translateRowToY(row);
                
                switch (tile) {
                    case World.PLAYER_START_TILE:
                        start = new Node(col, row);
                        
                        if(head != null)
                            continue;

                        g.setColor(COLOR_STEP);
                        g.fillOval(x, y, CELL_SIZE, CELL_SIZE);
                        
                        g.setColor(Color.BLACK);
                        g.drawOval(x, y, CELL_SIZE, CELL_SIZE);
                        break;

                    case World.WALL_TILE:
                        g.setColor(COLOR_WALL);
                        
                        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                        break;

                    case World.GATEWAY_TILE:
                        dest = new Node(col, row);
                        
                        g.setColor(Color.RED);
                        g.fillOval(x, y, CELL_SIZE, CELL_SIZE);
                        
                        g.setColor(Color.BLACK);
                        g.drawOval(x, y, CELL_SIZE, CELL_SIZE);
                        break;

                    case World.NO_TILE:
                    default:
                        g.setColor(Color.WHITE);
                        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        if (head == null)
            return;

        // Render the open cells
        g.setColor(Color.ORANGE);
        for (Node node : open) {
            int x = translateColToX(node.getCol());
            int y = translateRowToY(node.getRow());
            g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        }

        // Render the closed cells
        g.setColor(COLOR_CLOSED);
        for (Node node : closed) {
            int x = translateColToX(node.getCol());
            int y = translateRowToY(node.getRow());
            g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        }
        
        // Render the walk so far -- do this last because the current node
        // has been moved to the closed list.

        Node step = head;
        do {
            int x = translateColToX(step.getCol());
            int y = translateRowToY(step.getRow());
            
            g.setColor(COLOR_STEP);
            g.fillOval(x, y, CELL_SIZE, CELL_SIZE);
            
            g.setColor(Color.BLACK);
            g.drawOval(x, y, CELL_SIZE, CELL_SIZE);

            // Look back to parent to get how we got here
            step = step.getParent();
        } while (step != null);
        
        // Render the destination last
        int x = translateColToX(dest.getCol());
        int y = translateRowToY(dest.getRow());
        
        g.setColor(COLOR_DEST);
        g.fillOval(x, y, CELL_SIZE, CELL_SIZE);
        
        g.setColor(Color.BLACK);
        g.drawOval(x, y, CELL_SIZE, CELL_SIZE);
        
        // Render start and goal text
        g.setColor(Color.BLACK);
                        
        x = this.translateColToX(start.getCol());
        y = this.translateRowToY(start.getRow()) - 5;
        g.drawString("Start", x, y);
        
        x = this.translateColToX(dest.getCol());
        y = this.translateRowToY(dest.getRow()) - 5;
        g.drawString("Goal", x, y);     
    }
    
    
    private int translateColToX(int col) {
        int x = col * (CELL_SIZE + CELL_INSET) + CELL_INSET + baseX;
        
        return x;
    }
    
    private int translateRowToY(int row) {
        int y = row * (CELL_SIZE + CELL_INSET) + CELL_INSET + baseY;
        
        return y;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
//        System.out.println("clicked x = "+x+" y = "+y);
        
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point point = e.getPoint();

        touchX = point.x;
        touchY = point.y;
        
        repaint();        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        lastBaseX = baseX;
        lastBaseY = baseY;
        
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
//        System.out.println("entered x = "+x+" y = "+y);
        
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
//        System.out.println("exited x = "+x+" y = "+y);
        
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point point = e.getPoint();

        int dragToX = point.x;
        int dragToY = point.y;
        
        // Update the base x,y depending on how much we dragged the world
        baseX = lastBaseX + dragToX - touchX;
        baseY = lastBaseY + dragToY - touchY;
        
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
    
}
