package name.valery1707.test.download;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class Utils {
	Utils() {
		throw new UnsupportedOperationException("Must not be created directly");
	}

	private static final String[] bytesSuffixes = new String[]{"B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB", "ZiB", "YiB"};
	private static final DecimalFormat bytesFormat0 = new DecimalFormat("#");
	private static final DecimalFormat bytesFormat1 = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));

	/**
	 * Преобразование количества байт в строку
	 *
	 * @param b Количество байт
	 * @return Строка
	 */
	public static String bytesToDisplaySize(double b) {
		return bytesToDisplaySize(b, bytesSuffixes);
	}

	/**
	 * Преобразование количества байт в строку
	 *
	 * @param size          Количество байт
	 * @param bytesSuffixes Суффиксы для разных множителей
	 * @return Строка
	 */
	public static String bytesToDisplaySize(double size, String[] bytesSuffixes) {
		int memoryFactor = 0;
		while (size > 1000.0) {
			memoryFactor++;
			size = (size / 1024);
		}
		String suffix;
		if (memoryFactor >= bytesSuffixes.length) {
			suffix = "Unknown(" + memoryFactor + ")";
		} else {
			suffix = bytesSuffixes[memoryFactor];
		}
		if (memoryFactor > 0) {
			return bytesFormat1.format(size) + " " + suffix;
		} else {
			return bytesFormat0.format(size) + " " + suffix;
		}
	}
}
