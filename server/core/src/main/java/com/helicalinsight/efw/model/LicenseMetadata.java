package com.helicalinsight.efw.model;

import java.time.LocalDate;
import java.util.Set;

/**
 * A Record to hold the license metadata.
 * 
 * @since 6.1
 * @author HDEV067
 * 
 */
public record LicenseMetadata (
		
		String licenceAuthority,
		String email,
		String licenseKeyType,
		LocalDate issuedDate,
		LocalDate lastDate,
		String macAddress,
		Integer userLimit,
		Embed embed,
		Set<Module> modules 
		) {
	
	public static class Builder {
		
		private String licenseAuthority;
		private String email;
		private String licenseKeyType;
		private LocalDate issuedDate;
		private LocalDate lastDate;
		private String macAddress;
		private Integer userLimit;
		private Embed embed;
		private Set<Module> modules;
		
		private Boolean built = false;
		
		
		
		public Builder licenseAuthority(String licenseAuthority) {
			this.licenseAuthority = licenseAuthority;
			return this;
		} 
		
		public Builder email(String email) {
			this.email = email;
			return this;
		}
		
		public Builder licenseKeyType(String licenseKeyType) {
			this.licenseKeyType = licenseKeyType;
			return this;
		}
		
		public Builder issuedDate(LocalDate issuedDate) {
			this.issuedDate = issuedDate;
			return this;
		}
		
		public Builder lastDate(LocalDate lastDate) {
			this.lastDate = lastDate;
			return this;
		}
		
		public Builder macAddress(String macAddress) {
			this.macAddress = macAddress;
			return this;
		}
		
		public Builder userLimit(Integer userLimit) {
			this.userLimit = userLimit;
			return this;
		}
		
		public Builder embed(Embed embed) {
			this.embed = embed;
			return this;
		}
		
		public Builder modules(Set<Module> modules) {
			this.modules = modules;
			return this;
		}
		
		public LicenseMetadata build() {
			if(built) {
				throw new IllegalStateException("The builder has already been used to build a LicenseMetadata");
			}
			
			this.built = true;
			
			return new LicenseMetadata(licenseAuthority, email, licenseKeyType, issuedDate, lastDate, macAddress, userLimit, embed, modules);
		}
		
	}
}


