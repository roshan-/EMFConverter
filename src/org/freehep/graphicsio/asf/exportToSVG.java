package org.freehep.graphicsio.asf;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.AffineTransform;
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
import org.freehep.graphicsio.emf.EMFConstants;
import org.freehep.graphicsio.emf.EMFRenderer;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public class exportToSVG {
	
	private EMFRenderer renderer;
	
	
	 protected SVGGraphics2D buildSVGGraphics2D() {
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
	 
	public exportToSVG(EMFRenderer renderer, String destFileName) {
		this.renderer = renderer;

		/*DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
		String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
		SVGDocument doc = (SVGDocument)impl.createDocument(svgNS, "svg", null);		
		
		SVGGraphics2D g = new SVGGraphics2D(doc);*/		
		
		SVGGraphics2D g = buildSVGGraphics2D();
		renderer.getMapModeTransform();
//		renderer.setMapMode(EMFConstants.MM_HIENGLISH);
				
		g.setSVGCanvasSize(new Dimension((int) renderer.getSize().getWidth(), (int) renderer.getSize().getHeight()));
		
		AffineTransform at = renderer.getMapModeTransform();		
		at.setToTranslation(renderer.getHeader().getBounds().getX(), renderer.getHeader().getBounds().getY());
		g.setTransform(at);				
		renderer.paint(g);
		
		
			
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

}
	
