package prettyemailer.teamcity.content;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import jetbrains.buildServer.serverSide.SRunningBuild;
import lombok.Getter;
import prettyemailer.teamcity.Loggers;

public class PrettyEmailBuildStatistics {
	
	@Getter
	private Map<String, BigDecimal> stats = new LinkedHashMap<>();
	
	public static PrettyEmailBuildStatistics build(SRunningBuild sRunningBuild) {
		PrettyEmailBuildStatistics statistics = new PrettyEmailBuildStatistics();
		
		statistics.stats.putAll(sRunningBuild.getStatisticValues());
		Loggers.SERVER.debug("PrettyEmailBuildStatistics :: Found the following build statistics: "
				+ StringUtils.join(statistics.stats.keySet(), ",")
			);
		
		return statistics;
	}
	
	public boolean isFound() {
		return ! stats.isEmpty();
	}
	
	public int getCount() {
		return stats.size();
	}
	
	public Set<String> getKeys() {
		return stats.keySet();
	}
	
	public BigDecimal getValue(String key) {
		return this.stats.containsKey(key) ? this.stats.get(key) : null;
	}

}
