package havis.custom.harting.iso159612.hw;

import java.util.List;

import havis.application.component.db.Callback;
import havis.custom.harting.iso159612.data.Settings;
import havis.custom.harting.iso159612.data.Template;

public interface ConfigDao {

	/**
	 * loads settings
	 * 
	 * @param callback
	 */
	void loadSettings(Callback<Settings> callback);

	/**
	 * saves settings
	 * 
	 * @param settings
	 * @param callback
	 */
	void saveSettings(Settings settings, Callback<Void> callback);

	/**
	 * load templates
	 * 
	 * @param callback
	 */
	void loadTemplates(Callback<List<Template>> callback);

	/**
	 * saves templates
	 * 
	 * @param templates
	 * @param callback
	 */
	void saveTemplates(List<Template> templates, Callback<Void> callback);
}
