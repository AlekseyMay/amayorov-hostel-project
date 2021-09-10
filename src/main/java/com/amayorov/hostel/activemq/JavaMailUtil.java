package com.amayorov.hostel.activemq;

import com.amayorov.hostel.domain.dto.security.UserDTO;
import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Slf4j
public class JavaMailUtil {

	public static void sendMail(String recipients, UserDTO userDTO) throws MessagingException {
		log.info("Preparing to send email.");
		Properties properties = new Properties();

		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");

		/*
		for this to work you need to go to https://myaccount.google.com/security and turn on the access of all apps to your account
		 */
		String myAccountEmail = "amayorovhostel@gmail.com"; //test gmail address, you can log in it to check if email notifications are working or just change the receivers in userListener() method
		String password = "Hostel111";

		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(myAccountEmail, password);
			}
		});

		Message message = prepareMessage(session, myAccountEmail, recipients, userDTO);

		Transport.send(message);

		log.info("Email message sent successfully!");
	}

	private static Message prepareMessage(Session session, String myAccountEmail, String recipients, UserDTO userDTO) throws MessagingException {
		String[] recipientList = recipients.split(",");
		InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
		int counter = 0;
		for (String recipient : recipientList) {
			recipientAddress[counter] = new InternetAddress(recipient.trim());
			counter++;
		}

		Message message = new MimeMessage(session);


		message.setFrom(new InternetAddress(myAccountEmail));
		message.setRecipients(Message.RecipientType.TO, recipientAddress);
		message.setSubject("New Admin user!");
		message.setText("Good afternoon! \n\n\nNew ADMIN with name: " + userDTO.getUsername() +
				" was added to system. \n\nThis is informational message. \n\n\nBest regards, \nHostelApp");
		return message;
	}

	// private constructor to hide the implicit public one
	private JavaMailUtil() {
		throw new IllegalStateException("Utility Class");
	}
}
