package tutorial691.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;

public class UncheckedExceptionVisitor extends ASTVisitor {
	HashMap<String,String> FullNameMap=new HashMap<>();
	int catchCount=0;
	public void  UncheckedExceptionVisitor() {
		FullNameMap.put("ArithmeticException","java.lang.ArithmeticException");
		FullNameMap.put("ArrayIndexOutOfBoundsException","java.lang.ArrayIndexOutOfBoundsException");
		FullNameMap.put("ArrayStoreException","java.lang.ArrayStoreException");
		FullNameMap.put("ClassCastException","java.lang.ClassCastException");
		FullNameMap.put("IllegalArgumentException","java.lang.IllegalArgumentException");
		FullNameMap.put("IllegalMonitorStateException","java.lang.IllegalMonitorStateException");
		FullNameMap.put("IllegalStateException","java.lang.IllegalStateException");
		FullNameMap.put("IllegalThreadStateException","java.lang.IllegalThreadStateException");
		FullNameMap.put("IndexOutOfBoundsException","java.lang.IndexOutOfBoundsException");
		FullNameMap.put("NegativeArraySizeException","java.lang.NegativeArraySizeException");
		FullNameMap.put("NullPointerException","java.lang.NullPointerException");
		FullNameMap.put("NumberFormatException","java.lang.NumberFormatException");
		FullNameMap.put("SecurityException","java.lang.SecurityException");
		FullNameMap.put("StringIndexOutOfBounds","java.lang.StringIndexOutOfBounds");
		FullNameMap.put("UnsupportedOperationException","java.lang.UnsupportedOperationException");
	}
	@Override
	public boolean visit(TryStatement node) {
		List<String> exceptionsIn=new ArrayList<String>();
		//IClasspathEntry srcEntry = JavaCore.newSourceEntry(new Path("C:\\Users\\user\\Desktop\\src"));
		
		//System.out.println("Try for unchecked"+node.getBody());
		List<Statement> statement=node.getBody().statements();
		for (Statement statement2 : statement) {
			
			if(statement2 instanceof ExpressionStatement) {
				Expression express=((ExpressionStatement)statement2).getExpression();
				if(express instanceof MethodInvocation) {
					int Linenumber=((MethodInvocation)express).getStartPosition();
					MethodDeclaration declar=getMethodDeclaration((MethodInvocation)express);
				
					
					if(declar!=null) {
						exceptionsIn=parseMethod(declar);
						List<CatchClause> catcher = node.catchClauses();
						for (int i = 0; i < catcher.size(); i++) {
							for(int j = 0; j < exceptionsIn.size(); j++) {
								
								String fullyQNameOfInvoExc = catcher.get(i).getException().getName().resolveBinding().toString().split(" ")[0];
								String fullyQNameOfSignExc = exceptionsIn.get(j);
								if(fullyQNameOfInvoExc!=null && fullyQNameOfSignExc!=null) {
								if (!fullyQNameOfInvoExc.isEmpty() && !fullyQNameOfSignExc.isEmpty() &&
										!fullyQNameOfInvoExc.contentEquals(fullyQNameOfSignExc)) {

									try {	// here we compare two exception types. the one found with the calls and the one in called method's signature
										if (Class.forName(fullyQNameOfInvoExc).isAssignableFrom(Class.forName(fullyQNameOfSignExc))) {
											catchCount++;
											String ClassName=(node.getParent().getParent().getParent().toString().substring(13).split(" "))[0];
											String MethodName=((MethodDeclaration)node.getParent().getParent()).getName().toString();
											CompilationUnit unit=compile((MethodInvocation)express);
											Linenumber=unit.getLineNumber(statement2.getStartPosition()-1);
											
											System.out.println("Unchecked Exception in class "+ ClassName+ " in method "+MethodName+" at Line number "+Linenumber);
										}
									} catch (ClassNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								}
							}
						}
					}
				}
			}
		}
		//System.out.println("Final Exceptions list for try block "+exceptionsIn);
		return super.visit(node);
	}
	
	public MethodDeclaration getMethodDeclaration(MethodInvocation invoke) {
		MethodDeclaration decl=null;
		//System.out.println("1");
		IMethodBinding binding = (IMethodBinding) invoke.getName().resolveBinding();
		//System.out.println("1");
		if(binding!=null) {
		ICompilationUnit unit = (ICompilationUnit) binding.getJavaElement().getAncestor( IJavaElement.COMPILATION_UNIT );
		
		if ( unit != null ) {
			ASTParser parser = ASTParser.newParser( AST.JLS8 );
			parser.setKind( ASTParser.K_COMPILATION_UNIT );
			parser.setSource( unit );
			parser.setResolveBindings( true );
			CompilationUnit cu = (CompilationUnit) parser.createAST( null );
			decl = (MethodDeclaration)cu.findDeclaringNode( binding.getKey() );
			//System.out.println("Found Method");
			return decl;// not available, external declaration
		}
		}
		return decl;
	
	}
	
	
	public CompilationUnit compile(MethodInvocation invoke) {
		IMethodBinding binding = (IMethodBinding) invoke.getName().resolveBinding();
		//System.out.println("1");
		CompilationUnit cu=null;
		if(binding!=null) {
			ICompilationUnit unit = (ICompilationUnit) binding.getJavaElement().getAncestor( IJavaElement.COMPILATION_UNIT );
		 if ( unit != null ) {
				ASTParser parser = ASTParser.newParser( AST.JLS8 );
				parser.setKind( ASTParser.K_COMPILATION_UNIT );
				parser.setSource( unit );
				parser.setResolveBindings( true );
				cu = (CompilationUnit) parser.createAST( null );
		}
		}
		return  cu;
	}
	
	
	public List<String> parseMethod(MethodDeclaration declaration) {
		//System.out.println(declaration.getName());
		List<String> exceptionfound=new ArrayList<String>();
		if(declaration.getJavadoc()!=null) {
			List tags=declaration.getJavadoc().tags();
			for (Object object : tags) {
				String javadoccoment=object.toString();
				if(javadoccoment.contains("throws")) {
					int index=javadoccoment.indexOf("throws");
					String[] main=javadoccoment.substring(index+7).split(" ");
					String exceptions=main[0];
					
					String qualifiedname=FullNameMap.get(exceptions);
					exceptionfound.add(qualifiedname);
				}
			}
		}
		Block statements=declaration.getBody();
		if(statements!=null) {
			List<Statement> statement=statements.statements();
		
		for (Statement statement2 : statement) {
				/*
				 * System.out.println("In system body2");
				 * System.out.println(statement2.toString());
				 */
			
			if(statement2 instanceof ExpressionStatement) {
				Expression express=((ExpressionStatement)statement2).getExpression();
				if(express instanceof MethodInvocation) {
					MethodDeclaration declar=getMethodDeclaration((MethodInvocation)express);
					if(declar!=null) {
					List<String> innerfound=parseMethod(declar);
				//	System.out.println("About to add exception");
					for (String exception : innerfound) {
						//System.out.println("Adding inner exception exception "+exception);
						exceptionfound.add(exception);
					}
					}
				}
			}
			if(statement2 instanceof ThrowStatement) {
				Expression thr=((ThrowStatement)statement2).getExpression();
				if(thr.toString().length()>=7) {
				String exception=thr.toString().substring(4, thr.toString().length()-2);
				System.out.println(exception);
				//System.out.println("throw i dound you");
				if(!exception.contains(".")) 
					exceptionfound.add("java.lang."+exception);
				else
					exceptionfound.add(exception);	
				}
				
			}			
		}
		}
		return exceptionfound;
	}
	
	public int getCount() {
		return catchCount;
	}
}
