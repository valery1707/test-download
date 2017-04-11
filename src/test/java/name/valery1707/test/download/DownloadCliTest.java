package name.valery1707.test.download;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import static name.valery1707.test.download.Utils.bytesToDisplaySize;
import static name.valery1707.test.download.args.IsWritableDirTest.TMP_FILE_SUFFIX;
import static org.assertj.core.api.Assertions.assertThat;

public class DownloadCliTest {
	private File tmpDir;

	@Before
	public void setUp() throws Exception {
		File tmp = File.createTempFile("Download", TMP_FILE_SUFFIX);
		tmpDir = tmp.getParentFile();
		assertThat(tmp.delete()).isTrue();
	}

	@Test
	public void cli_urlList1_thread1_speedAny_debug() throws Exception {
		DownloadCli.main(new String[]{
				"-f ",
				sourceFile("/url_list_1.txt").getAbsolutePath(),
				"-o ",
				tmpDir.getAbsolutePath(),
				"--debug",
		});
	}

	@Test
	public void cli_urlList1_thread5_speedAny() throws Exception {
		DownloadCli.main(new String[]{
				"-f ",
				sourceFile("/url_list_1.txt").getAbsolutePath(),
				"-o ",
				tmpDir.getAbsolutePath(),
				"-n",
				"5",
		});
	}

	@Test
	public void cli_usage() throws Exception {
		DownloadCli.main(new String[]{
		});
	}

	private File sourceFile(String sourcePath) {
		File sourceFile = new File(this.getClass().getResource(sourcePath).getFile());
		assertThat(sourceFile).exists().isFile().canRead();
		return sourceFile;
	}

	private DownloadCli downloadUrlList(String sourcePath, int threadCount, Long speedLimit) throws IOException {
		File sourceFile = sourceFile(sourcePath);

		//Clear downloaded files
		List<Source> sources = SourceLoader.load(sourceFile);
		sources.forEach(source -> source.getTargetNames().stream().map(name -> new File(tmpDir, name)).forEach(
				file -> assertThat(!file.exists() || file.delete()).isTrue()
		));

		checkTempFiles(true);

		Args args = new Args();
		args.setThreadCount(threadCount);
		args.setSpeedLimit(speedLimit);
		args.setSourceFile(sourceFile);
		args.setTargetDirectory(tmpDir);
		args.setDebug(false);
		DownloadCli cli = new DownloadCli(args);
		cli.download();

		checkTempFiles(false);

		//Clear downloaded files
		sources.forEach(source -> source.getTargetNames().stream().map(name -> new File(tmpDir, name)).forEach(
				file -> assertThat(file)
						.exists()
						.isFile()
						.canRead()
						.is(canDelete())
		));

		assertThat(cli.getDownloads()).hasSameSizeAs(sources);
		assertThat(cli.getTime()).isPositive();
		assertThat(cli.getBytesCount()).isPositive();
		assertThat(cli.getSpeed()).isPositive();

		//Check speed
		if (speedLimit != null) {
			System.out.println(String.format("Reached overall speed %s/sec within limit %s/sec"
					, bytesToDisplaySize(cli.getSpeed()), bytesToDisplaySize(speedLimit)
			));
			assertThat(cli.getSpeed()).isLessThan(speedLimit);
			assertThat(cli.getTime()).isGreaterThanOrEqualTo(cli.getBytesCount() / speedLimit);
		}

		return cli;
	}

	private void checkTempFiles(boolean canClear) {
		File[] files = tmpDir.listFiles((FilenameFilter) new AndFileFilter(new PrefixFileFilter("download"), new SuffixFileFilter(".tmp")));
		if (canClear) {
			assertThat(files).are(canDelete());
		} else {
			assertThat(files).isEmpty();
		}
	}

	private static Condition<File> canDelete() {
		return new Condition<>(File::delete, "Can delete file");
	}

	@Test(timeout = 10_000/*10 seconds*/)
	@SuppressWarnings("unused")
	public void downloadUrlList1_thread1_speedAny() throws Exception {
		DownloadCli cli = downloadUrlList("/url_list_1.txt", 1, null);
	}

	@Test(timeout = 10_000/*10 seconds*/)
	@SuppressWarnings("unused")
	public void downloadUrlList1_thread2_speedAny() throws Exception {
		DownloadCli cli = downloadUrlList("/url_list_1.txt", 2, null);
	}

	/**
	 * Download about 68 KiB with speed 2 KiB/sec take about 35 seconds
	 */
	@Test(timeout = 40_000/*40 seconds*/)
	@SuppressWarnings("unused")
	public void downloadUrlList1_thread1_speed2K() throws Exception {
		DownloadCli cli = downloadUrlList("/url_list_1.txt", 1, 2 * 1024L);
	}

	/**
	 * Download about 68 KiB with speed 2 KiB/sec take about 35 seconds
	 */
	@Test(timeout = 40_000/*40 seconds*/)
	@SuppressWarnings("unused")
	public void downloadUrlList1_thread2_speed2K() throws Exception {
		DownloadCli cli = downloadUrlList("/url_list_1.txt", 2, 2 * 1024L);
	}

	/**
	 * Download about 260 KiB with speed 10 KiB/sec take about 26 seconds
	 */
	@Test(timeout = 50_000/*50 seconds*/)
	@SuppressWarnings("unused")
	public void downloadJQuery_thread1_speed10K() throws Exception {
		DownloadCli cli = downloadUrlList("/jQuery.txt", 1, 10 * 1024L);
	}

	@Test(timeout = 120_000/*120 seconds*/)
	@SuppressWarnings("unused")
	public void downloadFontAwesome_thread10_speed100K() throws Exception {
		DownloadCli cli = downloadUrlList("/Font-Awesome.txt", 10, 100 * 1024L);
	}
}
