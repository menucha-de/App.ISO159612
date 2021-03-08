package havis.custom.harting.iso159612.ui.client.wrapper;

import havis.custom.harting.iso159612.data.Controller;
import havis.custom.harting.iso159612.ui.client.SelectPanel;
import havis.custom.harting.iso159612.ui.client.SettingsPanel;
import havis.net.ui.shared.client.ConfigurationSection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class SettingsPanelWrapper extends ConfigurationSection {

	private static SettingsPanelWrapperUiBinder uiBinder = GWT
			.create(SettingsPanelWrapperUiBinder.class);

	interface SettingsPanelWrapperUiBinder extends
			UiBinder<Widget, SettingsPanelWrapper> {
	}

	@UiField(provided = true)
	protected SettingsPanel settingsPanel;

	@UiField(provided = true)
	protected SelectPanel selectPanel;

	@UiConstructor
	public SettingsPanelWrapper(String name, Controller controller) {
		super(name);
		settingsPanel = new SettingsPanel(controller);
		selectPanel = new SelectPanel(controller);
		initWidget(uiBinder.createAndBindUi(this));

	}
	
	@Override
	protected void onCloseSection() {
		settingsPanel.setVisible(false);
		super.onCloseSection();
	}
	
	@Override
	protected void onOpenSection() {
		settingsPanel.setVisible(true);
		settingsPanel.onShow(null);
		super.onOpenSection();
	}
	
	
	

}