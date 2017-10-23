import org.opendaylight.p4plugin.p4info.proto.MatchField;

import java.util.Arrays;
import java.util.stream.Stream;

public abstract class AbstractMessageGenerator<T> {
    protected T element;
    protected String indent;

    public AbstractMessageGenerator(T element, int indentCount) {
        this.element = element;
        this.indent = getIndent(indentCount);
    }

    public String getIndent(int indentCount) {
        String indent = "";
        for(int i = 0; i < indentCount; i++) {
            indent += " ";
        }
        return indent;
    }

    protected final String capitalizeFirstLetter(String name) {
        char[] cs = name.toCharArray();
        if ((cs[0] >= 'a' && cs[0] <= 'z') || (cs[0] >= 'A' && cs[0] <= 'Z')) {
            cs[0] -= 32;
        } else {
            throw new IllegalArgumentException("Invalid word = " + name
                    + " when capitalize the first letter.");
        }
        return String.valueOf(cs);
    }

    public final String construct() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(makeTitle())
                .append(makeField())
                .append(makeEnding());
        return new String(buffer);
    }

    protected final String makeTitle() {
        return indent + "message " + makeName() + " {" + "\n";
    }

    protected abstract String makeName();
    protected abstract String makeField();

    protected final String makeEnding() {
        return indent + "}" + "\n\n";
    }

    public int getIndex(int bitwidth) {
        return bitwidth / 32 > 2 ? 2 : bitwidth / 32;
    }

    /**
     * Encoding rules
     */
    protected String getEncodedType(MatchField.MatchType matchType, int bitwidth) {
        String[][] rules = {
                {}, //UNSPECIFIED
                {}, //VALID
                {"uint32", "uint64", "bytes"},
                {"LPM32", "LPM64", "LPM"},
                {"Ternary32", "Ternary64", "Ternary"},
                {"Range32", "Range64", "Range"}};
        return rules[matchType.getNumber()][getIndex(bitwidth)];
    }

    protected String stripBrackets(String input) {
        StringBuffer buffer = new StringBuffer();
        for(char c : input.toCharArray()) {
            if (c == '[' || c == ']') {
                continue;
            }
            buffer.append(c);
        }
        return new String(buffer);
    }

    protected String stripDollorSign(String input) {
        StringBuffer buffer = new StringBuffer();
        for(char c : input.toCharArray()) {
            if (c == '$') {
                continue;
            }
            buffer.append(c);
        }
        return new String(buffer);
    }
}
