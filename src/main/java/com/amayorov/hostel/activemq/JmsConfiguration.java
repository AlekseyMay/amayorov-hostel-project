package com.amayorov.hostel.activemq;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;

import javax.jms.ConnectionFactory;

@Configuration
@EnableJms
public class JmsConfiguration {

	@Bean // ContainerFactory for queues
	public DefaultJmsListenerContainerFactory jmsQueueListenerContainerFactory(ConnectionFactory connectionFactory) {
		DefaultJmsListenerContainerFactory jmsListenerContainerFactory =
				new DefaultJmsListenerContainerFactory();
		jmsListenerContainerFactory.setConnectionFactory(connectionFactory);
		jmsListenerContainerFactory.setConcurrency("5-10");

		return jmsListenerContainerFactory;
	}

	@Bean  // ContainerFactory for topics
	public DefaultJmsListenerContainerFactory jmsTopicListenerContainerFactory(ConnectionFactory connectionFactory) {
		DefaultJmsListenerContainerFactory jmsListenerContainerFactory =
				new DefaultJmsListenerContainerFactory();
		jmsListenerContainerFactory.setConnectionFactory(connectionFactory);
		jmsListenerContainerFactory.setPubSubDomain(true);

		return jmsListenerContainerFactory;
	}

	@Bean(name = "JmsQueueTemplate") //JmsTemplate for queue
	public JmsTemplate jmsQueueTemplate(ConnectionFactory connectionFactory) {
		JmsTemplate jmsTemplate = new JmsTemplate();
		jmsTemplate.setConnectionFactory(connectionFactory);
		jmsTemplate.setDefaultDestinationName("queue");

		return jmsTemplate;
	}


	@Bean(name = "JmsTopicTemplate") //JmsTemplate for topics
	public JmsTemplate jmsTopicTemplate(ConnectionFactory connectionFactory) {
		JmsTemplate jmsTemplate = new JmsTemplate();
		jmsTemplate.setConnectionFactory(connectionFactory);
		jmsTemplate.setDefaultDestinationName("topic");
		jmsTemplate.setPubSubDomain(true);

		return jmsTemplate;
	}

	@Bean
	public MessageConverter messageConverter() {
		return new SimpleMessageConverter();
	}
}
