package org.xidobi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static ClassLoader bundleClassLoader;

	public void start(BundleContext bundleContext) throws Exception {
		bundleClassLoader=bundleContext.getClass().getClassLoader();
	}

	public void stop(BundleContext bundleContext) throws Exception {}

	/**
	 * @return the bundleClassLoader
	 */
	public static ClassLoader getBundleClassLoader() {
		return bundleClassLoader;
	}

	

}
