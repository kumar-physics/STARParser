/*
 * LoopParser.java
 *
 * Created on June 14, 2004, 3:01 PM
 *
 * This software is copyright (c) 2004 Board of Regents, University of Wisconsin.
 * All Rights Reserved.
 *
 * FILE:        $Source: /cvs_archive/cvs/sansj/src/EDU/bmrb/sansj/LoopParser.java,v $
 * 
 * AUTHOR:      $Author: dmaziuk $
 * DATE:        $Date: 2006/04/03 22:40:40 $
 * 
 * UPDATE HISTORY:
 * ---------------
 * $Log: LoopParser.java,v $
 * Revision 1.1  2006/04/03 22:40:40  dmaziuk
 * bug fix in lex specs.
 *
 * Revision 1.2  2004/06/15 00:48:45  dmaziuk
 * added loop parser
 *
 * Revision 1.1  2004/06/15 00:23:26  dmaziuk
 * adding another parser
 * */

package EDU.bmrb.sansj;

/**
 * Parser for NMR-STAR loops.
 * This parser is for files that contain only one loop: from "loop_" to "stop_"
 * (inclusive). When called from command line, parses file specified as first
 * argument, if present, or standard input, and prints tokens to standard out.
 * @see ContentHandler
 * @see ErrorHandler
 * @see STARLexer
 * @author  dmaziuk
 * @version 1
 */
public class LoopParser {
    /** scanner error */
    public static final String ERR_LEXER = "Lexer error: ";
    /** invalid token */
    public static final String ERR_INVTOKEN = "Invalid token: ";
    /** unexpected token */
    public static final String ERR_UNXTOKEN = "Unexpected token: ";
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
    /** is not allowed inside */
    public static final String ILLEGAL = " is not allowed inside a loop";
    /** content handler object */
    private ContentHandler fCh = null;
    /* error handler object */
    private ErrorHandler fEh = null;
    /** scanner */
    private STARLexer fLex = null;
    /** flag: true when parsing tags */
    private boolean fParsingTags = true;
    /** flag: true if encountered "stop_" */
    private boolean fHaveStop = false;
//******************************************************************************    
    /** Creates a new instance of LoopParser.
     * @param lex scaner
     * @param ch content handler object
     * @param eh error handler object
     */
    public LoopParser( STARLexer lex, ContentHandler ch, ErrorHandler eh ) {
        fLex = lex;
        fCh = ch;
        fEh = eh;
    } //************************************************************************
    /** Returns content handler.
     * @return content handler object
     */
    public ContentHandler getContentHandler() {
        return fCh;
    } //************************************************************************
    /** Sets content handler.
     * @param ch content handler object
     */
    public void setContentHandler( ContentHandler ch ) {
        fCh = ch;
    } //************************************************************************
    /** Returns error handler.
     * @return error handler object
     */
    public ErrorHandler getErrorHandler() {
        return fEh;
    } //************************************************************************
    /** Sets error handler.
     * @param eh error handler object
     */
    public void setErrorHandler( ErrorHandler eh ) {
        fEh = eh;
    } //************************************************************************
    /** Returns scanner object.
     * @return scanner
     */
    public STARLexer getScanner() {
        return fLex;
    } //************************************************************************
    /** Sets scanner object.
     * @param lex scanner
     */
    public void setScanner( STARLexer lex ) {
        fLex = lex;
    } //************************************************************************
    /** Parses input file */
    public void parse() {
        int col = 0;
        int lastline = -1;
        int wrongline = -1;
        int wrongcol = -1;
        int numvals = 0;
        java.util.List tags = new java.util.ArrayList();
        IntStringPair [] loopTags = null;
        DataItemNode item = null;
        int tok;
        try {
            do {
                tok = fLex.yylex();
                switch( tok ) {
                    case STARLexer.ERROR :
                        fEh.error( fLex.getLine(), fLex.getColumn(), ERR_LEXER
                        + fLex.yytext() );
                        return;
                    case STARLexer.WARNING :
                        if( fEh.warning( fLex.getLine(), fLex.getColumn(), WARN_KEYWORD
                        + fLex.yytext() ) ) return;
                        break;
                    case STARLexer.COMMENT :
                        if( fCh.comment( fLex.getLine(), fLex.yytext() ) ) return;
                        break;
                    case STARLexer.LOOPSTART :
                        if( fCh.startLoop( fLex.getLine() ) ) return;
                        break;
                    case STARLexer.STOP : // check loop count
                        if( loopTags.length < 1 ) {
                            fEh.error( fLex.getLine(), fLex.getColumn(), ERR_LOOPNOTAGS );
                            return;
                        }                            
                        if( numvals == 0 ) {
                            fEh.error( fLex.getLine(), fLex.getColumn(), ERR_EMPTYLOOP );
                            tags.clear();
                            tags = null;
                            return;
                        }
                        fHaveStop = true;
                        if( (numvals % loopTags.length) != 0 ) 
                            fEh.warning( ( (wrongline >= 0) ? wrongline : fLex.getLine() ),
                            ( (wrongcol >= 0) ? wrongcol : -1 ), WARN_LOOPCOUNT );
                        fCh.endLoop( fLex.getLine() ); 
                        return;
                    case STARLexer.TAGNAME : // save tag
                        IntStringPair tag = new IntStringPair( fLex.getLine(), fLex.yytext() );
                        tags.add( tag );
                        break;
                    case STARLexer.DVNSINGLE :
                    case STARLexer.DVNDOUBLE :
                    case STARLexer.DVNSEMICOLON :
                    case STARLexer.DVNFRAMECODE :
                    case STARLexer.DVNNON :
                        if( fParsingTags ) {
                            fParsingTags = false;
// move tags to an array
                            loopTags = new IntStringPair[tags.size()];
                            for( int i = 0; i < tags.size(); i++ ) loopTags[i] = (IntStringPair) tags.get( i );
                            tags.clear();
                            tags = null;
                        }
                        if( (loopTags == null) || (loopTags.length < 1) ) {
                            fEh.error( fLex.getLine(), fLex.getColumn(), ERR_LOOPNOTAGS );
                            tags = null;
                            return;
                        }
                        item = new DataItemNode( loopTags[col] );
                        item.setDelimType( tok );
                        item.setValueLine( fLex.getLine() );
                        if( tok == STARLexer.DVNSEMICOLON ) { // strip leading \n
                            if( fLex.getText().substring( 0, 1 ).equals( "\n" ) )
                                item.setValue( fLex.getText().substring( 1 ) );
                            else item.setValue( fLex.getText() );
                        }
                        if( tok == STARLexer.DVNFRAMECODE ) // strip leading $
                            item.setValue( fLex.yytext().substring( 1 ) );
                        else if( tok == STARLexer.DVNNON )  // single-word value is in yytext(),
                            item.setValue( fLex.yytext() ); // multi-word -- in getText()
                        else item.setValue( fLex.getText() );
                        item.setLoopFlag( true );
// check # values in row
                        col++;
                        numvals++;
                        if( (col == loopTags.length) && (lastline < fLex.getLine()) ) {
                            if( wrongline < 0 ) {
			        wrongline = fLex.getLine();
                                wrongcol = fLex.getColumn();
			    }
                        }
                        lastline = fLex.getLine();
                        if( col == loopTags.length ) col = 0;
                        if( fCh.data( item ) ) return;
                        break;
                    case STARLexer.YYEOF : // barf if no "stop_"
                        if( ! fHaveStop ) fEh.error( fLex.getLine(), fLex.getColumn(), ERR_EOF );
                        return;
                    default : // invalid token
                        fEh.error( fLex.getLine(), fLex.getColumn(), ERR_INVTOKEN + fLex.yytext() );
                        return;
                }
            } while( tok != STARLexer.YYEOF );
        }
        catch( Exception e ) { e.printStackTrace(); }
    } //************************************************************************
    /** Main method.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.err.println( "Run TestLoopParser class to test this parser" );
    } //************************************************************************
}
