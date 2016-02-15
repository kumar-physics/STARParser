/*
 * ErrorHandler.java
 *
 * Created on November 22, 2002, 5:05 PM
 *
 * This software is copyright (c) 2002 Board of Regents, University of Wisconsin.
 * All Rights Reserved.
 *
 * FILE:        $Source: /cvs_archive/cvs/sansj/src/EDU/bmrb/sansj/ErrorHandler.java,v $
 * 
 * AUTHOR:      $Author: dmaziuk $
 * DATE:        $Date: 2006/04/03 22:40:40 $
 * 
 * UPDATE HISTORY:
 * ---------------
 * $Log: ErrorHandler.java,v $
 * Revision 1.1  2006/04/03 22:40:40  dmaziuk
 * bug fix in lex specs.
 *
 * Revision 1.2  2004/07/01 19:57:32  dmaziuk
 * added another parser
 *
 * Revision 1.1.1.1  2003/01/10 22:01:19  dmaziuk
 * initial import
 *
 */

package EDU.bmrb.sansj;

/**
 * Interface somewhat similar to org.xml.sax.ErrorHandler.
 * Declares callbacks a class must implement to use SANSj parser.
 * @author  dmaziuk
 * @version 1
 */
public interface ErrorHandler {
    /** Called when parser encounters an error.
     * @param line line number
     * @param col column number
     * @param msg error message
     */
    public void error( int line, int col, String msg );
    /** Called when parser encounters a possible error
     * @param line line number
     * @param col column number
     * @param msg error message
     * @return true signals parser to stop parsing
     */
    public boolean warning( int line, int col, String msg );
}

