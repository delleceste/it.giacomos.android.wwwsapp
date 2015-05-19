package it.giacomos.android.wwwsapp.personalMessageActivity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Log;

public class PersonalMessageDataDecoder 
{
	private PersonalMessageData mPersonalMessageData;

	public PersonalMessageDataDecoder(String document)
	{
		mPersonalMessageData = new PersonalMessageData();
		if(document.length() > 10)
		{
			/* parse xml and get parameters for news data */
			Document dom;
			DocumentBuilderFactory factory;
			DocumentBuilder builder;
			InputStream is;
			factory = DocumentBuilderFactory.newInstance();
			try {
				builder = factory.newDocumentBuilder();
				try 
				{
					is = new ByteArrayInputStream(document.getBytes("UTF-8"));
					try 
					{
						dom = builder.parse(is);
						NodeList limitationNodes = dom.getElementsByTagName("limitation");

						if(limitationNodes.getLength() == 1)
						{
							Element limitation = (Element) limitationNodes.item(0);
							if(limitation != null)
							{
								String blocking = limitation.getAttribute("blocking");
								mPersonalMessageData.blocking = (blocking.compareTo("true") == 0);

								NodeList childNodes = limitation.getChildNodes();
								for(int i = 0; i < childNodes.getLength(); i++)
								{
									Node n = childNodes.item(i);
									Element e = (Element ) n;

									if(e.getTagName().compareTo("name") == 0 )
										mPersonalMessageData.title = e.getTextContent();
									else if(e.getTagName().compareTo("message") == 0 )
										mPersonalMessageData.message = e.getTextContent();
									else if(e.getTagName().compareTo("date") == 0 )
										mPersonalMessageData.date = e.getTextContent();
								}
								
//								Log.e("PersonalMessageDataDecoder", "date " + mPersonalMessageData.date + 
//										", text " + mPersonalMessageData.message + 
//										", name " + mPersonalMessageData.title + " blockin " + mPersonalMessageData.blocking);
							}
						}
					} 
					catch (SAXException e) 
					{
						Log.e("PersonalMessageDataDecoder SAXException", e.getLocalizedMessage());
					} 
					catch (IOException e) 
					{	
						Log.e("PersonalMessageDataDecoder: IOException", e.getLocalizedMessage());
					}
					catch(DOMException e)
					{
						Log.e("PersonalMessageDataDecoder: DOMException", e.getLocalizedMessage());
					}
				} 
				catch (UnsupportedEncodingException e) 
				{
					Log.e("PersonalMessageDataFetchTask: doInBackground()", e.getLocalizedMessage());
				}
			} 
			catch (ParserConfigurationException e1) 
			{
				Log.e("PersonalMessageDataFetchTask: doInBackground()", e1.getLocalizedMessage());
			}		
		}
	}

	public PersonalMessageData getData() 
	{
		return mPersonalMessageData;
	}
}

