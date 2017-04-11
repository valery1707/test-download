package name.valery1707.test.download;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static name.valery1707.test.download.args.IsWritableDirTest.TMP_FILE_SUFFIX;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
public class SourceLoaderTest {
	private File tmp;

	@Before
	public void setUp() throws Exception {
		tmp = File.createTempFile("Download", TMP_FILE_SUFFIX);
	}

	@After
	public void tearDown() throws Exception {
		assertThat(tmp.delete()).isTrue();
	}

	@Test
	public void loadFileEmpty() throws Exception {
		Map<String, Set<String>> map = SourceLoader.load(tmp);
		assertThat(map).isEmpty();
	}

	@Test
	public void loadFileSingle() throws Exception {
		FileUtils.writeLines(tmp, UTF_8.name(), Arrays.asList(
				"https://github.com/fluidicon.png fluid-icon.png"
		));
		Map<String, Set<String>> map = SourceLoader.load(tmp);
		assertThat(map)
				.hasSize(1)
				.containsOnlyKeys("https://github.com/fluidicon.png")
		;
		assertThat(map.get("https://github.com/fluidicon.png"))
				.hasSize(1)
				.containsExactly("fluid-icon.png")
		;
	}

	@Test
	public void loadFileDuoDifferentURL() throws Exception {
		FileUtils.writeLines(tmp, UTF_8.name(), Arrays.asList(
				"https://github.com/fluidicon.png fluid-icon.png",
				"https://assets-cdn.github.com/images/modules/open_graph/github-octocat.png github-octocat.png"
		));
		Map<String, Set<String>> map = SourceLoader.load(tmp);
		assertThat(map)
				.hasSize(2)
				.containsOnlyKeys("https://github.com/fluidicon.png", "https://assets-cdn.github.com/images/modules/open_graph/github-octocat.png")
		;
		assertThat(map.get("https://github.com/fluidicon.png"))
				.hasSize(1)
				.containsExactly("fluid-icon.png")
		;
		assertThat(map.get("https://assets-cdn.github.com/images/modules/open_graph/github-octocat.png"))
				.hasSize(1)
				.containsExactly("github-octocat.png")
		;
	}

	@Test
	public void loadFileDuoSameURL() throws Exception {
		FileUtils.writeLines(tmp, UTF_8.name(), Arrays.asList(
				"https://github.com/fluidicon.png fluid-icon.png",
				"https://github.com/fluidicon.png fluid-icon2.png"
		));
		Map<String, Set<String>> map = SourceLoader.load(tmp);
		assertThat(map)
				.hasSize(1)
				.containsOnlyKeys("https://github.com/fluidicon.png")
		;
		assertThat(map.get("https://github.com/fluidicon.png"))
				.hasSize(2)
				.containsExactly("fluid-icon.png", "fluid-icon2.png")
		;
	}
}
