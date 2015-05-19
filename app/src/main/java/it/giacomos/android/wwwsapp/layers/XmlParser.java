package it.giacomos.android.wwwsapp.layers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import android.util.Log;

public class XmlParser 
{
	public ArrayList<LayerItemData> parseLayerList(String xml)
	{
		ArrayList<LayerItemData> list = new ArrayList<LayerItemData>();
		Document dom;
		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		InputStream is;
		LayerItemData ld = new LayerItemData();
		factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
			try 
			{
				is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
				try 
				{
					dom = builder.parse(is);
					NodeList layerNodes = dom.getElementsByTagName("layers");
					if(layerNodes.getLength() == 1)
					{
						NodeList layers = dom.getElementsByTagName("layer");
						for(int i = 0; i < layers.getLength(); i++)
						{
							Element layer = (Element) layers.item(i);
							ld.name = layer.getAttribute("name");
							try{
								ld.available_version = Float.parseFloat(layer.getAttribute("version"));
							}
							catch(NumberFormatException e)
							{
								Log.e("XmlParser.parseLayer NumberFormatException", "invalid float " + layer.getAttribute("version"));
								ld.available_version = -1;
							}
							list.add(ld);
						}	
					}
				}
				catch (SAXException e) 
				{
					Log.e("XmlParser.parseLayer SAXException: decode()", e.getLocalizedMessage());
				} 
				catch (IOException e) 
				{	
					Log.e("XmlParser.parseLayer: decode()", e.getLocalizedMessage());
				}
			} 
			catch (UnsupportedEncodingException e) 
			{
				Log.e("XmlParser.parseLayer: decode()", e.getLocalizedMessage());
			}
		} 
		catch (ParserConfigurationException e1) 
		{
			Log.e("XmlParser.parseLayer: decode()", e1.getLocalizedMessage());
		}	
		return list;
	}

	public LayerItemData parseLayerDescription(String xml)
	{		
		Document dom;
		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		InputStream is;
		LayerItemData ld = new LayerItemData();
		factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
			try 
			{
				is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
				try 
				{
					dom = builder.parse(is);
					Element layer = dom.getDocumentElement(); 
					ld.name = layer.getAttribute("name");
					if(!ld.name.isEmpty())
					{
						layer.normalize();

						NodeList elements = layer.getElementsByTagName("*");
						for(int i = 0; i < elements.getLength(); i++)
						{
							Node dnode = elements.item(i);
							if(dnode.getNodeType() == Node.ELEMENT_NODE)
							{
								Element el = (Element) elements.item(i);
								if(el.getTagName().compareTo("title") == 0)
									ld.title = el.getTextContent();
								else if(el.getTagName().compareTo("author") == 0)
									ld.author = el.getTextContent();
								else if(el.getTagName().compareTo("date") == 0)
									ld.date = el.getTextContent();
								else if(el.getTagName().compareTo("description") == 0 && el.getAttribute("length").compareTo("long") == 0 )
									ld.long_desc = el.getTextContent();
								else if(el.getTagName().compareTo("description") == 0 )
									ld.short_desc = el.getTextContent();
							}
						}
						Log.e("detected " ,"stuff " + ld.name + ", " + ld.author);
					}
				} 
				catch (SAXException e) 
				{
					Log.e("XmlParser.parseLayerDescription SAXException: ", e.getLocalizedMessage()
							 + xml);
				} 
				catch (IOException e) 
				{	
					Log.e("XmlParser.parseLayerDescription: decode()", e.getLocalizedMessage());
				}
				catch(NumberFormatException e)
				{
					Log.e("XmlParser.parseLayerDescription: decode()", e.getLocalizedMessage());
				}
			} 
			catch (UnsupportedEncodingException e) 
			{
				Log.e("XmlParser.parseLayer: decode()", e.getLocalizedMessage());
			}
		} 
		catch (ParserConfigurationException e1) 
		{
			Log.e("XmlParser.parseLayer: decode()", e1.getLocalizedMessage());
		}	

		return ld;
	}

	public float getVersionFromManifest(String mani) 
	{
		Document dom;
		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		InputStream is;
		factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
			try 
			{
				is = new ByteArrayInputStream(mani.getBytes("UTF-8"));
				try 
				{
					dom = builder.parse(is);
					Element layer = dom.getDocumentElement(); 
					return Float.parseFloat(layer.getAttribute("version"));
				}
				catch (SAXException e) 
				{
					Log.e("XmlParser.getVersionFromManifest SAXException:", e.getLocalizedMessage()
							 + mani);
				} 
				catch (IOException e) 
				{	
					Log.e("XmlParser.getVersionFromManifest: ", e.getLocalizedMessage());
				}
				catch(NumberFormatException e)
				{
					Log.e("XmlParser.getVersionFromManifest: ", e.getLocalizedMessage());
				}
			}
			catch (UnsupportedEncodingException e) 
			{
				Log.e("XmlParser.parseLayer: decode()", e.getLocalizedMessage());
			}
		} 
		catch (ParserConfigurationException e1) 
		{
			Log.e("XmlParser.parseLayer: decode()", e1.getLocalizedMessage());
		}
		return -1.0f;
	}

}



