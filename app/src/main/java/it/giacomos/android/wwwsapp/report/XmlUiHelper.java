package it.giacomos.android.wwwsapp.report;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
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

import it.giacomos.android.wwwsapp.MyAlertDialogFragment;
import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.layers.FileUtils;
import it.giacomos.android.wwwsapp.report.widgets.DataValuesSpinnerAdapter;
import it.giacomos.android.wwwsapp.report.widgets.RCheckBox;
import it.giacomos.android.wwwsapp.report.widgets.REditText;
import it.giacomos.android.wwwsapp.report.widgets.RSpinner;
import it.giacomos.android.wwwsapp.report.widgets.TextValueInterface;

/**
 * Created by giacomo on 3/06/15.
 */
public class XmlUiHelper implements RSpinner.OnItemSelectedListener,
        RCheckBox.OnCheckedChangeListener, TextWatcher

{
    public static final String UI_TYPE_REPORT = "report";
    public static final String UI_TYPE_REQUEST = "request";

    private HashMap<String, String> mPlaceholders;
    private ViewGroup mContainerViewGroup;
    private Context mContext;
    private int mOptionCheckBoxCount;
    private final int OPTION_CB_ID = 1126332445;
    private HashMap<Integer, Integer> mOptionViewsHash = new HashMap<Integer, Integer>();
    private ArrayList<WidgetData> mData;
    private String mTitle;

    public XmlUiHelper(Context ctx, ViewGroup vg)
    {
        mContainerViewGroup = vg;
        mContext = ctx;
        mOptionCheckBoxCount = 0;
        mData = new ArrayList<WidgetData>();
        mTitle = "No title";
    }

    private boolean inputsValid()
    {
        String repr, text = "";
        boolean isValid = true;

        for (WidgetData d : mData)
        {
            isValid = true;
            TextValueInterface textValueInterface = null;
            View view = mContainerViewGroup.findViewById(d.id);
            try
            {
                textValueInterface = (TextValueInterface) view;
                if (textValueInterface != null)
                    text = textValueInterface.getValue();
                for (WidgetValue wv : d.values)
                {
                    if (!wv.isValid && wv.text.compareTo(text) == 0 && view != null && view.isEnabled())
                        isValid = false;
                    if (textValueInterface != null)
                        textValueInterface.setValidData(isValid);
                }
            } catch (ClassCastException e)
            {
                Log.e("ReportUIHel.inputsValid", "No TextValueInterface with id " + d.id + ": " + e.getLocalizedMessage());
            }
        }
        return isValid;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public void build(String layer, String locality, String ui_type)
    {
        int id = 100;
        String uixml = "";
        FileUtils fu = new FileUtils();

        String layerRelativePath = "layers/" + layer + "/" + layer + "_ui.xml";
        String repr, text, name, type;
        boolean validate = false, category = false;
        uixml = fu.loadFromStorage(layerRelativePath, mContext);

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
                    Element ui = null;
                    NodeList uiElements = uis.getElementsByTagName("ui");
                    String layername = uis.getAttribute("name");
                    if (layername.compareTo(layer) == 0)
                    {
                    /* find the ui elements with attribute type equal to the ui_type
                     * we want to parse (report, request)
                     */
                        for (int i = 0; i < uiElements.getLength(); i++)
                        {
                            Element uiEl = (Element) uiElements.item(i);
                            Log.e("XmlUiHelper.build", " seeing if element ui has attribute type " + uiEl.getAttribute("type")
                                    + " and equals " + ui_type);
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
                                    mTitle = title.getAttribute("text");
                            }
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
                                            Log.e("XmlUiHelper.build", "Error in " + layerRelativePath + ": empty \"values\" tag");
                                        } else
                                        {
                                            for (int n = 0; n < valuelist.getLength(); n++)
                                            {
                                                Node valnode = valuelist.item(n);
                                                if (valnode.getNodeType() == Node.ELEMENT_NODE)
                                                {
                                                    Element val = (Element) valnode;
                                                    Log.e("Builder.build", " add value " + val.getAttribute("text"));
                                                    widgetData.addValue(getWidgetValueFromElement(val, layer));
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
                                                widgetData.addValue(getWidgetValueFromElement(val, layer));
                                            }
                                        }
                                        else
                                        {
                                            Log.e("XmlUiHelper.build", "Error in " + layerRelativePath + ": multiple value elements without \"values\" parent");
                                        }
                                    }

                                    int checkboxId = addElement(widgetData);
                                    mData.add(widgetData);
                                    if (checkboxId > 0)
                                        mOptionViewsHash.put(checkboxId, widgetData.id);

                                } /* if node is element node */
                            } /* for elements */
                        } /* if ui != null */
                    } /* if (layername.compareTo(layer) == 0) */

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

    }

    private WidgetValue getWidgetValueFromElement(Element val, String layerName)
    {
        FileUtils fu = new FileUtils();
        String text = val.getAttribute("text");
        String value = val.getTextContent();
        if(value == null || value.isEmpty()) /* initialize value from the text */
            value = text;
        WidgetValue wval = new WidgetValue(text, value);
        wval.isValid = !val.hasAttribute("valid") || (val.hasAttribute("valid") && val.getAttribute("valid").compareTo("true") == 0);
        if (val.hasAttribute("icon") && val.getAttribute("icon").length() > 0)
            wval.icon = fu.loadBitmapFromStorage("layers/" + layerName + "/bmps/" + val.getAttribute("icon") + ".bmp", mContext);
        return wval;
    }

    public void addTextPlaceHolder(String s, String placeH)
    {
        if (mPlaceholders == null)
            mPlaceholders = new HashMap<String, String>();
        mPlaceholders.put(s, placeH);
    }

    /**
     * Adds an interface widget to the layout according to the widget data specified.
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

        LinearLayout container = (LinearLayout) mContainerViewGroup.findViewById(R.id.requestContainerLayout);
        LinearLayout lo = new LinearLayout(mContext);
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
            cb = new CheckBox(mContext);
            checkboxId = OPTION_CB_ID + mOptionCheckBoxCount;
            cb.setId(checkboxId);
            cb.setText(name);
            cb.setChecked(false);
            mOptionCheckBoxCount++;
            lo.addView(cb);
            nameView = cb;
            cb.setOnCheckedChangeListener(this);
        }
        else
        {
            TextView label = new TextView(mContext);
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
            REditText editText = new REditText(mContext);
            editText.setId(id);
            if (data.values.size() > 0)
                editText.setText(mFilterText(data.values.get(0).text));
            editText.setLayoutParams(lp2);
            editText.addTextChangedListener(this);
            lo.addView(editText);
        }
        else if (repr.compareTo("Spinner") == 0)
        {
            RSpinner spin = new RSpinner(mContext);
            spin.setId(id);
            if (data.values.size() > 0)
            {
                DataValuesSpinnerAdapter adapter =
                        new DataValuesSpinnerAdapter(mContext, R.layout.post_icon_text_spinner, data.values);
                spin.setAdapter(adapter);
                spin.setOnItemSelectedListener(this);
            }
            spin.setLayoutParams(lp2);
            lo.addView(spin);
        }
        else if (repr.compareTo("Checkbox") == 0)
        {
            RCheckBox rcb = new RCheckBox(mContext);
            if (data.values.size() > 0)
            {
                WidgetValue v = data.values.get(0);
                if (v.text.compareTo("true") == 0)
                    rcb.setChecked(true);
            }
            rcb.setText(""); /* there is the label TextView */
            rcb.setOnCheckedChangeListener(this);
            rcb.setId(id);
            rcb.setLayoutParams(lp2);
            lo.addView(rcb);
        }

        if (cb != null)
            mContainerViewGroup.findViewById(id).setEnabled(cb.isChecked());


        return checkboxId;
    }

    private String mFilterText(String text)
    {
        String out;
        if (mPlaceholders.containsKey(text))
            out = mPlaceholders.get(text);
        else
            out = text;
        if (out == null || out.length() == 0)
            out = "-";
        return out;
    }

    public WidgetData getWidgetDataAt(long id)
    {
        for (WidgetData wd : mData)
            if (wd.id == id)
                return wd;
        return null;
    }

    public ArrayList<WidgetData> getData()
    {
        return mData;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        mContainerViewGroup.findViewById(R.id.buttonOk).setEnabled(inputsValid());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        int optionCheckboxId = buttonView.getId();
        if(mOptionViewsHash.containsKey(optionCheckboxId))
        {
            /* An "option" checkbox has been checked. Enable or disable the corresponding element */
            int viewId = this.mOptionViewsHash.get(buttonView.getId());
            mContainerViewGroup.findViewById(viewId).setEnabled(isChecked);
            mContainerViewGroup.findViewById(R.id.buttonOk).setEnabled(inputsValid());
        }
        /* else the checkbox is a property element */
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        mContainerViewGroup.findViewById(R.id.buttonOk).setEnabled(inputsValid());

    }

    @Override
    public void afterTextChanged(Editable s)
    {

    }


}
