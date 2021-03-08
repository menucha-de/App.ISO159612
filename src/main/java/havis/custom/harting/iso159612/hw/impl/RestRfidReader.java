package havis.custom.harting.iso159612.hw.impl;

import havis.application.common.data.Result;
import havis.application.common.data.Sighting;
import havis.application.common.data.Tag;
import havis.application.common.data.TagOperation;
import havis.custom.harting.iso159612.data.Bank;
import havis.custom.harting.iso159612.data.CCOpType;
import havis.custom.harting.iso159612.data.CCStatus;
import havis.custom.harting.iso159612.data.KeyValuePair;
import havis.custom.harting.iso159612.hw.RfidReader;
import havis.custom.harting.iso159612.utils.Translation;
import havis.device.rf.tag.TagData;
import havis.device.rf.tag.operation.ReadOperation;
import havis.device.rf.tag.operation.WriteOperation;
import havis.device.rf.tag.result.OperationResult;
import havis.device.rf.tag.result.ReadResult;
import havis.device.rf.tag.result.WriteResult;
import havis.middleware.tdt.DataTypeConverter;
import havis.net.rest.rf.async.RFDeviceServiceAsync;

import java.util.ArrayList;
import java.util.List;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * A {@link RfidReader} implementation which uses the Hal.js
 */
public class RestRfidReader implements RfidReader {

	int currentPwd = 0;

	private RFDeviceServiceAsync service = GWT
			.create(RFDeviceServiceAsync.class);

	public RestRfidReader() {

	}

	/**
	 * The method {@code onSuccess(...)} of the {@code callback} will be called
	 * if only 1 tag is in the reader field. In all other cases the
	 * {@code onFailure(...)} method is called.
	 * 
	 * @param callback
	 */
	private void getTagInReaderField(final AsyncCallback<Tag> callback) {

		service.getTags(new MethodCallback<List<TagData>>() {

			@Override
			public void onFailure(Method method, Throwable exception) {
				callback.onFailure(exception);
			}

			@Override
			public void onSuccess(Method method, List<TagData> response) {
				if (response.size() > 0) {
					TagData tagData = response.get(0);
					String epc = DataTypeConverter.byteArrayToHexString(tagData
							.getEpc());
					Tag tag = Tag.createInstance(epc);
					tag.setEpc(epc);
					short pc = tagData.getPc();
					byte[] ret = new byte[2];
					ret[1] = (byte) (pc & 0xff);
					ret[0] = (byte) ((pc >> 8) & 0xff);

					StringBuilder sb = new StringBuilder();
					for (byte b : ret) {
						sb.append(Integer.toHexString(b));
					}
					tag.setPc(sb.toString());
					Sighting sighting = Sighting.createInstance(null,
							tagData.getAntennaID(), tagData.getRssi());
					tag.setSighting(sighting);
					callback.onSuccess(tag);
				}

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
	private void executeTagOperation(List<TagOperation> operations, String id,
			final AsyncCallback<List<Result>> callback) {

		List<havis.device.rf.tag.operation.TagOperation> tagOperations = new ArrayList<havis.device.rf.tag.operation.TagOperation>();

		for (TagOperation operation : operations) {
			havis.device.rf.tag.operation.TagOperation tagOpertation = null;

			switch (operation.getType()) {
			case "READ":
				tagOpertation = new ReadOperation("read" + id,
						(short) operation.getBank(),
						(short) operation.getOffset(),
						(short) operation.getLength(), currentPwd);

				break;
			case "WRITE":
				tagOpertation = new WriteOperation("write" + id,
						(short) operation.getBank(),
						(short) operation.getOffset(), DataTypeConverter.hexStringToByteArray(operation.getData().substring(1)),
						currentPwd);
			default:
				break;
			}

			if (tagOpertation != null) {
				tagOperations.add(tagOpertation);
			}
		}

		service.getTags(id, tagOperations, new MethodCallback<List<TagData>>() {
			@Override
			public void onSuccess(Method method, List<TagData> response) {
				List<Result> results = new ArrayList<Result>();
				if (response.size() > 0) {
					TagData tagData = response.get(0);
					for (OperationResult opResult : tagData.getResultList()) {
						Result result = Result.createInstance();
						if (opResult instanceof ReadResult) {
							ReadResult readResult = (ReadResult) opResult;
							if (readResult.getResult().equals(
									ReadResult.Result.SUCCESS)) {

								result.setData(DataTypeConverter
												.byteArrayToHexString(readResult
														.getReadData()));
								result.setState("SUCCESS");
							} else {
								result.setState(readResult.getResult().toString());
							}
						} else if (opResult instanceof WriteResult) {
							WriteResult writeResult = (WriteResult) opResult;
							if (writeResult.getResult().equals(
									WriteResult.Result.SUCCESS)) {
								result.setState("SUCCESS");
							} else {
								result.setState(writeResult.getResult().toString());
							}
						}
						results.add(result);
					}
				}
				callback.onSuccess(results);
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				callback.onFailure(exception);
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
	private void readUserMemoryTag(
			final AsyncCallback<KeyValuePair<Tag, String>> callback) {
		getTagInReaderField(new AsyncCallback<Tag>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(final Tag tag) {
				List<TagOperation> tagOperations = new ArrayList<TagOperation>();
				TagOperation tagOperation = TagOperation.createInstance(
						CCOpType.READ.toString(), Bank.USER.getIndex(), 0, 0);
				tagOperations.add(tagOperation);
				executeTagOperation(tagOperations, tag.getId(),
						new AsyncCallback<List<Result>>() {

							@Override
							public void onFailure(Throwable caught) {
								callback.onFailure(caught);
							}

							@Override
							public void onSuccess(List<Result> results) {
								if (results == null || results.size() < 1)
									callback.onFailure(new Throwable(
											Translation.LANG
													.msgServiceC1g2ReadFailedNoResults()));
								else if (!CCStatus.SUCCESS.isEquals(results
										.get(0).getState()))
									callback.onFailure(new Throwable(
											Translation.LANG
													.msgServiceC1g2ReadFailedState(results
															.get(0).getState())));
								else {
									String data = results.get(0).getData();
									if (data == null || data.isEmpty())
										callback.onFailure(new Throwable(
												Translation.LANG
														.msgServiceC1g2ReadFailedNoData()));
									else
										callback.onSuccess(new KeyValuePair<>(
												tag, data));
								}
							}
						});
			}
		});
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
	public void writeUserMemory(final String data,
			final AsyncCallback<Void> callback) {
		readUserMemoryTag(new AsyncCallback<KeyValuePair<Tag, String>>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(KeyValuePair<Tag, String> result) {
				String userMemory = result.getValue();
				if (userMemory == null || userMemory.trim().length() < 1) {
					callback.onFailure(new Throwable(Translation.LANG
							.msgServiceC1g2WriteFailedSizeNotAvailable()));
				} else if (data == null
						|| (data.startsWith("x") && data.length() < 2)
						|| (!data.startsWith("x") && data.length() < 1)) {
					callback.onFailure(new Throwable(Translation.LANG
							.msgServiceC1g2WriteFailedDataIsMissing()));
				} else {
					String dataToWrite = data.startsWith("x") ? data : "x"
							+ data;
					int offset = 0;
					// Subtract 4 for the x
					int length = userMemory.length() * 4 - 4;
					while ((dataToWrite.length() * 4 - 4) < length)
						dataToWrite += "0";

					if (dataToWrite.length() * 4 - 4 > length) {
						callback.onFailure(new Throwable(Translation.LANG
								.msgServiceC1g2WriteFailedUserBankToSmall()));
					} else {
						List<TagOperation> tagOperations = new ArrayList<TagOperation>();
						TagOperation tagOperation = TagOperation
								.createInstance(CCOpType.WRITE.toString(),
										Bank.USER.getIndex(), offset, length,
										dataToWrite);
						tagOperations.add(tagOperation);

						executeTagOperation(tagOperations, result.getKey()
								.getId(), new AsyncCallback<List<Result>>() {

							@Override
							public void onFailure(Throwable caught) {
								callback.onFailure(caught);
							}

							@Override
							public void onSuccess(List<Result> results) {
								if (results == null || results.size() < 1)
									callback.onFailure(new Throwable(
											Translation.LANG
													.msgServiceC1g2WriteFailedNoResults()));
								else if (!CCStatus.SUCCESS.isEquals(results
										.get(0).getState()))
									callback.onFailure(new Throwable(
											Translation.LANG
													.msgServiceC1g2WriteFailedState(results
															.get(0).getState())));
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