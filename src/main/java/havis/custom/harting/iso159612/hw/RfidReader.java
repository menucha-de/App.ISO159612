package havis.custom.harting.iso159612.hw;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RfidReader {

	/**
	 * Reads the data in user memory of the tag in the reader field. The method
	 * {@code onSuccess(...)} of the {@code callback} will be called
	 * if only 1 tag was in the reader field and the data of the tag could be
	 * read. In all other cases the {@code onFailure(...)} method is
	 * called.
	 * 
	 * @param callback
	 */
	void readUserMemory(AsyncCallback<String> callback);

	/**
	 * The method {@code onSuccess(...)} of the {@code callback} will
	 * be called if only 1 tag was in the reader field and the data has been
	 * successfully written in the user memory. In all other cases the
	 * {@code onFailure(...)} method is called.
	 * 
	 * @param data
	 *            The data which shall be written in the user memory of the tag.
	 * @param callback
	 */
	void writeUserMemory(String data, AsyncCallback<Void> callback);
}
