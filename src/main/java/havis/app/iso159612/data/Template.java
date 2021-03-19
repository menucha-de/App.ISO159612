package havis.app.iso159612.data;

import havis.application.common.data.Json;
import havis.application.common.data.JsonArray;

/**
 * 
 */
public class Template extends Json {

	protected Template() {

	}

	/**
	 * Creates a new instance of {@link Template}
	 * 
	 * @return New instance of {@link Template}
	 */
	public static Template newInstance() {
		return Template.createInstance();
	}

	/**
	 * Creates a new instance of {@link Template}
	 * 
	 * @param name
	 *            The name of the template
	 * @return New instance of {@link Template}
	 */
	public static Template newInstance(String name) {
		Template template = Template.createInstance();
		template.setName(name);
		return template;
	}

	/**
	 * Creates a new instance of {@link Template}
	 * 
	 * @param name
	 *            The name of the template
	 * @param addFields
	 *            {@code true} if missing fields shall be added.
	 * @param elements
	 *            The elements of the template.
	 * @return New instance of {@link Template}
	 */
	public static Template newInstance(String name, boolean addFields, JsonArray<TemplateEntry> elements) {
		Template template = Template.createInstance();
		template.setName(name);
		template.setAddFields(addFields);
		template.setElements(elements);
		return template;
	}

	/**
	 * @return The default template
	 */
	public static Template getDefault() {
		Template template = Template.createInstance();
		template.setName("<DEFAULT>");
		template.setAddFields(true);
		return template;
	}

	/**
	 * @return The name of the template.
	 */
	public final native String getName() /*-{
		return this.name;
	}-*/;

	/**
	 * Sets the name of the template
	 * 
	 * @param name
	 *            The new name.
	 */
	public final native void setName(String name) /*-{
		this.name = name;
	}-*/;

	/**
	 * @return {@code true} if missing fields shall be added. {@code false} if
	 *         missing fields shall be dropped.
	 */
	public final native boolean isAddFields() /*-{
		return this.addFields;
	}-*/;

	/**
	 * Specifies if missing fields shall be added or dropped.
	 * 
	 * @param addFields
	 *            {@code true} if missing fields shall be added. {@code false}
	 *            if missing fields shall be dropped.
	 */
	public final native void setAddFields(boolean addFields) /*-{
		this.addFields = addFields;
	}-*/;

	/**
	 * @return The elements of the template.
	 */
	public final native JsonArray<TemplateEntry> getElements() /*-{
		return this.elements;
	}-*/;

	/**
	 * Set the elements of the template.
	 * 
	 * @param elements
	 *            The elements.
	 */
	public final native void setElements(JsonArray<TemplateEntry> elements) /*-{
		this.elements = elements;
	}-*/;
}
