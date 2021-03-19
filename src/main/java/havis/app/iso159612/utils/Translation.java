package havis.app.iso159612.utils;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * I18N Translation interface {@link http
 * ://www.gwtproject.org/doc/latest/DevGuideI18n.html}
 * 
 */
public interface Translation extends Messages {
	public static final Translation LANG = GWT.create(Translation.class);

	String appName();

	String menuItemReadWrite();

	String menuItemTemplates();

	String menuItemSettings();

	String msgUnknownError();

	String msgServiceBarcodeNotAvailable();

	String msgServiceBarcodeNoData();

	String msgServiceC1g2NotAvailable();

	String msgServiceC1g2MultipleTags();

	String msgServiceC1g2NoTag();

	String msgServiceC1g2ReadFailedNoResults();

	String msgServiceC1g2ReadFailedState(String state);

	String msgServiceC1g2WriteFailedNoResults();

	String msgServiceC1g2ReadFailedNoData();

	String msgServiceC1g2WriteFailedState(String state);

	String msgServiceC1g2WriteFailedSizeNotAvailable();

	String msgServiceC1g2WriteFailedDataIsMissing();

	String msgServiceC1g2WriteFailedUserBankToSmall();

	String msgTemplateNameNotEmpty();

	String msgTemplateNameIsReserved(String name);

	String msgTemplateNoModifcation(String name);

	String msgTemplateNoDeletion(String name);

	String msgTemplateTemplateExists(String name);

	String msgRwValueOfOidInvalid(String value, String title);

	String rwWriteResultSuccess();

	String rwButtonRead();

	String rwButtonScan();

	String rwButtonWrite();

	String templatePropertyTemplate();

	String templatePropertyTemplateItemNew();

	String templatePropertyName();

	String templatePropertyNamePlaceholder();

	String templatePropertyBehavior();

	String templatePropertyBehaviorItemAdd();

	String templatePropertyBehaviorItemDrop();

	String templateButtonReset();

	String templateButtonSave();

	String settingsPropertyIdEncoding();

	String settingsPropertyCompation();

	String settingsPropertyTemplate();

	String settingsButtonReset();

	String settingsButtonApply();

	String selectSearchPlaceholder();

	String remoteButtonApply();

	String remotePropertyUrl();

	String resultResult();

	String rwResultError();

	String rwResultScanning();

	String rwResultAnalysing();
	
	String rwResultDecode();

	String rwResultLoading();

	String rwReadResultSuccess();
	
	String rwResultEncode();

	String rwResultValidate();

	String rwResultWriting();

	String rwResultReading();
	
	String rwScanResultSuccess();
	
	String settingsResultError();
	
	String settingsResultLoadingSettings();
	
	String templateResultLoadingTemplates();
	
	String templateResultSaved();
	
	String templateResultReset();
	
	String settingsResultReset();
	
	String templateResultError();
	
	String templateResultDeleted();
	
	String templateModifiable();
	
	String settingsResultSaved();
	
	String rwResultNoData();
	
	String scannerHeader();
	
}
