/*
 * Copyright (c) 2006 Board of Regents, University of Wisconsin.
 *  All Rights Reserved.
 */
package edu.bmrb.sans;

/**
 * Interface similar to org.xml.sax.ContentHandler.
 * Declares callback methods a class must implement to use SANS parser.
 * <P>
 * Most methods in here can return true to interrupt the parsing.
 * <P>
 * Unlike ContentHandler, this interface has separate callbacks for tags and values.
 *
 * @author D. Maziuk
 * @see ContentHandler
 */

/*
 * Created by IntelliJ IDEA.
 * User: dmaziuk
 * Date: Jan 5, 2006
 * Time: 6:44:01 PM
 *
 * $Source: /cvs_archive/cvs/starlibs5/sans/src/edu/bmrb/sans/ContentHandler2.java,v $
 * $Author: dmaziuk $
 * Initial import: $Date: 2006/03/07 22:51:52 $
 * Update history:
 * ---------------
 * $Log: ContentHandler2.java,v $
 * Revision 1.2  2006/03/07 22:51:52  dmaziuk
 * Javadoc cleanup
 *
 * Revision 1.1  2006/01/30 22:56:40  dmaziuk
 * *** empty log message ***
 * */

public interface ContentHandler2 {
    /** Called when parser encounters <TT>data_</TT> block.
     * @param line line numer
     * @param id block id
     * @return true to stop parsing
     */
    public boolean startData( int line, String id );
    /** Called on EOF (implicit) end of <TT>data_</TT> block (EOF in NMR-STAR).
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
    /** Called when parser encounters a tag.
     * @param line line numer
     * @param tag tag
     * @return true to stop parsing
     */
    public boolean tag( int line, String tag );
    /** Called when parser encounters a value.
     * @param line line numer
     * @param val value
     * @param delim delimiter type
     * @return true to stop parsing
     */
    public boolean value( int line, String val, STARLexer.Types delim );
}
