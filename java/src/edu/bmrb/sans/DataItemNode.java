/*
 * Copyright (c) 2005 Board of Regents, University of Wisconsin.
 * All Rights Reserved.
 */
package edu.bmrb.sans;

/**
 * DataItemNode class similar to the one in starlibj, only much simpler.
 * <P>
 * Tag and value are stored as strings, there's a "loop" flag: false for free
 * tags, true for loop tags.
 */

/*
 * Created by IntelliJ IDEA.
 * User: dmaziuk
 * Date: Nov 9, 2005
 * Time: 6:04:46 PM
 *
 * $Source: /cvs_archive/cvs/starlibs5/sans/src/edu/bmrb/sans/DataItemNode.java,v $
 * $Author: dmaziuk $
 * Initial import: $Date: 2006/03/07 22:51:52 $
 * Update history:
 * ---------------
 * $Log: DataItemNode.java,v $
 * Revision 1.3  2006/03/07 22:51:52  dmaziuk
 * Javadoc cleanup
 *
 * Revision 1.2  2005/12/07 00:26:49  dmaziuk
 * Working
 *
 * Revision 1.1.1.1  2005/12/05 20:08:07  dmaziuk
 * initial import
 * */

public class DataItemNode {
    /** tag */
    private String fTag = null;
    /** tag line number */
    private int fTagLine = -1;
    /** value */
    private String fVal = null;
    /** value line number */
    private int fValLine = -1;
    /** value delimiter type */
    private STARLexer.Types fDel = null;
    /** loop flag */
    private boolean fInLoop = false;
//*******************************************************************************
    /** Creates new DataItemNode */
    public DataItemNode() {
    } //*************************************************************************
    /** Creates new DataItemNode for tag.
     * @param tag line number/tag name pair
     */
    public DataItemNode( IntStringPair tag ) {
        fTagLine = tag.getInt();
        fTag = tag.getString();
    } //*************************************************************************
    /** Creates new DataItemNode for tag.
     * @param tag tag name
     * @param val value
     * @param delim dlimiter
     */
    public DataItemNode( String tag, String val, STARLexer.Types delim ) {
        fTag = tag;
        fVal = val;
        fDel = delim;
    } //*************************************************************************
    /** Creates new DataItemNode for tag.
     * @param tagline tag line number
     * @param tag tag
     */
    public DataItemNode( int tagline, String tag ) {
        fTagLine = tagline;
        fTag = tag;
    } //*************************************************************************
    /** Changes tag line number.
     * @param tagline tag line number
     */
    public void setTagLine( int tagline ) {
        fTagLine = tagline;
    } //*************************************************************************
    /** Returns tag line number.
     * @return tag line number
     */
    public int getTagLine() {
        return fTagLine;
    } //*************************************************************************
    /** Changes tag.
     * @param tag tag
     */
    public void setName( String tag ) {
        fTag = tag;
    } //*************************************************************************
    /** Returns tag.
     * @return tag
     */
    public String getName() {
        return fTag;
    } //*************************************************************************
    /** Alias for setName().
     * @param tag tag
     */
    public void setTag( String tag ) {
        setName( tag );
    } //*************************************************************************
    /** Alias for getName().
     * @return tag
     */
    public String getTag() {
        return getName();
    } //*************************************************************************
    /** Alias for getName().
     * @return tag
     */
    public String getLabel() {
        return getName();
    } //*************************************************************************
    /** Changes value line number.
     * @param valline value line number
     */
    public void setValueLine( int valline ) {
        fValLine = valline;
    } //*************************************************************************
    /** Returns value line number.
     * @return value line number
     */
    public int getValueLine() {
        return fValLine;
    } //*************************************************************************
    /** Changes value.
     * @param val value
     */
    public void setValue( String val ) {
        fVal = val;
    } //*************************************************************************
    /** Returns value.
     * @return value
     */
    public String getValue() {
        return fVal;
    } //*************************************************************************
    /** Changes delimiter type.
     * Delimiter type is one of <TT>DVN</TT> constants from STARLexer.
     * @param delim delimiter type
     */
    public void setDelimType( STARLexer.Types delim ) {
        fDel = delim;
    } //*************************************************************************
    /** Returns delimiter type.
     * Delimiter type is one of <TT>DVN</TT> constants from STARLexer.
     * @return delimiter type
     */
    public STARLexer.Types getDelimType() {
        return fDel;
    } //*************************************************************************
    /** Changes loop flag.
     * @param flag
     */
    public void setLoopFlag( boolean flag ) {
        fInLoop = flag;
    } //*************************************************************************
    /** Returns false for free tags, true for loop tags.
     * @return true or false
     */
    public boolean isInLoop() {
        return fInLoop;
    } //*************************************************************************
    /** Returns this node as formatted string.
     * @return string
     */
    public String toString() {
        StringBuffer buf = new StringBuffer( fTag );
        buf.append( " (" );
        buf.append( fTagLine );
        buf.append( "): " );
        buf.append( fVal );
        buf.append( " (" );
        buf.append( fValLine );
        buf.append( ')' );
        return buf.toString();
    } //*************************************************************************
}
