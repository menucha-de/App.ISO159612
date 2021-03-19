package havis.app.iso159612.rest;

import havis.app.iso159612.imager.WebScannerManager;
import havis.transform.transformer.imager.Report;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("app/iso159612")
public class WebcamScannerService {

	private WebScannerManager manager;

	public WebcamScannerService(WebScannerManager manager) {
		this.manager = manager;
	}

	@PermitAll
	@GET
	@Path("ready")
	@Produces({ MediaType.APPLICATION_JSON })
	public boolean getServicesReady() {
		return manager.webScannnerReady();
	}

	@PermitAll
	@GET
	@Path("report")
	@Produces({ MediaType.APPLICATION_JSON })
	public Report getCurrentReport() throws Exception {
		return manager.getReport();
	}

	@PermitAll
	@GET
	@Path("scan")
	public void startScan() throws Exception {
		manager.startScan();
	}

	@PermitAll
	@GET
	@Path("cancelScan")
	public void cancelScan() throws Exception {
		manager.cancelScan();
	}

}