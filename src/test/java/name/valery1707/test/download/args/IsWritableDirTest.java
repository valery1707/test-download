package name.valery1707.test.download.args;

import com.beust.jcommander.ParameterException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IsWritableDirTest {
	public static final String TMP_FILE_SUFFIX = ".tmp";

	private IsWritableDir validator;
	private File tmp;

	@Before
	public void setUp() throws Exception {
		tmp = File.createTempFile("Download", TMP_FILE_SUFFIX);
		validator = new IsWritableDir();
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
	public void isNotDirectory() throws Exception {
		validator.validate("name", tmp);
	}

	@Test(expected = ParameterException.class)
	public void isNotWritable() throws Exception {
		File dir = mock(File.class);
		when(dir.exists()).thenReturn(true);
		when(dir.isDirectory()).thenReturn(true);
		when(dir.canWrite()).thenReturn(false);
		validator.validate("name", dir);
	}

	@Test
	public void isExistsWritableDirectory() throws Exception {
		validator.validate("name", tmp.getParentFile());
	}
}
