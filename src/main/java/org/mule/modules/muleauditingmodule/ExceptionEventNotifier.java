package org.mule.modules.muleauditingmodule;

import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.mule.api.MessagingException;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.context.notification.ExceptionNotificationListener;
import org.mule.context.notification.ExceptionNotification;
import org.mule.util.ExceptionUtils;

public class ExceptionEventNotifier extends AbstractEventNotifier implements ExceptionNotificationListener<ExceptionNotification> {
	
	public static final String EXCEPTION_TYPE = "EXCEPTION";
	
	public static final String EXCEPTION_MESSAGE = "exceptionMessage";
	public static final String EXCEPTION_STACKTRACE = "exceptionStacktrace";
	public static final String ROOT_EXCEPTION_MESSAGE = "rootExceptionMessage";
	public static final String ROOT_EXCEPTION_STACKTRACE = "rootExceptionStacktrace";
	
	public static final String EXCEPTIONS_KEY = "exceptions";
	
	public ExceptionEventNotifier(MuleContext context, String endpointUri, String transactionIdExpression) {
		super(context, endpointUri, transactionIdExpression);
	}

	@Override
	public void onNotification(ExceptionNotification notification) {
		
		HashMap<String, Object> auditEvent = null;
		if (notification.getSource() instanceof MessagingException) {
			MessagingException mex = (MessagingException) notification.getSource();
			auditEvent = super.generateDefaultAuditEvent(mex.getEvent().getMessage(), EXCEPTION_TYPE, mex.getEvent().getFlowConstruct().getName());
	    } else if (notification.getSource() instanceof MuleEvent) {
			MuleEvent evt = (MuleEvent) notification.getSource();
			auditEvent = super.generateDefaultAuditEvent(evt.getMessage(), EXCEPTION_TYPE, evt.getFlowConstruct().getName());
		} else if (notification.getSource() instanceof MuleMessage) {
			auditEvent = super.generateDefaultAuditEvent((MuleMessage)notification.getSource(), EXCEPTION_TYPE, "unknown");
		}
		
		
		if (auditEvent == null) {
			throw new IllegalStateException("Cannot audit current exception");
		}
		
		addExceptionAuditing(auditEvent, notification);
		
		super.sendAuditEvent(auditEvent);
	}

	private void addExceptionAuditing(HashMap<String, Object> event, ExceptionNotification notification) {
		
		HashMap<String, String> exceptionObject = createExceptionObject(notification.getException());
		
		//at the moment only one exception will be put but a list will be used for future improvements.
		event.put(EXCEPTIONS_KEY, Arrays.asList(exceptionObject));
	}
	
	private HashMap<String, String> createExceptionObject(Throwable exception) {
		
		HashMap<String, String> ret = new HashMap<String, String>();
		
		ret.put(EXCEPTION_MESSAGE, ExceptionUtils.getMessage(exception));
		ret.put(EXCEPTION_STACKTRACE, ExceptionUtils.getStackTrace(exception));
		ret.put(ROOT_EXCEPTION_MESSAGE, ExceptionUtils.getRootCauseMessage(exception));
		ret.put(ROOT_EXCEPTION_STACKTRACE, StringUtils.join(ExceptionUtils.getRootCauseStackTrace(exception), "\n"));
		
		return ret;
	}

}
