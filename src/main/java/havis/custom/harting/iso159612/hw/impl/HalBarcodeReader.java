package havis.custom.harting.iso159612.hw.impl;

import havis.application.common.HAL;
import havis.custom.harting.iso159612.hw.BarcodeReader;
import havis.custom.harting.iso159612.utils.Translation;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;

/**
 * A {@link BarcodeReader} implementation which uses the Hal.js
 */
public class HalBarcodeReader implements BarcodeReader {

	public HalBarcodeReader() {

	}

	/**
	 * @return True if barcode service is available.
	 */
	private boolean isServiceAvailable() {
		return HAL.Service.Barcode.isSupported();
	}
	
	

	@Override
	public void scan(final AsyncCallback<String> callback) {
		if (isServiceAvailable()) {
			HAL.Service.Barcode.barcodeScan(new AsyncCallback<String>() {

				@Override
				public void onSuccess(String result) {
					if (result == null || result.isEmpty())
						callback.onFailure(new Throwable(Translation.LANG.msgServiceBarcodeNoData()));
					else
						callback.onSuccess(result);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
			});
		} else {
			callback.onFailure(new Throwable(Translation.LANG.msgServiceBarcodeNotAvailable()));
		}
	}

	@Override
	public Image getPreviewImage() {
		return null;
	}

}
