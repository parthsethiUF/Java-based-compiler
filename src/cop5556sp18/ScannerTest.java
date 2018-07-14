 /**
 * JUunit tests for the Scanner for the class project in COP5556 Programming Language Principles 
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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp18.Scanner.Kind;
import cop5556sp18.Scanner.LexicalException;
import cop5556sp18.Scanner.Token;
import static cop5556sp18.Scanner.Kind.*;

public class ScannerTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	//To make it easy to print objects and turn this output on and off
	static boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 *Retrieves the next token and checks that it is an EOF token. 
	 *Also checks that this was the last token.
	 *
	 * @param scanner
	 * @return the Token that was retrieved
	 */
	
	Token checkNextIsEOF(Scanner scanner) {
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF, token.kind);
		assertFalse(scanner.hasTokens());
		return token;
	}


	/**
	 * Retrieves the next token and checks that its kind, position, length, line, and position in line
	 * match the given parameters.
	 * 
	 * @param scanner
	 * @param kind
	 * @param pos
	 * @param length
	 * @param line
	 * @param pos_in_line
	 * @return  the Token that was retrieved
	 */
	Token checkNext(Scanner scanner, Scanner.Kind kind, int pos, int length, int line, int pos_in_line) {
		Token t = scanner.nextToken();
		assertEquals(kind, t.kind);
		assertEquals(pos, t.pos);
		assertEquals(length, t.length);
		assertEquals(line, t.line());
		assertEquals(pos_in_line, t.posInLine());
		return t;
	}

	/**
	 * Retrieves the next token and checks that its kind and length match the given
	 * parameters.  The position, line, and position in line are ignored.
	 * 
	 * @param scanner
	 * @param kind
	 * @param length
	 * @return  the Token that was retrieved
	 */
	Token checkNext(Scanner scanner, Scanner.Kind kind, int length) {
		Token t = scanner.nextToken();
		assertEquals(kind, t.kind);
		assertEquals(length, t.length);
		return t;
	}
	


	/**
	 * Simple test case with an empty program.  The only Token will be the EOF Token.
	 *   
	 * @throws LexicalException
	 */
	@Test
	public void testEmpty() throws LexicalException {
		String input = "";  //The input is the empty string.  This is legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		checkNextIsEOF(scanner);  //Check that the only token is the EOF token.
	}
	
	/**
	 * Test illustrating how to put a new line in the input program and how to
	 * check content of tokens.
	 * 
	 * Because we are using a Java String literal for input, we use \n for the
	 * end of line character. (We should also be able to handle \n, \r, and \r\n
	 * properly.)
	 * 
	 * Note that if we were reading the input from a file, the end of line 
	 * character would be inserted by the text editor.
	 * Showing the input will let you check your input is 
	 * what you think it is.
	 * 
	 * @throws LexicalException
	 */
	@Test
	public void testSemi() throws LexicalException {
		String input = ";;\n;;";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, SEMI, 0, 1, 1, 1);
		checkNext(scanner, SEMI, 1, 1, 1, 2);
		checkNext(scanner, SEMI, 3, 1, 2, 1);
		checkNext(scanner, SEMI, 4, 1, 2, 2);
		checkNextIsEOF(scanner);
	}
	

	
	/**
	 * This example shows how to test that your scanner is behaving when the
	 * input is illegal.  In this case, we are giving it an illegal character '~' in position 2
	 * 
	 * The example shows catching the exception that is thrown by the scanner,
	 * looking at it, and checking its contents before rethrowing it.  If caught
	 * but not rethrown, then JUnit won't get the exception and the test will fail.  
	 * 
	 * The test will work without putting the try-catch block around 
	 * new Scanner(input).scan(); but then you won't be able to check 
	 * or display the thrown exception.
	 * 
	 * @throws LexicalException
	 */
	




	


	@Test
	public void test1() throws LexicalException {
		String input = "0";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL, 0, 1, 1, 1);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void test2() throws LexicalException {
		String input = "123+";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL, 0, 3, 1, 1);// POS, LENGTH, LINE, POS_IN_LINE
		checkNext(scanner, OP_PLUS, 3, 1, 1, 4);// POS, LENGTH, LINE, POS_IN_LINE
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void test3() throws LexicalException {
		String input = "--;!!=/";
		Scanner in = new Scanner(input).scan();
		show(input);
		show(in);
		checkNext(in, OP_MINUS, 0, 1, 1, 1); // POS, LENGTH, LINE, POS_IN_LINE
		checkNext(in, OP_MINUS, 1, 1, 1, 2);
		checkNext(in, SEMI, 2, 1, 1, 3);
		checkNext(in, OP_EXCLAMATION, 3, 1, 1, 4);
		checkNext(in, OP_NEQ, 4, 2, 1, 5);
		checkNext(in, OP_DIV, 6, 1, 1, 7);
		checkNextIsEOF(in);
	}
	
	@Test
	public void test4() throws LexicalException {
		String input = "false";
		Scanner in = new Scanner(input).scan();
		show(input);
		show(in);
		checkNext(in, BOOLEAN_LITERAL, 0, 5, 1, 1); // POS, LENGTH, LINE, POS_IN_LINE
		checkNextIsEOF(in);
	}
	
	@Test
	public void test5() throws LexicalException {
		String input = "909==***";
		Scanner in = new Scanner(input).scan();
		show(input);
		show(in);
		checkNext(in, INTEGER_LITERAL, 0, 3, 1, 1); // POS, LENGTH, LINE, POS_IN_LINE
		checkNext(in, OP_EQ, 3, 2, 1, 4); // POS, LENGTH, LINE, POS_IN_LINE
		checkNext(in, OP_POWER, 5, 2, 1, 6); // POS, LENGTH, LINE, POS_IN_LINE
		checkNext(in, OP_TIMES, 7, 1, 1, 8); // POS, LENGTH, LINE, POS_IN_LINE
		checkNextIsEOF(in);
	}
	
	@Test
	public void test6() throws LexicalException {
		String input = "false?:===:";
		Scanner in = new Scanner(input).scan();
		show(input);
		show(in);
		checkNext(in, BOOLEAN_LITERAL, 0, 5, 1, 1); // POS, LENGTH, LINE, POS_IN_LINE
		checkNext(in, OP_QUESTION, 5, 1, 1, 6); // POS, LENGTH, LINE, POS_IN_LINE
		checkNext(in, OP_ASSIGN, 6, 2, 1, 7); // POS, LENGTH, LINE, POS_IN_LINE
		checkNext(in, OP_EQ, 8, 2, 1, 9); // POS, LENGTH, LINE, POS_IN_LINE
		checkNext(in, OP_COLON, 10, 1, 1, 11); // POS, LENGTH, LINE, POS_IN_LINE
		checkNextIsEOF(in);
	}
	
	
	@Test
	public void test7() throws LexicalException {
		String input = "(|{})>=>><<<";
		Scanner in = new Scanner(input).scan();
		show(input);
		show(in);
		checkNext(in, LPAREN, 0, 1, 1, 1); // POS, LENGTH, LINE, POS_IN_LINE
		checkNext(in, OP_OR, 1, 1, 1, 2); // POS, LENGTH, LINE, POS_IN_LINE
		checkNext(in, LBRACE, 2, 1, 1, 3); // POS, LENGTH, LINE, POS_IN_LINE
		checkNext(in, RBRACE, 3, 1, 1, 4); // POS, LENGTH, LINE, POS_IN_LINE
		checkNext(in, RPAREN, 4, 1, 1, 5); // POS, LENGTH, LINE, POS_IN_LINE
		checkNext(in, OP_GE, 5, 2, 1, 6); // POS, LENGTH, LINE, POS_IN_LINE
		checkNext(in, RPIXEL, 7, 2, 1, 8); // POS, LENGTH, LINE, POS_IN_LINE
		checkNext(in, LPIXEL, 9, 2, 1, 10); // POS, LENGTH, LINE, POS_IN_LINE
		checkNext(in, OP_LT, 11, 1, 1, 12); // POS, LENGTH, LINE, POS_IN_LINE
		checkNextIsEOF(in);
	}
	
	@Test
	public void test8() throws LexicalException {
		String input = "<=:>";
		Scanner in = new Scanner(input).scan();
		show(input);
		show(in);
		checkNext(in, OP_LE, 0, 2, 1, 1); // POS, LENGTH, LINE, POS_IN_LINE
		checkNext(in, OP_COLON, 2, 1, 1, 3); // POS, LENGTH, LINE, POS_IN_LINE
		checkNext(in, OP_GT, 3, 1, 1, 4); // POS, LENGTH, LINE, POS_IN_LINE
		checkNextIsEOF(in);
	}
	
	@Test
	public void test9() throws LexicalException {
		String input = "0.89";
		Scanner in = new Scanner(input).scan();
		show(input);
		show(in);
		checkNext(in, FLOAT_LITERAL, 0, 4, 1, 1); // POS, LENGTH, LINE, POS_IN_LINE
		checkNextIsEOF(in);
	}
	
	@Test
	public void test10() throws LexicalException {
		String input = "189.8956";
		Scanner in = new Scanner(input).scan();
		show(input);
		show(in);
		checkNext(in, FLOAT_LITERAL, 0, 8, 1, 1); // POS, LENGTH, LINE, POS_IN_LINE
		checkNextIsEOF(in);
	}
	
	@Test
	public void test11() throws LexicalException {
		String input = ".81239";
		Scanner in = new Scanner(input).scan();
		show(input);
		show(in);
		checkNext(in, FLOAT_LITERAL, 0, 6, 1, 1); // POS, LENGTH, LINE, POS_IN_LINE
		checkNextIsEOF(in);
	}
	
	
	@Test
	public void test12() throws LexicalException {
		String input = ";012\r\t\n\n\n00801019;\r;{<<?>>00.09538";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, SEMI, 0, 1, 1, 1);// POS, LENGTH, LINE, POS_IN_LINE
		checkNext(scanner, INTEGER_LITERAL, 1, 1, 1, 2);// POS, LENGTH, LINE, POS_IN_LINE
		checkNext(scanner, INTEGER_LITERAL, 2, 2, 1, 3);// tab taking 2 spaces
		checkNext(scanner, INTEGER_LITERAL, 9, 1, 5, 1);		
		checkNext(scanner, INTEGER_LITERAL, 10, 1, 5, 2);
		checkNext(scanner, INTEGER_LITERAL, 11, 6, 5, 3);
		checkNext(scanner, SEMI, 17, 1, 5, 9);
		checkNext(scanner, SEMI, 19, 1, 6, 1);
		checkNext(scanner, LBRACE, 20, 1, 6, 2);
		checkNext(scanner, LPIXEL, 21, 2, 6, 3);
		checkNext(scanner, OP_QUESTION, 23, 1, 6, 5);
		checkNext(scanner, RPIXEL, 24, 2, 6, 6);
		checkNext(scanner, INTEGER_LITERAL, 26, 1, 6, 8);
		checkNext(scanner, FLOAT_LITERAL, 27, 7, 6, 9);
		checkNextIsEOF(scanner);
	}

	@Test
	public void test13() throws LexicalException {
		String input = "0099.00030";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL, 0, 1, 1, 1);
		checkNext(scanner, INTEGER_LITERAL, 1, 1, 1, 2);
		checkNext(scanner, FLOAT_LITERAL, 2, 8, 1, 3);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void test13b() throws LexicalException {
		String input = "99.00030";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, FLOAT_LITERAL, 0, 8, 1, 1);
		checkNextIsEOF(scanner);
	}
	@Test
	public void test14() throws LexicalException {
		String input = "//**//";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, OP_DIV, 0, 1, 1, 1);// POS, LENGTH, LINE, POS_IN_LINE
		checkNext(scanner, OP_DIV, 5, 1, 1, 6);// POS, LENGTH, LINE, POS_IN_LINE

		checkNextIsEOF(scanner);
		
	}
	@Test
	public void comment_fail() throws LexicalException {
		String input = "/**";
		show(input);
		thrown.expect(LexicalException.class); 
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) { 
			show(e); 
			assertEquals(3, e.getPos()); 
			throw e; 
		}
	}
	
	@Test
	public void failIllegalChar() throws LexicalException {
		String input = ";;~";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //Catch the exception
			show(e);                    //Display it
			assertEquals(2,e.getPos()); //Check that it occurred in the expected position
			throw e;                    //Rethrow exception so JUnit will see it
		}
	}

	@Test
	public void failfloat() throws LexicalException {
		String input = "987.";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, FLOAT_LITERAL, 0, 4, 1, 1);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testParens() throws LexicalException {
		String input = "()";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, LPAREN, 0, 1, 1, 1);
		checkNext(scanner, RPAREN, 1, 1, 1, 2);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void test15() throws LexicalException {
		String input = "hello world";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, IDENTIFIER, 0, 5, 1, 1);
		checkNext(scanner, IDENTIFIER, 6, 5, 1, 7);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void test16() throws LexicalException {
		String input = "hello A0766";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, IDENTIFIER, 0, 5, 1, 1);
		checkNext(scanner, IDENTIFIER, 6, 5, 1, 7);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void test17() throws LexicalException {
		String input = "default_width";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, KW_default_width, 0, 13, 1, 1);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void test18() throws LexicalException {
		String input = "Z";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, KW_Z, 0, 1, 1, 1);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void test19() throws LexicalException {
		String input = "=";
		show(input);
		thrown.expect(LexicalException.class); // Tell JUnit to expect a
												// LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) { // Catch the exception
			show(e); // Display it
			assertEquals(1, e.getPos()); // Check that it occurred in the
											// expected position
			throw e; // Rethrow exception so JUnit will see it
		}
	}
	
	@Test
	public void test20() throws LexicalException {
		String input = "int a == 20;";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner,KW_int,0,3,1,1);
		checkNext(scanner,IDENTIFIER,4,1,1,5);
		checkNext(scanner,OP_EQ,6,2,1,7);
		checkNext(scanner,INTEGER_LITERAL,9,2,1,10);
		checkNext(scanner,SEMI,11,1,1,12);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void intpass() throws LexicalException {
		String input = "2147483647";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner,INTEGER_LITERAL,0,10,1,1);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void intfail() throws LexicalException {
		String input = "2147483648";
		show(input);
		thrown.expect(LexicalException.class); // Tell JUnit to expect a
												// LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) { // Catch the exception
			show(e); // Display it
			assertEquals(10, e.getPos()); // Check that it occurred in the
											// expected position
			throw e; // Rethrow exception so JUnit will see it
		}
	}
	@Test
	public void floatpass() throws LexicalException {
		String input = "2147483647.98";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner,FLOAT_LITERAL,0,13,1,1);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void test21() throws LexicalException {
		String input = "21.47483647.98";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner,FLOAT_LITERAL,0,11,1,1);
		checkNext(scanner,FLOAT_LITERAL,11,3,1,12);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void test22() throws LexicalException {
		String input = "filename";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, Kind.KW_filename, 0, 8, 1, 1);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void test24() throws LexicalException {
		String input = "true911falsewow\n\r\tnono\twrite\t9.**==";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, IDENTIFIER, 0, 15, 1, 1);// POS, LENGTH, LINE, POS_IN_LINE
		checkNext(scanner, IDENTIFIER, 18, 4, 3, 2);
		checkNext(scanner, Kind.KW_write, 23, 5, 3, 7);
		checkNext(scanner, FLOAT_LITERAL, 29, 2, 3, 13);
		checkNext(scanner, Kind.OP_POWER, 31, 2, 3, 15);
		checkNext(scanner, Kind.OP_EQ, 33, 2, 3, 17);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void test25() throws LexicalException {
		String input = "_";
		show(input);
		thrown.expect(LexicalException.class); 
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) { 
			show(e); 
			assertEquals(0, e.getPos()); 
			throw e; 
		}
	}
	
	@Test
	public void test26() throws LexicalException {
		String input = "cart_x\ncart_y\npolar_a\npolar_r";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, KW_cart_x, 0, 6, 1, 1);// POS, LENGTH, LINE, POS_IN_LINE
		checkNext(scanner, KW_cart_y, 7, 6, 2, 1);// POS, LENGTH, LINE, POS_IN_LINE
		checkNext(scanner, KW_polar_a, 14, 7, 3, 1);// POS, LENGTH, LINE, POS_IN_LINE
		checkNext(scanner, KW_polar_r, 22, 7, 4, 1);// POS, LENGTH, LINE, POS_IN_LINE
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void extra1() throws LexicalException {
		String input = "abc 9999999999919199199100100110101010100111.1 ";
		show(input);
		thrown.expect(LexicalException.class); 
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) { 
			show(e); 
			assertEquals(46, e.getPos()); 
			throw e; 
		}
	}

	
}
	

