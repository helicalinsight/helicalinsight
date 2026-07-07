package com.helicalinsight.efw.components;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.helicalinsight.admin.model.HIPhase;
import com.helicalinsight.admin.service.PhaseDetailsService;
import com.helicalinsight.efw.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component("hIPhasesInitializer")
public class HIPhasesInitializer implements ApplicationValuesInitializer {

	private final PhaseDetailsService phaseDetailsService;
	private Map<String,HIPhase> hiResourcePhases;
	@Override
	public void initializeData(ApplicationContext applicationContext) {
		hiResourcePhases = new HashMap<>();
		List<HIPhase> phases = new ArrayList<>();
		try {
			String filePath = ApplicationProperties.getInstance().getSystemDirectory() + File.separator + "Admin"
					+ File.separator + "hiResourcePhases.xml";
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new File(filePath));
			document.normalize();
			NodeList processNodes = document.getElementsByTagName("process");
			for(int i=0;i<processNodes.getLength();i++) {
				Element processElement = (Element) processNodes.item(i);
				String type = processElement.getParentNode().getNodeName();
				NodeList phaseNodes = processElement.getElementsByTagName("phase");
				for(int j=0;j<phaseNodes.getLength();j++) {
					Element phase = (Element) phaseNodes.item(j);
					HIPhase newPhase = new HIPhase();
					newPhase.setId(Integer.valueOf(phase.getAttribute("id")));
					newPhase.setDescription(phase.getAttribute("description"));
					newPhase.setStatus(phase.getAttribute("name"));
					newPhase.setResourceType(type);
					newPhase.setProcess(processElement.getAttribute("name"));
					hiResourcePhases.put(type.toUpperCase()+"_"+phase.getAttribute("name"), newPhase);
					phases.add(newPhase);
				}
			}
			
		} catch (Exception e) {
			log.error("Error occurred while reading hiResourcePhases.xml file");
		}
		finally {
			ApplicationProperties.getInstance().setHIResourcePhases(hiResourcePhases);
		}

		List<HIPhase> existingPhases = phaseDetailsService.getAllPhases();
		phases.removeAll(existingPhases);
		if (!phases.isEmpty())
			phases.forEach(phaseDetailsService::addPhase);
	}

}
