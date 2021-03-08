package havis.custom.harting.iso159612.data;

import havis.application.common.data.Json;

public class Settings extends Json {

	protected Settings() {

	}

	/**
	 * Creates a new instance of {@link Settings}
	 * 
	 * @return New instance of {@link Settings}
	 */
	public static Settings newInstance() {
		return createInstance();
	}

	public final native IdEncoding getIdEncoding() /*-{
		return this.idEncoding;
	}-*/;

	public final native void setIdEncoding(IdEncoding idEncoding) /*-{
		this.idEncoding = idEncoding;
	}-*/;

	public final native Compaction getCompaction() /*-{
		return this.compaction;
	}-*/;

	public final native void setCompaction(Compaction compaction) /*-{
		this.compaction = compaction;
	}-*/;

	public final native String getTemplate() /*-{
		return this.template;
	}-*/;

	public final native void setTemplate(String template) /*-{
		this.template = template;
	}-*/;
}
