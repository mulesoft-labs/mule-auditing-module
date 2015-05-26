package org.mule.modules.muleauditingmodule;

import java.util.Calendar;
import java.util.HashMap;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractEventNotifier {
	
	public static final String APP_NAME = "AppName";

	public static final String FLOW_NAME = "FlowName";

	public static final String EVENT_TYPE = "Type";

	public static final String TRANSACTION_ID = "TransactionId";
	
	public static final String EVENT_DATE = "EventDate";
	
	public static final String COTNEXT_INFORMATION = "ContextInformation";
	
	protected static final Logger logger = LoggerFactory.getLogger(AbstractEventNotifier.class);
	
	protected final MuleContext context;
	private final String endpointUri;
	private final String transactionIdExpression;
	
	public AbstractEventNotifier(MuleContext context, String endpointUri, String transactionIdExpression) {
		this.context = context;
		this.endpointUri = endpointUri;
		this.transactionIdExpression = transactionIdExpression;
	}
	
	public void sendAuditEvent(HashMap<String, Object> payload) {
		try {
			MuleMessage msg = new DefaultMuleMessage(payload, context);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Sending audit event to: " + endpointUri);
				logger.debug("Audit event is: ");
				logger.debug(msg.toString());
			}
			
			context.getClient().dispatch(endpointUri, msg);;
		} catch (Exception ex) {
			logger.error("Error while sending audit event");
		}
	}
	
	HashMap<String, Object> generateDefaultAuditEvent(MuleMessage message, String eventType, String flowName) {
		HashMap<String, Object> ret = new HashMap<>();
		
		String transactionId = generateTransactionId(message);
		
		ret.put(TRANSACTION_ID, transactionId);
		ret.put(EVENT_TYPE, eventType);
		ret.put(EVENT_DATE, Calendar.getInstance().getTime());
		ret.put(FLOW_NAME, flowName);
		ret.put(APP_NAME, context.getConfiguration().getId());
		
		if (logger.isDebugEnabled()) {
			logger.debug("Generated default Audit Event:");
			logger.debug(ret.toString());
		}
		
		return ret;
	}

	private String generateTransactionId(MuleMessage message) {
		
		if (transactionIdExpression == null) {
			return message.getUniqueId();
		}
		
		Object result = context.getExpressionManager().evaluate(transactionIdExpression, null, message, true);
		
		if (result == null) {
			throw new IllegalStateException("Transaction ID expression returned null!");
		}
		
		if (result instanceof String) {
			return (String) result;
		}
		
		logger.warn("Transaction ID expression did not return a String, calling .toString() over it.");
		
		return result.toString();
	}
	
}
