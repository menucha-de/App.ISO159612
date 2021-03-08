package havis.custom.harting.iso159612.hw;

import com.google.gwt.user.client.rpc.AsyncCallback;

import havis.application.common.event.KeyEventListener;

public interface Keyboard {
	public boolean isSupported();
	public void setKeyEventListener(KeyEventListener listener);
	public void addSocketListener(AsyncCallback<Void> callback);
}
