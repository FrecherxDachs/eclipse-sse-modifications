/* Generated By:JJTree: Do not edit this line. ASTValuePrefix.java */

package org.eclipse.jst.jsp.core.internal.java.jspel;

public class ASTValuePrefix extends SimpleNode {
  public ASTValuePrefix(int id) {
    super(id);
  }

  public ASTValuePrefix(JSPELParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JSPELParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
