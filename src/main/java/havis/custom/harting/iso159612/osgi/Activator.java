package havis.custom.harting.iso159612.osgi;

import havis.capture.AdapterHandler;
import havis.custom.harting.iso159612.imager.WebScannerManager;
import havis.custom.harting.iso159612.rest.RESTApplication;
import havis.transform.Transformer;
import havis.transform.ValidationException;

import java.util.logging.Logger;

import javax.ws.rs.core.Application;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

	@SuppressWarnings("unused")
	private final static Logger log = Logger.getLogger(Activator.class
			.getName());

	private WebScannerManager manager;
	private ServiceRegistration<Application> app;
	private ServiceTracker<AdapterHandler, AdapterHandler> adapterTracker;
	private ServiceTracker<Transformer, Transformer> transformerTracker;

	@Override
	public void start(BundleContext context) throws Exception {
		manager = new WebScannerManager();
		adapterTracker = new ServiceTracker<AdapterHandler, AdapterHandler>(
				context, AdapterHandler.class, null) {
			@Override
			public AdapterHandler addingService(
					ServiceReference<AdapterHandler> reference) {
				AdapterHandler adapter = null;
				synchronized (Activator.this) {
					if (reference.getProperty("NAME").equals("camera")) {
						adapter = super.addingService(reference);
						manager.setCamera(adapter);
					}
				}
				return adapter;
			}

			@Override
			public void removedService(
					ServiceReference<AdapterHandler> reference,
					AdapterHandler service) {

				super.removedService(reference, service);
			}
		};
		adapterTracker.open(true);

		transformerTracker = new ServiceTracker<Transformer, Transformer>(
				context, Transformer.class, null) {
			@Override
			public Transformer addingService(
					ServiceReference<Transformer> reference) {
				Transformer transformer = null;
				synchronized (Activator.this) {
					if (reference.getProperty("NAME").equals("imager")) {
						transformer = super.addingService(reference);
						try {
							transformer.init(null);
						} catch (ValidationException e) {
						}
						manager.setImager(transformer);
					}
				}
				return transformer;
			}

			@Override
			public void removedService(ServiceReference<Transformer> reference,
					Transformer service) {

				super.removedService(reference, service);
			}
		};
		transformerTracker.open(true);

		app = context.registerService(Application.class, new RESTApplication(
				manager), null);

	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (manager != null) {
			manager.close();
		}

		if (app != null) {
			app.unregister();
			app = null;
		}
		adapterTracker.close();
		transformerTracker.close();

	}
}