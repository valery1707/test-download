package name.valery1707.test.download;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UtilsTest {
	@Test(expected = IllegalStateException.class)
	public void createDirectly() throws Exception {
		new Utils();
	}

	@Test
	public void testByteCountToDisplaySize() throws Exception {
		assertThat(Utils.bytesToDisplaySize(100)).describedAs("100").isEqualTo("100 B");
		assertThat(Utils.bytesToDisplaySize(500)).describedAs("500").isEqualTo("500 B");
		assertThat(Utils.bytesToDisplaySize(1024)).describedAs("1024").isEqualTo("1.00 KiB");
		assertThat(Utils.bytesToDisplaySize(5000)).describedAs("5000").isEqualTo("4.88 KiB");
	}

	@Test
	public void tooBig() throws Exception {
		double value = 1.256 * Math.pow(1024, 9);
		assertThat(Utils.bytesToDisplaySize(value)).isEqualTo("1.26 Unknown(9)");
	}
}
