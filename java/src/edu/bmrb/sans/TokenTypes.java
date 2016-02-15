/*
 * Copyright (c) 2005 Board of Regents, University of Wisconsin.
 *  All Rights Reserved.
 */
package edu.bmrb.sans;

/**
 * Token type map for compatibility backwards.
 * @author D. Maziuk
 */

/*
 * Created by IntelliJ IDEA.
 * User: dmaziuk
 * Date: Dec 8, 2005
 * Time: 6:42:14 PM
 *
 * $Source: /cvs_archive/cvs/starlibs5/sans/src/edu/bmrb/sans/TokenTypes.java,v $
 * $Author: dmaziuk $
 * Initial import: $Date: 2006/03/07 22:51:52 $
 * Update history:
 * ---------------
 * $Log: TokenTypes.java,v $
 * Revision 1.2  2006/03/07 22:51:52  dmaziuk
 * Javadoc cleanup
 *
 * Revision 1.1  2005/12/14 23:04:49  dmaziuk
 * refactoring: moved things to different package
 * */

public class TokenTypes {
    /* sansj tokens */
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
//*******************************************************************************
    /** Returns sansj token (int constant) for sans token (enum).
     * @param tok token
     * @return corresp. sans token.
     */
    public static int getSansjTokenType( STARLexer.Types tok ) {
        switch( tok ) {
            case ERROR : return TokenTypes.ERROR;
            case WARNING : return TokenTypes.WARNING;
            case GLOBALSTART : return TokenTypes.GLOBALSTART;
            case DATASTART : return TokenTypes.DATASTART;
            case SAVEEND : return TokenTypes.SAVEEND;
            case SAVESTART : return TokenTypes.SAVESTART;
            case LOOPSTART : return TokenTypes.LOOPSTART;
            case STOP : return TokenTypes.STOP;
            case TAGNAME : return TokenTypes.TAGNAME;
            case DVNSINGLE : return TokenTypes.DVNSINGLE;
            case DVNDOUBLE : return TokenTypes.DVNDOUBLE;
            case DVNSEMICOLON : return TokenTypes.DVNSEMICOLON;
            case DVNFRAMECODE : return TokenTypes.DVNFRAMECODE;
            case DVNNON : return TokenTypes.DVNNON;
            case COMMENT : return TokenTypes.COMMENT;
            case DATAEND : return STARLexer.YYEOF;
        }
        return -2; // never happens
    } //*************************************************************************
    /** Returns sansj token name for sans token.
     * @param tok token
     * @return corresp. sans token.
     */
    public static String getSansjTokenName( STARLexer.Types tok ) {
        return tok.toString();
    } //*************************************************************************
    /** Returns starlibj token (int constant) for sans token (enum).
     * @param tok token
     * @return corresp. starlibj token or -1.
     */
    public static int getStarlibjTokenType( STARLexer.Types tok ) {
        switch( tok ) {
            case DVNNON : return 0;
            case DVNDOUBLE : return 1;
            case DVNSINGLE : return 2;
            case DVNSEMICOLON : return 3;
            case DVNFRAMECODE : return 4;
        }
        return -1; // "don't care" -- starlibj does not have other tokens
    } //*************************************************************************
    /**
     * Main method.
     *
     * @param args command-line arguments.
     */
    public static void main( String [] args ) {
        System.out.printf( "DATAEND (YYEOF) : %d\n", STARLexer.YYEOF );
        for( int i = 0; i < STARLexer.Types.values().length; i++ )
            System.out.printf( "%s : %d\n", STARLexer.Types.values()[i], i );
    } //*************************************************************************
}
