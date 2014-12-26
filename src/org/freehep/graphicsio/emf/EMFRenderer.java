package org.freehep.graphicsio.emf;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.logging.Logger;

import org.freehep.graphicsio.asf.SVGGraphics2D;
import org.freehep.graphicsio.emf.gdi.GDIObject;
import org.freehep.util.io.Tag;

/**
 * Standalone EMF renderer.
 *
 * @author Daniel Noll (daniel@nuix.com)
 * @version $Id: freehep-graphicsio-emf/src/main/java/org/freehep/graphicsio/emf/EMFRenderer.java 10ec7516e3ce 2007/02/06 18:42:34 duns $
 */
public class EMFRenderer {
    private static final Logger logger = Logger.getLogger("org.freehep.graphicsio.emf");

    /**
     * Header read from the EMFInputStream
     */
    private EMFHeader header;

    /**
     * Each logical unit is mapped to one twentieth of a
     * printer's point (1/1440 inch, also called a twip).
     */
    public static double TWIP_SCALE = 1d / 1440 * 254;

    /**
     * affect by all XXXTo methods, e.g. LinTo. ExtMoveTo creates the
     * starting point. CloseFigure closes the figure.
     */
    private GeneralPath figure = null;

    /**
     * AffineTransform which is the base for all rendering
     * operations.
     */
    private AffineTransform initialTransform;

    /**
     * origin of the emf window, set by SetWindowOrgEx
     */
    private Point windowOrigin = null;

    /**
     * origin of the emf viewport, set By SetViewportOrgEx
     */
    private Point viewportOrigin = null;

    public Dimension getViewportSize() {
		return viewportSize;
	}

	/**
     * size of the emf window, set by SetWindowExtEx
     */
    private Dimension windowSize = null;

    /**
     * size of the emf viewport, set by SetViewportExtEx
     */
    private Dimension viewportSize = null;

    /**
     * The MM_ISOTROPIC mode ensures a 1:1 aspect ratio.
     *  The MM_ANISOTROPIC mode allows the x-coordinates
     * and y-coordinates to be adjusted independently.
     */
    //private boolean mapModeIsotropic = false;

    public int getMapMode() {
		return mapMode;
	}

	/**
     * AffineTransform defined by SetMapMode. Used for
     * resizing the emf to propper device bounds.
     */
    private AffineTransform  mapModeTransform =
        //DIA21 AffineTransform.getScaleInstance(TWIP_SCALE, TWIP_SCALE);
    		AffineTransform.getScaleInstance(1.0f, 1.0f);

    private int mapMode = EMFConstants.MM_ISOTROPIC; //DIA21EMFConstants.MM_TEXT; //DIA14 MM_ISOTROPIC;

    /**
     * clipping area which is the base for all rendering
     * operations.
     */
    private Shape initialClip;

    /**
     * current Graphics2D to paint on. It is set during
     * {@link #paint(java.awt.Graphics2D)}
     */
    private Graphics2D g2;

    /**
     * objects used by {@link org.freehep.graphicsio.emf.gdi.SelectObject}.
     * The array is filled by CreateXXX functions, e.g.
     * {@link org.freehep.graphicsio.emf.gdi.CreatePen}
     */
    private GDIObject[] gdiObjects = new GDIObject[256]; // TODO: Make this more flexible.

    // Rendering state.
    private Paint brushPaint = new Color(0, 0, 0, 0);
    private Paint penPaint = Color.BLACK;
    private Stroke penStroke = new BasicStroke();

    private int textAlignMode = 0;

    private double scaleX = 1.0f;
    private double scaleY = 1.0f;
    
    private double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        
    /**
     * color for simple text rendering
     */
    private Color textColor = Color.BLACK;

    /**
     * written by {@link org.freehep.graphicsio.emf.gdi.SetPolyFillMode} used by
     * e.g. {@link org.freehep.graphicsio.emf.gdi.PolyPolygon16}
     */
    private int windingRule = GeneralPath.WIND_EVEN_ODD;

    /**
     * Defined by SetBkModes, either {@link EMFConstants#BKG_OPAQUE} or
     * {@link EMFConstants#BKG_TRANSPARENT}. Used in
     * {@link #fillAndDrawOrAppend(java.awt.Graphics2D, java.awt.Shape)}
     */
    private int bkMode = EMFConstants.BKG_OPAQUE;

    /**
     * The SetBkMode function affects the line styles for lines drawn using a
     * pen created by the CreatePen function. SetBkMode does not affect lines
     * drawn using a pen created by the ExtCreatePen function.
     */
    private boolean useCreatePen = true;

    /**
     * The miter length is defined as the distance from the intersection
     * of the line walls on the inside of the join to the intersection of
     * the line walls on the outside of the join. The miter limit is the
     * maximum allowed ratio of the miter length to the line width.
     */
    private int meterLimit = 10;

    /**
     * The SetROP2 function sets the current foreground mix mode.
     * Default is to use the pen.
     */
    private int rop2 = EMFConstants.R2_COPYPEN;

    /**
     * e.g. {@link Image#SCALE_SMOOTH} for rendering images
     */
    private int scaleMode = Image.SCALE_SMOOTH;

    /**
     * The brush origin is a pair of coordinates specifying the location of one
     * pixel in the bitmap. The default brush origin coordinates are (0,0). For
     * horizontal coordinates, the value 0 corresponds to the leftmost column
     * of pixels; the width corresponds to the rightmost column. For vertical
     * coordinates, the value 0 corresponds to the uppermost row of pixels;
     * the height corresponds to the lowermost row.
     */
    private Point brushOrigin = new Point(0, 0);

    /**
     * stores the parsed tags. Filled by the constructor. Read by
     * {@link #paint(java.awt.Graphics2D)}
     */
    private Vector<Tag> tags = new Vector<Tag>(0);

    /**
     * Created by BeginPath and closed by EndPath.
     */
    private GeneralPath path = null;

    /**
     * The transformations set by ModifyWorldTransform are redirected to
     * that AffineTransform. They do not affect the current paint context,
     * after BeginPath is called. Only the figures appended to path
     * are transformed by this AffineTransform.
     * BeginPath clears the transformation, ModifyWorldTransform changes ist.
     */
    private AffineTransform pathTransform = new AffineTransform();

    /**
     * {@link org.freehep.graphicsio.emf.gdi.SaveDC} stores
     * an Instance of DC if saveDC is read. RestoreDC pops an object.
     */
    private Stack<DC> dcStack = new Stack<DC>();

    /**
     * default direction is counterclockwise
     */
    private int arcDirection = EMFConstants.AD_COUNTERCLOCKWISE;

    /**
     * Class the encapsulate the state of a Graphics2D object.
     * Instances are store in dcStack by
     * {@link org.freehep.graphicsio.emf.EMFRenderer#paint(java.awt.Graphics2D)}
     */
    private class DC {
        private Paint paint;
        private Stroke stroke;
        private AffineTransform transform;
        private Shape clip;
        public GeneralPath path;
        public int bkMode;
        public int windingRule;
        public int meterLimit;
        public boolean useCreatePen;
        public int scaleMode;
        public AffineTransform pathTransform;
    }

    /**
     * Constructs the renderer.
     *
     * @param is the input stream to read the EMF records from.
     * @throws IOException if an error occurs reading the header.
     */
    public EMFRenderer(EMFInputStream is) throws IOException {
        this.header = is.readHeader();
        int i = 0;

        // read all tags
        Tag tag;
        
        while ((tag = is.readTag()) != null) {
        	System.out.println("***--***" + i++);
            tags.add(tag);
        }
        is.close();
    }

    /**
     * Gets the size of a canvas which would be required to render the EMF.
     *
     * @return the size.
     */
    public Dimension getSize() {
        //DIA10
    	return header.getBounds().getSize();
    	//return header.getFrame().getSize();
    	//return new Dimension((int)(bounds.width), (int)(bounds.height));
        // TODO see the mapModePart of resetTransformation()
        // if uncommented size is too small
        /* Dimension bounds = header.getBounds().getSize();
            return new Dimension(
            (int)Math.ceil(bounds.width * TWIP_SCALE),
            (int)Math.ceil(bounds.height * TWIP_SCALE));*/
    }

    public void setInitialTransform(AffineTransform at) {
    	initialTransform = at;
    }
    /**
     * Paints the EMF onto the provided graphics context.
     *
     * @param g2 the graphics context to paint onto.
     */
    public void paint(Graphics2D g2) {
        this.g2 = g2;

        // store at leat clip and transformation
        Shape clip = g2.getClip();
        AffineTransform at = g2.getTransform();
        Map<?, ?> hints = g2.getRenderingHints();

        // some quality settings
        g2.setRenderingHint(
            RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(
            RenderingHints.KEY_FRACTIONALMETRICS,
            RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // used by SetWorldTransform to reset transformation
        if (initialTransform == null)
        initialTransform = g2.getTransform();

        // set the initial value, defaults for EMF
        path = null;
        figure = null;
        meterLimit = 10;
        windingRule = GeneralPath.WIND_EVEN_ODD;
        bkMode = EMFConstants.BKG_OPAQUE;
        useCreatePen = true;
        scaleMode = Image.SCALE_SMOOTH;

        windowOrigin = null;
        viewportOrigin = null;
        windowSize = null;
        viewportSize = null;

        /*mapModeIsotropic = true;
        mapModeTransform = AffineTransform.getScaleInstance(
            TWIP_SCALE, TWIP_SCALE);*/

        setMapMode(mapMode);
        // apply all default settings
        resetTransformation(g2);

        // determin initial clip after all basic transformations
        initialClip = g2.getClip();

        //EMFASFViewer view = new EMFASFViewer();
        
        // iterate and render all tags
        Tag tag;
        for (int i = 0; i < tags.size(); i++) {
            tag = tags.get(i);     
            if (tag instanceof EMFTag) {
            	System.out.println(i + "-" + tags.get(i).getName() + "!!!!!!!!!!!!");
                ((EMFTag) tags.get(i)).render(this);
            } else {
                logger.warning("unknown tag: " + tag);
            }    
        }
        
        g2.getClipBounds();
        // reset Transform and clip
        g2.setRenderingHints(hints);
        g2.setTransform(at);
        g2.setClip(clip);
    }

    // ---------------------------------------------------------------------
    //            complex drawing methods for EMFTags
    // ---------------------------------------------------------------------

    public Point getViewportOrigin() {
		return viewportOrigin;
	}

	/**
     * set the initial transform, the windowOrigin and viewportOrigin,
     * scales by viewportSize and windowSize
     * @param g2 Context to apply transformations
     */
    private void resetTransformation(Graphics2D g2) {
        // rest to device configuration
        if (initialTransform != null) {
            g2.setTransform(initialTransform);
        } else {
            g2.setTransform(new AffineTransform());
        }

        /* TODO mapModeTransform dows not work correctly
        if (mapModeTransform != null) {
            g2.transform(mapModeTransform);
        }*/
        if (mapModeTransform != null) {
            g2.setTransform(mapModeTransform);
        }

        // move to window origin
        if (windowOrigin != null) {
            g2.translate(
                - windowOrigin.getX(),
                - windowOrigin.getY());
        }
        // move to window origin
        if (viewportOrigin != null) {
            g2.translate(
                - viewportOrigin.getX(),
                - viewportOrigin.getY());
        }

        // TWIP_SCALE by window and viewport size
        if (viewportSize != null && windowSize != null) {
            scaleX =
                viewportSize.getWidth() /
                windowSize.getWidth();
            scaleY =
                viewportSize.getHeight() /
                windowSize.getHeight();
       //     initialTransform = new AffineTransform ();
            
            //initialTransform.scale(scaleX, scaleY);
       /*     double tx = 0.0;
            double ty = -this.getSize().getHeight();
            initialTransform.translate(tx, ty);*/
          //  g2.setTransform(initialTransform);
           
          //  g2.translate(this.getSize().getWidth() / scaleX, this.getSize().getHeight() / scaleY);
          //g2.translate(this.getSize().getWidth(), -this.getSize().getHeight());
          //g2.translate(1.0, -this.getSize().getHeight());

            g2.scale(scaleX,  scaleY);
          
          /*  ((SVGGraphics2D) g2).setSVGCanvasSize(new Dimension((int)(this.getSize().getWidth() / scaleX), 
            									  (int)(this.getSize().getHeight() / scaleY)));
            									  */            
            //((SVGGraphics2D) g2).setScaleX(scaleX);
            //((SVGGraphics2D) g2).setScaleY(scaleY);
        }
    }

  
    /**
     * Stores the current state. Used by
     * {@link org.freehep.graphicsio.emf.gdi.SaveDC}
     */
    public void saveDC() {
        // create a DC instance with current settings
        DC dc = new DC();
        dc.paint = g2.getPaint();
        dc.stroke = g2.getStroke();
        dc.transform = g2.getTransform();
        dc.pathTransform = pathTransform;
        dc.clip = g2.getClip();
        dc.path = path;
        dc.meterLimit = meterLimit;
        dc.windingRule = windingRule;
        dc.bkMode = bkMode;
        dc.useCreatePen = useCreatePen;
        dc.scaleMode = scaleMode;
        // push it on top of the stack
        dcStack.push(dc);
    }

    /**
     * Retores a saved state. Used by
     * {@link org.freehep.graphicsio.emf.gdi.RestoreDC}
     */
    public void retoreDC() {
        // is somethoing stored?
        if (!dcStack.empty()) {
            // read it
            DC dc = dcStack.pop();

            // use it
            meterLimit = dc.meterLimit;
            windingRule = dc.windingRule;
            path = dc.path;
            bkMode = dc.bkMode;
            useCreatePen = dc.useCreatePen;
            scaleMode = dc.scaleMode;
            pathTransform = dc.pathTransform;
            g2.setPaint(dc.paint);
            g2.setStroke(dc.stroke);
            g2.setTransform(dc.transform);
            g2.setClip(dc.clip);
        } else {
            // set the default values
        }
    }

    /**
     * closes and appends the current open figure to the
     * path
     */
    public void closeFigure() {
        if (figure == null) {
            return;
        }

            figure.closePath();
            appendToPath(figure);
            figure = null;        
    }

    
    /**
     * Logical units are mapped to arbitrary units with equally scaled axes;
     * that is, one unit along the x-axis is equal to one unit along the y-axis.
     * Use the SetWindowExtEx and SetViewportExtEx functions to specify the
     * units and the orientation of the axes. Graphics device interface (GDI)
     * makes adjustments as necessary to ensure the x and y units remain the
     * same size (When the window extent is set, the viewport will be adjusted
     * to keep the units isotropic).
     */
    public void fixViewportSize() {
        if ( mapMode == EMFConstants.MM_ISOTROPIC && (windowSize != null && viewportSize != null)) {
            viewportSize.setSize(
                viewportSize.getWidth(),
                viewportSize.getWidth() *
                    (windowSize.getHeight() / windowSize.getWidth())
            );
        }
        ((SVGGraphics2D) g2).setScaleX(scaleX);
        ((SVGGraphics2D) g2).setScaleY(scaleY);
    }

    /**
     * fills a shape using the brushPaint,  penPaint and penStroke
     * @param g2 Painting context
     * @param s Shape to fill with current brush
     */
    private void fillAndDrawOrAppend(Graphics2D g2, Shape s) {
        // don't draw, just append the shape if BeginPath
        // has opened the path
        if (!appendToPath(s)) {
            // The SetBkMode function affects the line styles for lines drawn using a
            // pen created by the CreatePen function. SetBkMode does not affect lines
            // drawn using a pen created by the ExtCreatePen function.
            if (useCreatePen) {
                // OPAQUE 	Background is filled with the current background
                // color before the text, hatched brush, or pen is drawn.
                if (bkMode == EMFConstants.BKG_OPAQUE) {
                    fillShape(g2, s);
                } else {
                    // TRANSPARENT 	Background remains untouched.
                    // TODO: if we really do nothing some drawings are incomplete
                    // this needs definitly a fix
                    fillShape(g2, s);
                }
            } else {
                // always fill the background if ExtCreatePen is set
                fillShape(g2, s);
            }
            drawShape(g2, s);
            if (s.getBounds().getX() < minX)
            	minX =s.getBounds().getX();            
            if (s.getBounds().getY() < minY)
               	minY =s.getBounds().getY();
         System.out.println("LL" + minX + "." + minY);
        }
    }

    /**
     * draws a shape using the penPaint and penStroke
     * @param g2 Painting context
     * @param s Shape to draw with current paen
     */
    private void drawOrAppend(Graphics2D g2, Shape s) {
        // don't draw, just append the shape if BeginPath
        // opens a GeneralPath
        if (!appendToPath(s)) {
            drawShape(g2, s);
        }
    }

    /**
     * draws the text
     *
     * @param text Text
     * @param x x-Position
     * @param y y-Position
     */
   /* public void drawOrAppendText(String text, double x, double y) {
        // TODO: Use explicit widths to pixel-position each character, if present.
        // TODO: Implement alignment properly.  What we have already seems to work well enough.
        //            FontRenderContext frc = g2.getFontRenderContext();
        //            TextLayout layout = new TextLayout(str, g2.getFont(), frc);
        //            if ((textAlignMode & EMFConstants.TA_CENTER) != 0) {
        //                layout.draw(g2, x + (width - textWidth) / 2, y);
        //            } else if ((textAlignMode & EMFConstants.TA_RIGHT) != 0) {
        //                layout.draw(g2, x + width - textWidth, y);
        //            } else {
        //                layout.draw(g2, x, y);
        //            }

        if (path != null) {
            // do not use g2.drawString(str, x, y) to be aware of path
            TextLayout tl = new TextLayout(
                text,
                g2.getFont(),
                g2.getFontRenderContext());
            path.append(tl.getOutline(null), false);
        } else {
            g2.setPaint(textColor);
            g2.setTransform(g2.getTransform());
            g2.drawString(text, (int)x, (int)y);
        }
    }*/
    
  /*  public void drawOrAppendText(String text, double x, double y,   int mode,
            float xScale,
            float yScale, Rectangle bounds) {
        // TODO: Use explicit widths to pixel-position each character, if present.
        // TODO: Implement alignment properly.  What we have already seems to work well enough.
   /*                 FontRenderContext frc = g2.getFontRenderContext();
                    TextLayout layout = new TextLayout(text, g2.getFont(), frc);
                    double textWidth = layout.getBounds().getWidth();
                    if ((textAlignMode & EMFConstants.TA_CENTER) != 0) {
                        layout.draw(g2, (float) ((float) x + (bounds.getWidth() - textWidth) / 2), (float) y);
                    } else if ((textAlignMode & EMFConstants.TA_RIGHT) != 0) {
                        layout.draw(g2, (float) ((float)  x + bounds.getWidth() - textWidth), (float) y);
                    } else {
                        layout.draw(g2, (float) x, (float) y);
                    }
*/
  /*      if (path != null) {
            // do not use g2.drawString(str, x, y) to be aware of path
            TextLayout tl = new TextLayout(
                text,
                g2.getFont(),
                g2.getFontRenderContext());
            path.append(tl.getOutline(null), false);
        } else {
        	
        	Color oldcol = g2.getColor();
    		g2.setColor(textColor);
    		
        	if (mode == EMFConstants.GM_COMPATIBLE)
        	{
        		g2.setPaint(textColor);
        		
        		
                /*AffineTransform oldat = g2.getTransform();
                AffineTransform at = new AffineTransform();
                at.setToTranslation(bounds.getX(), bounds.getY());
                at.setToScale(1/xScale, 1/yScale);
                int xinit = (int)((bounds.getX()+x));
                int yinit = (int)((bounds.getY()+y));
                g2.setTransform(at);*/                     
     //           g2.drawString(text, (int)x,(int) y);
                //g2.setTransform(oldat);            
                
       /* 	}
        	else if (mode == EMFConstants.GM_ADVANCED)
        	{
        	/*DIA13	AffineTransform at =g2.getTransform();
        		float p = (float) (Math.abs(at.getTranslateX() - bounds.getX())/bounds.getX());
        		float q = (float) (Math.abs(at.getTranslateY() - bounds.getY())/bounds.getY());

      		  	g2.setPaint(textColor);
        		if ((p > 0.20) || (q > 0.20))
          		  g2.drawString(text, (int)bounds.getX()+(int)x, (int)bounds.getY()+(int)y);
        		else*/
        		// DIA18
        /*		if (x < bounds.getX() && y < bounds.getY())
        			//g2.drawString(text, (int)bounds.getX()+(int)x, (int)bounds.getY()+(int)y);
        			g2.drawString(text, (int)bounds.getX(), (int)bounds.getY());
        		else
        			g2.drawString(text, (int)x, (int)y);
        		
        		

        		  //g2.drawString(text, (int)bounds.getX()+(int)x, (int)bounds.getY()+(int)y);
        	}         
            //g2.setColor(oldcol);
        }        
    }*/


    public void drawOrAppendText(String text, double x, double y,   int mode,
    		float xScale,
    		float yScale, Rectangle bounds) {

    	/*	FontRenderContext frc = g2.getFontRenderContext();
        TextLayout layout = new TextLayout(text, g2.getFont(), frc);
        double textWidth = layout.getBounds().getWidth();
        if ((textAlignMode & EMFConstants.TA_CENTER) != 0) {
            layout.draw(g2, (float) ((float) x + (bounds.getWidth() - textWidth) / 2), (float) y);
        } else if ((textAlignMode & EMFConstants.TA_RIGHT) != 0) {
            layout.draw(g2, (float) ((float)  x + bounds.getWidth() - textWidth), (float) y);
        } else {
            layout.draw(g2, (float) x, (float) y);
        }*/

    	y -= 0.125 * g2.getFont().getSize();
    	if (path != null) {
    		// do not use g2.drawString(str, x, y) to be aware of path
    		TextLayout tl = new TextLayout(
    				text,
    				g2.getFont(),
    				g2.getFontRenderContext());
    		path.append(tl.getOutline(null), false);
    	}
    	else {

    		Color oldcol = g2.getColor();
    		g2.setColor(textColor);
    		if (figure == null)
    		{
    			FontRenderContext frc = g2.getFontRenderContext();
				TextLayout layout = new TextLayout(text, g2.getFont(), frc);
				double textWidth = layout.getBounds().getWidth();
    			if ((textAlignMode & EMFConstants.TA_CENTER) != 0) {
					g2.drawString(text, (float) ((float) x + (bounds.getWidth() - textWidth) / 2), (float) y);
					//layout.draw(g2, (float) ((float) x + (bounds.getWidth() - textWidth) / 2), (float) y);
				} else if ((textAlignMode & EMFConstants.TA_RIGHT) != 0) {
					g2.drawString(text, (float) ((float)  x + bounds.getWidth() - textWidth), (float) y);
					//layout.draw(g2, (float) ((float)  x + bounds.getWidth() - textWidth), (float) y);
				} else if ((textAlignMode & EMFConstants.TA_BASELINE) != 0) {                        
					g2.drawString(text, (int) x, (int) y);
				} else
					//g2.drawString(text, (int)x, (int) y);
					g2.drawString(text, (int) x, (int) y+(int)g2.getFont().getSize());
    		}
    		else
    		{
    	
    			if (mode == EMFConstants.GM_COMPATIBLE)
    			{
    				g2.setPaint(textColor);

    				//g2.drawString(text, (int)x,(int) y);
    				FontRenderContext frc = g2.getFontRenderContext();
    				TextLayout layout = new TextLayout(text, g2.getFont(), frc);
    				double textWidth = layout.getBounds().getWidth();
    				if ((textAlignMode & EMFConstants.TA_CENTER) != 0) {
    					g2.drawString(text, (float) ((float) x + (bounds.getWidth() - textWidth) / 2), (float) y);
    					//layout.draw(g2, (float) ((float) x + (bounds.getWidth() - textWidth) / 2), (float) y);
    				} else if ((textAlignMode & EMFConstants.TA_RIGHT) != 0) {
    					g2.drawString(text, (float) ((float)  x + bounds.getWidth() - textWidth), (float) y);
    					//layout.draw(g2, (float) ((float)  x + bounds.getWidth() - textWidth), (float) y);
    				} else if ((textAlignMode & EMFConstants.TA_BASELINE) != 0) {                        
    					g2.drawString(text, (int) x, (int) y);
    				} else if (textAlignMode == 0) {
    					g2.drawString(text, (int) x, (int) y+(int)g2.getFont().getSize());
    				}    					
    				else
    					//g2.drawString(text, (int) x-(int)bounds.getWidth()/2, (int) y+(int)bounds.getHeight());
    					g2.drawString(text, (int) x-(int)bounds.getWidth()/2, (int) y+(int)g2.getFont().getSize());



    			}
    			else if (mode == EMFConstants.GM_ADVANCED)
    			{
    				g2.setTransform(g2.getTransform());
    				FontRenderContext frc = g2.getFontRenderContext();

    				TextLayout layout = new TextLayout(text, g2.getFont(), frc);
    				double textWidth = layout.getBounds().getWidth();
    				if ((textAlignMode & EMFConstants.TA_CENTER) != 0) {
    					g2.drawString(text, (float) ((float) x + (bounds.getWidth() - textWidth) / 2), (float) y);
    					//layout.draw(g2, (float) ((float) x + (bounds.getWidth() - textWidth) / 2), (float) y);
    				} else if ((textAlignMode & EMFConstants.TA_RIGHT) != 0) {
    					g2.drawString(text, (float) ((float)  x + bounds.getWidth() - textWidth), (float) y);
    					//layout.draw(g2, (float) ((float)  x + bounds.getWidth() - textWidth), (float) y);
    				} else if ((textAlignMode & EMFConstants.TA_BASELINE) != 0) {                        
    					g2.drawString(text, (int) x, (int) y);
    				} else
    					g2.drawString(text, (int) x, (int) y);
    				//layout.draw(g2,  (int) x,  (int)y);



    			}         
    		}
    	}        
    }
    
     public void drawOrAppendText(String text, double x, double y, Rectangle bounds)  {
        // TODO: Use explicit widths to pixel-position each character, if present.
        // TODO: Implement alignment properly.  What we have already seems to work well enough.
   /*                 FontRenderContext frc = g2.getFontRenderContext();
                    TextLayout layout = new TextLayout(text, g2.getFont(), frc);
                    double textWidth = layout.getBounds().getWidth();
                    if ((textAlignMode & EMFConstants.TA_CENTER) != 0) {
                        layout.draw(g2, (float) ((float) x + (bounds.getWidth() - textWidth) / 2), (float) y);
                    } else if ((textAlignMode & EMFConstants.TA_RIGHT) != 0) {
                        layout.draw(g2, (float) ((float)  x + bounds.getWidth() - textWidth), (float) y);
                    } else {
                        layout.draw(g2, (float) x, (float) y);
                    }
*/
        if (path != null) {
            // do not use g2.drawString(str, x, y) to be aware of path
            TextLayout tl = new TextLayout(
                text,
                g2.getFont(),
                g2.getFontRenderContext());
            path.append(tl.getOutline(null), false);
        } else {
            g2.setPaint(textColor);            
            /*AffineTransform at = new AffineTransform();
            at.setToTranslation(bounds.getX(), bounds.getY());
            at.setToScale(1.0, 1.0);
            g2.setTransform(at);*/
            //g2.drawString(text, (int)x, (int)y);            
            //DIA9 Esto me lo invente. Parece que no va g2.drawString(text, (int)bounds.getX()+(int)x, (int)bounds.getY()+(int)y);
            g2.drawString(text, (int)bounds.getX()+(int)x, (int)bounds.getY()+(int)y);
        }
    }

    /**
     * Append the shape to the current path
     *
     * @param s Shape to fill with current brush
     * @return true, if path was changed
     */
    private boolean appendToPath(Shape s) {
        // don't draw, just append the shape if BeginPath
        // opens a GeneralPath
        if (path != null) {
            // aplly transformation if set
            if (pathTransform != null) {
                s = pathTransform.createTransformedShape(s);
            }
            // append the shape
            path.append(s, false);
            // current path set
            return true;
        }
        // current path not set
        return false;
    }

    /**
     * closes the path opened by {@link org.freehep.graphicsio.emf.gdi.BeginPath}
     */
    public void closePath() {
        if (path != null) {
            try {
                path.closePath();
            } catch (java.awt.geom.IllegalPathStateException e) {
                logger.warning("no figure to close");
            }
        }
    }

    /**
     * fills a shape using the brushPaint,  penPaint and penStroke.
     * This method should only be called for path painting. It doesn't check for a
     * current path.
     *
     * @param g2 Painting context
     * @param s Shape to fill with current brush
     */
    private void fillShape(Graphics2D g2, Shape s) {
        g2.setPaint(brushPaint);
        g2.fill(s);
    }

    /**
     * draws a shape using the penPaint and penStroke
     * This method should only be called for path drawing. It doesn't check for a
     * current path.
     *
     * @param g2 Painting context
     * @param s Shape to draw with current pen
     */
    private void drawShape(Graphics2D g2, Shape s) {
        g2.setStroke(penStroke);

        // R2_BLACK 	Pixel is always 0.
        if (rop2 == EMFConstants.R2_BLACK) {
            g2.setComposite(AlphaComposite.SrcOver);
            g2.setPaint(Color.black);
        }
        // R2_COPYPEN 	Pixel is the pen color.
        else if (rop2 == EMFConstants.R2_COPYPEN) {
            g2.setComposite(AlphaComposite.SrcOver);
            g2.setPaint(penPaint);
        }
        // R2_NOP 	Pixel remains unchanged.
        else if (rop2 == EMFConstants.R2_NOP) {
            g2.setComposite(AlphaComposite.SrcOver);
            g2.setPaint(penPaint);
        }
        // R2_WHITE 	Pixel is always 1.
        else if (rop2 == EMFConstants.R2_WHITE) {
            g2.setComposite(AlphaComposite.SrcOver);
            g2.setPaint(Color.white);
        }
        // R2_NOTCOPYPEN 	Pixel is the inverse of the pen color.
        else if (rop2 == EMFConstants.R2_NOTCOPYPEN) {
            g2.setComposite(AlphaComposite.SrcOver);
            // TODO: set at least inverted color if paint is a color
        }
        // R2_XORPEN 	Pixel is a combination of the colors
        // in the pen and in the screen, but not in both.
        else if (rop2 == EMFConstants.R2_XORPEN) {
            g2.setComposite(AlphaComposite.Xor);
        } else {
            logger.warning("got unsupported ROP" + rop2);
            // TODO:
            //R2_MASKNOTPEN 	Pixel is a combination of the colors common to both the screen and the inverse of the pen.
            //R2_MASKPEN 	Pixel is a combination of the colors common to both the pen and the screen.
            //R2_MASKPENNOT 	Pixel is a combination of the colors common to both the pen and the inverse of the screen.
            //R2_MERGENOTPEN 	Pixel is a combination of the screen color and the inverse of the pen color.
            //R2_MERGEPEN 	Pixel is a combination of the pen color and the screen color.
            //R2_MERGEPENNOT 	Pixel is a combination of the pen color and the inverse of the screen color.
            //R2_NOT 	Pixel is the inverse of the screen color.
            //R2_NOTCOPYPEN 	Pixel is the inverse of the pen color.
            //R2_NOTMASKPEN 	Pixel is the inverse of the R2_MASKPEN color.
            //R2_NOTMERGEPEN 	Pixel is the inverse of the R2_MERGEPEN color.
            //R2_NOTXORPEN 	Pixel is the inverse of the R2_XORPEN color.
        }

        g2.draw(s);
    }

    // ---------------------------------------------------------------------
    //            simple wrapping methods to the painting context
    // ---------------------------------------------------------------------

    public void setFont(Font font) {
        g2.setFont(font);
        
    }

    public AffineTransform getTransform() {
        return g2.getTransform();
    }

    public void transform(AffineTransform transform) {
        g2.transform(transform);
        //DIA25 g2.setTransform(transform);
    }

    public void resetTransformation() {
        resetTransformation(g2);
    }

    public void setTransform(AffineTransform at) {
        g2.setTransform(at);
    }
    
    public void setTranslate(AffineTransform at) {
        AffineTransform old = g2.getTransform();
        System.out.println("# " +old.getScaleX() +" "+old.getScaleY());
        at.scale(old.getScaleX(), old.getScaleY());
    	g2.setTransform(at);
    }


    public void setClip(Shape shape) {
        g2.setClip(shape);
    }

    public void clip(Shape shape) {
        g2.clip(shape);
    }

    public Shape getClip() {
        return g2.getClip();
    }

    public void drawImage(BufferedImage image, AffineTransform transform) {
        g2.drawImage(image, transform, null);
    }

    public void drawImage(BufferedImage image, int x, int y, int width, int height) {
        g2.drawImage(image, x, y, width,  height,  null);
    }

    public void drawShape(Shape shape) {
        drawShape(g2, shape);
    }

    public void fillShape(Shape shape) {
        fillShape(g2, shape);
    }

    public void fillAndDrawOrAppend(Shape s) {
        fillAndDrawOrAppend(g2, s);
    }

    public void drawOrAppend(Shape s) {
        drawOrAppend(g2, s);
    }

    // ---------------------------------------------------------------------
    //            simple getter / setter methods
    // ---------------------------------------------------------------------

    public int getWindingRule() {
        return windingRule;
    }

    public GeneralPath getFigure() {
        return figure;
    }

    public void setFigure(GeneralPath figure) {
        this.figure = figure;
    }

    public GeneralPath getPath() {
        return path;
    }

    public void setPath(GeneralPath path) {
        this.path = path;
    }

    public Shape getInitialClip() {
        return initialClip;
    }

    public AffineTransform getPathTransform() {
        return pathTransform;
    }

    public void setPathTransform(AffineTransform pathTransform) {
        this.pathTransform = pathTransform;
    }

    public void setWindingRule(int windingRule) {
        this.windingRule = windingRule;
    }



    public AffineTransform getMapModeTransform() {
        return mapModeTransform;
    }

    public void setMapModeTransform(AffineTransform mapModeTransform) {
        this.mapModeTransform = mapModeTransform;
    }
    
    public void setMapMode (int mode) {
    	switch(mode) 
    	{
    	case EMFConstants.MM_TEXT:
    		 mapModeTransform = AffineTransform.getScaleInstance(1.0d, 1.0d);
    		break;
    	case EMFConstants.MM_LOMETRIC:
    		mapModeTransform = AffineTransform.getScaleInstance(0.1, 0.1);
    		break;
    	case EMFConstants.MM_HIMETRIC:
    		mapModeTransform = AffineTransform.getScaleInstance(0.01, 0.01);
    		break;
    	case EMFConstants.MM_LOENGLISH:
    		mapModeTransform = AffineTransform.getScaleInstance(2.54, 2.54);
    		break;
    	case EMFConstants.MM_HIENGLISH:
    		mapModeTransform = AffineTransform.getScaleInstance(0.254, 0.254);
    		break;
    	case EMFConstants.MM_TWIPS:
    		mapModeTransform = AffineTransform.getScaleInstance(TWIP_SCALE, TWIP_SCALE);
    		break;
    	case EMFConstants.MM_ISOTROPIC:    		
    		mapModeTransform = AffineTransform.getScaleInstance(1.0d, 1.0d);;
    		break;
    	case EMFConstants.MM_ANISOTROPIC:    		
    		mapModeTransform = null;
    		break;    		    	
    	}
    	mapMode = mode;
    }

    public void setWindowOrigin(Point windowOrigin) {
        this.windowOrigin = windowOrigin;
    }

    public void setViewportOrigin(Point viewportOrigin) {
        this.viewportOrigin = viewportOrigin;
    }

    public void setViewportSize(Dimension viewportSize) {
        this.viewportSize = viewportSize;
        fixViewportSize();        
    	/*((SVGGraphics2D) g2).setSVGCanvasSize(new Dimension((int) (viewportSize.getWidth()),
    				 (int) (viewportSize.getHeight())));
        */
        
        //DIA21 comm resetTransformation();
        //resetTransformation();
    }

    public void setWindowSize(Dimension windowSize) {
        this.windowSize = windowSize;
        fixViewportSize();
     	/*((SVGGraphics2D) g2).setSVGCanvasSize(new Dimension((int) (windowSize.getWidth()),
				 (int) (windowSize.getHeight())));*/
        //DIA21 comm resetTransformation();
        //resetTransformation();

    }

    public GDIObject getGDIObject(int index) {
        return gdiObjects[index];
    }

    public void storeGDIObject(int index, GDIObject tag) {
        gdiObjects[index] = tag;
    }

    public void setUseCreatePen(boolean useCreatePen) {
        this.useCreatePen = useCreatePen;
    }

    public void setPenPaint(Paint penPaint) {
        this.penPaint = penPaint;
    }

    public Stroke getPenStroke() {
        return penStroke;
    }

    public void setPenStroke(Stroke penStroke) {
        this.penStroke = penStroke;
    }

    public void setBrushPaint(Paint brushPaint) {
        this.brushPaint = brushPaint;
    }

    public float getMeterLimit() {
        return meterLimit;
    }

    public void setMeterLimit(int meterLimit) {
        this.meterLimit = meterLimit;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public void setRop2(int rop2) {
        this.rop2 = rop2;
    }

    public void setBkMode(int bkMode) {
        this.bkMode = bkMode;
    }

    public int getTextAlignMode() {
        return textAlignMode;
    }

    public void setTextAlignMode(int textAlignMode) {
        this.textAlignMode = textAlignMode;
    }

    public void setScaleMode(int scaleMode) {
        this.scaleMode = scaleMode;
    }

    public Point getBrushOrigin() {
        return brushOrigin;
    }

    public void setBrushOrigin(Point brushOrigin) {
        this.brushOrigin = brushOrigin;
    }

    public void setArcDirection(int arcDirection) {
        this.arcDirection = arcDirection;
}

    public int getArcDirection() {
        return arcDirection;
    }
    
    public EMFHeader getHeader() {
    	return header;
    }

	public double getScaleX() {
		return scaleX;
	}

	public void setScaleX(double scaleX) {
		this.scaleX = scaleX;
	}

	public double getScaleY() {
		return scaleY;
	}

	public void setScaleY(double scaleY) {
		this.scaleY = scaleY;
	}
    
    
}
