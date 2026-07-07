package com.helicalinsight.efw.jasperintegration;

import java.io.File;

import org.junit.Test;

import com.helicalinsight.efw.io.delete.EFWCEDeleteRule;
import com.helicalinsight.efw.io.delete.HCRDeleteRule;

public class HCRDeleteRuleTest {

	@Test
	public void testIsDeletable() {
		HCRDeleteRule deleteRule = new HCRDeleteRule();
		File file = new File("test.txt");
				
		deleteRule.isDeletable(file);
	}
	@Test
	public void testDelete() {
		HCRDeleteRule deleteRule = new HCRDeleteRule();
		File file = new File("test.txt");
		deleteRule.delete(file);
	}
	@Test
	public void testisThreadSafeToCache() {
		HCRDeleteRule deleteRule = new HCRDeleteRule();
		deleteRule.isThreadSafeToCache();
	}
	@Test
	public void testEFWCEDeleteRule(){
		EFWCEDeleteRule efwceDeleteRule = new EFWCEDeleteRule();
		File file = new File("test.txt");
		efwceDeleteRule.isDeletable(file);
	}
	@Test
	public void testdelete() {
		EFWCEDeleteRule efwceDeleteRule = new EFWCEDeleteRule();
		File file = new File("test.txt");
		efwceDeleteRule.delete(file);	
	}
	@Test
	public void testisThreadSafeToCache1() {
		EFWCEDeleteRule efwceDeleteRule = new EFWCEDeleteRule();
		efwceDeleteRule.isThreadSafeToCache();	
	}
}
