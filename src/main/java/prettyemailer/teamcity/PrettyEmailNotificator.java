package prettyemailer.teamcity;

import java.io.IOException;
import java.util.Set;

import javax.mail.MessagingException;

import jetbrains.buildServer.Build;
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
	private static PrettyEmailMainConfig myConfig;
	
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
		PrettyEmailNotificator.myConfig = PrettyEmailNotificator.mySettings.getConfig();
		
	}

	public void register() {
		// Set these to the defaults, since we can't rely on the the MainSettings factory having been registered by spring yet.
		PrettyEmailNotificator.templatePath = server.getServerRootPath()+ myPluginDescriptor.getPluginResourcesPath() + "templates/";
		PrettyEmailNotificator.attachmentPath = server.getServerRootPath()+ myPluginDescriptor.getPluginResourcesPath() + "img/";
		
		try {
			initVelocity();
			notificatorRegistry.register(this);
			Loggers.SERVER.info(this.getClass().getSimpleName() + " :: Registering");
		} catch (Exception e) {
			Loggers.SERVER.error(this.getClass().getSimpleName() + " :: " + PrettyEmailNotificator.TYPE + " was NOT successfully registered. See DEBUG for Stacktrace");
			Loggers.SERVER.debug(e);
			e.printStackTrace();
		}

	}

	private void reloadSettings() {
		PrettyEmailNotificator.myConfig = PrettyEmailNotificator.mySettings.getConfig();
		
		if (myConfig.getTemplatePath() != null){
			PrettyEmailNotificator.templatePath = myConfig.getTemplatePath(); 
		} else {
			PrettyEmailNotificator.templatePath = server.getServerRootPath()+ myPluginDescriptor.getPluginResourcesPath() + "templates/";
		}

		if (myConfig.getAttachmentPath() != null){
			PrettyEmailNotificator.attachmentPath 
					= myConfig.getAttachmentPath(); 
		} else {
			PrettyEmailNotificator.attachmentPath 
					= server.getServerRootPath()+ myPluginDescriptor.getPluginResourcesPath() + "img/";
		}
	}

	private void initVelocity() throws Exception {
		
		velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
				PrettyEmailNotificator.templatePath);

		// FIXME Turn caching on for deployment.
		velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE , false);
		velocityEngine.init();
		Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: file.resource.loader.path :: " + velocityEngine.getProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH));

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
		doNotifications("BuildProbablyHanging", sUsers, sRunningBuild);
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

	public void doNotifications(String reason, Set<SUser> sUsers,
			SRunningBuild sRunningBuild) {

		Loggers.SERVER.debug(this.getClass().getSimpleName() + ":doNotifications(" + reason + ") called for build " + sRunningBuild.getBuildTypeId());
		
		if (!mySettings.doSettingsExist()){
			Loggers.SERVER.error(this.getClass().getSimpleName() + " :: No Settings found in main-config.xml. Please read documentation at http://netwolfuk.wordpress.com/teamcity-plugins/");
		}
		
		if ( ! mySettings.getConfig().equals(myConfig)){ 
			Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: Settings have changed. Attempting to reload settings and velocity");
			PrettyEmailNotificator.velocityEngine = null;
			PrettyEmailNotificator.velocityEngine = new VelocityEngine();
			reloadSettings();
			try {
				initVelocity();
			} catch (Exception e) {
				Loggers.SERVER.error(this.getClass().getSimpleName() + " :: " + PrettyEmailNotificator.TYPE + ". Failed to re-initialise Velocity. Are your main-config.xml settings correct");
				Loggers.SERVER.debug(e);
				e.printStackTrace();
			}
		}
		
		
		
		PrettyEmailNotificator.mailSender.setHost(myConfig.getSmtpHost());
		PrettyEmailNotificator.mailSender.setPort(myConfig.getSmtpPort());
		if (myConfig.getSmtpUsername() != null){
			PrettyEmailNotificator.mailSender.setUsername(myConfig.getSmtpUsername());
		}
		if (myConfig.getSmtpPassword() != null){
			PrettyEmailNotificator.mailSender.setPassword(myConfig.getSmtpPassword());
		}
		
		PrettyEmailContentBuilder content = new PrettyEmailContentBuilder(sRunningBuild, server, myConfig.getMaxTestsToShow());
		
		try {

			PrettyEmailMimeMessageHelper helper = new PrettyEmailMimeMessageHelper(
					PrettyEmailNotificator.mailSender.createMimeMessage(), 
					PrettyEmailNotificator.velocityEngine,
					content);
			
			if (myConfig.getFromAddress() != null 
			 && myConfig.getFromName() != null ){
				helper.setFrom(myConfig.getFromAddress(),
						myConfig.getFromName());
			} else if (myConfig.getFromAddress() != null){
				helper.setFrom(myConfig.getFromAddress());
			} else {
				Loggers.SERVER.warn(this.getClass().getSimpleName() + " :: No from-address set in main-config.xml. PrettyEmail sending will fail!");
				helper.setFrom("");
			}
			
			helper.generateEmail(sRunningBuild, reason, myConfig.getAttachImages(), PrettyEmailNotificator.attachmentPath);


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
			Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: from-address: " + myConfig.getFromAddress());
			Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: from-name   : " + myConfig.getFromName());
			Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: host        : " + myConfig.getSmtpHost());
			Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: port        : " + myConfig.getSmtpPort());
			Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: username    : " + myConfig.getSmtpUsername());
			if (myConfig.getSmtpPassword() != null){
				Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: password    : HIDDEN");
			} else {
				Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: password    : " + myConfig.getSmtpPassword());
			}
			Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: template-path  : " + myConfig.getTemplatePath());
			Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: attachment-path: " + myConfig.getAttachmentPath());
			Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: attach-images  : " + myConfig.getAttachImages());
			Loggers.SERVER.debug(e);
			e.printStackTrace();
		}
	}
}
