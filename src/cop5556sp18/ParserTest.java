 /**
 * JUunit tests for the Parser for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Spring 2018.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Spring 2018 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2018
 */

package cop5556sp18;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp18.Scanner.Kind;
import cop5556sp18.Parser;
import cop5556sp18.Scanner;
import cop5556sp18.AST.ASTNode;
import cop5556sp18.AST.Block;
import cop5556sp18.AST.Declaration;
import cop5556sp18.AST.Expression;
import cop5556sp18.AST.ExpressionBinary;
import cop5556sp18.AST.ExpressionIdent;
import cop5556sp18.AST.ExpressionIntegerLiteral;
import cop5556sp18.AST.StatementInput;
import cop5556sp18.AST.StatementShow;
import cop5556sp18.AST.LHS;
import cop5556sp18.AST.LHSIdent;
import cop5556sp18.AST.LHSPixel;
import cop5556sp18.AST.LHSSample;
import cop5556sp18.AST.PixelSelector;
import cop5556sp18.AST.Program;
import cop5556sp18.AST.Statement;
import cop5556sp18.AST.StatementAssign;
import cop5556sp18.AST.StatementWrite;
import cop5556sp18.Parser.SyntaxException;
import cop5556sp18.Scanner.LexicalException;
import cop5556sp18.Scanner.Token;
import jdk.nashorn.internal.runtime.ParserException;

import static cop5556sp18.Scanner.Kind.*;

public class ParserTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	//To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}


	//creates and returns a parser for the given input.
	private Parser makeParser(String input) throws LexicalException {
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);
		return parser;
	}
	

	
	/**
	 * Simple test case with an empty program.  This throws an exception 
	 * because it lacks an identifier and a block
	 *   
	 * @throws LexicalException
	 * @throws SyntaxException 
	 */
	@Test
	public void testEmpty() throws LexicalException, SyntaxException {
		String input = "";  //The input is the empty string.  
		thrown.expect(SyntaxException.class);
		Parser parser = makeParser(input);
		@SuppressWarnings("unused")
		Program p = parser.parse();
	}
	
	/**
	 * Smallest legal program.
	 *   
	 * @throws LexicalException
	 * @throws SyntaxException 
	 */
	@Test
	public void testSmallest() throws LexicalException, SyntaxException {
		String input = "b{}";  
		Parser parser = makeParser(input);
		Program p = parser.parse();
		show(p);
		assertEquals("b", p.progName);
		assertEquals(0, p.block.decsOrStatements.size());
	}	
	
	
	/**
	 * Checks that an element in a block is a declaration with the given type and name.
	 * The element to check is indicated by the value of index.
	 * 
	 * @param block
	 * @param index
	 * @param type
	 * @param name
	 * @return
	 */
	Declaration checkDec(Block block, int index, Kind type,
			String name) {
		ASTNode node = block.decOrStatement(index);
		assertEquals(Declaration.class, node.getClass());
		Declaration dec = (Declaration) node;
		assertEquals(type, dec.type);
		assertEquals(name, dec.name);
		return dec;
	}	
	

	
	/** This test illustrates how you can test specific grammar elements by themselves by
	 * calling the corresponding parser method directly, instead of calling parse.
	 * This requires that the methods are visible (not private). 
	 * 
	 * @throws LexicalException
	 * @throws SyntaxException
	 */
	
	
	@Test
	public void testDec0() throws LexicalException, SyntaxException {
		String input = "b{int c; image j;}";
		Parser parser = makeParser(input);
		Program p = parser.parse();
		show(p);	
		checkDec(p.block, 0, Kind.KW_int, "c");
		checkDec(p.block, 1, Kind.KW_image, "j");
	}
	
	
	

	
	@Test
	public void testExpression1() throws LexicalException, SyntaxException {
		String input = "+ 2";
		Parser parser = makeParser(input);
		Expression e = parser.expression();  //call expression here instead of parse
		show(e);	
		assertEquals(e.toString(),"ExpressionUnary [op=OP_PLUS, expression=ExpressionIntegerLiteral [value=2]]");
		
	}
	
	@Test
	public void testExpression2() throws LexicalException, SyntaxException {
		String input = "- 2";
		Parser parser = makeParser(input);
		Expression e = parser.expression();  //call expression here instead of parse
		show(e);	
		assertEquals(e.toString(),"ExpressionUnary [op=OP_MINUS, expression=ExpressionIntegerLiteral [value=2]]");

	}
	@Test
	public void testExpression3() throws LexicalException, SyntaxException {
		String input = "x**+2";
		Parser parser = makeParser(input);
		Expression e = parser.expression();  //call expression here instead of parse
		show(e);	
		assertEquals(e.toString(),"ExpressionBinary [leftExpression=ExpressionIdent [name=x], op=OP_POWER, rightExpression=ExpressionUnary [op=OP_PLUS, expression=ExpressionIntegerLiteral [value=2]]]");

	}
	
	@Test
	public void testExpression4() throws LexicalException, SyntaxException {
		String input = "+x";
		Parser parser = makeParser(input);
		Expression e = parser.expression();  //call expression here instead of parse
		show(e);	
		assertEquals(e.toString(),"ExpressionUnary [op=OP_PLUS, expression=ExpressionIdent [name=x]]");

	}
	@Test
	public void testExpression5() throws LexicalException, SyntaxException {
		String input = "+0.0";
		Parser parser = makeParser(input);
		Expression e = parser.expression();  //call expression here instead of parse
		show(e);	
		assertEquals(e.toString(), 
				"ExpressionUnary [op=OP_PLUS, expression=ExpressionFloatLiteral [value=0.0]]");

	}
	
	
	@Test
	public void testExpression6() throws LexicalException, SyntaxException {
		String input = "+ z";
		Parser parser = makeParser(input);
		Expression e = parser.expression();  //call expression here instead of parse
		show(e);	
		assertEquals(e.toString(),"ExpressionUnary [op=OP_PLUS, expression=ExpressionIdent [name=z]]");

	}
	
	@Test
	public void testExpression7() throws LexicalException, SyntaxException {
		String input = "++2";
		Parser parser = makeParser(input);
		Expression e = parser.expression();  //call expression here instead of parse
		show(e);	
		assertEquals(e.toString(),"ExpressionUnary [op=OP_PLUS, expression=ExpressionUnary [op=OP_PLUS, expression=ExpressionIntegerLiteral [value=2]]]");

	}
	
	@Test
	public void testExpression8() throws LexicalException, SyntaxException {
		String input = "-+2";
		Parser parser = makeParser(input);
		Expression e = parser.expression();  //call expression here instead of parse
		show(e);	
		assertEquals(e.toString(),
				"ExpressionUnary [op=OP_MINUS, expression=ExpressionUnary [op=OP_PLUS, expression=ExpressionIntegerLiteral [value=2]]]");

	}
	
	@Test
	public void testExpression9() throws LexicalException, SyntaxException {
		String input = "(a/b)*(c+d)%(3)";
		Parser parser = makeParser(input);
		Expression e = parser.expression();  //call expression here instead of parse
		show(e);	
		assertEquals(e.toString(),"ExpressionBinary [leftExpression=ExpressionBinary [leftExpression=ExpressionBinary [leftExpression=ExpressionIdent [name=a], op=OP_DIV, rightExpression=ExpressionIdent [name=b]], op=OP_TIMES, rightExpression=ExpressionBinary [leftExpression=ExpressionIdent [name=c], op=OP_PLUS, rightExpression=ExpressionIdent [name=d]]], op=OP_MOD, rightExpression=ExpressionIntegerLiteral [value=3]]");

	}
	
	@Test
	public void testExpression10() throws LexicalException, SyntaxException {
		String input = "(a/b)*(c+d)%(3)";
		Parser parser = makeParser(input);
		Expression e = parser.expression();  //call expression here instead of parse
		show(e);	
		assertEquals(e.toString(),"ExpressionBinary [leftExpression=ExpressionBinary [leftExpression=ExpressionBinary [leftExpression=ExpressionIdent [name=a], op=OP_DIV, rightExpression=ExpressionIdent [name=b]], op=OP_TIMES, rightExpression=ExpressionBinary [leftExpression=ExpressionIdent [name=c], op=OP_PLUS, rightExpression=ExpressionIdent [name=d]]], op=OP_MOD, rightExpression=ExpressionIntegerLiteral [value=3]]");

	}
	
	@Test
	public void testExpression11() throws LexicalException, SyntaxException {
		String input = "a**b**c*d/e%f+g*d-d*e<a+b>c+d<=e+f>=g+h==c!=d";
		Parser parser = makeParser(input);
		Expression e = parser.expression();  //call expression here instead of parse
		show(e);	
		assertEquals(e.toString(),"ExpressionBinary [leftExpression=ExpressionBinary [leftExpression=ExpressionBinary [leftExpression=ExpressionBinary [leftExpression=ExpressionBinary [leftExpression=ExpressionBinary [leftExpression=ExpressionBinary [leftExpression=ExpressionBinary [leftExpression=ExpressionBinary [leftExpression=ExpressionBinary [leftExpression=ExpressionBinary [leftExpression=ExpressionBinary [leftExpression=ExpressionIdent [name=a], op=OP_POWER, rightExpression=ExpressionBinary [leftExpression=ExpressionIdent [name=b], op=OP_POWER, rightExpression=ExpressionIdent [name=c]]], op=OP_TIMES, rightExpression=ExpressionIdent [name=d]], op=OP_DIV, rightExpression=ExpressionIdent [name=e]], op=OP_MOD, rightExpression=ExpressionIdent [name=f]], op=OP_PLUS, rightExpression=ExpressionBinary [leftExpression=ExpressionIdent [name=g], op=OP_TIMES, rightExpression=ExpressionIdent [name=d]]], op=OP_MINUS, rightExpression=ExpressionBinary [leftExpression=ExpressionIdent [name=d], op=OP_TIMES, rightExpression=ExpressionIdent [name=e]]], op=OP_LT, rightExpression=ExpressionBinary [leftExpression=ExpressionIdent [name=a], op=OP_PLUS, rightExpression=ExpressionIdent [name=b]]], op=OP_GT, rightExpression=ExpressionBinary [leftExpression=ExpressionIdent [name=c], op=OP_PLUS, rightExpression=ExpressionIdent [name=d]]], op=OP_LE, rightExpression=ExpressionBinary [leftExpression=ExpressionIdent [name=e], op=OP_PLUS, rightExpression=ExpressionIdent [name=f]]], op=OP_GE, rightExpression=ExpressionBinary [leftExpression=ExpressionIdent [name=g], op=OP_PLUS, rightExpression=ExpressionIdent [name=h]]], op=OP_EQ, rightExpression=ExpressionIdent [name=c]], op=OP_NEQ, rightExpression=ExpressionIdent [name=d]]");

	}
	
	@Test
	public void testExpression12() throws LexicalException, SyntaxException, ParserException {
		String input = "sin x";
		thrown.expect(SyntaxException.class);  //Tell JUnit to expect a LexicalException
		
			Parser parser = makeParser(input);
			Expression ee = parser.expression();  //call expression here instead of parse
			show(ee);
		
	
	}

	
}
	

