package cop5556sp18;

import cop5556sp18.Types;

import java.util.ArrayList;
import cop5556sp18.Scanner.Kind;
import cop5556sp18.Scanner.Token;
import cop5556sp18.Types.Type;
import cop5556sp18.AST.ASTNode;
import cop5556sp18.AST.ASTVisitor;
import cop5556sp18.AST.Block;
import cop5556sp18.AST.Declaration;
import cop5556sp18.AST.Expression;
import cop5556sp18.AST.ExpressionBinary;
import cop5556sp18.AST.ExpressionBooleanLiteral;
import cop5556sp18.AST.ExpressionConditional;
import cop5556sp18.AST.ExpressionFloatLiteral;
import cop5556sp18.AST.ExpressionFunctionAppWithExpressionArg;
import cop5556sp18.AST.ExpressionFunctionAppWithPixel;
import cop5556sp18.AST.ExpressionIdent;
import cop5556sp18.AST.ExpressionIntegerLiteral;
import cop5556sp18.AST.ExpressionPixel;
import cop5556sp18.AST.ExpressionPixelConstructor;
import cop5556sp18.AST.ExpressionPredefinedName;
import cop5556sp18.AST.ExpressionUnary;
import cop5556sp18.AST.LHSIdent;
import cop5556sp18.AST.LHSPixel;
import cop5556sp18.AST.LHSSample;
import cop5556sp18.AST.PixelSelector;
import cop5556sp18.AST.*;
import cop5556sp18.AST.Statement;
import cop5556sp18.AST.StatementAssign;
import cop5556sp18.AST.StatementIf;
import cop5556sp18.AST.StatementInput;
import cop5556sp18.AST.StatementShow;
import cop5556sp18.AST.StatementSleep;
import cop5556sp18.AST.StatementWhile;
import cop5556sp18.AST.StatementWrite;

public class TypeChecker implements ASTVisitor {
	
	//int temp_slot = 1;
	Symbol_table table;
	TypeChecker() {
		table = new Symbol_table();

	}

	@SuppressWarnings("serial")
	public static class SemanticException extends Exception {
		Token t;

		public SemanticException(Token t, String message) {
			super(message);
			this.t = t;
		}
	}

	
	
	// Name is only used for naming the output file. 
	// Visit the child block to type check program.
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		//System.out.println("in program");
		program.block.visit(this, arg);
		
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("in block");
		table.enterScope();
		for(ASTNode d : block.decsOrStatements){
			//System.out.println("in block000"+d);

			d.visit(this, null);
			//System.out.println("in blockout"+d);

		}
		//System.out.println("aaaaa");
		table.leaveScope();
		
		return null;
	}

	@Override
	public Object visitDeclaration(Declaration declaration, Object arg) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("in dec");
		
		//declaration.slotno = temp_slot++;
		String name = declaration.name;
		if(table.hm.containsKey(name)) {
			ArrayList<node> aa = table.hm.get(name);
			for(int i = 0; i < aa.size(); i++)
			{
				if(aa.get(i).scope == table.current_scope)
					throw new SemanticException(declaration.firstToken, "current scope exists");
			}
		}
		//System.out.println("ded"+declaration.type);
		declaration.ttype = Types.getType(declaration.type);
		//System.out.println("ttype = "+declaration.ttype);

		//System.out.println("--");
		Expression e0 = declaration.width;
		Expression e1 = declaration.height;
		
		Kind dkind = declaration.type;
		
		if(e0 != null)
		e0.visit(this, arg);
		if(e1 != null)
		e1.visit(this, arg);
		if(e0 == null && e1 == null) {
			table.insert(declaration.name, declaration);
			return null;
		}
		
		//System.out.println("e0"+e0.ttype+" e1"+e1.ttype);
		if(!(e0 == null || (e0.ttype == Type.INTEGER && Types.getType(dkind) == Type.IMAGE))) {
			//System.out.println("e0 null");
			throw new SemanticException(declaration.firstToken, "wrong expressions in declaration");
		}
		if(!(e1 == null || (e1.ttype == Type.INTEGER && Types.getType(dkind) == Type.IMAGE))){
			//System.out.println("e1 null");
			throw new SemanticException(declaration.firstToken, "wrong expressions in declaration");
		}
		
		if((e1 == null && e0 != null) || (e1 != null && e0 == null))
			throw new SemanticException(declaration.firstToken, "wrong expressions in declaration");

		//System.out.println(e0.ttype+"--"+e1.ttype+"--"+Types.getType(dkind));
		table.insert(declaration.name, declaration);
		return null;
	}

	@Override
	public Object visitStatementWrite(StatementWrite statementWrite, Object arg) throws Exception {
		// TODO Auto-generated method stub
		 
		Declaration sourcedec = table.lookup(statementWrite.sourceName);
		if(sourcedec == null) 
			throw new SemanticException(statementWrite.firstToken, "declaration in statement write == null");
		Declaration destdec = table.lookup(statementWrite.destName);
		if(destdec == null) 
			throw new SemanticException(statementWrite.firstToken, "declaration in statement write == null");
		statementWrite.source = sourcedec;
		statementWrite.dest = destdec;
		if(Types.getType(sourcedec.type) != Types.Type.IMAGE ) throw new SemanticException(statementWrite.firstToken,"source declaration type != image");
		if(Types.getType(destdec.type) != Types.Type.FILE ) throw new SemanticException(statementWrite.firstToken,"destination declaration type != file");
		statementWrite.source = sourcedec;
		statementWrite.dest = destdec;
		
		return null;
	}

	@Override
	public Object visitStatementInput(StatementInput statementInput, Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		Declaration dec = table.lookup(statementInput.destName);
		Expression e = statementInput.e;
		e.visit(this, arg);
		if(dec == null) 
			{
			throw new SemanticException(statementInput.firstToken, "declaration in statement input == null");
			}
		if(e.ttype != Types.Type.INTEGER) {
			throw new SemanticException(statementInput.firstToken, "expression in statement is not integer");
		}
			
		statementInput.dec = dec;
		return null;
		
	}

	@Override
	public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("u3ydbeqjsknlz");
		Expression e0 = pixelSelector.ex;
		Expression e1 = pixelSelector.ey;
		e0.visit(this, arg);
		e1.visit(this, arg);
		if(e0.ttype!= e1.ttype) 
				throw new SemanticException(pixelSelector.firstToken, "wrong pixel selector");
		if(!(e0.ttype == Types.Type.INTEGER || e0.ttype == Types.Type.FLOAT)) 
			throw new SemanticException(pixelSelector.firstToken, "wrong pixel selector no int or float");
		return null;
	}

	@Override
	public Object visitExpressionConditional(ExpressionConditional expressionConditional, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e0 = expressionConditional.guard;
		Expression e1 = expressionConditional.trueExpression;
		Expression e2 = expressionConditional.falseExpression;
		e0.visit(this, arg);
		e1.visit(this, arg);
		e2.visit(this, arg);
		if(e0.ttype != Types.Type.BOOLEAN) 
			throw new SemanticException(expressionConditional.firstToken,"e0 is not boolean in expression conditional");
		
		if(e1.ttype != e2.ttype) 
			throw new SemanticException(expressionConditional.firstToken,"e1 != e2 in expression conditional");
		
		expressionConditional.ttype = e1.ttype;
		return null;
	}

	@Override
	public Object visitExpressionBinary(ExpressionBinary expressionBinary, Object arg) throws Exception {
		Expression e0 = expressionBinary.leftExpression;
		Expression e1 = expressionBinary.rightExpression;
		//System.out.println("-------expressiombinary");
		e0.visit(this, arg);
		e1.visit(this, arg);
		Types.Type t = inferredtype(e0.ttype,e1.ttype, expressionBinary.op);
		//System.out.println("----------------------------"+t);
		if(t == Types.Type.NONE) throw new SemanticException(expressionBinary.firstToken,"no inferred type in expression binary");
		expressionBinary.ttype = t;
		return null;
	}

	@Override
	public Object visitExpressionUnary(ExpressionUnary expressionUnary, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e = expressionUnary.expression;
		
		e.visit(this, arg);
		
		
		expressionUnary.ttype = e.ttype;
		return null;
	}

	@Override
	public Object visitExpressionIntegerLiteral(ExpressionIntegerLiteral expressionIntegerLiteral, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		expressionIntegerLiteral.ttype = Type.INTEGER;
		return null;
	}

	@Override
	public Object visitBooleanLiteral(ExpressionBooleanLiteral expressionBooleanLiteral, Object arg) throws Exception {
		// TODO Auto-generated method stub
		expressionBooleanLiteral.ttype = Type.BOOLEAN;
		return null;
	}

	@Override
	public Object visitExpressionPredefinedName(ExpressionPredefinedName expressionPredefinedName, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		expressionPredefinedName.ttype = Type.INTEGER;
		return null;
	}

	@Override
	public Object visitExpressionFloatLiteral(ExpressionFloatLiteral expressionFloatLiteral, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		expressionFloatLiteral.ttype = Type.FLOAT;
		return null;
	}

	@Override
	public Object visitExpressionFunctionAppWithExpressionArg(
			ExpressionFunctionAppWithExpressionArg expressionFunctionAppWithExpressionArg, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		Expression e = expressionFunctionAppWithExpressionArg.e;
		e.visit(this, arg);
		Type t = e.ttype;
		
		Type tt = inferredtype(t, expressionFunctionAppWithExpressionArg.function);
		if( tt == Type.NONE) throw new SemanticException(expressionFunctionAppWithExpressionArg.firstToken,"none expression arg");
		expressionFunctionAppWithExpressionArg.ttype = tt;
		return null;
	}

	@Override
	public Object visitExpressionFunctionAppWithPixel(ExpressionFunctionAppWithPixel expressionFunctionAppWithPixel,
			Object arg) throws Exception {
		Expression e0 = expressionFunctionAppWithPixel.e0;
		Expression e1 = expressionFunctionAppWithPixel.e1;
		// TODO Auto-generated method stub
		e0.visit(this, arg);
		e1.visit(this, arg);
		if(expressionFunctionAppWithPixel.name == Kind.KW_cart_x || expressionFunctionAppWithPixel.name == Kind.KW_cart_y) {
			if(e0.ttype != Type.FLOAT) 
				throw new SemanticException(expressionFunctionAppWithPixel.firstToken,"wrong cart pixel");
			if(e1.ttype != Type.FLOAT) 
				throw new SemanticException(expressionFunctionAppWithPixel.firstToken,"wrong cart pixel");
			expressionFunctionAppWithPixel.ttype=Type.INTEGER;
			return null;
		}
		
		else if(expressionFunctionAppWithPixel.name == Kind.KW_polar_a || expressionFunctionAppWithPixel.name == Kind.KW_polar_r) {
			if(e0.ttype != Type.INTEGER) 
				throw new SemanticException(expressionFunctionAppWithPixel.firstToken,"wrong polar pixel");
			if(e1.ttype != Type.INTEGER) 
				throw new SemanticException(expressionFunctionAppWithPixel.firstToken,"wrong polar pixel");
			expressionFunctionAppWithPixel.ttype=Type.FLOAT;
			return null;
		}
		
		else 
			throw new SemanticException(expressionFunctionAppWithPixel.firstToken,"wrong polar pixel");

		
	}

	@Override
	public Object visitExpressionPixelConstructor(ExpressionPixelConstructor expressionPixelConstructor, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		Expression alpha = expressionPixelConstructor.alpha;
		Expression red = expressionPixelConstructor.red;
		Expression green = expressionPixelConstructor.green;
		Expression blue = expressionPixelConstructor.blue;
		alpha.visit(this, arg);
		red.visit(this, arg);
		blue.visit(this, arg);
		green.visit(this, arg);
		if(alpha.ttype != Type.INTEGER) 
			throw new SemanticException(expressionPixelConstructor.firstToken, "wrong alpha");
		if(red.ttype != Type.INTEGER) 
			throw new SemanticException(expressionPixelConstructor.firstToken, "wrong red");
		if(green.ttype != Type.INTEGER) 
			throw new SemanticException(expressionPixelConstructor.firstToken, "wrong green");
		if(blue.ttype != Type.INTEGER) 
			throw new SemanticException(expressionPixelConstructor.firstToken, "wrong blue");
		
		expressionPixelConstructor.ttype = Type.INTEGER;
		return null;
		
	}

	@Override
	public Object visitStatementAssign(StatementAssign statementAssign, Object arg) throws Exception {
		// TODO Auto-generated method stub
		LHS llhhss = statementAssign.lhs;
		Expression e = statementAssign.e;
		llhhss.visit(this, arg);
		e.visit(this, arg);
		
		if(llhhss.ttype != e.ttype )
			throw new SemanticException(statementAssign.firstToken, "statement assign wrong");
	
		return null;
	}
	Type[] lol = {Types.Type.INTEGER,Types.Type.BOOLEAN,Types.Type.FLOAT,Types.Type.IMAGE};
	protected boolean isKind(Type t) {
		for (Type k : lol) {
			if (k == t)
				return true;
		}
		return false;
	}
	@Override
	public Object visitStatementShow(StatementShow statementShow, Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		//statementShow.ttype = Types.getType(statementShow.firstToken.kind);
		
		Expression ees = statementShow.e;
		ees.visit(this, arg);
		Type t = ees.ttype;
		//System.out.println("in statment show"+t+"in---"+ees);
		if( ! (isKind(t)) ) {
			//System.out.println("in statment show");
			throw new SemanticException(statementShow.firstToken, "statement show wrong");
		}
		
		return null;
	}

	@Override
	public Object visitExpressionPixel(ExpressionPixel expressionPixel, Object arg) throws Exception {
		// TODO Auto-generated method stub
		expressionPixel.pixelSelector.visit(this, arg);
		Declaration dec = table.lookup(expressionPixel.name);
		if(dec== null) return null;
		if(Types.getType(dec.type) != Type.IMAGE) 
			throw new SemanticException(expressionPixel.firstToken,"wrong expression pixel");
		expressionPixel.ttype = Type.INTEGER;
		expressionPixel.dec = dec;
		return null;
	}

	@Override
	public Object visitExpressionIdent(ExpressionIdent expressionIdent, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Declaration dec = table.lookup(expressionIdent.name);
		if(dec == null ) throw new SemanticException(expressionIdent.firstToken,"wrong expression ident");
		
		Types.Type t = Types.getType(dec.type);
		expressionIdent.ttype = t;
		expressionIdent.dec = dec;
		return null;
	}

	@Override
	public Object visitLHSSample(LHSSample lhsSample, Object arg) throws Exception {
		// TODO Auto-generated method stub
		lhsSample.pixelSelector.visit(this, arg);
		Declaration dec = table.lookup(lhsSample.name);
		if(dec == null)
			throw new SemanticException(lhsSample.firstToken,"wrong lhsSample");
		if(Types.getType(dec.type) != Types.Type.IMAGE) 
			throw new SemanticException(lhsSample.firstToken,"not image in lhspixel");
		lhsSample.ttype = Types.Type.INTEGER;
		lhsSample.dec = dec;
		return null;
	}

	@Override
	public Object visitLHSPixel(LHSPixel lhsPixel, Object arg) throws Exception {
		// TODO Auto-generated method stub
		lhsPixel.pixelSelector.visit(this, arg);
		Declaration dec = table.lookup(lhsPixel.name);
		//lhsPixel.dec.visit(this, arg);
		if(dec == null) 
			throw new SemanticException(lhsPixel.firstToken,"wrong lhspixel");
		
		if(Types.getType(dec.type) != Types.Type.IMAGE) 
			throw new SemanticException(lhsPixel.firstToken,"not image in lhspixel");
		lhsPixel.ttype = Types.Type.INTEGER;
		lhsPixel.dec = dec;
		//System.out.println("in lhs pixel+++++++++++++++"+dec);
		return null;
	}

	@Override
	public Object visitLHSIdent(LHSIdent lhsIdent, Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		Declaration dec = table.lookup(lhsIdent.name);
		if(dec == null) throw new SemanticException(lhsIdent.firstToken,"wrong lhsident");
		
		lhsIdent.ttype = Types.getType(dec.type);
		lhsIdent.dec = dec;
		return null;
	}

	@Override
	public Object visitStatementIf(StatementIf statementIf, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e = statementIf.guard;
		e.visit(this, arg);
		statementIf.b.visit(this, arg);
		if(e.ttype != Types.Type.BOOLEAN) 
			throw new SemanticException(statementIf.firstToken, "statement if wrong");
	
		return null;
	}

	@Override
	public Object visitStatementWhile(StatementWhile statementWhile, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e = statementWhile.guard;
		e.visit(this, arg);
		statementWhile.b.visit(this, arg);

		if(e.ttype != Types.Type.BOOLEAN) throw new SemanticException(statementWhile.firstToken, "statement while wrong");
	
		return null;
	}

	@Override
	public Object visitStatementSleep(StatementSleep statementSleep, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e = statementSleep.duration;
		
		e.visit(this,arg);
		if(e.ttype != Types.Type.INTEGER) 
			throw new SemanticException(statementSleep.firstToken, "statement sleep wrong");
	
		return null;
	}


	public Type inferredtype(Type e0, Type e1, Kind op) {
		if(e0 == Types.Type.INTEGER && e1 == Types.Type.INTEGER && (op == Kind.OP_PLUS || op == Kind.OP_MINUS || op == Kind.OP_TIMES || op == Kind.OP_DIV || op == Kind.OP_MOD || op== Kind.OP_POWER || op == Kind.OP_AND || op == Kind.OP_OR)) {
			return Types.Type.INTEGER;
		}
		
		if(e0 == Types.Type.FLOAT && e1 == Types.Type.FLOAT && (op == Kind.OP_PLUS || op == Kind.OP_MINUS || op == Kind.OP_TIMES || op == Kind.OP_DIV ||  op== Kind.OP_POWER )) {
			return Types.Type.FLOAT;
		}
		
		if(e0 == Types.Type.FLOAT && e1 == Types.Type.INTEGER && (op == Kind.OP_PLUS || op == Kind.OP_MINUS || op == Kind.OP_TIMES || op == Kind.OP_DIV || op== Kind.OP_POWER )) {
			return Types.Type.FLOAT;
		}
		
		if(e0 == Types.Type.INTEGER && e1 == Types.Type.FLOAT && (op == Kind.OP_PLUS || op == Kind.OP_MINUS || op == Kind.OP_TIMES || op == Kind.OP_DIV || op== Kind.OP_POWER )) {
			return Types.Type.FLOAT;
		}
		
		if(e0 == Types.Type.BOOLEAN && e1 == Types.Type.BOOLEAN && (op == Kind.OP_AND || op == Kind.OP_OR)) {
			return Types.Type.BOOLEAN;
		}
		
		if(e0 == Types.Type.INTEGER && e1 == Types.Type.INTEGER && (op == Kind.OP_AND || op == Kind.OP_OR)) {
			return Types.Type.INTEGER;
		}
		
		if(e0 == Types.Type.INTEGER && e1 == Types.Type.INTEGER && (op == Kind.OP_EQ || op == Kind.OP_NEQ || op == Kind.OP_GT || op == Kind.OP_GE || op == Kind.OP_LT || op== Kind.OP_LE )) {
			return Types.Type.BOOLEAN;
		}
		
		if(e0 == Types.Type.FLOAT && e1 == Types.Type.FLOAT && (op == Kind.OP_EQ || op == Kind.OP_NEQ || op == Kind.OP_GT || op == Kind.OP_GE || op == Kind.OP_LT || op== Kind.OP_LE )) {
			return Types.Type.BOOLEAN;
		}
		
		if(e0 == Types.Type.BOOLEAN && e1 == Types.Type.BOOLEAN && (op == Kind.OP_EQ || op == Kind.OP_NEQ || op == Kind.OP_GT || op == Kind.OP_GE || op == Kind.OP_LT || op== Kind.OP_LE )) {
			return Types.Type.BOOLEAN;
		}
		
		else return Types.Type.NONE;
	}
	
	public Type inferredtype(Type e, Kind fun) {
		if(e == Types.Type.INTEGER && (fun == Kind.KW_abs || fun == Kind.KW_red || fun == Kind.KW_green || fun == Kind.KW_blue || fun == Kind.KW_alpha)) return Types.Type.INTEGER;
		
		if(e == Types.Type.FLOAT && (fun == Kind.KW_abs || fun == Kind.KW_sin || fun == Kind.KW_cos || fun == Kind.KW_atan || fun == Kind.KW_log)) return Types.Type.FLOAT;
		
		if(e == Types.Type.IMAGE && (fun == Kind.KW_width || fun == Kind.KW_height)) return Types.Type.INTEGER;
		
		if(e == Types.Type.INTEGER && fun == Kind.KW_float) return Types.Type.FLOAT;

		if(e == Types.Type.FLOAT && fun == Kind.KW_float) return Types.Type.FLOAT;
		
		if(e == Types.Type.FLOAT && fun == Kind.KW_int) return Types.Type.INTEGER;
		
		if(e == Types.Type.INTEGER && fun == Kind.KW_int) return Types.Type.INTEGER;
		
		else
			return Types.Type.NONE;

	}
	
	
}
