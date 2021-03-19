package havis.app.iso159612.hw;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;

public interface BarcodeReader {

	/**
	 * Executes a barcode scan operation. The method {@code onSuccess(...)} of
	 * the {@code callback} will be called if the barcode could be read. In all
	 * other cases the {@code onFailure(...)} method is called.
	 * 
	 * @param callback
	 */
	void scan(AsyncCallback<String> callback);

	Image getPreviewImage();

}