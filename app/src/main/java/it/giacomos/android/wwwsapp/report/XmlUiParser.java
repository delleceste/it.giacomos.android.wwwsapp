package it.giacomos.android.wwwsapp.report;

import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import it.giacomos.android.wwwsapp.Intl;
import it.giacomos.android.wwwsapp.MyAlertDialogFragment;
import it.giacomos.android.wwwsapp.layers.FileUtils;

/**
 * Created by giacomo on 17/06/15.
 */
public class XmlUiParser
{
    public static final String UI_TYPE_REPORT = "report";
    public static final String UI_TYPE_REQUEST = "request";

    public XmlUIDocumentRepr parse(String layer, Context ctx, String ui_type)
    {
        String text, propertyName, type, marker_icon = "";
        Intl intl = new Intl(ctx, Locale.getDefault().getLanguage());
        XmlUIDocumentRepr documentRepr = new XmlUIDocumentRepr(layer);
        final String errPrefix = "XMlUIDocumentRepr.parse: error parsing layer " + layer + ": ";
        FileUtils fu = new FileUtils();
        String layerRelativePath = "layers/" + layer + "/" + layer + "_ui.xml";
        String uixml = fu.loadFromStorage(layerRelativePath, ctx);

        Document dom;
        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        InputStream is;
        factory = DocumentBuilderFactory.newInstance();
        try
        {
            builder = factory.newDocumentBuilder();
            try
            {
                is = new ByteArrayInputStream(uixml.getBytes("UTF-8"));
                try
                {
                    dom = builder.parse(is);
                    Element uis = dom.getDocumentElement();
                    String layername = uis.getAttribute("name");
                    if (layername.compareTo(layer) == 0)
                    {
                        Element ui = null;
                        NodeList uiElements = uis.getElementsByTagName("ui");

                    /* find the ui elements with attribute type equal to the ui_type
                     * we want to parse (report, request)
                     */
                        for (int i = 0; i < uiElements.getLength(); i++)
                        {
                            Element uiEl = (Element) uiElements.item(i);
                            if (uiEl.getAttribute("type").compareTo(ui_type) == 0)
                                ui = uiEl;
                        }
                        if (ui != null)
                        {

                            ui.normalize();
                            NodeList titleElements = ui.getElementsByTagName("title");
                            if (titleElements.getLength() == 1)
                            {
                                Element title = (Element) titleElements.item(0);
                                if (title.hasAttribute("text"))
                                    documentRepr.setTitle(intl.tr(title.getAttribute("text")));
                            }
                            NodeList elements = ui.getElementsByTagName("property");
                            for (int i = 0; i < elements.getLength(); i++)
                            {
                                marker_icon = "";
                                Node dnode = elements.item(i);
                                if (dnode.getNodeType() == Node.ELEMENT_NODE)
                                {
                                    XmlUiProperty xmlproperty = null;
                                    Element prop = (Element) elements.item(i);
                                    text = intl.tr(prop.getAttribute("text"));
                                    propertyName = prop.getAttribute("name");
                                    type = prop.getAttribute("type");
                                    if (prop.hasAttribute("marker_icon"))
                                        marker_icon = prop.getAttribute("marker_icon");

                                    xmlproperty = new XmlUiProperty(propertyName, text, type);
                                    xmlproperty.setIsMarkerIcon(marker_icon.compareTo("true") == 0);

                                    NodeList values = prop.getElementsByTagName("values");
                                    Log.e("XmlUiParser.parse", " gettubg avlues " + values.getLength());
                                    if (values.getLength() == 1)
                                    {
                                        NodeList valuelist = prop.getElementsByTagName("value");
                                        if (valuelist.getLength() == 0)
                                        {
                                            documentRepr.setError("Error in " + layerRelativePath + ": empty \"values\" tag");
                                        } else
                                        {
                                            for (int n = 0; n < valuelist.getLength(); n++)
                                            {
                                                Node valnode = valuelist.item(n);
                                                if (valnode.getNodeType() == Node.ELEMENT_NODE)
                                                {
                                                    Element val = (Element) valnode;
//                                                Log.e("Builder.build", " add value " + val.getAttribute("text"));
                                                    setPropertyValueFromElement(val, xmlproperty, intl);
                                                } else
                                                    Log.e("Builder.build", " valnode is not element node " + valnode);
                                            }
                                        }
                                    } else
                                    {
                                        NodeList valueNode = prop.getElementsByTagName("value");
                                        if (valueNode.getLength() == 1)
                                        {
                                            if (valueNode.item(0).getNodeType() == Node.ELEMENT_NODE)
                                            {
                                                Element val = (Element) valueNode.item(0);
                                                setPropertyValueFromElement(val, xmlproperty, intl);
                                            }
                                        } else
                                        {
                                            documentRepr.setError("Error in " + layerRelativePath + ": multiple value elements without \"values\" parent");
                                        }
                                    }

                                    documentRepr.addProperty(propertyName, xmlproperty);

                                } /* if node is element node */

                                if (documentRepr.hasError())
                                    break;

                            }

                        } /* if there's a ui element with the desired type */
                    } /* layer name as expected */

                }
                catch (SAXException e)
                {
                    documentRepr.setError(errPrefix + e.getLocalizedMessage());
                    Log.e("build SAXExc: ", e.getLocalizedMessage()
                            + uixml);
                }
                catch (IOException e)
                {
                    documentRepr.setError(errPrefix + e.getLocalizedMessage());
                    Log.e("build: IOExc", e.getLocalizedMessage());
                }
                catch (NumberFormatException e)
                {
                    documentRepr.setError(errPrefix + e.getLocalizedMessage());
                    Log.e("build: NumFmtEx", e.getLocalizedMessage());
                }
            }
            catch (UnsupportedEncodingException e)
            {
                documentRepr.setError(errPrefix + e.getLocalizedMessage());
                Log.e("build: UnsuppEncEx", e.getLocalizedMessage());
            }
        }
        catch (ParserConfigurationException e1)
        {
            documentRepr.setError(errPrefix + e1.getLocalizedMessage());
            Log.e("build: ParserConfExc", e1.getLocalizedMessage());
        }

        return documentRepr;
    }

    private void setPropertyValueFromElement(Element val, XmlUiProperty xmlproperty, Intl intl)
    {
        String value, icon = "", text = "";
        value = val.getTextContent();
        if(val.hasAttribute("icon"))
            icon = val.getAttribute("icon");
        if(val.hasAttribute("text"))
            text = intl.tr(val.getAttribute("text"));
        xmlproperty.addValue(value, text, icon);
    }
}
