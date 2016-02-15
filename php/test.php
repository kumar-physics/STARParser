<?php
require_once 'SansParser.php';

class TestParser implements ErrorHandler, ContentHandler {
    function criticalError( $line, $msg ) {
	echo "Critical error in line $line: $msg\n";
        return true;
    }
    function error( $line, $msg ) {
	echo "Error in line $line: $msg\n";
        return true;
    }
    function warning( $line, $msg ) {
	echo "Warning in line $line: $msg\n";
        return false;
    }
    function startData( $line, $id ) {
        echo "Start data block $id in line $line\n";
        return false;
    }
    function endData( $line, $id ) {
	echo "End data block $id in line $line\n";
    }
    function startSaveframe( $line, $id ) {
	echo "Start saveframe $id in line $line\n";
        return false;
    }
    function endSaveframe( $line, $id ) {
	echo "End saveframe $id in line $line\n";
        return false;
    }
    function startLoop( $line ) {
        echo "Start of loop in line $line\n";
        return false;
    }
    function endLoop( $line ) {
        echo "End of loop in line $line\n";
        return false;
    }
    function comment( $line, $text ) {
        echo "Comment $text in line $line\n";
        return false;
    }
    function data( $tag, $tagline, $val, $valline, $delim, $loop ) {
        echo "Tag/value pair $tag : $val in line $tagline : $valline (";
        if( $loop ) echo "loop";
        else echo "free";
        echo ")\n";
        return false;
    }
}

$t = new TestParser();
//$in = fopen( "bmr16651_3.str", "r" );
//$in = fopen( "/share/dmaziuk/websoft/conv/cyay1.txt", "r" );
$in = fopen( "../testfiles/bmr19459_3.str", "r" );
$lex = new STARLexer( $in );
$p = new SansParser( $lex, $t, $t );
$p->parse();
fclose( $in );

?>
