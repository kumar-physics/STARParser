/*
 * CifParser.java
 *
 * Created on April 24, 2003, 2:13 PM
 *
 * This software is copyright (c) 2003 Board of Regents, University of Wisconsin.
 * All Rights Reserved.
 *
 * FILE:        $Source: /cvs_archive/cvs/sansj/src/EDU/bmrb/sansj/CifParser.java,v $
 * 
 * AUTHOR:      $Author: dmaziuk $
 * DATE:        $Date: 2006/04/03 22:40:40 $
 * 
 * UPDATE HISTORY:
 * ---------------
 * $Log: CifParser.java,v $
 * Revision 1.1  2006/04/03 22:40:40  dmaziuk
 * bug fix in lex specs.
 *
 * Revision 1.2  2003/05/16 00:57:51  dmaziuk
 * Fixed a bug in end-of-loop processing (CIFs don't have stop_)
 *
 * Revision 1.1  2003/04/24 20:25:41  dmaziuk
 * Added CIF parser, fixed memory leak
 *
 */

package EDU.bmrb.sansj;

/**
 * CIF parser.
 * <P>
 * CIF files do not contain saveframes, and the have no loop terminators (stop_).
 * Other than that, they are no different from NMR-STAR files.
 * @author  dmaziuk
 * @version 1
 */
public class CifParser {
    /** scanner error */
    public static final String ERR_LEXER = "Lexer error: ";
    /** invalid token */
    public static final String ERR_TOKEN = "Invalid token: ";
    /** loop with no tags */
    public static final String ERR_LOOPNOTAGS = "Loop with no tags";
    /** loop with no value */
    public static final String ERR_LOOPNOVALS = "Loop with no values";
    /** parse warning */
    public static final String WARN_KEYWORD = "Keyword in value: ";
    /* content handler object */
    private ContentHandler fCh = null;
    /* error handler object */
    private ErrorHandler fEh = null;
    /* scanner */
    STARLexer fLex = null;
    /* data block id */
    String fDataId = null;
//*******************************************************************************
    /** Creates new CifParser.
     * @param lex scanner
     */
    public CifParser( STARLexer lex ) {
        fLex = lex;
    } //*************************************************************************
    /** Creates new CifParser.
     * @param lex scanner
     * @param ch content handler
     * @param eh error handler
     */
    public CifParser( STARLexer lex, ContentHandler ch, ErrorHandler eh ) {
        fLex = lex;
        fCh = ch;
        fEh = eh;
    } //*************************************************************************
    /** Returns content handler.
     * @return content handler object
     */
    public ContentHandler getContentHandler() {
        return fCh;
    } //*************************************************************************
    /** Sets content handler.
     * @param ch content handler object
     */
    public void setContentHandler( ContentHandler ch ) {
        fCh = ch;
    } //*************************************************************************
    /** Returns error handler.
     * @return error handler object
     */
    public ErrorHandler getErrorHandler() {
        return fEh;
    } //*************************************************************************
    /** Sets error handler.
     * @param eh error handler object
     */
    public void setErrorHandler( ErrorHandler eh ) {
        fEh = eh;
    } //*************************************************************************
    /** Returns scanner object.
     * @return scanner
     */
    public STARLexer getScanner() {
        return fLex;
    } //*************************************************************************
    /** Sets scanner object.
     * @param lex scanner
     */
    public void setScanner( STARLexer lex ) {
        fLex = lex;
    } //*************************************************************************
    /** Parses input file */
    public void parse() {
        int tok;
        try {
            do {
                tok = fLex.yylex();
                switch( tok ) {
                    case STARLexer.ERROR :
                        fEh.error( fLex.getLine(), fLex.getColumn(), ERR_LEXER
                        + fLex.yytext() );
                        return;
                    case STARLexer.COMMENT :
                        if( fCh.comment( fLex.getLine(), fLex.yytext() ) ) return;
                        break;
                    case STARLexer.DATASTART : // parse data block
                        fDataId = fLex.yytext().substring( 5 ); // strip data_
                        if( fCh.startData( fLex.getLine(), fDataId ) ) return;
                        if( parseDataBlock() ) return;
                        break;
                    case STARLexer.YYEOF : // fake end of data block
                        fCh.endData( fLex.getLine(), fDataId );
                        return;
                    default : // invalid token
                        fEh.error( fLex.getLine(), fLex.getColumn(), ERR_TOKEN
                        + fLex.yytext() );
                        return;
                }
            } while( tok != STARLexer.YYEOF );
        }
        catch( Exception e ) { 
            System.err.println( e.getMessage() );
            e.printStackTrace(); 
        }
    } //*************************************************************************
    /** Parses data block */
    public boolean parseDataBlock() {
        int tok;
        int tagline = -1;
        String tag = null;
        IntStringPair pair = null;
        DataItemNode item = null;
        try {
            do {
                item = null;
                tok = fLex.yylex();
                switch( tok ) {
                    case STARLexer.ERROR :
                        fEh.error( fLex.getLine(), fLex.getColumn(), ERR_LEXER
                        + fLex.yytext() );
                        return true;
                    case STARLexer.COMMENT :
                        if( fCh.comment( fLex.getLine(), fLex.yytext() ) ) 
                            return true;
                        break;
                    case STARLexer.YYEOF : // fake end of data block
                        fCh.endData( fLex.getLine(), fDataId );
                        return true;
                    case STARLexer.LOOPSTART :
                        if( fCh.startLoop( fLex.getLine() ) ) return true;
			parseLoop();
//                        pair = parseLoop();
//                        if( pair == null ) return true;
//                        else if( pair.getText().equals( "NEWLOOP" ) ) pair = parseLoop();
//                        else {
//                            tag = pair.getText();
//                            tagline = pair.getLineNumber();
//                            pair = null;
//                        }
                        break;
                    case STARLexer.TAGNAME : // save tag
                        tag = fLex.yytext();
                        tagline = fLex.getLine();
                        break;
                    case STARLexer.DVNSINGLE :
                    case STARLexer.DVNDOUBLE :
                    case STARLexer.DVNSEMICOLON :
                    case STARLexer.DVNFRAMECODE :
                    case STARLexer.DVNNON :
                        item = new DataItemNode( tagline, tag );
                        item.setDelimType( tok );
                        item.setValueLine( fLex.getLine() );
                        if( tok == STARLexer.DVNSEMICOLON ) { // strip \n
                            if( fLex.getText().substring( 0, 1 ).equals( "\n" ) )
                                item.setValue( fLex.getText().substring( 1 ) );
                            else item.setValue( fLex.getText() );
                        }
                        else if( tok == STARLexer.DVNFRAMECODE ) // strip $
                            item.setValue( fLex.yytext().substring( 1 ) );
                        else if( tok == STARLexer.DVNNON ) 
                            item.setValue( fLex.yytext() );
                        else item.setValue( fLex.getText() );
                        item.setLoopFlag( false );
                        if( fCh.data( item ) ) return true;
                        break;
                    default : // invalid token
                        fEh.error( fLex.getLine(), fLex.getColumn(), ERR_TOKEN
                        + fLex.yytext() );
                        return true;
                }
            } while( tok != STARLexer.YYEOF );
            return false;
        }
        catch( Exception e ) { 
            System.err.println( e.getMessage() );
            e.printStackTrace(); 
            return true;
        }
    } //*************************************************************************
    /** Parses a loop.
     * @return null to stop the parser, or the free tag following the loop.
     */
    public IntStringPair parseLoop() {
        int tok;
        int col = 0;
        java.util.Vector tags = new java.util.Vector();
        IntStringPair tag = null;
        DataItemNode item = null;
        boolean parsing_values = false;
        try {
            do {
                item = null;
                tok = fLex.yylex();
                switch( tok ) {
                    case STARLexer.ERROR :
                        fEh.error( fLex.getLine(), fLex.getColumn(), ERR_LEXER
                        + fLex.yytext() );
                        return null;
                    case STARLexer.WARNING :
                        if( fEh.warning( fLex.getLine(), fLex.getColumn(), WARN_KEYWORD
                        + fLex.yytext() ) ) return null;
                        break;
                    case STARLexer.COMMENT :
                        if( fCh.comment( fLex.getLine(), fLex.yytext() ) ) 
                            return null;
                        break;
                    case STARLexer.LOOPSTART :
                        if( ! parsing_values ) { // error: loop with no values
                            fEh.error( fLex.getLine(), fLex.getColumn(), ERR_LOOPNOVALS );
                            return null;
                        }
			fLex.pushBack( 5 ); // loop_ and a whitespace
			return null;
//                        if( fCh.startLoop( fLex.getLine() ) ) return null;
//                        tag = new IntStringPair( fLex.getLine(), "NEWLOOP" );
//                        return tag;
                    case STARLexer.TAGNAME : // if we're reading loop header, save tag
                        tag = new IntStringPair( fLex.getLine(), fLex.yytext() );
                        if( ! parsing_values ) tags.addElement( tag );
			else {
     			    fLex.pushBack( fLex.yytext().length() ); // loop_ and a whitespace
			    return null;
			}
//                        else return tag; // otherwise return tag to calling func
                        break;
                    case STARLexer.DVNSINGLE :
                    case STARLexer.DVNDOUBLE :
                    case STARLexer.DVNSEMICOLON :
                    case STARLexer.DVNFRAMECODE :
                    case STARLexer.DVNNON :
                        if( tags.size() < 1 ) {
                            fEh.error( fLex.getLine(), fLex.getColumn(), ERR_LOOPNOTAGS );
                            tags = null;
                        return null;
                        }
                        parsing_values = true;
                        item = new DataItemNode( (IntStringPair)tags.elementAt( col ) );
                        item.setDelimType( tok );
                        item.setValueLine( fLex.getLine() );
                        if( tok == STARLexer.DVNSEMICOLON ) { // strip \n
                            if( fLex.getText().substring( 0, 1 ).equals( "\n" ) )
                                item.setValue( fLex.getText().substring( 1 ) );
                            else item.setValue( fLex.getText() );
                        }
                        if( tok == STARLexer.DVNFRAMECODE ) // strip $
                            item.setValue( fLex.yytext().substring( 1 ) );
                        else if( tok == STARLexer.DVNNON ) 
                            item.setValue( fLex.yytext() );
                        else item.setValue( fLex.getText() );
                        item.setLoopFlag( true );
                        col++;
                        if( col == tags.size() ) col = 0;
                        if( fCh.data( item ) ) return null;
                        break;
                    case STARLexer.YYEOF : // fake end of data block
                        fCh.endData( fLex.getLine(), fDataId );
                        return null;
                    default : // invalid token
                        fEh.error( fLex.getLine(), fLex.getColumn(), ERR_TOKEN
                        + fLex.yytext() );
                        return null;
                }
            } while( tok != STARLexer.YYEOF );
            return null;
        }
        catch( Exception e ) { 
            e.printStackTrace(); 
            return null;
        }
    } //*************************************************************************
    /**
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
            CifParser p = new CifParser( lex );
            p.test_parse();
        }
        catch( Exception e ) { e.printStackTrace(); }
    } //*************************************************************************
    public void test_parse() {
        try {
            int tok = fLex.yylex();
	    while( tok != STARLexer.YYEOF ) {
                    System.out.print( STARLexer.TOKEN_TYPES[tok] + "(" 
                    + fLex.getLine() + ":" + fLex.getColumn() + "): " );
	        switch( tok ) {
                        case STARLexer.DVNSINGLE :
                        case STARLexer.DVNDOUBLE :
                        case STARLexer.DVNSEMICOLON :
                        System.out.println( fLex.getText() );
                            break;
                        default :
                            System.out.println( fLex.yytext() );
	        }
	        tok = fLex.yylex();
            }
            System.out.println( "End of data_ (EOF)" );
        }
        catch( Exception e ) { e.printStackTrace(); }
    } //*************************************************************************
}
