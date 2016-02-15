/*
 * Time.java
 *
 * Created on September 18, 2002, 3:24 PM
 *
 * This software is copyright (c) 2002 Board of Regents, University of Wisconsin.
 * All Rights Reserved.
 *
 * FILE:        $Source: /bmrb/cvs_archive/cvs/sansj/doc/examples/Time.java,v $
 * 
 * AUTHOR:      $Author: dmaziuk $
 * DATE:        $Date: 2002/10/02 00:47:17 $
 * 
 * UPDATE HISTORY:
 * ---------------
 * $Log: Time.java,v $
 * Revision 1.2  2002/10/02 00:47:17  dmaziuk
 * changes to interface
 *
 * Revision 1.1  2002/09/18 23:16:17  dmaziuk
 * added 2 more test programs
 *
 */

import java.io.*;
import java.util.*;
import EDU.bmrb.sansj.*;
import EDU.bmrb.starlibj.*;
/**
 * Time SANS and Starlib parsing.
 * 
 * @author  dmaziuk
 * @version 1
 */
public class Time implements ContentHandler, ErrorHandler {
//*******************************************************************************
    /** Creates new Time. */
    public Time() {
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
            Time t = new Time(); 
            t.parse( args[0] );
        }
        catch( Exception e ) { 
            e.printStackTrace(); 
        }
    } //*************************************************************************
    /** Parses input file.
     */
    public void parse( String filename ) {
	long start;
        try {
            BufferedReader in = new BufferedReader( new InputStreamReader(
            new FileInputStream( filename ) ) );
	    start = System.currentTimeMillis();
            SansParser sans = new SansParser( in );
	    sans.setContentHandler( this );
	    sans.setErrorHandler( this );
	    sans.parse( true );
	    System.out.println( "SANS: " + (System.currentTimeMillis() - start) );
	    in = null;
            in = new BufferedReader( new InputStreamReader(
            new FileInputStream( filename ) ) );
	    start = System.currentTimeMillis();
	    StarParser star = new StarParser( in );
	    star.StarFileNodeParse( star );
	    System.out.println( "Star: " + (System.currentTimeMillis() - start) );
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
	return false;
    } //*************************************************************************
    /** Called on EOF.  */
    public boolean endData( int line, String name) {
	return false;
    } //*************************************************************************
    /** Called when parser encouters a <STRONG>save_<EM>name</EM></STRONG>.
     * @param name saveframe name.
     */
    public boolean startSaveFrame( int line, String name) {
	return false;
    } //*************************************************************************
    /** Called when parser encounters a <STRONG>save_</STRONG>.
     * @param name saveframe name.
     */
    public boolean endSaveFrame( int line, String name) {
	return false;
    } //*************************************************************************
    /** Called when parser encounters a <STRONG>loop_</STRONG>.
     * @param level nesting level.
     */
    public boolean startLoop( int line, int level) {
	return false;
    } //*************************************************************************
    /** Called when parser encounters a <STRONG>stop_</STRONG>.
     * @param level nesting level.
     */
    public boolean endLoop( int line, int level) {
	return false;
    } //*************************************************************************
    /**
     * Called when parser encounters a tag.
     * Tag is a non-quoted string that starts with an underscore.
     * @param name tag name.
     */
    public boolean tag( int line, String name) {
	return false;
    } //*************************************************************************
    /** Called when parser encouters a value.
     * Value is anything that's not a keyword or a tag.
     */
    public boolean characters( int line, String data, int kind) {
	return false;
    } //*************************************************************************
    /** Called when parser encounters a comment.
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
