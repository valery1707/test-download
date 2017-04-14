package name.valery1707.test.download;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
public class SourceLoaderTest {
	public static final String TMP_FILE_SUFFIX = ".tmp";
	private File tmp;

	@Test(expected = UnsupportedOperationException.class)
	public void createDirectly() throws Exception {
		new SourceLoader();
	}

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
		List<Source> sources = SourceLoader.load(tmp);
		assertThat(sources).isEmpty();
	}

	@Test
	public void loadFileSingle() throws Exception {
		FileUtils.writeLines(tmp, UTF_8.name(), Arrays.asList(
				"https://github.com/fluidicon.png fluid-icon.png"
		));
		List<Source> sources = SourceLoader.load(tmp);
		assertThat(sources)
				.hasSize(1)
		;
		assertThat(sources.get(0).getUrl()).isEqualTo("https://github.com/fluidicon.png");
		assertThat(sources.get(0).getTargetNames())
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
		List<Source> sources = SourceLoader.load(tmp);
		assertThat(sources)
				.hasSize(2)
		;
		assertThat(sources.get(0).getUrl()).isEqualTo("https://github.com/fluidicon.png");
		assertThat(sources.get(0).getTargetNames())
				.hasSize(1)
				.containsExactly("fluid-icon.png")
		;
		assertThat(sources.get(1).getUrl()).isEqualTo("https://assets-cdn.github.com/images/modules/open_graph/github-octocat.png");
		assertThat(sources.get(1).getTargetNames())
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
		List<Source> sources = SourceLoader.load(tmp);
		assertThat(sources)
				.hasSize(1)
		;
		assertThat(sources.get(0).getUrl()).isEqualTo("https://github.com/fluidicon.png");
		assertThat(sources.get(0).getTargetNames())
				.hasSize(2)
				.containsExactly("fluid-icon.png", "fluid-icon2.png")
		;
	}
}
