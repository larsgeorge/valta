package com.larsgeorge.valta.hbase.server;

import org.apache.hadoop.hbase.CoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.BaseRegionObserver;

import java.io.IOException;

/**
 * Coprocessor to track and manage resource usage server side.
 * <p/>
 * User: larsgeorge
 * Date: 6/12/13 12:58 PM
 */
public class ValtaCoprocessor extends BaseRegionObserver {

  @Override
  public void start(CoprocessorEnvironment e) throws IOException {
    super.start(e);
    // initialize handlers

  }
}
