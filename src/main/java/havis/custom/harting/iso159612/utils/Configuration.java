package havis.custom.harting.iso159612.utils;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.Constants;

/**
 * Configuration Interface {@link http
 * ://www.gwtproject.org/javadoc/latest/com/google
 * /gwt/i18n/client/Constants.html}
 */
public interface Configuration extends Constants {

	public static final Configuration APP_CONF = GWT.create(Configuration.class);

	String configDatabaseName();

	String configDatabaseVersion();

	String configDatabaseDesc();

	String dbKeyWordSettings();

	String dbKeyWordTemplates();

}
