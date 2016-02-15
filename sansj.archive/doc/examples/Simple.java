/*
 * Simple.java
 *
 * Created on September 13, 2002, 5:02 PM
 *
 * This software is copyright (c) 2002 Board of Regents, University of Wisconsin.
 * All Rights Reserved.
 *
 * FILE:        $Source: /bmrb/cvs_archive/cvs/sansj/doc/examples/Simple.java,v $
 * 
 * AUTHOR:      $Author: dmaziuk $
 * DATE:        $Date: 2002/10/02 00:47:16 $
 * 
 * UPDATE HISTORY:
 * ---------------
 * $Log: Simple.java,v $
 * Revision 1.3  2002/10/02 00:47:16  dmaziuk
 * changes to interface
 *
 * Revision 1.2  2002/09/18 23:16:17  dmaziuk
 * added 2 more test programs
 *
 * Revision 1.1  2002/09/17 21:27:43  dmaziuk
 * *** empty log message ***
 *
 *
 */

import java.io.*;
import EDU.bmrb.sansj.*;
/**
 * Simple example of SANS usage.
 * @author  dmaziuk
 * @version 1
 */
public class Simple implements ContentHandler, ErrorHandler {
    /** parser object */
    private SansParser fParser = null;
//*******************************************************************************
    /** Creates new Simple.
     * @param in input stream.
     */
    public Simple( BufferedReader in ) {
        fParser = new SansParser( in );
        fParser.setContentHandler( this );
        fParser.setErrorHandler( this );
    } //*************************************************************************
    /**
    * @param args the command line arguments
    */
    public static void main( String args[] ) {
        if( args.length < 1 ) {
            System.err.println( "Usage: java Simple filename" );
            return;
        }
        try {
            BufferedReader in = new BufferedReader( new InputStreamReader(
            new FileInputStream( args[0] ) ) );
            Simple s = new Simple( in ); 
            s.parse(); 
        }
        catch( Exception e ) { 
            e.printStackTrace(); 
        }
    } //*************************************************************************
    /** Parses input file.
     */
    public void parse() {
        try {
            fParser.parse( true );
        }
        catch( Exception e ) {
            System.err.println( e.getMessage() );
            e.printStackTrace();
        }
    } //*************************************************************************
    /**
     * Called when parser encounters <STRONG>global_</STRONG>.
     * @param line line number.
     */
    public boolean startGlobal( int line ) {
	return false;
    } //*************************************************************************
    /**
     * Called when parser encounters <STRONG>data_<EM>name</EM></STRONG>.
     * @param name block name.
     */
    public boolean startData( int line, String name) {
        System.out.println( "Start of data block " + name );
	return false;
    } //*************************************************************************
    /** Called on EOF.  */
    public boolean endData( int line, String name) {
        System.out.println( "End of data block" );
	return false;
    } //*************************************************************************
    /** Called when parser encouters a <STRONG>save_<EM>name</EM></STRONG>.
     * @param name saveframe name.
     */
    public boolean startSaveFrame( int line, String name) {
        System.out.println( "Start of " + name + " saveframe" );
	return false;
    } //*************************************************************************
    /** Called when parser encounters a <STRONG>save_</STRONG>.
     * @param name saveframe name.
     */
    public boolean endSaveFrame( int line, String name) {
        System.out.println( "End of " + name + " saveframe" );
	return false;
    } //*************************************************************************
    /** Called when parser encounters a <STRONG>loop_</STRONG>.
     * @param level nesting level.
     */
    public boolean startLoop( int line, int level) {
        System.out.println( "Start of " + ( level > 1 ? "nested " : "" ) 
        + "loop (level " + level + ")" );
	return false;
    } //*************************************************************************
    /** Called when parser encounters a <STRONG>stop_</STRONG>.
     * @param level nesting level.
     */
    public boolean endLoop( int line, int level) {
        System.out.println( "End of " + ( level > 1 ? "nested " : "" ) 
        + "loop (level " + level + ")" );
	return false;
    } //*************************************************************************
    /**
     * Called when parser encounters a tag.
     * Tag is a non-quoted string that starts with an underscore.
     * @param name tag name.
     */
    public boolean tag( int line, String name) {
        System.out.println( "Tag: " + name );
	return false;
    } //*************************************************************************
    /** Called when parser encouters a value.
     * Value is anything that's not a keyword or a tag.
     */
    public boolean characters( int line, String data, int kind) {
        switch( kind ) {
            case SansParserConstants.DVNNON : 
                System.out.println( "Value: " );
                break;
            case SansParserConstants.DVNSINGLE : 
                System.out.println( "Single-quoted value: " );
                break;
            case SansParserConstants.DVNDOUBLE : 
                System.out.println( "Double-quoted value: " );
                break;
            case SansParserConstants.DVNSEMICOLON : 
                System.out.println( "Semicolon-quoted value: " );
                break;
            case SansParserConstants.DVNFRAMECODE : 
                System.out.println( "Framecode value: " );
                break;
        }
        System.out.println( data );
	return false;
    } //*************************************************************************
    /** Called when parser encounters a comment.
     * @param text comment text.
     */
    public boolean comment( int line, String text) { 
        System.out.println( "Comment: " + text );
	return false;
    } //*************************************************************************
    /** Called when parser encounters an error.  */
    public void error( SansException err ) {
        System.err.println( "Parse error around line " + err.getLineNumber()
        + ": " + err.getMessage() );
    } //*************************************************************************
    /** Called when parser encounters a possible error.  */
    public boolean warning( SansException warn ) {
        System.err.println( "Parse warning around line " + warn.getLineNumber()
        + ": " + warn.getMessage() );
	return false;
    } //*************************************************************************
} //*********** eof Test ********************************************************
