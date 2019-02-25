package prettyemailer.teamcity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import jetbrains.buildServer.serverSide.BuildStatisticsOptions;
import jetbrains.buildServer.serverSide.CompilationBlockBean;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.ShortStatistics;
import jetbrains.buildServer.util.TimePrinter;
import jetbrains.buildServer.vcs.SVcsModification;
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy;

public class PrettyEmailContentBuilder {
	SRunningBuild sRunningBuild;
	SBuildServer sBuildServer;
	ShortStatistics shortStats;
	PrettyEmailBranchImpl branch = null;
	int maxTestsToLoad;
	int maxErrorLinesToLoad;

	public PrettyEmailContentBuilder(SRunningBuild sRunningBuild, SBuildServer server, int maxTestsToLoad, int maxErrorLinesToLoad) {
		this.sRunningBuild = sRunningBuild;
		this.sBuildServer = server;
		this.maxTestsToLoad = maxTestsToLoad;
		this.maxErrorLinesToLoad = maxErrorLinesToLoad;
		BuildStatisticsOptions options = new BuildStatisticsOptions();
		this.shortStats = this.getShortStats(options);
		if (this.sRunningBuild.getBranch() != null){
			this.branch = new PrettyEmailBranchImpl(sRunningBuild.getBranch());
		}
	}

	private ShortStatistics getShortStats(BuildStatisticsOptions options){
		// Use reflection to determine if we can get the Stats in TC 4.0.x mode or 4.5.x mode
		try {
			
			Method mSetTestNumber = options.getClass().getDeclaredMethod("setMaxNumberOfTestsStacktracesToLoad", Integer.TYPE);
			mSetTestNumber.invoke(options, this.maxTestsToLoad);
			
		} 
		// Catch a bunch of expected exceptions. Would mean the TC version is less than 4.5.x
		catch (NoSuchMethodException e){}
		catch (InvocationTargetException e){}
		catch (Exception e){
			e.printStackTrace();
		} 
		
		try {
			
			Method mLoadErrors = options.getClass().getDeclaredMethod("setLoadCompilationErrors", Boolean.TYPE);
			mLoadErrors.invoke(options, true);
			
		} 
		// Catch a bunch of expected exceptions. Would mean the TC version is less than 4.5.x
		catch (NoSuchMethodException e){}
		catch (InvocationTargetException e){}
		catch (Exception e){
			e.printStackTrace();
		} 
		
		
		return this.sRunningBuild.getBuildStatistics(options);
		
	}
	
	public List<PrettyEmailTestBlockBean> getTests() {
		return PrettyEmailTestBlockBeanWrapper.wrap(this.shortStats.getFailedTests());
	}
	
	public List<SVcsModification> getChanges(){
		return sRunningBuild.getChanges(
				SelectPrevBuildPolicy.SINCE_LAST_SUCCESSFULLY_FINISHED_BUILD,
				true);
	}
	
	public int getFailedTestCount(){
		return shortStats.getFailedTestCount();
	}

	public int getMaxTestCount(){
		return this.maxTestsToLoad;
	}	
	
	public int getNewFailedTestCount(){
		return shortStats.getNewFailedCount();
	}
	
	public int getPassedTestCount(){
		return shortStats.getPassedTestCount();
	}
	
	public int getIgnoredTestCount(){
		return shortStats.getIgnoredTestCount();
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
	
	public Long getBuildId(){
		return this.sRunningBuild.getBuildId();
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

	public String getStatus(){
		return this.sRunningBuild.getStatusDescriptor().getText();
	}

	public String getStatusLowercase(){
		return this.getStatus().toLowerCase();
	}
	
	public List<CompilationBlockBean> getCompileErrors() {
		    List<CompilationBlockBean> tempList = this.sRunningBuild.getFullStatistics().getCompilationErrorBlocks();
		    if (tempList.size() > this.maxErrorLinesToLoad){
		    	return tempList.subList(tempList.size() - this.maxErrorLinesToLoad, tempList.size());
		    } else { 
		    	return tempList;
			}
	}

	
	public String getDate(){
		Calendar calFinishDate = Calendar.getInstance();
		calFinishDate.setTime(this.sRunningBuild.getStartDate());
		calFinishDate.add(Calendar.SECOND, (int) this.sRunningBuild.getDuration());
		
		final StringBuilder sb = new StringBuilder();
		TimePrinter.createSecondsFormatter(false).formatTime(sb, this.sRunningBuild.getDuration());
		
		Loggers.SERVER.debug(this.getClass().getSimpleName()  + " :: StartDate : " + this.sRunningBuild.getStartDate());
		Loggers.SERVER.debug(this.getClass().getSimpleName()  + " :: FinishDate : " + this.sRunningBuild.getFinishDate());
		Loggers.SERVER.debug(this.getClass().getSimpleName()  + " :: Duration : " + this.sRunningBuild.getDuration());
		SimpleDateFormat startDateFormat = new SimpleDateFormat("dd MMM yy HH:mm");
		SimpleDateFormat endDateFormat = new SimpleDateFormat("HH:mm");
		return startDateFormat.format(this.sRunningBuild.getStartDate())
			+ " - "
			+ endDateFormat.format(calFinishDate.getTime())
			+ " (" + sb.toString() + ")";
	}
	
	public PrettyEmailBranchImpl getBranch(){
		return this.branch;
	}
	
	public boolean hasBranch(){
		return this.branch != null;
	}
	
	public String getFormattedBranchName(){
		if (hasBranch()){
			if (this.branch.isDefaultBranch){
				return "default branch";
			}
			return this.branch.getDisplayName();
		}
		return "";
	}
	
}
