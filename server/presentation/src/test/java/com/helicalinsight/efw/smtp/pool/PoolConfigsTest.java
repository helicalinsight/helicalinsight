package com.helicalinsight.efw.smtp.pool;

import org.junit.Test;

public class PoolConfigsTest {

	@Test
	public void testdefaultConfig() {
		PoolConfigs config = new PoolConfigs();
		PoolConfigs.standardConfig();
		PoolConfigs.defaultConfig();
		PoolConfigs.standardConfig(1, 2, 3, 4, 5, 6);
	}
}
