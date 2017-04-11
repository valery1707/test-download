package name.valery1707.test.download;

import com.beust.jcommander.Parameter;
import name.valery1707.test.download.args.BytesConverter;
import name.valery1707.test.download.args.GreaterThenZero;
import name.valery1707.test.download.args.IsReadableFile;
import name.valery1707.test.download.args.IsWritableDir;

import java.io.File;

public class Args {
	@Parameter(
			names = {"-n", "--threadCount"}
			, description = "Count of total thread count"
			, validateValueWith = GreaterThenZero.class
	)
	private int threadCount = 1;

	@Parameter(
			names = {"-l", "--limit"}
			, description =
			"Overall speed limit for all threads."
			+ " Dimension: byte per second."
			+ " Can use suffixes (K,M,...) with lower/upper case. Lowercase is multiply by 1000. Uppercase is multiply by 1024."
			, validateValueWith = GreaterThenZero.class
			, converter = BytesConverter.class
	)
	private Long speedLimit;

	@Parameter(
			names = {"-f"}
			, description = "File with links list"
			, validateValueWith = IsReadableFile.class
			, required = true
	)
	private File sourceFile;

	@Parameter(
			names = {"-o"}
			, description = "Target directory"
			, validateValueWith = IsWritableDir.class
			, required = true
	)
	private File targetDirectory;

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public Long getSpeedLimit() {
		return speedLimit;
	}

	public void setSpeedLimit(Long speedLimit) {
		this.speedLimit = speedLimit;
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}

	public File getTargetDirectory() {
		return targetDirectory;
	}

	public void setTargetDirectory(File targetDirectory) {
		this.targetDirectory = targetDirectory;
	}
}
