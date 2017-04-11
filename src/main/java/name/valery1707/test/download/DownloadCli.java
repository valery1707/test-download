package name.valery1707.test.download;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DownloadCli {
	@SuppressWarnings("unused")
	private static final int EXIT_OK = 0;
	private static final int EXIT_ARGS_FAIL = 1;

	public static void main(String[] args) throws IOException {
		Args argv = parseArgs(args);
		System.out.println("argv.getThreadCount() = " + argv.getThreadCount());
		System.out.println("argv.getSpeedLimit() = " + argv.getSpeedLimit());
		System.out.println("argv.getSourceFile() = " + argv.getSourceFile());
		System.out.println("argv.getTargetDirectory() = " + argv.getTargetDirectory());
		DownloadCli cli = new DownloadCli(argv);
		cli.download();
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

	public DownloadCli(Args argv) {
		this.argv = argv;
	}

	private void download() throws IOException {
		Map<String, Set<String>> source = SourceLoader.load(argv.getSourceFile());
		System.out.println("Will download:");
		source.forEach((url, files) ->
				System.out.println("  From '" + url + "' into " + files.stream().collect(Collectors.joining(", ", "[", "]")))
		);
	}
}
