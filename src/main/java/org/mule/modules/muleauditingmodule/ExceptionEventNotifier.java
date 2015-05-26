package org.mule.modules.muleauditingmodule;

import java.util.HashMap;

import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.context.notification.ExceptionStrategyNotificationListener;
import org.mule.context.notification.ExceptionStrategyNotification;

public class ExceptionEventNotifier extends AbstractEventNotifier implements ExceptionStrategyNotificationListener<ExceptionStrategyNotification> {
	
	public static final String EXCEPTION_TYPE = "EXCEPTION";

	public ExceptionEventNotifier(MuleContext context, String endpointUri, String transactionIdExpression) {
		super(context, endpointUri, transactionIdExpression);
	}

	@Override
	public void onNotification(ExceptionStrategyNotification notification) {
		
		HashMap<String, Object> auditEvent = null;
		
		if (notification.getSource() instanceof MuleEvent) {
			MuleEvent evt = (MuleEvent) notification.getSource();
			auditEvent = super.generateDefaultAuditEvent(evt.getMessage(), EXCEPTION_TYPE, evt.getFlowConstruct().getName());
		} else if (notification.getSource() instanceof MuleMessage) {
			auditEvent = super.generateDefaultAuditEvent((MuleMessage)notification.getSource(), EXCEPTION_TYPE, "unknown");
		}
		
		
		
		if (auditEvent == null) {
			throw new IllegalStateException("Cannot audit current exception");
		}
		
		super.sendAuditEvent(auditEvent);
	}

}
