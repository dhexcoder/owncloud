/**
 * File: LoginTest.java 27.05.2014, 12:44:50, author: mreinhardt
 * 
 * Project: OwnCloud
 *
 * https://www.martinreinhardt-online.de/apps
 */
package de.martinreinhardt.owncloud.webtest.tests;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import net.thucydides.core.annotations.Steps;
import net.thucydides.core.reports.adaptors.xunit.model.TestError;

import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.user.UserException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

import de.martinreinhardt.owncloud.webtest.steps.LoggedInUserSteps;
import de.martinreinhardt.owncloud.webtest.steps.RoundCubeSteps;
import de.martinreinhardt.owncloud.webtest.util.AbstractUITest;
import de.martinreinhardt.owncloud.webtest.util.EmailUserDetails;

/**
 * @author mreinhardt
 * 
 */
public abstract class RoundCubeMockedMailIT extends AbstractUITest {

	/**
	 * 
	 */
	protected static final String TEST_MAIL_SUBJECT = "RoundCube App";

	@Steps
	protected LoggedInUserSteps loggedInuserSteps;

	@Steps
	protected RoundCubeSteps appSteps;

	public void runEmailTest() throws AddressException, MessagingException,
			UserException, TestError {

		GreenMail server = null;
		try {
			server = new GreenMail(ServerSetupTest.ALL);
			server.start();

			EmailUserDetails userDtls = getEmailUserDetailsTest();

			final MimeMessage msg = new MimeMessage((Session) null);
			msg.setSubject(TEST_MAIL_SUBJECT);
			// msg.setFrom("from@sender.com");
			msg.setText("Some text here ...");
			msg.setRecipient(RecipientType.TO,
					new InternetAddress(userDtls.getEmail()));
			Properties props = new Properties();
			props.put("mail.store.protocol", "imap");
			props.put("mail.host", "localhost");
			props.put("mail.imap.port", "3143");

			GreenMailUser user = server.setUser(userDtls.getEmail(),
					userDtls.getUsername(), userDtls.getPassword());
			user.deliver(msg);
			assertEquals(1, server.getReceivedMessages().length);
			executeTestStepsFrontend();
		} finally {
			if (server != null) {
				server.stop();
			}
		}
	}

	public abstract EmailUserDetails getEmailUserDetailsTest();

	public abstract void executeTestStepsFrontend() throws TestError;
}