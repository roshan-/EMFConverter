// Copyright 2001, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;
import org.freehep.graphicsio.emf.EMFRenderer;

/**
 * ExtCreateFontIndirectW TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: freehep-graphicsio-emf/src/main/java/org/freehep/graphicsio/emf/gdi/ExtCreateFontIndirectW.java c0f15e7696d3 2007/01/22 19:26:48 duns $
 */
public class ExtCreateFontIndirectW extends EMFTag {

    private int index;

    private ExtLogFontW font;
    private LogFontPanose panoseFont;

    public ExtCreateFontIndirectW() {
        super(82, 1);
    }

    public ExtCreateFontIndirectW(int index, ExtLogFontW font) {
        this();
        this.index = index;
        this.font = font;
    }
    public ExtCreateFontIndirectW(int index, LogFontPanose pfont) {
        this();
        this.index = index;
        this.panoseFont = pfont;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {
//http://msdn.microsoft.com/en-us/library/cc231075.aspx
  //http://msdn.microsoft.com/en-us/library/cc230619.aspx
    	int datalen = len -12;
    	if (datalen <=  0x0140)
    		return new ExtCreateFontIndirectW(
    	            emf.readDWORD(),
    	            new LogFontPanose(emf));
    	else
    		return new ExtCreateFontIndirectW(
    	            emf.readDWORD(),
    	            new ExtLogFontW(emf));    	    	
    }

    public void write(int tagID, EMFOutputStream emf) throws IOException {
        emf.writeDWORD(index);
        font.write(emf);
    }

    public String toString() {
    	if (font != null)
        return super.toString() +
            "\n  index: 0x" + Integer.toHexString(index) +
            "\n" + font.toString();
    	else if(panoseFont != null)
    		return super.toString() +
    	            "\n  index: 0x" + Integer.toHexString(index) +
    	            "\n" + panoseFont.toString();
    	else 
    		return "ERROR";
    }

    /**
     * displays the tag using the renderer
     *
     * @param renderer EMFRenderer storing the drawing session data
     */
    public void render(EMFRenderer renderer) {
    	if (font != null)
        renderer.storeGDIObject(index, font);
    	else
    		renderer.storeGDIObject(index, panoseFont);
    }
}
