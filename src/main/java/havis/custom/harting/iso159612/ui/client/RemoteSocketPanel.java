package havis.custom.harting.iso159612.ui.client;

import havis.application.common.HAL;
import havis.custom.harting.iso159612.data.Controller;
import havis.custom.harting.iso159612.ui.client.widgets.HavisActivateableComposite;
import havis.custom.harting.iso159612.ui.client.widgets.HavisParameter;
import havis.custom.harting.iso159612.ui.client.widgets.PlaceholderTextBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;

public class RemoteSocketPanel extends HavisActivateableComposite {

	@UiField
	protected PlaceholderTextBox url;

	private static RemoteSocketPanelUiBinder uiBinder = GWT.create(RemoteSocketPanelUiBinder.class);

	interface RemoteSocketPanelUiBinder extends UiBinder<Widget, RemoteSocketPanel> {
	}
	
	private Controller controller;

	public RemoteSocketPanel(Controller controller) {
		this.controller = controller;
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void onShow(HavisParameter parameter) {
	}

	@Override
	public void onLeave() {
	}

	@Override
	public void onKeyEvent(String key) {
	}

	@UiHandler("apply")
	public void onClick(ClickEvent e) {
		try {
			HAL.createSocket(url.getText());
			activateComposite(ReadWritePanel.class, null);
		} catch (Exception ex){
			controller.showError(ex.getMessage());
		}
	}

	public void setUrl(String url) {
		this.url.setValue(url);
	}
}
