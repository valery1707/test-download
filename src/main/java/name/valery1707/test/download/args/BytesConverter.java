package name.valery1707.test.download.args;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;

public class BytesConverter implements IStringConverter<Long> {
	private static final List<Character> TYPES = unmodifiableList(asList(
			'k',
			'm',
			'g',
			't',
			'p'
	));
	private static final Pattern PATTERN = Pattern.compile(
			"^" +                                                                   //Start
			"(\\d+)" +                                                              //Base
			"([" + TYPES.stream().map(Object::toString).collect(joining()) + "])?" +//Multiply suffix
			"$"                                                                     //End
			, Pattern.CASE_INSENSITIVE);
	private static final Map<Character, Long> MULTIPLIER = TYPES.stream()
			.flatMap(t -> Stream.of(t, Character.toUpperCase(t)))
			.collect(Collectors.toMap(Function.identity(), t ->
					(long) Math.pow(
							Character.isUpperCase(t) ? 1024 : 1000,
							TYPES.indexOf(Character.toLowerCase(t)) + 1
					)
			));

	@Override
	public Long convert(String value) {
		Matcher matcher = PATTERN.matcher(value);
		if (!matcher.matches()) {
			throw new ParameterException("Couldn't convert \"" + value + "\"");
		}
		Long base = Long.parseLong(matcher.group(1));
		String type = matcher.group(2);
		Long multiplier = type != null ? MULTIPLIER.get(type.charAt(0)) : 1L;
		return base * multiplier;
	}
}
