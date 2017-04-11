package name.valery1707.test.download.args;

import com.beust.jcommander.ParameterException;
import org.junit.Before;
import org.junit.Test;

public class BytesConverterInvalidTest {
	private BytesConverter converter;

	@Before
	public void setUp() throws Exception {
		converter = new BytesConverter();
	}

	@Test(expected = ParameterException.class)
	public void isEmpty() throws Exception {
		converter.convert("");
	}

	@Test(expected = ParameterException.class)
	public void isBlank() throws Exception {
		converter.convert("   ");
	}

	@Test(expected = ParameterException.class)
	public void hasUnknownPrefix() throws Exception {
		converter.convert("--10k");
	}

	@Test(expected = ParameterException.class)
	public void hasUnknownSuffix1() throws Exception {
		converter.convert("10k-");
	}

	@Test(expected = ParameterException.class)
	public void hasUnknownSuffix2() throws Exception {
		converter.convert("10F");
	}

	@Test(expected = ParameterException.class)
	public void hasDot() throws Exception {
		converter.convert("10.1");
	}

	@Test(expected = ParameterException.class)
	public void hasComma() throws Exception {
		converter.convert("10,1");
	}
}
