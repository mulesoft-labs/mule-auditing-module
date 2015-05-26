/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

package org.mule.modules.muleauditingmodule;

import java.util.HashMap;

import javax.inject.Inject;

import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Module;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.lifecycle.Start;
import org.mule.api.annotations.param.Optional;
import org.mule.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Anypoint Connector
 *
 * @author MuleSoft, Inc.
 */
@Module(name="mule-auditing-module", friendlyName="MuleAuditingModule")
public class MuleAuditingModule {

	private static final Logger logger = LoggerFactory.getLogger(MuleAuditingModule.class);
	
	@Inject
	private MuleContext muleContext;
	
	@Configurable
	private String dispatchEndpoint;
	
	@Configurable
	@Optional
	private String transactionIdExpression;
	
	private CustomEventNotifier customNotifier;
	
	@Start
	public void startModule() {
		logger.debug("Registering audit hooks...");
		
		customNotifier = new CustomEventNotifier(muleContext, dispatchEndpoint, transactionIdExpression);
		
		
		//register notification listeners.
		ExceptionEventNotifier exceptionNotifier = new ExceptionEventNotifier(muleContext, dispatchEndpoint, transactionIdExpression);
		EndpointEventNotifier endpointEventNotifier = new EndpointEventNotifier(muleContext, dispatchEndpoint, transactionIdExpression); 
		try {
			logger.debug("Registering endpoint notification listener...");
			muleContext.registerListener(endpointEventNotifier);
			logger.debug("Registerign exception notification listener...");
			muleContext.registerListener(exceptionNotifier);
		} catch (Exception ex) {
			logger.error("Could not register notification listeners...", ex);
		}
	}
	
	
	@Processor
	@Inject
	public void audit(MuleEvent event, HashMap<String, String> values) {
		customNotifier.send(event, values);
	}


	//Getters and Setters
	public MuleContext getMuleContext() {
		return muleContext;
	}


	public void setMuleContext(MuleContext muleContext) {
		this.muleContext = muleContext;
	}


	public String getDispatchEndpoint() {
		return dispatchEndpoint;
	}


	public void setDispatchEndpoint(String dispatchEndpoint) {
		this.dispatchEndpoint = dispatchEndpoint;
	}


	public String getTransactionIdExpression() {
		return transactionIdExpression;
	}


	public void setTransactionIdExpression(String transactionIdExpression) {
		this.transactionIdExpression = transactionIdExpression;
	}
	
	
	
}