package it.giacomos.android.wwwsapp.widgets.map.report;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import it.giacomos.android.wwwsapp.IconTextSpinnerAdapter;
import it.giacomos.android.wwwsapp.MyAlertDialogFragment;
import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.layers.FileUtils;

/**
 * Created by giacomo on 3/06/15.
 */
public class ReportUiBuilder
{
    private PostActivity mActivity;
    private int mOptionCheckBoxCount;
    private final int OPTION_CB_ID = 1126332445;

    private class Pair
    {
        Pair(int cbid, int viewId)
        {
            checkboxId = cbid;
            assocViewId = viewId;
        }

        public int checkboxId, assocViewId;
    }

    private class WidgetValue
    {
        public String text;
        public Bitmap icon;
        public boolean isValid;

        public WidgetValue(String txt)
        {
            text = txt;
            isValid = true;
            icon = null;
        }
    }

    private class WidgetData
    {
        public ArrayList<WidgetValue> values;

        public String name, type, text, representation;
        boolean isOption, isCategory;
        int id;

        public WidgetData(int wid, String nam, String ty, String txt, String repr)
        {
            id = wid;
            name = nam;
            type = ty;
            representation = repr;
            values = new ArrayList<WidgetValue>();
            isOption = false;
            isCategory = false;
        }

        public void addValue(WidgetValue v)
        {
            values.add(v);
        }
    }

    private class DataValuesSpinnerAdapter extends ArrayAdapter<String>
    {
        private ArrayList<WidgetValue> values;

        public DataValuesSpinnerAdapter(Context context, int resource,
                                        ArrayList<WidgetValue> data, Activity activity)
        {
            super(context, resource);
            values = data;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent)
        {

            LayoutInflater inflater = mActivity.getLayoutInflater();
            View row = inflater.inflate(R.layout.post_icon_text_spinner, parent, false);
            TextView label = (TextView) row.findViewById(R.id.text);
            if (position < values.size())
            {
                WidgetValue v = values.get(position);
                label.setText(v.text);
                if (v.icon != null)
                {
                    ImageView icon = (ImageView) row.findViewById(R.id.icon);
                    icon.setImageBitmap(v.icon);
                }
            }
            return row;
        }
    }


    public ReportUiBuilder(PostActivity a)
    {
        mActivity = a;
        mOptionCheckBoxCount = 0;
    }

    public HashMap<Integer, Integer> build(String layer, String locality)
    {
        int id = 100;
        String uixml = "";
        FileUtils fu = new FileUtils();
        HashMap<Integer, Integer> optionViewsHash = new HashMap<Integer, Integer>();
        Pair pair;
        String layerRelativePath = "layers/" + layer + "_ui.xml";
        String repr, text, name, type;
        boolean validate = false, category = false;
        uixml = fu.loadFromStorage(layerRelativePath, mActivity);

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
                    Element ui = dom.getDocumentElement();
                    String layername = ui.getAttribute("name");
                    if (layername.compareTo(layer) == 0)
                    {
                        ui.normalize();
                        NodeList elements = ui.getElementsByTagName("property");
                        for (int i = 0; i < elements.getLength(); i++)
                        {
                            Node dnode = elements.item(i);
                            if (dnode.getNodeType() == Node.ELEMENT_NODE)
                            {
                                Element prop = (Element) elements.item(i);
                                repr = prop.getAttribute("repr");
                                text = prop.getAttribute("text");
                                name = prop.getAttribute("name");
                                type = prop.getAttribute("type");
                                WidgetData widgetData = new WidgetData(id, name, type, text, repr);
                                widgetData.isCategory = prop.hasAttribute("category") && prop.getAttribute("category").compareTo("true") == 0;
                                widgetData.isOption = prop.hasAttribute("is_option") && prop.getAttribute("is_option").compareTo("true") == 0;

                                NodeList values = prop.getElementsByTagName("values");
                                if (values.getLength() == 1)
                                {
                                    NodeList valuelist = prop.getElementsByTagName("value");
                                    if (valuelist.getLength() == 0)
                                    {
                                        MyAlertDialogFragment.MakeGenericError("Error in " + layerRelativePath + ": empty \"values\" tag", mActivity);
                                    } else
                                    {
                                        for (int n = 0; n < valuelist.getLength(); n++)
                                        {
                                            Node valnode = valuelist.item(n);
                                            if (valnode.getNodeType() == Node.ELEMENT_NODE)
                                            {
                                                Element val = (Element) valnode;
                                                widgetData.addValue(getWidgetValueFromElement(val, layer));
                                            }
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
                                            widgetData.addValue(getWidgetValueFromElement(val, layer));
                                        }
                                    } else
                                    {
                                        MyAlertDialogFragment.MakeGenericError("Error in " + layerRelativePath + ": multiple value elements without \"values\" parent", mActivity);
                                    }
                                }

                                addElement(widgetData);

                            } /* if node is element node */
                        } /* for each property  */
                    }

                } catch (SAXException e)
                {
                    Log.e("build SAXExc: ", e.getLocalizedMessage()
                            + uixml);
                } catch (IOException e)
                {
                    Log.e("build: IOExc", e.getLocalizedMessage());
                } catch (NumberFormatException e)
                {
                    Log.e("build: NumFmtEx", e.getLocalizedMessage());
                }
            } catch (UnsupportedEncodingException e)
            {
                Log.e("build: UnsuppEncEx", e.getLocalizedMessage());
            }
        } catch (ParserConfigurationException e1)
        {
            Log.e("build: ParserConfExc", e1.getLocalizedMessage());
        }

        return optionViewsHash;
    }

    private WidgetValue getWidgetValueFromElement(Element val, String layerName)
    {
        FileUtils fu = new FileUtils();
        WidgetValue wval = new WidgetValue(val.getAttribute("text"));
        wval.isValid = !val.hasAttribute("valid") || (val.hasAttribute("valid") && val.getAttribute("valid").compareTo("true") == 0);
        if (val.hasAttribute("icon") && val.getAttribute("icon").length() > 0)
            wval.icon = fu.loadBitmapFromStorage("layers/" + layerName + "/icons/" + val.getAttribute("icon"), mActivity);
        return wval;
    }


    private Pair addElement(WidgetData data)
    {
        Pair pair = null;
        String type = data.type;
        String name = data.name;
        boolean isOption = data.isOption;
        boolean isCategory = data.isCategory;
        int id = data.id;

        LinearLayout container = (LinearLayout) mActivity.findViewById(R.id.containerLayout);
        LinearLayout lo = new LinearLayout(mActivity);
        lo.setOrientation(LinearLayout.HORIZONTAL); /* items displayed in a row */
        lo.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        container.addView(lo);
        CheckBox cb = null;
        /* text label or checkbox for options */

        View nameView = null;
        if (isOption)
        {
            cb = new CheckBox(mActivity);
            cb.setId(OPTION_CB_ID + mOptionCheckBoxCount);
            cb.setText(name);
            cb.setChecked(false);
            pair = new Pair(cb.getId(), id);
            cb.setOnCheckedChangeListener(mActivity);
            mOptionCheckBoxCount++;
            lo.addView(cb);
            nameView = cb;
        } else
        {
            TextView label = new TextView(mActivity);
            label.setText(name);
            lo.addView(label);
            nameView = label;
        }
        ViewGroup.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        nameView.setLayoutParams(lp);
        ViewGroup.LayoutParams lp2 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2.0f);

		/* widget */
        if (type.compareTo("EditText") == 0)
        {
            EditText editText = new EditText(mActivity);
            editText.setId(id);
            if (data.values.size() > 0)
                editText.setText(data.values.get(0).text);
            editText.setLayoutParams(lp2);
            lo.addView(editText);
        } else if (type.compareTo("Spinner") == 0)
        {
            Spinner spin = new Spinner(mActivity);
            if (data.values.size() > 0)
            {
                DataValuesSpinnerAdapter adapter =
                        new DataValuesSpinnerAdapter(mActivity, R.layout.post_icon_text_spinner, data.values, mActivity);
                spin.setId(id);
                spin.setAdapter(adapter);
            }
            spin.setLayoutParams(lp2);
            lo.addView(spin);
        } else if (type.compareTo("Button") == 0 && data.values.size() == 1)
        {
            Button b = new Button(mActivity);
            WidgetValue v = data.values.get(0);
            b.setText(v.text);
            b.setOnClickListener(mActivity);
            b.setId(id);
            b.setLayoutParams(lp2);
            lo.addView(b);
        }

        if (cb != null)
            mActivity.findViewById(id).setEnabled(cb.isChecked());


        return pair;
    }
}
