package prettyemailer.teamcity;

import static org.junit.Assert.*;

import org.apache.velocity.app.VelocityEngine;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class PrettyEmailMimeMessageHelperTest {

	@Test
	public void testGenerateEmail() {
		
		VelocityEngine velocityEngine = new VelocityEngine();
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setDefaultEncoding("UTF-8");
		
//		PrettyEmailContentBuilder content = new PrettyEmailContentBuilder(sRunningBuild, server, 5, 5));
//
//		
//		PrettyEmailMimeMessageHelper helper = new PrettyEmailMimeMessageHelper(
//				mailSender.createMimeMessage(), 
//				velocityEngine,
//				content);
	}

}
