package com.helicalinsight.resourcedb;


import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.efw.utility.JsonUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("deleteResourceAction")
@Scope("prototype")
public class DeleteResourceAction extends AbstractResourceAction {

    private static final Logger logger = LoggerFactory.getLogger(DeleteResourceAction.class);

    @Override
    public Boolean performAction() {
        AtomicBoolean isSaved = new AtomicBoolean(false);
        FileOperationDTO payLoad = (FileOperationDTO) this.getPayLoad();
        List<String> sourceList = JsonToCollectionUtil.getList(payLoad.getSourceArray());
        sourceList.stream().forEach(location -> {
            try {
                isSaved.set(checkAndDeleteEntryInDb(location));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return isSaved.get();
    }

    private boolean checkAndDeleteEntryInDb(String location) {
    	
        HIResource resourceByUrl = hiResourceServiceDB.getResourceByUrl(location);
        resourceByUrl.setVisible(false);
        hiResourceServiceDB.deleteHIResourceById(resourceByUrl.getResourceId());
        setMessage("Delete operation is successful");
        return true;
    }


    private static boolean isContain(String source, String subItem) {
        //System.out.println(source);
        subItem = subItem.replace("/", "");
        String pattern = "\\b" + subItem + "\\b";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(source);
        //System.out.println(m.find());
        return m.find();
    }
}
