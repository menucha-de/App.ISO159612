package havis.custom.harting.iso159612.ui.client.wrapper;

import havis.custom.harting.iso159612.data.Controller;
import havis.custom.harting.iso159612.ui.client.SelectPanel;
import havis.custom.harting.iso159612.ui.client.TemplatesPanel;
import havis.net.ui.shared.client.ConfigurationSection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class TemplatesPanelWrapper extends ConfigurationSection {

	private static TemplatesPanelWrapperUiBinder uiBinder = GWT
			.create(TemplatesPanelWrapperUiBinder.class);

	interface TemplatesPanelWrapperUiBinder extends
			UiBinder<Widget, TemplatesPanelWrapper> {
	}

	@UiField(provided = true)
	protected TemplatesPanel templatesPanel;

	@UiField(provided = true)
	protected SelectPanel selectPanel;

	@UiConstructor
	public TemplatesPanelWrapper(String name, Controller controller) {
		super(name);
		templatesPanel = new TemplatesPanel(controller);
		selectPanel = new SelectPanel(controller);
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	protected void onCloseSection() {
		templatesPanel.setVisible(false);
		super.onCloseSection();
	}
	
	@Override
	protected void onOpenSection() {
		templatesPanel.setVisible(true);
		templatesPanel.onShow(null);
		super.onOpenSection();
	}
	
	
	
	

}