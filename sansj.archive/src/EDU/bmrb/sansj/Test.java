/*
 * Test.java
 *
 * Created on November 26, 2002, 6:02 PM
 *
 * This software is copyright (c) 2002 Board of Regents, University of Wisconsin.
 * All Rights Reserved.
 *
 * FILE:        $Source: /cvs_archive/cvs/sansj/src/EDU/bmrb/sansj/Test.java,v $
 * 
 * AUTHOR:      $Author: dmaziuk $
 * DATE:        $Date: 2006/04/03 22:40:41 $
 * 
 * UPDATE HISTORY:
 * ---------------
 * $Log: Test.java,v $
 * Revision 1.1  2006/04/03 22:40:41  dmaziuk
 * bug fix in lex specs.
 *
 * Revision 1.5  2004/03/03 23:11:45  dmaziuk
 * fixed a bug in comment parsing
 *
 * Revision 1.4  2003/05/16 00:57:53  dmaziuk
 * Fixed a bug in end-of-loop processing (CIFs don't have stop_)
 *
 * Revision 1.3  2003/04/24 21:05:48  dmaziuk
 * updated javadoc
 *
 * Revision 1.2  2003/04/24 20:25:42  dmaziuk
 * Added CIF parser, fixed memory leak
 *
 * Revision 1.1.1.1  2003/01/10 22:01:19  dmaziuk
 * initial import
 *
 */

package EDU.bmrb.sansj;

/**
 * Simple test for SANSj parser.
 * Prints out tokens.
 * @author  dmaziuk
 * @version 1
 */
public class Test implements ErrorHandler, ContentHandler {
    /** Creates new Test */
    public Test() {
    } //*************************************************************************
    /** Parses input */
    public void parse_jflex( java.io.BufferedReader in ) {
        try { 
            STARLexer lex = new STARLexer( in );
            SansParser parser = new SansParser( lex );
            parser.setContentHandler( this );
            parser.setErrorHandler( this );
            parser.parse(); 
        }
        catch( Exception e ) { e.printStackTrace(); }
    } //*************************************************************************
    /** Parses input */
    public void parse_cif( java.io.BufferedReader in ) {
        try { 
            STARLexer lex = new STARLexer( in );
            CifParser parser = new CifParser( lex );
            parser.setContentHandler( this );
            parser.setErrorHandler( this );
            parser.parse(); 
        }
        catch( Exception e ) { e.printStackTrace(); }
    } //*************************************************************************
    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) {
        try {
            java.io.InputStream sin;
            if( args.length < 1 ) sin = System.in;
            else sin = new java.io.FileInputStream( args[0] );
            java.io.BufferedReader in = new java.io.BufferedReader ( 
            new java.io.InputStreamReader( sin ) );
            Test t = new Test();
            long start = System.currentTimeMillis();
            t.parse_jflex( in );
//            t.parse_cif( in );
            long jflex = System.currentTimeMillis() - start;
            System.out.println( "NMRSTARLexer: " + jflex + "(" + (jflex/1000.0) + " sec)" );
        }
        catch( Exception e ) { e.printStackTrace(); }
    } //*************************************************************************
    /** Called on EOF.
     * @param line line number
     * @param id block id
     */
    public void endData(int line, String id) {
        System.out.println( line + ": end of data block " + id );
    } //*************************************************************************
    /** Called on start of saveframe (parser encounters save_<name>).
     * @param line line number
     * @param name saveframe name
     * @return true to stop parsing
     */
    public boolean startSaveFrame(int line, String name) {
//        System.out.println( line + ": start of saveframe " + name );
        return false;
    } //*************************************************************************
    /** Called when parser encounters a comment.
     * @param line line number
     * @param text comment text
     * @return true to stop parsing
     */
    public boolean comment(int line, String text) {
        System.out.println( line + ": comment " + text );
        return false;
    } //*************************************************************************
    /** Called when parser encounters a tag-value pair.
     * Note that parser returns a "fake" DataItemNode for values in a loop.
     * @param data data
     * @return true to stop parsing
     */
    public boolean data(DataItemNode data) {
        System.out.println( data );
        return false;
    } //*************************************************************************
    /** Called on end of saveframe (parser encounters save_).
     * @param line line number
     * @param name saveframe name
     * @return true to stop parsing
     */
    public boolean endSaveFrame(int line, String name) {
//        System.out.println( line + ": end of saveframe " + name );
        return false;
    } //*************************************************************************    
    /** Called when parser encounters data_ block.
     * @param line line number
     * @param id block id
     * @return true to stop parsing
     */
    public boolean startData(int line, String id) {
//        System.out.println( line + ": start of data block " + id );
        return false;
    } //*************************************************************************
    /** Called on start of loop (parser encounters loop_).
     * @param line line number
     * @return true to stop parsing
     */
    public boolean startLoop(int line) {
        System.out.println( line + ": start of loop" );
        return false;
    } //*************************************************************************
    /** Called on end of loop (parser encounters stop_).
     * @param line line number
     * @return true to stop parsing
     */
    public boolean endLoop(int line) {
//        System.out.println( line + ": end of loop" );
        return false;
    } //*************************************************************************
    /** Called when parser encounters a possible error
     * @param line line number
     * @param col column number
     * @param msg error message
     * @return true signals parser to stop parsing
     */
    public boolean warning(int line, int col, String msg) {
        System.err.println( "WARN:" + line + ":" + col + ":" + msg );
        return false;
    } //*************************************************************************
    /** Called when parser encounters an error.
     * @param line line number
     * @param col column number
     * @param msg error message
     */
    public void error(int line, int col, String msg) {
        System.err.println( "ERR:" + line + ":" + col + ":" + msg );
    } //*************************************************************************
}
