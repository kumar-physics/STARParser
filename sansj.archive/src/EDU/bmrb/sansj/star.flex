/* NMR-STAR lexer with some grammar checking */
/*
 * This software is copyright (c) 2002 Board of Regents, University of Wisconsin.
 * All Rights Reserved.
 *
 * FILE:        $Source: /cvs_archive/cvs/sansj/src/EDU/bmrb/sansj/star.flex,v $
 * 
 * AUTHOR:      $Author: dmaziuk $
 * DATE:        $Date: 2006/04/03 22:40:42 $
 * 
 * UPDATE HISTORY:
 * ---------------
 * $Log: star.flex,v $
 * Revision 1.1  2006/04/03 22:40:42  dmaziuk
 * bug fix in lex specs.
 *
 * Revision 1.6  2005/11/10 00:17:38  dmaziuk
 * *** empty log message ***
 *
 * Revision 1.5  2004/03/03 23:55:23  dmaziuk
 * removed unneeded 'keyword in value' warnings
 *
 * Revision 1.4  2004/03/03 23:11:45  dmaziuk
 * fixed a bug in comment parsing
 *
 * Revision 1.3  2003/05/16 00:57:53  dmaziuk
 * Fixed a bug in end-of-loop processing (CIFs don't have stop_)
 *
 * Revision 1.2  2003/01/23 01:36:43  dmaziuk
 * fixed comment parsing errors
 *
 * Revision 1.1.1.1  2003/01/10 22:01:19  dmaziuk
 * initial import
 *
 */

package EDU.bmrb.sansj;

%%

%class STARLexer
%public
%line
%column
%unicode
/* %eofclose */
%int
%state YYSINGLE YYDOUBLE YYSEMI
//%debug

%init{
    buf = new StringBuffer();
%init}

%{
    /* tokens */
    /** lexer error token */
    public static final int ERROR = 0;
    /** lexer warning token */
    public static final int WARNING = 1;
    /** start of global block token */
    public static final int GLOBALSTART = 2;
    /** start of data block token */
    public static final int DATASTART = 3;
    /** end of saveframe token */
    public static final int SAVEEND = 4;
    /** start of saveframe token */
    public static final int SAVESTART = 5;
    /** start of loop token */
    public static final int LOOPSTART = 6;
    /** end of loop token */
    public static final int STOP = 7;
    /** tag token */
    public static final int TAGNAME = 8;
    /** value in single quotes token */
    public static final int DVNSINGLE = 9;
    /** value in double quotes token */
    public static final int DVNDOUBLE = 10;
    /** value in semicolons token */
    public static final int DVNSEMICOLON = 11;
    /** framecode value token */
    public static final int DVNFRAMECODE = 12;
    /** bareword value token */
    public static final int DVNNON = 13;
    /** comment token */
    public static final int COMMENT = 14;
    /** token names. Use int token constant as array index */
    public static final String [] TOKEN_TYPES = {
    "ERROR",
    "WARNING",
    "GLOBALSTART",
    "DATASTART",
    "SAVEEND",
    "SAVESTART",
    "LOOPSTART",
    "STOP",
    "TAGNAME",
    "DVNSINGLE",
    "DVNDOUBLE",
    "DVNSEMICOLON",
    "DVNFRAMECODE",
    "DVNNON",
    "COMMENT" };
    /* buffer for quoted values */
    private StringBuffer buf;
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

//COMMENT=(#{NON_NL}+)
//COMMENT=(#.*{NL})
COMMENT=#

%% 

<YYINITIAL> {
    <<EOF>> { return YYEOF; }
    {GLOBALSTART} { return GLOBALSTART; }
    {DATASTART}{NON_WS}+ { return DATASTART; }
    {SAVEEND}{NON_WS}+ { return SAVESTART; }
    {SAVEEND} { return SAVEEND; }
    {LOOPSTART} { return LOOPSTART; }
    {STOP} { return STOP; }
    {COMMENT} {NON_NL}*  { return COMMENT; }  
    {TAGNAME} { return TAGNAME; }
    {FRAMECODE} { return DVNFRAMECODE; }
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
    {NON_WS}+ { return DVNNON; }
    {WS} { }
}

<YYSINGLE, YYDOUBLE> {
    ({WS}+|{NL}+)({GLOBALSTART} 
                 | {DATASTART}
                 | {SAVEEND}
                 | {LOOPSTART}
                 | {STOP})
    {
        buf.append( yytext() );
	return WARNING;
    }
  . { buf.append( yytext() ); }
}

<YYSINGLE> {
    ' / {WS}+ {
        yybegin( YYINITIAL ); 
        return DVNSINGLE;
    }
}

<YYDOUBLE> {
    \" / {WS}+ {
        yybegin( YYINITIAL ); 
        return DVNDOUBLE;
    }
}


<YYSEMI> {
    ({WS}+|{NL}+)({GLOBALSTART} 
                 | {DATASTART}
                 | {SAVEEND}
                 | {LOOPSTART}
                 | {STOP})
    {
        buf.append( yytext() );
	return WARNING;
    }
  .|\n { buf.append( yytext() ); }
}


<YYSEMI> {
    {SEMI} { 
        yybegin( YYINITIAL ); 
        return DVNSEMICOLON;
    }
}

[^] {
    buf.setLength( 0 );
    buf.append( "Invalid token: ``" );
    buf.append( yytext() );
    buf.append( "''" );
    return ERROR;
}