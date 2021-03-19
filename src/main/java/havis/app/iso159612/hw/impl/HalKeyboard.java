package havis.app.iso159612.hw.impl;

import com.google.gwt.user.client.rpc.AsyncCallback;

import havis.application.common.HAL;
import havis.application.common.event.KeyEventListener;
import havis.app.iso159612.hw.Keyboard;

public class HalKeyboard implements Keyboard{

	@Override
	public boolean isSupported() {
		return HAL.Service.Keyboard.isSupported();
	}

	@Override
	public void setKeyEventListener(KeyEventListener listener) {
		HAL.Service.Keyboard.setKeyEventListener(listener);
	}

	@Override
	public void addSocketListener(AsyncCallback<Void> callback) {
		HAL.addSocketListener(callback);
	}

}
