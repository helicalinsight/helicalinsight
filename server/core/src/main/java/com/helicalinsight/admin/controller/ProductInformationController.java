package com.helicalinsight.admin.controller;


import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.helicalinsight.admin.dto.ProductInformationDTO;
import com.helicalinsight.admin.utils.DateUtils;
import com.helicalinsight.efw.ApplicationInformation;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.model.LicenseMetadata;

@RequestMapping("/v2")
@RestController
public class ProductInformationController {
	
	@GetMapping(value = "/getProductInformation", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProductInformationDTO> getProductInformation() {
		
		 ApplicationInformation appInfo = ApplicationInformation.getInstance();
		 LicenseMetadata licenseMetadata = ApplicationProperties.getInstance().getLicenseMetadata();
		 
		 //NOTE : Add more limits here. 
		 ProductInformationDTO.Limits  licenseLimits = new ProductInformationDTO.Limits();
		 licenseLimits.setUsers(licenseMetadata.userLimit());
		
		 licenseLimits.setEmbed(licenseMetadata.embed());
		 
		 licenseLimits.setModules(licenseMetadata.modules());
		 
		 ProductInformationDTO.Builder builder = ProductInformationDTO.builder();
		 builder.productName(appInfo.getProductName())
				.buildNumber(appInfo.getBuild())
				.limits(licenseLimits)
				.version(appInfo.getVersion())
				.productType(appInfo.getProductType());
			
		String licenseKeyType = licenseMetadata.licenseKeyType();
		
		if ( StringUtils.isBlank(licenseKeyType) || "NA".equalsIgnoreCase(licenseKeyType)) {
			builder.licenseType(appInfo.getSourceCodeType());
			builder.expiration("NA");
		}
		else {
			builder.licenseType(licenseKeyType);
		}
		
		if (!"Unlimited".equalsIgnoreCase(licenseKeyType)) {
			builder.expiration(DateUtils.convertDateToString(licenseMetadata.lastDate(),"dd/MM/yyyy"));
		}
					
		ProductInformationDTO productInformation = builder.build();
			
		return ResponseEntity.ok(productInformation);
	}
	
	
}
