// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFTag;
import org.freehep.graphicsio.emf.EMFRenderer;

/**
 * PolyBezierTo TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: freehep-graphicsio-emf/src/main/java/org/freehep/graphicsio/emf/gdi/PolyBezierTo.java c0f15e7696d3 2007/01/22 19:26:48 duns $
 */
public class PolyBezierTo extends AbstractPolygon {
	private Rectangle bounds;
	
    public PolyBezierTo() {
        super(5, 1, null, 0, null);
    }

    public PolyBezierTo(Rectangle bounds, int numberOfPoints, Point[] points) {
        super(5, 1, bounds, numberOfPoints, points);
        this.bounds = bounds;
    }

    protected PolyBezierTo (int id, int version, Rectangle bounds, int numberOfPoints, Point[] points) {
        super(id, version, bounds, numberOfPoints, points);
        this.bounds = bounds;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

    	bounds = emf.readRECTL();
        int n = emf.readDWORD();
        return new PolyBezierTo(bounds, n, emf.readPOINTL(n));
    }

    /**
     * displays the tag using the renderer
     *
     * @param renderer EMFRenderer storing the drawing session data
     */
    public void render(EMFRenderer renderer) {
        Point[] points = getPoints();
        int numberOfPoints = getNumberOfPoints();
        GeneralPath currentFigure = renderer.getFigure();
        boolean nodraw = false;
        
        
        if (nodraw == false) {
        if (points != null && points.length > 0) {

            Point p1, p2, p3;
            for (int point = 0; point < numberOfPoints; point = point + 3) {
                // add a point to gp
                p1 = points[point];
                p2 = points[point + 1];
                p3 = points[point + 2];
                currentFigure.curveTo(
                    (float)p1.getX(), (float)p1.getY(),
                    (float)p2.getX(), (float)p2.getY(),
                    (float)p3.getX(), (float)p3.getY());                
            }
/*            currentFigure.lineTo(points[points.length-1].getX(),
            		points[points.length-1].getY());
            		*/
            		
        }
        /*AffineTransform at = new AffineTransform ();
        
        double h = bounds.getHeight();
        double w = bounds.getWidth();
        
       // at.translate(bounds.getX(), bounds.getY());
        //renderer.setTranslate(at);
        
        double scaleX = currentFigure.getBounds2D().getWidth()/w;
        double scaleY = currentFigure.getBounds2D().getHeight()/h;
        at.scale(scaleX, scaleY);
        renderer.setTransform(at);*/      
        
      /*  renderer.resetTransformation();
        AffineTransform at = new AffineTransform ();	
        currentFigure.getBounds2D();
        renderer.setTranslate(at);*/
        
        
        }
    }
}
