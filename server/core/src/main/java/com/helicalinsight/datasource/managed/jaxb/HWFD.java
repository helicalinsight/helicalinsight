package com.helicalinsight.datasource.managed.jaxb;

import com.helicalinsight.efw.utility.AdapterCDATA;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Created by author on 9/9/2019.
 *
 * @author Rajesh
 */

@Component
@Scope("prototype")
@XmlRootElement(name = "HWFD")
@XmlAccessorType(XmlAccessType.FIELD)
public class HWFD {

    @XmlElement(name = "state")
    @XmlJavaTypeAdapter(AdapterCDATA.class)
    private String state;

    @XmlElement(name = "diagram")
    @XmlJavaTypeAdapter(AdapterCDATA.class)
    private String diagramData;

    @XmlElement(name = "hwfFileName")
    private String hwfFile;

    @XmlElement(name = "hwfFileDir")
    private String hwfFileDir;

    public String getHwfFile() {
        return hwfFile;
    }

    public void setHwfFile(String hwfFile) {
        this.hwfFile = hwfFile;
    }

    public String getHwfFileDir() {
        return hwfFileDir;
    }

    public void setHwfFileDir(String hwfFileDir) {
        this.hwfFileDir = hwfFileDir;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDiagramData() {
        return diagramData;
    }

    public void setDiagramData(String diagramData) {
        this.diagramData = diagramData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HWFD hwfd = (HWFD) o;

        if (diagramData != null ? !diagramData.equals(hwfd.diagramData) : hwfd.diagramData != null) return false;
        if (hwfFile != null ? !hwfFile.equals(hwfd.hwfFile) : hwfd.hwfFile != null) return false;
        if (hwfFileDir != null ? !hwfFileDir.equals(hwfd.hwfFileDir) : hwfd.hwfFileDir != null) return false;
        if (state != null ? !state.equals(hwfd.state) : hwfd.state != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = state != null ? state.hashCode() : 0;
        result = 31 * result + (diagramData != null ? diagramData.hashCode() : 0);
        result = 31 * result + (hwfFile != null ? hwfFile.hashCode() : 0);
        result = 31 * result + (hwfFileDir != null ? hwfFileDir.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "HWFD{" +
                "state='" + state + '\'' +
                ", diagramData='" + diagramData + '\'' +
                ", hwfFile='" + hwfFile + '\'' +
                ", hwfFileDir='" + hwfFileDir + '\'' +
                '}';
    }
}
