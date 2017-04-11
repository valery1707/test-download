package name.valery1707.test.download.args;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class BytesConverterValidTest {
	private BytesConverter converter;

	@Parameterized.Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][]{
				{"0", 0L},
				{"01", 1L},
				{"10", 10L},
				{"10k", 10L * 1000},
				{"10m", 10L * 1000 * 1000},
				{"10g", 10L * 1000 * 1000 * 1000},
				{"10t", 10L * 1000 * 1000 * 1000 * 1000},
				{"10p", 10L * 1000 * 1000 * 1000 * 1000 * 1000},
				{"10K", 10L * 1024},
				{"10M", 10L * 1024 * 1024},
				{"10G", 10L * 1024 * 1024 * 1024},
				{"10T", 10L * 1024 * 1024 * 1024 * 1024},
				{"10P", 10L * 1024 * 1024 * 1024 * 1024 * 1024},
		});
	}

	private String input;
	private Long expected;

	public BytesConverterValidTest(String input, Long expected) {
		this.input = input;
		this.expected = expected;
	}

	@Before
	public void setUp() throws Exception {
		converter = new BytesConverter();
	}

	@Test
	public void testData() throws Exception {
		assertThat(converter.convert(input)).isEqualByComparingTo(expected);
	}
}
