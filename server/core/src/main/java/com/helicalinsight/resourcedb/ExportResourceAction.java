package com.helicalinsight.resourcedb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("exportResourceAction")
@Scope("prototype")
public class ExportResourceAction extends AbstractResourceAction {
    @Override
    public Boolean performAction() {
        return null;
    }
}
