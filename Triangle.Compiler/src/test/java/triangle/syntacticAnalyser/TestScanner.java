package triangle.syntacticAnalyser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import triangle.ErrorReporter;
import triangle.syntacticAnalyzer.Parser;
import triangle.syntacticAnalyzer.Scanner;
import triangle.syntacticAnalyzer.SourceFile;

public class TestScanner {
	
	/* some individual unit tests for helper methods in Scanner */

	@Test
	public void testIsDigit() {
		assertTrue(Scanner.isDigit('0'));
		assertTrue(Scanner.isDigit('1'));
		assertTrue(Scanner.isDigit('5'));
		assertTrue(Scanner.isDigit('8'));
		assertTrue(Scanner.isDigit('9'));
		assertFalse(Scanner.isDigit('a'));
		assertFalse(Scanner.isDigit('Z'));
		assertFalse(Scanner.isDigit('&'));
		assertFalse(Scanner.isDigit(';'));
		assertFalse(Scanner.isDigit('\n'));
	}
	
	@Test
	public void testIsOperator() {
		assertTrue(Scanner.isOperator('*'));
		assertTrue(Scanner.isOperator('/'));
		assertTrue(Scanner.isOperator('?'));
		assertTrue(Scanner.isOperator('+'));
		assertTrue(Scanner.isOperator('-'));
		assertFalse(Scanner.isOperator('a'));
		assertFalse(Scanner.isOperator('Z'));
		assertFalse(Scanner.isOperator('1'));
		assertFalse(Scanner.isOperator(';'));
		assertFalse(Scanner.isOperator('\n'));
	}
	
	
	/* these tests all try to compile example programs... */
	
	@Test
	public void testHi() {
		compileExpectSuccess("/hi.tri");
	}
	

	@Test
	public void testHiNewComment() {
		compileExpectSuccess("/hi-newcomment.tri");
	}
	

	@Test
	public void testHiNewComment2() {
        compileExpectSuccess("/hi-newcomment2.tri");
	}

    @Test
	public void testBarDemo() {
        compileExpectSuccess("/bardemo.tri");
	}

	@Test
	public void testRepeatUntil() {
        compileExpectSuccess("/repeatuntil.tri");
	}

    @Test
    public void testAdd() {
        compileExpectSuccess("/add.tri");
    }

    @Test
    public void testDouble()
    {
        compileExpectSuccess("/double.tri");
    }

    @Test
    public void testLoopwhile() //This tests if my amended compiler can compile a perfect version
    {
        compileExpectSuccess("/loopwhile.tri");
    }

    public void testLoopWhileCurly() //This tests if my amended compiler can compile the curly version
    {
        compileExpectSuccess("/while-curly.tri");
    }

    @Test
    public void testWhileWithMissingLeftCurly()
    {
        compileExpectFailure("/whileWithMissingLeftCurly.tri");
    }

    @Test
    public void testWhileWithMissingRightCurly()
    {
        compileExpectFailure("/whileWithMissingRightCurly.tri");
    }

    @Test
    public void testWhileWithMissingNestedLeftcurly()
    {
        compileExpectFailure("/whileWithMissingNestedLeftCurly.tri");
    }

    @Test
    public void testWhileWithMissingNestedRightCurly()
    {
        compileExpectFailure("/whileWithMissingNestedRightCurly.tri");
    }

//    @Test  // this was me testing my assumptions regarding the flaws of the tests!!!
//    public void testWhileWithMissingCurlys()
//    {
//        compileExpectSuccess("/whileWithNoCurlyies.tri"); //this passes these tests!!
//    }

    @Test
    public void testwhileWithNoNestedCurrlies()
    {
        compileExpectSuccess("/whileWithNoNestedCurrlies.tri");
    }

    @Test
    public void testWhileWithMissingOuterCurlyBrakets()
    {
        compileExpectSuccess("/whileWithMissingOuterCurlyBrakets.tri");
    }

    @Test
    public void testWhileWithTopBegin()
    {
        compileExpectSuccess("/whileWithTopBegin.tri"); //I assume mixing and matching of this kind should not be allowed.
    }

    @Test
    public void testWhileWithBottomEnd()
    {
        compileExpectFailure("/whileWithEndAtBottomInstedOf}.tri"); //I assume mixing and matching of this kind should not be allowed.
    }

    @Test
    public void testWhileWithBothBeginAndEnd()
    {
        compileExpectSuccess("/whileWithBothBeginAndEnd.tri"); //I have not implemented this kind of mixing and matching either as I think it would go against readability based on what I know from Clean Code.
    }

    @Test
    public void testWhileWithBothBeginAndEndInTheNestedLoop()
    {
        compileExpectSuccess("/whileWithBothBeginAndEndInTheNestedLoop.tri"); //I have not implemented this kind of mixing and matching either as I think it would go against readability based on what I know from Clean Code.
    }

    @Test
    public void testWhileWithJustBeginInTheNestedLoop()
    {
        compileExpectSuccess("/whileWithJustBeginInTheNestedLoop.tri"); //I have not implemented this kind of mixing and matching either as I think it would go against readability based on what I know from Clean Code.
    }

    @Test
    public void testWhileWithJustEndInTheNestedLoop()
    {
        compileExpectSuccess("/whileWithJustEndInTheNestedLoop.tri"); //I have not implemented this kind of mixing and matching either as I think it would go against readability based on what I know from Clean Code.
    }

    @Test
    public void testWhileWith2Begins()
    {
        compileExpectSuccess("/whileWith2Begins.tri"); //I have not implemented this kind of mixing and matching either as I think it would go against readability based on what I know from Clean Code.
    }


    private void compileExpectSuccess(String filename) {
		// build.gradle has a line sourceSets.test.resources.srcDir file("$rootDir/programs")
		// which adds the programs directory to the list of places Java can easily find files
		// getResource() below searches for a file, which is in /programs 
		//SourceFile source = SourceFile.ofPath(this.getClass().getResource(filename).getFile().toString());
		SourceFile source = SourceFile.fromResource(filename);
		
		Scanner scanner = new Scanner(source);
		ErrorReporter reporter = new ErrorReporter(true);
		Parser parser = new Parser(scanner, reporter);
		
		parser.parseProgram();
		
		// we should get to here with no exceptions
		
		assertEquals("Problem compiling " + filename, 0, reporter.getNumErrors());
	}
	
	private void compileExpectFailure(String filename) {
		//SourceFile source = SourceFile.ofPath(this.getClass().getResource(filename).getFile().toString());
		SourceFile source = SourceFile.fromResource(filename);
		Scanner scanner = new Scanner(source);
		ErrorReporter reporter = new ErrorReporter(true);
		Parser parser = new Parser(scanner, reporter);

		// we expect an exception here as the program has invalid syntax
		assertThrows(RuntimeException.class, new ThrowingRunnable() {
			public void run(){
				parser.parseProgram();
			}
		});
		
		// currently this program will fail
		assertNotEquals("Problem compiling " + filename, 0, reporter.getNumErrors());
	}

}
