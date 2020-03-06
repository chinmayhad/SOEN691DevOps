package tutorial691.visitors;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.internal.core.nd.INdStruct;


public class CheckedExceptionVisitor extends ASTVisitor{
	
	private int methodCount = 0;
	private int catchCount;

	@Override
	public boolean visit(TryStatement node) {
		catchCount=0;
		//System.out.println("Check Try statement starts " + node.getBody());
		//System.out.println(node.getBody().statements().get(0));
		List<Statement> statement = node.getBody().statements();
		String methodinv = "";
		String exceptionInSignature = "";
		String fullyQNameOfSignExc = "";
		String[] excpnArrAtSignature = {};
		for (Statement statement2 : statement) {
			if (statement2 instanceof ExpressionStatement) {
				Expression express = ((ExpressionStatement) statement2).getExpression();
				if (express instanceof MethodInvocation) {
					IMethodBinding bind = ((MethodInvocation) express).resolveMethodBinding();
							if(bind!=null) {
								methodinv=bind.getMethodDeclaration().toString();
								System.out.println(methodinv);
							
					if (!(methodinv.indexOf("throws") == -1)) {
						int index = methodinv.indexOf("throws");
						fullyQNameOfSignExc = methodinv.substring(index+7, methodinv.length());
						fullyQNameOfSignExc = fullyQNameOfSignExc.replaceAll("\\s+", "");
						
						if(fullyQNameOfSignExc.indexOf(",") != -1) {
							excpnArrAtSignature = fullyQNameOfSignExc.split(",");
						}
						List<CatchClause> catcher = node.catchClauses();
						for (int i = 0; i < catcher.size(); i++) {
							for(int j = 0; j < excpnArrAtSignature.length; j++) {
								
								String fullyQNameOfInvoExc = catcher.get(i).getException().getName().resolveBinding().toString().split(" ")[0];
								fullyQNameOfSignExc = excpnArrAtSignature[j];
								
								if (!fullyQNameOfInvoExc.isEmpty() && !fullyQNameOfSignExc.isEmpty() &&
										!fullyQNameOfInvoExc.contentEquals(fullyQNameOfSignExc)) {

									try {	// here we compare two exception types. the one found with the calls and the one in called method's signature
										if (Class.forName(fullyQNameOfInvoExc).isAssignableFrom(Class.forName(fullyQNameOfSignExc))) {
											catchCount++;
											System.out.println(
													"Checked exception found Exception at invocation is super class of exception at invoked method's signature");
										}
									} catch (ClassNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								
							}
						}
						
					}}
				}
			}
		}
		//catchCount=0;
		
				
		System.out.println(catchCount);
		return super.visit(node);
	}
	
	public int getMethodCount() {
		
		return catchCount;
	}
	
}
