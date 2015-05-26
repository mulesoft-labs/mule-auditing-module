package org.mule.modules.auditing;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.api.transport.PropertyScope;
import org.mule.modules.muleauditingmodule.AbstractEventNotifier;
import org.mule.tck.junit4.FunctionalTestCase;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("unchecked")
public class AuditingModuleTest extends FunctionalTestCase {

	@Override
	protected String getConfigFile() {
		return "mule-auditing-module-config.xml";
	}
	
	@Test
	public void testAudit() throws Exception {
		
		MuleClient client = muleContext.getClient();
		
		MuleEvent evt = super.getTestEvent("Test object");
		
		runFlow("testCustomEvent", evt);
		
		MuleMessage msg = client.request("vm://test-endpoint", 1000);
		
		assertNotNull("should have received a message", msg);
		assertTrue("payload should be a map", msg.getPayload() instanceof HashMap);
		assertThat("event should be of custom type", (Map<? extends String, ? extends String>) msg.getPayload(), hasEntry(AbstractEventNotifier.EVENT_TYPE, "CUSTOM"));
		assertThat("event should have a date", (Map<? extends String, ?>) msg.getPayload(), hasKey(AbstractEventNotifier.EVENT_DATE));
	}
	
	@Test
	public void testAuditCustomId() throws Exception {
		
		MuleClient client = muleContext.getClient();
		
		MuleEvent evt = super.getTestEvent("Test object");
		
		evt.getMessage().setProperty("transactionId", "1234", PropertyScope.INBOUND);
		
		runFlow("testCustomTransactionId", evt);
		
		MuleMessage msg = client.request("vm://test-endpoint-tid", 1000);
		
		assertNotNull("should have received a message", msg);
		assertTrue("payload should be a map", msg.getPayload() instanceof HashMap);
		assertThat("transactionId should be 1234", (Map<? extends String, ? extends String>) msg.getPayload(), hasEntry(AbstractEventNotifier.TRANSACTION_ID, "1234"));
	}
	
	@Test
	public void testAuditException() throws Exception {
		
		MuleEvent evt = super.getTestEvent("Test object");
		
		MuleClient client = muleContext.getClient();
		
		runFlow("testExceptionFlow", evt);
		
		MuleMessage msg = client.request("vm://test-endpoint", 1000);
		
		assertNotNull("should have received a message", msg);
		assertTrue("payload should be a map", msg.getPayload() instanceof HashMap);
		assertThat("event should be of exception type", (Map<? extends String, ? extends String>) msg.getPayload(), hasEntry(AbstractEventNotifier.EVENT_TYPE, "EXCEPTION"));
		
	}
	
	@Test @Ignore
	public void testAuditInboundEndpointNotification() throws Exception {

		MuleClient client = muleContext.getClient();
		client.send("http://localhost:9099/test", super.getTestMuleMessage());
		
		MuleMessage msg = client.request("vm://test-endpoint", 1000);
		assertNotNull("should have received a message", msg);
		
	}
	
}
