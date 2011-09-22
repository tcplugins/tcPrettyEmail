package prettyemailer.teamcity;

import java.util.ArrayList;
import java.util.List;

import jetbrains.buildServer.serverSide.STestRun;

public final class PrettyEmailTestBlockBeanWrapper {
	public static List<PrettyEmailTestBlockBean> wrap(List<STestRun> tests) {
		ArrayList<PrettyEmailTestBlockBean> testBeans = new ArrayList<PrettyEmailTestBlockBean>();
		for (STestRun test : tests){
			testBeans.add(new PrettyEmailTestBlockBean(test));
		}
		return testBeans;
	}
}
