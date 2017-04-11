package name.valery1707.test.download;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.DeferredFileOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
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

	public long getTime() {
		return time;
	}

	public long getBytesCount() {
		return downloads.stream().mapToLong(download -> download.getStream().getByteCount()).sum();
	}

	public List<Download> getDownloads() {
		return unmodifiableList(downloads);
	}

	private String getTotalLine() {
		return String.format("Download %d bytes in %.3f seconds. ", getBytesCount(), (time / 1000.0));
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
		ExecutorService threadPool = Executors.newFixedThreadPool(argv.getThreadCount());
		List<CompletableFuture<Download>> downloadFutures = sources.stream()
				.map(source -> downloadAsync(source, threadPool).thenApply(download -> save(download, argv.getTargetDirectory())))
				.collect(toList());
		CompletableFuture<List<Download>> downloadsFuture = sequence(downloadFutures);
		threadPool.shutdown();
		downloads = downloadsFuture.join();
		time = System.currentTimeMillis() - time;
		return downloads;
	}

	private CompletableFuture<Download> downloadAsync(Source source, Executor threadPool) {
		return CompletableFuture.supplyAsync(() -> {
					try {
						return downloadSync(source);
					} catch (IOException e) {
						throw new IllegalStateException(e);
					}
				}
				, threadPool
		);
	}

	private static final int IN_MEMORY_THRESHOLD = 10 * 1024;//10 KiB

	private Download downloadSync(Source source) throws IOException {
		long start = System.currentTimeMillis();
		DeferredFileOutputStream out = new DeferredFileOutputStream(IN_MEMORY_THRESHOLD, "downloadAsync", ".tmp", null);
		try {
			if (argv.isDebug()) {
				System.out.println(String.format("Start '%s' in thread '%s'", source.getUrl(), Thread.currentThread().getName()));
			}
			InputStream in = new URL(source.getUrl()).openStream();
			IOUtils.copy(in, out);
			return new Download(source, out, System.currentTimeMillis() - start);
		} finally {
			closeQuietly(out);
			if (argv.isDebug()) {
				System.out.println(String.format("Complete '%s' in thread '%s'", source.getUrl(), Thread.currentThread().getName()));
			}
		}
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
		closeQuietly(stream);
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
