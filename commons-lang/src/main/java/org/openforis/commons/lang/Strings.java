package org.openforis.commons.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.openforis.commons.collection.CollectionUtils;
import org.openforis.commons.collection.Predicate;

/**
 * 
 * @author S. Ricci
 *
 */
public class Strings {

	public static String firstNotBlank(String... values) {
		for (String value : values) {
			if (StringUtils.isNotBlank(value)) {
				return value;
			}
		}
		return null;
	}
	
	public static <C extends Collection<String>> String joinNotBlank(C values, String separator) {
		List<String> notBlankValues = filterNotBlank(values);
		String result = StringUtils.join(notBlankValues, separator);
		return result;
	}
	
	public static String textToHtml(String text) {
		String result;
		result = StringUtils.replace(text, "\n", "<br>");
		result = StringUtils.replace(result, "\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
		return result;
	}
	
	public static String htmlToText(String html) {
		String result = StringEscapeUtils.unescapeHtml4(html);
		result = result.replaceAll("<br>", "\n");
		return result;
	}

	public static String[] filterNotBlank(String... texts) {
		List<String> result = filterNotBlank(Arrays.asList(texts));
		return result.toArray(new String[result.size()]);
	}

	private static List<String> filterNotBlank(Collection<String> list) {
		return filterNotBlank(new ArrayList<String>(list));
	}
	
	private static List<String> filterNotBlank(List<String> list) {
		List<String> result = new ArrayList<String>(list);
		CollectionUtils.filter(result, new Predicate<String>() {
			@Override
			public boolean evaluate(String text) {
				return StringUtils.isNotBlank((String) text);
			}
		});
		return result;
	}

}
