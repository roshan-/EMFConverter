package com.iConverter;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import org.freehep.graphicsio.ImageConstants;
import org.freehep.graphicsio.emf.EMFConverter;


public class convMain extends EMFConverter{

	public static void main(String[] args) {
		final String inDir= "D:\\0\\t\\sampleEMFs\\";
		
		FileWriter html;
		try {
			html = new FileWriter(inDir+"out_adrian.html");
		          
		html.write("<html>\n<head><meta http-equiv=\"x-ua-compatible\" content=\"IE=edge,chrome=1\"></head><body>\n");
		
		for (File f :  new File(inDir).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name.endsWith(".emf");
			}
		})) {
			System.out.println("Processing..."+f.getName());
			export(ImageConstants.SVG, inDir+f.getName(), inDir+f.getName()+"_adrian.svg");		 		 
			html.write("\t<div>\n\t\t\n\t\t<br>\n\t\t<hr><p align=center>.SVG figure: "+ f.getName() + 
					"</p>\n\t\t<hr>\n\t\t<br>\n\t\t<br>\n\t\t<img src=\""+f.getName()+"_adrian.svg\" width=\"100%\" />\n\t</div>\n");
		}
		html.write("</body>\n</html>");
		html.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}		
}
