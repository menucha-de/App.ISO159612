package havis.custom.harting.iso159612.hw.impl;

import havis.application.common.HAL;
import havis.application.common.data.JsonArray;
import havis.application.common.data.Result;
import havis.application.common.data.Tag;
import havis.application.common.data.TagOperation;
import havis.custom.harting.iso159612.data.Bank;
import havis.custom.harting.iso159612.data.CCOpType;
import havis.custom.harting.iso159612.data.CCStatus;
import havis.custom.harting.iso159612.data.KeyValuePair;
import havis.custom.harting.iso159612.hw.RfidReader;
import havis.custom.harting.iso159612.utils.Translation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * A {@link RfidReader} implementation which uses the Hal.js
 */
public class HalRfidReader implements RfidReader {

	public HalRfidReader() {

	}

	/**
	 * @return True if C1G2 service is available.
	 */
	private boolean isServiceAvailable() {
		return HAL.Service.C1G2.isSupported();
	}

	/**
	 * The method {@code onSuccess(...)} of the {@code callback} will be called
	 * if only 1 tag is in the reader field. In all other cases the
	 * {@code onFailure(...)} method is called.
	 * 
	 * @param callback
	 */
	private void getTagInReaderField(final AsyncCallback<Tag> callback) {
		HAL.Service.C1G2.tagInventory(new AsyncCallback<JsonArray<Tag>>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(JsonArray<Tag> result) {
				if (result == null || result.length() == 0)
					callback.onFailure(new Throwable(Translation.LANG.msgServiceC1g2NoTag()));
				else if (result.length() > 1)
					callback.onFailure(new Throwable(Translation.LANG.msgServiceC1g2MultipleTags()));
				else
					callback.onSuccess(result.get(0));
			}
		});
	}

	/**
	 * Executes the specified {@code operations} on the tags in the reader
	 * fields. The method {@code onSuccess(...)} of the {@code callback} will be
	 * called if the operations have been executed. In all other cases the
	 * {@code onFailure(...)} method is called.
	 * 
	 * @param operations
	 *            The operations which shall be executed.
	 * @param id
	 *            The id of the tag on which the operations shall be executed.
	 * @param callback
	 */
	private void executeTagOperation(List<TagOperation> operations, String id, final AsyncCallback<List<Result>> callback) {
		JsonArray<TagOperation> jsonOperations = JsonArray.createInstance();
		for (TagOperation operation : operations)
			jsonOperations.push(operation);
		HAL.Service.C1G2.executeTagOperation(jsonOperations, id, new AsyncCallback<JsonArray<Result>>() {

			@Override
			public void onSuccess(JsonArray<Result> results) {
				List<Result> result = new ArrayList<Result>();
				if (results != null && results.length() > 0) {
					for (int i = 0; i < results.length(); i++) {
						result.add(results.get(i));
					}
				}
				callback.onSuccess(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}

	/**
	 * Reads the data in user memory of the tag in the reader field. The method
	 * {@code onSuccess(...)} of the {@code callback} will be called if only 1
	 * tag was in the reader field and the data of the tag could be read. In all
	 * other cases the {@code onFailure(...)} method is called.
	 * 
	 * @param callback
	 */
	private void readUserMemoryTag(final AsyncCallback<KeyValuePair<Tag, String>> callback) {
		if (isServiceAvailable()) {
			getTagInReaderField(new AsyncCallback<Tag>() {

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}

				@Override
				public void onSuccess(final Tag tag) {
					List<TagOperation> tagOperations = new ArrayList<TagOperation>();
					TagOperation tagOperation = TagOperation.createInstance(CCOpType.READ.toString(), Bank.USER.getIndex(), 0, 0);
					tagOperations.add(tagOperation);
					executeTagOperation(tagOperations, tag.getId(), new AsyncCallback<List<Result>>() {

						@Override
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}

						@Override
						public void onSuccess(List<Result> results) {
							if (results == null || results.size() < 1)
								callback.onFailure(new Throwable(Translation.LANG.msgServiceC1g2ReadFailedNoResults()));
							else if (!CCStatus.SUCCESS.isEquals(results.get(0).getState()))
								callback.onFailure(
										new Throwable(Translation.LANG.msgServiceC1g2ReadFailedState(results.get(0).getState())));
							else {
								String data = results.get(0).getData();
								if (data == null || data.isEmpty())
									callback.onFailure(new Throwable(Translation.LANG.msgServiceC1g2ReadFailedNoData()));
								else
									callback.onSuccess(new KeyValuePair<>(tag, data));
							}
						}
					});
				}
			});
		} else {
			callback.onFailure(new Throwable(Translation.LANG.msgServiceC1g2NotAvailable()));
		}
	}

	@Override
	public void readUserMemory(final AsyncCallback<String> callback) {
		readUserMemoryTag(new AsyncCallback<KeyValuePair<Tag, String>>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(KeyValuePair<Tag, String> result) {
				callback.onSuccess(result.getValue());
			}
		});
	}

	@Override
	public void writeUserMemory(final String data, final AsyncCallback<Void> callback) {
		readUserMemoryTag(new AsyncCallback<KeyValuePair<Tag, String>>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(KeyValuePair<Tag, String> result) {
				String userMemory = result.getValue();
				if (userMemory == null || userMemory.trim().length() < 1) {
					callback.onFailure(new Throwable(Translation.LANG.msgServiceC1g2WriteFailedSizeNotAvailable()));
				} else if (data == null || (data.startsWith("x") && data.length() < 2) || (!data.startsWith("x") && data.length() < 1)) {
					callback.onFailure(new Throwable(Translation.LANG.msgServiceC1g2WriteFailedDataIsMissing()));
				} else {
					String dataToWrite = data.startsWith("x") ? data : "x" + data;
					int offset = 0;
					// Subtract 4 for the x
					int length = userMemory.length() * 4 - 4;
					while ((dataToWrite.length() * 4 - 4) < length)
						dataToWrite += "0";

					if (dataToWrite.length() * 4 - 4 > length) {
						callback.onFailure(new Throwable(Translation.LANG.msgServiceC1g2WriteFailedUserBankToSmall()));
					} else {
						List<TagOperation> tagOperations = new ArrayList<TagOperation>();
						TagOperation tagOperation = TagOperation.createInstance(CCOpType.WRITE.toString(), Bank.USER.getIndex(), offset,
								length, dataToWrite);
						tagOperations.add(tagOperation);

						executeTagOperation(tagOperations, result.getKey().getId(), new AsyncCallback<List<Result>>() {

							@Override
							public void onFailure(Throwable caught) {
								callback.onFailure(caught);
							}

							@Override
							public void onSuccess(List<Result> results) {
								if (results == null || results.size() < 1)
									callback.onFailure(new Throwable(Translation.LANG.msgServiceC1g2WriteFailedNoResults()));
								else if (!CCStatus.SUCCESS.isEquals(results.get(0).getState()))
									callback.onFailure(
											new Throwable(Translation.LANG.msgServiceC1g2WriteFailedState(results.get(0).getState())));
								else
									callback.onSuccess(null);
							}
						});
					}
				}
			}
		});
	}
}