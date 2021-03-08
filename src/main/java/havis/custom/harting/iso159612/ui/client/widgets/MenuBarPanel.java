package havis.custom.harting.iso159612.ui.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

public class MenuBarPanel extends Composite {

	private static MenuBarPanelUiBinder uiBinder = GWT.create(MenuBarPanelUiBinder.class);

	@UiField
	protected MenuItem appMenuItem;

	@UiField
	protected MenuBar appMenu;

	interface MenuBarPanelUiBinder extends UiBinder<Widget, MenuBarPanel> {
	}

	public MenuBarPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void addItem(MenuItem item) {
		appMenu.addItem(item);
	}
}
