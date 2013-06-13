package com.larsgeorge.valta.hbase.client;

import com.larsgeorge.valta.hbase.handler.HandlerFactory;
import com.larsgeorge.valta.hbase.handler.accounting.AccountingHandler;
import com.larsgeorge.valta.hbase.handler.authentication.AuthenticationHandler;
import com.larsgeorge.valta.hbase.handler.authorizaton.AuthorizationHandler;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.HServerAddress;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Append;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Row;
import org.apache.hadoop.hbase.client.RowLock;
import org.apache.hadoop.hbase.client.RowMutations;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.coprocessor.Batch;
import org.apache.hadoop.hbase.ipc.CoprocessorProtocol;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Implement a HTable version that handles all accounting, authentication, and authorization.
 *
 * User: larsgeorge
 * Date: 3/18/12 11:54 AM
 */
public class ValtaTable implements HTableInterface {

  private HTable _table = null;
  private HandlerFactory _factory = null;
  private AuthenticationHandler _authenticationHandler = null;
  private AuthorizationHandler _authorizationHandler = null;
  private AccountingHandler _accountingHandler = null;

  class Scanner implements ResultScanner {
    @Override
    public Result next() throws IOException {
      return null;
    }

    @Override
    public Result[] next(int nbRows) throws IOException {
      return new Result[0];
    }

    @Override
    public void close() {
    }

    @Override
    public Iterator<Result> iterator() {
      return null;
    }
  }

  // Constructors

  public ValtaTable(final String tableName) throws IOException {
    this(HBaseConfiguration.create(), Bytes.toBytes(tableName));
    initHandlers();
  }

  public ValtaTable(final byte[] tableName)
  throws IOException {
    this(HBaseConfiguration.create(), tableName);
    initHandlers();
  }

  public ValtaTable(Configuration conf, final String tableName)
  throws IOException {
    this(conf, Bytes.toBytes(tableName));
    initHandlers();
  }

  public ValtaTable(Configuration conf, final byte[] tableName)
  throws IOException {
    _table = new HTable(conf, tableName);
    initHandlers();
  }

  public ValtaTable(HTable table) {
    _table = table;
    initHandlers();
  }

  // Helpers
  private void initHandlers() {
    _factory = HandlerFactory.GetHandlerFactory(_table.getConfiguration());
    _authenticationHandler = _factory.getAuthenticationHandler();
    _authorizationHandler = _factory.getAuthorizationHandler();
    _accountingHandler = _factory.getAccountingHandler();
  }

  // Inherited

  @Override
  public byte[] getTableName() {
    return _table.getTableName();
  }

  @Override
  public Configuration getConfiguration() {
    return _table.getConfiguration();
  }

  @Override
  public HTableDescriptor getTableDescriptor() throws IOException {
    return _table.getTableDescriptor();
  }

  @Override
  public boolean exists(Get get) throws IOException {
    return _table.exists(get);
  }

  @Override
  public void batch(List<? extends Row> rows,
    Object[] objects) throws IOException, InterruptedException {
    _table.batch(rows, objects);
  }

  @Override
  public Object[] batch(List<? extends Row> rows) throws IOException, InterruptedException {
    return _table.batch(rows);
  }

  @Override
  public Result get(Get get) throws IOException {
    return _table.get(get);
  }

  @Override
  public Result[] get(List<Get> gets) throws IOException {
    return _table.get(gets);
  }

  @Override
  public Result getRowOrBefore(byte[] row, byte[] family) throws IOException {
    return _table.getRowOrBefore(row, family);
  }

  @Override
  public ResultScanner getScanner(Scan scan) throws IOException {
    return _table.getScanner(scan);
  }

  @Override
  public ResultScanner getScanner(byte[] family) throws IOException {
    return _table.getScanner(family);
  }

  @Override
  public ResultScanner getScanner(byte[] family, byte[] qualifier) throws IOException {
    return _table.getScanner(family, qualifier);
  }

  @Override
  public void put(Put put) throws IOException {
    _table.put(put);
  }

  @Override
  public void put(List<Put> puts) throws IOException {
    _table.put(puts);
  }

  @Override
  public boolean checkAndPut(final byte [] row,
      final byte [] family, final byte [] qualifier, final byte [] value,
      final Put put) throws IOException {
    return _table.checkAndPut(row, family, qualifier, value, put);
  }

  @Override
  public void delete(Delete delete) throws IOException {
    _table.delete(delete);
  }

  @Override
  public void delete(List<Delete> deletes) throws IOException {
    _table.delete(deletes);
  }

  @Override
  public boolean checkAndDelete(final byte [] row,
      final byte [] family, final byte [] qualifier, final byte [] value,
      final Delete delete) throws IOException {
    return _table.checkAndDelete(row, family, qualifier, value, delete);
  }

  @Override
  public void mutateRow(RowMutations rowMutations) throws IOException {
    _table.mutateRow(rowMutations);
  }

  @Override
  public Result append(Append append) throws IOException {
    return _table.append(append);
  }

  @Override
  public Result increment(Increment increment) throws IOException {
    return _table.increment(increment);
  }

  @Override
  public long incrementColumnValue(final byte [] row, final byte [] family,
      final byte [] qualifier, final long amount) throws IOException {
    return _table.incrementColumnValue(row, family, qualifier, amount);
  }

  @Override
  public long incrementColumnValue(final byte [] row, final byte [] family,
      final byte [] qualifier, final long amount, final boolean writeToWAL) throws IOException {
    return _table.incrementColumnValue(row, family, qualifier, amount, writeToWAL);
  }

  @Override
  public boolean isAutoFlush() {
    return _table.isAutoFlush();
  }

  @Override
  public void flushCommits() throws IOException {
    _table.flushCommits();
  }

  @Override
  public void close() throws IOException {
    _table.close();
  }

  @Override
  public RowLock lockRow(byte[] row) throws IOException {
    return _table.lockRow(row);
  }

  @Override
  public void unlockRow(RowLock rowLock) throws IOException {
    _table.unlockRow(rowLock);
  }

  @Override
  public <T extends CoprocessorProtocol> T coprocessorProxy(Class<T> tClass, byte[] bytes) {
    return null;
  }

  @Override
  public <T extends CoprocessorProtocol, R> Map<byte[], R> coprocessorExec(Class<T> tClass,
    byte[] bytes, byte[] bytes2, Batch.Call<T, R> trCall) throws IOException, Throwable {
    return null;
  }

  @Override
  public <T extends CoprocessorProtocol, R> void coprocessorExec(Class<T> tClass, byte[] bytes,
    byte[] bytes2, Batch.Call<T, R> trCall,
    Batch.Callback<R> rCallback) throws IOException, Throwable {
  }

  // Overriden

  public ArrayList<Put> getWriteBuffer() {
    return _table.getWriteBuffer();
  }

  public HRegionLocation getRegionLocation(String row) throws IOException {
    return _table.getRegionLocation(row);
  }

  public HRegionLocation getRegionLocation(byte[] row) throws IOException {
    return _table.getRegionLocation(row);
  }

  public HConnection getConnection() {
    return _table.getConnection();
  }

  public int getScannerCaching() {
    return _table.getScannerCaching();
  }

  public void setScannerCaching(int scannerCaching) {
    _table.setScannerCaching(scannerCaching);
  }

  public byte[][] getStartKeys() throws IOException {
    return _table.getStartKeys();
  }

  public byte[][] getEndKeys() throws IOException {
    return _table.getEndKeys();
  }

  public Pair<byte[][], byte[][]> getStartEndKeys() throws IOException {
    return _table.getStartEndKeys();
  }

  public Map<HRegionInfo, HServerAddress> getRegionsInfo() throws IOException {
    return _table.getRegionsInfo();
  }

  public void prewarmRegionCache(Map<HRegionInfo, HServerAddress> regionMap) {
    _table.prewarmRegionCache(regionMap);
  }

  public void serializeRegionInfo(DataOutput out) throws IOException {
    _table.serializeRegionInfo(out);
  }

  public Map<HRegionInfo, HServerAddress> deserializeRegionInfo(DataInput in) throws IOException {
    return _table.deserializeRegionInfo(in);
  }

  public void clearRegionCache() {
    _table.clearRegionCache();
  }

  public void setAutoFlush(boolean autoFlush) {
    _table.setAutoFlush(autoFlush);
  }

  public void setAutoFlush(boolean autoFlush, boolean clearBufferOnFail) {
    _table.setAutoFlush(autoFlush, clearBufferOnFail);
  }

  public long getWriteBufferSize() {
    return _table.getWriteBufferSize();
  }

  public void setWriteBufferSize(long writeBufferSize) throws IOException {
    _table.setWriteBufferSize(writeBufferSize);
  }
}
