package havis.app.iso159612.hw.impl;

import havis.application.common.data.JsonArray;
import havis.application.component.db.Callback;
import havis.application.component.db.Database;
import havis.application.component.db.Error;
import havis.app.iso159612.data.Settings;
import havis.app.iso159612.data.Template;
import havis.app.iso159612.hw.ConfigDao;
import havis.app.iso159612.utils.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Specifies the data source of the {@link ConfigDao} interface. Local database
 * in this implementation represents the data store.
 */
public class ConfigDaoImpl implements ConfigDao {

	private Database configDatabase;

	public ConfigDaoImpl() {
		configDatabase = DBAccess.getConfigDatabaseInstance();
	}

	@Override
	public void loadSettings(final Callback<Settings> callback) {
		getConfigValue(Configuration.APP_CONF.dbKeyWordSettings(), new Callback<String>() {

			@Override
			public void onFailure(Error error) {
				callback.onFailure(error);
			}

			@Override
			public void onSuccess(String result) {
				if (result != null && !result.isEmpty()) {
					callback.onSuccess((Settings) Settings.parse(result));
				} else {
					callback.onSuccess(Settings.newInstance());
				}
			}
		});

	}

	public void saveSettings(Settings settings, Callback<Void> callback) {
		setConfigValue(Configuration.APP_CONF.dbKeyWordSettings(), settings.stringify(), callback);
	}

	@Override
	public void loadTemplates(final Callback<List<Template>> callback) {
		getConfigValue(Configuration.APP_CONF.dbKeyWordTemplates(), new Callback<String>() {

			@Override
			public void onFailure(Error error) {
				callback.onFailure(error);
			}

			@Override
			public void onSuccess(String result) {
				List<Template> templates = new ArrayList<Template>();
				if (result != null && !result.isEmpty()) {
					JsonArray<Template> jsonTemplates = JsonArray.parse(result);
					for (int i = 0; i < jsonTemplates.length(); i++)
						templates.add(jsonTemplates.get(i));
				}
				callback.onSuccess(templates);
			}
		});
	}

	@Override
	public void saveTemplates(List<Template> templates, Callback<Void> callback) {
		JsonArray<Template> jsonTemplates = JsonArray.createInstance();
		for (Template t : templates)
			jsonTemplates.push(t);
		setConfigValue(Configuration.APP_CONF.dbKeyWordTemplates(), jsonTemplates.stringify(), callback);
	}

	private void getConfigValue(String key, Callback<String> callback) {
		configDatabase.get(key, callback);
	}

	private void setConfigValue(String key, String value, Callback<Void> callback) {
		configDatabase.put(key.toString(), value, callback);
	}
}
