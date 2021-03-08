package havis.custom.harting.iso159612.hw.impl;

import havis.application.component.db.Database;
import havis.custom.harting.iso159612.utils.Configuration;

/**
 * Instance of this class is a singleton and provides instances of three
 * databases - asset database, order database and configuration database.
 */
public class DBAccess {

	private static DBAccess dbAccess = new DBAccess();
	private Database configDatabase;

	private DBAccess() {
		configDatabase = new Database(Configuration.APP_CONF.configDatabaseName(), Configuration.APP_CONF.configDatabaseVersion(),
				Configuration.APP_CONF.configDatabaseDesc(), new Long(1024 * 1024));
	}

	/**
	 * @return Configuration database instance
	 */
	public static Database getConfigDatabaseInstance() {
		return dbAccess.configDatabase;
	}
}