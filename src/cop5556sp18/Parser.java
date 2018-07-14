package cop5556sp18;
/* *
 * Initial code for SimpleParser for the class project in COP5556 Programming Language Principles 
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


import cop5556sp18.Scanner.Token;
import cop5556sp18.AST.*;
import cop5556sp18.Scanner.Kind;
import static cop5556sp18.Scanner.Kind.*;

import java.util.ArrayList;
import java.util.List;


public class Parser {
	
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}



	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}


	public Program parse() throws SyntaxException {
		Program p = null;
		p = program();
		matchEOF();
		return p;
	}

	/*
	 * Program ::= Identifier Block
	 */
	public Program program() throws SyntaxException {
		Program p = null;
		Block b = null;
		Token temp = t;
		match(IDENTIFIER);
		//System.out.println("progr00000000000000000000000000000000000000000000000000000000000000000");

		b = block();
		//System.out.println("program11111111111111111111111111111111111111111111111111111111111111111111111");

		p = new Program(temp, temp ,b);
		return p;
	}
	
	/*
	 * Block ::=  { (  (Declaration | Statement) ; )* }
	 */
	
	Kind[] firstDec = { KW_int, KW_boolean, KW_image, KW_float, KW_filename };
	Kind[] firstStatement = {KW_input, IDENTIFIER, KW_write , KW_red, KW_green, KW_blue, KW_alpha, KW_while, KW_if, KW_show, KW_sleep  };
	Kind[] color = {KW_red, KW_green, KW_blue, KW_alpha};
	Kind[] predefined_name = {KW_Z, KW_default_width, KW_default_height};
	Kind[] function_name = {KW_sin, KW_cos, KW_atan, KW_abs, KW_log, KW_cart_x, KW_cart_y, KW_polar_a, KW_polar_r, KW_int, KW_float, KW_width, KW_height, KW_red, KW_green, KW_blue, KW_alpha};
	
	
	
	public Block block() throws SyntaxException {
		Token temp = t;
		ArrayList<ASTNode> l=new ArrayList<ASTNode>(); 
		Block b = null;
		match(LBRACE);
		//System.out.println("block0"+l);

		while (isKind(firstDec)|isKind(firstStatement)) {
			//System.out.println("first"+l);
	     if (isKind(firstDec)) {
			l.add(declaration());
	    	 //System.out.println("decc+l");

		} else if (isKind(firstStatement)) {
			l.add(statement());
	    	 //System.out.println("state"+l);

		}
	    
    	 //System.out.println("before semi"+l);
    	 match(SEMI);
	    	 //System.out.println("last");

		}
   	 //System.out.println("exit");

		match(RBRACE);
		//System.out.println("block1" + l);

		b = new Block(temp, l);
		return b;
	}
	
	public Declaration declaration() throws SyntaxException {
		//TODO
		Token temp = t;
		int i = 0;
		Declaration d = null;
		
		Expression e1 = null, e2 = null; 
		if(isKind(KW_image)) i = 1;
		if(isKind(firstDec)) {
			
			consume();
			Token n = match(IDENTIFIER);
			if(i == 1) {
				if(isKind(LSQUARE)) {
				match(LSQUARE);
				//System.out.println("e1");
				e1 = expression();
				//System.out.println("e11");
				match(COMMA);
				//System.out.println("e12");
				//System.out.println("e2");
				e2 = expression();
				match(RSQUARE);
				}
			}
			//System.out.println("return dec");
			d = new Declaration(temp, temp, n, e1,e2);
			return d;
		}
		else
			throw new SyntaxException(t, "wrong Declaration");
		
	}
	
	public Statement statement() throws SyntaxException {
		//TODO
		Statement s = null;
		if(isKind(firstStatement)) {
			switch(t.kind) {
			case KW_input: {
				s = statement_input();
			}
			break;
			case KW_write: {
				s = statement_write();
			}
			break;
			case KW_while: {
				s = statement_while();
			}
			break;
			case KW_if: {
				s = statement_if();
			}
			break;
			case KW_show: {
				s = statement_show();
			}
			break;
			case KW_sleep: {
				s = statement_sleep();
			}
			break;
			default: {
				s = statement_assignment();
			}
			break;
			}
			return s;
		}
		else
			throw new UnsupportedOperationException();
		
	}

	
	public Statement statement_assignment() throws SyntaxException {
		LHS l = null;
		Expression e = null;
		Token temp = t;
		l = LHS();
		match(OP_ASSIGN);
		e = expression();
		Statement s = new StatementAssign(temp, l, e);
		return s;
	}
	
	
	public Statement statement_input() throws SyntaxException {
		Statement s = null;
		Token temp = t;
		if(t.kind.equals(KW_input)) {
			consume();
			Token destName = match(IDENTIFIER);
			match(KW_from);
			match(OP_AT);
			Expression e =  expression();
			s = new StatementInput(temp, destName, e);
			return s;
		}
		else {
			throw new SyntaxException(t, "wrong statement input");
		}
		
	}
	
	public Statement statement_write() throws SyntaxException {
		Statement s = null;
		Token temp = t;
		if(t.kind.equals(KW_write)) {
			consume();
			Token sourceName = match(IDENTIFIER);
			match(KW_to);
			Token destName =	match(IDENTIFIER);
			s = new StatementWrite(temp, sourceName, destName);
			return s;
		}
		else {
			throw new SyntaxException(t, "wrong statement write");
		}
		
	}
	
	public Statement statement_while() throws SyntaxException {
		Statement s = null;
		Token temp = t;
		if(isKind(KW_while)) {
			consume();
			match(LPAREN);
			Expression e = expression();
			match(RPAREN);
			Block b = block();
			s = new StatementWhile(temp, e, b);
			return s;
		}
		else {
			throw new SyntaxException(t, "wrong statement while");
		}
		
	}
	
	
	public Statement statement_if() throws SyntaxException {
		Statement s = null;
		Token temp = t;
		if(isKind(KW_if)) {
			consume();
			match(LPAREN);
			Expression e = expression();
			match(RPAREN);
			Block b = block();
			s = new StatementIf(temp, e, b);
			return s;
		}
		else {
			throw new SyntaxException(t, "wrong statement if");
		}
		
	}
	
	public Statement statement_show() throws SyntaxException {
		Statement s = null;
		Token temp = t;
		if(t.kind.equals(KW_show)) {
			consume();
			Expression e = expression();
			s = new StatementShow(temp, e);
			return s;
		}
		else {
			throw new SyntaxException(t, "wrong statement show");
		}
		
	}
	
	public Statement statement_sleep() throws SyntaxException {
		Statement s = null;
		Token temp = t;
		if(t.kind.equals(KW_sleep)) {
			consume();
			Expression e = expression();
			s = new StatementSleep(temp, e);
			return s;
		}
		else {
			throw new SyntaxException(t, "wrong statement sleep");
		}
	
	}
	
	
	public LHS LHS() throws SyntaxException {
		LHS l = null;
		Token temp = t;
		if(isKind(IDENTIFIER)) {
			
			consume();
			if(t.kind.equals(OP_ASSIGN)){
				l = new LHSIdent(temp, temp);
				return l;
			}
			else if(t.kind.equals(LSQUARE)) {
				PixelSelector p = pixelselector();
				l = new LHSPixel(temp, temp, p); 
				return l;
			}
			
			
		}
		else if(isKind(color)) {
			consume();
			match(LPAREN);
			Token t2 = match(IDENTIFIER);
			PixelSelector p = pixelselector();
			match(RPAREN);
			l = new LHSSample(temp, t2, p, temp);
			return l;
		}
		else {
			throw new SyntaxException(t, "wrong statement LHS");
		}
		return l;
	
	}
	
	public PixelSelector pixelselector() throws SyntaxException {
		PixelSelector p = null;
		Token temp = t;
		Expression e = null;
		Expression e2 = null;
		match(LSQUARE);
		e = expression();
		match(COMMA);
		e2 = expression();
		match(RSQUARE);
		p = new PixelSelector(temp, e, e2);
		return p;
	}

	public Expression expression() throws SyntaxException {
		Token temp =t;
		
		Expression e1 = null;
		Expression e2 = null;
		Expression e3 = null;
		e1 = orexpression();
		//System.out.println( "lalala\n");
		if(isKind(OP_QUESTION)) {
		match(OP_QUESTION);
		e2 = expression();
		match(OP_COLON);
		e3 = expression();
		return new ExpressionConditional(temp, e1, e2, e3);
		}
		//System.out.println("out of expression");
		return e1;
	}
	
	public Expression orexpression() throws SyntaxException {
		Token t1 = t, t2 = null;
		Expression e0 = null, e1 = null;
		//System.out.println( "lalala1\n");
		e0 = andexpression();
		//System.out.println( "lalala2\n");
		
		while(isKind(OP_OR)) {
			t2 = t;
			consume();
			e1 = andexpression();
			e0 = new ExpressionBinary(t1, e0, t2, e1);
		}
		return e0;
	}
	
	
	public Expression andexpression() throws SyntaxException{
		Token t1 = t, t2 = null;
		//System.out.println( "lalala3\n");
		Expression e0 = eqexpression(), e1 = null;
		//System.out.println( "lalala4\n");
		
		
		while(isKind(OP_AND)) {
			t2 = t;
			consume();
			e1 = eqexpression();
			e0 = new ExpressionBinary(t1, e0, t2, e1);
		}
		return e0;
	}
	
	public Expression eqexpression() throws SyntaxException{
		Token first = t, op = null;
		//System.out.println( "lalala5\n");
		Expression e0 = relexpression(), e1 = null;
		//System.out.println( "lalala6\n");
		
		while(isKind(OP_EQ) || isKind(OP_NEQ)) {
			op = t;
			consume();
			e1 = relexpression();
			e0 = new ExpressionBinary(first, e0, op, e1);
		}
		return e0;
	}
	
	public Expression relexpression() throws SyntaxException{
		Token t1 = t, t2 = null;
		Expression e0 = addexpression(), e1 = null;
		
		while(isKind(OP_GT) || isKind(OP_LT) || isKind(OP_GE) || isKind(OP_LE)) {
			t2 = t;
			consume();
			e1 = addexpression();
			e0 = new ExpressionBinary(t1, e0, t2, e1);
		}
		return e0;
	}
	
	public Expression addexpression() throws SyntaxException{
		Token t1 = t, t2 = null;
		Expression e0 = multexpression(), e1 = null;
		
		while(isKind(OP_PLUS) || isKind(OP_MINUS)) {
			t2 = t;
			consume();
			e1 = multexpression();
			e0 = new ExpressionBinary(t1, e0, t2, e1);
		}
		return e0;
	}
	
	public Expression multexpression() throws SyntaxException{
		Token t1 = t, t2 = null;
		Expression e0 = powerexpression(), e1 = null;
		
		while(isKind(OP_TIMES) || isKind(OP_DIV) || isKind(OP_MOD) ) {
			t2 = t;
			consume();
			e1 = powerexpression();
			e0 = new ExpressionBinary(t1, e0, t2, e1);
		}
		return e0;
	}
	
	
	public Expression powerexpression() throws SyntaxException{
		Token t1 = t, t2 = null;
		//System.out.println( "lalala;popop9\n");
		Expression e0 = unaryexpression(), e1 = null;
		//System.out.println( "lalalapoppop0\n");
		
		if(isKind(OP_POWER)) {
			t2 = t;
			consume();
			e1 = powerexpression();
			e0 = new ExpressionBinary(t1, e0, t2, e1);
		}
		return e0;
	}
	
	public Expression unaryexpression() throws SyntaxException{
		Token temp = t;
		Expression e = null;
		Token t2 = null;
		
		if(isKind(OP_PLUS) || isKind(OP_MINUS)) {
			t2 = t;
			consume();
			//System.out.println( "unary\n");
			e = unaryexpression();
			//System.out.println( "unary0\n");
			return new ExpressionUnary(temp, t2, e);
		}
		
		else {
			//System.out.println( "unary0111\n");
			e = unaryexpressionnotplusminus();
			//System.out.println( "unary1\n");
		}
		//System.out.println( "unary2\n");
		return e;
	}
	
	public Expression unaryexpressionnotplusminus() throws SyntaxException{
		Token temp = t, t2 = null;
		Expression e = null;
		if(isKind(OP_EXCLAMATION)) {
			t2 = t;
			consume();
			//System.out.println( "unarynot+-00\n");
			e = unaryexpression();
			return new ExpressionUnary(temp, t2, e);
			//System.out.println( "unarynot+-01\n");
		}
		else {
			//System.out.println( "unarynot+-0\n");
			e = primary();
			//return new ExpressionUnary(temp, t2, e);
			//System.out.println( "unarynot+-1\n");
		}
		//System.out.println( "unarynot+-2\n");
		return e;
	}
	
	public Expression primary() throws SyntaxException{
		Expression e = null;
		Token temp = t;
		Token t2 = null;
		if(isKind(INTEGER_LITERAL) || isKind(BOOLEAN_LITERAL) || isKind(FLOAT_LITERAL)) {
			if(isKind(INTEGER_LITERAL)) {
				t2 = t;
				consume();
				return new ExpressionIntegerLiteral(temp, t2);
			}
			if(isKind(BOOLEAN_LITERAL)) {
				t2 = t;
				consume();
				return new ExpressionBooleanLiteral(temp, t2);
			}
			if(isKind(FLOAT_LITERAL)) {
				t2 = t;
				consume();
				return new ExpressionFloatLiteral(temp, t2);
			}
			
		}
		else if(isKind(IDENTIFIER)) {
			t2 = t;
			consume();
			if(isKind(LSQUARE)) {
				PixelSelector p = pixelselector();
				return new ExpressionPixel(temp, t2, p);
			}
			return new ExpressionIdent(temp, t2);
		}
		else if(isKind(Kind.LPAREN)) {
			consume();
			Expression ee = expression();
			
			match(Kind.RPAREN);
			return ee;
		}
		else if(isKind(function_name)) {
			e = functionapplication();
			return e;
		}
		else if(isKind(predefined_name)) {
			t2 = t;
			
			consume();
			return new ExpressionPredefinedName(temp, t2);
		}
		else if(isKind(LPIXEL)) {
			e = pixelconstructor();
			return e;
		}
		else
			throw new SyntaxException(t, "wrong primary");		
		return e;

	}
	
	
	public Expression functionapplication() throws SyntaxException{
		Expression e = null;
		Token temp = t, t2 = null;
		
		if(isKind(function_name)) {
			t2 = t;
			consume();
			if(isKind(Kind.LPAREN)) {
				consume();
				e = expression();
				match(RPAREN);
				return new ExpressionFunctionAppWithExpressionArg(temp, t2, e);
			}
			else if(isKind(Kind.LSQUARE)) {
				consume();
				e = expression();
				match(COMMA);
				Expression e2 = expression();
				match(Kind.RSQUARE);
				return new ExpressionFunctionAppWithPixel(temp, t2, e, e2);
			}
			else
				throw new SyntaxException(t, "wrong function application");
		}
		else
			throw new SyntaxException(t, "wrong function application");	
		
	}
	
	
	public ExpressionPixel pixelexpression() throws SyntaxException{
		Token temp = null;
		PixelSelector e = null;
		if(isKind(IDENTIFIER)) {
			temp = t;
			consume();
			e = pixelselector();
			return new ExpressionPixel(temp, temp, e);
		}
		else
			throw new SyntaxException(t, "wrong pixel expression");		
		
	}
	
	
	public ExpressionPixelConstructor pixelconstructor() throws SyntaxException{
		Token temp = t;
		Expression e1 = null, e2 = null, e3 = null, e4 = null;
		if(isKind(LPIXEL)) {
			consume();
			e1 = expression();
			match(COMMA);
			e2 = expression();
			match(COMMA);
			e3 = expression();
			match(COMMA);
			e4 = expression();
			match(RPIXEL);
			return new ExpressionPixelConstructor(temp, e1, e2, e3, e4);
		}
		else
			throw new SyntaxException(t, "wrong function application");		
	}
	
	
	
	protected boolean isKind(Kind kind) {
		return t.kind == kind;
	}

	protected boolean isKind(Kind... kinds) {
		for (Kind k : kinds) {
			if (k == t.kind)
				return true;
		}
		return false;
	}
	
	


	/**
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		Token tmp = t;
		//System.out.println("encountered "+t.kind+" expected "+ kind);
		if (isKind(kind)) {
			consume();
			return tmp;
		}
		//System.out.println("error");
		throw new SyntaxException(t,"encountered "+t.kind+" expected "+ kind); //TODO  give a better error message!
	}


	private Token consume() throws SyntaxException {
		Token tmp = t;
		if (isKind( EOF)) {
			throw new SyntaxException(t,"Syntax Error reached eof"); //TODO  give a better error message!  
			//Note that EOF should be matched by the matchEOF method which is called only in parse().  
			//Anywhere else is an error. */
		}
		t = scanner.nextToken();
		return tmp;
	}


	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (isKind(EOF)) {
			return t;
		}
		throw new SyntaxException(t,"Syntax Error"); //TODO  give a better error message!
	}
	

}

