/*
 * Example1.java
 *
 * Created on January 31, 2005, 1:16 PM
 */

package EDU.bmrb.sansj;

/**
 * Example code that extracts title and author names from BMRB entry.
 *
 * @author  dmaziuk
 */
public class Example1 implements ErrorHandler, ContentHandler {
    /** Entry title */
    private String Title = null;
    /** Author names */
    private java.util.List Authors = null;
    /** Buffer to store author's name */
    private StringBuffer Buf = null;
//******************************************************************************
    /** Creates a new instance of Example1 */
    public Example1() {
        Authors = new java.util.ArrayList();
        Buf = new StringBuffer();
    } //************************************************************************
    /** parse error */
    public void error(int line, int col, String msg) {
        System.err.println( "STAR syntax error in line " + line + ", col " + col + ": "
        + msg );
    } //************************************************************************
    /** parse warning */
    public boolean warning(int line, int col, String msg) {
        System.err.println( "Parser warning in line " + line + ", col " + col + ": "
        + msg );
        return false; // continue parsing
    } //************************************************************************
    /** comment -- do nothing */
    public boolean comment(int line, String text) {
        return false; // continue parsing
    } //************************************************************************
    /** start of data block -- do nothing */
    public boolean startData(int line, String id) {
        return false; // continue parsing
    } //************************************************************************
    /** start of saveframe -- do nothing */
    public boolean startSaveFrame(int line, String name) {
        return false; // continue parsing
    } //************************************************************************
    /** start of loop -- do nothing */
    public boolean startLoop(int line) {
        return false; // continue parsing
    } //************************************************************************
    /** Tag-value pair.
     * This callback gets tag/value pairs for both free and loop tags. Use
     * DataItemNode.isInLoop() to check.
     */
    public boolean data(DataItemNode data) {
        if( data.getName().equals( "_Entry_title" ) ) {
            Title = data.getValue();
            return false;
        }
        if( data.getName().equals( "_Author_family_name" ) ) {
            Buf.setLength( 0 );
            Buf.append( data.getValue() );
            Buf.append( ", " );
            return false;
        }
        if( data.getName().equals( "_Author_given_name" ) ) {
            Buf.append( data.getValue() );
            Authors.add( Buf.toString() );
        }
        return false; // continue parsing
    } //************************************************************************
    /** end of loop -- do nothing */
    public boolean endLoop(int line) {
        return false; // continue parsing
    } //************************************************************************
    /** end of saveframe.
     * If we have the data, quit.
     */
    public boolean endSaveFrame(int line, String name) {
        if( Title != null ) // title and author info are in the same SF, if we have
            return true;    // one we have the other too
        return false;
    } //************************************************************************
    /** End of data block */
    public void endData(int line, String id) {
    } //************************************************************************
    /** Parse */
    public void parse( java.io.Reader in ) {
        STARLexer lex = new STARLexer( in );
        SansParser p = new SansParser( lex, this, this );
        p.parse();
    } //************************************************************************
    /** Print out the results */
    public void print() {
        System.out.print( "Entry title: " );
        System.out.println( Title );
        System.out.println( "Authors: " );
        for( int i = 0; i < Authors.size(); i ++ )
            System.out.println( Authors.get( i ) );
        System.out.flush();
    } //************************************************************************
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            java.io.InputStream in;
            if( args.length < 1 ) in = System.in;
            else in = new java.io.FileInputStream( args[0] );
            Example1 e1 = new Example1();
            e1.parse( new java.io.BufferedReader( new java.io.InputStreamReader( in, "ISO-8859-1" ) ) );
            e1.print();
// parser closes input stream automagically
        }
        catch( Exception e ) {
            System.err.println( e );
            e.printStackTrace();
        }
    } //************************************************************************
    
    
    
    
    
    
    
    
    
    
    
}
