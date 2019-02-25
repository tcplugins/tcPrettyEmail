package prettyemailer.teamcity;

import java.io.IOException;
import java.util.Set;

import javax.mail.MessagingException;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;

import jetbrains.buildServer.notification.Notificator;
import jetbrains.buildServer.notification.NotificatorAdapter;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PluginDescriptor;

public class PrettyEmailNotificator extends NotificatorAdapter implements Notificator {

	private static final String TYPE = "prettyEmailNotifier";
	private static final String TYPE_NAME = "Pretty Email Notifier";
	private SBuildServer myServer;
	private PluginDescriptor myPluginDescriptor;
	private VelocityEngine velocityEngine;

	private NotificatorRegistry myNotificatorRegistry;
	private PrettyEmailMainSettings mySettings;
	private PrettyEmailMainConfig myConfig;
	
	private String templatePath;
	private String attachmentPath;


	public PrettyEmailNotificator(NotificatorRegistry notificatorRegistry,
			SBuildServer server, PluginDescriptor pluginDescriptor,
			PrettyEmailMainSettings settings)
			throws IOException {
		
		myServer = server;
		mySettings = settings;
		myPluginDescriptor = pluginDescriptor;
		myNotificatorRegistry = notificatorRegistry;
		myConfig = mySettings.getConfig();
		
	}

	public void register() {
		templatePath = myServer.getServerRootPath() + myPluginDescriptor.getPluginResourcesPath() + "templates/";
		attachmentPath = myServer.getServerRootPath() + myPluginDescriptor.getPluginResourcesPath() + "img/";
		
		try {
			velocityEngine = initVelocity();
			myNotificatorRegistry.register(this);
			Loggers.SERVER.info(this.getClass().getSimpleName() + " :: Registering");
		} catch (Exception e) {
			Loggers.SERVER.error(this.getClass().getSimpleName() + " :: " + PrettyEmailNotificator.TYPE + " was NOT successfully registered. See DEBUG for Stacktrace");
			Loggers.SERVER.debug(e);
			e.printStackTrace();
		}

	}

	private void reloadSettings(PrettyEmailMainConfig myConfig) {
		
		if (myConfig.getTemplatePath() != null){
			templatePath = myConfig.getTemplatePath(); 
		} else {
			templatePath = myServer.getServerRootPath()+ myPluginDescriptor.getPluginResourcesPath() + "templates/";
		}

		if (myConfig.getAttachmentPath() != null){
			attachmentPath = myConfig.getAttachmentPath(); 
		} else {
			attachmentPath = myServer.getServerRootPath()+ myPluginDescriptor.getPluginResourcesPath() + "img/";
		}
	}

	private VelocityEngine initVelocity() throws Exception {
		
		VelocityEngine velocityEngine = new VelocityEngine();
		
		velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
				templatePath);

		// FIXME Turn caching on for deployment.
		velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE , false);
		velocityEngine.setProperty(RuntimeConstants.INPUT_ENCODING, "utf-8");
		velocityEngine.init();
		Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: file.resource.loader.path :: " + velocityEngine.getProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH));

		return velocityEngine;
	}
	

	@Override
	public String getNotificatorType() {
		return TYPE;
	}

	@Override
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
			reloadSettings(mySettings.getConfig());
			myConfig = mySettings.getConfig();
			try {
				velocityEngine = initVelocity();
			} catch (Exception e) {
				Loggers.SERVER.error(this.getClass().getSimpleName() + " :: " + PrettyEmailNotificator.TYPE + ". Failed to re-initialise Velocity. Are your main-config.xml settings correct");
				Loggers.SERVER.debug(e);
				e.printStackTrace();
			}
		}
		

		JavaMailSender mailSender = PrettyEmailMailSenderFactory.getJavaMailSender(myConfig);
		PrettyEmailContentBuilder content = new PrettyEmailContentBuilder(
													sRunningBuild, 
													myServer, 
													myConfig.getMaxTestsToShow(), 
													myConfig.getMaxErrorLinesToShow()
												);
		
		try {

			PrettyEmailMimeMessageHelper helper = new PrettyEmailMimeMessageHelper(
					mailSender.createMimeMessage(), 
					velocityEngine,
					content).build(myConfig);
			
			helper.generateEmail(sRunningBuild, reason, myConfig.getAttachImages(), attachmentPath);


			for (SUser user : sUsers) {
				if (user.getEmail() == null || user.getEmail() == ""){
					Loggers.SERVER.warn(this.getClass().getSimpleName() + " :: Invalid Email address for " + user.getUsername().toString() + " (" + user.getEmail().toString() + ")");
					continue;
				}
				
				Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: Sending " + reason + " pretty email for " + sRunningBuild.getBuildTypeId() + " to " + user.getEmail());
				try {
					helper.setTo(user.getEmail());
				} catch (MessagingException mEx){
					Loggers.SERVER.warn(this.getClass().getSimpleName() + " :: Invalid Email address for " + user.getUsername() + " (" + user.getEmail() + ")");
				}
				try {
					mailSender.send(helper.getMimeMessage());
				} catch (MailException mEx){
					Loggers.SERVER.warn(this.getClass().getSimpleName() + " :: Could not send email to " + user.getUsername() + " (" + user.getEmail() + ")" + " See DEBUG output and/or STDOUT for Stacktrace");
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
			Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: starttls-enabled: " + myConfig.getStartTLSEnabled());
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

	@Override
	public void notifyBuildStarted(SRunningBuild sRunningBuild,
			Set<SUser> sUsers) {
		doNotifications("BuildStarted", sUsers, sRunningBuild);
	}

	@Override
	public void notifyBuildSuccessful(SRunningBuild sRunningBuild,
			Set<SUser> sUsers) {
		doNotifications("BuildSuccessful", sUsers, sRunningBuild);
	}

	@Override
	public void notifyBuildFailed(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
		doNotifications("BuildFailed", sUsers, sRunningBuild);
	}

	@Override
	public void notifyBuildFailing(SRunningBuild sRunningBuild,	Set<SUser> sUsers) {
		doNotifications("BuildFailing", sUsers, sRunningBuild);
	}

	@Override
	public void notifyBuildProbablyHanging(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
		doNotifications("BuildProbablyHanging", sUsers, sRunningBuild);
	}

}
