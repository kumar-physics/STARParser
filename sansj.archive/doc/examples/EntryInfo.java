/*
 * EntryInfo.java
 *
 * Created on September 13, 2002, 5:02 PM
 *
 * This software is copyright (c) 2002 Board of Regents, University of Wisconsin.
 * All Rights Reserved.
 *
 * FILE:        $Source: /bmrb/cvs_archive/cvs/sansj/doc/examples/EntryInfo.java,v $
 * 
 * AUTHOR:      $Author: dmaziuk $
 * DATE:        $Date: 2002/10/02 00:47:16 $
 * 
 * UPDATE HISTORY:
 * ---------------
 * $Log: EntryInfo.java,v $
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
/**
 * Example of SANS usage.
 * Extracts BMRB accession number, entry title, and author names. Shows that
 * dealing with loops is a pain.
 * @author  dmaziuk
 * @version 1
 */
public class EntryInfo implements ContentHandler, ErrorHandler {
    /** entry information saveframe */
    private String SFNAME = "entry_information";
    /** flag */
    private boolean fInSF = false;
    /** entry title tag, */
    private String TITLE = "_Entry_title";
    /** flag, */
    private boolean fGetTitle = false;
    /** and value */
    private String fTitle = null;
    /** accession number tag, */
    private String ACCNO = "_BMRB_accession_number";
    /** flag, */
    private boolean fGetAccno = false;
    /** and value */
    private String fAccno = null;
    /** true if we're inside a loop */
    private boolean fInLoop = false;
    /** true if we're inside authors loop */
    private boolean fInAuthLoop = false;    
    /** author surname tag */
    private String ASURNAME = "_Author_family_name";
    /** and value */
    private String fAuthSurname = null;
    /** author 1st name tag */
    private String A1STNAME = "_Author_given_name";
    /** and value */
    private String fAuth1stname = null;
    /** loop indices */
    private int[] fAuthIndex = null;
    /** surname */
    private int SURNAME = 0;
    /** 1st name */
    private int NAME = 1;
    /** and counter */
    private int fIndex = -1;
    /** number of loop columns */
    private int fColCount = -1;
    /** vector of author names */
    private Vector fNames = null;
    /** parser object */
    private SansParser fParser = null;
//*******************************************************************************
    /** Creates new EntryInfo.
     * @param in input stream.
     */
    public EntryInfo( BufferedReader in ) {
        fParser = new SansParser( in );
        fParser.setContentHandler( this );
        fParser.setErrorHandler( this );
        fAuthIndex = new int[2];
        fNames = new Vector();
    } //*************************************************************************
    /**
    * @param args the command line arguments
    */
    public static void main( String args[] ) {
        if( args.length < 1 ) {
            System.err.println( "Usage: java EntryInfo filename" );
            return;
        }
        try {
            BufferedReader in = new BufferedReader( new InputStreamReader(
            new FileInputStream( args[0] ) ) );
            EntryInfo ei = new EntryInfo( in ); 
            ei.parse(); 
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
            System.out.println( "    BMBR entry " + fAccno + ":" );
            System.out.println( fTitle );
            System.out.println( "    by" );
            for( int i = 0; i < fNames.size(); i++ )
                System.out.println( (String)fNames.elementAt( i ) );
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
    /** Called on EOF.  */
    public boolean endData( int line, String name) {
	return false;
    } //*************************************************************************
    /** Called when parser encouters a <STRONG>save_<EM>name</EM></STRONG>.
     * @param line line number.
     * @param name saveframe name.
     */
    public boolean startSaveFrame( int line, String name) {
        if( name.equals( SFNAME ) ) fInSF = true;
	return false;
    } //*************************************************************************
    /** Called when parser encounters a <STRONG>save_</STRONG>.
     * @param line line number.
     * @param name saveframe name.
     */
    public boolean endSaveFrame( int line, String name) {
        if( fInSF ) return true; // stop parsing, we're done
	return false;
    } //*************************************************************************
    /** Called when parser encounters a <STRONG>loop_</STRONG>.
     * @param line line number.
     * @param level nesting level.
     */
    public boolean startLoop( int line, int level) {
        fInLoop = true;
	return false;
    } //*************************************************************************
    /** Called when parser encounters a <STRONG>stop_</STRONG>.
     * @param line line number.
     * @param level nesting level.
     */
    public boolean endLoop( int line, int level) {
        fIndex = -1;
        fInAuthLoop = false;
        fInLoop = false;
	return false;
    } //*************************************************************************
    /**
     * Called when parser encounters a tag.
     * Tag is a non-quoted string that starts with an underscore.
     * @param line line number.
     * @param name tag name.
     */
    public boolean tag( int line, String name) {
        if( fInSF ) {
            if( name.equals( TITLE ) )
// tell characters() to read  citation title
                fGetTitle = true; 
            else if( name.equals( ACCNO ) )
// tell characters() to read  accession number
                fGetAccno = true; 
            else if( fInLoop ) {
// keep track of column number
                fIndex++;
                if( name.equals( ASURNAME ) ) {
// author's surname
                    fInAuthLoop = true;
                    fAuthIndex[SURNAME] = fIndex;
                }
                if( name.equals( A1STNAME ) ) {
// and first name
                    fInAuthLoop = true;
                    fAuthIndex[NAME] = fIndex;
                }
            }
        }
	return false;
    } //*************************************************************************
    /** Called when parser encouters a value.
     * Value is anything that's not a keyword or a tag.
     * @param line line number.
     */
    public boolean characters( int line, String data, int kind) {
        if( fGetTitle ) { // save entry title
            fTitle = data;
            fGetTitle = false;
        }
        if( fGetAccno ) { // save accession number
            fAccno = data;
            fGetAccno = false;
        }
        if( fInAuthLoop ) {
// once we read last loop tag, fIndex contains number of loop columns - 1.
// first, save it and reset the counter.
            if( fColCount < 0 ) {
                fColCount = fIndex;
                fIndex = -1;
            }
            fIndex++;
            if( fIndex == fAuthIndex[SURNAME] ) // save author's surname
                fAuthSurname = data;
            else if( fIndex == fAuthIndex[NAME] ) // save author's given name
                fAuth1stname = data;
            else if( fIndex == fColCount ) { // add names to vector, reset counter
                fNames.addElement( fAuth1stname + " " + fAuthSurname );
                fIndex = -1;
            }
        }
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
