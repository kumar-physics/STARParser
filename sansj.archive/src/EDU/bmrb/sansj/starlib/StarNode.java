/*
 * StarNode.java
 *
 * Created on July 16, 2003, 2:23 PM
 *
 * This software is copyright (c) 2002 Board of Regents, University of Wisconsin.
 * All Rights Reserved.
 *
 * FILE:        $Source: /cvs_archive/cvs/sansj/src/EDU/bmrb/sansj/starlib/StarNode.java,v $
 * 
 * AUTHOR:      $Author: dmaziuk $
 * DATE:        $Date: 2006/04/03 22:40:42 $
 * 
 * UPDATE HISTORY:
 * ---------------
 * $Log: StarNode.java,v $
 * Revision 1.1  2006/04/03 22:40:42  dmaziuk
 * bug fix in lex specs.
 *
 * Revision 1.2  2004/06/15 00:48:45  dmaziuk
 * added loop parser
 *
 * Revision 1.1  2003/07/16 21:53:21  dmaziuk
 * added another parser
 *
 */

package EDU.bmrb.sansj.starlib;

/**
 * Parent class for all STAR nodes.
 * This class is similar to starlib's StarNode, sans search methods.  It has 
 * toString() method instead of starlib's Unparse().
 * @author  dmaziuk
 * @version 1
 */
public class StarNode implements Cloneable, java.io.Serializable {
    /** line number */
    protected int fLine = -1;
    /** column */
    protected int fCol = -1;
    /** parent node */
    protected StarNode fParent = null;
    /** preceding comment */
    protected String fComment = null;
    /** value */
    protected String fVal = null;
//*******************************************************************************
    /** Creates new StarNode. */
    public StarNode() {
    } //*************************************************************************
    /** Creates new StarNode. 
     * @param line line number
     * @param col column number
     */
    public StarNode( int line, int col ) {
        fLine = line;
        fCol = col;
    } //*************************************************************************
    /** Creates new StarNode. 
     * @param line line number
     * @param col column number
     * @param val value
     */
    public StarNode( int line, int col, String val ) {
        fLine = line;
        fCol = col;
        fVal = val;
    } //*************************************************************************
    /** Copy constructor.
     * @param node node to copy
     */
    public StarNode( StarNode node ) {
        fParent = node.getParent();
        fLine = node.getLineNum();
        fCol = node.getColNum();
        fComment = node.getPreComment();
        fVal = node.getValue();
    } //*************************************************************************
    /** Copies the node.
     * @return new node
     */
    public Object clone() {
        return new StarNode( this );
    } //*************************************************************************
    /** Returns column number
     * @return column number
     */
    public int getColNum() {
        return fCol;
    } //*************************************************************************
    /** Changes column number
     * @param col column
     */
    public void setColNum( int col ) {
        fCol = col;
    } //*************************************************************************
    /** Returns line number
     * @return line number
     */
    public int getLineNum() {
        return fLine;
    } //*************************************************************************
    /** Changes line number
     * @param line line number
     */
    public void setLineNum( int line ) {
        fLine = line;
    } //*************************************************************************
    /** Returns parent node
     * @return parent node
     */
    public StarNode getParent() {
        return fParent;
    } //*************************************************************************
    /** Changes parent node
     * @param node parent node
     */
    public void setParent( StarNode node ) {
        fParent = node;
    } //*************************************************************************
    /** Returns comment
     * @return comment
     */
    public String getPreComment() {
        return fComment;
    } //*************************************************************************
    /** Changes comment
     * @param comment comment text
     */
    public void setPreComment( String comment ) {
        fComment = comment;
    } //*************************************************************************
    /** Returns value
     * @return value
     */
    public String getValue() {
        return fVal;
    } //*************************************************************************
    /** Changes value
     * @param val value
     */
    public void setValue( String val ) {
        fVal = val;
    } //*************************************************************************
    /** Alias to getValue().
     * @return value
     */
    public String toString() {
        return fVal;
    } //*************************************************************************
}
