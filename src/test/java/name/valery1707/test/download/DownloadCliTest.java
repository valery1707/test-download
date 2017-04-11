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

	private DownloadCli downloadUrlList(String sourcePath, int threadCount, Long speedLimit) throws IOException {
		File sourceFile = new File(this.getClass().getResource(sourcePath).getFile());
		assertThat(sourceFile).exists().isFile().canRead();

		//Clear downloaded files
		List<Source> sources = SourceLoader.load(sourceFile);
		sources.forEach(source -> source.getTargetNames().stream().map(name -> new File(tmpDir, name)).forEach(
				file -> assertThat(!file.exists() || file.delete()).isTrue()
		));

		checkTempFiles();

		Args args = new Args();
		args.setThreadCount(threadCount);
		args.setSpeedLimit(speedLimit);
		args.setSourceFile(sourceFile);
		args.setTargetDirectory(tmpDir);
		DownloadCli cli = new DownloadCli(args);
		cli.download();

		checkTempFiles();

		//Clear downloaded files
		sources.forEach(source -> source.getTargetNames().stream().map(name -> new File(tmpDir, name)).forEach(
				file -> assertThat(file)
						.exists()
						.isFile()
						.canRead()
						.is(new Condition<>(File::delete, "Can delete file %s", file.getAbsolutePath()))
		));

		return cli;
	}

	private void checkTempFiles() {
		File[] files = tmpDir.listFiles((FilenameFilter) new AndFileFilter(new PrefixFileFilter("download"), new SuffixFileFilter(".tmp")));
		assertThat(files).isEmpty();
	}

	@Test(timeout = 10_000/*10 seconds*/)
	public void downloadUrlList1_thread1_speedAny() throws Exception {
		DownloadCli cli = downloadUrlList("/url_list_1.txt", 1, null);
		assertThat(cli.getDownloads()).hasSize(2);
		assertThat(cli.getTime()).isPositive();
		assertThat(cli.getBytesCount()).isPositive();
		assertThat(cli.getSpeed()).isPositive();
	}
}
