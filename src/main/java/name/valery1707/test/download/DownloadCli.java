package name.valery1707.test.download;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.apache.commons.io.output.DeferredFileOutputStream;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.concurrent.TimedSemaphore;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.concurrent.*;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static name.valery1707.test.download.Utils.bytesToDisplaySize;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.commons.io.IOUtils.closeQuietly;

public class DownloadCli {
	@SuppressWarnings("unused")
	private static final int EXIT_OK = 0;
	private static final int EXIT_ARGS_FAIL = 1;

	public static void main(String[] args) throws IOException {
		Args argv = parseArgs(args);
		if (argv.isDebug()) {
			System.out.println("argv.getThreadCount() = " + argv.getThreadCount());
			System.out.println("argv.getSpeedLimit() = " + argv.getSpeedLimit());
			System.out.println("argv.getSourceFile() = " + argv.getSourceFile());
			System.out.println("argv.getTargetDirectory() = " + argv.getTargetDirectory());
		}
		DownloadCli cli = new DownloadCli(argv);
		cli.download();
		System.out.println(cli.getTotalLine());
	}

	private static Args parseArgs(String[] args) {
		Args argv = new Args();
		JCommander commander = new JCommander();
		commander.setProgramName("java -jar download-cli.jar");
		commander.addObject(argv);
		try {
			commander.parse(args);
		} catch (ParameterException e) {
			System.out.println(e.getMessage());
			commander.usage();
			System.exit(EXIT_ARGS_FAIL);
		}
		return argv;
	}

	private final Args argv;
	private List<Source> sources;
	private List<Download> downloads;
	private long time;

	public DownloadCli(Args argv) {
		this.argv = argv;
	}

	/**
	 * @return Time in milliseconds
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @return Total downloaded bytes count
	 */
	public long getBytesCount() {
		return downloads.stream().mapToLong(download -> download.getStream().getByteCount()).sum();
	}

	public double getSpeed() {
		return getBytesCount() / (getTime() / 1000.0);
	}

	public List<Download> getDownloads() {
		return unmodifiableList(downloads);
	}

	public String getTotalLine() {
		return String.format("Download %d bytes in %.3f seconds.%nOverall speed %s/second", getBytesCount(), (time / 1000.0), bytesToDisplaySize(getSpeed()));
	}

	public List<Download> download() throws IOException {
		if (sources == null) {
			sources = SourceLoader.load(argv.getSourceFile());
		}
		if (argv.isDebug()) {
			System.out.println("Will download:");
			sources.forEach(source ->
					System.out.println("  From '" + source.getUrl() + "' into " + source.getTargetNames().stream().collect(joining(", ", "[", "]")))
			);
		}
		time = System.currentTimeMillis();
		TimedSemaphore semaphore;
		if (argv.getSpeedLimit() != null) {
			//Semaphore allow execute N requests in selected time period
			//Every semaphore request process X bytes
			//Select time limit less than 1 second to more correct limit speed for small files
			//N = `bytes/sec` / 10 / X
			int timePeriod = 500;
			semaphore = new TimedSemaphore(timePeriod, TimeUnit.MILLISECONDS, (int) (argv.getSpeedLimit() * (timePeriod / 1000.0) / BUFFER_SIZE));
		} else {
			semaphore = null;
		}
		ExecutorService threadPool = Executors.newFixedThreadPool(argv.getThreadCount(), new BasicThreadFactory.Builder()
				.namingPattern("download-%d")
				.build()
		);
		List<CompletableFuture<Download>> downloadFutures = sources.stream()
				.map(source -> downloadAsync(source, threadPool, semaphore).thenApply(download -> save(download, argv.getTargetDirectory())))
				.collect(toList());
		CompletableFuture<List<Download>> downloadsFuture = sequence(downloadFutures);
		downloads = downloadsFuture.join();
		threadPool.shutdown();
		if (semaphore != null) {
			semaphore.shutdown();
		}
		time = System.currentTimeMillis() - time;
		return downloads;
	}

	private CompletableFuture<Download> downloadAsync(Source source, Executor threadPool, TimedSemaphore semaphore) {
		return CompletableFuture.supplyAsync(() -> {
					try {
						return downloadSync(source, semaphore);
					} catch (IOException | InterruptedException e) {
						throw new IllegalStateException(e);
					}
				}
				, threadPool
		);
	}

	private static final int IN_MEMORY_THRESHOLD = 10 * 1024;//10 KiB

	private Download downloadSync(Source source, TimedSemaphore semaphore) throws IOException, InterruptedException {
		long start = System.currentTimeMillis();
		DeferredFileOutputStream out = new DeferredFileOutputStream(IN_MEMORY_THRESHOLD, "download", ".tmp", null);
		try {
			if (argv.isDebug()) {
				System.out.println(String.format("Start '%s' in thread '%s'", source.getUrl(), Thread.currentThread().getName()));
			}
			InputStream in = new URL(source.getUrl()).openStream();
			copy(in, out, semaphore);
			return new Download(source, out, System.currentTimeMillis() - start);
		} finally {
			closeQuietly(out);
			if (argv.isDebug()) {
				System.out.println(String.format("Complete '%s' in thread '%s'", source.getUrl(), Thread.currentThread().getName()));
			}
		}
	}

	private static final int BUFFER_SIZE = 100;//100 B

	/**
	 * Hint for speed limit found at <a href="http://stackoverflow.com/a/6271935/1263442">StackOverflow</a>.
	 * <p>
	 * But with this algorithm has problem on some network: after start limiting all network start freezing.
	 * The lower the limit, the stronger the freezes.
	 * Same problem has Steam client.
	 */
	private long copy(InputStream source, OutputStream sink, TimedSemaphore semaphore) throws IOException, InterruptedException {
		long nread = 0L;
		byte[] buf = new byte[BUFFER_SIZE];
		int n;
		while ((n = source.read(buf)) > 0) {
			if (semaphore != null) {
				semaphore.acquire();
			}
			sink.write(buf, 0, n);
			nread += n;
		}
		return nread;
	}

	private Download save(Download download, File targetDirectory) {
		DeferredFileOutputStream stream = download.getStream();

		download.getSource().getTargetNames()
				.forEach(name -> {
							try {
								FileOutputStream out = new FileOutputStream(new File(targetDirectory, name), false);
								stream.writeTo(out);
								out.close();
							} catch (IOException e) {
								throw new IllegalStateException("Could not write content into " + name);
							}
						}
				);
		if (!stream.isInMemory()) {
			deleteQuietly(stream.getFile());
		}
		return download;
	}

	private static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {
		CompletableFuture<Void> allDoneFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
		return allDoneFuture.thenApply(v ->
				futures.stream()
						.map(CompletableFuture::join)
						.collect(toList())
		);
	}
}
