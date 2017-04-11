package name.valery1707.test.download;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class DownloadCli {
	@SuppressWarnings("unused")
	private static final int EXIT_OK = 0;
	private static final int EXIT_ARGS_FAIL = 1;

	public static void main(String[] args) {
		Args argv = parseArgs(args);
		System.out.println("argv.getThreadCount() = " + argv.getThreadCount());
		System.out.println("argv.getSpeedLimit() = " + argv.getSpeedLimit());
		System.out.println("argv.getSourceFile() = " + argv.getSourceFile());
		System.out.println("argv.getTargetDirectory() = " + argv.getTargetDirectory());
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
}
