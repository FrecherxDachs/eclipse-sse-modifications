/* Generated By:JJTree: Do not edit this line. ASTAndExpression.java */

package org.eclipse.jst.jsp.core.internal.java.jspel;

public class ASTAndExpression extends ASTOperatorExpression {
  public ASTAndExpression(int id) {
    super(id);
  }

  public ASTAndExpression(JSPELParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JSPELParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
