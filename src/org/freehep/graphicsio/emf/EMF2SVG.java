// Copyright FreeHEP 2006-2007.
package org.freehep.graphicsio.emf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
    	
    	
    /*	try {
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
        System.out.println("FILE SAVED");*/
 	

            
    	
    	
        boolean all = true;
        String dirbase = "sampleEMFs/";
/*    	if (all == true)
    	{
    		List<String> files = finder(System.getProperty("user.dir") + "/images/");
    		for (int i = 0; i< files.size(); i++)
    			export(ImageConstants.SVG, "images/"+ files.get(i), "images/"+ files.get(i)+".svg");    	
    	}*/
        if (all == true)
    	{        
    		List<String> files = finder(System.getProperty("user.dir") + "/"+dirbase);
    		for (int i = 0; i< files.size(); i++)
    			export(ImageConstants.SVG, dirbase+ files.get(i), dirbase+ files.get(i)+".svg");
    		createHTML(dirbase, files);
    		
    	}
    	else
    	{   
    		String filename = "10008800_image10.emf";

    		boolean createfile = false;
    		
    		if (createfile == true) {
    		int nTags = 397;			
			try {
				EMFReader reader = new EMFReader();
				reader.read(dirbase + filename, dirbase + "542321_image100_rec.emf", 500, nTags);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		}
			
			
			
//    		String filename = "542321_image100.emf";
        	export(ImageConstants.SVG, dirbase + filename, dirbase+filename + ".svg");        	
     /*   	export(ImageConstants.SVG, dirbase + "10008800_image12.emf", dirbase+"10008800_image12.emf.svg");
        	export(ImageConstants.SVG, dirbase + "10008800_image13.emf", dirbase+"10008800_image13.emf.svg");
       */ 	
    	}
    	    
    	
    	
    	
        System.out.println("EXPORT FINISHED");
        
    }
       
    public static List<String> finder( String directory){     
    	  List<String> textFiles = new ArrayList<String>();
    	  File dir = new File(directory);
    	  for (File file : dir.listFiles()) {
    	    if (file.getName().endsWith((".emf"))) {
    	      textFiles.add(file.getName());
    	    }
    	  }
    	  return textFiles;    	
    }
    
    public static void createHTML (String dir, List<String> files){
    	PrintWriter writer;
		try {
			writer = new PrintWriter(dir + "out.html", "UTF-8");							
			writer.println("<html>");
	    	writer.println("<head><meta http-equiv=\"x-ua-compatible\" content=\"IE=edge,chrome=1\"></head><body>");
	    	
	    	for (int i = 0; i < files.size(); i++)
	    	{
	    		writer.println("<div>");
	    		writer.println("<br>");
	    		writer.println("<hr><p align=center>.SVG figure: "+files.get(i)+"</p>");
	    		writer.println("<hr>");
	    		writer.println("<br><br>");
	    		writer.println("<img src=\""+ files.get(i) + ".svg\" width=\"100%\" />");
	    		writer.println("</div>");
	    	}
	    	writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	
    	
    }
}
