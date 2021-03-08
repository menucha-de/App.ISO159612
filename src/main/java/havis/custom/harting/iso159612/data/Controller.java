package havis.custom.harting.iso159612.data;

import havis.middleware.tdt.Barcode;
import havis.middleware.tdt.PackedObjectInvestigator;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Controller {

	/**
	 * Shows an error message.
	 * 
	 * @param error
	 *            The error message.
	 */
	void showError(String error);

	/**
	 * @return The TDT PackedObjectInvestigator
	 */
	PackedObjectInvestigator getPackedObjectInvestigator();

	/**
	 * @return The TDT Barcode analyzer
	 */
	Barcode getBarcode();

	/**
	 * Gathers the names of all OIDs
	 * 
	 * @param callback
	 */
	void getOidNames(AsyncCallback<Map<String, String>> callback);

	/**
	 * Gathers the templates which have been created by the user
	 * 
	 * @param callback
	 */
	void getTemplates(AsyncCallback<List<Template>> callback);

	/**
	 * Saves the templates which have been created by the user
	 * 
	 * @param templates
	 *            The user created templates
	 * @param callback
	 */
	void setTemplates(List<Template> templates, AsyncCallback<Void> callback);

	/**
	 * Returns the application settings
	 * 
	 * @param callback
	 */
	void getSettings(AsyncCallback<Settings> callback);

	/**
	 * Saves the new application settings
	 * 
	 * @param settings
	 *            The new application settings
	 * @param callback
	 */
	void setSettings(Settings settings, AsyncCallback<Void> callback);
	
	boolean isHandheld();
	
}
