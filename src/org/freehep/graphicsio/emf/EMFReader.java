package org.freehep.graphicsio.emf;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Logger;

import org.freehep.util.io.Tag;

/**
 * Standalone EMF renderer.
 *
 * @author Daniel Noll (daniel@nuix.com)
 * @version $Id: freehep-graphicsio-emf/src/main/java/org/freehep/graphicsio/emf/EMFRenderer.java 10ec7516e3ce 2007/02/06 18:42:34 duns $
 */
public class EMFReader {
	private static final Logger logger = Logger.getLogger("org.freehep.graphicsio.emf");

	/**
	 * Header read from the EMFInputStream
	 */
	private EMFHeader header;


	/**
	 * stores the parsed tags. Filled by the constructor. Read by
	 * {@link #paint(java.awt.Graphics2D)}
	 */
	private Vector<Tag> tags = new Vector<Tag>(0);


	public long pending;
	/**
	 * Class the encapsulate the state of a Graphics2D object.
	 * Instances are store in dcStack by
	 * {@link org.freehep.graphicsio.emf.EMFReader#paint(java.awt.Graphics2D)}
	 */

	/**
	 * Constructs the renderer.
	 *
	 * @param is the input stream to read the EMF records from.
	 * @throws IOException if an error occurs reading the header.
	 */
	public EMFReader() throws IOException {
		/*this.header = is.readHeader();
		int i = 0;

		// read all tags
		Tag tag;
		while (((i++ < numTags) && (tag = is.readTag()) != null)){
			tags.add(tag);
		}                
		pending = is.getLength();
		is.close();*/
	}

	protected void read(String srcFileName, String destFileName, int offset, int nTags) {


		// read the EMF file
		try {
			/*EMFRenderer emfRenderer = new EMFRenderer(
					new EMFInputStream(
							new FileInputStream(srcFileName)));*/

			long total = new File(srcFileName).length();
			
			//long total = new EMFInputStream(new FileInputStream(srcFileName)).getLength();
			FileInputStream fileInput = new FileInputStream(srcFileName);
			EMFInputStream is =new EMFInputStream(fileInput);			
			Tag tag;
						
			this.header = is.readHeader();			
			
			int i = 0;
			while (((i++ < nTags) && (tag = is.readTag()) != null)){
				if (i>=offset)
				tags.add(tag);				
			}      
			long nbytes = fileInput.available();
			
			long pend = total -nbytes;
			//long pend = is.getLength();
			is.close();

			copia(srcFileName, destFileName, pend);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        

	}


	public static void copia (String srcFileName, String destFileName, long l)
	{
		try
		{
			// Se abre el fichero original para lectura
			FileInputStream fileInput = new FileInputStream(srcFileName);
			BufferedInputStream bufferedInput = new BufferedInputStream(fileInput);

			// Se abre el fichero donde se hará la copia
			FileOutputStream fileOutput = new FileOutputStream (destFileName);
			BufferedOutputStream bufferedOutput = new BufferedOutputStream(fileOutput);

			// Bucle para leer de un fichero y escribir en el otro.
			byte [] array = new byte[(int) l];
			int leidos = bufferedInput.read(array, 0, (int) l);
			bufferedOutput.write(array,0,leidos);
			
			byte[] eof = new byte[] {0x0e, 00,00,00,0x14,00,00,00,00,00,00,00,0x10,00,00,00,0x14,00,00,00};
			bufferedOutput.write(eof,0,eof.length);
			/*while (leidos > 0)
    {
        bufferedOutput.write(array,0,leidos);
        leidos=bufferedInput.read(array);
    }*/

			// Cierre de los ficheros
			bufferedInput.close();
			bufferedOutput.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
 }
