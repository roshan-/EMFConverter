// Copyright FreeHEP 2007.
package org.freehep.graphicsio.emf;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGeneratorContext.GraphicContextDefaults;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.util.SVGConstants;
import org.freehep.graphicsio.asf.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 * This class converts an EMF image to all available
 * grafik formats, e.g. PDF, or PNG
 *
 * @author Steffen Greiffenberg
 * @version $Id: freehep-graphicsio-emf/src/main/java/org/freehep/graphicsio/emf/EMFConverter.java 9c0688d78e6b 2007/01/30 23:58:16 duns $
 */
public class EMFConverter {

    /**
     * Looks for an (FreeHEP-) ExportFileType in class path
     * to create the selected output format for destFileName.
     *
     * @param type extension / file format to write
     * @param srcFileName emf file to read
     * @param destFileName file to create, if null srcFileName
     *        is used with the extension type
     * @throws FileNotFoundException 
     */
    public static void export(String type, String srcFileName, String destFileName) throws FileNotFoundException {
    	export (type, new BufferedInputStream(new FileInputStream(srcFileName)), destFileName);
    }
    
	 protected static SVGGraphics2D buildSVGGraphics2D() {
	        // CSSDocumentHandler.setParserClassName(CSS_PARSER_CLASS_NAME);
	        DOMImplementation impl = GenericDOMImplementation.getDOMImplementation();
	        String namespaceURI = SVGConstants.SVG_NAMESPACE_URI;
	        Document domFactory = impl.createDocument(namespaceURI,"svg", null);
	        SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(domFactory);
	        GraphicContextDefaults defaults
	            = new GraphicContextDefaults();
	        //09.12 ASF defaults.setFont(new Font("Arial", Font.PLAIN, 12));	        
	        ctx.setGraphicContextDefaults(defaults);
	        ctx.setPrecision(12);
	        
	        return new SVGGraphics2D(ctx, false);
	    }
	 
	public static void exportToSVG(EMFRenderer renderer, String destFileName) {
		SVGGraphics2D g = buildSVGGraphics2D();		
		renderer.getMapModeTransform();
		renderer.getViewportOrigin();
		AffineTransform at = new AffineTransform();
		at.translate(-renderer.getHeader().getBounds().getX(), -renderer.getHeader().getBounds().getY());
		g.setTransform(at);
		renderer.setInitialTransform(at);
		
		renderer.paint(g);
		g.setSVGCanvasSize(new Dimension((int) (renderer.getSize().getWidth()), (int) (renderer.getSize().getHeight())));
		
			
		Writer out = null;
		try {
			OutputStream s = 
					new FileOutputStream(destFileName);
			out = new OutputStreamWriter(s, "UTF-8");

		} catch (UnsupportedEncodingException use) {
			use.printStackTrace();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
	
		try {
			boolean useCSS = true;	//FIXME ASF true antes
			g.stream(out,useCSS);		 
		} catch (SVGGraphics2DIOException sioe) {
			sioe.printStackTrace();
		}
	}

    protected static void export(String type, BufferedInputStream in, String destFileName) {
        try {
            // read the EMF file
            EMFRenderer emfRenderer = new EMFRenderer(
                new EMFInputStream(in));

            EMFPanel emfPanel = new EMFPanel();
            emfPanel.setRenderer(emfRenderer);
            exportToSVG(emfRenderer, destFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
