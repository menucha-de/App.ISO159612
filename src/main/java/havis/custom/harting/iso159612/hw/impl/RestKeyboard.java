package havis.custom.harting.iso159612.hw.impl;

import com.google.gwt.user.client.rpc.AsyncCallback;

import havis.application.common.event.KeyEventListener;
import havis.custom.harting.iso159612.hw.Keyboard;

public class RestKeyboard implements Keyboard{

	@Override
	public boolean isSupported() {
		return false;
	}

	@Override
	public void setKeyEventListener(KeyEventListener listener) {
	}

	@Override
	public void addSocketListener(AsyncCallback<Void> callback) {
	}

}
