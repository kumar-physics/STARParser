/*
 * Copyright (c) 2005 Board of Regents, University of Wisconsin.
 * All Rights Reserved.
 */
package edu.bmrb.sans;

/**
 * Interface similar to org.xml.sax.Errorhandler.
 * Declares callbacks a class must implement ot use SANS parser.
 * <P>
 * Implementing parsers should call <CODE>fatalError()</CODE> on unrecoverable errors:
 * lexer errors, I/O exceptions, etc., <CODE>error()</CODE> on recoverable errors
 * (e.g. STAR syntax errors), and <CODE>warning()</CODE> on possible errors.
 * It is, however, up to the articular application to decide what errors unrecoverable
 * and what errors warrant a mere warning.
 * <P>
 * Callbacks in this interface return a boolean: true to stop parsing. Most of
 * the time you'd return true from the error callback, returning false can be
 * useful if you want to try and find as many errors as possible on the first pass.
 *
 */

/*
 * Created by IntelliJ IDEA.
 * User: dmaziuk
 * Date: Nov 9, 2005
 * Time: 5:52:26 PM
 *
 * $Source: /cvs_archive/cvs/starlibs5/sans/src/edu/bmrb/sans/ErrorHandler.java,v $
 * $Author: dmaziuk $
 * Initial import: $Date: 2006/03/07 22:51:52 $
 * Update history:
 * ---------------
 * $Log: ErrorHandler.java,v $
 * Revision 1.2  2006/03/07 22:51:52  dmaziuk
 * Javadoc cleanup
 *
 * Revision 1.1.1.1  2005/12/05 20:08:07  dmaziuk
 * initial import
 * */

public interface ErrorHandler {
    /** Called when parser encounters a fatal error and has to stop.
     * @param line line number
     * @param col column number
     * @param msg error message
     */
    public void fatalError( int line, int col, String msg );
    /** Called when parser encounters an error.
     * @param line line number
     * @param col column number
     * @param msg error message
     * @return true signals parser to stop
     */
    public boolean error( int line, int col, String msg );
    /** Called when parser generates a warning.
     * @param line line number
     * @param col column number
     * @param msg error message
     * @return true signals parser to stop
     */
    public boolean warning( int line, int col, String msg );
}
