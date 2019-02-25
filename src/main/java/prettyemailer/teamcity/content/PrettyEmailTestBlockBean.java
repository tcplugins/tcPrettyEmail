package prettyemailer.teamcity.content;

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
		
		try {
			return test.getFailureInfo().getShortStacktraceForWeb();
		} 
		// Catch a null pointer, which means teamcity is version 9.1.6 or higher.
		catch (NullPointerException npe){
			return test.getFullText();
		}

	}
	
	public boolean isNew(){
		return test.isNewFailure();
	}
}
