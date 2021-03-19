package havis.app.iso159612.ui.client;

import havis.app.iso159612.data.Compaction;
import havis.app.iso159612.data.Controller;
import havis.app.iso159612.data.IdEncoding;
import havis.app.iso159612.data.Settings;
import havis.app.iso159612.data.Template;
import havis.app.iso159612.ui.client.widgets.HavisActivateableComposite;
import havis.app.iso159612.ui.client.widgets.HavisParameter;
import havis.app.iso159612.ui.client.widgets.ResultBox;
import havis.app.iso159612.utils.Translation;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class SettingsPanel extends HavisActivateableComposite {

	private static SettingsPanelUiBinderUiBinder uiBinder = GWT.create(SettingsPanelUiBinderUiBinder.class);

	interface SettingsPanelUiBinderUiBinder extends UiBinder<Widget, SettingsPanel> {
	}

	@UiField
	protected Button reset;
	@UiField
	protected Button apply;
	@UiField
	protected ListBox idEncoding;
	@UiField
	protected ListBox compaction;
	@UiField
	protected ListBox template;
	@UiField
	protected ResultBox resultBox;

	private Controller controller;

	private Settings settings;

	public SettingsPanel(Controller controller) {
		this.controller = controller;
		initWidget(uiBinder.createAndBindUi(this));

		// fill ID Encoding list
		for (IdEncoding idEncoding : IdEncoding.values()) {
			this.idEncoding.addItem(idEncoding.toString(), idEncoding.toString());
			if (!idEncoding.isSupported())
				this.idEncoding.getElement().<SelectElement> cast().getOptions().getItem(this.idEncoding.getItemCount() - 1)
						.setDisabled(true);
		}
		setIdEncoding(IdEncoding.AUTO);

		// fill Compaction list
		for (Compaction compaction : Compaction.values()) {
			this.compaction.addItem(compaction.getCompaction(), compaction.toString());
			if (!compaction.isSupported())
				this.compaction.getElement().<SelectElement> cast().getOptions().getItem(this.compaction.getItemCount() - 1)
						.setDisabled(true);
		}
		setCompaction(Compaction.PACKED_OBJECTS);
	}

	@Override
	public void onShow(HavisParameter parameter) {
		resultBox.clear();
		template.clear();				
		template.addItem(Template.getDefault().getName(), Template.getDefault().getName());
		setTemplate(Template.getDefault().getName());		
		loadSettings(false);		
	}

	@Override
	public void onLeave() {

	}

	@Override
	public void onKeyEvent(String key) {

	}

	/**
	 * Resets the layout
	 */
	private void reset() {		
		template.clear();				
		template.addItem(Template.getDefault().getName(), Template.getDefault().getName());
		setTemplate(Template.getDefault().getName());		
		loadSettings(true);		
	}

	/**
	 * Sets the id encoding in the listBox
	 * 
	 * @param idEncoding
	 */
	private void setIdEncoding(IdEncoding idEncoding) {
		if (idEncoding == null) {
			setIdEncoding(IdEncoding.AUTO);
		} else {
			int size = this.idEncoding.getItemCount();
			for (int i = 0; i < size; i++) {
				if (this.idEncoding.getValue(i).equals(idEncoding.toString())) {
					this.idEncoding.setSelectedIndex(i);
					break;
				}
			}
		}
	}

	/**
	 * @return The selected id encoding
	 */
	private IdEncoding getSelectedIdEncoding() {
		for (IdEncoding idEncoding : IdEncoding.values())
			if (idEncoding.toString().equals(this.idEncoding.getSelectedValue()))
				return idEncoding;
		return IdEncoding.AUTO;
	}

	/**
	 * Sets the compaction in the listBox
	 * 
	 * @param compaction
	 */
	private void setCompaction(Compaction compaction) {
		if (compaction == null) {
			setCompaction(Compaction.PACKED_OBJECTS);
		} else {
			int size = this.compaction.getItemCount();
			for (int i = 0; i < size; i++) {
				if (this.compaction.getValue(i).equals(compaction.toString())) {
					this.compaction.setSelectedIndex(i);
					break;
				}
			}
		}
	}

	/**
	 * @return The selected compaction
	 */
	private Compaction getSelectedCompaction() {
		for (Compaction compaction : Compaction.values())
			if (compaction.toString().equals(this.compaction.getSelectedValue()))
				return compaction;
		return Compaction.PACKED_OBJECTS;
	}

	/**
	 * Sets the template in the listBox
	 * 
	 * @param template
	 */
	private void setTemplate(String template) {
		for (int i = 0; i < this.template.getItemCount(); i++) {
			if (this.template.getValue(i).equals(template)) {
				this.template.setSelectedIndex(i);
				return;
			}
		}
		setTemplate(Template.getDefault().getName());
	}

	/**
	 * Load the current set settings
	 */
	private void loadSettings(final boolean reset) {
		resultBox.showInfo(Translation.LANG.settingsResultLoadingSettings());
		// load templates
		controller.getTemplates(new AsyncCallback<List<Template>>() {

			@Override
			public void onFailure(Throwable caught) {
				resultBox.showError(Translation.LANG.settingsResultError());
				controller.showError(caught.getMessage());
			}

			@Override
			public void onSuccess(List<Template> result) {				
				// add all templates to the select list
				for (Template t : result)
					template.addItem(t.getName(), t.getName());
				// load settings
				controller.getSettings(new AsyncCallback<Settings>() {

					@Override
					public void onFailure(Throwable caught) {
						resultBox.showError(Translation.LANG.settingsResultError());
						controller.showError(caught.getMessage());
					}

					@Override
					public void onSuccess(Settings result) {
						if (result != null)
							settings = result;
						else
							settings = Settings.newInstance();
						setIdEncoding(settings.getIdEncoding());
						setCompaction(settings.getCompaction());
						setTemplate(settings.getTemplate());
						if(reset){
							resultBox.showSuccess(Translation.LANG.settingsResultReset());
						} else {
							resultBox.clear();
						}
					}
				});
			}
		});

	}

	/**
	 * Saves the current settings
	 */
	private void save() {
		settings.setIdEncoding(getSelectedIdEncoding());
		settings.setCompaction(getSelectedCompaction());
		settings.setTemplate(template.getSelectedValue());

		controller.setSettings(settings, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				resultBox.showError(Translation.LANG.settingsResultError());
				controller.showError(caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {				
				resultBox.showSuccess(Translation.LANG.settingsResultSaved());
			}
		});
	}

	@UiHandler("reset")
	public void onReset(ClickEvent e) {
		resultBox.clear();
		reset();
	}

	@UiHandler("apply")
	public void onSave(ClickEvent e) {
		resultBox.clear();
		save();
	}
}
