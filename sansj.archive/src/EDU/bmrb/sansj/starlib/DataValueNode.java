/*
 * DataValueNode.java
 *
 * Created on July 16, 2003, 2:57 PM
 *
 * This software is copyright (c) 2002 Board of Regents, University of Wisconsin.
 * All Rights Reserved.
 *
 * FILE:        $Source: /cvs_archive/cvs/sansj/src/EDU/bmrb/sansj/starlib/DataValueNode.java,v $
 * 
 * AUTHOR:      $Author: dmaziuk $
 * DATE:        $Date: 2006/04/03 22:40:42 $
 * 
 * UPDATE HISTORY:
 * ---------------
 * $Log: DataValueNode.java,v $
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
 * STAR value.
 * @author  dmaziuk
 * @version 1
 */
public class DataValueNode extends StarNode {
    /** delimiter type */
    private int fDelim = -1;
//*******************************************************************************
    /** Creates new DataValueNode */
    public DataValueNode() {
        super();
    } //*************************************************************************
    /** Creates new DataValueNode.
     * Delimiter type is STARLexer.DVNNON
     * @param val value
     */
    public DataValueNode( String val ) {
        super();
        fVal = val;
        fDelim = EDU.bmrb.sansj.STARLexer.DVNNON;
    } //*************************************************************************
    /** Creates new DataValueNode.
     * @param val value
     * @param delim delimier type
     */
    public DataValueNode( String val, int delim ) {
        super();
        fVal = val;
        fDelim = delim;
    } //*************************************************************************
    /** Creates new DataValueNode.
     * @param line line number
     * @param col column
     */
    public DataValueNode( int line, int col ) {
        super( line, col );
    } //*************************************************************************
    /** Creates new DataValueNode.
     * @param line line number
     * @param col column
     * @param val value
     * @param delim delimier type
     */
    public DataValueNode( int line, int col, String val, int delim ) {
        super( line, col, val );
        fDelim = delim;
    } //*************************************************************************
    /** Copy constructor
     * @param node node to copy
     */
    public DataValueNode( DataValueNode node ) {
        super( node );
        fDelim = node.getDelimType();
    } //*************************************************************************
    /** Copies the node.
     * @return new node
     */
    public Object clone() {
        return new DataValueNode( this );
    } //*************************************************************************
    /** Returns delimiter type.
     * Delimiter type is one of the STARLexer.DVN* constants.
     * @return delimiter type
     */
    public int getDelimType() {
        return fDelim;
    } //*************************************************************************
    /** Changes delimiter type.
     * If new type is not valid, no change is made
     * @param delim new delimiter type
     */
    public void setDelimType( int delim ) {
        switch( delim ) {
            case EDU.bmrb.sansj.STARLexer.DVNNON :
            case EDU.bmrb.sansj.STARLexer.DVNSINGLE :
            case EDU.bmrb.sansj.STARLexer.DVNDOUBLE :
            case EDU.bmrb.sansj.STARLexer.DVNSEMICOLON :
            case EDU.bmrb.sansj.STARLexer.DVNFRAMECODE :
                fDelim = delim;
        }
    } //*************************************************************************
    /** Returns length of value plus delimiters.
     * Does not work too well for semicolon-delimited values.
     * @return length of the value
     */
    public int length() {
        int rc = fVal.length();
        switch( fDelim ) {
            case EDU.bmrb.sansj.STARLexer.DVNSINGLE :
            case EDU.bmrb.sansj.STARLexer.DVNDOUBLE :
                return rc + 2;
            case EDU.bmrb.sansj.STARLexer.DVNSEMICOLON :
                return rc + 2 + 4 * "\n".length();
            case EDU.bmrb.sansj.STARLexer.DVNFRAMECODE :
                return rc + 1;
            default : return rc;
        }
    } //*************************************************************************
    /** Alias to length();
     * @return length of the value
     */
    public int myLongestStr() {
        return length();
    } //*************************************************************************
    /** Returns node as quoted STAR string.
     * @return node as string
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        switch( fDelim ) {
            case EDU.bmrb.sansj.STARLexer.DVNSINGLE :
                buf.append( '\'' );
                buf.append( fVal );
                buf.append( '\'' );
                break;
            case EDU.bmrb.sansj.STARLexer.DVNDOUBLE :
                buf.append( '"' );
                buf.append( fVal );
                buf.append( '"' );
                break;
            case EDU.bmrb.sansj.STARLexer.DVNSEMICOLON :
                buf.append( "\n;\n" );
                buf.append( fVal );
                buf.append( "\n;\n" );
                break;
            case EDU.bmrb.sansj.STARLexer.DVNFRAMECODE :
                buf.append( "$" );
                buf.append( fVal );
                break;
            default : 
                buf.append( fVal );
        }
        return buf.toString();
    } //*************************************************************************
}
