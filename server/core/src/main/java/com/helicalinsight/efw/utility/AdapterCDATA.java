package com.helicalinsight.efw.utility;

import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created by author on 20-03-13
 *
 * @author Rajasekhar
 */
public class AdapterCDATA extends XmlAdapter<String, String> {

    @NotNull
    @Override
    public String marshal(String arg0) throws Exception {
        return "<![CDATA[" + arg0 + "]]>";
    }

    @Override
    public String unmarshal(String arg0) throws Exception {
        return arg0;
    }

}