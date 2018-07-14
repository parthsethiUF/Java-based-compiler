/**
 * Starter code for CodeGenerator.java used n the class project in COP5556 Programming Language Principles 
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



import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cop5556sp18.Types.Type;
import cop5556sp18.AST.ASTNode;
import cop5556sp18.AST.ASTVisitor;
import cop5556sp18.AST.Block;
import cop5556sp18.AST.Declaration;
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
import cop5556sp18.AST.Program;
import cop5556sp18.AST.StatementAssign;
import cop5556sp18.AST.StatementIf;
import cop5556sp18.AST.StatementInput;
import cop5556sp18.AST.StatementShow;
import cop5556sp18.AST.StatementSleep;
import cop5556sp18.AST.StatementWhile;
import cop5556sp18.AST.StatementWrite;
import cop5556sp18.Types;
import cop5556sp18.CodeGenUtils;
import cop5556sp18.Scanner.Kind;

public class CodeGenerator implements ASTVisitor, Opcodes {

	/**
	 * All methods and variable static.
	 */
	public int tempslot = 1;
	static final int Z = 255;

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;

	final int defaultWidth;
	final int defaultHeight;
	// final boolean itf = false;
	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 * @param defaultWidth
	 *            default width of images
	 * @param defaultHeight
	 *            default height of images
	 */
	public CodeGenerator(boolean DEVEL, boolean GRADE, String sourceFileName,
			int defaultWidth, int defaultHeight) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
		this.defaultWidth = defaultWidth;
		this.defaultHeight = defaultHeight;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		// TODO refactor and extend as necessary
		
		//System.out.println("visiting block");
		for (ASTNode node : block.decsOrStatements) {
			//System.out.println("INBLOCK----------------------in block" + node);
			node.visit(this, null);
		}
		return null;
	}

	@Override
	public Object visitBooleanLiteral(
			ExpressionBooleanLiteral expressionBooleanLiteral, Object arg)
			throws Exception {
		mv.visitLdcInsn(expressionBooleanLiteral.value);
		return null;
	}

	@Override
	public Object visitDeclaration(Declaration declaration, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("visiting dec");
		declaration.slotno = tempslot;
		tempslot = tempslot+1;
		if(declaration.ttype.equals(Type.IMAGE) ) {
			if(declaration.width != null && declaration.height != null) {
				//System.out.println("---------in declalalal");
				declaration.width.visit(this, arg);
				//System.out.println("-------------in decb");
				declaration.height.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className, "makeImage", RuntimeImageSupport.makeImageSig, false);
			}
			else
			{
				mv.visitLdcInsn(defaultWidth);
				mv.visitLdcInsn(defaultHeight);
				mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className, "makeImage", RuntimeImageSupport.makeImageSig, false);
			}
			mv.visitVarInsn(ASTORE, declaration.slotno);
		}
		
		
		return null;
	}

	@Override
	public Object visitExpressionBinary(ExpressionBinary expressionBinary,
			Object arg) throws Exception {

		Type type1 = expressionBinary.leftExpression.ttype;
		Type type2 = expressionBinary.rightExpression.ttype;
		Kind op = expressionBinary.op;
		Label load_true = new Label();
		Label end = new Label();
		Label finisher = new Label();
		
		switch(op) {
		case OP_PLUS:
			expressionBinary.leftExpression.visit(this, arg);
			expressionBinary.rightExpression.visit(this, arg);
			if(type1.equals(Types.Type.INTEGER) && type2.equals(Types.Type.INTEGER)){
				mv.visitInsn(IADD);
				}
			
			if(type1.equals(Types.Type.INTEGER) && type2.equals(Types.Type.FLOAT)){
				mv.visitInsn(SWAP);
				mv.visitInsn(I2F);
				mv.visitInsn(FADD);
				}
			
			if(type1.equals(Types.Type.FLOAT) && type2.equals(Types.Type.FLOAT)){
				mv.visitInsn(FADD);
				}	
			
			if(type1.equals(Types.Type.FLOAT) && type2.equals(Types.Type.INTEGER)){
				mv.visitInsn(I2F);
				mv.visitInsn(FADD);
				}
			break;	
		case OP_MINUS:
			expressionBinary.leftExpression.visit(this, arg);
			expressionBinary.rightExpression.visit(this, arg);
			if(type1.equals(Types.Type.INTEGER) && type2.equals(Types.Type.INTEGER)){
				mv.visitInsn(ISUB);
				}
			
			if(type1.equals(Types.Type.INTEGER) && type2.equals(Types.Type.FLOAT)){
				mv.visitInsn(SWAP);
				mv.visitInsn(I2F);
				mv.visitInsn(SWAP);
				mv.visitInsn(FSUB);
				}
			
			if(type1.equals(Types.Type.FLOAT) && type2.equals(Types.Type.FLOAT)){
				mv.visitInsn(FSUB);
				}
			
			if(type1.equals(Types.Type.FLOAT) && type2.equals(Types.Type.INTEGER)){
				mv.visitInsn(I2F);
				mv.visitInsn(FSUB);
				}
			break;
		case OP_DIV:
			expressionBinary.leftExpression.visit(this, arg);
			expressionBinary.rightExpression.visit(this, arg);
			if(type1.equals(Types.Type.INTEGER) && type2.equals(Types.Type.INTEGER)){
				mv.visitInsn(IDIV);
				}
			
			if(type1.equals(Types.Type.INTEGER) && type2.equals(Types.Type.FLOAT)){
				mv.visitInsn(SWAP);
				mv.visitInsn(I2F);
				mv.visitInsn(SWAP);
				mv.visitInsn(FDIV);
				}
			
			if(type1.equals(Types.Type.FLOAT) && type2.equals(Types.Type.FLOAT)){
				mv.visitInsn(FDIV);
				}
			
			if(type1.equals(Types.Type.FLOAT) && type2.equals(Types.Type.INTEGER)){
				mv.visitInsn(I2F);
				mv.visitInsn(FDIV);
				}
			break;
		case OP_TIMES:
			expressionBinary.leftExpression.visit(this, arg);
			expressionBinary.rightExpression.visit(this, arg);
			if(type1.equals(Types.Type.INTEGER) && type2.equals(Types.Type.INTEGER)){
				mv.visitInsn(IMUL);
				}
			
			if(type1.equals(Types.Type.INTEGER) && type2.equals(Types.Type.FLOAT)){
				mv.visitInsn(SWAP);
				mv.visitInsn(I2F);
				mv.visitInsn(FMUL);
				}
			
			if(type1.equals(Types.Type.FLOAT) && type2.equals(Types.Type.FLOAT)){
				mv.visitInsn(FMUL);
				}
			
			if(type1.equals(Types.Type.FLOAT) && type2.equals(Types.Type.INTEGER)){
				mv.visitInsn(I2F);
				mv.visitInsn(FMUL);
				}
			break;
		case OP_POWER:
			
			
			if(type1.equals(Types.Type.INTEGER) && type2.equals(Types.Type.INTEGER)){
				//System.out.println("====+++++++++=====i am op power");
				expressionBinary.leftExpression.visit(this, arg);
				mv.visitInsn(I2D);				
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(I2D);
				
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
				mv.visitInsn(D2I);
				}
			
			if(type1.equals(Types.Type.INTEGER) && type2.equals(Types.Type.FLOAT)){
				expressionBinary.leftExpression.visit(this, arg);
				mv.visitInsn(I2D);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(F2D);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
				mv.visitInsn(D2F);
				}
			
			if(type1.equals(Types.Type.FLOAT) && type2.equals(Types.Type.FLOAT)){
				expressionBinary.leftExpression.visit(this, arg);
				mv.visitInsn(F2D);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(F2D);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
				mv.visitInsn(D2F);
				}
			
			if(type1.equals(Type.FLOAT) && type2.equals(Type.INTEGER)){
				expressionBinary.leftExpression.visit(this, arg);
				mv.visitInsn(F2D);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(I2D);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
				mv.visitInsn(D2F);
				}
			break;
		case OP_AND:
			expressionBinary.leftExpression.visit(this, arg);
			expressionBinary.rightExpression.visit(this, arg);
			mv.visitInsn(IAND);
			break;
		case OP_OR:
			expressionBinary.leftExpression.visit(this, arg);
			expressionBinary.rightExpression.visit(this, arg);
			mv.visitInsn(IOR);
			break;
		case OP_MOD:
			expressionBinary.leftExpression.visit(this, arg);
			expressionBinary.rightExpression.visit(this, arg);
			mv.visitInsn(IREM);
			break;
		case OP_EQ:
			expressionBinary.leftExpression.visit(this, arg);
			expressionBinary.rightExpression.visit(this, arg);
			if(type1.equals(Types.Type.INTEGER) || type2.equals(Types.Type.BOOLEAN)){
				mv.visitJumpInsn(IF_ICMPNE, end);//if not equal jump
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(GOTO,finisher);
				mv.visitLabel(end);
				mv.visitLdcInsn(false);			
				}
			else{
				mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFNE, end);//if not equal jump
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(GOTO,finisher);
				mv.visitLabel(end);
				mv.visitLdcInsn(false);

				}
			
			break;
		case OP_NEQ:
			expressionBinary.leftExpression.visit(this, arg);
			expressionBinary.rightExpression.visit(this, arg);
			if(type1.equals(Types.Type.INTEGER) || type2.equals(Types.Type.BOOLEAN)){
				mv.visitJumpInsn(IF_ICMPEQ, end);
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(GOTO,finisher);
				mv.visitLabel(end);
				mv.visitLdcInsn(false);
				}
			else{
				mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFEQ, end);
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(GOTO,finisher);
				mv.visitLabel(end);
				mv.visitLdcInsn(false);
				}
			break;
		case OP_GT:
			expressionBinary.leftExpression.visit(this, arg);
			expressionBinary.rightExpression.visit(this, arg);
			if(type1.equals(Types.Type.INTEGER) || type2.equals(Types.Type.BOOLEAN)){
				mv.visitJumpInsn(IF_ICMPGT, load_true);
				mv.visitJumpInsn(GOTO, end);
				mv.visitLabel(load_true);
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(GOTO,finisher);
				mv.visitLabel(end);
				mv.visitLdcInsn(false);

				}
			else{
				mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFGT, load_true);
				mv.visitJumpInsn(GOTO, end);
				mv.visitLabel(load_true);
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(GOTO,finisher);
				mv.visitLabel(end);
				mv.visitLdcInsn(false);
				}
			
			break;
		case OP_GE:
			expressionBinary.leftExpression.visit(this, arg);
			expressionBinary.rightExpression.visit(this, arg);
			if(type1.equals(Types.Type.INTEGER) || type2.equals(Types.Type.BOOLEAN)){
				mv.visitJumpInsn(IF_ICMPGE, load_true);
				mv.visitJumpInsn(GOTO, end);
				mv.visitLabel(load_true);
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(GOTO,finisher);
				mv.visitLabel(end);
				mv.visitLdcInsn(false);


				}
			else{
				mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFGE, load_true);
				mv.visitJumpInsn(GOTO, end);
				mv.visitLabel(load_true);
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(GOTO,finisher);
				mv.visitLabel(end);
				mv.visitLdcInsn(false);
				}
			break;
			
		case OP_LT:
			expressionBinary.leftExpression.visit(this, arg);
			expressionBinary.rightExpression.visit(this, arg);
			if(type1.equals(Types.Type.INTEGER) || type2.equals(Types.Type.BOOLEAN)){
				mv.visitJumpInsn(IF_ICMPLT, load_true);
				mv.visitJumpInsn(GOTO, end);
				mv.visitLabel(load_true);
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(GOTO,finisher);
				mv.visitLabel(end);
				mv.visitLdcInsn(false);

				}
			else{
				mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFLT, load_true);
				mv.visitJumpInsn(GOTO, end);
				mv.visitLabel(load_true);
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(GOTO,finisher);
				mv.visitLabel(end);
				mv.visitLdcInsn(false);
				}
			
			break;
		case OP_LE:
			expressionBinary.leftExpression.visit(this, arg);
			expressionBinary.rightExpression.visit(this, arg);
			if(type1.equals(Types.Type.INTEGER) || type2.equals(Types.Type.BOOLEAN)){
				mv.visitJumpInsn(IF_ICMPLE, load_true);
				mv.visitJumpInsn(GOTO, end);
				mv.visitLabel(load_true);
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(GOTO,finisher);
				mv.visitLabel(end);
				mv.visitLdcInsn(false);

				}
			else{
				mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFLE, load_true);
				mv.visitJumpInsn(GOTO, end);
				mv.visitLabel(load_true);
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(GOTO,finisher);
				mv.visitLabel(end);
				mv.visitLdcInsn(false);
				}
			
			break;
		default:
			break;
		
		}
		
		
		mv.visitLabel(finisher);

		return null;
	}

	@Override
	public Object visitExpressionConditional(
			ExpressionConditional expressionConditional, Object arg)
			throws Exception {
		expressionConditional.guard.visit(this, arg);
		
		//boolean b = 5 > 4 ? true:false ;
		Label ffalse = new Label();
		Label end = new Label();
		mv.visitJumpInsn(IFEQ, ffalse);
		expressionConditional.trueExpression.visit(this, arg);
		mv.visitJumpInsn(GOTO, end);
		mv.visitLabel(ffalse);
		expressionConditional.falseExpression.visit(this, arg);
		mv.visitLabel(end);
		return null;
	}

	@Override
	public Object visitExpressionFloatLiteral(
			ExpressionFloatLiteral expressionFloatLiteral, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		mv.visitLdcInsn(expressionFloatLiteral.value);
		return null;
		
	}

	@Override
	public Object visitExpressionFunctionAppWithExpressionArg(
			ExpressionFunctionAppWithExpressionArg expressionFunctionAppWithExpressionArg,
			Object arg) throws Exception {
		expressionFunctionAppWithExpressionArg.e.visit(this, arg);
		Type temp_type = expressionFunctionAppWithExpressionArg.e.getType();
		switch(expressionFunctionAppWithExpressionArg.function) {
		case KW_abs:
			if(temp_type.equals(Type.INTEGER)){
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(I)I", false);
			}
			else if(temp_type.equals(Type.FLOAT)){
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(F)F", false);
			}
			break;
		case KW_sin:
			mv.visitInsn(F2D);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "sin", "(D)D", false);
			mv.visitInsn(D2F);
			break;
		case KW_cos:
			mv.visitInsn(F2D);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "cos", "(D)D", false);
			mv.visitInsn(D2F);
			break;
		case KW_atan:
			mv.visitInsn(F2D);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "atan", "(D)D", false);
			mv.visitInsn(D2F);
			break;
		case KW_log:
			mv.visitInsn(F2D);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "log", "(D)D", false);
			mv.visitInsn(D2F);
			break;
		case KW_int:
			if(temp_type.equals(Type.INTEGER)){
				
			}
			if(temp_type.equals(Type.FLOAT)){
				mv.visitInsn(F2I);
			}
			break;
		case KW_float:
			if(temp_type.equals(Type.FLOAT)){
				
			}
			if(temp_type.equals(Type.INTEGER)){
				mv.visitInsn(I2F);
			}
			break;
		case KW_alpha:
			mv.visitMethodInsn(INVOKESTATIC, RuntimePixelOps.className, "getAlpha", RuntimePixelOps.getAlphaSig, false);
			break;
		case KW_red:
			mv.visitMethodInsn(INVOKESTATIC, RuntimePixelOps.className, "getRed", RuntimePixelOps.getRedSig, false);
			break;
		case KW_blue:
			mv.visitMethodInsn(INVOKESTATIC, RuntimePixelOps.className, "getBlue", RuntimePixelOps.getBlueSig, false);
			break;
		case KW_green:
			mv.visitMethodInsn(INVOKESTATIC, RuntimePixelOps.className, "getGreen", RuntimePixelOps.getGreenSig, false);
		break;
		case KW_width:
			//RuntimePixelOps.getw
			mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className, "getWidth", RuntimeImageSupport.getWidthSig, false);
			break;
		case KW_height:
			mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className, "getHeight", RuntimeImageSupport.getHeightSig, false);
			break;
		default:
			break;
		
		}
		return null;
	}
	public Object Cart_Coordinates(Kind kind) {
		if(kind.equals(Kind.KW_cart_x))
		{
			mv.visitInsn(F2D);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "cos", "(D)D", false);
			mv.visitInsn(D2F);
			mv.visitInsn(FMUL);
			mv.visitInsn(F2I);
		}
		if(kind.equals(Kind.KW_cart_y))
		{
			mv.visitInsn(F2D);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "sin", "(D)D", false);
			mv.visitInsn(D2F);
			mv.visitInsn(FMUL);
			mv.visitInsn(F2I);
		}
		return null;
	}
	
	public void Polar_Coordinate(Kind kind) {
		
	}
	@Override
	public Object visitExpressionFunctionAppWithPixel(
			ExpressionFunctionAppWithPixel expressionFunctionAppWithPixel,
			Object arg) throws Exception {
		
		Kind kind = expressionFunctionAppWithPixel.name;
		switch(kind){
		case KW_cart_x:
		case KW_cart_y:
			expressionFunctionAppWithPixel.e0.visit(this, arg);
			expressionFunctionAppWithPixel.e1.visit(this, arg);
			Cart_Coordinates(kind);
		break;
		
		case KW_polar_a:
		case KW_polar_r:
			
			if(kind.equals(Kind.KW_polar_a))
			{
				expressionFunctionAppWithPixel.e1.visit(this, arg);
				mv.visitInsn(I2D);
				expressionFunctionAppWithPixel.e0.visit(this, arg);
				mv.visitInsn(I2D);
				//Math.atan2(0d, 0d);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "atan2", "(DD)D", false);
				mv.visitInsn(D2F);
			}
			if(kind.equals(Kind.KW_polar_r))
			{
				expressionFunctionAppWithPixel.e1.visit(this, arg);
				mv.visitInsn(I2D);
				expressionFunctionAppWithPixel.e0.visit(this, arg);
				mv.visitInsn(I2D);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "hypot", "(DD)D", false);
				mv.visitInsn(D2F);
			}
			break;
		default:
			break;
		}
	
		return null;
	}

	@Override
	public Object visitExpressionIdent(ExpressionIdent expressionIdent,
			Object arg) throws Exception {
		//System.out.println("visiting ident");
			Type type = expressionIdent.ttype;
			//System.out.println("-----------------"+expressionIdent.dec);
			Declaration dec = expressionIdent.dec;
			switch(type) {
			case INTEGER:
			case BOOLEAN:
				mv.visitVarInsn(ILOAD, dec.slotno);
				break;
			case FLOAT:
				mv.visitVarInsn(FLOAD, dec.slotno);
				break;
			default:
				mv.visitVarInsn(ALOAD, dec.slotno);
				break;
			}
			return null;

	}

	@Override
	public Object visitExpressionIntegerLiteral(
			ExpressionIntegerLiteral expressionIntegerLiteral, Object arg)
			throws Exception {
		// This one is all done!
		//System.out.println("visiting intlit");
		mv.visitLdcInsn(expressionIntegerLiteral.value);
		//System.out.println("----------------in intlit22");
		return null;
	}

	@Override
	public Object visitExpressionPixel(ExpressionPixel expressionPixel,
			Object arg) throws Exception {
		
		mv.visitVarInsn(ALOAD, expressionPixel.dec.slotno);
		expressionPixel.pixelSelector.visit(this, arg);
		
		mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className, "getPixel", RuntimeImageSupport.getPixelSig, false);
		return null;
	}

	@Override
	public Object visitExpressionPixelConstructor(
			ExpressionPixelConstructor expressionPixelConstructor, Object arg)
			throws Exception {
		expressionPixelConstructor.alpha.visit(this, arg);
		expressionPixelConstructor.red.visit(this, arg);
		expressionPixelConstructor.green.visit(this, arg);
		expressionPixelConstructor.blue.visit(this, arg);
		mv.visitMethodInsn(INVOKESTATIC, RuntimePixelOps.className, "makePixel", RuntimePixelOps.makePixelSig, false);
		
		return null;
	}

	@Override
	public Object visitExpressionPredefinedName(
			ExpressionPredefinedName expressionPredefinedName, Object arg)
			throws Exception {
		if(expressionPredefinedName.name == Kind.KW_Z) {
			mv.visitLdcInsn(Z);
		}
		else if (expressionPredefinedName.name == Kind.KW_default_width) {
			mv.visitLdcInsn(defaultWidth);
		}
		else if (expressionPredefinedName.name == Kind.KW_default_height) {
			mv.visitLdcInsn(defaultHeight);
		}
		
		return null;
	}

	@Override
	public Object visitExpressionUnary(ExpressionUnary expressionUnary,
			Object arg) throws Exception {
		Scanner.Kind op = expressionUnary.op;
		Type t = expressionUnary.expression.ttype;
		expressionUnary.expression.visit(this, arg);
		Kind k = expressionUnary.op;
		//if((t.equals(Type.IMAGE)) || (t.equals(Type.FILE)) || (k.equals(Kind.OP_EXCLAMATION) && t.equals(Type.FLOAT)) || (k.equals(Kind.OP_PLUS) && t.equals(Type.BOOLEAN)) || (k.equals(Kind.OP_MINUS) && t.equals(Type.BOOLEAN)))
		//	throw new SemanticException(expressionUnary.firstToken, "Unary expression error with +,-,!");
		switch(op){
		case OP_PLUS:
			
			break;
		case OP_MINUS:
			//expressionUnary.expression.visit(this, arg);
			if(expressionUnary.expression.ttype == Types.Type.INTEGER) {
				mv.visitInsn(INEG);
			}
			if(expressionUnary.expression.ttype == Types.Type.FLOAT) {
				mv.visitInsn(FNEG);
			}
			break;
		case OP_EXCLAMATION:
			//expressionUnary.expression.visit(this, arg);
			if(expressionUnary.expression.ttype == Types.Type.BOOLEAN) {
				//compareUtil(IFEQ);
				
				Label load_true = new Label();
				Label end = new Label();
				
				mv.visitLdcInsn(true);
				mv.visitJumpInsn(IF_ICMPNE, load_true);
				mv.visitLdcInsn(false);
				mv.visitJumpInsn(GOTO, end);
				mv.visitLabel(load_true);
				mv.visitLdcInsn(true);
				mv.visitLabel(end);
			}
			if(expressionUnary.expression.ttype == Types.Type.INTEGER) {
				mv.visitLdcInsn(-1);
				mv.visitInsn(IXOR);
			}
			
			break;
		}
		return null;
	}

	@Override
	public Object visitLHSIdent(LHSIdent lhsIdent, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//lhsIdent.dec.visit(this, arg);
		switch(lhsIdent.ttype) {
		
		case INTEGER:
			mv.visitVarInsn(ISTORE, lhsIdent.dec.slotno);
			break;
		case FLOAT:
			mv.visitVarInsn(FSTORE, lhsIdent.dec.slotno);
			break;
		
		case BOOLEAN:
			mv.visitVarInsn(ISTORE, lhsIdent.dec.slotno);
			break;
			
		case IMAGE:
			mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className, "deepCopy", RuntimeImageSupport.deepCopySig, false);
			mv.visitVarInsn(ASTORE, lhsIdent.dec.slotno);

			break;
		case FILE:
			mv.visitVarInsn(ASTORE, lhsIdent.dec.slotno);
			break;
		default:
			break;
		}

		return null;
	}

	@Override
	public Object visitLHSPixel(LHSPixel lhsPixel, Object arg)
			throws Exception {
		
		mv.visitVarInsn(ALOAD, lhsPixel.dec.slotno);

		lhsPixel.pixelSelector.visit(this, arg);
		
		mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className, "setPixel", RuntimeImageSupport.setPixelSig, false);
		return null;
	}

	@Override
	public Object visitLHSSample(LHSSample lhsSample, Object arg)
			throws Exception {
		
		
		mv.visitVarInsn(ALOAD, lhsSample.dec.slotno);
		lhsSample.pixelSelector.visit(this, arg);
		switch(lhsSample.color) {
		case KW_alpha:
			mv.visitLdcInsn(RuntimePixelOps.ALPHA);
			break;
		case KW_red:
			mv.visitLdcInsn(RuntimePixelOps.RED);
			break;
		case KW_blue:
			mv.visitLdcInsn(RuntimePixelOps.BLUE);
			break;
		case KW_green:
			mv.visitLdcInsn(RuntimePixelOps.GREEN);
			break;
		default:
			break;
		}
		mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className, "updatePixelColor", RuntimeImageSupport.updatePixelColorSig, false);
		
		return null;
	}

	@Override
	public Object visitPixelSelector(PixelSelector pixelSelector, Object arg)
			throws Exception {
		//Expression ex = pixelSelector.ex;
		pixelSelector.ex.visit(this, arg);
		pixelSelector.ey.visit(this, arg);
		if(pixelSelector.ex.getType().equals(Type.FLOAT)) {
			Cart_Coordinates(Kind.KW_cart_x);
			pixelSelector.ex.visit(this, arg);
			pixelSelector.ey.visit(this, arg);
			Cart_Coordinates(Kind.KW_cart_y);
		}
		
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		// TODO refactor and extend as necessary
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		// cw = new ClassWriter(0); //If the call to mv.visitMaxs(1, 1) crashes,
		// it is
		// sometime helpful to
		// temporarily run it without COMPUTE_FRAMES. You probably
		// won't get a completely correct classfile, but
		// you will be able to see the code that was
		// generated.
		//System.out.println("vising prog");
		className = program.progName;
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null,
				"java/lang/Object", null);
		cw.visitSource(sourceFileName, null);

		// create main method
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main",
				"([Ljava/lang/String;)V", null, null);
		// initialize
		mv.visitCode();

		// add label before first instruction
		Label mainStart = new Label();
		mv.visitLabel(mainStart);

		CodeGenUtils.genLog(DEVEL, mv, "entering main");
		//System.out.println("-----------------in program");
		program.block.visit(this, arg);
		//System.out.println("------------------in programb");
		// generates code to add string to log
		CodeGenUtils.genLog(DEVEL, mv, "leaving main");

		// adds the required (by the JVM) return statement to main
		mv.visitInsn(RETURN);

		// adds label at end of code
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart,
				mainEnd, 0);
		// Because we use ClassWriter.COMPUTE_FRAMES as a parameter in the
		// constructor,
		// asm will calculate this itself and the parameters are ignored.
		// If you have trouble with failures in this routine, it may be useful
		// to temporarily change the parameter in the ClassWriter constructor
		// from COMPUTE_FRAMES to 0.
		// The generated classfile will not be correct, but you will at least be
		// able to see what is in it.
		mv.visitMaxs(0, 0);

		// terminate construction of main method
		mv.visitEnd();

		// terminate class construction
		cw.visitEnd();

		// generate classfile as byte array and return
		return cw.toByteArray();
		
		
	}
	
	
	public Object nope(String[] args)throws Exception {
		
		return null;
	}
	

	@Override
	public Object visitStatementAssign(StatementAssign statementAssign,
			Object arg) throws Exception {
		statementAssign.e.visit(this, arg);
		statementAssign.lhs.visit(this, arg);
		
		return null;
	}

	

	@Override
	public Object visitStatementInput(StatementInput statementInput, Object arg)
			throws Exception {
		//System.out.println("visiting statementinput");
		//System.out.println("------------"+statementInput.dec);
		Type type = statementInput.dec.ttype;
		
		switch(type){
			case INTEGER:
			//System.out.println("-------------int");
			mv.visitVarInsn(ALOAD, 0);
			statementInput.e.visit(this, arg);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
			mv.visitVarInsn(ISTORE, statementInput.dec.slotno);
		break;
		
			case FILE:
			//System.out.println("--------------file");
			mv.visitVarInsn(ALOAD, 0);
			statementInput.e.visit(this, arg);
			mv.visitInsn(AALOAD);
			mv.visitVarInsn(ASTORE, statementInput.dec.slotno);
		break;
			case FLOAT:
			//System.out.println("--------------statementinputfloat");
			mv.visitVarInsn(ALOAD, 0);
			System.out.println("++++++++++++++++++++++++"+statementInput.e);
			statementInput.e.visit(this, arg);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "parseFloat", "(Ljava/lang/String;)F", false);
			mv.visitVarInsn(FSTORE, statementInput.dec.slotno);
			
		break;
			case BOOLEAN:
			//System.out.println("----------------bool");
			mv.visitVarInsn(ALOAD, 0);
			statementInput.e.visit(this, arg);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
			mv.visitVarInsn(ISTORE, statementInput.dec.slotno);

		break;
			
			case IMAGE:
			//System.out.println("---------------img");
			mv.visitVarInsn(ALOAD, 0);
			statementInput.e.visit(this, arg);
			mv.visitInsn(AALOAD);
			if(statementInput.dec.width != null && statementInput.dec.height != null) {
				statementInput.dec.width.visit(this, arg);
				
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				
				statementInput.dec.height.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
			
			}else {
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
			}
			
			mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className, "readImage", RuntimeImageSupport.readImageSig, false);
			mv.visitVarInsn(ASTORE, statementInput.dec.slotno);																				
		break;
		
		}
		
		//System.out.println("------------------statementinputexit");
		
		return null;
	}

	@Override
	public Object visitStatementShow(StatementShow statementShow, Object arg)
			throws Exception {
		/**
		 * TODO refactor and complete implementation.
		 * 
		 * For integers, booleans, and floats, generate code to print to
		 * console. For images, generate code to display in a frame.
		 * 
		 * In all cases, invoke CodeGenUtils.genLogTOS(GRADE, mv, type); before
		 * consuming top of stack.
		 */
		//System.out.println("visiting show");
		statementShow.e.visit(this, arg);
		Type type = statementShow.e.getType();
		switch (type) {
			case INTEGER : {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
						"Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
						"println", "(I)V", false);
			}
				break;
			case BOOLEAN : {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				
				mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
						"Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
						"println", "(Z)V", false);
			}
			break;
			// break; commented out because currently unreachable. You will need
			// it.
			case FLOAT : {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				
				mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
						"Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
						"println", "(F)V", false);
			}
			break;
			// break; commented out because currently unreachable. You will need
			// it.
			
			case FILE : {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
						"Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
						"println", "(Ljava/lang/String;)V", false);
			}
			break;
			
			case IMAGE : {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className, "makeFrame", RuntimeImageSupport.makeFrameSig, false);
				//mv.visitInsn(POP);
			}
			break;
			
			default :{
				
			}

		}
		return null;
	}

	@Override
	public Object visitStatementSleep(StatementSleep statementSleep, Object arg)
			throws Exception {
		
		
		statementSleep.duration.visit(this, arg);
		mv.visitInsn(I2L);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
		
		return null;
	}
	
	@Override
	public Object visitStatementIf(StatementIf statementIf, Object arg)
			throws Exception {
		statementIf.guard.visit(this, arg);
		Label end = new Label();
		mv.visitJumpInsn(IFEQ, end);
		statementIf.b.visit(this, arg);
		mv.visitLabel(end);
		return null;
	}

	@Override
	public Object visitStatementWhile(StatementWhile statementWhile, Object arg)
			throws Exception {
		Label visit_gaurd = new Label();
		mv.visitJumpInsn(GOTO, visit_gaurd);
		Label visit_b = new Label();
		
		
		mv.visitLabel(visit_b);
		statementWhile.b.visit(this, arg);
		mv.visitLabel(visit_gaurd);
		statementWhile.guard.visit(this, arg);
		mv.visitJumpInsn(IFNE, visit_b);
			
		return null;
	}

	@Override
	public Object visitStatementWrite(StatementWrite statementWrite, Object arg)
			throws Exception {
		//statementWrite.source.visit(this, arg);
		
		mv.visitVarInsn(ALOAD, statementWrite.source.slotno);
		//statementWrite.dest.visit(this, arg);
		mv.visitVarInsn(ALOAD, statementWrite.dest.slotno);
		mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className, "write", RuntimeImageSupport.writeSig, false);
		
		return null;
	}

}
