package it.sevenbits.web.service;

/**
 * Created by awemath on 7/9/15.
 */


import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CommonFieldValidator {

    /** Email exists pattern */
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE
    );
    /** Pattern for whitespaces */
    private static final String WHITESPACE_PATTERN = "\\s+";

    /**
     * Validate whether value is not null and empty or contains only spaces, otherwise reject it
     *
     * @param value  Value of field
     * @param errors Map for errors
     * @param field  Rejected field name
     * @param key    Rejected message key
     */
    public void isNotNullOrEmpty(
            final String value,
            final Map<String, String> errors,
            final String field,
            final String key
    ) {
        if (!errors.containsKey(field)) {
            if (value == null) {
                errors.put(field, key);
            } else if (value.isEmpty()) {
                errors.put(field, key);
            } else if (value.matches(WHITESPACE_PATTERN)) {
                errors.put(field, key);
            }
        }
    }

    /**
     * Validate whether value is valid email, otherwise reject it
     *
     * @param value  Value of field
     * @param errors Map for errors
     * @param field  Rejected field name
     * @param key    Rejected message key
     */
    public void isEmail(final String value, final Map<String, String> errors, final String field, final String key) {
        if (value != null && !errors.containsKey(field)) {
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(value);
            if (!matcher.find()) {
                errors.put(field, key);
            }
        }
    }

    /**
     * Validate, whether value is too long
     *
     * @param value     Value of field
     * @param maxLength Length allowed
     * @param errors    Map for errors
     * @param field     Rejected field name
     * @param key       Rejected message key
     */
    public void shorterThan(
            final String value,
            final Integer maxLength,
            final Map<String, String> errors,
            final String field,
            final String key
    ) {
        if (value != null && !errors.containsKey(field)) {
            if (value.length() > maxLength) {
                errors.put(field, key);
            }
        }
    }

    public void isNum(
            final String value,
            final Map<String, String> errors,
            final String field,
            final String key
    ){
        char c;
        char buf[] = value.toCharArray();
        for(int i=0;i<value.length();i++) {
            c=buf[i];
            if ((c<'0' || c>'9') && c!='-') {
                errors.put(field, key);
            }
        }
    }

    public void isPositiveNum(
            final String value,
            final Map<String, String> errors,
            final String field,
            final String key
    ){
        char buf[] = value.toCharArray();
        if(value.length()>0) {
            if (buf[0] == '-' || Double.valueOf(value) <= 0) {
                errors.put(field, key);
                return;
            }
        }
    }



}

