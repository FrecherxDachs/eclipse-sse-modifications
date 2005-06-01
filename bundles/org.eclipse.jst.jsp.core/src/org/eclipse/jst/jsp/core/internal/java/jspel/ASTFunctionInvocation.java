/* Generated By:JJTree: Do not edit this line. ASTFunctionInvocation.java */

package org.eclipse.jst.jsp.core.internal.java.jspel;

public class ASTFunctionInvocation extends SimpleNode {
  String fullFunctionName; 
  
  public ASTFunctionInvocation(int id) {
    super(id);
  }

  public ASTFunctionInvocation(JSPELParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JSPELParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

public String getFullFunctionName() {
	return fullFunctionName;
}

public void setFullFunctionName(String fullFunctionName) {
	this.fullFunctionName = fullFunctionName;
}
}
