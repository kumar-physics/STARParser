/*
 * TestLoopParser.java
 *
 * Created on June 14, 2004, 3:45 PM
 */

package EDU.bmrb.sansj;

/**
 * Test LoopParser
 * @author  dmaziuk
 */
public class TestLoopParser implements ContentHandler, ErrorHandler {
//******************************************************************************    
    /** Creates a new instance of TestLoopParser
     */
    public TestLoopParser() {
    } //************************************************************************
    /** Main method
    * @param args the command line arguments
    */
    public static void main (String args[]) {
        try {
            long start = System.currentTimeMillis();
            java.io.InputStream in;
            if( args.length < 1 ) in = System.in;
            else in = new java.io.FileInputStream( args[0] );
            java.io.BufferedReader reader = new java.io.BufferedReader( 
            new java.io.InputStreamReader( in ) );
            STARLexer lex = new STARLexer( reader );
            TestLoopParser t = new TestLoopParser();
            LoopParser p = new LoopParser( lex, t, t );
            p.parse();
            System.out.println( "Elapsed: " + (System.currentTimeMillis() - start) );
        }
        catch( Exception e ) {
            System.err.println( e.getMessage() );
            e.printStackTrace();
        }
    } //************************************************************************
    private void print( String str ) {
        System.out.print( str );
    } //************************************************************************
    private void print( int num ) {
        System.out.print( num );
    } //************************************************************************
    private void println( String str ) {
        print( str );
        print( "\n" );
    } //************************************************************************
    private void println( int num ) {
        print( num );
        print( "\n" );
    } //************************************************************************
    public void error(int line, int col, String msg) {
        print( "Parse error in line " );
        print( line );
        print( ", column " );
        print( col );
        print( ": " );
        println( msg );
    } //************************************************************************
    public boolean warning(int line, int col, String msg) {
        print( "Parse warning in line " );
        print( line );
        print( ", column " );
        print( col );
        print( ": " );
        println( msg );
        return false;
    } //************************************************************************
    public boolean comment(int line, String text) {
        print( "# " );
        print( text );
        print( '(' );
        print( line );
        println( ')' );
        return false;
    } //************************************************************************
    public boolean startLoop(int line) {
        print( "Start of loop (" );
        print( line );
        println( ')' );
        return false;
    } //************************************************************************
    public boolean data(DataItemNode data) {
        print( "Value: " );
        print( data.getTag() );
        print( '(' );
        print( data.getTagLine() );
        print( ") " );
        print( data.getValue() );
        print( '(' );
        print( data.getValueLine() );
        println( ')' );
        return false;
    } //************************************************************************   
    public boolean endLoop(int line) {
        print( "End of loop (" );
        print( line );
        println( ')' );
        return false;
    } //************************************************************************
    public boolean startData(int line, String id) {
        println( "ERROR: start of data block" );
        return false;
    } //************************************************************************
    public void endData(int line, String id) {
        println( "ERROR: end of data block" );
    } //************************************************************************
    public boolean startSaveFrame(int line, String name) {
        println( "ERROR: start of saveframe" );
        return false;
    } //************************************************************************
    public boolean endSaveFrame(int line, String name) {
        println( "ERROR: end of saveframe" );
        return false;
    } //************************************************************************
}
