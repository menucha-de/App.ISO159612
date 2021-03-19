package havis.app.iso159612.ui.client;

import havis.app.iso159612.data.Controller;
import havis.app.iso159612.data.Template;
import havis.app.iso159612.ui.client.widgets.HavisActivateableComposite;
import havis.app.iso159612.ui.client.widgets.HavisParameter;
import havis.app.iso159612.ui.client.widgets.ResultBox;
import havis.app.iso159612.ui.client.widgets.TemplateFlexTable;
import havis.app.iso159612.utils.Translation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class TemplatesPanel extends HavisActivateableComposite {

	private static TemplatesPanelUiBinder uiBinder = GWT.create(TemplatesPanelUiBinder.class);

	interface TemplatesPanelUiBinder extends UiBinder<Widget, TemplatesPanel> {
	}

	private Controller controller;

	private final static String NEW_ITEM = Translation.LANG.templatePropertyTemplateItemNew();

	@UiField(provided = true)
	TemplateFlexTable table;
	@UiField
	protected ScrollPanel scrollPanel;
	@UiField
	protected FlowPanel container;
	@UiField
	protected Label add;
	@UiField
	protected ListBox templates;
	@UiField
	protected ListBox behavior;
	@UiField
	protected TextBox name;
	@UiField
	protected ResultBox resultBox;

	private Template selectedTemplate;

	private List<Template> templateList;

	private ChangeHandler templateChangeHandler = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			selectedTemplate = null;
			String selected = templates.getItemText(templates.getSelectedIndex());
			if (NEW_ITEM.equals(selected))
				selectedTemplate = null;
			else if (Template.getDefault().getName().equals(selected))
				selectedTemplate = Template.getDefault();
			else {
				for (Template t : templateList) {
					if (t.getName().equals(selected)) {
						selectedTemplate = t;
						break;
					}
				}
			}
			loadTemplate();
		}
	};

	public TemplatesPanel(Controller controller) {
		this.controller = controller;
		table = new TemplateFlexTable(controller);

		initWidget(uiBinder.createAndBindUi(this));

		templates.addChangeHandler(templateChangeHandler);
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

		behavior.addItem(Behavior.ADD_MISSING.getItemTest(), Behavior.ADD_MISSING.toString());
		behavior.addItem(Behavior.DROP_MISSING.getItemTest(), Behavior.DROP_MISSING.toString());
		setSelectedBehaviour(Behavior.ADD_MISSING);
	}

	@Override
	public void onShow(HavisParameter parameter) {
		resultBox.clear();
		if (parameter != null && parameter.getFrom() != null && SelectPanel.class.equals(parameter.getFrom().getClass())) {
			// add field to list
			if (parameter.getParameter() != null) {
				String oid = parameter.getParameter();
				addField(oid, "");
			}
		} else {
			// refresh page
			selectedTemplate = null;
			templates.clear();
			templateList = new ArrayList<Template>();
			controller.getTemplates(new AsyncCallback<List<Template>>() {

				@Override
				public void onSuccess(List<Template> result) {
					if (templateList != null) {
						templateList = result;
					}
					refreshTemplateList();
				}

				@Override
				public void onFailure(Throwable caught) {
					controller.showError(caught.getMessage());
				}
			});
		}
		resize();
	}

	@Override
	public void onLeave() {

	}

	@Override
	public void onKeyEvent(String key) {

	}

	/**
	 * Calculates the max height for the scroll panel
	 */
	private void resize() {
		int maxHeight = container.getOffsetHeight() - add.getOffsetHeight() - 14;
		if (controller.isHandheld()){
			scrollPanel.getElement().getStyle().setProperty("maxHeight", maxHeight + "px");
		}
	}

	/**
	 * Adds a field to the list of data elements
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
			int scrollposition = table.getWidget(row, 0).getAbsoluteTop() - table.getAbsoluteTop() + container.getAbsoluteTop()
					- scrollPanel.getAbsoluteTop() - 1;
			scrollPanel.setVerticalScrollPosition(scrollposition);
			table.setWarning(row);
		}
	}

	/**
	 * Refresh the list of existing templates.
	 */
	private void refreshTemplateList() {
		templates.clear();
		templates.addItem(NEW_ITEM, NEW_ITEM);
		templates.setSelectedIndex(0);
		templates.addItem(Template.getDefault().getName());
		if (templateList != null) {
			for (Template t : templateList)
				templates.addItem(t.getName(), t.getName());
		}
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), templates);
	}

	/**
	 * Loads the data of the selected template
	 */
	private void loadTemplate() {		
		// clear all fields
		table.removeAllRows();
		name.setValue("");
		setSelectedBehaviour(Behavior.ADD_MISSING);
		// if an existing template has been selected the data will be loaded.
		if (selectedTemplate != null) {
			// set name
			name.setValue(selectedTemplate.getName());
			// set the behavior
			if (selectedTemplate.isAddFields())
				setSelectedBehaviour(Behavior.ADD_MISSING);
			else
				setSelectedBehaviour(Behavior.DROP_MISSING);
			if (selectedTemplate.getElements() != null) {
				for (int i = 0; i < selectedTemplate.getElements().length(); i++)
					table.addField(selectedTemplate.getElements().get(i).getOid(), selectedTemplate.getElements().get(i).getValue(),
							selectedTemplate.getElements().get(i).getOverride());
			}
		}
	}

	/**
	 * @return The selected behavior
	 */
	private Behavior getSelectedBehavior() {
		Behavior behavior = Behavior.valueOf(this.behavior.getSelectedValue());
		return behavior == null ? Behavior.ADD_MISSING : behavior;
	}

	/**
	 * Sets the behavior which shall be selected.
	 * 
	 * @param behavior
	 *            The behavior which shall be selected.
	 */
	private void setSelectedBehaviour(Behavior behavior) {
		int size = this.behavior.getItemCount();
		for (int i = 0; i < size; i++) {
			if (this.behavior.getValue(i).equals(behavior.toString())) {
				this.behavior.setSelectedIndex(i);
				break;
			}
		}
	}

	/**
	 * Saves the current selected template
	 */
	private void save() {
		String templateName = this.name.getValue();
		if (templateName == null || templateName.trim().isEmpty()) {
			// Checks if template name is empty
			resultBox.showError(Translation.LANG.templateResultError());
			controller.showError(Translation.LANG.msgTemplateNameNotEmpty());
		} else if (Template.getDefault().getName().equals(templateName)) {
			// Checks if selected template is the default template which can not
			// be modified
			resultBox.showError(Translation.LANG.templateResultError());
			if (selectedTemplate == null || !selectedTemplate.getName().equals(templateName)){
				controller.showError(Translation.LANG.msgTemplateNameIsReserved(templateName));
			} else {
				controller.showError(Translation.LANG.msgTemplateNoModifcation(templateName));
			}
		} else if (NEW_ITEM.toLowerCase().equals(templateName.toLowerCase())
				|| Template.getDefault().getName().toLowerCase().equals(templateName.toLowerCase())) {
			// Checks if template name is a reserved name
			resultBox.showError(Translation.LANG.templateResultError());
			controller.showError(Translation.LANG.msgTemplateNameIsReserved(templateName));
		} else {
			boolean exists = false;
			if (selectedTemplate == null || !selectedTemplate.getName().equals(templateName))
				for (Template t : templateList)
					if (t.getName().equals(templateName))
						exists = true;
			if (exists) {
				// Checks if a template with the name already exists.
				resultBox.showError(Translation.LANG.templateResultError());
				controller.showError(Translation.LANG.msgTemplateTemplateExists(templateName));
			} else {
				// create new template if required
				if (selectedTemplate == null) {
					selectedTemplate = Template.newInstance();
					templateList.add(selectedTemplate);
				}
				// set new template name
				selectedTemplate.setName(templateName);
				// set template behavior
				switch (getSelectedBehavior()) {
				case DROP_MISSING:
					selectedTemplate.setAddFields(false);
					break;
				default:
					selectedTemplate.setAddFields(true);
					break;
				}
				// set template entries
				selectedTemplate.setElements(table.getTemplateDataElements());
				// Save all templates
				controller.setTemplates(templateList, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						resultBox.showError(Translation.LANG.templateResultError());
						controller.showError(caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						resultBox.showSuccess(Translation.LANG.templateResultSaved());						
						refreshTemplateList();
					}
				});
			}
		}
	}

	/**
	 * Deletes the current selected template
	 */
	private void delete() {
		String templateName = templates.getSelectedValue();
		if (NEW_ITEM.toLowerCase().equals(templateName.toLowerCase())
				|| Template.getDefault().getName().toLowerCase().equals(templateName.toLowerCase())) {
			controller.showError(Translation.LANG.msgTemplateNoDeletion(templateName));
		} else {
			templateList.remove(selectedTemplate);
			controller.setTemplates(templateList, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					controller.showError(caught.getMessage());
				}

				@Override
				public void onSuccess(Void result) {
					resultBox.showSuccess(Translation.LANG.templateResultDeleted());
					refreshTemplateList();
				}
			});
		}
	}

	@UiHandler("delete")
	public void onDelete(ClickEvent e) {
		delete();
	}

	@UiHandler("save")
	public void onSave(ClickEvent e) {
		save();
	}

	@UiHandler("reset")
	public void onReset(ClickEvent e) {
		resultBox.showSuccess(Translation.LANG.templateResultReset());
		loadTemplate();
	}

	/**
	 * Enumeration for behavior selection
	 */
	private enum Behavior {
		ADD_MISSING(Translation.LANG.templatePropertyBehaviorItemAdd()),
		DROP_MISSING(Translation.LANG.templatePropertyBehaviorItemDrop());

		private String itemText;

		private Behavior(String itemText) {
			this.itemText = itemText;
		}

		public String getItemTest() {
			return itemText;
		}
	}
}
