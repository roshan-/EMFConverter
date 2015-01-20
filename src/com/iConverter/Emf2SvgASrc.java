package com.iConverter;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import org.freehep.graphicsio.ImageConstants;
import org.freehep.graphicsio.emf.EMFConverter;


public class Emf2SvgASrc extends EMFConverter{
	private static final String tag= "adrianSrc\\";

	public static void main(String[] args) {
		final String inDir= "D:\\0\\t\\SampleEMFs\\small\\";
		
		FileWriter html;
		try {
			html = new FileWriter(inDir+tag+"out.html");
		          
		html.write("<html>\n<head><meta http-equiv=\"x-ua-compatible\" content=\"IE=edge,chrome=1\"></head><body>\n");
		
		for (File f :  new File(inDir).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".emf") || name.endsWith(".wmf");
			}
		})) {
			System.out.println("Processing..."+f.getName());
			try {
				String outFile= inDir+tag+f.getName()+".svg";
				File of= new File(outFile);
				of.delete();
				export(ImageConstants.SVG, inDir+f.getName(), outFile);
			} catch (Throwable t) {
				t.printStackTrace();
			}
			html.write("\t<div>\n\t\t\n\t\t<br>\n\t\t<hr><p align=center>.SVG figure: "+ f.getName() + 
					"</p>\n\t\t<hr>\n\t\t<br>\n\t\t<br>\n\t\t<img src=\""+f.getName()+".svg\" width=\"100%\" />\n\t</div>\n");
		}
		html.write("</body>\n</html>");
		html.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}		
}
