package tutorial691.handlers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import tutorial691.visitors.CatchReturnNullvisitor;
import tutorial691.visitors.CheckedExceptionVisitor;
import tutorial691.visitors.UncheckedExceptionVisitor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class DetectMethodDeclaration extends AbstractHandler {
private static int nullexceptionCount=0;
private static int checkdExceptionCount=0;
private static int UncheckedExceptionCount=0;
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects();
		//System.out.println("heloooooooooooooooooo");
		detectInProjects(projects);
		SampleHandler.printMessage(String.
				  format("The number of checked overCatch Exception in the project is %s ",
						  checkdExceptionCount));
		SampleHandler.printMessage(String.
				  format("The number of unchecked overCatch Exception in the project is %s ",
						  UncheckedExceptionCount));
		SampleHandler.printMessage(String.
				  format("The number of null overCatch Exception in the project is %s ",
						  nullexceptionCount));
		SampleHandler.printMessage("DONE DETECTING");
		return null;
	}
	
	// provide java source code to parse
	private void detectInProjects(IProject[] projects) {
		for (IProject project : projects) {
			SampleHandler.printMessage("DETECTING IN: " + project.getName());
			IPackageFragment[] packages;
			try {
				packages = JavaCore.create(project).getPackageFragments();
				for (IPackageFragment mypackage : packages) {
					//SampleHandler.printMessage("I am looking in package");
					findMethods(mypackage);
					
				}
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	  private static void findMethods(IPackageFragment packageFragment) throws
	  JavaModelException {
		  
		  for (ICompilationUnit unit :packageFragment.getCompilationUnits()) { 
			  CompilationUnit
	  parsedCompilationUnit = parse(unit); // Create AST from source code project
	  
	  // do method visit here and check stuff MethodDeclarationVisitor
			  CheckedExceptionVisitor checked = new CheckedExceptionVisitor(); // implement your
			  UncheckedExceptionVisitor unchecked=new UncheckedExceptionVisitor();
			  CatchReturnNullvisitor nullvisitor=new CatchReturnNullvisitor();
	 parsedCompilationUnit.accept(checked); //
	  parsedCompilationUnit.accept(nullvisitor);
	  parsedCompilationUnit.accept(unchecked); ////
	 printOvercatch(checked); 
	 printnullreturn(nullvisitor);
	 printOvercatchUnchecked(unchecked);
	  } }
		  
	 // printnullreturn(nullvisitor);}}
	  private static void printOvercatch(CheckedExceptionVisitor
			  checked) {
		  checkdExceptionCount+=checked.getMethodCount();
	 /* SampleHandler.printMessage(String.
	  format("The number of checked overCatch Exception in the project is %s ",
			  checked.getMethodCount()));*/ }
	  
	  private static void printnullreturn(CatchReturnNullvisitor
			  nullvisitor) {
	  
		  nullexceptionCount+= nullvisitor.getreturncount();
		/*
		 * SampleHandler.printMessage(String.
		 * format("The number of null overCatch Exception in the project is %s ",
		 * nullvisitor.getreturncount()));
		 */ 
	  }
	  private static void printOvercatchUnchecked(UncheckedExceptionVisitor
			  unchecked) {
	  
		  UncheckedExceptionCount+=unchecked.getCount();
		/*
		 * SampleHandler.printMessage(String.
		 * format("The number of unchecked overCatch Exception in the project is %s ",
		 * unchecked.getCount() ));
		 */ }
	  
	  @SuppressWarnings("deprecation")
	 
	public static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setStatementsRecovery(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}
}
