package org.mule.modules.muleauditingmodule;

import java.util.HashMap;

import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;

public class CustomEventNotifier extends AbstractEventNotifier {
	
	public static final String EVENT_TYPE = "CUSTOM";

	public CustomEventNotifier(MuleContext context, String endpointUri, String transactionIdExpression) {
		super(context, endpointUri, transactionIdExpression);
	}
	
	public void send(MuleEvent event, HashMap<String, String> customData) {
		HashMap<String, Object> auditEvent = super.generateDefaultAuditEvent(event.getMessage(), EVENT_TYPE, event.getFlowConstruct().getName()); 
		auditEvent.put("CustomData", customData);
		super.sendAuditEvent(auditEvent);
	}
	
}
