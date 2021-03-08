package havis.custom.harting.iso159612.rest.provider;

import havis.custom.harting.iso159612.imager.WebcamScannerException;
import havis.net.rest.shared.data.SerializableValue;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WebcamScannerExceptionMapper implements
		ExceptionMapper<WebcamScannerException> {

	@Override
	public Response toResponse(WebcamScannerException e) {
		return Response.status(Response.Status.BAD_REQUEST)
				.entity(new SerializableValue<String>(e.getMessage()))
				.type(MediaType.APPLICATION_JSON).build();
	}
}