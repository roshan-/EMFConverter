// Copyright 2001-2009, FreeHEP.
package org.freehep.graphicsio.font.truetype;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * TrueType Font with all its tables.
 * 
 * @author Simon Fischer
 * @version $Id: freehep-graphicsio/src/main/java/org/freehep/graphicsio/font/truetype/TTFFont.java 5641ca92a537 2005/11/26 00:15:35 duns $
 */
public abstract class TTFFont {

    private Map<String, Object> entry = new HashMap<String, Object>();

    public abstract int getFontVersion();

    void newTable(String tag, TTFInput input) throws IOException {
        entry.put(tag, initTable(tag, input));
    }

    private Object initTable(String name, TTFInput input) throws IOException {
        TTFTable table = null;
        for (int i = 0; i < TTFTable.TT_TAGS.length; i++) {
            if (name.equals(TTFTable.TT_TAGS[i])) {
                    try {
						table = (TTFTable) TTFTable.TABLE_CLASSES[i].newInstance();
					} catch (InstantiationException e) {
						throw new IOException(e);
					} catch (IllegalAccessException e) {
						throw new IOException(e);
					}
                    table.init(this, input);
                    return table;
            }
        }
//        System.err.println("Table '" + name + "' ignored.");
        return null;
    }

    public void show() {
        System.out.println("Tables:");
        for (Iterator<Object> i = entry.values().iterator(); i.hasNext();) {
            System.out.println(i.next());
        }
    }

    /** Returns the table with the given tag and reads it if necessary. */
    public TTFTable getTable(String tag) throws IOException {
        TTFTable table = (TTFTable) entry.get(tag);
        if (!table.isRead())
            table.read();
        return table;
    }

    /**
     * Reads all tables. This method does not need to be called since the tables
     * are read on demand (<tt>getTable()</tt>. It might be useful to call
     * it in order to print out all available information.
     */
    public void readAll() throws IOException {
        Iterator<Object> i = entry.values().iterator();
        while (i.hasNext()) {
            TTFTable table = (TTFTable) i.next();
            if ((table != null) && (!table.isRead()))
                table.read();
        }
    }

    public void close() throws IOException {
    }
}
