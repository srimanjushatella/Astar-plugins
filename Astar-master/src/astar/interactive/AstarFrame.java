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

import java.awt.BorderLayout;
import static java.awt.BorderLayout.CENTER;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * Started from this demo: http://www.javacodex.com/More-Examples/2/4
 * @author roncoleman
 */
public class AstarFrame extends JFrame {

    WorldPanel worldPanel;

    public static void main(String[] arguments) {
        AstarFrame mainFrame = new AstarFrame();

        mainFrame.setVisible(true);

    }

    protected SingleStepAstar astar;
    protected JTextField stepSizeField;
    protected JCheckBox runEndCheckBox;
    protected int seed = 0;
    protected int level = 10;
    protected char[][] tileMap;
    protected boolean debug = false;

    public AstarFrame() {
        init();
    }

    protected final void init() {        
        initWorld();
        
        initFrame();
        
        String value = System.getProperty("astar.debug");
        if(value != null && value.toLowerCase().equals("true"))
            debug = true;
    }
    
    protected void initWorld() {
        astar = new SingleStepAstar();
        
        if(debug) System.out.println(tileMap.length + " x " + tileMap[0].length);
    }
    
    protected void initFrame() {
        setTitle("Interactive A*");
        
        setDefaultLookAndFeelDecorated(true);

        setSize(new Dimension(500, 500));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane = getContentPane();

        // Configure the world panel where
        worldPanel = new WorldPanel(astar.getTileMap());

        worldPanel.setPreferredSize(new Dimension(800, 800));

        JScrollPane scroller = new JScrollPane(worldPanel);

        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.getVerticalScrollBar().setUnitIncrement(10);
        scroller.getHorizontalScrollBar().setUnitIncrement(10);

        contentPane.add(BorderLayout.CENTER, scroller);

        // Configure the control panel
        JPanel controlPanel = new JPanel();

        controlPanel.setLayout(new BorderLayout());

        controlPanel.add(BorderLayout.EAST, new QuitButton());
        //create the export button.
        controlPanel.add(BorderLayout.CENTER, new ExportButton(this));
        // Create the step control subpanel
        JPanel stepPanel = new JPanel(new BorderLayout());

        // Add the checkbox that allows automated steps
        runEndCheckBox = new JCheckBox("Run to end");

        runEndCheckBox.setSelected(false);

        stepPanel.add(BorderLayout.CENTER, runEndCheckBox);

        // Add manual single-step button
        stepPanel.add(BorderLayout.WEST, new StepButton(this));

        controlPanel.add(BorderLayout.WEST, stepPanel);

        contentPane.add(BorderLayout.SOUTH, controlPanel);

        contentPane.setPreferredSize(new Dimension(500, 500));

        setLocationRelativeTo(null);

//      setVisible(true);        
    }
}
