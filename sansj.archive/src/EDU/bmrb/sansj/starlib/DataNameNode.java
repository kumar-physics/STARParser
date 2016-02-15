/*
 * DataNameNode.java
 *
 * Created on July 16, 2003, 2:48 PM
 *
 * This software is copyright (c) 2002 Board of Regents, University of Wisconsin.
 * All Rights Reserved.
 *
 * FILE:        $Source: /cvs_archive/cvs/sansj/src/EDU/bmrb/sansj/starlib/DataNameNode.java,v $
 * 
 * AUTHOR:      $Author: dmaziuk $
 * DATE:        $Date: 2006/04/03 22:40:42 $
 * 
 * UPDATE HISTORY:
 * ---------------
 * $Log: DataNameNode.java,v $
 * Revision 1.1  2006/04/03 22:40:42  dmaziuk
 * bug fix in lex specs.
 *
 * Revision 1.1  2003/07/16 21:53:21  dmaziuk
 * added another parser
 *
 */

package EDU.bmrb.sansj.starlib;

/**
 * STAR tag.
 * This class is similar to starlib's DataNameNode.
 * @author  dmaziuk
 * @version 1
 */
public class DataNameNode extends StarNode {
    /** tag */
    private String fVal = null;
//*******************************************************************************
    /** Creates new DataNameNode */
    public DataNameNode() {
        super();
    } //*************************************************************************
    /** Creates new DataNameNode.
     * @param tag tag
     */
    public DataNameNode( String tag ) {
        super();
        fVal = tag;
    } //*************************************************************************
    /** Creates new DataNameNode.
     * @param line line number
     * @param col column
     */
    public DataNameNode( int line, int col ) {
        super( line, col );
    } //*************************************************************************
    /** Creates new DataNameNode.
     * @param line line number
     * @param col column
     * @param tag tag
     */
    public DataNameNode( int line, int col, String tag ) {
        super( line, col, tag );
    } //*************************************************************************
    /** Copy constructor.
     * @param node node to copy
     */
    public DataNameNode( DataNameNode node ) {
        super( node );
    } //*************************************************************************        
    /** Copies the node.
     * @return new node
     */
    public Object clone() {
        return new DataNameNode( this );
    } //*************************************************************************
}
