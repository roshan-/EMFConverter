// Copyright 2002-2007, FreeHEP.
package org.freehep.graphicsio.svg;

import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsbase.util.UserProperties;
import org.freehep.graphicsio.exportchooser.AbstractExportFileType;

import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsbase.util.UserProperties;
import org.freehep.graphicsio.exportchooser.AbstractExportFileType;
import org.freehep.graphicsio.exportchooser.OptionCheckBox;

/**
 * 
 * @author Mark Donszelmann
 * @version $Id: freehep-graphicsio-svg/src/main/java/org/freehep/graphicsio/svg/SVGExportFileType.java 4c4708a97391 2007/06/12 22:32:31 duns $
 */
public class SVGExportFileType extends AbstractExportFileType {

	// Constants for the SVG options.
	final private static String versionList[] = {
	// SVGGraphics2D.VERSION_1_0,
	SVGGraphics2D.VERSION_1_1,
	// SVGGraphics2D.VERSION_1_2
	};

	public String getDescription() {
		return "Scalable Vector Graphics";
	}

	public String[] getExtensions() {
		return new String[] { "svg", "svgz" };
	}

	public String[] getMIMETypes() {
		return new String[] { "image/svg+xml" };
	}

	public boolean hasOptionPanel() {
		return true;
	}

public VectorGraphics getGraphics(OutputStream os, Component target)
			throws IOException {

		return new SVGGraphics2D(os, target);
	}

	public VectorGraphics getGraphics(OutputStream os, Dimension dimension)
			throws IOException {

		return new SVGGraphics2D(os, dimension);
	}

	public VectorGraphics getGraphics(File file, Component target)
			throws IOException {

		
		return new SVGGraphics2D(file, target);
	}

	public VectorGraphics getGraphics(File file, Dimension dimension)
			throws IOException {

		return new SVGGraphics2D(file, dimension);
	}

	/*public File adjustFilename(File file, Properties user) {
		UserProperties options = new UserProperties(user, SVGGraphics2D
				.getDefaultProperties());
		if (options.isProperty(SVGGraphics2D.COMPRESS)) {
			return adjustExtension(file, "svgz", null, "");
		} else {
			return adjustExtension(file, "svg", null, "");
		}
	}*/

}
