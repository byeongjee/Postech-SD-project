package gr.uom.java.history;

import java.util.Map.Entry;
import java.util.Set;

public interface Evolution {

	public Set<Entry<ProjectVersionPair, Double>> getSimilarityEntries();

	public Set<Entry<ProjectVersionPair, Double>> getChangeEntries();

	public String getCode(ProjectVersion projectVersion);
}
