// Copyright 2001, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;
import org.freehep.graphicsio.emf.EMFRenderer;

/**
 * ExtCreatePen TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: freehep-graphicsio-emf/src/main/java/org/freehep/graphicsio/emf/gdi/ExtCreatePen.java c0f15e7696d3 2007/01/22 19:26:48 duns $
 */
public class ExtCreatePen extends EMFTag {

    private int index;

    private ExtLogPen pen;

    public ExtCreatePen() {
        super(95, 1);
    }

    public ExtCreatePen(int index, ExtLogPen pen) {
        this();
        this.index = index;
        this.pen = pen;
    }
    /**
     * HPEN ExtCreatePen(
  _In_  DWORD dwPenStyle,
  _In_  DWORD dwWidth,
  _In_  const LOGBRUSH *lplb,
  _In_  DWORD dwStyleCount,
  _In_  const DWORD *lpStyle
);
     */
   /* FIXME ASF
    * public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {
    	index = 2;
       	//int index = emf.readDWORD();
        int bmiOffset =  emf.readDWORD();
        int bmiSize =  emf.readDWORD();
        int brushOffset =  emf.readDWORD();
        int brushSize =  emf.readDWORD();
        return new ExtCreatePen(index, new ExtLogPen(emf));
    }*/
    
    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {    	    
       	int index = 1;
        int bmiOffset ;
        int bmiSize ;
        int brushOffset ;
        int brushSize;
        
      /*DIA 13  
        if (len == 48) {
        index = emf.readDWORD();
        bmiOffset =  emf.readDWORD();
        bmiSize =  emf.readDWORD();
        brushOffset =  emf.readDWORD();
        brushSize =  emf.readDWORD();
        } else if (len == 44) {
        	index = emf.readDWORD();            
            bmiSize =  emf.readDWORD();
            brushOffset =  emf.readDWORD();
            brushSize =  emf.readDWORD();
        }else if (len == 52) {
            index = emf.readDWORD();
            bmiOffset =  emf.readDWORD();
            bmiSize =  emf.readDWORD();
            brushOffset =  emf.readDWORD();
            brushSize =  emf.readDWORD();
            emf.readDWORD();
        }*/
        index = emf.readDWORD();
        bmiOffset =  emf.readDWORD();
        bmiSize =  emf.readDWORD();
        brushOffset =  emf.readDWORD();
        brushSize =  emf.readDWORD();
        ExtLogPen pen = new ExtLogPen(emf);
        //DIA14
        emf.popBuffer();
        return new ExtCreatePen(index, pen);
    }

    public void write(int tagID, EMFOutputStream emf) throws IOException {
        emf.writeDWORD(index);
        emf.writeDWORD(0); // offset to bmi
        emf.writeDWORD(0); // size of bmi
        emf.writeDWORD(0); // offset to brush bitmap
        emf.writeDWORD(0); // size of brush bitmap
        pen.write(emf);
    }

    public String toString() {
        return super.toString() +
            "\n  index: 0x" + Integer.toHexString(index) +
            "\n" + pen.toString();
    }

    /**
     * displays the tag using the renderer
     *
     * @param renderer EMFRenderer storing the drawing session data
     */
    public void render(EMFRenderer renderer) {
        renderer.storeGDIObject(index, pen);
    }
}
