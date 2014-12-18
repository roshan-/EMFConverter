package org.freehep.graphicsio.emf;

import org.freehep.graphicsio.emf.EMFRenderer;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFPanel;
import org.freehep.graphicsio.pdf.PDFGraphics2D;

import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class EMF2PDF
{
	public static void main(String[] args)
	{
		FileInputStream input = null;
		FileOutputStream output = null;

		try
		{
			// read the EMF file
			input = new FileInputStream("landscape.emf");
			EMFRenderer emfRenderer = new EMFRenderer(new EMFInputStream(input));
			EMFPanel emfPanel = new EMFPanel();
			emfPanel.setRenderer(emfRenderer);

			// Print EMF file to PDFGraphics device
			Properties properties = new Properties();
			properties.setProperty(PDFGraphics2D.TEXT_AS_SHAPES, "false");
			properties.setProperty(PDFGraphics2D.CLIP, "false");

			output = new FileOutputStream("l.pdf");
			PDFGraphics2D graphics = new PDFGraphics2D(output, new Dimension(3644, 1536));
			graphics.setProperties(properties);
			graphics.startExport();
			emfPanel.print(graphics);
			graphics.endExport();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (input != null)
				{
					input.close();
				}
				if (output != null)
				{   
					output.close();
				}
			}
			catch (Exception ex) {}
		}
	}
}

