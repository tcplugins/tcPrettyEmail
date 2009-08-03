package prettyemailer.teamcity;



import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jetbrains.buildServer.Build;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.notification.Notificator;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.UserPropertyInfo;
import jetbrains.buildServer.tests.TestInfo;
import jetbrains.buildServer.users.NotificatorPropertyKey;
import jetbrains.buildServer.users.PropertyKey;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.vcs.SVcsModification;
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy;
import jetbrains.buildServer.vcs.VcsFileModification;
import jetbrains.buildServer.vcs.VcsModification;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.velocity.VelocityEngineUtils;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

public class PrettyEmailNotificator implements Notificator {

    private static final String TYPE = "prettyEmailNotifier";
    private static final String TYPE_NAME = "Pretty Email Notifier";
    private static final String WEBHOOK_URL = "webHookURL";
    private static SBuildServer server;
    
    private JavaMailSender mailSender;
    private VelocityEngine velocityEngine;

    //private static final PropertyKey URL = new NotificatorPropertyKey(TYPE, WEBHOOK_URL);
    
    public PrettyEmailNotificator(NotificatorRegistry notificatorRegistry, SBuildServer server) throws IOException {
        ArrayList<UserPropertyInfo> userProps = new ArrayList<UserPropertyInfo>();
        //userProps.add(new UserPropertyInfo(WEBHOOK_URL, "WebHook URL"));
        notificatorRegistry.register(this, userProps);
        this.server = server;
    }

    public void notifyBuildStarted(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        //doNotifications("Build " + sRunningBuild.getFullName() + " started.",sUsers, sRunningBuild);
    }

    public void notifyBuildSuccessful(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        doNotifications("Build " + sRunningBuild.getFullName() + " successfull.",sUsers, sRunningBuild);
    }

    public void notifyBuildFailed(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        doNotifications("Build " + sRunningBuild.getFullName() + " failed.",sUsers, sRunningBuild);
    }

    public void notifyLabelingFailed(Build build, jetbrains.buildServer.vcs.VcsRoot vcsRoot, Throwable throwable, Set<SUser> sUsers) {
        //doNotifications("Labeling of build " + build.getFullName() + " failed.",sUsers, sRunningBuild);
    }

    public void notifyBuildFailing(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        //doNotifications("Build " + sRunningBuild.getFullName() + " is failing.",sUsers,sRunningBuild);
    }

    public void notifyBuildProbablyHanging(SRunningBuild sRunningBuild, Set<SUser> sUsers) {
        //doNotifications("Build " + sRunningBuild.getFullName() + " is probably hanging.",sUsers, sRunningBuild);
    }

    public void notifyResponsibleChanged(SBuildType sBuildType, Set<SUser> sUsers) {
        //doNotifications("Responsibility of build " + sBuildType.getFullName() + " changed.",sUsers, sRunningBuild);
    }

    public String getNotificatorType() {
        return TYPE;
    }

    public String getDisplayName() {
        return TYPE_NAME;
    }

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
     }

     public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
     }
    
    public void prepare(MimeMessage mimeMessage) throws Exception {
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
        //message.setTo(user.getEmailAddress());
        message.setFrom("webmaster@csonth.gov.uk"); // could be parameterized...
        Map model = new HashMap();
        //model.put("user", user);
        String text = VelocityEngineUtils.mergeTemplateIntoString(
           velocityEngine, "com/dns/registration-confirmation.vm", model);
        message.setText(text, true);
     }
    
    public void doNotifications(String message, Set<SUser> sUsers, SRunningBuild sRunningBuild) {
        for(SUser user : sUsers) {
        	Loggers.SERVER.info("PrettyEmailerNotifier :: " + message);
        	Loggers.SERVER.info("PrettyEmailerNotifier :: " + user.getEmail().toString());
        	//Loggers.SERVER.info("PretyEmailerNotifier :: " + sRunningBuild.getTestMessages(-1).toString());
        	//if (sRunningBuild.isFinished()){
        		//Build blah = (Build)sRunningBuild;
        		List<TestInfo> tests = sRunningBuild.getTestMessages(0, -1);
        		List<SVcsModification> changes = sRunningBuild.getChanges(SelectPrevBuildPolicy.SINCE_LAST_SUCCESSFULLY_FINISHED_BUILD, true);
        		
        		for (SVcsModification change : changes){
        			Loggers.SERVER.info("PrettyEmailerNotifier:change.getDescription " + change.getDescription());
        			Loggers.SERVER.info("PrettyEmailerNotifier:change.getUserName " + change.getUserName());
        			Loggers.SERVER.info("PrettyEmailerNotifier:change.getChangeCount " + String.valueOf(change.getChangeCount()));
        			List<VcsFileModification> files = change.getChanges();
        			for (VcsFileModification file : files){
        				Loggers.SERVER.info("PrettyEmailerNotifier:file.getFileName " + file.getFileName());
        				Loggers.SERVER.info("PrettyEmailerNotifier:file.getRelativeFileName " + file.getRelativeFileName());
        			}
        			Loggers.SERVER.info("PrettyEmailerNotifier:change.getChangeCount " + String.valueOf(change.getChangeCount()));
        			
        		}
        		
        		//server.get
        		Loggers.SERVER.info("PretyEmailerNotifier :: " + tests.toString());
        		for (TestInfo info : tests){
        			Loggers.SERVER.info("PrettyEmailerNotifier:info.getStacktrace " + info.getStacktrace());
        			Loggers.SERVER.info("PrettyEmailerNotifier:info.getStacktraceMessage " + info.getStacktraceMessage());
        			Loggers.SERVER.info("PrettyEmailerNotifier:info.getTestName " + info.getTestName());
        			Loggers.SERVER.info("PrettyEmailerNotifier:info.getActual " + info.getActual());
        			Loggers.SERVER.info("PrettyEmailerNotifier:info.toString " + info.toString());
        			Loggers.SERVER.info("PrettyEmailerNotifier:info.getErrOutput " + info.getErrOutput());
        			Loggers.SERVER.info("PrettyEmailerNotifier:info.getStdOutput " + info.getStdOutput());
        			Loggers.SERVER.info("PrettyEmailerNotifier:info.getErrOutput " + info.getErrOutput());
        			
        		}
        	//}
        	
        	
//        	WebHook webhook = new WebHook(user.getPropertyValue(URL));
//        	webhook.addParam("message", message);
//        	try {
//				webhook.post();
//			} catch (FileNotFoundException e) {
//				Loggers.SERVER.error(e.toString());
//			} catch (IOException e) {
//				Loggers.SERVER.error(e.toString());
//			}
        }
    }
}
