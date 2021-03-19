package havis.app.iso159612.imager;

import havis.capture.AdapterHandler;
import havis.transform.Transformer;
import havis.transform.transformer.imager.Report;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebScannerManager {
	private final static Logger log = Logger.getLogger(WebScannerManager.class
			.getName());

	private Transformer imager;
	private AdapterHandler camera;
	private Report current;

	private int codeInterval = 500; // Put picture to imager intervall
	private final static TimeUnit TIMEUNIT = TimeUnit.MILLISECONDS;
	private ScheduledExecutorService worker;

	public WebScannerManager() {
	}

	public void setImager(Transformer imager) {
		this.imager = imager;
	}

	public void setCamera(AdapterHandler camera) {
		this.camera = camera;
	}

	public boolean webScannnerReady() {
		if (imager != null && camera != null) {
			return true;
		}
		return false;
	}

	public void close() throws Exception {
		if (camera != null) {
			camera.close();
		}
	}

	public synchronized Report getReport() {
		Report result = new Report();
		if (current != null) {
			result.setTimestamp(current.getTimestamp());
			result.getBarcodes().addAll(current.getBarcodes());
			current = null;
		}
		return result;
	}

	public synchronized void startScan() {
		current = null;
		stopExecutor();
		startExecutor();
	}
	
	public synchronized void cancelScan(){
		stopExecutor();
	}

	private void startExecutor() {
		worker = Executors.newScheduledThreadPool(2);
		worker.scheduleAtFixedRate(new CodeRunner(), 0, codeInterval, TIMEUNIT);
	}

	private void stopExecutor() {
		if (worker != null) {
			worker.shutdownNow();
			try {
				if (!worker.awaitTermination(30, TimeUnit.SECONDS)) {
					IllegalStateException illegalStateException = new IllegalStateException(
							"Termination failed");
					log.log(Level.SEVERE, "Failed to stop executore",
							illegalStateException);
					throw illegalStateException;
				}
			} catch (InterruptedException e) {
				// ignore
			}
			worker = null;
		}
	}

	private class CodeRunner implements Runnable {
		@Override
		public void run() {
			if (webScannnerReady()) {
				byte[] image = null;
				try {
					image = (byte[]) camera
							.getValue(
									havis.capture.adapter.camera.FieldConstants.DEVICEID,
									havis.capture.adapter.camera.FieldConstants.IMAGE);
					if (image!=null){
						current = imager.transform(image);
						if (current!=null && current.getBarcodes().size()>0){
							stopExecutor();
						}	
					}
				} catch (Throwable e) {
					log.log(Level.FINE, "Failed to process image ", e);
				}

			}
		}
	}

}