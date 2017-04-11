package name.valery1707.test.download.args;

import com.beust.jcommander.ParameterException;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("RedundantCast")
public class GreaterThenZeroTest {
	private GreaterThenZero validator;

	@Before
	public void setUp() throws Exception {
		validator = new GreaterThenZero();
	}

	//Byte

	@Test(expected = ParameterException.class)
	public void byteZero() throws Exception {
		validator.validate("name", (byte) 0);
	}

	@Test(expected = ParameterException.class)
	public void byteZeroLower() throws Exception {
		validator.validate("name", (byte) -1);
	}

	@Test
	public void byteZeroGreater() throws Exception {
		validator.validate("name", (byte) 1);
	}

	//Short

	@Test(expected = ParameterException.class)
	public void shortZero() throws Exception {
		validator.validate("name", (short) 0);
	}

	@Test(expected = ParameterException.class)
	public void shortZeroLower() throws Exception {
		validator.validate("name", (short) -1);
	}

	@Test
	public void shortZeroGreater() throws Exception {
		validator.validate("name", (short) 1);
	}

	//Integer

	@Test(expected = ParameterException.class)
	public void intZero() throws Exception {
		validator.validate("name", (int) 0);
	}

	@Test(expected = ParameterException.class)
	public void intZeroLower() throws Exception {
		validator.validate("name", (int) -1);
	}

	@Test
	public void intZeroGreater() throws Exception {
		validator.validate("name", (int) 1);
	}

	//Long

	@Test(expected = ParameterException.class)
	public void longZero() throws Exception {
		validator.validate("name", (long) 0);
	}

	@Test(expected = ParameterException.class)
	public void longZeroLower() throws Exception {
		validator.validate("name", (long) -1);
	}

	@Test
	public void longZeroGreater() throws Exception {
		validator.validate("name", (long) 1);
	}

	//Float

	@Test(expected = ParameterException.class)
	public void floatZero() throws Exception {
		validator.validate("name", (float) 0.0);
	}

	@Test(expected = ParameterException.class)
	public void floatZeroLower() throws Exception {
		validator.validate("name", (float) -0.1);
	}

	@Test
	public void floatZeroGreater() throws Exception {
		validator.validate("name", (float) 0.1);
	}

	//Double

	@Test(expected = ParameterException.class)
	public void doubleZero() throws Exception {
		validator.validate("name", (double) 0.0);
	}

	@Test(expected = ParameterException.class)
	public void doubleZeroLower() throws Exception {
		validator.validate("name", (double) -0.1);
	}

	@Test
	public void doubleZeroGreater() throws Exception {
		validator.validate("name", (double) 0.1);
	}
}
