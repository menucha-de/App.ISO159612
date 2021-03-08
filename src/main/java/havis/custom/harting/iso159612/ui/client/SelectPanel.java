package havis.custom.harting.iso159612.ui.client;

import java.util.Iterator;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import havis.custom.harting.iso159612.data.Controller;
import havis.custom.harting.iso159612.ui.client.widgets.HavisActivateableComposite;
import havis.custom.harting.iso159612.ui.client.widgets.HavisParameter;
import havis.custom.harting.iso159612.ui.client.widgets.PlaceholderTextBox;
import havis.custom.harting.iso159612.utils.Translation;

/**
 * Panel to select an OID
 */
public class SelectPanel extends HavisActivateableComposite {

	private static SelectPanelUiBinderUiBinder uiBinder = GWT.create(SelectPanelUiBinderUiBinder.class);

	interface SelectPanelUiBinderUiBinder extends UiBinder<Widget, SelectPanel> {
	}

	private Controller controller;

	/**
	 * Source Page
	 */
	private HavisActivateableComposite from;

	/**
	 * Contains as key the OID and as value the name
	 */
	private Map<String, String> oids;

	/**
	 * The last scroll position.
	 */
	private int lastScrollPos = 0;

	/**
	 * Oids list position
	 */
	private int oidsCount = 0;

	/**
	 * Max table entries to load
	 */
	private final static int TABLE_SIZE = 25;

	private final static String ATTRIBUTE_OID = "oid";

	private final static String OID_DATAFORMAT_9 = "urn:oid:1.0.15961.9";

	@UiField
	protected FlowPanel header;
	@UiField
	protected Image clear;
	@UiField
	protected PlaceholderTextBox searchBar;
	@UiField
	protected FlexTable list;
	@UiField
	protected ScrollPanel scrollPanel;

	private ClickHandler selectClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			Label source = (Label) event.getSource();
			activateComposite(from.getClass(), source.getElement().getAttribute(ATTRIBUTE_OID));
		}
	};

	/**
	 * Loads new items if scroll end has been reached.
	 */
	private ScrollHandler scrollHandler = new ScrollHandler() {

		@Override
		public void onScroll(ScrollEvent event) {
			int oldScrollPos = lastScrollPos;
			lastScrollPos = scrollPanel.getVerticalScrollPosition();

			// If scrolling up, ignore the event.
			if (oldScrollPos >= lastScrollPos) {
				return;
			}

			int maxScrollTop = scrollPanel.getWidget().getOffsetHeight() - scrollPanel.getOffsetHeight();
			if (lastScrollPos >= maxScrollTop) {
				// We are near the end, so increase the page size.
				loadList(searchBar.getValue());
			}
		}
	};

	/**
	 * If the search value changed the list will be updated.
	 */
	private KeyUpHandler searchBarHandler = new KeyUpHandler() {

		@Override
		public void onKeyUp(KeyUpEvent event) {
			fillList(searchBar.getValue());
		}
	};

	/**
	 * Clears the search bar
	 */
	private ClickHandler clearClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			searchBar.setValue("");
			fillList("");
		}
	};

	public SelectPanel(Controller controller) {
		this.controller = controller;

		initWidget(uiBinder.createAndBindUi(this));

		scrollPanel.addScrollHandler(scrollHandler);
		searchBar.addKeyUpHandler(searchBarHandler);
		clear.addClickHandler(clearClickHandler);
		
		Window.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				resize();				
			}
		});
	}
	
	private void resize(){
		this.scrollPanel.getElement().getStyle().setTop(this.header.getOffsetHeight(), Unit.PX);
	}

	@Override
	public void onShow(final HavisParameter parameter) {
		from = null;
		if (parameter != null)
			from = parameter.getFrom();
		searchBar.setValue("");
		if (oids == null) {
			list.removeAllRows();
			controller.getOidNames(new AsyncCallback<Map<String, String>>() {

				@Override
				public void onFailure(Throwable caught) {
					SelectPanel.this.controller.showError(caught.getMessage());
				}

				@Override
				public void onSuccess(Map<String, String> result) {
					oids = result;
					fillList("");
				}
			});
		} else {
			fillList("");
		}
		resize();
	}

	@Override
	public void onLeave() {

	}

	@Override
	public void onKeyEvent(String key) {

	}

	/**
	 * Close this panel and returns to the previous one.
	 */
	private void close() {
		if (from != null)
			activateComposite(from.getClass(), null);
		else
			controller.showError(Translation.LANG.msgUnknownError());
	}

	/**
	 * Reloads the list with the specified filter.
	 * 
	 * @param filter
	 */
	private void fillList(String filter) {
		list.removeAllRows();
		oidsCount = 0;
		loadList(filter);
	}

	/**
	 * loads the next 25 entries
	 * 
	 * @param filter
	 */
	private void loadList(String filter) {
		filter = filter.toLowerCase();
		int row = list.getRowCount();
		int marker = row + TABLE_SIZE;
		Iterator<Map.Entry<String, String>> iterator = oids.entrySet().iterator();
		Map.Entry<String, String> entry = null;
		for (int i = 0; i < oidsCount && iterator.hasNext(); i++)
			entry = iterator.next();

		for (; iterator.hasNext() && row < marker; oidsCount++) {
			entry = iterator.next();
			String oid = entry.getKey();
			String ai = oid.substring((OID_DATAFORMAT_9 + ".").length());
			ai = ai.length() < 2 ? "0" + ai : ai;
			String value = "AI " + ai + " - " + entry.getValue();
			if (filter == null || filter.trim().isEmpty() || value.toLowerCase().contains(filter)) {
				Label label = new Label(value);
				label.getElement().setAttribute(ATTRIBUTE_OID, oid);
				label.addClickHandler(selectClickHandler);
				list.setWidget(row, 0, label);
				row++;
			}
		}
	}

	@UiHandler("cancel")
	public void onClose(ClickEvent e) {
		close();
	}
}