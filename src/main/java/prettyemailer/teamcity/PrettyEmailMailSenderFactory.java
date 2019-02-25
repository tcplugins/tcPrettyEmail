package prettyemailer.teamcity;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class PrettyEmailMailSenderFactory {
	
	public static JavaMailSender getJavaMailSender(PrettyEmailMainConfig myConfig) {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(myConfig.getSmtpHost());
		mailSender.setPort(myConfig.getSmtpPort());
		mailSender.setJavaMailProperties(myConfig.getMailProperties());
		
		if (myConfig.getSmtpUsername() != null){
			mailSender.setUsername(myConfig.getSmtpUsername());
		}
		if (myConfig.getSmtpPassword() != null){
			mailSender.setPassword(myConfig.getSmtpPassword());
		}
		return mailSender;
		
	}

}
