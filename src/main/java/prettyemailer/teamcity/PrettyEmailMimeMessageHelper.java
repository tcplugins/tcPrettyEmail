package prettyemailer.teamcity;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import jetbrains.buildServer.serverSide.SRunningBuild;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;

public class PrettyEmailMimeMessageHelper extends MimeMessageHelper {
	private VelocityEngine velocityEngine;
	private PrettyEmailContentBuilder content;

	
	PrettyEmailMimeMessageHelper(MimeMessage message, VelocityEngine ve, PrettyEmailContentBuilder content) throws MessagingException{
		// Create message, and set the multipart flag, so that we can add attachments.
		super(message, true);
		this.velocityEngine = ve;
		this.content = content;
	}
	
	public void generateEmail(SRunningBuild sRunningBuild, 
			String reason, boolean addAttachments, String attachmentPath) 
		   throws ResourceNotFoundException, ParseErrorException, 
		   			MethodInvocationException, IOException, MessagingException, Exception
{
		
		VelocityContext context = new VelocityContext();
		context.put("tests", content.getTests());
		context.put("changes", content.getChanges());
		context.put("errors", content.getCompileErrors());
		context.put("info", content);
		
		Template emailBodyTemplate = velocityEngine.getTemplate("email-" + reason + ".vm");
		Template emailSubjectTemplate = velocityEngine.getTemplate("emailSubject-" + reason + ".vm");
		
		/* now render the template into a Writer */
		StringWriter emailBodyWriter = new StringWriter();
		StringWriter emailSubjectWriter = new StringWriter();
		emailBodyTemplate.merge(context, emailBodyWriter);
		emailSubjectTemplate.merge(context, emailSubjectWriter);
		
		this.setText(emailBodyWriter.toString(), true);
		this.setSubject(emailSubjectWriter.toString().trim());
		
		if (addAttachments){
			FileSystemResource buildStateResource = new FileSystemResource(new File(attachmentPath
					+ reason + ".gif"));
			this.addInline("buildState000", buildStateResource);
			
			if (content.getNewFailedTestCount() > 0){
				FileSystemResource newTestResource = new FileSystemResource(new File(attachmentPath
						+ "star.gif"));
				this.addInline("newTest000", newTestResource);
			}
		}
	}
	
}
