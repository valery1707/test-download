package name.valery1707.test.download;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singleton;

public final class SourceLoader {
	SourceLoader() {
		throw new UnsupportedOperationException("Must not be created directly");
	}

	public static List<Source> load(File sourceFile) throws IOException {
		List<String> lines = FileUtils.readLines(sourceFile, UTF_8);
		return lines.stream()
				.map(s -> s.split(" "))
				.collect(Collectors.toMap(
						ss -> ss[0],
						ss -> singleton(ss[1]),
						(valueL, valueR) -> {
							Set<String> result = new LinkedHashSet<>();
							result.addAll(valueL);
							result.addAll(valueR);
							return result;
						},
						LinkedHashMap::new
				))
				.entrySet().stream()
				.map(e -> new Source(e.getKey(), e.getValue()))
				.collect(Collectors.toList());
	}
}
