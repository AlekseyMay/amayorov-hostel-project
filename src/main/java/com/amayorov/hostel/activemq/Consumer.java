package com.amayorov.hostel.activemq;

import com.amayorov.hostel.domain.dto.security.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;

@Component
@Slf4j
@RequiredArgsConstructor
public class Consumer {

	@Qualifier("JmsTopicTemplate")
	private final JmsTemplate jmsTopicTemplate;

	@JmsListener(destination = "queue", containerFactory = "jmsQueueListenerContainerFactory")
    @SendTo("answer")
	public String userListener(UserDTO userDTO) throws MessagingException {
		log.info("User from queue received, username: {}", userDTO.getUsername());
		// you can add several emails here, example: "node1@gmail.com, node2@gmail.com, etc" but pls check SPAM folder sometimes it can be there
		String recipientList = "amayorovhostel@gmail.com" ; // here can be the list of all the emails that are needed, divided with comma or space+comma, comma is necessary!
		JavaMailUtil.sendMail(recipientList, userDTO);
		jmsTopicTemplate.convertAndSend("topic", userDTO);
		return "Email was sent to emails: " + recipientList +
				" and topic created for User with name: " + userDTO.getUsername();
	}

    // an answer for queue
	@JmsListener(destination = "answer", containerFactory = "jmsQueueListenerContainerFactory")
	public void userListener2(String message) {
		log.info("The answer for User queue: {}", message); }

	// imaginary node №1 not connected with this app, that is subscribed to topic
	@JmsListener(destination = "topic", containerFactory = "jmsTopicListenerContainerFactory")
	public synchronized void userTopicListener(UserDTO userDTO) {
		log.info("Imaginary node #1: Email considering information about new User with name: {} was sent to you. Check email.", userDTO.getUsername());
		log.info("Imaginary node #1: Email considering information about new User with name: {} was sent to you. Check email.", userDTO.getUsername());
		log.info("Imaginary node #1: Email considering information about new User with name: {} was sent to you. Check email.", userDTO.getUsername());
	}

	// imaginary node №2 not connected with this app. that is subscribed to topic
	@JmsListener(destination = "topic", containerFactory = "jmsTopicListenerContainerFactory")
	public synchronized void userTopicListener2(UserDTO userDTO) {
		log.info("Imaginary node #2: Email considering information about new User with name: {} was sent to you. Check email.", userDTO.getUsername());
		log.info("Imaginary node #2: Email considering information about new User with name: {} was sent to you. Check email.", userDTO.getUsername());
		log.info("Imaginary node #2: Email considering information about new User with name: {} was sent to you. Check email.", userDTO.getUsername());
	}
}
