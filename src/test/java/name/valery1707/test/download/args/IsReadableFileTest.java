package name.valery1707.test.download.args;

import com.beust.jcommander.ParameterException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static name.valery1707.test.download.args.IsWritableDirTest.TMP_FILE_SUFFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IsReadableFileTest {
	private IsReadableFile validator;
	private File tmp;

	@Before
	public void setUp() throws Exception {
		tmp = File.createTempFile("Download", TMP_FILE_SUFFIX);
		validator = new IsReadableFile();
	}

	@After
	public void tearDown() throws Exception {
		assertThat(tmp.delete()).isTrue();
	}

	@Test(expected = ParameterException.class)
	public void isNotExists() throws Exception {
		validator.validate("name", new File(tmp, "some does not exists file"));
	}

	@Test(expected = ParameterException.class)
	public void isNotFile() throws Exception {
		validator.validate("name", tmp.getParentFile());
	}

	@Test(expected = ParameterException.class)
	public void isNotReadable() throws Exception {
		File dir = mock(File.class);
		when(dir.exists()).thenReturn(true);
		when(dir.isFile()).thenReturn(true);
		when(dir.canRead()).thenReturn(false);
		validator.validate("name", dir);
	}

	@Test
	public void isExistsReadableFile() throws Exception {
		validator.validate("name", tmp);
	}
}
