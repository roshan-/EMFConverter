package org.freehep.graphicsio.emf;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class EMFASFViewer {

	private ViewerPanel panel;
    /**
     * Default constructor for Test.class
     */
    public EMFASFViewer() {
        initComponents();
    }

    /**
     * Initialize GUI and components (including ActionListeners etc)
     */
    private void initComponents() {
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new ViewerPanel();
        jFrame.add(panel);

        //pack frame (size JFrame to match preferred sizes of added components and set visible
        jFrame.pack();
        jFrame.setVisible(true);
    }
    
    
    public void  setImageToDraw(Graphics2D g) {
    	panel.paintComponent(g);
    }
}

class ViewerPanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
    }
    
 
}