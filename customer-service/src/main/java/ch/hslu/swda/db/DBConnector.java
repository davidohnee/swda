package ch.hslu.swda.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBConnector {

    private static final Logger LOG = LoggerFactory.getLogger(DBConnector.class);

    public DBConnector() {

    }

    public final void connect() {
        LOG.debug("It's just a dummy bro (db.connect() called)");
    }

    public final void close() {
        LOG.debug("It's just a dummy bro (db.close() called)");
    }
}
