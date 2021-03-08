package havis.custom.harting.iso159612.ui.client.widgets;

import havis.custom.harting.iso159612.hw.BarcodeReader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ScannerOverlay extends Composite {
	private static ScannerOverlayUiBinder uiBinder = GWT
			.create(ScannerOverlayUiBinder.class);

	interface ScannerOverlayUiBinder extends UiBinder<Widget, ScannerOverlay> {
	}

	@UiField
	FlowPanel infoDialog;

	private ResultBox box;

	private BarcodeReader reader;

	public ScannerOverlay() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setReader(BarcodeReader reader, ResultBox box) {
		this.box = box;
		this.reader = reader;
		Image previewImage = reader.getPreviewImage();
		previewImage.setVisible(true);
		infoDialog.add(previewImage);
	}

	@UiHandler("infoCloseLabel")
	public void onClose(ClickEvent event) {
		final ScannerOverlay parent = this;
		reader.scan(new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				box.clear();
				parent.setVisible(false);
			}

			@Override
			public void onFailure(Throwable caught) {
				parent.setVisible(false);
			}
		});
	}

}