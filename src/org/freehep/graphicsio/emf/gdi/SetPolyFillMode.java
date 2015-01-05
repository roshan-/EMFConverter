// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;
import java.awt.geom.GeneralPath;

import org.freehep.graphicsio.emf.EMFConstants;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;
import org.freehep.graphicsio.emf.EMFRenderer;

/**
 * SetPolyFillMode TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: freehep-graphicsio-emf/src/main/java/org/freehep/graphicsio/emf/gdi/SetPolyFillMode.java c0f15e7696d3 2007/01/22 19:26:48 duns $
 */
public class SetPolyFillMode extends EMFTag implements EMFConstants {

    private int mode;

    public SetPolyFillMode() {
        super(19, 1);
    }

    public SetPolyFillMode(int mode) {
        this();
        this.mode = mode;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

    	int mode = emf.readDWORD();
    	if ((mode != EMFConstants.ALTERNATE) && (mode != WINDING))
    		System.out.println("ERROR**********************");
        return new SetPolyFillMode(mode);
    }

    public void write(int tagID, EMFOutputStream emf) throws IOException {
        emf.writeDWORD(mode);        
    }

    public String toString() {
        return super.toString() + "\n  mode: " + mode;
    }

    /**
     * displays the tag using the renderer
     *
     * @param renderer EMFRenderer storing the drawing session data
     */
    public void render(EMFRenderer renderer) {
        renderer.setWindingRule(getWindingRule(mode));
    }

    /**
     * gets a winding rule for GeneralPath creation based on
     * EMF SetPolyFillMode.
     *
     * @param polyFillMode PolyFillMode to convert
     * @return winding rule
     */
    private int getWindingRule(int polyFillMode) {
    	//DIA3 Esta cruzado MALL!!!!
        /*if (polyFillMode == EMFConstants.WINDING) {
            return GeneralPath.WIND_EVEN_ODD;
        } else if (polyFillMode == EMFConstants.ALTERNATE) {
            return GeneralPath.WIND_NON_ZERO;
        } else {
            return GeneralPath.WIND_EVEN_ODD;
        }*/
        
        if (polyFillMode == EMFConstants.WINDING) {
            return GeneralPath.WIND_NON_ZERO;
        } else if (polyFillMode == EMFConstants.ALTERNATE) {
            return GeneralPath.WIND_EVEN_ODD;
        } else {
            return GeneralPath.WIND_EVEN_ODD;
        }
    }

}
