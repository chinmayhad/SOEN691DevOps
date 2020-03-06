package tutorial691.visitors;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TryStatement;

public class CatchReturnNullvisitor extends ASTVisitor{
	private int nullreturn=0;
	@Override
	public boolean visit(CatchClause node) {
		List<Statement> statements=node.getBody().statements();
		for (Statement statement : statements) {
			if(statement.toString().contains("return null")) {
				nullreturn++;//System.out.println("We found the antipattern"+ node);
			}
		}
		return super.visit(node);
	}
	public int getreturncount() {
		return nullreturn;
	}
}
