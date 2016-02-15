/*
 * ContentHandler.java
 *
 * Created on November 22, 2002, 3:51 PM
 *
 * This software is copyright (c) 2002 Board of Regents, University of Wisconsin.
 * All Rights Reserved.
 *
 * FILE:        $Source: /cvs_archive/cvs/sansj/src/EDU/bmrb/sansj/ContentHandler.java,v $
 * 
 * AUTHOR:      $Author: dmaziuk $
 * DATE:        $Date: 2006/04/03 22:40:40 $
 * 
 * UPDATE HISTORY:
 * ---------------
 * $Log: ContentHandler.java,v $
 * Revision 1.1  2006/04/03 22:40:40  dmaziuk
 * bug fix in lex specs.
 *
 * Revision 1.1.1.1  2003/01/10 22:01:19  dmaziuk
 * initial import
 *
 */

package EDU.bmrb.sansj;

/**
 * Interface similar to org.xml.sax.ContentHandler.
 * Declares callback methods that class must implement to use SANS parser.
 * @author  dmaziuk
 * @version 1
 */
public interface ContentHandler {
    /** Called when parser encounters data_ block.
     * @param line line number
     * @param id block id
     * @return true to stop parsing
     */
    boolean startData( int line, String id );
    /** Called on EOF.
     * @param line line number
     * @param id block id
     */
    void endData( int line, String id );
    /** Called on start of saveframe (parser encounters save_<name>).
     * @param line line number
     * @param name saveframe name
     * @return true to stop parsing
     */
    boolean startSaveFrame( int line, String name );
    /** Called on end of saveframe (parser encounters save_).
     * @param line line number
     * @param name saveframe name
     * @return true to stop parsing
     */
    boolean endSaveFrame( int line, String name );
    /** Called on start of loop (parser encounters loop_).
     * @param line line number
     * @return true to stop parsing
     */
    boolean startLoop( int line );
    /** Called on end of loop (parser encounters stop_).
     * @param line line number
     * @return true to stop parsing
     */
    boolean endLoop( int line );
    /** Called when parser encounters a comment.
     * @param line line number
     * @param text comment text
     * @return true to stop parsing
     */
    boolean comment( int line, String text );
    /** Called when parser encounters a tag-value pair.
     * Note that parser returns a "fake" DataItemNode for values in a loop.
     * @param data data
     * @return true to stop parsing
     */
    boolean data( DataItemNode data );
}

