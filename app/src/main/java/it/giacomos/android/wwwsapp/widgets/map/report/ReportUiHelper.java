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
public class ReportUiHelper
{
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
        public boolean isOption, isCategory;
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
            if(values == null)
                values = new ArrayList<WidgetValue>();
            values.add(v);
        }
    }

    private class DataValuesSpinnerAdapter extends ArrayAdapter<WidgetValue>
    {

        public DataValuesSpinnerAdapter(Context context, int resource,
                                        ArrayList<WidgetValue> data, Activity activity)
        {
            super(context, resource);
            this.addAll(data);
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
            if (position < this.getCount())
            {
                WidgetValue v = this.getItem(position);
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

    private PostActivity mActivity;
    private int mOptionCheckBoxCount;
    private final int OPTION_CB_ID = 1126332445;
    private ArrayList<WidgetData> mData;

    public ReportUiHelper(PostActivity a)
    {
        mActivity = a;
        mOptionCheckBoxCount = 0;
        mData = new ArrayList<WidgetData>();
    }


    public boolean verify()
    {
        String repr, text = "";
        for(WidgetData d : mData)
        {
            View view = mActivity.findViewById(d.id);
            if(d.representation.compareTo("Spinner") == 0 && view != null)
            {
                Spinner sp = (Spinner) view;
                if(sp != null)
                {
                    DataValuesSpinnerAdapter adapter = (DataValuesSpinnerAdapter) sp.getAdapter();
                    WidgetValue v = adapter.getItem(sp.getSelectedItemPosition());
                    text = v.text;
                }
            }
            else if(d.representation.compareTo("EditText") == 0 && view != null)
            {
                EditText et = (EditText) view;
                text = et.getText().toString();
            }
            else if(d.representation.compareTo("CheckBox") == 0)
            {
                CheckBox cb = (CheckBox) view;
                if(cb.isChecked())
                    text = "true";
                else
                    text = "false";
            }
            for(WidgetValue wv : d.values)
            {
                if(!wv.isValid && wv.text.compareTo(text) == 0 && view != null)
                    return false;
            }
        }
        return true;
    }

    public HashMap<Integer, Integer> build(String layer, String locality)
    {
        int id = 100;
        String uixml = "";
        FileUtils fu = new FileUtils();
        HashMap<Integer, Integer> optionViewsHash = new HashMap<Integer, Integer>();
        Pair pair;
        String layerRelativePath = "layers/" + layer + "/" + layer +  "_ui.xml";
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

                                WidgetData widgetData = new WidgetData(id + i, name, type, text, repr);
                                widgetData.isCategory = prop.hasAttribute("category") && prop.getAttribute("category").compareTo("true") == 0;
                                widgetData.isOption = prop.hasAttribute("is_option") && prop.getAttribute("is_option").compareTo("true") == 0;

                                NodeList values = prop.getElementsByTagName("values");
                                Log.e("Builder.build", " gettubg avlues " + values.getLength());
                                if (values.getLength() == 1)
                                {
                                    NodeList valuelist = prop.getElementsByTagName("value");
                                    if (valuelist.getLength() == 0)
                                    {
                                        MyAlertDialogFragment.MakeGenericError("Error in " + layerRelativePath + ": empty \"values\" tag", mActivity);
                                    }
                                    else
                                    {
                                        for (int n = 0; n < valuelist.getLength(); n++)
                                        {
                                            Node valnode = valuelist.item(n);
                                            if (valnode.getNodeType() == Node.ELEMENT_NODE)
                                            {
                                                Element val = (Element) valnode;
                                                Log.e("Builder.build", " add value " + val.getAttribute("text"));
                                                widgetData.addValue(getWidgetValueFromElement(val, layer));
                                            }
                                            else
                                                Log.e("Builder.build", " valnode is not element node " + valnode);
                                        }
                                    }
                                }
                                else
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

                                int checkboxId = addElement(widgetData);
                                mData.add(widgetData);
                                if(checkboxId > 0)
                                    optionViewsHash.put(checkboxId, widgetData.id);

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
            wval.icon = fu.loadBitmapFromStorage("layers/" + layerName + "/bmps/" + val.getAttribute("icon") + ".bmp", mActivity);
        return wval;
    }

    /** Adds an interface widget to the layout according to the widget data specified.
     * If the data is optional, a checkbox will be placed in order to enable the option.
     * In this case, the id of the checkbox is returned, -1 is returned in all the other cases.
     *
     * @param data a WidgetData object describing how the element must be.
     * @return the id of the checkbox that enables an optional field, -1 if the element is not optional
     */
    private int addElement(WidgetData data)
    {
        int checkboxId = -1;
        String type = data.type;
        String name = data.name;
        String repr = data.representation;
        boolean isOption = data.isOption;
        boolean isCategory = data.isCategory;
        int id = data.id;

        Log.e("Builder.addElement", " adding element " + name + " id " + id + " repr " + repr);

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
            checkboxId = OPTION_CB_ID + mOptionCheckBoxCount;
            cb.setId(checkboxId);
            cb.setText(name);
            cb.setChecked(false);
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
        if (repr.compareTo("EditText") == 0)
        {
            EditText editText = new EditText(mActivity);
            editText.setId(id);
            if (data.values.size() > 0)
                editText.setText(data.values.get(0).text);
            editText.setLayoutParams(lp2);
            lo.addView(editText);
        }
        else if (repr.compareTo("Spinner") == 0)
        {
            Spinner spin = new Spinner(mActivity);
            spin.setId(id);
            Log.e("SPINNER", " data values size " + data.values.size());
            if (data.values.size() > 0)
            {
                DataValuesSpinnerAdapter adapter =
                        new DataValuesSpinnerAdapter(mActivity, R.layout.post_icon_text_spinner, data.values, mActivity);
                spin.setAdapter(adapter);
            }
            spin.setLayoutParams(lp2);
            lo.addView(spin);
        }
        else if (repr.compareTo("Button") == 0 && data.values.size() == 1)
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


        return checkboxId;
    }
}
