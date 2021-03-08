package havis.custom.harting.iso159612.rest;

import havis.custom.harting.iso159612.imager.WebScannerManager;
import havis.custom.harting.iso159612.rest.provider.WebcamScannerExceptionMapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Application;

public class RESTApplication extends Application {

	private final static String PROVIDERS = "javax.ws.rs.ext.Providers";

	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();
	private Map<String, Object> properties = new HashMap<>();

	public RESTApplication(WebScannerManager main) {
		singletons.add(new WebcamScannerService(main));
		properties.put(PROVIDERS, new Class<?>[] { WebcamScannerExceptionMapper.class });
	}

	@Override
	public Set<Class<?>> getClasses() {
		return empty;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}

	@Override
	public Map<String, Object> getProperties() {
		return properties;
	}
}