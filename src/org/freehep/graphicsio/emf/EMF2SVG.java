// Copyright FreeHEP 2006-2007.
package org.freehep.graphicsio.emf;

import java.io.FileInputStream;
import java.io.IOException;

import org.freehep.graphicsio.ImageConstants;

/**
 * simple class that uses EMFConverter to convert emf to svg.
 * Uses {@link org.freehep.graphicsio.emf.EMFConverter#export(String, String, String)}
 * with type = SVG.
 *
 * @author Steffen Greiffenberg
 * @version $Id: freehep-graphicsio-emf/src/main/java/org/freehep/graphicsio/emf/EMF2SVG.java 10ec7516e3ce 2007/02/06 18:42:34 duns $
 */
public class EMF2SVG extends EMFConverter {

    /**
     * starts the export
     *
     * @param args args[0] source file name, args[1] target file (can be empty)
     */
    public static void main(String[] args) {
     /*   if (args == null || args.length == 0 || args[0] == null || args[0].length() == 0) {
            System.out.println("usage: EMF2SVG imput.emf [output.svg]");
            return;
        }*/
    	
    	
/*    	try {
    		//84203
    		int nTags = 297;
			EMFReader reader = new EMFReader();
			reader.read("Infographics.emf", "out.emf", nTags);
			//reader.read("out.emf", "out2.emf", nTags);
			
			export(ImageConstants.SVG, "out.emf", "out.svg");    		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("FILE SAVED");
  */	

        //export(ImageConstants.SVG, args[0], args.length > 1 ? args[1] : null);

        /*if (args.length > 1)
        export(ImageConstants.SVG, args[0], args[1]);
    	else
    		export(ImageConstants.SVG, args[0], null);   
        */
        
    	//export(ImageConstants.SVG, "a.emf", "a_001.svg");
    	//export(ImageConstants.SVG, "landscape.emf", "landscape.svg");
    //	export(ImageConstants.SVG, "image2.emf", "image2.svg");
    	//export(ImageConstants.SVG, "image5.emf", "image5.svg");
    	//export(ImageConstants.SVG, "excel-ppt-2.emf", "excel-ppt-2.svg");
    	
    	//export(ImageConstants.SVG, "out.emf", "aaaa.svg");
    	            	
    	//export(ImageConstants.SVG, "Infographics.emf", "Infographics.svg");
        
        boolean all = true;
    	if (all == true)
    	{
    	export(ImageConstants.SVG, "images/13.emf", "images/13.emf.svg");
    	export(ImageConstants.SVG, "images/14.emf", "images/14.emf.svg");
    	export(ImageConstants.SVG, "images/15.emf", "images/15.emf.svg");
    	export(ImageConstants.SVG, "images/16.emf", "images/16.emf.svg");
    	export(ImageConstants.SVG, "images/17.emf", "images/17.emf.svg");
    	export(ImageConstants.SVG, "images/18.emf", "images/18.emf.svg");
    	export(ImageConstants.SVG, "images/19.emf", "images/19.emf.svg");
    	export(ImageConstants.SVG, "images/20.emf", "images/20.emf.svg");
    	export(ImageConstants.SVG, "images/21.emf", "images/21.emf.svg");
    	export(ImageConstants.SVG, "images/22.emf", "images/22.emf.svg");
    	export(ImageConstants.SVG, "images/23.emf", "images/23.emf.svg");
    	export(ImageConstants.SVG, "images/24.emf", "images/24.emf.svg");
    	export(ImageConstants.SVG, "images/25.emf", "images/25.emf.svg");
    	export(ImageConstants.SVG, "images/26.emf", "images/26.emf.svg");
    	export(ImageConstants.SVG, "images/27.emf", "images/27.emf.svg");
    	export(ImageConstants.SVG, "images/28.emf", "images/28.emf.svg");
    	export(ImageConstants.SVG, "images/29.emf", "images/29.emf.svg");
    	export(ImageConstants.SVG, "images/30.emf", "images/30.emf.svg");
    	export(ImageConstants.SVG, "images/31.emf", "images/31.emf.svg");
    	export(ImageConstants.SVG, "images/32.emf", "images/32.emf.svg");    	
    	export(ImageConstants.SVG, "images/33.emf", "images/33.emf.svg");
    	export(ImageConstants.SVG, "images/34.emf", "images/34.emf.svg");
    	export(ImageConstants.SVG, "images/35.emf", "images/35.emf.svg");
    	export(ImageConstants.SVG, "images/36.emf", "images/36.emf.svg");
    	export(ImageConstants.SVG, "images/37.emf", "images/37.emf.svg");
    	export(ImageConstants.SVG, "images/38.emf", "images/38.emf.svg");
    	export(ImageConstants.SVG, "images/39.emf", "images/39.emf.svg");
    	export(ImageConstants.SVG, "images/40.emf", "images/40.emf.svg");
    	export(ImageConstants.SVG, "images/41.emf", "images/41.emf.svg");
    	export(ImageConstants.SVG, "images/42.emf", "images/42.emf.svg");
    	export(ImageConstants.SVG, "images/43.emf", "images/43.emf.svg");
    	export(ImageConstants.SVG, "images/44.emf", "images/44.emf.svg");
    	export(ImageConstants.SVG, "images/45.emf", "images/45.emf.svg");
    	export(ImageConstants.SVG, "images/46.emf", "images/46.emf.svg");
    	export(ImageConstants.SVG, "images/48.emf", "images/48.emf.svg");
      	
    	export(ImageConstants.SVG, "images/a.emf", "images/a.emf.svg");
    	export(ImageConstants.SVG, "images/landscape.emf", "images/landscape.emf.svg");
    	export(ImageConstants.SVG, "images/picture1313.emf", "images/picture1313.emf.svg");
    	export(ImageConstants.SVG, "images/picture14.emf", "images/picture14.emf.svg");
    	export(ImageConstants.SVG, "images/picture1414.emf", "images/picture1414.emf.svg");
    	export(ImageConstants.SVG, "images/picture1515.emf", "images/picture1515.emf.svg");
    	export(ImageConstants.SVG, "images/picture16.emf", "images/picture16.emf.svg");
    	export(ImageConstants.SVG, "images/picture17.emf", "images/picture17.emf.svg");
    	export(ImageConstants.SVG, "images/picture18.emf", "images/picture18.emf.svg");
    	export(ImageConstants.SVG, "images/picture19.emf", "images/picture19.emf.svg");
    	export(ImageConstants.SVG, "images/picture20.emf", "images/picture20.emf.svg");
    	export(ImageConstants.SVG, "images/picture25.emf", "images/picture25.emf.svg");
    	export(ImageConstants.SVG, "images/picture26.emf", "images/picture26.emf.svg");
    	export(ImageConstants.SVG, "images/picture27.emf", "images/picture27.emf.svg");
    	export(ImageConstants.SVG, "images/picture333.emf", "images/picture333.emf.svg");
    	export(ImageConstants.SVG, "images/picture335.emf", "images/picture335.emf.svg");
    	export(ImageConstants.SVG, "images/picture337.emf", "images/picture337.emf.svg");
    	export(ImageConstants.SVG, "images/picture340.emf", "images/picture340.emf.svg");
    	export(ImageConstants.SVG, "images/shpLogo.emf", "images/shpLogo.emf.svg");
    	export(ImageConstants.SVG, "images/shpLogo1.emf", "images/shpLogo1.emf.svg");
    	}
    	else
    	{
    		//export(ImageConstants.SVG, "images/13.emf", "images/13.emf.svg");
        	export(ImageConstants.SVG, "images/shpLogo.emf", "images/shpLogo.emf.svg");
        	export(ImageConstants.SVG, "images/landscape.emf", "images/landscape.emf.svg");
        	//export(ImageConstants.SVG, "images/24.emf", "images/24.emf.svg");
    	}
    	
    	//export(ImageConstants.SVG, "images/picture333.emf", "images/picture333.emf.svg");
    	
    	
    	
        System.out.println("EXPORT FINISHED");
        
    }
}
