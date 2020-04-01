package com.github.chencn.yamlandprops.props2yaml;

/**
 * @author xqchen
 */
class ValueConverter {
    public final static String TRUE_STR = "true";
    public final static String FALSE_STR = "false";

    public static Object asObject(String string) {
        if (TRUE_STR.equalsIgnoreCase(string) || FALSE_STR.equalsIgnoreCase(string)) {
            return Boolean.valueOf(string);
        } else {
            try {
                return Integer.parseInt(string);
            } catch (NumberFormatException ignored) {
            }
            try {
                return Long.parseLong(string);
            } catch (NumberFormatException ignored) {
            }
            try {
                return Double.parseDouble(string);
            } catch (NumberFormatException ignored) {
            }
            return string;
        }
    }
}
