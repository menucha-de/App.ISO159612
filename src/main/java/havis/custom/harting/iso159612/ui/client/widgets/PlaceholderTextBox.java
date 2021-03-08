package havis.custom.harting.iso159612.ui.client.widgets;

import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.TextBox;

public class PlaceholderTextBox extends TextBox {

	public PlaceholderTextBox() {

	}

	@UiConstructor
	public PlaceholderTextBox(String placeholder) {
		setPlaceholder(placeholder);
	}

	public void setPlaceholder(String placeholder) {
		this.getElement().setPropertyString("placeholder", placeholder);
	}
}
