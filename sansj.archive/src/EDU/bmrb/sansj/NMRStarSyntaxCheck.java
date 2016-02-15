/*
 * NMRStarSyntaxCheck.java
 *
 * Created on July 1, 2004, 2:12 PM
 */

package EDU.bmrb.sansj;

/**
 * NMR-STAR syntax checker.
 * Reports all syntax errors in an NMR-STAR file.
 * <P>
 * Usage: java EDU.bmrb.sansj.NMRStarSyntaxCheck [input file].
 * If input file is not specified, reads stdin. Error messages are printed
 * to stdout.
 * @author  dmaziuk
 */
public class NMRStarSyntaxCheck implements StarContentHandler, ErrorHandlerNS {
    /** error list */
    private EDU.bmrb.lib.ErrorList fErrs = null;
    /** buffer */
//    private StringBuffer fBuf = null;
//******************************************************************************
    /** Creates a new instance of NMRStarSyntaxCheck */
    public NMRStarSyntaxCheck() {
//        fBuf = new StringBuffer();
    } //************************************************************************
    /** Creates a new instance of NMRStarSyntaxCheck.
     * @param errs error list
     */
    public NMRStarSyntaxCheck( EDU.bmrb.lib.ErrorList errs ) {
        fErrs = errs;
//        fBuf = new StringBuffer();
    } //************************************************************************
    /** Main method.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            java.io.InputStream in;
            if( args.length < 1 ) in = System.in;
            else in = new java.io.FileInputStream( args[0] );
            java.io.BufferedReader reader = new java.io.BufferedReader( 
            new java.io.InputStreamReader( in ) );
            STARLexer lex = new STARLexer( reader );
            NMRStarSyntaxCheck t = new NMRStarSyntaxCheck( null );
            NMRStarParserNS p = new NMRStarParserNS( lex, t, t );
            p.parse();
        }
        catch( Exception e ) { 
            System.err.println( e.getMessage() );
            e.printStackTrace(); 
        }
    } //************************************************************************
    /** Parse error.
     * @param line line number
     * @param col column number
     * @return true to stop parsing
     */
    public boolean error(int line, int col, String msg) {
        if( fErrs != null )
            fErrs.add( true, -1, -1, line, msg );
        else {
            System.out.print( "Parse error in line " );
            System.out.print( line );
            System.out.print( ", column " );
            System.out.print( col );
            System.out.print( ": " );
            System.out.println( msg );
        }
        return false;
    } //************************************************************************
    /** Parse warning.
     * @param line line number
     * @param col column number
     * @return true to stop parsing
     */    
    public boolean warning(int line, int col, String msg) {
        if( fErrs != null )
            fErrs.add( false, -1, -1, line, msg );
        else {
            System.out.print( "Parse warning in line " );
            System.out.print( line );
            System.out.print( ", column " );
            System.out.print( col );
            System.out.print( ": " );
            System.out.println( msg );
        }
        return false;
    } //************************************************************************
    
    public boolean data(EDU.bmrb.sansj.starlib.DataValueNode node) {
        return false;
    }
    
    public void endData(EDU.bmrb.sansj.starlib.StarNode node) {
    }
    
    public boolean endLoop(EDU.bmrb.sansj.starlib.StarNode node) {
        return false;
    }
    
    public boolean endSaveFrame(EDU.bmrb.sansj.starlib.StarNode node) {
        return false;
    }
    
    public boolean startData(EDU.bmrb.sansj.starlib.StarNode node) {
        return false;
    }
    
    public boolean startLoop(EDU.bmrb.sansj.starlib.StarNode node) {
        return false;
    }
    
    public boolean startSaveFrame(EDU.bmrb.sansj.starlib.StarNode node) {
        return false;
    }
    
    public boolean tag(EDU.bmrb.sansj.starlib.DataNameNode node) {
        return false;
    }    
}
