/*
 * SimpleStarParser.java
 *
 * Created on July 1, 2004, 2:00 PM
 *
 * This software is copyright (c) 2004 Board of Regents, University of Wisconsin.
 * All Rights Reserved.
 *
 * FILE:        $Source: /cvs_archive/cvs/sansj/src/EDU/bmrb/sansj/NMRStarParserNS.java,v $
 * 
 * AUTHOR:      $Author: dmaziuk $
 * DATE:        $Date: 2006/04/03 22:40:41 $
 * 
 * UPDATE HISTORY:
 * ---------------
 * $Log: NMRStarParserNS.java,v $
 * Revision 1.1  2006/04/03 22:40:41  dmaziuk
 * bug fix in lex specs.
 *
 * Revision 1.2  2004/07/01 20:30:44  dmaziuk
 * slight improvements to syntax checker
 *
 * Revision 1.1  2004/07/01 19:57:32  dmaziuk
 * added another parser
 * */

package EDU.bmrb.sansj;

import EDU.bmrb.sansj.starlib.*;

/**
 * Validating NMR-STAR parser.
 * Unlike SansParser, this parser returns tag and values in separate callbacks,
 * and does not stop on syntax errors. This parser is useful for NMR-STAR
 * syntax checking: you can report all syntax errors in single pass.
 * Tokens are returned as StarNodes. Classes using this parser must implement
 * StarContentHandler interface.
 * <P>
 * Parse errors:
 * <UL>
 *   <LI>Lexer error (should never happen).
 *   <LI>Global block(s) in input.
 *   <LI>Anything other than comments or data block at top-level.
 *   <LI>Anything other than comments or saveframes in data block.
 *   <LI>Anything other than comments, loops, <CODE>save_</CODE>, or free tags 
 *       (tag/value pairs) in saveframe.
 *   <LI>Anything other than comments, <CODE>stop_</CODE>, tags, or values in loop
 *   <LI>Loops with no values.
 *   <LI>Loops with no tags.
 *   <LI>Premature end of file: EOF is legal only inside a data block, EOF inside
 *       a saveframe or loop is an error.
 * </UL>
 * Error reporting is not very detailed at the moment: parser simply reports
 * <CODE>Invalid token: <EM>token</EM></CODE>. <BR>
 * Parser assumes that first data block continues to the end of input. If there
 * is more than one data block in input, parser will see the second <CODE>data_</CODE>
 * as being inside the current data block and it will report that as invalid token 
 * (only comments and saveframes are allowed in data block).
 * <P>
 * Parse warnings:
 * <UL>
 *   <LI>NMR-STAR keyword inside quoted value. This warning is generated by the
 *       lexer, it's purpose is to catch semicolon-delimited values where closing
 *       semicolon is missing.
 *   <LI>Loop count error. This warning is generated when number of values in the
 *       loop is not an exact multiple of the number of tags. Parser makes "best
 *       effort" to report the line number where the value is missing: in a 
 *       well-formatted loop it will report [first] line number where a value is
 *       missing, otherwise it'll report the line that contains <CODE>stop_</CODE>
 *       or anything in between. Note also that if there is as many values missing
 *       as there are columns in the loop, parser will not see that as error.
 * </UL>
 * @see StarContentHandler
 * @see ErrorHandler
 * @see STARLexer
 * @author  dmaziuk
 * @version 1
 */
public class NMRStarParserNS {
    /** scanner error */
    public static final String ERR_LEXER = "Lexer error: ";
    /** global blocks are not allowed */
    public static final String ERR_GLOBAL = "Global blocks are illegal in NMR-STAR";
    /** invalid token */
    public static final String ERR_TOKEN = "Invalid token: ";
    /** premature EOF */
    public static final String ERR_EOF = "Premature end of file";
    /** loop with no values */
    public static final String ERR_EMPTYLOOP = "Loop with no values";
    /** loop with no tags */
    public static final String ERR_LOOPNOTAGS = "Loop with no tags";
    /** parse warning */
    public static final String WARN_KEYWORD = "Keyword in value: ";
    /** loop count error */
    public static final String WARN_LOOPCOUNT = "Loop count error";
    /** token */
    public static final String TOKEN = "Token ";
    /** is not allowed outside of a data block */
    public static final String NOTINDATABLOCK = " is not allowed outside of a data block";
    /** is not allowed inside */
    public static final String ILLEGAL = " is not allowed inside a";
    /** data block */
    public static final String DATA_BLOCK = " data block";
    /** saveframe */
    public static final String SAVEFRAME = " saveframe";
    /** loop */
    public static final String LOOP = " loop";
    /** value expected */
    public static final String NOTVALUE = ": value expected";
    /** value not expected */
    public static final String AVALUE = ": value not expected here";
    /** content handler object */
    private StarContentHandler fCh = null;
    /** error handler object */
    private ErrorHandlerNS fEh = null;
    /** scanner */
    STARLexer fLex = null;
    /** comment */
    StringBuffer fComment = null;
    /** true after consuming a tag */
    private boolean fNeedValue = false;
    /** true when parsing loop tags */
    private boolean fParsingTags = false;
    /** true when parsing loop values */
    private boolean fParsingValues = false;
//*******************************************************************************
    /** Creates new SimpleStarParser
     * @param lex scaner
     */
    public NMRStarParserNS(STARLexer lex) {
        fLex = lex;
    } //*************************************************************************
    /** Creates new SansParser.
     * @param lex scaner
     * @param ch content handler object
     * @param eh error handler object
     */
    public NMRStarParserNS(STARLexer lex, StarContentHandler ch, ErrorHandlerNS eh) {
        fLex = lex;
        fCh = ch;
        fEh = eh;
    } //*************************************************************************
    /** Returns content handler.
     * @return content handler object
     */
    public StarContentHandler getContentHandler() {
        return fCh;
    } //*************************************************************************
    /** Sets content handler.
     * @param ch content handler object
     */
    public void setContentHandler( StarContentHandler ch ) {
        fCh = ch;
    } //*************************************************************************
    /** Returns error handler.
     * @return error handler object
     */
    public ErrorHandlerNS getErrorHandler() {
        return fEh;
    } //*************************************************************************
    /** Sets error handler.
     * @param eh error handler object
     */
    public void setErrorHandler( ErrorHandlerNS eh ) {
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
        fComment = new StringBuffer();
        StarNode node;
        int tok;
        try {
            do {
                tok = fLex.yylex();
//System.err.println( "********************* PARSE Next token: " + tok );
                switch( tok ) {
                    case STARLexer.ERROR :
                        if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_LEXER
                        + fLex.yytext() ) ) return;
                        break;
                    case STARLexer.COMMENT :
                        fComment.append( fLex.yytext() );
                        fComment.append( "\n" );
                        break;
                    case STARLexer.DATASTART : // parse data block
                        node = new StarNode( fLex.getLine(), fLex.getColumn(), 
                                             fLex.yytext().substring( 5 ) ); // strip data_
                        if( fComment.length() > 0 ) {
                            int pos = fComment.toString().lastIndexOf( "\n" );
                            if( pos > -1 ) node.setPreComment( fComment.toString().substring( 0, pos ) );
                            else node.setPreComment( fComment.toString() );
                            fComment.setLength( 0 );
                        }
                        if( fCh.startData( node ) ) return;
                        if( parseDataBlock() ) return;
                        break;
                    case STARLexer.YYEOF : // fake end of data block
                        node = new StarNode( fLex.getLine(), fLex.getColumn() );
                        if( fComment.length() > 0 ) {
                            int pos = fComment.toString().lastIndexOf( "\n" );
                            if( pos > -1 ) node.setPreComment( fComment.toString().substring( 0, pos ) );
                            else node.setPreComment( fComment.toString() );
                            fComment.setLength( 0 );
                        }
                        fCh.endData( node );
                        return;
                    default : // invalid token
                        if( fEh.error( fLex.getLine(), fLex.getColumn(), TOKEN
                        + fLex.yytext() + NOTINDATABLOCK ) ) return;
                }
            } while( tok != STARLexer.YYEOF );
        }
        catch( Exception e ) { 
            System.err.println( e.getLocalizedMessage() );
            e.printStackTrace(); 
        }
    } //*************************************************************************
    /** Parses data block.
     * @return true on error (stop parsing)
     */
    public boolean parseDataBlock() {
        int tok;
        StarNode node;
        try {
            do {
                tok = fLex.yylex();
//System.err.println( "********************* DataBlock Next token: " + tok );
                switch( tok ) {
                    case STARLexer.ERROR :
                        if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_LEXER
                        + fLex.yytext() ) ) return true;
                        break;
                    case STARLexer.COMMENT :
                        fComment.append( fLex.yytext() );
                        fComment.append( "\n" );
                        break;
                    case STARLexer.SAVESTART : // parse saveframe
                        node = new StarNode( fLex.getLine(), fLex.getColumn(), 
                                             fLex.yytext().substring( 5 ) ); // strip save_
                        if( fComment.length() > 0 ) {
                            int pos = fComment.toString().lastIndexOf( "\n" );
                            if( pos > -1 ) node.setPreComment( fComment.toString().substring( 0, pos ) );
                            else node.setPreComment( fComment.toString() );
                            fComment.setLength( 0 );
                        }
                        if( fCh.startSaveFrame( node ) ) return true;
                        if( parseSaveFrame() ) {
//System.err.println( "*************************** EndSaveframe returns true ***" );
                            return true;
                        }
//System.err.println( "*************************** EndSaveframe returns false ***" );
                        break;
                    case STARLexer.YYEOF : // fake end of data block
                        node = new StarNode( fLex.getLine(), fLex.getColumn() );
                        if( fComment.length() > 0 ) {
                            int pos = fComment.toString().lastIndexOf( "\n" );
                            if( pos > -1 ) node.setPreComment( fComment.toString().substring( 0, pos ) );
                            else node.setPreComment( fComment.toString() );
                            fComment.setLength( 0 );
                        }
                        fCh.endData( node );
                        return true;
                    default : // invalid token
                        return fEh.error( fLex.getLine(), fLex.getColumn(), ERR_TOKEN
                        + fLex.yytext() );
//                        return true;
                }
            } while( tok != STARLexer.YYEOF );
//if( tok == STARLexer.YYEOF );
//System.err.println( "*************************** DataBlock: EOF ***" );
            return false;
        }
        catch( Exception e ) { 
            System.err.println( e.getLocalizedMessage() );
            e.printStackTrace(); 
            return true;
        }
    } //*************************************************************************
    /** Parses a saveframe.
     * @return true on error (stop parsing)
     */
    public boolean parseSaveFrame() {
        int tok;
        DataNameNode tag;
        DataValueNode val;
        StarNode node;
        try {
            do {
                tok = fLex.yylex();
                switch( tok ) {
                    case STARLexer.ERROR :
                        if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_LEXER
                        + fLex.yytext() ) ) return true;
                        break;
                    case STARLexer.WARNING :
                        if( fEh.warning( fLex.getLine(), fLex.getColumn(), WARN_KEYWORD
                        + fLex.yytext() ) ) return true;
                        break;
                    case STARLexer.COMMENT :
                        fComment.append( fLex.yytext() );
                        fComment.append( "\n" );
                        break;
                    case STARLexer.SAVEEND : // exit point
                        if( fNeedValue ) {
                            if( fEh.error( fLex.getLine(), fLex.getColumn(), TOKEN
                            + fLex.yytext() + NOTVALUE ) ) return true;
//                            else return false;
                        }
                        node = new StarNode( fLex.getLine(), fLex.getColumn() );
                        if( fComment.length() > 0 ) {
                            int pos = fComment.toString().lastIndexOf( "\n" );
                            if( pos > -1 ) node.setPreComment( fComment.toString().substring( 0, pos ) );
                            else node.setPreComment( fComment.toString() );
                            fComment.setLength( 0 );
                        }
                        if( fCh.endSaveFrame( node ) )
                            return true;
                        return false;
                    case STARLexer.LOOPSTART :
                        if( fNeedValue ) {
                            return fEh.error( fLex.getLine(), fLex.getColumn(), TOKEN
                            + fLex.yytext() + NOTVALUE );
                        }
                        node = new StarNode( fLex.getLine(), fLex.getColumn() );
                        if( fComment.length() > 0 ) {
                            int pos = fComment.toString().lastIndexOf( "\n" );
                            if( pos > -1 ) node.setPreComment( fComment.toString().substring( 0, pos ) );
                            else node.setPreComment( fComment.toString() );
                            fComment.setLength( 0 );
                        }
                        if( fCh.startLoop( node ) ) return true;
                        if( parseLoop() ) return true;
                        break;
                    case STARLexer.TAGNAME : // tag
                        if( fNeedValue ) {
                            return fEh.error( fLex.getLine(), fLex.getColumn(), TOKEN
                            + fLex.yytext() + NOTVALUE );
                        }
                        tag = new DataNameNode( fLex.getLine(), fLex.getColumn(), fLex.yytext() );
                        fNeedValue = true;
                        if( fComment.length() > 0 ) {
                            int pos = fComment.toString().lastIndexOf( "\n" );
                            if( pos > -1 ) tag.setPreComment( fComment.toString().substring( 0, pos ) );
                            else tag.setPreComment( fComment.toString() );
                            fComment.setLength( 0 );
                        }
                        if( fCh.tag( tag ) ) return true;
                        break;
                    case STARLexer.DVNSINGLE :
                    case STARLexer.DVNDOUBLE :
                    case STARLexer.DVNSEMICOLON :
                    case STARLexer.DVNFRAMECODE :
                    case STARLexer.DVNNON :
                        if( ! fNeedValue ) {
                            fNeedValue = false;
                            if( fEh.error( fLex.getLine(), fLex.getColumn(), TOKEN
                            + fLex.yytext() + AVALUE ) ) return true;
                            else break;
                        }
                        fNeedValue = false;
                        val = new DataValueNode( fLex.getLine(), fLex.getColumn() );
                        val.setDelimType( tok );
                        if( fComment.length() > 0 ) {
                            int pos = fComment.toString().lastIndexOf( "\n" );
                            if( pos > -1 ) val.setPreComment( fComment.toString().substring( 0, pos ) );
                            else val.setPreComment( fComment.toString() );
                            fComment.setLength( 0 );
                        }
                        if( tok == STARLexer.DVNSEMICOLON ) { // strip \n
                            if( fLex.getText().substring( 0, 1 ).equals( "\n" ) )
                                val.setValue( fLex.getText().substring( 1 ) );
                            else val.setValue( fLex.getText() );
                        }
                        else if( tok == STARLexer.DVNFRAMECODE ) // strip $
                            val.setValue( fLex.yytext().substring( 1 ) );
                        else if( tok == STARLexer.DVNNON ) 
                            val.setValue( fLex.yytext() );
                        else val.setValue( fLex.getText() );
                        if( fCh.data( val ) ) return true;
                        break;
                    case STARLexer.YYEOF : // error: no closing save_
                        fEh.error( fLex.getLine(), fLex.getColumn(), ERR_EOF );
                        return true;
                    default : // invalid token
                        return fEh.error( fLex.getLine(), fLex.getColumn(), TOKEN
                        + fLex.yytext() + ILLEGAL + SAVEFRAME );
                }
            } while( tok != STARLexer.YYEOF );
            return false;
        }
        catch( Exception e ) { 
            System.err.println( e.getLocalizedMessage() );
            e.printStackTrace(); 
            return true;
        }
    } //*************************************************************************
    /** Parses a loop.
     * @return true on error (stop parsing)
     */
    public boolean parseLoop() {
        int tok;
        DataNameNode tag;
        DataValueNode val;
        StarNode node;
        int numtags = 0;
        int numvals = 0;
        int col = 0;
        int lastline = -1;
        int wrongline = -1;
        int wrongcol = -1;
        fParsingTags = true;
        fParsingValues = false;
        try {
            do {
                tok = fLex.yylex();
                switch( tok ) {
                    case STARLexer.ERROR :
                        if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_LEXER
                        + fLex.yytext() ) ) return true;
                        break;
                    case STARLexer.WARNING :
                        if( fEh.warning( fLex.getLine(), fLex.getColumn(), WARN_KEYWORD
                        + fLex.yytext() ) ) return true;
                        break;
                    case STARLexer.COMMENT :
                        fComment.append( fLex.yytext() );
                        fComment.append( "\n" );
                        break;
                    case STARLexer.STOP : 
                        if( numtags < 1 ) { // loop with no tags
                            return fEh.error( fLex.getLine(), fLex.getColumn(), ERR_LOOPNOTAGS );
                        }                            
                        if( numvals < 1 ) { // loop with no values
                            return fEh.error( fLex.getLine(), fLex.getColumn(), ERR_EMPTYLOOP );
                        }
                        fParsingTags = false;
                        fParsingValues = false;
                        boolean rc = false;
                        if( (numvals % numtags) != 0 ) 
                            rc = fEh.warning( ( (wrongline >= 0) ? wrongline : fLex.getLine() ),
                            ( (wrongcol >= 0) ? wrongcol : -1 ), WARN_LOOPCOUNT );
                        node = new StarNode( fLex.getLine(), fLex.getColumn() );
                        if( fComment.length() > 0 ) {
                            int pos = fComment.toString().lastIndexOf( "\n" );
                            if( pos > -1 ) node.setPreComment( fComment.toString().substring( 0, pos ) );
                            else node.setPreComment( fComment.toString() );
                            fComment.setLength( 0 );
                        }
                        rc = (rc || fCh.endLoop( node ) ); 
                        return rc;
                    case STARLexer.TAGNAME : // tag
                        if( ! fParsingTags ) {
                            return fEh.error( fLex.getLine(), fLex.getColumn(), TOKEN
                            + fLex.yytext() + NOTVALUE );
                        }
                        tag = new DataNameNode( fLex.getLine(), fLex.getColumn(), fLex.yytext() );
                        if( fComment.length() > 0 ) {
                            int pos = fComment.toString().lastIndexOf( "\n" );
                            if( pos > -1 ) tag.setPreComment( fComment.toString().substring( 0, pos ) );
                            else tag.setPreComment( fComment.toString() );
                            fComment.setLength( 0 );
                        }
                        numtags++;
                        if( fCh.tag( tag ) ) return true;
                        break;
                    case STARLexer.DVNSINGLE :
                    case STARLexer.DVNDOUBLE :
                    case STARLexer.DVNSEMICOLON :
                    case STARLexer.DVNFRAMECODE :
                    case STARLexer.DVNNON :
                        if( fParsingTags ) {
                            fParsingTags = false;
                            fParsingValues = true;
                        }
                        if( ! fParsingValues ) {
                            return fEh.error( fLex.getLine(), fLex.getColumn(), TOKEN
                            + fLex.yytext() + AVALUE );
                        }
                        val = new DataValueNode( fLex.getLine(), fLex.getColumn() );
                        val.setDelimType( tok );
                        if( fComment.length() > 0 ) {
                            int pos = fComment.toString().lastIndexOf( "\n" );
                            if( pos > -1 ) val.setPreComment( fComment.toString().substring( 0, pos ) );
                            else val.setPreComment( fComment.toString() );
                            fComment.setLength( 0 );
                        }
                        if( tok == STARLexer.DVNSEMICOLON ) { // strip \n
                            if( fLex.getText().substring( 0, 1 ).equals( "\n" ) )
                                val.setValue( fLex.getText().substring( 1 ) );
                            else val.setValue( fLex.getText() );
                        }
                        else if( tok == STARLexer.DVNFRAMECODE ) // strip $
                            val.setValue( fLex.yytext().substring( 1 ) );
                        else if( tok == STARLexer.DVNNON ) 
                            val.setValue( fLex.yytext() );
                        else val.setValue( fLex.getText() );
// check # values in row
                        col++;
                        numvals++;
                        if( (col == numtags) && (lastline < fLex.getLine()) ) {
                            if( wrongline < 0 ) {
			        wrongline = fLex.getLine();
                                wrongcol = fLex.getColumn();
			    }
                        }
                        lastline = fLex.getLine();
                        if( col == numtags ) col = 0;
                        if( fCh.data( val ) ) return true;
                        break;
                    case STARLexer.YYEOF : // error: no closing save_
                        fEh.error( fLex.getLine(), fLex.getColumn(), ERR_EOF );
                        return true;
                    default : // invalid token
                        return fEh.error( fLex.getLine(), fLex.getColumn(), TOKEN
                        + fLex.yytext() + ILLEGAL + LOOP );
                }
            } while( tok != STARLexer.YYEOF );
            return false;
        }
        catch( Exception e ) { 
            System.err.println( e.getLocalizedMessage() );
            e.printStackTrace(); 
            return true;
        }
    } //*************************************************************************
    private void test_parse() {
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
            NMRStarParserNS p = new NMRStarParserNS( lex );
            p.test_parse();
        }
        catch( Exception e ) { e.printStackTrace(); }
    } //*************************************************************************

}
