package jp.bo.bocc.system.apiconfig.bean;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * @author manhnt
 */
public class AppContext implements EnvironmentAware {

	private static Environment env;

	public static Environment getEnv() {
		return env;
	}

	@Override
	public void setEnvironment(Environment environment) {
		AppContext.env = environment;
	}
}
