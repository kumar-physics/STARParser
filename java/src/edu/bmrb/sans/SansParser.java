/*
 * Copyright (c) 2005 Board of Regents, University of Wisconsin.
 *  All Rights Reserved.
 */
package edu.bmrb.sans;

/**
 * Validating callback-based NMR-STAR parser.
 *
 * Returns tag/value pairs as DataItemNodes.
 * <P>
 * Callbacks are defined in ContentHandler and ErrorHandler interfaces.
 * <P>
 * Parser errors:
 * <UL>
 *  <LH>Fatal</LH>
 *  <LI>i/o exception when reading input</LI>
 *  <LI>Internal lexer errors (of the "can't possibly happen" kind)</LI>
 * </UL>
 * <UL>
 *  <LH>(maybe) recoverable</LH>
 *  <LI>Invalid tokens:
 *    <UL>
 *      <LI>Anything other than comments and/or one data block at top level</LI>
 *      <LI>Anything other than comments and/or saveframes in data block</LI>
 *      <LI>Anything other than comments, loops, "save_", and/or free tags
 *          (tag/value pairs) in a saveframe</LI>
 *      <LI>Anything other than comments, "stop_", tags and values in a loop</LI>
 *      <LI>Loop without "stop_" -- next token is flagged as invalid</LI>
 *      <LI>Saveframe without closing "save_" -- next token is flagged as invalid</LI>
 *      <LI>Global block(s)</LI>
 *      <LI>Multiple data blocks</LI>
 *      <LI>Free tags without values and values without tags</LI>
 *    </UL>
 *  </LI>
 *  <LI>Loop with no tags and/or values</LI>
 *  <LI>Premature end of file: EOF generates an "end data block" token that is
 *      legal only outside of a saveframe.</LI>
 * </UL>
 * Parser warnings:
 * <UL>
 *  <LI>NMR-STAR keyword inside a quoted value. This warning's purpose is to catch
 *      missing semicolon in semicolon-delimited value</LI>
 *  <LI>Loop count error. This warning is generated when number of values in a
 *      loop is not an exact multiple of number of tags. Line number reported is
 *      correct only if the loop is properly formatted and has no multi-line
 *      values (it's the best we can do at this level).</LI>
 * </UL>
 *
 * @author D. Maziuk
 */

/*
 * Created by IntelliJ IDEA.
 * User: dmaziuk
 * Date: Dec 6, 2005
 * Time: 6:21:21 PM
 *
 * $Source: /cvs_archive/cvs/starlibs5/sans/src/edu/bmrb/sans/SansParser.java,v $
 * $Author: dmaziuk $
 * Initial import: $Date: 2006/03/07 22:51:52 $
 * Update history:
 * ---------------
 * $Log: SansParser.java,v $
 * Revision 1.3  2006/03/07 22:51:52  dmaziuk
 * Javadoc cleanup
 *
 * Revision 1.2  2006/02/11 02:28:56  dmaziuk
 * updated
 *
 * Revision 1.1  2005/12/07 00:26:50  dmaziuk
 * Working
 * */

public class SansParser {
    private static final boolean DEBUG = false;
    /** scanner error. */
    public static final String ERR_LEXER = "Lexer error: ";
    /** scanner warning. */
    public static final String WARN_LEXER = "Lexer warning: ";
    /** invalid token at file level. */
    public static final String ERR_TOPTOKEN = "Illegal token: ";
    /** invalid token in data_ block. */
    public static final String ERR_DATATOKEN = "Illegal token in data_ block: ";
    /** invalid token in saveframe. */
    public static final String ERR_SAVETOKEN = "Illegal token in saveframe: ";
    /** value expected in saveframe. */
    public static final String ERR_SAVEVAL = "Saveframe: value expected, found: ";
    /** value not expected here. */
    public static final String ERR_SAVENVAL = "Saveframe: value not expected here: ";
    /** premature end of file. */
    public static final String ERR_EOF = "Premature end of file";
    /** missing "save_". */
    public static final String ERR_NOSAVE = ": no closing save_";
    /** missing "stop_". */
    public static final String ERR_NOSTOP = ": no closing stop_";
    /** loop with no tags. */
    public static final String ERR_NOTAGS = "Loop with no tags";
    /** loop with no values. */
    public static final String ERR_NOVALS = "Loop with no values";
    /** loop count error. */
    public static final String WARN_LOOPCNT = "Loop count error";
    /** tag not expected here. */
    public static final String ERR_LOOPNVAL = "Loop: tag not expected here: ";
    /** bad saveframe name. */
    public static final String ERR_BADCODE = "Invalid saveframe name/label: ";
    /** valid saveframe name. */
    public static final String FRAMECODE = "[.;:\"&<>(){}'`~!%A-Za-z0-9*|+-][_.;:\"&<>(){}'`~!$%A-Za-z0-9*|+-]*";
// "[}{_,.;:\"&<>()/'`~!@#$%A-Za-z0-9*|+-]+";

    /** scanner. */
    private STARLexer fLex = null;
    /** error handler. */
    private ErrorHandler fEh = null;
    /** content handler. */
    private ContentHandler fCh = null;
    /** data block name. */
    private String fBlockName = null;
//*******************************************************************************
    /** Creates new SansParser2.
     * @param lex scanner
     * @param ch content handler
     * @param eh error handler
     */
    public SansParser( STARLexer lex, ContentHandler ch, ErrorHandler eh ) {
        fLex = lex;
        fEh = eh;
        fCh = ch;
    } //*************************************************************************
    /** Creates new SansParser2 -- for testing.
     * @param lex scanner
     */
    private SansParser( STARLexer lex ) {
        fLex = lex;
    } //*************************************************************************
    /** Returns content handler
     * @return content handler
     */
    public ContentHandler getContentHandler() {
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
    public ErrorHandler getErrorHandler() {
        return fEh;
    } //*************************************************************************
    /** Changes error handler
     * @param eh new error handler
     */
    public void setErrorHandler( ErrorHandler eh ) {
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
    public void setScanner( STARLexer lex ) {
        fLex = lex;
    } //*************************************************************************
    /** Parses input file. */
    public void parse() {
        assert( fLex != null );
        assert( fCh == null );
        assert( fEh == null );
        STARLexer.Types tok;
        try {
            do {
                tok = fLex.yylex();
                switch( tok ) {
                    case ERROR :
                        fEh.fatalError( fLex.getLine(), fLex.getColumn(), ERR_LEXER + fLex.yytext() );
                        return;
                    case WARNING :
                        if( fEh.warning( fLex.getLine(), fLex.getColumn(), WARN_LEXER + fLex.yytext() ) )
                            return;
			            break;
                    case COMMENT :
                        if( fCh.comment( fLex.getLine(), fLex.getText() ) ) return;
                        break;
                    case DATASTART :
                        fBlockName = fLex.getText();
                        if( fCh.startData( fLex.getLine(), fBlockName ) ) return;
                        if( parseDataBlock() ) return;
                        break;
                    case EOF :
                        fCh.endData( fLex.getLine(), fBlockName );
                        return;
                    default :
                        if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_TOPTOKEN + fLex.yytext() ) )
                            return;
                }
            } while( tok != STARLexer.Types.EOF );
        }
        catch( java.io.IOException e ) {
            fEh.fatalError( fLex.getLine(), fLex.getColumn(), e.getMessage() );
            System.err.println( e );
            e.printStackTrace();
        }
    } //*************************************************************************
    /** Parses data block.
     * @return true to stop parsing
     * @throws java.io.IOException from the scanner.
     */
    private boolean parseDataBlock() throws java.io.IOException {
        STARLexer.Types tok;
        do {
            tok = fLex.yylex();
if( DEBUG ) System.out.println( "** in data block" );
            switch( tok ) {
                case ERROR :
                    fEh.fatalError( fLex.getLine(), fLex.getColumn(), ERR_LEXER + fLex.yytext() );
                    return true;
                case WARNING :
                    if( fEh.warning( fLex.getLine(), fLex.getColumn(), WARN_LEXER + fLex.yytext() ) )
                        return true;
                    break;
                case COMMENT :
                    if( fCh.comment( fLex.getLine(), fLex.getText() ) ) return true;
                    break;
                case SAVESTART :
                    String savename = fLex.getText();
if( DEBUG ) System.out.printf( "checking |%s| against |%s|\n", savename, FRAMECODE );
                    if( ! savename.matches( FRAMECODE ) ) {
                	if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_BADCODE + fLex.yytext() ) )
                	    return true;
                    }
                    if( fCh.startSaveFrame( fLex.getLine(), savename ) ) return true;
                    if( parseSaveFrame( savename ) ) return true;
                    break;
                case EOF :
                    fCh.endData( fLex.getLine(), fBlockName );
                    return true;
                default :
                    if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_DATATOKEN + fLex.yytext() ) )
                        return true;
            }
        } while( tok != STARLexer.Types.DATAEND );
        return false;
    } //*************************************************************************
    /** Parses saveframe.
     * @param name saveframe name
     * @return true to stop parsing
     * @throws java.io.IOException from the scanner.
     */
    private boolean parseSaveFrame( String name ) throws java.io.IOException {
        STARLexer.Types tok;
        String val;
        DataItemNode item = null;
        boolean needvalue = false;
        do {
            tok = fLex.yylex();
            switch( tok ) {
                case ERROR :
                    fEh.fatalError( fLex.getLine(), fLex.getColumn(), ERR_LEXER + fLex.yytext() );
                    return true;
                case WARNING :
                    if( fEh.warning( fLex.getLine(), fLex.getColumn(), WARN_LEXER + fLex.yytext() ) )
                        return true;
                    break;
                case COMMENT :
                    if( fCh.comment( fLex.getLine(), fLex.getText() ) ) return true;
                    break;
                case EOF :
                    fEh.error( fLex.getLine(), fLex.getColumn(), ERR_EOF + ERR_NOSAVE );
                    fCh.endData( fLex.getLine(), fBlockName );
                    return true;
                case SAVEEND : // exit point
                    if( needvalue ) {
                        if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_SAVEVAL + fLex.getText() ) )
                            return true;
                    }
                    return fCh.endSaveFrame( fLex.getLine(), name );
                case LOOPSTART :
                    if( needvalue ) {
                        if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_SAVEVAL + fLex.getText() ) )
                            return true;
                    }
                    if( fCh.startLoop( fLex.getLine() ) ) return true;
                    if( parseLoop() ) return true;
                    break;
                case TAGNAME :
                    if( needvalue ) {
                        if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_SAVEVAL + fLex.getText() ) )
                            return true;
                    }
                    item = new DataItemNode( fLex.getLine(), fLex.getText() );
                    needvalue = true;
                    break;
                case DVNSINGLE :
                case DVNDOUBLE :
                case DVNSEMICOLON :
                case DVNFRAMECODE :
                case DVNNON :
                    if( ! needvalue )
                        if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_SAVENVAL + fLex.getText() ) )
                            return true;
                    needvalue = false;
                    val = fLex.getText();
                    switch( tok ) {
                        case DVNSEMICOLON : // strip leading \n
                            if( val.indexOf( System.getProperty( "line.separator" ) ) == 0 )
                                val = val.substring( System.getProperty( "line.separator" ).length() );
                            break;
                        case DVNFRAMECODE : // check if valid name
                	    if( ! val.matches( FRAMECODE ) ) {
                		if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_BADCODE + fLex.yytext() ) )
                		return true;
                	    }
// strip $ -- now done by lexer
//                            val = val.substring( 1 );
                            break;
                    }
// if this throws a NullPointer, the "needvalue" check above didn't work.
                    item.setValueLine( fLex.getLine() );
                    item.setDelimType( tok );
                    item.setValue( val );
                    if( fCh.data( item ) ) return true;
                    item = null;
                    break;
                default :
                    if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_DATATOKEN + fLex.yytext() ) )
                        return true;
            }
        } while( tok != STARLexer.Types.EOF );
        return false;
    } //*************************************************************************
    /** Parses a loop.
     * @return true to stop parsing
     * @throws java.io.IOException from the scanner.
     */
    private boolean parseLoop() throws java.io.IOException {
        STARLexer.Types tok;
        int numvals = 0;
        int lastline = -1, wrongline = -1, wrongcol = -1;
        int loopcol = -1; //0;
        boolean parsingtags = true;
        String val;
        DataItemNode item;
        java.util.ArrayList<IntStringPair> tags = new java.util.ArrayList<IntStringPair>();
        do {
            tok = fLex.yylex();
            switch( tok ) {
                case ERROR :
                    fEh.fatalError( fLex.getLine(), fLex.getColumn(), ERR_LEXER + fLex.yytext() );
                    return true;
                case WARNING :
                    if( fEh.warning( fLex.getLine(), fLex.getColumn(), WARN_LEXER + fLex.yytext() ) )
                        return true;
                    break;
                case COMMENT :
                    if( fCh.comment( fLex.getLine(), fLex.getText() ) )
                        return true;
                    break;
                case EOF :
                    fEh.error( fLex.getLine(), fLex.getColumn(), ERR_EOF + ERR_NOSTOP );
                    fCh.endData( fLex.getLine(), fBlockName );
                    return true;
                case STOP : // exit point
                    if( tags.size() < 1 )
                        if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_NOTAGS ) )
                            return true;
                    if( numvals < 1 )
                        if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_NOVALS ) )
                            return true;
                    boolean rc = false;
                    if( ( numvals % tags.size() ) != 0 ) {
                        if( wrongline < 0 ) wrongline = fLex.getLine();
                        rc = fEh.warning( wrongline, wrongcol, WARN_LOOPCNT );
                    }
                    rc = rc || fCh.endLoop( fLex.getLine() );
                    return rc;
                case TAGNAME :
                    if( ! parsingtags )
                        if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_LOOPNVAL + fLex.yytext() ) )
                            return true;
                    tags.add( new IntStringPair( fLex.getLine(), fLex.getText() ) );
                    break;
                case DVNSINGLE :
                case DVNDOUBLE :
                case DVNSEMICOLON :
                case DVNFRAMECODE :
                case DVNNON :
                    if( parsingtags ) parsingtags = false;
                    val = fLex.getText();
                    switch( tok ) {
                        case DVNSEMICOLON : // strip leading \n
                            if( val.indexOf( System.getProperty( "line.separator" ) ) == 0 )
                                val = val.substring( System.getProperty( "line.separator" ).length() );
                            break;
                        case DVNFRAMECODE : // check if valid name
                	    if( ! val.matches( FRAMECODE ) ) {
                		if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_BADCODE + fLex.yytext() ) )
                		return true;
                	    }
// strip $ -- now done by lexer
//                            val = val.substring( 1 );
                            break;
                    }
                    numvals++;
// check # values in the row
                    loopcol++;
                    if( loopcol == tags.size() ) {
// save line,col where new loop row started
                        if( lastline < fLex.getLine() ) {
                            if( wrongline < 0 ) {
                                wrongline = fLex.getLine();
                                wrongcol = fLex.getColumn();
                            }
                            lastline = fLex.getLine();
                        }
                        loopcol = 0;
                    }
if( DEBUG ) System.out.printf( "loopcol %d, numvals %d, numtags %d, line %d\n",
  loopcol, numvals, tags.size(), fLex.getLine() );
                    item = new DataItemNode( tags.get( loopcol ) );
                    item.setValueLine( fLex.getLine() );
                    item.setValue( val );
                    item.setDelimType( tok );
                    item.setLoopFlag( true );
if( DEBUG ) System.out.printf( "SANS: %s:%s\n", tags.get( loopcol ).getString(), val );
                    if( fCh.data( item ) ) return true;
                    break;
                default :
                    if( fEh.error( fLex.getLine(), fLex.getColumn(), ERR_DATATOKEN + fLex.yytext() ) )
                        return true;
            }
        } while( tok != STARLexer.Types.EOF );
        return false;
    } //*************************************************************************
    /** test parser. */
    private void test_parse()  throws java.io.IOException {
        STARLexer.Types tok = fLex.yylex();
        while( tok != STARLexer.Types.EOF ) {
            System.out.printf( "%s (%d,%d): %s\n", tok, fLex.getLine(), fLex.getColumn(), fLex.getText() );
            if( tok == STARLexer.Types.ERROR ) 
	        System.err.printf( "Error: %s in line %d (%d),", fLex.getText(), fLex.getLine(), fLex.getColumn() );
            if( tok == STARLexer.Types.WARNING ) 
	        System.err.printf( "Warning: %s in line %d (%d),", fLex.getText(), fLex.getLine(), fLex.getColumn() );
            tok = fLex.yylex();
        }
//        System.out.println( "End of data (EOF)" );
    } //*************************************************************************
    /**
     * Main method -- for testing only.
     * @param args command-line arguments.
     */
    public static void main( String [] args ) {
        try {
            java.io.BufferedReader r;
	    if( args.length < 1 ) r = new java.io.BufferedReader( new java.io.InputStreamReader( System.in ) );
	    else r = new java.io.BufferedReader( new java.io.FileReader( args[0] ) );
            STARLexer lex = new STARLexer( r );
            SansParser p = new SansParser( lex );
	    long start = System.currentTimeMillis();
            p.test_parse();
	    System.err.printf( "Elapsed: %d millis (%d - %d)\n", (System.currentTimeMillis() - start),
	    System.currentTimeMillis(), start );
	    System.err.printf( "%d\n", (System.currentTimeMillis() - start) );
	    if( args.length > 0 ) r.close();
        }
        catch( Exception e ) {
            System.err.println( e );
            e.printStackTrace();
            System.exit( 1 );
        }
    } //*************************************************************************
}
