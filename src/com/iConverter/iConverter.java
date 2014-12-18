package com.iConverter;
import org.freehep.graphicsio.ImageConstants;
import org.freehep.graphicsio.emf.EMFConverter;


public class iConverter extends EMFConverter{
	
	public static void main(String[] args) {		
		System.out.println("Working Directory = " +
	              System.getProperty("user.dir"));
		 export(ImageConstants.SVG, args[0], args.length > 1 ? args[1] : null);		 		 

	}

}
