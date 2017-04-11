package name.valery1707.test.download;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class Source {
	private final String url;
	private final Set<String> targetNames;

	public Source(String url, Set<String> targetNames) {
		this.url = url;
		this.targetNames = Collections.unmodifiableSet(new LinkedHashSet<>(targetNames));
	}

	public String getUrl() {
		return url;
	}

	public Set<String> getTargetNames() {
		return targetNames;
	}
}
