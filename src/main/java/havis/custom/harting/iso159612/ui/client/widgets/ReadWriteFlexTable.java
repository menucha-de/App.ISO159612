package havis.custom.harting.iso159612.ui.client.widgets;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

import havis.custom.harting.iso159612.data.Controller;
import havis.custom.harting.iso159612.data.KeyValuePair;
import havis.custom.harting.iso159612.utils.Resources;
import havis.custom.harting.iso159612.utils.Translation;

public class ReadWriteFlexTable extends DataElementFlexTable {

	private final static String ATTRIBUTE_OID = "oid";

	private ClickHandler removeClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			Image source = (Image) event.getSource();
			ReadWriteFlexTable.this.removeRow(indexOfOid(source.getElement().getAttribute(ATTRIBUTE_OID)));
		}
	};

	public ReadWriteFlexTable(Controller controller) {
		super(controller);
	}

	@Override
	public int addField(String oid, String value) {
		int row = indexOfOid(oid);
		if (row < 0) {
			row = this.getRowCount();
			Label l = new Label(getTitle(oid));
			l.addStyleName(Resources.Css.DATA_ELEMENT_TITLE);
			this.setWidget(row, 0, l);

			PlaceholderTextBox t = new PlaceholderTextBox(controller.getPackedObjectInvestigator().getFormatString(oid));
			t.setValue(value);
			t.getElement().setAttribute(ATTRIBUTE_OID, oid);
			this.setWidget(row, 1, t);

			Image i = new Image(Resources.Image.DELETE);
			i.addClickHandler(removeClickHandler);
			i.getElement().setAttribute(ATTRIBUTE_OID, oid);
			this.setWidget(row, 2, i);
		}
		return row;
	}

	@Override
	public int indexOfOid(String oid) {
		int rows = this.getRowCount();
		for (int i = 0; i < rows; i++) {
			TextBox t = (TextBox) this.getWidget(i, 1);
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
			TextBox t = (TextBox) getWidget(i, 1);
			dataElements.add(new KeyValuePair<String, String>(t.getElement().getAttribute(ATTRIBUTE_OID), t.getValue()));
		}
		return dataElements;
	}

	/**
	 * If the {@code oid} is already in the list the value will be updated. If
	 * the {@code oid} is not in the list a new entry will be added.
	 * 
	 * @param oid
	 *            The oid which shall be updated.
	 * @param value
	 *            The new value
	 * @return The row index of the oid
	 */
	public int updateField(String oid, String value) {
		int row = indexOfOid(oid);
		if (row < 0)
			return addField(oid, value);
		else
			((TextBox) getWidget(row, 1)).setValue(value);
		return row;
	}

	/**
	 * Validates the data elements
	 * 
	 * @return true if valid
	 */
	public boolean validate(AsyncCallback<String> callback) {
		try {
			List<KeyValuePair<String, String>> dataElements = getDataElements();
			for (KeyValuePair<String, String> entry : dataElements) {			
				if (!controller.getPackedObjectInvestigator().validateDataElement(entry.getKey(), entry.getValue())) {
					controller.showError(Translation.LANG.msgRwValueOfOidInvalid(entry.getValue(), getTitle(entry.getKey())));
					callback.onSuccess(entry.getKey());
					setError(indexOfOid(entry.getKey()));				
					return false;
				}
			}
			return true;
		} catch (Exception e){
			controller.showError(e.getMessage());
			return false;
		}
	}
}