<?php

interface ErrorHandler {
    public function criticalError( $line, $msg );
    public function error( $line, $msg );
    public function warning( $line, $msg );
}

interface ContentHandler {
    public function startData( $line, $id );
    public function endData( $line, $id );
    public function startSaveframe( $line, $id );
    public function endSaveframe( $line, $id );
    public function startLoop( $line );
    public function endLoop( $line );
    public function comment( $line, $text );
    public function data( $tag, $tagline, $val, $valline, $delim, $loop );
}

interface ContentHandler2 {
    public function startData( $line, $id );
    public function endData( $line, $id );
    public function startSaveframe( $line, $id );
    public function endSaveframe( $line, $id );
    public function startLoop( $line );
    public function endLoop( $line );
    public function comment( $line, $text );
    public function tag( $line, $tag );
    public function value( $line, $val, $delim );
}

class STARLexer {
// lexical states
    const YYINITIAL = 0;
    const YYSEMI = 3;
// tokens
    const GLOBALSTART = 1;
    const GLOBALEND = 2;
    const DATASTART = 3;
    const DATAEND = 4;
    const SAVESTART = 5;
    const SAVEEND = 6;
    const LOOPSTART = 7;
    const STOP = 8;
    const TAGNAME = 9;
    const DVNSINGLE = 10;
    const DVNDOUBLE = 11;
    const DVNSEMICOLON = 12;
    const DVNFRAMECODE = 13;
    const DVNNON = 14;
    const COMMENT = 15;
    const FILEEND = 16;
    const ERROR = -1;
    const WARNING = -3;

    private $lineno;
    private $filename;
    private $in;
    private $buffer = "";
    private $text;
    private $yystate;

    function __construct( $in ) {
        $this->in = $in;
    }
    function getLine() {
        if( ! isset( $this ) ) die( "Lexer not initialized\n" );
        return $this->lineno;
    }
    function getText() {
        if( ! isset( $this ) ) die( "Lexer not initialized\n" );
        return $this->text;
    }
    function yylex() {
        if( ! isset( $this ) ) die( "Lexer not initialized\n" );
        if( ! isset( $this->in ) ) die( "File not open\n" );
        $this->text = "";
        while( strlen( $this->buffer ) < 1 ) {
            $this->buffer = fgets( $this->in );
            if( feof( $this->in ) ) {
# if there was a trailing \n
                if( strlen( $this->buffer ) < 1 )
                    return self::FILEEND;
            }
            $this->buffer = trim( $this->buffer );
            $this->lineno++;
        }
        if( feof( $this->in ) ) {
# if there was a trailing \n
            if( strlen( $this->buffer ) < 1 )
                return self::FILEEND;
        }
        while( strlen( $this->buffer ) > 0 ) {
            switch( $this->yystate ) {
                case self::YYINITIAL :
                    $this->buffer = trim( $this->buffer );
                    if( strlen( $this->buffer ) < 1 ) continue;
// comment
                    if( preg_match( "/^#.*/i", $this->buffer ) == 1 ) {
                        $this->text = $this->buffer;
                        $this->buffer = "";
                        return self::COMMENT;
                    }
// global block
                    if( preg_match( "/^global_/i", $this->buffer ) == 1 ) {
                        $this->buffer = substr( $this->buffer, 7 );
                        $this->buffer = trim( $this->buffer );
                        return self::GLOBALSTART;
                    }
// data block
                    if( preg_match( "/^data_(.*)/i", $this->buffer, $matches ) == 1 ) {
                        $this->buffer = substr( $this->buffer, strlen( $matches[0] ) + 1 );
                        $this->text = $matches[1];
//echo "Block ID: $this->text\n";
                        $this->buffer = trim( $this->buffer );
                        return self::DATASTART;
                    }
// saveframe start
                    if( preg_match( "/^save_([^\s]+)/i", $this->buffer, $matches ) == 1 ) {
                        $this->buffer = substr( $this->buffer, strlen( $matches[0] ) + 1 );
                        $this->text = $matches[1];
//echo "SF ID: $this->text\n";
                        $this->buffer = trim( $this->buffer );
                        return self::SAVESTART;
                    }
// saveframe end
                    if( preg_match( "/^save_/i", $this->buffer ) == 1 ) {
                        $this->buffer = "";
                        return self::SAVEEND;
                    }
// loop start
                    if( preg_match( "/^loop_/i", $this->buffer ) == 1 ) {
                        $this->buffer = substr( $this->buffer, 5 );
                        $this->buffer = trim( $this->buffer );
                        return self::LOOPSTART;
                    }
// loop end
                    if( preg_match( "/^stop_/i", $this->buffer ) == 1 ) {
                        $this->buffer = substr( $this->buffer, 5 );
                        $this->buffer = trim( $this->buffer );
                        return self::STOP;
                    }
// tagname
                    if( preg_match( "/^_[_A-Za-z0-9]+[][_.A-Za-z0-9%-]+/", $this->buffer, $matches ) == 1 ) {
                        $this->buffer = substr( $this->buffer, strlen( $matches[0] ) + 1 );
                        $this->text = $matches[0];
                        $this->buffer = trim( $this->buffer );
                        return self::TAGNAME;
                    }
// double-quote
                    if( preg_match( "/^\".+/", $this->buffer ) == 1 ) { // dquote
//echo "* buffer is |" . $this->buffer . "|\n";
                        $this->buffer = substr( $this->buffer, 1 );
//echo "** buffer is |" . $this->buffer . "|\n";
                        if( (preg_match( "/\"\s/", $this->buffer, $matches, PREG_OFFSET_CAPTURE ) < 1)
                         && (preg_match( "/\"$/", $this->buffer, $matches, PREG_OFFSET_CAPTURE ) < 1) ) {
                            $this->text = "Unterminated double quote";
                            return self::ERROR;
                        }
//print_r( $matches );
                        $this->text = substr( $this->buffer, 0, $matches[0][1] );
                        $this->buffer = substr( $this->buffer, $matches[0][1] + 1 );
                        $this->buffer = trim( $this->buffer );
//echo "**** buffer is |" . $this->buffer . "|\n";
                        return self::DVNDOUBLE;
                    }
// single quote
                    if( preg_match( "/^\'.+/", $this->buffer ) == 1 ) { // squote
//echo "* buffer is |" . $this->buffer . "|\n";
                        $this->buffer = substr( $this->buffer, 1 );
//echo "** buffer is |" . $this->buffer . "|\n";
                        if( (preg_match( "/\'\s/", $this->buffer, $matches, PREG_OFFSET_CAPTURE ) < 1) 
                         && (preg_match( "/\'$/", $this->buffer, $matches, PREG_OFFSET_CAPTURE ) < 1)) {
                            $this->text = "Unterminated single quote";
                            return self::ERROR;
                        }
//print_r( $matches );
                        $this->text = substr( $this->buffer, 0, $matches[0][1] );
                        $this->buffer = substr( $this->buffer, $matches[0][1] + 1 );
                        $this->buffer = trim( $this->buffer );
//echo "**** buffer is |" . $this->buffer . "|\n";
                        return self::DVNSINGLE;
                    }
// bareword
//ORDER is important! If this comes after DVNSINGLE values like H' should work here
                    if( preg_match( "/^[^\n\s\"';][^\n\s]*/", $this->buffer, $matches ) == 1 ) { // bareword
//echo "* buffer matches bareword: |" . $this->buffer . "|\n";
                        $this->buffer = substr( $this->buffer, strlen( $matches[0] ) + 1 );
                        $this->buffer = trim( $this->buffer );
                        $this->text = $matches[0];
                        if( substr( $this->text, 0, 1 ) == '$' ) {
                            $this->text = substr( $this->text, 1 );
                            return self::DVNFRAMECODE;
                        }
                        return self::DVNNON;
                    }
// semicolon
                    if( preg_match( "/^;/", $this->buffer ) == 1 ) { // semi
//echo "**** buffer is |" . $this->buffer . "|, entering yysemi\n";
                        $this->yystate = self::YYSEMI;
                        $this->text = "";
                        break;
                    }
//echo "BUFFER: " . $this->buffer . "\n";
                    $this->text = $this->buffer;
                    return self::ERROR;
                case self::YYSEMI : 
//echo "!! In YYSEMI, buffer = |" . $this->buffer . "|\n";
                    $this->text .= substr( $this->buffer, 1 );
                    do {
                        $this->buffer = fgets( $this->in );
                        $this->lineno++;
//echo "!! read $this->buffer\n";
                        if( preg_match( "/^;/", $this->buffer ) == 1 ) {
                            $this->buffer = trim( substr( $this->buffer, 2 ) ); // otherwise "; " will generate an empty token
//echo "**** buffer is |" . $this->buffer . "|, text is |" . $this->text . "|, return to yyinitial\n";
                            $this->yystate = self::YYINITIAL;
                            return self::DVNSEMICOLON;
                        }
                        else {
                            $this->text .= $this->buffer;
                            $this->buffer = "";
                        }
                    } while( ! feof( $this->in ) );
                    break;
            } // endswitch
        } // endwhile strlen
    } // end yylex()
}

?>
