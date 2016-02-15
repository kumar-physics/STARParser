/*
 * ErrorHandlerNS.java
 *
 * Created on July 1, 2004, 1:59 PM
 *
 * This software is copyright (c) 2004 Board of Regents, University of Wisconsin.
 * All Rights Reserved.
 *
 * FILE:        $Source: /cvs_archive/cvs/sansj/src/EDU/bmrb/sansj/ErrorHandlerNS.java,v $
 * 
 * AUTHOR:      $Author: dmaziuk $
 * DATE:        $Date: 2006/04/03 22:40:40 $
 * 
 * UPDATE HISTORY:
 * ---------------
 * $Log: ErrorHandlerNS.java,v $
 * Revision 1.1  2006/04/03 22:40:40  dmaziuk
 * bug fix in lex specs.
 *
 * Revision 1.1  2004/07/01 19:57:32  dmaziuk
 * added another parser
 * */

package EDU.bmrb.sansj;

/**
 * Interface somewhat similar to org.xml.sax.ErrorHandler.
 * Declares callbacks a class must implement to use SANSj parser.
 * This interface differs from ErrorHandler in that error callback returns a
 * boolean.
 * @author  dmaziuk
 * @version 1
 */
public interface ErrorHandlerNS {
    /** Called when parser encounters an error.
     * @param line line number
     * @param col column number
     * @param msg error message
     * @return true signals parser to stop parsing
     */
    public boolean error( int line, int col, String msg );
    /** Called when parser encounters a possible error
     * @param line line number
     * @param col column number
     * @param msg error message
     * @return true signals parser to stop parsing
     */
    public boolean warning( int line, int col, String msg );
}

