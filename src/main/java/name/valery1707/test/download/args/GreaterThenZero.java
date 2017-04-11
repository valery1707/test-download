package name.valery1707.test.download.args;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;

public class GreaterThenZero implements IValueValidator<Number> {
	@Override
	public void validate(String name, Number value) throws ParameterException {
		if (value.doubleValue() <= 0.0) {
			throw new ParameterException("Value for parameter '" + name + "' must be greater than zero.");
		}
	}
}
