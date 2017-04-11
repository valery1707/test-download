package name.valery1707.test.download;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singleton;

public class SourceLoader {
	public static Map<String, Set<String>> load(File sourceFile) throws IOException {
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
				));
	}
}