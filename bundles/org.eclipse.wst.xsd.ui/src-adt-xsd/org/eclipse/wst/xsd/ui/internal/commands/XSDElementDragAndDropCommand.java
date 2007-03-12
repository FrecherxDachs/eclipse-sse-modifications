/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.wst.xsd.ui.internal.actions.MoveXSDElementAction;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDElementDeclarationAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseFieldEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.CompartmentEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.ComplexTypeEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.StructureEditPart;
import org.eclipse.wst.xsd.ui.internal.design.editparts.ModelGroupDefinitionReferenceEditPart;
import org.eclipse.wst.xsd.ui.internal.design.editparts.ModelGroupEditPart;
import org.eclipse.wst.xsd.ui.internal.design.editparts.TargetConnectionSpacingFigureEditPart;
import org.eclipse.wst.xsd.ui.internal.design.editparts.XSDBaseFieldEditPart;
import org.eclipse.wst.xsd.ui.internal.design.editparts.XSDGroupsForAnnotationEditPart;
import org.eclipse.wst.xsd.ui.internal.design.figures.GenericGroupFigure;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;


public class XSDElementDragAndDropCommand extends BaseDragAndDropCommand
{
  protected ModelGroupEditPart topMostGroup;

  public XSDElementDragAndDropCommand(EditPartViewer viewer, ChangeBoundsRequest request, GraphicalEditPart target, XSDBaseFieldEditPart itemToDrag, Point location)
  {
    super(viewer, request);
    this.target = target;
    this.itemToDrag = itemToDrag;
    this.location = location;
    setup();
  }

  protected void setup()
  {
    canExecute = false;

    // Drop target is model group
    if (target instanceof ModelGroupEditPart)
    {
      parentEditPart = (ModelGroupEditPart) target;
      if (((GenericGroupFigure) parentEditPart.getFigure()).getIconFigure().getBounds().contains(location))
      {
        xsdComponentToDrag = (XSDConcreteComponent) ((XSDElementDeclarationAdapter) itemToDrag.getModel()).getTarget();
        action = new MoveXSDElementAction(((ModelGroupEditPart) target).getXSDModelGroup(), xsdComponentToDrag, null, null);
        canExecute = action.canMove();
      }
    }
    else if (target instanceof BaseFieldEditPart)
    {
      targetSpacesList = new ArrayList();
      // Calculate the list of all sibling field edit parts;
      List targetEditPartSiblings = calculateFieldEditParts();
      calculateModelGroupList();

      doDrop(targetEditPartSiblings, itemToDrag);
    }
  }

  protected void doDrop(List siblings, GraphicalEditPart movingEditPart)
  {
    commonSetup(siblings, movingEditPart);

    // Can common this code up with XSDAttributeDragAndDropCommand... 
    if (previousRefComponent instanceof XSDElementDeclaration && nextRefComponent instanceof XSDElementDeclaration)
    {
      XSDModelGroup modelGroup = (XSDModelGroup) ((XSDElementDeclaration) previousRefComponent).getContainer().getContainer();
      if (parentEditPart != null)
        modelGroup = ((ModelGroupEditPart) parentEditPart).getXSDModelGroup();
      action = new MoveXSDElementAction(modelGroup, xsdComponentToDrag, previousRefComponent, nextRefComponent);
    }
    else if (previousRefComponent == null && nextRefComponent instanceof XSDElementDeclaration)
    {
      if (closerSibling == ABOVE_IS_CLOSER)
      {
        if (leftSiblingEditPart == null)
        {
          action = new MoveXSDElementAction(topMostGroup.getXSDModelGroup(), xsdComponentToDrag, null, null, false);
        }
        else if (parentEditPart != null)
        {
          action = new MoveXSDElementAction(((ModelGroupEditPart) parentEditPart).getXSDModelGroup(), xsdComponentToDrag, previousRefComponent, nextRefComponent);
        }
      }
      else
      {
        XSDModelGroup modelGroup = (XSDModelGroup) ((XSDElementDeclaration) nextRefComponent).getContainer().getContainer();
        action = new MoveXSDElementAction(modelGroup, xsdComponentToDrag, previousRefComponent, nextRefComponent);
      }
    }
    else if (previousRefComponent instanceof XSDElementDeclaration && nextRefComponent == null)
    {
      XSDModelGroup modelGroup = (XSDModelGroup) ((XSDElementDeclaration) previousRefComponent).getContainer().getContainer();
      if (parentEditPart != null)
        modelGroup = ((ModelGroupEditPart) parentEditPart).getXSDModelGroup();
      if (closerSibling == ABOVE_IS_CLOSER)
      {
        action = new MoveXSDElementAction(modelGroup, xsdComponentToDrag, previousRefComponent, nextRefComponent);
      }
      else
      {
        if (rightSiblingEditPart == null)
        {
          action = new MoveXSDElementAction(topMostGroup.getXSDModelGroup(), xsdComponentToDrag, null, null, true);
        }
        else
        {
          action = new MoveXSDElementAction(modelGroup, xsdComponentToDrag, previousRefComponent, nextRefComponent);
        }
      }
    }

    if (action != null)
      canExecute = action.canMove();
  }

  /**
   * overrides base
   */
  protected boolean handleFirstAndLastDropTargets(int index, List siblings)
  {
    // This boolean is to handle the Top and Bottom drop targets for which we want to drop
    // to the top most model group
    // TODO: I need to rearrange this code better
    boolean isHandled = false;
    int pointerYLocation = location.y;
    // We need to find the parent editpart, which is the model group
    // Handle case where you drop to first position
    if (index == 0 && siblings.size() > 0)
    {
      leftSiblingEditPart = null;
      rightSiblingEditPart = (GraphicalEditPart) siblings.get(0);
      int siblingYLocation = getZoomedBounds(rightSiblingEditPart.getFigure().getBounds()).getCenter().y;
      closerSibling = BELOW_IS_CLOSER;
      if (Math.abs(pointerYLocation - siblingYLocation) > getZoomedBounds(rightSiblingEditPart.getFigure().getBounds()).height / 4)
      {
        isHandled = true;
        parentEditPart = topMostGroup;
        if (topMostGroup != null)
          closerSibling = ABOVE_IS_CLOSER;
      }
    }
    // Handle case where you drop to last position
    if (index > 0 && index == siblings.size())
    {
      leftSiblingEditPart = (GraphicalEditPart) siblings.get(index - 1);
      int siblingYLocation = getZoomedBounds(leftSiblingEditPart.getFigure().getBounds()).getCenter().y;
      if (Math.abs(pointerYLocation - siblingYLocation) > getZoomedBounds(leftSiblingEditPart.getFigure().getBounds()).height / 4)
      {
        isHandled = true;
        parentEditPart = topMostGroup;
        if (topMostGroup != null)
          closerSibling = BELOW_IS_CLOSER;
      }
    }
    return isHandled;
  }

  // Methods specific to element as drag source
  
  // Model Group related helper method
  protected void calculateModelGroupList()
  {
    EditPart editPart = target;
    while (editPart != null)
    {
      if (editPart instanceof ModelGroupEditPart)
      {
        getModelGroupEditParts((ModelGroupEditPart) editPart);
      }
      else if (editPart instanceof ComplexTypeEditPart || editPart instanceof StructureEditPart)
      {
        boolean foundTop = false;
        List list = editPart.getChildren();
        for (Iterator i = list.iterator(); i.hasNext();)
        {
          Object child = i.next();
          if (child instanceof CompartmentEditPart)
          {
            List compartmentList = ((CompartmentEditPart) child).getChildren();
            for (Iterator it = compartmentList.iterator(); it.hasNext();)
            {
              Object obj = it.next();
              if (obj instanceof XSDGroupsForAnnotationEditPart)
              {
                XSDGroupsForAnnotationEditPart groups = (XSDGroupsForAnnotationEditPart) obj;
                List groupList = groups.getChildren();
                for (Iterator iter = groupList.iterator(); iter.hasNext();)
                {
                  Object groupChild = iter.next();
                  if (groupChild instanceof ModelGroupEditPart)
                  {
                    if (!foundTop)
                    {
                      foundTop = true;
                      topMostGroup = (ModelGroupEditPart) groupChild;
                    }
                    getModelGroupEditParts((ModelGroupEditPart) groupChild);
                  }
                }
              }
            }
          }
        }
      }
      editPart = editPart.getParent();
    }
  }

  // Model Group related helper method
  
  protected List getModelGroupEditParts(ModelGroupEditPart modelGroupEditPart)
  {
    List modelGroupList = new ArrayList();
    List list = modelGroupEditPart.getChildren();
    for (Iterator i = list.iterator(); i.hasNext();)
    {
      Object object = i.next();
      if (object instanceof TargetConnectionSpacingFigureEditPart)
      {
        targetSpacesList.add(object);
      }
      else if (object instanceof ModelGroupDefinitionReferenceEditPart)
      {
        ModelGroupDefinitionReferenceEditPart groupRef = (ModelGroupDefinitionReferenceEditPart) object;
        List groupRefChildren = groupRef.getChildren();
        for (Iterator it = groupRefChildren.iterator(); it.hasNext();)
        {
          Object o = it.next();
          if (o instanceof ModelGroupEditPart)
          {
            getModelGroupEditParts((ModelGroupEditPart) o);
          }
        }
      }
      else if (object instanceof ModelGroupEditPart)
      {
        getModelGroupEditParts((ModelGroupEditPart) object);
      }
    }
    return modelGroupList;
  }
}