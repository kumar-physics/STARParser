/*
 * Copyright (c) 2005 Board of Regents, University of Wisconsin.
 * All Rights Reserved.
 */
package edu.bmrb.sans;

/**
 * Interface similar to org.xml.sax.ContentHandler.
 * Declares callback methods a class must implement to use SANS parser.
 * <P>
 * Most methods in here can return true to interrupt the parsing.
 * <P>
 * Tag-value callback returns tag-value pairs for both free tags and loop tags (there is a "loop flag" in DataItemNode
 * to distinguish them).
 *
 * @see DataItemNode
 */

/* Created by IntelliJ IDEA.
 * User: dmaziuk
 * Date: Nov 9, 2005
 * Time: 5:51:27 PM
 *
 * $Source: /cvs_archive/cvs/starlibs5/sans/src/edu/bmrb/sans/ContentHandler.java,v $
 * $Author: dmaziuk $
 * Initial import: $Date: 2006/03/07 22:51:52 $
 * Update history:
 * ---------------
 * $Log: ContentHandler.java,v $
 * Revision 1.2  2006/03/07 22:51:52  dmaziuk
 * Javadoc cleanup
 *
 * Revision 1.1.1.1  2005/12/05 20:08:07  dmaziuk
 * initial import
 * */

public interface ContentHandler {
    /** Called when parser encounters <TT>data_</TT> block.
     * @param line line numer
     * @param id block id
     * @return true to stop parsing
     */
    public boolean startData( int line, String id );
    /** Called on (implicit) end of <TT>data_</TT> block (EOF in NMR-STAR).
     * @param line line numer
     * @param id block id
     */
    public void endData( int line, String id );
    /** Called on start of saveframe.
     * @param line line numer
     * @param name saveframe name
     * @return true to stop parsing
     */
    public boolean startSaveFrame( int line, String name );
    /** Called on end of saveframe.
     * @param line line numer
     * @param name saveframe name
     * @return true to stop parsing
     */
    public boolean endSaveFrame( int line, String name );
    /** Called on start of loop.
     * @param line line numer
     * @return true to stop parsing
     */
    public boolean startLoop( int line );
    /** Called on end of loop.
     * @param line line numer
     * @return true to stop parsing
     */
    public boolean endLoop( int line );
    /** Called on coment.
     * @param line line numer
     * @param text comment text
     * @return true to stop parsing
     */
    public boolean comment( int line, String text );
    /** Called when parser encounters a tag-value pair.
     * @param data tag-value pair
     * @return true to stop parsing
     */
    public boolean data( DataItemNode data );
}
