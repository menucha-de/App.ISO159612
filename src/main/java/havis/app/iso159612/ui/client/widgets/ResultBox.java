package havis.app.iso159612.ui.client.widgets;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

import havis.app.iso159612.utils.Resources;
import havis.app.iso159612.utils.Translation;

public class ResultBox extends HorizontalPanel {

	private Label label;

	private TextBox textBox;

	private String style;

	private Timer timer;

	public ResultBox() {
		this.setVerticalAlignment(ALIGN_MIDDLE);
		this.addStyleName(Resources.Css.RESULT_BOX_CONTAINER);

		label = new Label(Translation.LANG.resultResult());
		label.addStyleName(Resources.Css.LABEL_STYLE);
		this.add(label);

		textBox = new TextBox();
		textBox.setEnabled(false);
		textBox.addStyleName(Resources.Css.HAVIS_TEXTBOX);
		textBox.addStyleName(Resources.Css.RESULT_BOX);
		textBox.addStyleName(Resources.Css.HAVIS_TEXTBOX_TE);
		this.add(textBox);
	}

	public void showError(String message) {
		setText(message, Resources.Css.RESULT_BOX_ERROR);
	}

	public void showWarning(String message) {
		setText(message, Resources.Css.RESULT_BOX_WARNING);
	}

	public void showSuccess(String message) {
		setText(message, Resources.Css.RESULT_BOX_SUCCESS);
	}

	public void showInfo(String message) {
		setText(message, Resources.Css.RESULT_BOX_INFO);
	}

	public void showMessage(String message) {
		setText(message, null);
	}

	public void clear() {
		this.textBox.setValue("");
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (this.style != null) {
			this.textBox.removeStyleName(this.style);
			this.style = null;
		}
	}

	private void setText(String message, String style) {
		clear();
		this.textBox.setText(message);	
		if (style != null)
			this.textBox.addStyleName(style);
		this.style = style;
		timer = new Timer() {
			@Override
			public void run() {
				if (ResultBox.this.style != null) {
					textBox.removeStyleName(ResultBox.this.style);
					ResultBox.this.style = null;
				}
			}
		};
		timer.schedule(10000);
	}

}
