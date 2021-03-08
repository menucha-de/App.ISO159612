package havis.custom.harting.iso159612.ui.client.widgets;

import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;

import havis.custom.harting.iso159612.data.Controller;
import havis.custom.harting.iso159612.data.KeyValuePair;
import havis.custom.harting.iso159612.utils.Resources;
import havis.middleware.tdt.PackedObjectInvestigator.ColumnName;

public abstract class DataElementFlexTable extends FlexTable {

	protected final static String ATTRIBUTE_OID = "oid";

	protected Controller controller;

	protected ClickHandler removeClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			Image source = (Image) event.getSource();
			DataElementFlexTable.this.removeRow(indexOfOid(source.getElement().getAttribute(ATTRIBUTE_OID)));
		}
	};

	public DataElementFlexTable(Controller controller) {
		this.controller = controller;
	}

	/**
	 * Determines the title of the OID
	 * 
	 * @param oid
	 *            The OID
	 * @return The title of the OID
	 */
	protected String getTitle(String oid) {
		List<Map.Entry<String, Integer>> entries = controller.getPackedObjectInvestigator().getOidEntry(oid);
		switch (entries.size()) {
		case 1:
			return controller.getPackedObjectInvestigator().getData(entries.get(0).getKey(), ColumnName.FORMAT_9_DATA_TITLE, entries.get(0).getValue());
		case 2:
			return controller.getPackedObjectInvestigator().getData(entries.get(1).getKey(), ColumnName.FORMAT_9_DATA_TITLE, entries.get(1).getValue());
		default:
			return null;
		}
	}

	public void setWarning(int row) {
		setColor(row, Resources.Css.DATA_ELEMENTS_COLOR_WARNING);
	}

	public void setError(int row) {
		setColor(row, Resources.Css.DATA_ELEMENTS_COLOR_ERROR);
	}

	private void setColor(int row, final String className) {
		if (row > -1 && row < this.getRowCount()) {
			final Element element = getRowFormatter().getElement(row);
			element.addClassName(className);
			Timer timer = new Timer() {
				@Override
				public void run() {
					element.removeClassName(className);
				}
			};
			timer.schedule(500);
		}
	}

	/**
	 * Adds the oid and value to the list
	 * 
	 * @param oid
	 *            The oid which shall be added.
	 * @param value
	 *            The new value
	 * @return The row index of the oid
	 */
	public abstract int addField(String oid, String value);

	/**
	 * Returns the row index of the OID
	 * 
	 * @param oid
	 * @return The index of the oid
	 */
	public abstract int indexOfOid(String oid);

	/**
	 * Returns a list of OIDs and the value
	 * 
	 * @return
	 */
	public abstract List<KeyValuePair<String, String>> getDataElements();
}