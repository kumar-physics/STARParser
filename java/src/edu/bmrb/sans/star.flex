/*
 * This software is copyright (c) 2006 Board of Regents, University of
 * Wisconsin. All Rights Reserved.
 *
 * $Id$
 *
 */
//***************************************************************************
// Scanner flex specification
//***************************************************************************
/* Lex Definitions for a STAR File */

package edu.bmrb.sans;

/**
 * STAR Lexer with some error checking.
 *
 * <STRONG>NOTES</STRONG>:<UL>
 *  <LI>this scanner does not close input file on EOF</LI>
 *  <LI>the scanner does not generate fake "end-of" tokens for
 *      end of data block, end of global block, end of loop
 *      (if closing "stop_" is omitted)</LI>
 *  <LI>this scanner generates "keyword in value" warnings (in C++
 *      version they are generated by the parser)</LI>
 * </UL>
 */

%%

%class STARLexer
%public
%line
%column
%unicode
%type Types
%x YYSINGLE YYDOUBLE YYSEMI YYSEMIEND

%init{
    buf = new StringBuilder();
%init}

%{
    /** tokens */
    public enum Types {
        /** Parser error. */
        ERROR,
        /** Parser warning. */
        WARNING,
        /** Start of global block. */
        GLOBALSTART,
        /** End of global block. */
        GLOBALEND,
        /** Start of data block. */
        DATASTART,
        /** End of data block. */
        DATAEND,
        /** Start of saveframe. */
        SAVESTART,
        /** End of saveframe. */
        SAVEEND,
        /** Start of loop. */
        LOOPSTART,
        /** End of loop. */
        STOP,
        /** Tag. */
        TAGNAME,
        /** Value enclosed in single quotes. */
        DVNSINGLE,
        /** Value enclosed in double quotes. */
        DVNDOUBLE,
        /** Value enclosed in semicolons. */
        DVNSEMICOLON,
        /** Framecode value. */
        DVNFRAMECODE,
        /** Bareword value. */
        DVNNON,
        /** Comment. */
        COMMENT,
        /** End of input. */
        EOF;
    }
    /* buffer for quoted values */
    private StringBuilder buf;
    /** Returns line number (counting from 1).
     * Returns <CODE>yyline + 1</CODE>
     * @return line number
     */
    public int getLine() {
        return yyline + 1;
    }
    /** Returns column number.
     * @return column number
     */
    public int getColumn() {
        return yycolumn;
    }
    /** Returns text of the last token.
     * <P>
     * Use this method instead of <CODE>yytext()</CODE> to retrieve tokens.
     *<P>
     * Because quoted values (DVNSINGLE, DVNDOUBLE, DVNSEMICOLON) are parsed
     * differently from non-quoted ones (DVNNON and DVNFRAMECODE), their
     * text is stored in a separate buffer.
     * <P>
     * For other token types returns <CODE>yytext()</CODE>.
     */
    public String getText() {
        if( buf.length() < 1 ) return yytext();
	return buf.toString();
    }
    /** Pushes number of characters back into input stream.
     * @param num number of characters to push back
     */
    public void pushBack( int num ) {
        yypushback( num );
    }
//******************************************************************************
%}

WS=[ \t\b\012]
NON_WS=[^ \t\b\012\r]
NL=(\r|\n|\r\n)
WS_NL = ({WS}|{NL})
NON_NL = [^\r\n]

GLOBALSTART         = ([gG][lL][oO][bB][aA][lL]_)
DATASTART           = ([dD][aA][tT][aA]_)
SAVEEND             = ([sS][aA][vV][eE]_)
LOOPSTART           = ([lL][oO][oO][pP]_)
STOP                = ([sS][tT][oO][pP]_)
/*
   NMR-STAR 3.0/PDBX tagname
   TAGNAME              _[_[:alnum:]]+\.[][_[:alnum:]%-]+
*/
/* 
   NMR-STAR 2.1 tagname
   TAGNAME             = (_[_[:letter:][:digit:]+[\]\[_[:letter:][:digit:]%-]+)
*/
TAGNAME             = (_[_[:letter:][:digit:]]+[._\[\][:letter:][:digit:]%-]+)
SINGLESTART         = {WS}+\'
DOUBLESTART         = {WS}+\"

/*
   PDBX definition for code (sf name) is
   [_,.;:"&<>()/\{}'`~!@#$%A-Za-z0-9*|+-]+

FRAMECODE           = \$[_,.;:\"&<>()/\{}'`~!@#$%A-Za-z0-9*|+-]+
*/
FRAMECODE	    = \$
COMMENT             = #

%%
<YYINITIAL> {
    <<EOF>> {
        buf.setLength( 0 );
        return Types.EOF;
    }
    {GLOBALSTART}{WS}* {
        buf.setLength( 0 );
	    return Types.GLOBALSTART;
    }
    {DATASTART}{NON_WS}+ {
        buf.setLength( 0 );
	    buf.append( yytext().substring( 5 ) );
	    return Types.DATASTART;
    }
    {SAVEEND}{NON_WS}+ {
        buf.setLength( 0 );
	    buf.append( yytext().substring( 5 ) );
	    return Types.SAVESTART;
    }
    {SAVEEND}{WS}* {
        buf.setLength( 0 );
	    return Types.SAVEEND;
    }
    {LOOPSTART}{WS_NL}* {
        buf.setLength( 0 );
	    return Types.LOOPSTART;
    }
    {STOP}{WS}* {
        buf.setLength( 0 );
	    return Types.STOP;
    }
    {COMMENT}.* {
        buf.setLength( 0 );
	    return Types.COMMENT;
    }
    {TAGNAME} {
        buf.setLength( 0 );
	    buf.append( yytext() );
	    return Types.TAGNAME;
    }
//    {FRAMECODE} {
    {FRAMECODE}{NON_WS}+ {
        buf.setLength( 0 );
	    buf.append( yytext().substring( 1 ) );
	    return Types.DVNFRAMECODE;
    }
    {SINGLESTART} {
//System.err.printf( "Matched %s in {SINGLESTART}\n", yytext() );
        buf.setLength( 0 );
	    yybegin( YYSINGLE );
    }
    {DOUBLESTART} {
        buf.setLength( 0 );
        yybegin( YYDOUBLE );
    }
    ^;.* {
//System.err.printf( "Matched %s in ^{SEMI}\n", yytext() );
        buf.setLength( 0 );
        buf.append( yytext().substring( 1 ) );
        yybegin( YYSEMI );
    }
    {NON_WS}+ {
//System.err.printf( "Matched %s in {NON_WS}+\n", yytext() );
        buf.setLength( 0 );
	    buf.append( yytext() );
	    return Types.DVNNON;
    }
    {WS_NL}+ {}
}

<YYSINGLE, YYDOUBLE> {
    . {
//System.err.printf( "Matched %s in {YYSINGLE|DOUBLE}, cont.\n", yytext() );
        buf.append( yytext() );
    }
}

<YYSINGLE>'/{WS_NL}+ {
//System.err.printf( "Matched %s in {YYSINGLE}, exit\n", yytext() );
    yybegin( YYINITIAL );
    return Types.DVNSINGLE;
}

<YYDOUBLE>\"/{WS_NL}+ {
    yybegin( YYINITIAL );
    return Types.DVNDOUBLE;
}

<YYSEMI> {
    {NL} {
//System.err.printf( "In YYSEMI, matched |%s|, begin SEMIEND\n", yytext() );
	yybegin( YYSEMIEND );
    }
    {NON_NL}+ {
//System.err.printf( "In YYSEMI, matched |%s|\n", yytext() );
        buf.append( yytext() );
    }
}

<YYSEMIEND> {
    ;/{WS_NL}+ {
//System.err.printf( "In YYSEMIEND, matched |%s|, exit\n", yytext() );
        yybegin( YYINITIAL );
        return Types.DVNSEMICOLON;
    }
    {NL} {
        buf.append( "\n" );
    }
    [^;] {
//System.err.printf( "In YYSEMIEND, matched |%s|, drop back\n", yytext() );
        buf.append( "\n" );
	    buf.append( yytext() );
	    yybegin( YYSEMI );
    }
}

<YYINITIAL,YYSINGLE,YYDOUBLE,YYSEMI,YYSEMIEND>.|\n {
//System.err.printf( "In ERROR, matched |%s|\n", yytext() );
    buf.setLength( 0 );
    buf.append( "Unknown token: ``" );
    buf.append( yytext() );
    buf.append( "''" );
    return Types.ERROR;
}