package org.mule.modules.muleauditingmodule;

import java.util.HashMap;

import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.context.notification.EndpointMessageNotificationListener;
import org.mule.context.notification.EndpointMessageNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndpointEventNotifier extends AbstractEventNotifier implements EndpointMessageNotificationListener<EndpointMessageNotification> {
	
	private static final Logger logger = LoggerFactory.getLogger(EndpointEventNotifier.class);
	
	public EndpointEventNotifier(MuleContext context, String endpointUri, String transactionIdExpression) {
		super(context, endpointUri, transactionIdExpression);
	}

	@Override
	public void onNotification(EndpointMessageNotification notification) {
		
		MuleMessage msg = (MuleMessage) notification.getSource();
	    try {
	    	HashMap<String, Object> auditEvent =  super.generateDefaultAuditEvent(msg, notification.getActionName().toUpperCase(), notification.getFlowConstruct().getName());
	    	auditEvent.put(CONTEXT_INFORMATION, notification.getEndpoint());
	    	if(!notification.getEndpoint().equals(endpointUri)){
	    		super.sendAuditEvent(auditEvent);
	    	}
	    	else {
	    		logger.debug("Ignoring same uri endpoint");
	    	}
	    } catch (Throwable t) {
	    	logger.error("Got exception on event notifier", t);
	    }
	}

}
