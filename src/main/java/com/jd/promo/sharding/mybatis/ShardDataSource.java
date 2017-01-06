package com.jd.promo.sharding.mybatis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/21
 * @Version: 1.0.0
 */
public class ShardDataSource implements DataSource {
    private static final Logger logger = LoggerFactory.getLogger(ShardDataSource.class);
    private Map<String, DataSource> dataSources = new HashMap<String, DataSource>();
    private String logicDatabaseName;

    @Override
    public Connection getConnection() throws SQLException {
        return new ShardConnectionImpl();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return new ShardConnectionImpl(username, password);
    }

    public ShardDataSource() {
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        DriverManager.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException(getClass() + " is not a wrapper.");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
    @Override
    public java.util.logging.Logger getParentLogger() {
        return java.util.logging.Logger.getLogger("global");
    }






    private class ShardConnectionImpl implements ShardConnection {
        private boolean prepared = false;
        private String username = null;
        private String password = null;
        private boolean autoCommit = true;
        private boolean closed = false;
        private Connection instance = null;
        private String targetDatabase = null;

        public ShardConnectionImpl() {

        }

        public ShardConnectionImpl(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public void prepare(ShardCondition condition) throws SQLException {
            String targetDatabaseTmp = logicDatabaseName + condition.getDatabaseSuffix();
            if (prepared) {
                if (!this.targetDatabase.equals(targetDatabaseTmp)) {
                    logger.error("ShardConnection Initialized,on one transaction open more condition");
                    throw new SQLException("ShardConnection Initialized");
                } else {
                    return;
                }
            }
            prepared = true;
            targetDatabase = targetDatabaseTmp;
            DataSource datasource = dataSources.get(targetDatabase);
            if (datasource == null) {
                throw new SQLException("ShardDataSource Unavailable");
            }
            if (username == null && password == null) {
                instance = datasource.getConnection();
            } else {
                instance = datasource.getConnection(username, password);
            }
            instance.setAutoCommit(autoCommit);
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized");
            }
            return instance.unwrap(iface);
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized");
            }
            return instance.isWrapperFor(iface);
        }

        @Override
        public Statement createStatement() throws SQLException {

            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized");
            }
            return instance.createStatement();
        }

        @Override
        public PreparedStatement prepareStatement(String sql) throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized");
            }
            return instance.prepareStatement(sql);
        }

        @Override
        public CallableStatement prepareCall(String sql) throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized");
            }
            return instance.prepareCall(sql);
        }

        @Override
        public String nativeSQL(String sql) throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized");
            }
            return instance.nativeSQL(sql);
        }

        @Override
        public void setAutoCommit(boolean autoCommit) throws SQLException {
            if (instance == null) {
                this.autoCommit = autoCommit;
            } else {
                instance.setAutoCommit(autoCommit);
            }
        }

        @Override
        public boolean getAutoCommit() throws SQLException {
            if (instance == null) {
                return autoCommit;
            } else {
                return instance.getAutoCommit();
            }
        }

        @Override
        public void commit() throws SQLException {
            if (instance != null) {
                instance.commit();
            }
        }

        @Override
        public void rollback() throws SQLException {
            if (instance != null) {
                instance.rollback();
            }
        }

        @Override
        public void close() throws SQLException {
            if (instance != null) {
                instance.close();
            } else {
                closed = true;
            }
        }

        @Override
        public boolean isClosed() throws SQLException {
            if (instance != null) {
                return instance.isClosed();
            } else {
                return closed;
            }
        }

        @Override
        public DatabaseMetaData getMetaData() throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when getMetaData");
            }
            return instance.getMetaData();
        }

        @Override
        public void setReadOnly(boolean readOnly) throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when setReadOnly");
            }
            instance.setReadOnly(readOnly);
        }

        @Override
        public boolean isReadOnly() throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when isReadOnly");
            }
            return instance.isReadOnly();
        }

        @Override
        public void setCatalog(String catalog) throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when setCatalog");
            }
            instance.setCatalog(catalog);
        }

        @Override
        public String getCatalog() throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when getCatalog");
            }
            return instance.getCatalog();
        }

        @Override
        public void setTransactionIsolation(int level) throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when setTransactionIsolation");
            }
            instance.setTransactionIsolation(level);
        }

        @Override
        public int getTransactionIsolation() throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when getTransactionIsolation");
            }
            return instance.getTransactionIsolation();
        }

        @Override
        public SQLWarning getWarnings() throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when getWarnings");
            }
            return instance.getWarnings();
        }

        @Override
        public void clearWarnings() throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when clearWarnings");
            }
            instance.clearWarnings();
        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when createStatement");
            }
            return instance.createStatement(resultSetType, resultSetConcurrency);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
                throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when prepareStatement");
            }
            return instance.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when prepareCall");
            }
            return instance.prepareCall(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public Map<String, Class<?>> getTypeMap() throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when getTypeMap");
            }
            return instance.getTypeMap();
        }

        @Override
        public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when setTypeMap");
            }
            instance.setTypeMap(map);
        }

        @Override
        public void setHoldability(int holdability) throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when setHoldability");
            }
            instance.setHoldability(holdability);
        }

        @Override
        public int getHoldability() throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when getHoldability");
            }
            return instance.getHoldability();
        }

        @Override
        public Savepoint setSavepoint() throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when setSavepoint");
            }
            return instance.setSavepoint();
        }

        @Override
        public Savepoint setSavepoint(String name) throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when setSavepoint");
            }
            return instance.setSavepoint(name);
        }

        @Override
        public void rollback(Savepoint savepoint) throws SQLException {
            if (instance != null) {
                instance.rollback(savepoint);
            }
        }

        @Override
        public void releaseSavepoint(Savepoint savepoint) throws SQLException {
            if (instance != null) {
                instance.releaseSavepoint(savepoint);
            }
        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
                throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when createStatement");
            }
            return instance.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
                                                  int resultSetHoldability) throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when prepareStatement");
            }
            return instance.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,int resultSetHoldability) throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when prepareCall");
            }
            return instance.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when prepareStatement");
            }
            return instance.prepareStatement(sql, autoGeneratedKeys);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when prepareStatement");
            }
            return instance.prepareStatement(sql, columnIndexes);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when prepareStatement");
            }
            return instance.prepareStatement(sql, columnNames);
        }

        @Override
        public Clob createClob() throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when createClob");
            }
            return instance.createClob();
        }

        @Override
        public Blob createBlob() throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when createBlob");
            }
            return instance.createBlob();
        }

        @Override
        public NClob createNClob() throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when createNClob");
            }
            return instance.createNClob();
        }

        @Override
        public SQLXML createSQLXML() throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when createSQLXML");
            }
            return instance.createSQLXML();
        }

        @Override
        public boolean isValid(int timeout) throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when isValid");
            }
            return instance.isValid(timeout);
        }

        @Override
        public void setClientInfo(String name, String value) throws SQLClientInfoException {
            if (instance == null) {
                throw new RuntimeException("ShardConnection Uninitialized when setClientInfo");
            }
            instance.setClientInfo(name, value);
        }

        @Override
        public void setClientInfo(Properties properties) throws SQLClientInfoException {
            if (instance == null) {
                throw new RuntimeException("ShardConnection Uninitialized when setClientInfo");
            }
            instance.setClientInfo(properties);
        }

        @Override
        public String getClientInfo(String name) throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when getClientInfo");
            }
            return instance.getClientInfo(name);
        }

        @Override
        public Properties getClientInfo() throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when getClientInfo");
            }
            return instance.getClientInfo();
        }

        @Override
        public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when createArrayOf");
            }
            return instance.createArrayOf(typeName, elements);
        }

        @Override
        public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when createStruct");
            }
            return instance.createStruct(typeName, attributes);
        }

        @Override
        public void setSchema(String schema) throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when setSchema");
            }
            instance.setSchema(schema);
        }

        @Override
        public String getSchema() throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when getSchema getSchema");
            }
            return instance.getSchema();
        }

        @Override
        public void abort(Executor executor) throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when abort");
            }
            instance.abort(executor);
        }

        @Override
        public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when setNetworkTimeout");
            }
            instance.setNetworkTimeout(executor, milliseconds);
        }

        @Override
        public int getNetworkTimeout() throws SQLException {
            if (instance == null) {
                throw new SQLException("ShardConnection Uninitialized when getNetworkTimeout");
            }
            return instance.getNetworkTimeout();
        }
    }

    public Map<String, DataSource> getDataSources() {
        return dataSources;
    }

    public void setDataSources(Map<String, DataSource> dataSources) {
        this.dataSources = dataSources;
    }

    public String getLogicDatabaseName() {
        return logicDatabaseName;
    }

    public void setLogicDatabaseName(String logicDatabaseName) {
        this.logicDatabaseName = logicDatabaseName;
    }
}
