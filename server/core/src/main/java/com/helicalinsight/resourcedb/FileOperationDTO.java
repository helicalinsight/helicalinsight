package com.helicalinsight.resourcedb;

import com.helicalinsight.core.request.PayLoad;

import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class FileOperationDTO implements PayLoad {

    private List<String> createSourceArray;

    private List<List<String>> renameSourceArray;

    public FileOperationDTO(){
        createSourceArray = new ArrayList<>();
        renameSourceArray = new ArrayList<>();
    }

    @NotEmpty(message = "Source array must not be empty")
    private String sourceArray;

    @NotEmpty(message = "Action must not be empty")
    private String action;

    public String getFormData() {
		return formData;
	}

	public void setFormData(String formData) {
		this.formData = formData;
	}

	private String folderName;

    private String isPublic;

    private String isVisible;
    
    private String formData;

    public String getSourceArray() {
        return sourceArray;
    }

    public void setSourceArray(String sourceArray) {
        this.sourceArray = sourceArray;

        if(action.equalsIgnoreCase("newfolder")){
            createSourceArray.add(sourceArray);
            setCreateSourceArray(createSourceArray);
        }else if(action.equalsIgnoreCase("rename")){
            List<String> renameSourceArrayList = new ArrayList<>();
            renameSourceArrayList.add(sourceArray);
            renameSourceArray.add(renameSourceArrayList);
            setRenameSourceArray(renameSourceArray);
        }
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }


    public List<String> getCreateSourceArray() {
        return createSourceArray;
    }

    public void setCreateSourceArray(List<String> createSourceArray) {
        this.createSourceArray = createSourceArray;
    }

    public List<List<String>> getRenameSourceArray() {
        return renameSourceArray;
    }

    public void setRenameSourceArray(List<List<String>> renameSourceArray) {
        this.renameSourceArray = renameSourceArray;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public String getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(String isVisible) {
        this.isVisible = isVisible;
    }

    @Override
    public String toString() {
        return "FolderPayLoad{" +
                "sourceArray=" + sourceArray;
    }
}