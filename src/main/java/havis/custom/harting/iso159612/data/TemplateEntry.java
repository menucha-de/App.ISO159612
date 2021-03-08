package havis.custom.harting.iso159612.data;

import havis.application.common.data.Json;

public class TemplateEntry extends Json {

	protected TemplateEntry() {

	}

	public static TemplateEntry newInstance() {
		return TemplateEntry.createInstance();
	}

	public static TemplateEntry newInstance(String oid, String value, boolean override) {
		TemplateEntry result = TemplateEntry.createInstance();
		result.setOid(oid);
		result.setValue(value);
		result.setOverride(override);
		return result;
	}

	public final native String getOid() /*-{
		return this.oid;
	}-*/;

	public final native void setOid(String oid) /*-{
		this.oid = oid;
	}-*/;

	public final native String getValue() /*-{
		return this.value;
	}-*/;

	public final native void setValue(String value) /*-{
		this.value = value;
	}-*/;

	public final native boolean getOverride() /*-{
		return this.override;
	}-*/;

	public final native void setOverride(boolean override) /*-{
		this.override = override;
	}-*/;
}
