package havis.custom.harting.iso159612.ui.client;

import havis.application.component.db.Callback;
import havis.application.component.db.Error;
import havis.custom.harting.iso159612.data.Controller;
import havis.custom.harting.iso159612.data.Settings;
import havis.custom.harting.iso159612.data.Template;
import havis.custom.harting.iso159612.hw.ConfigDao;
import havis.custom.harting.iso159612.hw.impl.ConfigDaoImpl;
import havis.custom.harting.iso159612.ui.client.wrapper.ReadWritePanelWrapper;
import havis.custom.harting.iso159612.ui.client.wrapper.SettingsPanelWrapper;
import havis.custom.harting.iso159612.ui.client.wrapper.TemplatesPanelWrapper;
import havis.custom.harting.iso159612.utils.Translation;
import havis.middleware.tdt.Barcode;
import havis.middleware.tdt.PackedObjectInvestigator;
import havis.middleware.tdt.PackedObjectInvestigator.ColumnName;
import havis.middleware.tdt.TdtResources;
import havis.net.ui.shared.client.event.MessageEvent.MessageType;
import havis.net.ui.shared.client.widgets.CustomMessageWidget;
import havis.net.ui.shared.resourcebundle.ResourceBundle;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class ISO159612Rest extends Composite implements EntryPoint, Controller {

	private static ISO159612UiBinder uiBinder = GWT
			.create(ISO159612UiBinder.class);

	interface ISO159612UiBinder extends UiBinder<Widget, ISO159612Rest> {
	}

	private ResourceBundle res = ResourceBundle.INSTANCE;

	@UiField(provided = true)
	protected ReadWritePanelWrapper readWritePanelWrapper;

	@UiField(provided = true)
	protected TemplatesPanelWrapper templatesPanelWrapper;

	@UiField(provided = true)
	protected SettingsPanelWrapper settingsPanelWrapper;

	private final static int SLEEP_MS = 25;

	private static boolean loadingOids;

	private PackedObjectInvestigator tdtInvestigator;
	private Barcode tdtBarcode;

	private ConfigDao configDao;

	private Map<String, Map<ColumnName, String>> oids;

	public ISO159612Rest() {

		configDao = new ConfigDaoImpl();

		getOidNames(new AsyncCallback<Map<String, String>>() {

			@Override
			public void onFailure(Throwable caught) {
				showError(caught.getMessage());
			}

			@Override
			public void onSuccess(Map<String, String> result) {
				// OIDs haven been loaded.
			}
		});

		readWritePanelWrapper = new ReadWritePanelWrapper(
				Translation.LANG.menuItemReadWrite(), this);
		templatesPanelWrapper = new TemplatesPanelWrapper(
				Translation.LANG.menuItemTemplates(), this);
		settingsPanelWrapper = new SettingsPanelWrapper(
				Translation.LANG.menuItemSettings(), this);

		initWidget(uiBinder.createAndBindUi(this));

		readWritePanelWrapper.setOpen(true);
	}

	@Override
	public void onModuleLoad() {
		RootPanel.get().add(this);
		res.css().ensureInjected();
	}

	@Override
	public PackedObjectInvestigator getPackedObjectInvestigator() {
		if (tdtInvestigator == null)
			tdtInvestigator = new PackedObjectInvestigator(
					TdtResources.INSTANCE.tableForData().getText());
		return tdtInvestigator;
	}

	@Override
	public Barcode getBarcode() {
		if (tdtBarcode == null)
			tdtBarcode = new Barcode(getPackedObjectInvestigator());
		return tdtBarcode;
	}

	@Override
	public void getOidNames(final AsyncCallback<Map<String, String>> callback) {
		if (oids != null) {
			Map<String, String> result = new LinkedHashMap<String, String>();
			for (Map.Entry<String, Map<ColumnName, String>> entry : oids
					.entrySet())
				result.put(entry.getKey(),
						entry.getValue().get(ColumnName.FORMAT_9_NAME));
			callback.onSuccess(result);
		} else if (loadingOids) {
			Timer timer = new Timer() {
				@Override
				public void run() {
					getOidNames(callback);
				}
			};
			timer.schedule(SLEEP_MS);
		} else {
			loadingOids = true;
			Timer timer = new Timer() {
				@Override
				public void run() {
					try {
						oids = getPackedObjectInvestigator()
								.getOids("urn:oid:1.0.15961.9",
										ColumnName.FORMAT_9_NAME);
						getOidNames(callback);
					} catch (Exception e) {
						callback.onFailure(e);
					}
				}
			};
			timer.schedule(0);
		}
	}

	@Override
	public void getSettings(final AsyncCallback<Settings> callback) {
		Timer timer = new Timer() {
			@Override
			public void run() {
				configDao.loadSettings(new Callback<Settings>() {

					@Override
					public void onSuccess(Settings result) {
						if (result != null)
							callback.onSuccess(result);
						else
							callback.onSuccess(Settings.newInstance());
					}

					@Override
					public void onFailure(Error error) {
						callback.onFailure(new Throwable(error.getMessage()));
					}
				});
			}
		};
		timer.schedule(0);
	}

	@Override
	public void getTemplates(final AsyncCallback<List<Template>> callback) {
		Timer timer = new Timer() {
			@Override
			public void run() {
				configDao.loadTemplates(new Callback<List<Template>>() {

					@Override
					public void onFailure(Error error) {
						callback.onFailure(new Throwable(error.getMessage()));
					}

					@Override
					public void onSuccess(List<Template> result) {
						if (result != null)
							callback.onSuccess(result);
						else
							callback.onSuccess(new ArrayList<Template>());
					}
				});
			}
		};
		timer.schedule(0);
	}

	@Override
	public void setTemplates(final List<Template> templates,
			final AsyncCallback<Void> callback) {
		Timer timer = new Timer() {
			@Override
			public void run() {
				configDao.saveTemplates(templates, new Callback<Void>() {

					@Override
					public void onFailure(Error error) {
						callback.onFailure(new Throwable(error.getMessage()));
					}

					@Override
					public void onSuccess(Void result) {
						callback.onSuccess(result);
					}
				});
			}
		};
		timer.schedule(0);
	}

	@Override
	public void setSettings(final Settings settings,
			final AsyncCallback<Void> callback) {
		Timer timer = new Timer() {
			@Override
			public void run() {
				configDao.saveSettings(settings, new Callback<Void>() {

					@Override
					public void onFailure(Error error) {
						callback.onFailure(new Throwable(error.getMessage()));
					}

					@Override
					public void onSuccess(Void result) {
						callback.onSuccess(result);
					}
				});
			}
		};
		timer.schedule(0);
	}

	@Override
	public void showError(String error) {
		CustomMessageWidget errorPanel = new CustomMessageWidget();
		errorPanel.showMessage(error, MessageType.ERROR);
	}

	@Override
	public boolean isHandheld() {
		return false;
	}

}