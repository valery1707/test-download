package name.valery1707.test.download.args;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;

import java.io.File;

public class IsReadableFile implements IValueValidator<File> {
	@Override
	public void validate(String name, File value) throws ParameterException {
		if (!value.exists() || !value.isFile() || !value.canRead()) {
			throw new ParameterException("Value for parameter '" + name + "' must be exists and readable file.");
		}
	}
}
