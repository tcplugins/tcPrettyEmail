package prettyemailer.teamcity;

import java.text.SimpleDateFormat;
import java.util.List;

import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.problems.BuildProblem;
import jetbrains.buildServer.tests.TestInfo;
import jetbrains.buildServer.vcs.SVcsModification;
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy;

public class PrettyEmailContentBuilder {
	SRunningBuild sRunningBuild;
	SBuildServer sBuildServer;

	public PrettyEmailContentBuilder(SRunningBuild sRunningBuild, SBuildServer server) {
		this.sRunningBuild = sRunningBuild;
		this.sBuildServer = server;
	}

	public List<TestInfo> getTests() {
		return sRunningBuild.getTestMessages(0, -1);
	}
	
	public List<SVcsModification> getChanges(){
		return sRunningBuild.getChanges(
				SelectPrevBuildPolicy.SINCE_LAST_SUCCESSFULLY_FINISHED_BUILD,
				true);
	}
	
	public List<BuildProblem> getBuildProblems(){
		return sRunningBuild.getBuildProblems();
	}
	
	public String getProjectName(){
		return this.sBuildServer.getProjectManager().findProjectById(this.sRunningBuild.getProjectId()).getName();
	}
	
	public String getBuildTypeName(){
		return this.sRunningBuild.getBuildTypeName();
	}
	
	public String getProjectId(){
		return this.sRunningBuild.getProjectId();
	}
	
	public String getBuildTypeId(){
		return this.sRunningBuild.getBuildTypeId();
	}	
	public String getBuildNumber(){
		return this.sRunningBuild.getBuildNumber();
	}
	
	public String getTriggeredBy(){
		return this.sRunningBuild.getTriggeredBy().getAsString();
	}
	
	public String getRootURL(){
		return this.sBuildServer.getRootUrl();
	}
	
	public String getAgentName(){
		return this.sRunningBuild.getAgentName();
	}
	
	public String getDate(){
		SimpleDateFormat startDateFormat = new SimpleDateFormat("dd MMM yy HH:mm");
		SimpleDateFormat endDateFormat = new SimpleDateFormat("HH:mm");
		return startDateFormat.format(this.sRunningBuild.getStartDate())
			+ " - "
			+ endDateFormat.format(this.sRunningBuild.getFinishDate());
	}
}
