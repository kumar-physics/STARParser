/*
 * IntStringPair.java
 *
 * Created on November 22, 2002, 5:47 PM
 *
 * This software is copyright (c) 2002 Board of Regents, University of Wisconsin.
 * All Rights Reserved.
 *
 * FILE:        $Source: /cvs_archive/cvs/sansj/src/EDU/bmrb/sansj/IntStringPair.java,v $
 * 
 * AUTHOR:      $Author: dmaziuk $
 * DATE:        $Date: 2006/04/03 22:40:40 $
 * 
 * UPDATE HISTORY:
 * ---------------
 * $Log: IntStringPair.java,v $
 * Revision 1.1  2006/04/03 22:40:40  dmaziuk
 * bug fix in lex specs.
 *
 * Revision 1.1.1.1  2003/01/10 22:01:19  dmaziuk
 * initial import
 *
 */

package EDU.bmrb.sansj;

/**
 * Object to hold text string and its line number.
 * @author  dmaziuk
 * @version 1
 */
public class IntStringPair {
    /** line number */
    private int fLine = -1;
    /** text */
    private String fTxt = null;
    /** Creates new IntStringPair.
     * @param line line number
     * @param txt text
     */
    public IntStringPair( int line, String txt ) {
        fLine = line;
        fTxt = txt;
    } //*************************************************************************
    /** Returns line number.
     * @return line number
     */
    public int getLineNumber() {
        return fLine;
    } //*************************************************************************
    /** Returns text.
     * @return text
     */
    public String getText() {
        return fTxt;
    } //*************************************************************************
}
