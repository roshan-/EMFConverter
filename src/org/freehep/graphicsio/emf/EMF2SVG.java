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
    	export(ImageConstants.SVG, "image2.emf", "image2.svg");
    	//export(ImageConstants.SVG, "image5.emf", "image5.svg");
    	//export(ImageConstants.SVG, "excel-ppt-2.emf", "excel-ppt-2.svg");
    	
    	//export(ImageConstants.SVG, "out.emf", "aaaa.svg");
    	            	
    	//export(ImageConstants.SVG, "Infographics.emf", "Infographics.svg");
        
        
        System.out.println("EXPORT FINISHED");
        
    }
}
