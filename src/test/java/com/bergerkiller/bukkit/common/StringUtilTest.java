package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.bergerkiller.bukkit.common.utils.StringUtil;

/**
 * Tests some functions in the StringUtil class
 */
public class StringUtilTest {

    @Test
    public void testConvertArgs() {
        // Standard usage in escaping arguments
        testConvertArgs(Arrays.asList("hello", "world"), Arrays.asList("hello", "world"));
        testConvertArgs(Arrays.asList("\"hello", "world\""), Arrays.asList("hello world"));
        testConvertArgs(Arrays.asList("\"\"hello", "world\""), Arrays.asList("\"hello world"));
        testConvertArgs(Arrays.asList("hello\"", "world"), Arrays.asList("hello\"", "world"));
        testConvertArgs(Arrays.asList("hello\"\"", "world"), Arrays.asList("hello\"\"", "world"));
        testConvertArgs(Arrays.asList("\"\"hello\"\"", "world"), Arrays.asList("\"hello\"", "world"));

        // Mid-string, pre-string and post-string escaping
        testConvertArgs(Arrays.asList("hello", "pre\"\"\"", "world"), Arrays.asList("hello", "pre\"", "world"));
        testConvertArgs(Arrays.asList("hello", "pre\"\"\"post", "world"), Arrays.asList("hello", "pre\"post", "world"));
        testConvertArgs(Arrays.asList("hello", "\"\"\"post", "world"), Arrays.asList("hello", "\"post", "world"));

        // Special handling only quotes in a single argument
        // Unescaped
        testConvertArgs(Arrays.asList("hello\"\"\"", "world"), Arrays.asList("hello\"", "world"));
        testConvertArgs(Arrays.asList("hello", "\"", "world"), Arrays.asList("hello", " world"));
        testConvertArgs(Arrays.asList("hello", "\"\"", "world"), Arrays.asList("hello", "", "world"));
        testConvertArgs(Arrays.asList("hello", "\"\"\"", "world"), Arrays.asList("hello", "\"", "world"));
        testConvertArgs(Arrays.asList("hello", "\"\"\"\"", "world"), Arrays.asList("hello", "\" world"));
        testConvertArgs(Arrays.asList("hello", "\"\"\"\"\"", "world"), Arrays.asList("hello", "\"", "world"));
        testConvertArgs(Arrays.asList("hello", "\"\"\"\"\"\"", "world"), Arrays.asList("hello", "\"\"", "world"));
        // Escaped
        testConvertArgs(Arrays.asList("\"hello\"\"\"", "world\""), Arrays.asList("hello\" world"));
        testConvertArgs(Arrays.asList("\"hello", "\"", "world"), Arrays.asList("hello ", "world"));
        testConvertArgs(Arrays.asList("\"hello", "\"\"", "world"), Arrays.asList("hello \"", "world"));
        testConvertArgs(Arrays.asList("\"hello", "\"\"\"", "world\""), Arrays.asList("hello \" world"));
        testConvertArgs(Arrays.asList("\"hello", "\"\"\"\"", "world"), Arrays.asList("hello \"", "world"));
        testConvertArgs(Arrays.asList("\"hello", "\"\"\"\"\"", "world"), Arrays.asList("hello \"\"", "world"));
        testConvertArgs(Arrays.asList("\"hello", "\"\"\"\"\"\"", "world\""), Arrays.asList("hello \"\" world"));

        // When parity is incorrect, check the last bit is committed anyway
        testConvertArgs(Arrays.asList("\"hello", "world"), Arrays.asList("hello world"));
        testConvertArgs(Arrays.asList("hello", "\"world"), Arrays.asList("hello", "world"));
        testConvertArgs(Arrays.asList("\"hello\"", "\"world"), Arrays.asList("hello", "world"));
    }

    private void testConvertArgs(List<String> input, List<String> expectedOutput) {
        LinkedList<String> result = StringUtil.convertArgsList(input);
        boolean equal = true;
        if (result.size() != expectedOutput.size()) {
            equal = false;
        } else {
            for (int i = 0; i < expectedOutput.size(); i++) {
                if (!expectedOutput.get(i).equals(result.get(i))) {
                    equal = false;
                    break;
                }
            }
        }
        if (!equal) {
            System.err.println("Input:    [" + StringUtil.join(" | ", input) + "]");
            System.err.println("Expected: [" + StringUtil.join(" | ", expectedOutput) + "]");
            System.err.println("But was:  [" + StringUtil.join(" | ", result) + "]");
            fail("Result of input [" + StringUtil.join(" | ", input) + "] is not correct");
        }
    }
}
