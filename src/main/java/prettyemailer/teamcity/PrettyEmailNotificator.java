package prettyemailer.teamcity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;

import jetbrains.buildServer.Build;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.notification.Notificator;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.tests.TestInfo;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.vcs.SVcsModification;
import jetbrains.buildServer.web.openapi.PluginDescriptor;

import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

public class PrettyEmailNotificator implements Notificator {

	private static final String TYPE = "prettyEmailNotifier";
	private static final String TYPE_NAME = "Pretty Email Notifier";
	private static SBuildServer server;
	private static PluginDescriptor myPluginDescriptor;
	private static final Logger LOG = Logger
			.getLogger("prettyemailer.teamcity.PrettyEmailNotificator");
	private static VelocityEngine velocityEngine;

	private static JavaMailSenderImpl mailSender;
	private static NotificatorRegistry notificatorRegistry;


	public PrettyEmailNotificator(NotificatorRegistry notificatorRegistry,
			SBuildServer server, PluginDescriptor pluginDescriptor)
			throws IOException {
		
		PrettyEmailNotificator.server = server;
		PrettyEmailNotificator.myPluginDescriptor = pluginDescriptor;
		PrettyEmailNotificator.notificatorRegistry = notificatorRegistry;
		PrettyEmailNotificator.velocityEngine = new VelocityEngine();
		PrettyEmailNotificator.mailSender = new JavaMailSenderImpl();
	}

	public void register() {
		
		
		velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
				server.getServerRootPath()+ myPluginDescriptor.getPluginResourcesPath() + "templates/");
		// FIXME Turn caching on for deployment.
		velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE , false);

		
		try {
			velocityEngine.init();
			notificatorRegistry.register(this);
			LOG.info("Registering");
		} catch (Exception e) {
			LOG.error(PrettyEmailNotificator.TYPE + " was NOT successfully registered.");
			LOG.debug(e.toString());
		}
		LOG.debug("file.resource.loader.path :: " + velocityEngine.getProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH));
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

	public void prepare(MimeMessageHelper message,
			List<SVcsModification> changes, List<TestInfo> tests)
			throws Exception {
		// MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
		// message.setTo(user.getEmailAddress());

	}

	public void doNotifications(String reason, Set<SUser> sUsers,
			SRunningBuild sRunningBuild) {

		
		// FIXME, get settings from TC!!
		PrettyEmailNotificator.mailSender.setHost("mail");
		
		PrettyEmailContentBuilder content = new PrettyEmailContentBuilder(sRunningBuild, server);
		
		try {

			PrettyEmailMimeMessageHelper helper = new PrettyEmailMimeMessageHelper(
					PrettyEmailNotificator.mailSender.createMimeMessage(), 
					PrettyEmailNotificator.velocityEngine,
					content);
			
			// FIXME, get settings from TC!!
			helper.setFrom("netwolfuk+tcPrettyEmail@gmail.com",	"Net Wolf - tcPrettyEmail");
			helper.generateEmail(sRunningBuild, reason, 
					server.getServerRootPath()+ myPluginDescriptor.getPluginResourcesPath());


			for (SUser user : sUsers) {
				//Loggers.SERVER.info("PrettyEmailerNotifier :: " + user.getEmail()	+ " :: " + emailBodyText);
				helper.setTo(user.getEmail());

			}
			PrettyEmailNotificator.mailSender.send(helper.getMimeMessage());
			
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			Loggers.SERVER.error(e.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			Loggers.SERVER.error(e.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Loggers.SERVER.error(e.toString());
		}


		
		
		// mimeMessage.

		/*
		 * for(SUser user : sUsers) {
		 * //Loggers.SERVER.info("PrettyEmailerNotifier :: " + mimeMessage);
		 * Loggers.SERVER.info("PrettyEmailerNotifier :: " +
		 * user.getEmail().toString());
		 * //Loggers.SERVER.info("PretyEmailerNotifier :: " +
		 * sRunningBuild.getTestMessages(-1).toString()); //if
		 * (sRunningBuild.isFinished()){ //Build blah = (Build)sRunningBuild;
		 */
		/*
		 for (SVcsModification change : changes){
		 Loggers.SERVER.info("PrettyEmailerNotifier:change.getDescription " +
		 change.getDescription());
		 Loggers.SERVER.info("PrettyEmailerNotifier:change.getUserName " +
		 change.getUserName());
		 Loggers.SERVER.info("PrettyEmailerNotifier:change.getChangeCount " +
		 String.valueOf(change.getChangeCount())); List<VcsFileModification>
		 files = change.getChanges(); for (VcsFileModification file : files){
		 Loggers.SERVER.info("PrettyEmailerNotifier:file.getFileName " +
		 file.getFileName());
		 Loggers.SERVER.info("PrettyEmailerNotifier:file.getRelativeFileName "
		 + file.getRelativeFileName()); }
		 Loggers.SERVER.info("PrettyEmailerNotifier:change.getChangeCount " +
		 String.valueOf(change.getChangeCount()));

		  
		 }
		 */
		 /*
		 * 
		 * //server.get Loggers.SERVER.info("PretyEmailerNotifier :: " +
		 * tests.toString()); for (TestInfo info : tests){
		 * Loggers.SERVER.info("PrettyEmailerNotifier:info.getStacktrace " +
		 * info.getStacktrace());
		 * Loggers.SERVER.info("PrettyEmailerNotifier:info.getStacktraceMessage "
		 * + info.getStacktraceMessage());
		 * Loggers.SERVER.info("PrettyEmailerNotifier:info.getTestName " +
		 * info.getTestName());
		 * Loggers.SERVER.info("PrettyEmailerNotifier:info.getActual " +
		 * info.getActual());
		 * Loggers.SERVER.info("PrettyEmailerNotifier:info.toString " +
		 * info.toString());
		 * Loggers.SERVER.info("PrettyEmailerNotifier:info.getErrOutput " +
		 * info.getErrOutput());
		 * Loggers.SERVER.info("PrettyEmailerNotifier:info.getStdOutput " +
		 * info.getStdOutput());
		 * Loggers.SERVER.info("PrettyEmailerNotifier:info.getErrOutput " +
		 * info.getErrOutput());
		 * 
		 * } //}
		 * 
		 * 
		 * // WebHook webhook = new WebHook(user.getPropertyValue(URL)); //
		 * webhook.addParam("message", message); // try { // webhook.post(); //
		 * } catch (FileNotFoundException e) { //
		 * Loggers.SERVER.error(e.toString()); // } catch (IOException e) { //
		 * Loggers.SERVER.error(e.toString()); // } }
		 */
	}
}
