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

import astar.plugin.IModel;
import astar.util.Node;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

/**
 * This class implements the step button.
 * @author Ron Coleman
 */
public final class StepButton extends JButton implements ActionListener {
    public final static int DELAY = 3;
    
    private final WorldPanel worldPanel;
    private final SingleStepAstar astar;
    private final JCheckBox runEndCheckBox;
    private int tries = 0;
    private final AstarFrame frame;
    
    /**
     * Constructor.
     * @param frame Parent frame
     */
    public StepButton(AstarFrame frame) {
        super("Step");
        
        this.frame = frame;
        
        this.worldPanel = frame.worldPanel;
        
        this.astar = frame.astar;
        
        this.runEndCheckBox = frame.runEndCheckBox;
        
        init();
    }
    
    /**
     * Initialize the button.
     */
    protected void init() {
        this.addActionListener(this); 
        
        astar.begin();
    }
    
    /**
     * Responds to a button click.
     * @param e Event
     */
    @Override
    public void actionPerformed(ActionEvent e) {  
        setEnabled(false);
        
        new Thread(new Runnable() {
            /**
             * Runs the game loop.
             */
            @Override
            public void run() {                                      
                do {
                    // Gets the current lowest cost node which is back linked
                    // to its parent.
                    Node head = astar.find1();
                    
                    tries++;
                    
                    // If the head is null, it means we didn't find a path
                    if(head == null) {
                        String msg = "Tries: " + tries;
                        msg += "\nElapsed time: " + 0;
                        
                        JOptionPane.showMessageDialog(frame, msg,"No path found",JOptionPane.ERROR_MESSAGE); 

                        return;
                    }

                    // Run one cylce of the game loop
                    
                    // Update the world panel with the state change
                    ArrayList<Node> openNodes = astar.getOpen();
                    ArrayList<Node> closedNodes = astar.getClosed();
                    
                    worldPanel.update(head, openNodes, closedNodes);
                    
                    // Render the world panel
                    worldPanel.render();
                    
                    // If the run-to-end box is NOT checked reenable this button
                    // and return since we're doing just one step.
                    if(!runEndCheckBox.isSelected()) {
                        setEnabled(true);
                        return;
                    }
                            
                    // If the head is the destination, then we're done                    
                    Node dest = astar.getGoal();
                    
                    if(head.equals(dest)) {
                        // If there's a model, tweak it and run update one last time
                        IModel model = astar.getModel();
                        
                        if(model != null) {
                            // Sleep here may not be necessary but allows last repaint run
                            sleep(DELAY);
                            
                            model.complete(head);
                            
                            worldPanel.update(head, openNodes, closedNodes);
                        }
                        
                        int distance = Math.round((float)head.getSteps());
                        
                        String msg = "Model: " + astar.getModel().getClass().getSimpleName();
                                
                        msg += "\nHeuristic: " + astar.getEstimator().getClass().getSimpleName();
                        
                        msg += "\nTries: " + tries; 
                        
                        msg += "\nDistance: " + distance;
                        
                        msg += "\nNodes: " + Node.idCount;
                        
                        JOptionPane.showMessageDialog(frame, msg);
                        
                        setEnabled(false);
                        
                        return;
                    }
                    
                    // If we haven't reached the goal, wait a while and try again.
                    sleep(DELAY);
                    
                } while (true);
            }
        }).start();
    }
    
    protected void loop() {
        
    }
    
    /**
     * Sleeps for specified amount of time.
     * @param time Specified time
     */
    private void sleep(int time) {
        try {
            Thread.sleep(time);
            
        } catch (InterruptedException ex) {
            
        }
    }
    
}
