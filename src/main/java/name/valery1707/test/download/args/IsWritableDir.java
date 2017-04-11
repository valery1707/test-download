package name.valery1707.test.download.args;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;

import java.io.File;

public class IsWritableDir implements IValueValidator<File> {
	@Override
	public void validate(String name, File value) throws ParameterException {
		if (!value.exists() || !value.isDirectory() || !value.canWrite()) {
			throw new ParameterException("Value for parameter '" + name + "' must be exists and writable directory.");
		}
	}
}
