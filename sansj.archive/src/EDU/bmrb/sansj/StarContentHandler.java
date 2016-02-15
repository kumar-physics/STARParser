/*
 * StarContentHandler.java
 *
 * Created on July 16, 2003, 3:20 PM
 *
 * Created on July 16, 2003, 2:57 PM
 *
 * This software is copyright (c) 2002 Board of Regents, University of Wisconsin.
 * All Rights Reserved.
 *
 * FILE:        $Source: /cvs_archive/cvs/sansj/src/EDU/bmrb/sansj/StarContentHandler.java,v $
 * 
 * AUTHOR:      $Author: dmaziuk $
 * DATE:        $Date: 2006/04/03 22:40:41 $
 * 
 * UPDATE HISTORY:
 * ---------------
 * $Log: StarContentHandler.java,v $
 * Revision 1.1  2006/04/03 22:40:41  dmaziuk
 * bug fix in lex specs.
 *
 * Revision 1.1  2003/07/16 21:53:21  dmaziuk
 * added another parser
 *
 */

package EDU.bmrb.sansj;

import EDU.bmrb.sansj.starlib.*;

/**
 * Callback interface similar to ContentHandler.
 * Unlike ContentHandler, this interface defines separate callbacks for tags and
 * values, and no callback for comments. Tokens are passed as StarNodes, comment
 * is added to the node that follows it (multi-line comments are rolled into one
 * line). End-of-token callbacks pass StarNode with no value (but there may be a
 * comment).
 * @author  dmaziuk
 * @version 1
 */
public interface StarContentHandler {
    /** Called when parser encounters data_ block.
     * @param node StarNode
     * @return true to stop parsing
     */
    boolean startData( StarNode node );
    /** Called on EOF.
     * @param node StarNode
     */
    void endData( StarNode node );
    /** Called on start of saveframe (parser encounters save_<name>).
     * @param node StarNode
     * @return true to stop parsing
     */
    boolean startSaveFrame( StarNode node );
    /** Called on end of saveframe (parser encounters save_).
     * @param node StarNode
     * @return true to stop parsing
     */
    boolean endSaveFrame( StarNode node );
    /** Called on start of loop (parser encounters loop_).
     * @param node StarNode
     * @return true to stop parsing
     */
    boolean startLoop( StarNode node );
    /** Called on end of loop (parser encounters stop_).
     * @param node StarNode
     * @return true to stop parsing
     */
    boolean endLoop( StarNode node );
    /** Called when parser encounters a tag.
     * @param node DataNameNode
     * @return true to stop parsing
     */
    boolean tag( DataNameNode node );
    /** Called when parser encounters a value.
     * @param node DataValueNode
     * @return true to stop parsing
     */
    boolean data( DataValueNode node );
}

