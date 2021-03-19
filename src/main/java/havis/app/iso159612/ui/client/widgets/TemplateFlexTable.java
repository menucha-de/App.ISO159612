package havis.app.iso159612.ui.client.widgets;

import havis.application.common.data.JsonArray;
import havis.app.iso159612.data.Controller;
import havis.app.iso159612.data.KeyValuePair;
import havis.app.iso159612.data.TemplateEntry;
import havis.app.iso159612.utils.Resources;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;

public class TemplateFlexTable extends DataElementFlexTable {

	public TemplateFlexTable(Controller controller) {
		super(controller);
	}

	@Override
	public int addField(String oid, String value) {
		return addField(oid, value, true);
	}

	@Override
	public int indexOfOid(String oid) {
		int rows = this.getRowCount();
		for (int i = 0; i < rows; i++) {
			TextBox t = (TextBox) this.getWidget(i, 2);
			if (oid.equals(t.getElement().getAttribute(ATTRIBUTE_OID)))
				return i;
		}
		return -1;
	}

	@Override
	public List<KeyValuePair<String, String>> getDataElements() {
		List<KeyValuePair<String, String>> dataElements = new ArrayList<KeyValuePair<String, String>>();
		int rows = getRowCount();
		for (int i = 0; i < rows; i++) {
			TextBox t = (TextBox) getWidget(i, 2);
			dataElements.add(new KeyValuePair<String, String>(t.getElement().getAttribute(ATTRIBUTE_OID), t.getValue()));
		}
		return dataElements;
	}

	public int addField(String oid, String value, boolean override) {
		int row = indexOfOid(oid);
		if (row < 0) {
			row = this.getRowCount();

			ToggleButton toggleButton = new ToggleButton();
			toggleButton.setStyleName(Resources.Css.TOGGLE_BUTTON);
			this.setWidget(row, 0, toggleButton);
			toggleButton.setDown(override);

			Label l = new Label(getTitle(oid));
			l.addStyleName(Resources.Css.DATA_ELEMENT_TITLE);
			this.setWidget(row, 1, l);

			PlaceholderTextBox t = new PlaceholderTextBox(controller.getPackedObjectInvestigator().getFormatString(oid));
			t.setValue(value);
			t.getElement().setAttribute(ATTRIBUTE_OID, oid);
			this.setWidget(row, 2, t);

			Image i = new Image(Resources.Image.DELETE);
			i.addClickHandler(removeClickHandler);
			i.getElement().setAttribute(ATTRIBUTE_OID, oid);
			this.setWidget(row, 3, i);
		}
		return row;
	}

	/**
	 * @return List of all entries
	 */
	public JsonArray<TemplateEntry> getTemplateDataElements() {
		JsonArray<TemplateEntry> result = JsonArray.createInstance();
		int rows = getRowCount();
		for (int i = 0; i < rows; i++) {
			ToggleButton toggleButton = (ToggleButton) getWidget(i, 0);
			TextBox textBox = (TextBox) getWidget(i, 2);
			result.push(TemplateEntry.newInstance(textBox.getElement().getAttribute(ATTRIBUTE_OID), textBox.getValue(), toggleButton.isDown()));
		}
		return result;
	}
}