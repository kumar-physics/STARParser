/*
 * Combined.java
 *
 * Created on September 13, 2002, 5:02 PM
 *
 * This software is copyright (c) 2002 Board of Regents, University of Wisconsin.
 * All Rights Reserved.
 *
 * FILE:        $Source: /bmrb/cvs_archive/cvs/sansj/doc/examples/Combined.java,v $
 * 
 * AUTHOR:      $Author: dmaziuk $
 * DATE:        $Date: 2002/10/02 00:47:16 $
 * 
 * UPDATE HISTORY:
 * ---------------
 * $Log: Combined.java,v $
 * Revision 1.4  2002/10/02 00:47:16  dmaziuk
 * changes to interface
 *
 * Revision 1.3  2002/09/18 23:16:16  dmaziuk
 * added 2 more test programs
 *
 * Revision 1.2  2002/09/17 23:15:25  dmaziuk
 * beta release version
 *
 * Revision 1.1  2002/09/17 21:27:43  dmaziuk
 * *** empty log message ***
 *
 *
 */

import java.io.*;
import java.util.*;
import EDU.bmrb.sansj.*;
import EDU.bmrb.starlibj.*;
/**
 * Example of using SANS and Starlib together.
 * SANS is used to parse the file until we find entry_citation saveframe.
 * The saveframe is then parsed into a Starlib SaveFrameNode via 
 * StringWriter/StringReader pair.
 * 
 * NOTE that you may need to adjust StringWriter's buffer size.
 * 
 * @author  dmaziuk
 * @version 1
 */
public class Combined implements ContentHandler, ErrorHandler {
    /** entry citation saveframe */
    private String SFNAME = "entry_citation";
    /** SANS parser */
    private SansParser fSansParser = null;
//*******************************************************************************
    /** Creates new Combined.
     * @param in input stream.
     */
    public Combined( BufferedReader in ) {
        fSansParser = new SansParser( in );
        fSansParser.setContentHandler( this );
        fSansParser.setErrorHandler( this );
    } //*************************************************************************
    /**
    * @param args the command line arguments
    */
    public static void main( String args[] ) {
	String s = null;
        if( args.length < 1 ) {
            System.err.println( "Usage: java Combined filename" );
            return;
        }
        try {
            BufferedReader in = new BufferedReader( new InputStreamReader(
            new FileInputStream( args[0] ) ) );
            Combined c = new Combined( in ); 
            c.parse();
        }
        catch( Exception e ) { 
            e.printStackTrace(); 
        }
    } //*************************************************************************
    /** Wrapper around SansParser.backup() */
    public void backup( int amount ) {
	fSansParser.backup( amount );
    } //*************************************************************************
    /** Wrapper around SansParser.write() */
    public void write( Writer out, int stopToken ) {
	try { fSansParser.write( out, stopToken ); }
	catch( Exception e ) {
            e.printStackTrace(); 
        }
    } //*************************************************************************
    /** Parses input file.
     */
    public void parse() {
        try {
// read input until we hit entry_citation saveframe (see startSaveFrame() callback)
            fSansParser.parse( true );
// backup save_ + SFNAME.length() characters
            backup( 20 );
// write Saveframe to a StringWriter
	    StringWriter sw = new StringWriter(); 
	    write( sw, SansParserConstants.SAVEEND );
// create a StringReader
	    StringReader sr = new StringReader( sw.toString() );
//  and a StarParser
	    StarParser parser = new StarParser( sr );
// parse in the saveframe
	    parser.SaveFrameNodeParse( parser );
// print it out
	    StarUnparser unparser = new StarUnparser( System.out );
	    unparser.writeOut( (SaveFrameNode)parser.endResult(), 1 );
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
     * @param line line number.
     * @param name block name.
     */
    public boolean startData( int line, String name) {
	return false;
    } //*************************************************************************
    /** Called on EOF.  
     * @param line line number.
     * @param name block name.
     */
    public boolean endData( int line, String name) {
	return false;
    } //*************************************************************************
    /** Called when parser encouters a <STRONG>save_<EM>name</EM></STRONG>.
     * @param line line number.
     * @param name saveframe name.
     */
    public boolean startSaveFrame( int line, String name) {
        if( name.equals( SFNAME ) ) return true; // stop parsing, we're done
	return false;
    } //*************************************************************************
    /** Called when parser encounters a <STRONG>save_</STRONG>.
     * @param line line number.
     * @param name saveframe name.
     */
    public boolean endSaveFrame( int line, String name) {
	return false;
    } //*************************************************************************
    /** Called when parser encounters a <STRONG>loop_</STRONG>.
     * @param line line number.
     * @param level nesting level.
     */
    public boolean startLoop( int line, int level) {
	return false;
    } //*************************************************************************
    /** Called when parser encounters a <STRONG>stop_</STRONG>.
     * @param line line number.
     * @param level nesting level.
     */
    public boolean endLoop( int line, int level) {
	return false;
    } //*************************************************************************
    /**
     * Called when parser encounters a tag.
     * Tag is a non-quoted string that starts with an underscore.
     * @param line line number.
     * @param name tag name.
     */
    public boolean tag( int line, String name) {
	return false;
    } //*************************************************************************
    /** Called when parser encouters a value.
     * Value is anything that's not a keyword or a tag.
     * @param line line number.
     * @param data value as string.
     * @param kind delimiter type.
     */
    public boolean characters( int line, String data, int kind) {
	return false;
    } //*************************************************************************
    /** Called when parser encounters a comment.
     * @param line line number.
     * @param text comment text.
     */
    public boolean comment( int line, String text) { 
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
