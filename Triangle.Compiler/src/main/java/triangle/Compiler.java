/*
 * @(#)Compiler.java                       
 * 
 * Revisions and updates (c) 2022-2025 Sandy Brownlee. alexander.brownlee@stir.ac.uk
 * 
 * Original release:
 *
 * Copyright (C) 1999, 2003 D.A. Watt and D.F. Brown
 * Dept. of Computing Science, University of Glasgow, Glasgow G12 8QQ Scotland
 * and School of Computer and Math Sciences, The Robert Gordon University,
 * St. Andrew Street, Aberdeen AB25 1HG, Scotland.
 * All rights reserved.
 *
 * This software is provided free for educational use only. It may
 * not be used for commercial purposes without the prior written permission
 * of the authors.
 */

package triangle;

import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;
import triangle.abstractSyntaxTrees.Program;
import triangle.codeGenerator.Emitter;
import triangle.codeGenerator.Encoder;
import triangle.contextualAnalyzer.Checker;
import triangle.optimiser.ConstantFolder;
import triangle.optimiser.statisticalDataGeneratingVisitor;
import triangle.syntacticAnalyzer.Parser;
import triangle.syntacticAnalyzer.Scanner;
import triangle.syntacticAnalyzer.SourceFile;
import triangle.treeDrawer.Drawer;

/**
 * The main driver class for the Triangle compiler.
 */
public class Compiler {
	/** The filename for the object program, normally obj.tam. */
	//static String objectName = "obj.tam";
    //static boolean showTree = false;
//	static boolean folding = false;

	private static Scanner scanner;
	private static Parser parser;
	private static Checker checker;
	private static Encoder encoder;
	private static Emitter emitter;
	private static ErrorReporter reporter;
	private static Drawer drawer;
//    public static boolean toShowStats;


	/** The AST representing the source program. */
	private static Program theAST;

	/**
	 * Compile the source program to TAM machine code.
	 *
	 * @param sourceName   the name of the file containing the source program.
	 * @param objectName   the name of the file containing the object program.
	 * @param showingAST   true iff the AST is to be displayed after contextual
	 *                     analysis
	 * @param showingTable true iff the object description details are to be
	 *                     displayed during code generation (not currently
	 *                     implemented).
	 * @return true iff the source program is free of compile-time errors, otherwise
	 *         false.
	 */
	static boolean compileProgram(String sourceName, String objectName, boolean showingAST, boolean showingTable, boolean showTreeAfterFold) {

		System.out.println("********** " + "Triangle Compiler (Java Version 2.1)" + " **********");

		System.out.println("Syntactic Analysis ...");
		SourceFile source = SourceFile.ofPath(sourceName);

		if (source == null) {
			System.out.println("Can't access source file " + sourceName);
			System.exit(1);
		}

		scanner = new Scanner(source);
		reporter = new ErrorReporter(false);
		parser = new Parser(scanner, reporter);
		checker = new Checker(reporter);
		emitter = new Emitter(reporter);
		encoder = new Encoder(emitter, reporter);
		drawer = new Drawer();

		// scanner.enableDebugging();
		theAST = parser.parseProgram(); // 1st pass
		if (reporter.getNumErrors() == 0) {
			// if (showingAST) {
			// drawer.draw(theAST);
			// }
			System.out.println("Contextual Analysis ...");
			checker.check(theAST); // 2nd pass

            generateAppropriateTreeIfNeeded(showingAST, showTreeAfterFold);

            if (toShowStats){
            statisticalDataGeneratingVisitor getStats = new statisticalDataGeneratingVisitor();
            getStats.visitProgram(theAST,null);

            System.out.println("");
            System.out.println("Stats:");
            System.out.println("There are " + getStats.characterExpressionsCount + " Char expressions in " + sourceName + "!");
            System.out.println("There are " + getStats.integerExpressionsCount + " Int expressions in " + sourceName + "!");
            System.out.println("---------------------");
            System.out.println("");}

            if (reporter.getNumErrors() == 0) {
				System.out.println("Code Generation ...");
				encoder.encodeRun(theAST, showingTable); // 3rd pass
			}
		}

		boolean successful = (reporter.getNumErrors() == 0);
		if (successful) {
			emitter.saveObjectProgram(objectName);
			System.out.println("Compilation was successful.");
		} else {
			System.out.println("Compilation was unsuccessful.");
		}
		return successful;
	}

    private static void generateAppropriateTreeIfNeeded(boolean showingAST, boolean showTreeAfterFold) {
        if (showTreeAfterFold)
        {
            theAST.visit(new ConstantFolder());
            drawer.draw(theAST);
        }
        else if (folding)
        {
            theAST.visit(new ConstantFolder());
        }
    }

    /**
	 * Triangle compiler main program.
	 *
	 * @param args the only command-line argument to the program specifies the
	 *             source filename.
	 */

    @Argument(alias = "fn", description = "Give a name to the file containing the compiled code (by default it is obj.tam)", required = false)
    static String objectName = "obj.tam"; // this is the value if this is not specified.

    @Argument(alias = "st", description = "Provide an instruction to show the abstract syntax tree (by default it is set to false)", required = false)
    static boolean showTree = false;

    @Argument(alias = "fo", description = "Provide an instruction to do folding (by default it is set to false)", required = false)
    static boolean folding = false;

    @Argument(alias = "taf", description = "Provide an instruction to display the abstract syntax tree after folding (by default it is set to false)", required = false)
    static boolean showTreeAfterFold = false;

    @Argument(alias = "showStats", description = "Provide an instruction to display the program in questions statistics like the amount of Character and Integer (by default it is set to false)", required = false)
    public static boolean toShowStats = false;

    @Argument(alias = "hoisting", description = "Should hoisting be performed (by default it is false)", required = false)
    static boolean hoisting = false;

    public static void main(String[] args) {

        Compiler CompilerOBJ = new Compiler();
        Args.parseOrExit(CompilerOBJ, args);

        if (args.length < 1) {
			System.out.println("Usage: tc filename [-o=outputfilename] [tree] [folding]");
			System.exit(1);
		}



		//parseArgs(args);
		String sourceName = args[0];
		
		var compiledOK = compileProgram(sourceName, objectName, showTree, false, showTreeAfterFold);

		if (!showTree) {
			System.exit(compiledOK ? 0 : 1);
		}
	}
	
//	private static void parseArgs(String[] args) {
//		for (String s : args) {
//			var sl = s.toLowerCase();  //for each of the arguments, save a lowercase version of it in sl
//			if (sl.equals("tree")) { // if it equals "tree" then set show tree to tree or ...
//				showTree = true;
//			} else if (userIntention.equals("changeOBJName")) { //if it starts with "-o=" the objectName string is then set to the 3rd letter (inclusive) to the end
//				objectName = s.substring(3); //for now, I have commented this out as while testing its setting the name to be things it shouldn't be
//			} else if (sl.equals("folding")) { // if it is folding then we want to do folding
//				 = true;
//			}
//		}
//	}
}
