/*
 * Copyright (c) 2005 Board of Regents, University of Wisconsin.
 * All Rights Reserved.
 */
package edu.bmrb.sans;

/**
 * Data structure that holds an int and a string.
 * <P>
 * Used by sans parser that returns tag/value pairs as <CODE>DataItemNode</CODE>:
 * when parsing loop header, it needs to save tags and their line numbers.
 *
 */

/*
 * Created by IntelliJ IDEA.
 * User: dmaziuk
 * Date: Nov 10, 2005
 * Time: 5:57:42 PM
 *
 * $Source: /cvs_archive/cvs/starlibs5/sans/src/edu/bmrb/sans/IntStringPair.java,v $
 * $Author: dmaziuk $
 * Initial import: $Date: 2006/03/07 22:51:52 $
 * Update history:
 * ---------------
 * $Log: IntStringPair.java,v $
 * Revision 1.2  2006/03/07 22:51:52  dmaziuk
 * Javadoc cleanup
 *
 * Revision 1.1.1.1  2005/12/05 20:08:07  dmaziuk
 * initial import
 * */

public class IntStringPair {
    /** int */
    private int fNum = -1;
    /** String */
    private String fStr = null;
//*******************************************************************************
    /** Creates new IntStringPair.
     * @param num int member
     * @param str string member
     */
    public IntStringPair( int num, String str ) {
        fNum = num;
        fStr = str;
    } //*************************************************************************
    /** Returns int.
     * @return int member.
     */
    public int getInt() {
        return fNum;
    } //*************************************************************************
    /** Returns string.
     * @return string member
     */
    public String getString() {
        return fStr;
    } //*************************************************************************
    /** Alias to getInt().
     * @return int member
     */
    public int getLineNumber() {
        return getInt();
    } //*************************************************************************
    /** Alias to getString().
     * @return string member
     */
    public String getText() {
        return getString();
    } //*************************************************************************
}
