package com.helicalinsight.resourcedb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("moveResourceAction")
@Scope("prototype")
public class MoveResourceAction extends AbstractResourceAction {
    @Override
    public Boolean performAction() {
        return null;
    }
}
