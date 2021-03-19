package havis.app.iso159612.ui.client;

import havis.app.iso159612.data.Controller;
import havis.app.iso159612.data.Settings;
import havis.app.iso159612.data.Template;
import havis.app.iso159612.data.TemplateEntry;
import havis.app.iso159612.hw.BarcodeReader;
import havis.app.iso159612.hw.RfidReader;
import havis.app.iso159612.ui.client.widgets.HavisActivateableComposite;
import havis.app.iso159612.ui.client.widgets.HavisParameter;
import havis.app.iso159612.ui.client.widgets.ReadWriteFlexTable;
import havis.app.iso159612.ui.client.widgets.ResultBox;
import havis.app.iso159612.ui.client.widgets.ScannerOverlay;
import havis.app.iso159612.utils.Translation;
import havis.middleware.tdt.Barcode;
import havis.middleware.tdt.PackedObjectInvestigator;
import havis.middleware.tdt.PackedObjects;
import havis.middleware.tdt.TdtResources;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReadWritePanel extends HavisActivateableComposite {

	private final static String GS1_BARCODE_OID = "urn:oid:1.0.15961.9";

	private BarcodeReader barcodeReader = GWT.create(BarcodeReader.class);
	private RfidReader rfidReader = GWT.create(RfidReader.class);

	@UiField(provided = true)
	protected ReadWriteFlexTable table;
	@UiField
	protected Label add;
	@UiField
	protected Button scan;
	@UiField
	protected Button read;
	@UiField
	protected Button write;
	@UiField
	protected ScrollPanel scrollPanel;
	@UiField
	protected FlowPanel container;
	@UiField
	protected ResultBox resultBox;

	@UiField
	protected FlowPanel toolArea;

	@UiField
	protected ScannerOverlay scanner;

	private static ReadWritePanelUiBinder uiBinder = GWT
			.create(ReadWritePanelUiBinder.class);

	interface ReadWritePanelUiBinder extends UiBinder<Widget, ReadWritePanel> {
	}

	private Settings settings;

	private Template template;

	private Controller controller;

	private Image preview;

	public ReadWritePanel(final Controller controller) {
		this.controller = controller;
		table = new ReadWriteFlexTable(controller);
		initWidget(uiBinder.createAndBindUi(ReadWritePanel.this));

		add.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				activateComposite(SelectPanel.class, null);
			}
		});
		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				resize();
			}
		});
	}

	@Override
	public void onShow(HavisParameter parameter) {
		resultBox.clear();
		scrollPanel.scrollToBottom();
		if (parameter != null && parameter.getFrom() != null
				&& SelectPanel.class.equals(parameter.getFrom().getClass())) {
			// add field to list
			if (parameter.getParameter() != null) {
				String oid = parameter.getParameter();
				addField(oid, "");
			}
		} else {
			// load settings and the selected template
			reset();
			settings = null;
			template = null;
			controller.getSettings(new AsyncCallback<Settings>() {

				@Override
				public void onFailure(Throwable caught) {
					controller.showError(caught.getMessage());
				}

				@Override
				public void onSuccess(Settings result) {
					settings = result;
					controller
							.getTemplates(new AsyncCallback<List<Template>>() {

								@Override
								public void onFailure(Throwable caught) {
									controller.showError(caught.getMessage());
								}

								@Override
								public void onSuccess(List<Template> result) {
									for (Template t : result) {
										if (t.getName().equals(
												settings.getTemplate())) {
											template = t;
											break;
										}
									}
									if (template == null)
										template = Template.getDefault();
									reset();
								}
							});
				}
			});

		}
		resize();

		if (!controller.isHandheld()) {
			preview = barcodeReader.getPreviewImage();
			toolArea.setVisible(true);
		}
	}

	@Override
	public void onLeave() {
	}

	@Override
	public void onKeyEvent(String key) {
		if (key != null) {
			switch (key.toLowerCase()) {
			case "leftaction":
				scan();
				break;
			case "frontaction":
				read();
				break;
			case "rightaction":
				write();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Calculates the max height for the scroll panel
	 */
	private void resize() {
		int maxHeight = container.getOffsetHeight() - add.getOffsetHeight()
				- 25;
		if (controller.isHandheld()) {
			scrollPanel.getElement().getStyle()
					.setProperty("maxHeight", maxHeight + "px");
		}
	}

	/**
	 * Clears all fields and resets the UI as specified by the used template.
	 */
	private void reset() {
		table.removeAllRows();
		if (template != null && template.getElements() != null) {
			for (int i = 0; i < template.getElements().length(); i++) {
				TemplateEntry entry = template.getElements().get(i);
				table.addField(entry.getOid(), entry.getValue());
			}
		}
	}

	/**
	 * Updates the data element in the list. The selected template will be used
	 * for the update.
	 * 
	 * @param oid
	 *            The OID of the data element
	 * @param value
	 *            The value of the data element
	 */
	private void updateField(String oid, String value) {
		boolean update = true;
		if (template != null && template.getElements() != null) {
			for (int i = 0; i < template.getElements().length(); i++) {
				TemplateEntry entry = template.getElements().get(i);
				if (entry.getOid().equals(oid) && !entry.getOverride())
					update = false;
			}
			if (template.isAddFields() || table.indexOfOid(oid) > -1) {
				if (update)
					table.updateField(oid, value);
			}
		} else {
			table.updateField(oid, value);
		}
	}

	/**
	 * Adds a field to the list of data elements.
	 * 
	 * @param oid
	 *            The OID of the data element
	 * @param value
	 *            The value of the data element
	 */
	private void addField(String oid, String value) {
		int row = table.indexOfOid(oid);
		if (row < 0) {
			row = table.addField(oid, value);
			scrollPanel.scrollToBottom();
		} else {
			int scrollposition = table.getWidget(row, 0).getAbsoluteTop()
					- table.getAbsoluteTop() + container.getAbsoluteTop()
					- scrollPanel.getAbsoluteTop() - 1;
			scrollPanel.setVerticalScrollPosition(scrollposition);
			table.setWarning(row);
		}
	}

	/**
	 * Scans the barcode and updates the list. The selected template will be
	 * used for the update.
	 */
	private void scan() {
		if (preview != null) {
			scanner.setReader(barcodeReader, resultBox);
			scanner.setVisible(true);
		}
		table.removeAllRows();
		resultBox.showInfo(Translation.LANG.rwResultScanning());
		Timer timer = new Timer() {
			@Override
			public void run() {
				reset();
				barcodeReader.scan(new AsyncCallback<String>() {
					@Override
					public void onFailure(Throwable caught) {
						resultBox.showError(Translation.LANG.rwResultError());
						controller.showError(caught.getMessage());
						if (preview != null) {
							scanner.setVisible(false);
						}
					}

					@Override
					public void onSuccess(final String barcode) {
						resultBox.showInfo(Translation.LANG.rwResultAnalysing());
						Barcode tdtBarcode = controller.getBarcode();
						try {
							List<Map.Entry<String, String>> result = tdtBarcode
									.decodeGs1128Barcode(barcode);
							resultBox.showInfo(Translation.LANG
									.rwResultLoading());
							for (Map.Entry<String, String> entry : result) {
								String oid = GS1_BARCODE_OID
										+ "."
										+ (entry.getKey().startsWith("0") ? entry
												.getKey().substring(1) : entry
												.getKey());
								updateField(oid, entry.getValue());
							}
							resultBox.showSuccess(Translation.LANG
									.rwScanResultSuccess());
							resize();
						} catch (Throwable caught) {
							resultBox.showError(Translation.LANG
									.rwResultError());
							controller.showError(caught.getMessage());
						}
						if (preview != null) {
							scanner.setVisible(false);
						}
					}
				});
			}
		};
		timer.schedule(0);
	}

	/**
	 * Reads the user memory of the tag in the reader field and displays the
	 * decoded data elements
	 */
	private void read() {
		table.removeAllRows();
		resultBox.showInfo(Translation.LANG.rwResultReading());
		Timer timer_1 = new Timer() {
			@Override
			public void run() {
				reset();
				rfidReader.readUserMemory(new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						resultBox.showError(Translation.LANG.rwResultError());
						controller.showError(caught.getMessage());
					}

					@Override
					public void onSuccess(final String result) {
						resultBox.showInfo(Translation.LANG.rwResultDecode());
						Timer timer_2 = new Timer() {
							@Override
							public void run() {
								try {
									if (result == null) {
										resultBox.showError(Translation.LANG
												.rwResultError());
										controller.showError(Translation.LANG
												.msgUnknownError());
									} else {
										resultBox.showInfo(Translation.LANG
												.rwResultAnalysing());
										PackedObjects packedObjects = controller
												.getPackedObjectInvestigator()
												.decodePackedObjects(
														result.startsWith("x") ? result
																.substring(1)
																: result);
										if (packedObjects.getDataElements()
												.size() == 0) {
											resultBox
													.showSuccess(Translation.LANG
															.rwResultNoData());
										} else {
											resultBox.showInfo(Translation.LANG
													.rwResultLoading());
											for (Map.Entry<String, String> entry : packedObjects
													.getDataElements())
												updateField(entry.getKey(),
														entry.getValue());
											resultBox
													.showSuccess(Translation.LANG
															.rwReadResultSuccess());
										}
									}
									resize();
								} catch (Exception e) {
									resultBox.showError(Translation.LANG
											.rwResultError());
									controller.showError(e.getMessage());
								}
							}
						};
						timer_2.schedule(0);
					}
				});
			}
		};
		timer_1.schedule(0);
	}

	/**
	 * Writes the data elements in the list into the tag in the reader field.
	 */
	private void write() {
		resultBox.showInfo(Translation.LANG.rwResultValidate());
		Timer timer1 = new Timer() {
			@Override
			public void run() {
				if (table.validate(new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						resultBox.showError(Translation.LANG.rwResultError());
						controller.showError(caught.getMessage());
					}

					@Override
					public void onSuccess(String oid) {
						resultBox.showError(Translation.LANG.rwResultError());
						int row = table.indexOfOid(oid);
						if (row > -1) {
							int scrollposition = table.getWidget(row, 0)
									.getAbsoluteTop()
									- table.getAbsoluteTop()
									+ container.getAbsoluteTop()
									- scrollPanel.getAbsoluteTop() - 1;
							scrollPanel
									.setVerticalScrollPosition(scrollposition);
							table.setError(row);
						}
					}
				})) {
					resultBox.showInfo(Translation.LANG.rwResultEncode());
					Timer timer2 = new Timer() {
						@Override
						public void run() {
							try {
								final String hexData = controller
										.getPackedObjectInvestigator()
										.encodePackedObjects(getPackedObjects());
								resultBox.showInfo(Translation.LANG
										.rwResultWriting());

								Timer timer3 = new Timer() {
									@Override
									public void run() {
										try {
											rfidReader.writeUserMemory(hexData,
													new AsyncCallback<Void>() {

														@Override
														public void onSuccess(
																Void result) {
															resultBox
																	.showSuccess(Translation.LANG
																			.rwWriteResultSuccess());
														}

														@Override
														public void onFailure(
																Throwable caught) {
															resultBox
																	.showError(Translation.LANG
																			.rwResultError());
															controller
																	.showError(caught
																			.getMessage());
														}
													});
										} catch (Exception e) {
											resultBox
													.showError(Translation.LANG
															.rwResultError());
											controller
													.showError(e.getMessage());
										}
									}
								};
								timer3.schedule(0);
							} catch (Exception e) {
								resultBox.showError(Translation.LANG
										.rwResultError());
								controller.showError(e.getMessage());
							}
						}
					};
					timer2.schedule(0);
				}
			}
		};
		timer1.schedule(0);
	}

	private PackedObjects getPackedObjects() {
		PackedObjects result = new PackedObjects(new PackedObjectInvestigator(TdtResources.INSTANCE.tableForData().getText()));
		result.getDataElements().addAll(table.getDataElements());
		return result;
	}

	@UiHandler("scan")
	public void onScanClick(ClickEvent e) {
		scan();
	}

	@UiHandler("read")
	public void onReadClick(ClickEvent e) {
		read();
	}

	@UiHandler("write")
	public void onWriteClick(ClickEvent e) {
		write();
	}
}
