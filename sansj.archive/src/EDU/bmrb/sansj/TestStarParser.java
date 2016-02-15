/*
 * TestStarParser.java
 *
 * Created on July 21, 2003, 1:59 PM
 *
 * This software is copyright (c) 2002 Board of Regents, University of Wisconsin.
 * All Rights Reserved.
 *
 * FILE:        $Source: /cvs_archive/cvs/sansj/src/EDU/bmrb/sansj/TestStarParser.java,v $
 * 
 * AUTHOR:      $Author: dmaziuk $
 * DATE:        $Date: 2006/04/03 22:40:41 $
 * 
 * UPDATE HISTORY:
 * ---------------
 * $Log: TestStarParser.java,v $
 * Revision 1.1  2006/04/03 22:40:41  dmaziuk
 * bug fix in lex specs.
 *
 * Revision 1.3  2004/03/03 23:11:45  dmaziuk
 * fixed a bug in comment parsing
 *
 * Revision 1.2  2003/08/01 17:45:34  dmaziuk
 * bugfix: value without tag/tag without value is an error
 *
 * Revision 1.1  2003/07/21 19:12:53  dmaziuk
 * added test program for new parser
 *
 */

package EDU.bmrb.sansj;
import EDU.bmrb.sansj.starlib.*;
/**
 * Test program for SimpleStarParser.
 * Command-line argument: input file name (if omitted, parse stdin).
 * @author  dmaziuk
 * @version 1
 */
public class TestStarParser implements ErrorHandler, StarContentHandler {
//*******************************************************************************
    /** Creates new TestStarParser */
    public TestStarParser() {
    } //*************************************************************************
    /** Main method
    * @param args the command line arguments
    */
    public static void main (String args[]) {
        try {
            java.io.InputStream in;
            if( args.length < 1 ) in = System.in;
            else in = new java.io.FileInputStream( args[0] );
            java.io.BufferedReader reader = new java.io.BufferedReader( 
            new java.io.InputStreamReader( in ) );
            STARLexer lex = new STARLexer( reader );
            TestStarParser t = new TestStarParser();
            SimpleStarParser p = new SimpleStarParser( lex, t, t );
            p.parse();
        }
        catch( Exception e ) { e.printStackTrace(); }
    } //*************************************************************************
    /** Called when parser encounters an error.
     * @param line line number
     * @param col column number
     * @param msg error message
     */
    public void error(int line, int col, String msg) {
        System.err.println( "Parse error in line " + line + ", column " + col + ": " + msg );
    } //*************************************************************************
    /** Called when parser encounters a possible error
     * @param line line number
     * @param col column number
     * @param msg error message
     * @return true signals parser to stop parsing
     */
    public boolean warning(int line, int col, String msg) {
        System.err.println( "Parse warning in line " + line + ", column " + col + ": " + msg );
        return false;
    } //*************************************************************************
    /** Called when parser encounters data_ block.
     * @param node StarNode
     * @return true to stop parsing
     */
    public boolean startData(StarNode node) {
        System.out.println( "Start data block " + node + " in line " + node.getLineNum() + ", column " + node.getColNum() );
        if( node.getPreComment() != null ) System.out.println( node.getPreComment() );
        return false;
    } //*************************************************************************
    /** Called on EOF.
     * @param node StarNode
     */
    public void endData(StarNode node) {
        System.out.println( "End data block " + node + " in line " + node.getLineNum() + ", column " + node.getColNum() );
        if( node.getPreComment() != null ) System.out.println( node.getPreComment() );
    } //*************************************************************************
    /** Called on start of saveframe (parser encounters save_<name>).
     * @param node StarNode
     * @return true to stop parsing
     */
    public boolean startSaveFrame(StarNode node) {
        System.out.println( "Start saveframe " + node + " in line " + node.getLineNum() + ", column " + node.getColNum() );
        if( node.getPreComment() != null ) System.out.println( node.getPreComment() );
        return false;
    } //*************************************************************************
    /** Called on end of saveframe (parser encounters save_).
     * @param node StarNode
     * @return true to stop parsing
     */
    public boolean endSaveFrame(StarNode node) {
        System.out.println( "End saveframe " + node + " in line " + node.getLineNum() + ", column " + node.getColNum() );
        if( node.getPreComment() != null ) System.out.println( node.getPreComment() );
        return false;
    } //*************************************************************************
    /** Called on start of loop (parser encounters loop_).
     * @param node StarNode
     * @return true to stop parsing
     */
    public boolean startLoop(StarNode node) {
        System.out.println( "Start loop " + node + " in line " + node.getLineNum() + ", column " + node.getColNum() );
        if( node.getPreComment() != null ) System.out.println( node.getPreComment() );
        return false;
    } //*************************************************************************
    /** Called on end of loop (parser encounters stop_).
     * @param node StarNode
     * @return true to stop parsing
     */
    public boolean endLoop(StarNode node) {
        System.out.println( "End loop " + node + " in line " + node.getLineNum() + ", column " + node.getColNum() );
        if( node.getPreComment() != null ) System.out.println( node.getPreComment() );
        return false;
    } //*************************************************************************
    /** Called when parser encounters a tag.
     * @param node DataNameNode
     * @return true to stop parsing
     */
    public boolean tag(DataNameNode node) {
        System.out.println( "Tag " + node + " in line " + node.getLineNum() + ", column " + node.getColNum() );
        if( node.getPreComment() != null ) System.out.println( node.getPreComment() );
        return false;
    } //*************************************************************************
    /** Called when parser encounters a value.
     * @param node DataValueNode
     * @return true to stop parsing
     */
    public boolean data(DataValueNode node) {
        System.out.println( "Value " + node + " in line " + node.getLineNum() + ", column " + node.getColNum() );
        if( node.getPreComment() != null ) System.out.println( node.getPreComment() );
        return false;
    } //*************************************************************************
}
