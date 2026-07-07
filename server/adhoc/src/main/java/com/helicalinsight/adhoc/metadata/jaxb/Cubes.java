package com.helicalinsight.adhoc.metadata.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**

 * Created by helical021 on 1/24/2020.
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "cubes")
@XmlAccessorType(XmlAccessType.FIELD)
public class Cubes {
    @Override
    public String toString() {
        return "Cubes{" +
                "cubeList=" + cubeList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cubes cubes = (Cubes) o;

        if (cubeList != null ? !cubeList.equals(cubes.cubeList) : cubes.cubeList != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return cubeList != null ? cubeList.hashCode() : 0;
    }

    @XmlElement(name = "cube")
    private List<Cube> cubeList;

    public List<Cube> getCubeList() {
        return cubeList;
    }

    public void setCubeList(List<Cube> cubeList) {
        this.cubeList = cubeList;
    }

}
