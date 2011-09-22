package prettyemailer.teamcity;

import jetbrains.buildServer.serverSide.STestRun;

public class PrettyEmailTestBlockBean {
	STestRun test;
	
	PrettyEmailTestBlockBean(STestRun t){
		test = t;
	}
	
	public String getShortName(){
		return test.getTest().getName().getShortName();
	}
	
	public String getPackage(){
		if (test.getTest().getName().hasPackage()){
			return test.getTest().getName().getPackageName();
		} else {
			return "no package";
		}
	}
	
	public String getShortTextForWeb(){
		return test.getFailureInfo().getShortStacktraceForWeb();
	}
	
	public boolean isNew(){
		return test.isNewFailure();
	}
}
