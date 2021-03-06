/*
 * This software is copyright (c) 2002 Board of Regents, University of Wisconsin.
 * All Rights Reserved.
 *
 * FILE:        $Source: /cvs_archive/cvs/starlibs5/sans/src/edu/bmrb/sans/star5.flex,v $
 * 
 * AUTHOR:      $Author: dmaziuk $
 * DATE:        $Date: 2005/12/19 23:47:38 $
 * 
 * UPDATE HISTORY:
 * ---------------
 * $Log: star5.flex,v $
 * Revision 1.3  2005/12/19 23:47:38  dmaziuk
 * refactoring, finished table gen. converter
 *
 * Revision 1.2  2005/12/14 23:04:50  dmaziuk
 * refactoring: moved things to different package
 *
 * Revision 1.1  2005/12/07 00:27:29  dmaziuk
 * Added v. 1.5 lexer
 *
 * Revision 1.1.1.1  2005/12/05 20:08:07  dmaziuk
 * initial import
 * */

package edu.bmrb.sans;
/**
 * NMR-STAR lexer with some grammar checking.
 *
 * <STRONG>Note:</STRONG> this scanner does not close input file on EOF.
 */
%%

%class STARLexer
%public
%line
%column
%unicode
%type Types
/*
 * use <<EOF>> rule instead
%eofval{
  return Types.DATAEND;
%eofval}
*/

%state YYSINGLE YYDOUBLE YYSEMI

/* %eofclose */
/* %debug */

%init{
    buf = new StringBuilder();
%init}

%{
    /** tokens */
    public enum Types {
        ERROR,
        WARNING,
        GLOBALSTART,
        DATASTART,
        DATAEND,
        SAVESTART,
        SAVEEND,
        LOOPSTART,
        STOP,
        TAGNAME,
        DVNSINGLE,
        DVNDOUBLE,
        DVNSEMICOLON,
        DVNFRAMECODE,
        DVNNON,
        COMMENT;
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
    /** Returns DVNSINGLE, DVNDOUBLE, or DVNSEMICOLON value text.
     *<P>
     * Because quoted values (DVNSINGLE, DVNDOUBLE, DVNSEMICOLON) are parsed
     * differently from non-quoted ones (DVNNON and DVNFRAMECODE), their
     * text is stored in a separate buffer.
     * <P>
     * Use <CODE>yytext()</CODE> to retrieve text of any token
     * other than DVNSINGLE, DVNDOUBLE, or DVNSEMICOLON value. Use 
     * <CODE>getText()</CODE> to retrieve text of DVNSINGLE, DVNDOUBLE, 
     * or DVNSEMICOLON token.
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

WS=[\n\r\ \t\b\012]
NON_WS=[^\n\r\ \t\b\012]
NL=[\r|\n|\r\n|\u2028|\u2029|\u000B|\u000C|\u0085]
NON_NL=[^\r|\n|\r\n|\u2028|\u2029|\u000B|\u000C|\u0085]

GLOBALSTART=([Gg][Ll][Oo][Bb][Aa][Ll]_)

DATASTART=([Dd][Aa][Tt][Aa]_)

SAVEEND=([Ss][Aa][Vv][Ee]_)

LOOPSTART=([Ll][Oo][Oo][Pp]_)

STOP=([Ss][Tt][Oo][Pp]_)

TAGNAME=(_{NON_WS}+)

SINGLESTART=({WS}+')

DOUBLESTART=({WS}+\")

SEMI=({NL};)

FRAMECODE=(\${NON_WS}+)

COMMENT=#

%% 

<YYINITIAL> {
    <<EOF>> { buf.setLength( 0 ); return Types.DATAEND; }
    {GLOBALSTART} { buf.setLength( 0 ); return Types.GLOBALSTART; }
    {DATASTART}{NON_WS}+ { buf.setLength( 0 ); return Types.DATASTART; }
    {SAVEEND}{NON_WS}+ { buf.setLength( 0 ); return Types.SAVESTART; }
    {SAVEEND} { buf.setLength( 0 ); return Types.SAVEEND; }
    {LOOPSTART} { buf.setLength( 0 ); return Types.LOOPSTART; }
    {STOP} { buf.setLength( 0 ); return Types.STOP; }
    {COMMENT} {NON_NL}*  { buf.setLength( 0 ); return Types.COMMENT; }
    {TAGNAME} { buf.setLength( 0 ); return Types.TAGNAME; }
    {FRAMECODE} { buf.setLength( 0 ); return Types.DVNFRAMECODE; }
    {SINGLESTART} { 
        buf.setLength( 0 );
        yybegin( YYSINGLE ); 
    }
    {DOUBLESTART} { 
        buf.setLength( 0 );
        yybegin( YYDOUBLE ); 
    }
    {SEMI} { 
        buf.setLength( 0 );
        yybegin( YYSEMI ); 
    }
    {NON_WS}+ { buf.setLength( 0 ); return Types.DVNNON; }
    {WS} { }
}

<YYSINGLE, YYDOUBLE, YYSEMI> {
    ({WS}+|{NL}+)({GLOBALSTART} 
                 | {DATASTART}
                 | {SAVEEND}
                 | {LOOPSTART}
                 | {STOP})
    {
        buf.append( yytext() );
	return Types.WARNING;
    }
  [^] { buf.append( yytext() ); }
}

<YYSINGLE> {
    ' / {WS}+ {
        yybegin( YYINITIAL ); 
        return Types.DVNSINGLE;
    }
}

<YYDOUBLE> {
    \" / {WS}+ {
        yybegin( YYINITIAL ); 
        return Types.DVNDOUBLE;
    }
}

<YYSEMI> {
    {SEMI} { 
        yybegin( YYINITIAL ); 
        return Types.DVNSEMICOLON;
    }
}
