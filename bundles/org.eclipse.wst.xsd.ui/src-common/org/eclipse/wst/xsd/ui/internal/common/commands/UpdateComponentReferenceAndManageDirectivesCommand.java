/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.commands;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef.commands.Command;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDConcreteComponent;

public abstract class UpdateComponentReferenceAndManageDirectivesCommand extends Command{
	  protected XSDConcreteComponent concreteComponent;
	  protected String componentName;
	  protected String componentNamespace;
	  protected IFile file;

	  public UpdateComponentReferenceAndManageDirectivesCommand(XSDConcreteComponent concreteComponent, String componentName, String componentNamespace, IFile file)
	  {
	    this.concreteComponent = concreteComponent;
	    this.componentName = componentName;
	    this.componentNamespace = componentNamespace;
	    this.file = file;
	  }  

	  protected abstract XSDComponent computeComponent();
	  
	  public abstract void execute() ;

}
