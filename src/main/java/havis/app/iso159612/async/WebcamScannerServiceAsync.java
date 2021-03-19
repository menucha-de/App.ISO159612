package havis.app.iso159612.async;

import havis.transform.transformer.imager.Report;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

@Path("../rest/app/iso159612")
public interface WebcamScannerServiceAsync extends RestService {
	@GET
	@Path("report")
	void getReport(MethodCallback<Report> callback);

	@GET
	@Path("scan")
	void scan(MethodCallback<Void> callback);

	@GET
	@Path("cancelScan")
	void cancelScan(MethodCallback<Void> callback);

}