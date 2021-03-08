package havis.custom.harting.iso159612.ui.client.wrapper;

import havis.custom.harting.iso159612.data.Controller;
import havis.custom.harting.iso159612.ui.client.ReadWritePanel;
import havis.custom.harting.iso159612.ui.client.SelectPanel;
import havis.net.ui.shared.client.ConfigurationSection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class ReadWritePanelWrapper extends ConfigurationSection {
	
	private static ReadWritePanelWrapperUiBinder uiBinder = GWT.create(ReadWritePanelWrapperUiBinder.class);
	
	interface ReadWritePanelWrapperUiBinder extends UiBinder<Widget, ReadWritePanelWrapper> {
	}


	@UiField(provided = true)
	protected ReadWritePanel readWritePanel;
	
	@UiField(provided = true)
	protected SelectPanel selectPanel;
	
	@UiConstructor
	public ReadWritePanelWrapper(String name, Controller controller) {
		super(name);
		readWritePanel = new ReadWritePanel(controller);
		selectPanel = new SelectPanel(controller);
		initWidget(uiBinder.createAndBindUi(this));
		
	}
	
	@Override
	protected void onCloseSection() {
		readWritePanel.setVisible(false);
		super.onCloseSection();
	}
	
	@Override
	protected void onOpenSection() {
		readWritePanel.setVisible(true);
		readWritePanel.onShow(null);
		super.onOpenSection();
	}
	

}