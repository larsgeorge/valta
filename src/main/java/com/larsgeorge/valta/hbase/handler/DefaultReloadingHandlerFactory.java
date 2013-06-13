package com.larsgeorge.valta.hbase.handler;

import com.larsgeorge.valta.hbase.handler.accounting.AccountingHandler;
import com.larsgeorge.valta.hbase.handler.accounting.DefaultAccountingHandler;
import com.larsgeorge.valta.hbase.handler.authentication.AuthenticationHandler;
import com.larsgeorge.valta.hbase.handler.authentication.DefaultAuthenticationHandler;
import com.larsgeorge.valta.hbase.handler.authorizaton.AuthorizationHandler;
import com.larsgeorge.valta.hbase.handler.authorizaton.DefaultAuthorizationHandler;

/**
 * Implements the default handler factory.
 *
 * User: larsgeorge
 * Date: 3/18/12 12:42 PM
 */
public class DefaultReloadingHandlerFactory extends HandlerFactory {

  @Override
  public AuthenticationHandler getAuthenticationHandler() {
    return new DefaultAuthenticationHandler(_conf);
  }

  @Override
  public AuthorizationHandler getAuthorizationHandler() {
    return new DefaultAuthorizationHandler(_conf);
  }

  @Override
  public AccountingHandler getAccountingHandler() {
    return new DefaultAccountingHandler(_conf);
  }
}
