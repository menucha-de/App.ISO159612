package havis.app.iso159612.imager;

public class WebcamScannerException extends Exception {

	private static final long serialVersionUID = 1L;

	public WebcamScannerException(String message) {
		super(message);
	}

	public WebcamScannerException(String message, Throwable cause) {
		super(message, cause);
	}

	public WebcamScannerException(Throwable cause) {
		super(cause);
	}
}