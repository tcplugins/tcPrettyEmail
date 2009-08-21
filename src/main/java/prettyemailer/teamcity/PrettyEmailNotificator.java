package prettyemailer.teamcity;

import java.io.IOException;
import java.util.Set;

import javax.mail.MessagingException;

import jetbrains.buildServer.Build;
import jetbrains.buildServer.Used;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.notification.Notificator;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PluginDescriptor;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class PrettyEmailNotificator implements Notificator {

	private static final String TYPE = "prettyEmailNotifier";
	private static final String TYPE_NAME = "Pretty Email Notifier";
	private static SBuildServer server;
	private static PluginDescriptor myPluginDescriptor;
	private static VelocityEngine velocityEngine;

	private static JavaMailSenderImpl mailSender;
	private static NotificatorRegistry notificatorRegistry;
	private static PrettyEmailMainSettings mySettings;
	
	private static String templatePath;
	private static String attachmentPath;


	public PrettyEmailNotificator(NotificatorRegistry notificatorRegistry,
			SBuildServer server, PluginDescriptor pluginDescriptor,
			PrettyEmailMainSettings settings)
			throws IOException {
		
		PrettyEmailNotificator.server = server;
		PrettyEmailNotificator.mySettings = settings;
		PrettyEmailNotificator.myPluginDescriptor = pluginDescriptor;
		PrettyEmailNotificator.notificatorRegistry = notificatorRegistry;
		PrettyEmailNotificator.velocityEngine = new VelocityEngine();
		PrettyEmailNotificator.mailSender = new JavaMailSenderImpl();
		
	}

	public void register() {
		if (mySettings.getConfig().getTemplatePath() != null){
			PrettyEmailNotificator.templatePath = mySettings.getConfig().getTemplatePath(); 
		} else {
			PrettyEmailNotificator.templatePath = server.getServerRootPath()+ myPluginDescriptor.getPluginResourcesPath() + "templates/";
		}

		if (mySettings.getConfig().getAttachmentPath() != null){
			PrettyEmailNotificator.attachmentPath 
					= mySettings.getConfig().getAttachmentPath(); 
		} else {
			PrettyEmailNotificator.attachmentPath 
					= server.getServerRootPath()+ myPluginDescriptor.getPluginResourcesPath() + "img/";
		}
		
		velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
				PrettyEmailNotificator.templatePath);

		// FIXME Turn caching on for deployment.
		velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE , false);

		
		try {
			velocityEngine.init();
			notificatorRegistry.register(this);
			Loggers.SERVER.info(this.getClass().getSimpleName() + " :: Registering");
		} catch (Exception e) {
			Loggers.SERVER.error(this.getClass().getSimpleName() + " :: " + PrettyEmailNotificator.TYPE + " was NOT successfully registered. See DEBUG for Stacktrace");
			Loggers.SERVER.debug(e);
			e.printStackTrace();
		}
		Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: file.resource.loader.path :: " + velocityEngine.getProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH));
		
		if (!mySettings.doSettingsExist()){
			Loggers.SERVER.error(this.getClass().getSimpleName() + " :: No Settings found in main-config.xml. Please read documentation at http://netwolfuk.wordpress.com/teamcity-plugins/");
		}
	}

	public void notifyBuildStarted(SRunningBuild sRunningBuild,
			Set<SUser> sUsers) {
		doNotifications("BuildStarted", sUsers, sRunningBuild);
	}

	public void notifyBuildSuccessful(SRunningBuild sRunningBuild,
			Set<SUser> sUsers) {
		doNotifications("BuildSuccessful", sUsers, sRunningBuild);
	}

	public void notifyBuildFailed(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
		doNotifications("BuildFailed", sUsers, sRunningBuild);
	}

	public void notifyLabelingFailed(Build build,
			jetbrains.buildServer.vcs.VcsRoot vcsRoot, Throwable throwable,
			Set<SUser> sUsers) {
		// doNotifications("LabelingFailed" ,sUsers, sRunningBuild);
	}

	public void notifyBuildFailing(SRunningBuild sRunningBuild,
			Set<SUser> sUsers) {
		doNotifications("BuildFailing", sUsers, sRunningBuild);
	}

	public void notifyBuildProbablyHanging(SRunningBuild sRunningBuild,
			Set<SUser> sUsers) {
		doNotifications("BuildProbablyHanging.", sUsers, sRunningBuild);
	}

	public void notifyResponsibleChanged(SBuildType sBuildType,
			Set<SUser> sUsers) {
		// doNotifications("Responsibility of build " + sBuildType.getFullName()
		// + " changed.",sUsers, sRunningBuild);
	}

	public String getNotificatorType() {
		return TYPE;
	}

	public String getDisplayName() {
		return TYPE_NAME;
	}

	public void setMailSender(JavaMailSenderImpl mailSender) {
		PrettyEmailNotificator.mailSender = mailSender;
	}

	public void doNotifications(String reason, Set<SUser> sUsers,
			SRunningBuild sRunningBuild) {

		Loggers.SERVER.debug(this.getClass().getSimpleName() + ":doNotifications(" + reason + ") called for build " + sRunningBuild.getBuildTypeId());
		
		PrettyEmailNotificator.mailSender.setHost(mySettings.getConfig().getSmtpHost());
		PrettyEmailNotificator.mailSender.setPort(mySettings.getConfig().getSmtpPort());
		if (mySettings.getConfig().getSmtpUsername() != null){
			PrettyEmailNotificator.mailSender.setUsername(mySettings.getConfig().getSmtpUsername());
		}
		if (mySettings.getConfig().getSmtpPassword() != null){
			PrettyEmailNotificator.mailSender.setPassword(mySettings.getConfig().getSmtpPassword());
		}
		
		PrettyEmailContentBuilder content = new PrettyEmailContentBuilder(sRunningBuild, server, mySettings.getConfig().getMaxTestsToShow());
		
		try {

			PrettyEmailMimeMessageHelper helper = new PrettyEmailMimeMessageHelper(
					PrettyEmailNotificator.mailSender.createMimeMessage(), 
					PrettyEmailNotificator.velocityEngine,
					content);
			
			if (mySettings.getConfig().getFromAddress() != null 
			 && mySettings.getConfig().getFromName() != null ){
				helper.setFrom(mySettings.getConfig().getFromAddress(),
						mySettings.getConfig().getFromName());
			} else if (mySettings.getConfig().getFromAddress() != null){
				helper.setFrom(mySettings.getConfig().getFromAddress());
			} else {
				Loggers.SERVER.warn(this.getClass().getSimpleName() + " :: No from-address set in main-config.xml. PrettyEmail sending will fail!");
				helper.setFrom("");
			}
			
			helper.generateEmail(sRunningBuild, reason, PrettyEmailNotificator.attachmentPath);


			for (SUser user : sUsers) {
				Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: Sending " + reason + " pretty email for " + sRunningBuild.getBuildTypeId() + " to " + user.getEmail());
				try {
					helper.setTo(user.getEmail());
				} catch (MessagingException mEx){
					Loggers.SERVER.warn(this.getClass().getSimpleName() + " :: Invalid Email address for " + user.getEmail());
				}
				try {
					PrettyEmailNotificator.mailSender.send(helper.getMimeMessage());
				} catch (MailException mEx){
					Loggers.SERVER.warn(this.getClass().getSimpleName() + " :: Could not send email to " + user.getEmail() + " See DEBUG output and/or STDOUT for Stacktrace");
					Loggers.SERVER.debug(mEx);
					mEx.printStackTrace();				
				}
			}
			
			
		} catch (Exception e) {
			Loggers.SERVER.warn(this.getClass().getSimpleName() + " :: Could not assemble the " + reason + " prettyemail for " + sRunningBuild.getBuildTypeId() + " See DEBUG output and/or STDOUT for Stacktrace");
			Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: Attempting to send email using the following configuration");
			Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: from-address: " + mySettings.getConfig().getFromAddress());
			Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: from-name   : " + mySettings.getConfig().getFromName());
			Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: host        : " + mySettings.getConfig().getSmtpHost());
			Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: port        : " + mySettings.getConfig().getSmtpPort());
			Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: username    : " + mySettings.getConfig().getSmtpUsername());
			if (mySettings.getConfig().getSmtpPassword() != null){
				Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: password    : HIDDEN");
			} else {
				Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: password    : " + mySettings.getConfig().getSmtpPassword());
			}
			Loggers.SERVER.debug(e);
			e.printStackTrace();
		}
	}
}
