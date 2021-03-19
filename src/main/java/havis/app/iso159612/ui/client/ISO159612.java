package havis.app.iso159612.ui.client;

import havis.application.common.HAL;
import havis.application.common.event.KeyEventListener;
import havis.application.common.event.SocketErrorListener;
import havis.application.component.db.Callback;
import havis.application.component.db.Error;
import havis.app.iso159612.data.Controller;
import havis.app.iso159612.data.Settings;
import havis.app.iso159612.data.Template;
import havis.app.iso159612.hw.ConfigDao;
import havis.app.iso159612.hw.Keyboard;
import havis.app.iso159612.hw.impl.ConfigDaoImpl;
import havis.app.iso159612.ui.client.widgets.ErrorPopup;
import havis.app.iso159612.ui.client.widgets.HavisActivateableComposite;
import havis.app.iso159612.ui.client.widgets.HavisOfflinePanel;
import havis.app.iso159612.ui.client.widgets.MenuBarPanel;
import havis.app.iso159612.utils.Translation;
import havis.middleware.tdt.Barcode;
import havis.middleware.tdt.PackedObjectInvestigator;
import havis.middleware.tdt.PackedObjectInvestigator.ColumnName;
import havis.middleware.tdt.TdtResources;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class ISO159612 extends Composite implements EntryPoint, Controller, KeyEventListener {

	private static ISO159612UiBinder uiBinder = GWT.create(ISO159612UiBinder.class);

	interface ISO159612UiBinder extends UiBinder<Widget, ISO159612> {
	}

	private final static int SLEEP_MS = 25;

	private static boolean loadingOids;

	private PackedObjectInvestigator tdtInvestigator;
	private Barcode tdtBarcode;

	private ConfigDao configDao;

	private Map<String, Map<ColumnName, String>> oids;

	private ErrorPopup messagePopup = new ErrorPopup();
	@UiField
	protected MenuBarPanel menuBar;
	@UiField
	protected HorizontalPanel headline;
	@UiField(provided = true)
	protected ReadWritePanel readWritePanel;
	@UiField(provided = true)
	protected TemplatesPanel templatesPanel;
	@UiField(provided = true)
	protected SettingsPanel settingsPanel;
	@UiField(provided = true)
	protected SelectPanel selectPanel;
	@UiField(provided = true)
	protected RemoteSocketPanel remoteSocketPanel;
	@UiField
	protected Image image;

	private Keyboard keyboard = GWT.create(Keyboard.class);
	
	public ISO159612() {
		initKeyboard();

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

		readWritePanel = new ReadWritePanel(this);
		templatesPanel = new TemplatesPanel(this);
		settingsPanel = new SettingsPanel(this);
		selectPanel = new SelectPanel(this);
		remoteSocketPanel = new RemoteSocketPanel(this);

		initWidget(uiBinder.createAndBindUi(this));

		MenuItem scanReadWrite = new MenuItem(new Label(Translation.LANG.menuItemReadWrite()).toString(), true,
				getCommand(ReadWritePanel.class));
		MenuItem templates = new MenuItem("<div>" + Translation.LANG.menuItemTemplates() + "</div>", true,
				getCommand(TemplatesPanel.class));
		MenuItem settings = new MenuItem("<div>" + Translation.LANG.menuItemSettings() + "</div>", true, getCommand(SettingsPanel.class));

		menuBar.addItem(scanReadWrite);
		menuBar.addItem(templates);
		menuBar.addItem(settings);

		HAL.addSocketErrorListener(new SocketErrorListener() {
			@Override
			public String onError(String origin) {
				// will change URL asynchronously in RemoteSocketPanel
				rootPanel.activatePanel(RemoteSocketPanel.class, null);
				remoteSocketPanel.setUrl(origin);
				return null;
			}
		});

		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				resize();
			}
		});

		image.addLoadHandler(new LoadHandler() {

			@Override
			public void onLoad(LoadEvent event) {
				resize();
			}
		});
	}

	private void resize() {
		rootPanel.getElement().getStyle().setTop(menuBar.getElement().getOffsetHeight() + headline.getOffsetHeight(), Unit.PX);
	}

	@UiField
	protected HavisOfflinePanel rootPanel;

	@Override
	public void onKeyEvent(String key) {
		messagePopup.hide();
		rootPanel.onKeyPressed(key);
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				resize();
			}
		});
	}

	@Override
	public void onModuleLoad() {
		RootPanel.get().add(this);
	}

	@Override
	public void showError(String message) {
		messagePopup.showError(message, menuBar.getOffsetHeight() + 2, Unit.PX);
	}

	@Override
	public PackedObjectInvestigator getPackedObjectInvestigator() {
		if (tdtInvestigator == null)
			tdtInvestigator = new PackedObjectInvestigator(TdtResources.INSTANCE.tableForData().getText());
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
			for (Map.Entry<String, Map<ColumnName, String>> entry : oids.entrySet())
				result.put(entry.getKey(), entry.getValue().get(ColumnName.FORMAT_9_NAME));
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
						oids = getPackedObjectInvestigator().getOids("urn:oid:1.0.15961.9", ColumnName.FORMAT_9_NAME);
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
	public void setTemplates(final List<Template> templates, final AsyncCallback<Void> callback) {
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
	public void setSettings(final Settings settings, final AsyncCallback<Void> callback) {
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

	/**
	 * Initializes the keyboard service
	 */
	private void initKeyboard() {
		if (keyboard.isSupported())
			keyboard.setKeyEventListener(ISO159612.this);
		keyboard.addSocketListener(new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				HAL.addServiceListener("keyboard", new AsyncCallback<havis.application.common.data.Service>() {
					@Override
					public void onSuccess(havis.application.common.data.Service service) {
						if (service.isActive()) {
							HAL.Service.Keyboard.setKeyEventListener(ISO159612.this);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						showError(caught.getMessage());

					}
				});
			}

			@Override
			public void onFailure(Throwable caught) {
				showError(caught.getMessage());
			}
		});
	}

	/**
	 * Returns the command for menu item
	 * 
	 * @param clazz
	 * @return
	 */
	private ScheduledCommand getCommand(final Class<? extends HavisActivateableComposite> clazz) {
		ScheduledCommand command = new ScheduledCommand() {
			@Override
			public void execute() {
				if (clazz != null) {
					rootPanel.activatePanel(clazz, null);
				}
			}
		};

		return command;
	}

	@Override
	public boolean isHandheld() {
		return true;
	}

}