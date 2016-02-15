/*
 * Copyright (c) 2005 Board of Regents, University of Wisconsin.
 *  All Rights Reserved.
 */
package edu.bmrb.sans;

/**
 * CIF (pdbx) parser.
 * @author D. Maziuk
 */

/*
 * Created by IntelliJ IDEA.
 * User: dmaziuk
 * Date: Dec 2, 2005
 * Time: 1:28:02 PM
 *
 * $Source: /cvs_archive/cvs/starlibs5/sans/src/edu/bmrb/sans/CifParser.java,v $
 * $Author: dmaziuk $
 * Initial import: $Date: 2006/02/11 02:28:56 $
 * Update history:
 * ---------------
 * $Log: CifParser.java,v $
 * Revision 1.6  2006/02/11 02:28:56  dmaziuk
 * updated
 *
 * Revision 1.5  2006/02/03 23:45:46  dmaziuk
 * finished stardictionary module
 *
 * Revision 1.4  2006/01/30 22:56:40  dmaziuk
 * *** empty log message ***
 *
 * Revision 1.3  2005/12/19 23:47:38  dmaziuk
 * refactoring, finished table gen. converter
 *
 * Revision 1.2  2005/12/07 00:26:49  dmaziuk
 * Working
 *
 * Revision 1.1.1.1  2005/12/05 20:08:07  dmaziuk
 * initial import
 * */

public class CifParser {
    private static final boolean DEBUG = false;
    /** lexer error */
    public static final String ERR_LEXER = "Scanner error: ";
    /** invalid token */
    public static final String ERR_TOKEN = "Invalid token: ";
    /** tag expected */
    public static final String ERR_TAG = "Tag expected: ";
    /** loop with no values */
    public static final String ERR_LOOPVALS = "Loop with no values";
    /** loop with no tags */
    public static final String ERR_LOOPTAGS = "Loop with no tags";
    /** scanner warning. */
    public static final String WARN_LEXER = "Lexer warning: ";
    /** scanner */
    private STARLexer fLex = null;
    /** error handler */
    private ErrorHandler fEh = null;
    /** content handler */
    private ContentHandler fCh = null;
    /** data block id */
    private String fDataId = null;
//*******************************************************************************
    /** Creates new CifParser.
     * This constructor is used by main() for testing.
     * @param lex lexer
     */
    private CifParser( STARLexer lex ) {
        fLex = lex;
    } //*************************************************************************
    /** Creates new CifParser.
     * @param lex lexer
     * @param ch content handler
     * @param eh error handler
     */
    public CifParser( STARLexer lex, ContentHandler ch, ErrorHandler eh ) {
        fLex = lex;
        fCh = ch;
        fEh = eh;
    } //*************************************************************************
    /** Returns content handler
     * @return content handler
     */
    public edu.bmrb.sans.ContentHandler getContentHandler() {
        return fCh;
    } //*************************************************************************
    /** Changes content handler
     * @param ch new content handler
     */
    public void setContentHandler( ContentHandler ch ) {
        fCh = ch;
    } //*************************************************************************
    /** Returns error handler
     * @return error handler
     */
    public edu.bmrb.sans.ErrorHandler getErrorHandler() {
        return fEh;
    } //*************************************************************************
    /** Changes error handler
     * @param eh new error handler
     */
    public void setErrorHandler( edu.bmrb.sans.ErrorHandler eh ) {
        fEh = eh;
    } //*************************************************************************
    /** Returns scanner
     * @return scanner
     */
    public edu.bmrb.sans.STARLexer getScanner() {
        return fLex;
    } //*************************************************************************
    /** Changes scanner
     * @param lex new scanner
     */
    public void setScanner( edu.bmrb.sans.STARLexer lex ) {
        fLex = lex;
    } //*************************************************************************
    /** Parses input file.
     * @throws NullPointerException if scanner or hadnler(s) is null.
     */
    public void parse() {
        if( (fLex == null) || (fCh == null) || (fEh == null) )
            throw new NullPointerException( "Parser not initialized" );
        STARLexer.Types tok;
        try {
            do {
                tok = fLex.yylex();
                switch( tok ) {
                    case EOF :
                        fCh.endData( fLex.getLine(), fDataId );
                        return;
                    case ERROR :
                        fEh.fatalError( fLex.getLine(), fLex.getColumn(), ERR_LEXER
                        + fLex.yytext() );
                        return;
                    case WARNING :
                        if( fEh.warning( fLex.getLine(), fLex.getColumn(), WARN_LEXER + fLex.yytext() ) )
                            return;
			            break;
                    case COMMENT :
                        if( fCh.comment( fLex.getLine(), fLex.yytext() ) ) return;
                        break;
                    case DATASTART :
                        fDataId = fLex.yytext().substring( 5 ); // strip "data_"
                        if( fCh.startData( fLex.getLine(), fDataId ) ) return;
                        if( parseDataBlock() ) return;
                        break;
                    default :
                        if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_TOKEN
                        + fLex.yytext() ) )
                            return;
                }
            } while( tok != STARLexer.Types.EOF );
        }
        catch( Exception e ) {
            fEh.fatalError( -1, -1, e.getMessage() );
            System.err.println( e );
            e.printStackTrace();
        }
    } //*************************************************************************
    /** Parses data block.
     * @return true to stop parsing
     */
    public boolean parseDataBlock() {
        STARLexer.Types tok;
        int tagline = -1;
        String tag = null;
        DataItemNode item;
        try {
            do {
                item = null;
                tok = fLex.yylex();
                switch( tok ) {
                    case EOF :
                        fCh.endData( fLex.getLine(), fDataId );
                        return true;
                    case ERROR :
                        fEh.fatalError( fLex.getLine(), fLex.getColumn(), ERR_LEXER
                        + fLex.yytext() );
                        return true;
                    case WARNING :
                        if( fEh.warning( fLex.getLine(), fLex.getColumn(), WARN_LEXER + fLex.yytext() ) )
                            return true;
			            break;
                    case COMMENT :
                        if( fCh.comment( fLex.getLine(), fLex.yytext() ) ) return true;
                        break;
//
                    case DATASTART : 
                        fCh.endData( fLex.getLine(), fDataId ); // fake end of block
                        fDataId = fLex.yytext().substring( 5 ); // strip "data_"
                        if( fCh.startData( fLex.getLine(), fDataId ) ) return true;
                        break;
//
                    case LOOPSTART :
                        if( fCh.startLoop( fLex.getLine() ) ) return true;
                        if( parseLoop() ) return true;
                        break;
                    case TAGNAME :
                        tag = fLex.yytext();
                        tagline = fLex.getLine();
                        break;
                    case DVNDOUBLE :
                    case DVNFRAMECODE :
                    case DVNNON :
                    case DVNSEMICOLON :
                    case DVNSINGLE :
                        if( tag == null ) {
                            if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_TAG + fLex.yytext() ) )
                                return true;
                        }
                        item = new DataItemNode( tagline, tag );
                        item.setDelimType( tok );
                        item.setLoopFlag( false );
                        item.setValueLine( fLex.getLine() );
                        if( tok == STARLexer.Types.DVNSEMICOLON ) { // strip leading "\n"
                            if( fLex.getText().indexOf( System.getProperty( "line.separator" ) ) == 0 )
                                item.setValue( fLex.getText().substring( System.getProperty( "line.separator").length() ) );
                            else item.setValue( fLex.getText() );
                        }
                        else if( tok == STARLexer.Types.DVNFRAMECODE ) // strip '$'
                            item.setValue( fLex.yytext().substring( 1 ) );
                        else if( tok == STARLexer.Types.DVNNON ) item.setValue( fLex.yytext() );
                        else item.setValue( fLex.getText() );
if( DEBUG ) System.err.printf(  "*Value: %s\n", fLex.yytext() );
                        if( fCh.data( item ) ) return true;
                        tag = null;
                        break;
                    default :
                        if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_TOKEN
                        + fLex.yytext() ) )
                            return true;
                }
            } while( tok != STARLexer.Types.EOF );
            return false;
        }
        catch( Exception e ) {
            fEh.fatalError( -1, -1, e.getMessage() );
            System.err.println( e );
            e.printStackTrace();
            return true;
        }
    } //*************************************************************************
    /** Parses loop.
     * @return true to stop parsing
     */
    public boolean parseLoop() {
        STARLexer.Types tok;
        int col = 0;
        IntStringPair tag = null;
        DataItemNode item;
        boolean parsing_values = false;
        java.util.ArrayList<IntStringPair> tags = new java.util.ArrayList<IntStringPair>();
        try {
            do {
                item = null;
                tok = fLex.yylex();
                switch( tok ) {
                    case EOF :
                        fCh.endData( fLex.getLine(), fDataId );
                        return true;
                    case ERROR :
                        fEh.fatalError( fLex.getLine(), fLex.getColumn(), ERR_LEXER
                        + fLex.yytext() );
                        return true;
                    case WARNING :
                        if( fEh.warning( fLex.getLine(), fLex.getColumn(), WARN_LEXER + fLex.yytext() ) )
                            return true;
			            break;
                    case COMMENT :
                        if( fCh.comment( fLex.getLine(), fLex.yytext() ) ) return true;
                        break;
//
                    case DATASTART : // fake end of loop
                        if( ! parsing_values ) {
                            if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_LOOPVALS ) )
                                return true;
                        }
                        if( fCh.endLoop( fLex.getLine() ) ) return true;
                        fCh.endData( fLex.getLine(), fDataId ); // fake end of block
                        fDataId = fLex.yytext().substring( 5 ); // strip "data_"
                        if( fCh.startData( fLex.getLine(), fDataId ) ) return true;
                        return false; // return to parseDataBlock()
//
                    case LOOPSTART : // fake end of loop
                        if( ! parsing_values ) {
                            if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_LOOPVALS ) )
                                return true;
                        }
                        if( fCh.endLoop( fLex.getLine() ) ) return true;
                        fLex.pushBack( 6 ); // "loop_"
                        return false;
                    case TAGNAME :
                        if( parsing_values ) { // fake end of loop
                            if( fCh.endLoop( fLex.getLine() ) ) return true;
                            fLex.pushBack( fLex.yytext().length() ); // tag
                            return false;
                        }
// reading loop header
                        tag = new IntStringPair( fLex.getLine(), fLex.yytext() );
                        tags.add( tag );
                        break;
                    case DVNDOUBLE :
                    case DVNFRAMECODE :
                    case DVNNON :
                    case DVNSEMICOLON :
                    case DVNSINGLE :
                        if( tags.size() < 1 ) {
                            if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_LOOPTAGS ) ) {
                                tags.clear();
                                tags = null;
                                return true;
                            }
                        }
                        parsing_values = true;
                        tag = null;
                        item = new DataItemNode( tags.get( col ) );
                        item.setLoopFlag( true );
                        item.setDelimType( tok );
                        item.setValueLine( fLex.getLine() );
                        if( tok == STARLexer.Types.DVNSEMICOLON ) { // strip leading "\n"
                            if( fLex.getText().indexOf( System.getProperty( "line.separator" ) ) == 0 )
                                item.setValue( fLex.getText().substring( System.getProperty( "line.separator").length() ) );
                            else item.setValue( fLex.getText() );
                        }
                        else if( tok == STARLexer.Types.DVNFRAMECODE ) // strip '$'
                            item.setValue( fLex.yytext().substring( 1 ) );
                        else if( tok == STARLexer.Types.DVNNON ) item.setValue( fLex.yytext() );
                        else item.setValue( fLex.getText() );
                        col++;
                        if( col == tags.size() ) col = 0;
                        if( fCh.data( item ) ) return true;
                        break;
                    default :
                        if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_TOKEN
                        + fLex.yytext() ) )
                            return true;
                }
            } while( tok != STARLexer.Types.EOF );
            return false;
        }
        catch( Exception e ) {
            fEh.fatalError( -1, -1, e.getMessage() );
            System.err.println( e );
            e.printStackTrace();
            return true;
        }
    } //*************************************************************************
    /** Test parser. */
    private void test_parse() throws java.io.IOException {
        STARLexer.Types tok = fLex.yylex();
        while( tok != STARLexer.Types.EOF ) {
            System.out.printf( "%s (%d,%d): %s\n", tok, fLex.getLine(), fLex.getColumn(), fLex.getText() );
            tok = fLex.yylex();
        }
        System.out.println( "End of data (EOF)" );
    } //*************************************************************************
    /**
     * Main method.
     * @param args command-line arguments.
     */
    public static void main( String [] args ) {
        try {
            java.io.BufferedReader r = new java.io.BufferedReader( new java.io.InputStreamReader( System.in ) );
            STARLexer lex = new STARLexer( r );
            CifParser p = new CifParser( lex );
            p.test_parse();
        }
        catch( Exception e ) {
            System.err.println( e );
            e.printStackTrace();
            System.exit( 1 );
        }
    } //*************************************************************************
}
