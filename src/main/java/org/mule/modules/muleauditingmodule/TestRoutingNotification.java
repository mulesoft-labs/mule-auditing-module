package org.mule.modules.muleauditingmodule;


import org.mule.api.context.notification.RoutingNotificationListener;
import org.mule.context.notification.RoutingNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRoutingNotification implements RoutingNotificationListener<RoutingNotification> {
	
	private static final Logger logger = LoggerFactory.getLogger(TestRoutingNotification.class);
	
	@Override
	public void onNotification(RoutingNotification notification) {
		logger.error(notification.toString());
	}

}
