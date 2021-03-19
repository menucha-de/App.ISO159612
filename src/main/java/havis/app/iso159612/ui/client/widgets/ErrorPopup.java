package havis.app.iso159612.ui.client.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import havis.app.iso159612.utils.Resources;

public class ErrorPopup extends PopupPanel {

	public ErrorPopup() {
		super(true);
	}
	
	public void showError(String message, double top, Unit unit) {
		setTopPosition(top, unit);
		initMessage(message, Resources.Css.POPUP_DOT_ERROR);
	}
	
	private void setTopPosition(double top, Unit unit){
		getElement().getStyle().setProperty("top", top + unit.getType() + " !important");
	}

	private void initMessage(String message, String style) {
		Label messageLabel = new Label(message);

		SimplePanel dot = new SimplePanel();
		dot.setStylePrimaryName(Resources.Css.POPUP_DOT);
		if (style != null)
			dot.addStyleName(style);

		FlowPanel cont = new FlowPanel();
		cont.add(messageLabel);
		cont.add(dot);

		setWidget(cont);
		show();
	}
}
