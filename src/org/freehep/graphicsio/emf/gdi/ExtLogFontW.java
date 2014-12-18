// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Font;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFConstants;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFRenderer;

/**
 * EMF ExtLogFontW
 * 
 * @author Mark Donszelmann
 * @version $Id: freehep-graphicsio-emf/src/main/java/org/freehep/graphicsio/emf/gdi/ExtLogFontW.java c0f15e7696d3 2007/01/22 19:26:48 duns $
 */
public class ExtLogFontW implements EMFConstants, GDIObject {

    private LogFontW font;

    private String fullName;
    private String style;    
    private String script;
    private int signature;
    private int numAxes;
    private int arrayValues[];

    public ExtLogFontW(LogFontW font, String fullName, String style,
            String script, int signature, int numAxes, int[] arrayValues) {
        this.font = font;
        this.fullName = fullName;
        this.style = style;
        this.script = script;       
        this.signature = signature;
        this.numAxes = numAxes;
        this.arrayValues = arrayValues;
    }

    public ExtLogFontW(Font font) {
        this.font = new LogFontW(font);
        this.fullName = "";
        this.style = "";
        this.script = "";       
        this.signature =  0x08007664;
        this.numAxes = 0;
        this.arrayValues = new int[16];
    }

    public ExtLogFontW(EMFInputStream emf) throws IOException {
        font = new LogFontW(emf);
        fullName = emf.readWCHAR(64);
        style = emf.readWCHAR(32);           
        script= emf.readWCHAR(32);
        signature = emf.readDWORD();
        numAxes= emf.readDWORD();
        this.arrayValues = new int[numAxes];
        for (int i=0; i < numAxes; i++)
        	arrayValues[i] = emf.readDWORD();      
    }

    public void write(EMFOutputStream emf) throws IOException {
        font.write(emf);
        emf.writeWCHAR(fullName, 64);
        emf.writeWCHAR(style, 32);
        emf.writeWCHAR(script, 32);
        emf.writeDWORD(signature);
        emf.writeDWORD(numAxes);
        for (int i=0; i < numAxes; i++)
        	 emf.writeDWORD(arrayValues[i]);        
    }

/*    public String toString() {
        return super.toString() +
            "\n  LogFontW\n" + font.toString() +
            "\n    fullname: " + fullName +
            "\n    style: " + style +
            "\n    version: " + version +
            "\n    stylesize: " + styleSize +
            "\n    match: " + match +
            "\n    vendorID: " + vendorID +
            "\n    culture: "  + culture +
            "\n" + panose.toString();
    }*/

    public String toString() {
/*        return super.toString() +
            "\n  " + font.toString() +
            "\n    fullname: " + fullName +
            "\n    style: " + style +
            "\n    version: " + version +
            "\n    stylesize: " + styleSize +
            "\n    match: " + match +
            "\n    vendorID: " + vendorID +
            "\n    culture: "  + culture +
            "\n" + signature;
 */       
        return 	"\n  " + font.toString() +
                "\n    fullname: " + fullName +
                "\n    style: " + style +
                "\n    script: " + script +
                "\n    signature: " + signature +
                "\n    numAxes: " + numAxes ;
    }

    /**
     * displays the tag using the renderer
     *
     * @param renderer EMFRenderer storing the drawing session data
     */
    public void render(EMFRenderer renderer) {
        renderer.setFont(font.getFont());
    }
}
