/*
 * $Id$
 *
 * This software is copyright (c) 2006 Board of Regents, University of Wisconsin.
 * All Rights Reserved.
 *
 */
#ifndef _SANSPARSER_H_
#define _SANSPARSER_H_

#include <string>
#include <istream>

#include "STARLexer.h"
#include "ErrorHandler.h"
#include "ContentHandler.h"

/**
 * \class SansParser
 * \brief Callback-based validating NMR-STAR parser.
 *
 * This parser generates errors if input is not an NMR-STAR v.3 file.
 * In NMR-STAR v.3:
 *  - global block is not allowed
 *  - only one data block is allowed
 *  - only saveframe(s) are allowed in the data block
 *  - nested loops are not allowed
 *  - loop terminators ("stop_") are mandatory
 *  - (coments are allowed anywhere in the file)
 * <p>
 * Warnings are generated for loop count errors and tags/keywords in
 * delimited values.
 * <p>
 * This parser returns tags and values in one callback.
 * <p>
 * <strong>Performance note:</strong><br>
 * the paser has separate subroutines for parsing saveframes and loops. The more
 * saveframes and/or loops your input data has, the more subroutine calls will be
 * generated during parsing. See also performance note in scanner's documentation.
 */
class SansParser {
  public:
    /**
     * \brief Default constructor.
     *
     * \param lex scanner
     * \param ch content handler
     * \param eh error handler
     */
    SansParser( STARLexer * lex = NULL, 
                 ContentHandler * ch = NULL, 
                 ErrorHandler * eh = NULL ) :
        fLex( lex ),
        fCh( ch ),
        fEh( eh )
    {}
    /**
     * \brief Copy-ctor.
     *
     * Makes shallow copy: copies pointers only.
     * \param other parser to copy
     */
    SansParser( SansParser& other ) :
        fLex( other.fLex ),
	fCh( other.fCh ),
	fEh( other.fEh )
    {}
    /**
     * \brief Changes scanner.
     *
     * \param lex scanner
     */
     void setScanner( STARLexer * lex ) { fLex = lex; }
    /**
     * \brief Changes error handler.
     *
     * \param eh error handler
     */
     void setHandler( ErrorHandler * eh ) { fEh = eh; }
    /**
     * \brief Changes content handler.
     *
     * \param ch content handler
     */
     void setHandler( ContentHandler * ch ) { fCh = ch; }
    /**
     * \brief Parses input.
     *
     * Parsing stops on EOF, critical error, or if user callback
     * returns true.
     */
    void parse();

  private:
    STARLexer * fLex;       ///< scanner
    ContentHandler * fCh;  ///< content andler
    ErrorHandler * fEh;     ///< error handler
    std::string fBlockName; ///< data block name
    std::string fSaveName;  ///< current saveframe name

    bool parseDataBlock();
    bool parseSaveFrame();
    bool parseLoop();
};

#endif // SANSPARSER_H
