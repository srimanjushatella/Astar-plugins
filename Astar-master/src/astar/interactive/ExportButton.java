/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package astar.interactive;

import astar.util.Node;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;

/**
 * This class implements the Export button.
 * @author TSM
 */
public class ExportButton extends JButton implements ActionListener {

     /**
     * Initializes the button.
     */
    protected void init() {
        this.addActionListener(this);
    }

    private final AstarFrame mainframe;
    
     public ExportButton(AstarFrame frame) {
        super("Export");

        this.mainframe = frame;

        init();
    }
    
     /**
     * Handles when the button is clicked.
     * @param e Event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        WorldPanel panel = mainframe.worldPanel;

        BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics g = image.createGraphics();

        panel.paint(g);

        int pathdistance = Math.round((float) mainframe.astar.getGoal().getSteps());

        String msg = "Model: " + mainframe.astar.getModel().getClass().getSimpleName();

        msg += "\n Heuristic: " + mainframe.astar.getEstimator().getClass().getSimpleName();

        msg += "\nDistance: " + pathdistance;

        msg += "\nNodes: " + Node.idCount;
       
        g.setColor(Color.ORANGE);

        g.drawString(msg, panel.CELL_SIZE*1, panel.CELL_SIZE*1);

        try {
            ImageIO.write(image, "png", new File("new_image.png"));

        } catch (IOException ex) {
            Logger.getLogger(ExportButton.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

    
