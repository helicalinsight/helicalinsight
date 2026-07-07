package com.helicalinsight.resourcedb;

public class DTOFilter {
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof NewFolderAction){
            return false;
        }
        return true;
    }
}
