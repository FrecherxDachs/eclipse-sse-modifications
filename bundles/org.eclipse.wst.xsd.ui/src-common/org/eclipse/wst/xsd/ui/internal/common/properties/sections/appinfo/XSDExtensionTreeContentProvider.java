package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDCommonUIUtils;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XSDExtensionTreeContentProvider extends DOMExtensionTreeContentProvider
{  
  public Object[] getElements(Object inputElement)
  {
    if (inputElement instanceof XSDConcreteComponent)
    {
      XSDConcreteComponent component = (XSDConcreteComponent) inputElement;
      List elementsAndAttributes = new ArrayList();
      XSDAnnotation xsdAnnotation = XSDCommonUIUtils.getInputXSDAnnotation(component, false);
      if (xsdAnnotation != null)
      {
        List appInfoList = xsdAnnotation.getApplicationInformation();
        Element appInfoElement = null;
        if (appInfoList.size() > 0)
        {
          appInfoElement = (Element) appInfoList.get(0);
        }
        if (appInfoElement != null)
        {
          for (Iterator it = appInfoList.iterator(); it.hasNext();)
          {
            Object obj = it.next();
            if (obj instanceof Element)
            {
              Element appInfo = (Element) obj;
              NodeList nodeList = appInfo.getChildNodes();
              int length = nodeList.getLength();
              for (int i = 0; i < length; i++)
              {
                Node node = nodeList.item(i);
                if (node instanceof Element)
                {
                  elementsAndAttributes.add(node);
                }
              }
            }
          }
        }
      }
      Element element = component.getElement();
      if (element == null)
      {
        return elementsAndAttributes.toArray();
      }
      
      /** Construct attributes list */
      NamedNodeMap attributes = component.getElement().getAttributes();
      if (attributes != null)
      {
        // String defaultNamespace =
        // (String)component.getSchema().getQNamePrefixToNamespaceMap().get("");
        int length = attributes.getLength();
        for (int i = 0; i < length; i++)
        {
          Node oneAttribute = attributes.item(i);
          if (!isXmlnsAttribute(oneAttribute))
          {
            String namespace = oneAttribute.getNamespaceURI();
            boolean isExtension = true;
            if (namespace == null && oneAttribute.getPrefix() == null)
            {
              isExtension = false;
            }
            else if (!XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001.equals(namespace))
            {
              isExtension = true;
            }
            if (isExtension)
            {
              elementsAndAttributes.add(oneAttribute);
            }
          }
        }
      }
      return elementsAndAttributes.toArray();
    }
    return Collections.EMPTY_LIST.toArray();
  }

  private static boolean isXmlnsAttribute(Node attribute)
  {
    String prefix = attribute.getPrefix();
    if (prefix != null)
    {
      // this handle the xmlns:foo="blah" case
      return "xmlns".equals(prefix); //$NON-NLS-1$
    }
    else
    {
      // this handles the xmlns="blah" case
      return "xmlns".equals(attribute.getNodeName()); //$NON-NLS-1$
    }
  }
}
