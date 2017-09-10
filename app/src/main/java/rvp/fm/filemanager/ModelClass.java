package rvp.fm.filemanager;

/**
 * Created by radhikaparmar on 06/09/17.
 */

public class ModelClass {
    Boolean type;
    Boolean selected;
    String nameOfFolder;
    String path;

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getTypeOfFolder() {
        return type;
    }

    public void setTypeOfFolder(Boolean type) {
        this.type = type;
    }

    public String getNameOfFolder() {
        return nameOfFolder;
    }

    public void setNameOfFolder(String nameOfFolder) {
        this.nameOfFolder = nameOfFolder;
    }

}
