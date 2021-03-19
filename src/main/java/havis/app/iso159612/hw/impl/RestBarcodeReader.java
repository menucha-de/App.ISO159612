package havis.app.iso159612.hw.impl;

import havis.app.iso159612.async.WebcamScannerServiceAsync;
import havis.app.iso159612.hw.BarcodeReader;
import havis.app.iso159612.utils.Resources;
import havis.transform.transformer.imager.Report;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;

/**
 * A {@link BarcodeReader} implementation which uses the Hal.js
 */
public class RestBarcodeReader implements BarcodeReader {

	private WebcamScannerServiceAsync scanner = GWT
			.create(WebcamScannerServiceAsync.class);;

	private Timer scan = null;
	private Image preview = GWT.create(Image.class);
	private static final String IMAGE_LINK = com.google.gwt.core.client.GWT
			.getHostPageBaseURL()
			+ "rest/capture/adapter/camera/devices/cam0/stream";

	private static final String IMAGE_LINK_STATIC = com.google.gwt.core.client.GWT
			.getHostPageBaseURL()
			+ "rest/capture/adapter/camera/devices/cam0/image";

	public RestBarcodeReader() {
		preview.setUrl(IMAGE_LINK);
		preview.setHeight("240px");
		preview.setWidth("320px");
		preview.setVisible(false);
		preview.addStyleName(Resources.Css.PREVIEW_IMG);
	}

	@Override
	public void scan(final AsyncCallback<String> callback) {
		if (scan != null && scan.isRunning()) {
			scanner.cancelScan(new MethodCallback<Void>() {
				@Override
				public void onSuccess(Method method, Void response) {
					scan.cancel();
					callback.onSuccess("");
				}

				@Override
				public void onFailure(Method method, Throwable exception) {
				}
			});
		} else {
			if (scan == null || !scan.isRunning()) {
				scanner.scan(new MethodCallback<Void>() {
					@Override
					public void onSuccess(Method method, Void response) {
						scan = new Timer() {
							@Override
							public void run() {
								// TODO Adds IE switch
								if (Window.Navigator.getUserAgent().contains(
										"MSIE")
										|| Window.Navigator.getUserAgent()
												.contains("Edge")) {
									preview.setUrl(IMAGE_LINK_STATIC + "?"
											+ Duration.currentTimeMillis());
								}
								scanner.getReport(new MethodCallback<Report>() {
									@Override
									public void onFailure(Method method,
											Throwable exception) {
										callback.onFailure(exception);
									}

									@Override
									public void onSuccess(Method method,
											Report response) {
										if (response.getBarcodes().size() > 0) {
											scan.cancel();
											callback.onSuccess(response
													.getBarcodes().get(0)
													.getCode());
											return;
										}
									}
								});
							}
						};
						scan.scheduleRepeating(500);
					}

					@Override
					public void onFailure(Method method, Throwable exception) {
						callback.onFailure(exception);
					}
				});
			}
		}
	}

	@Override
	public Image getPreviewImage() {
		return preview;
	}

}
