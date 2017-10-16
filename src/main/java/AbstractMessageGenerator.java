import org.opendaylight.p4plugin.p4info.proto.MatchField;

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

    /**
     * Encoding rules
     */
    protected final String getEncodedType(MatchField.MatchType matchType, int bitwidth) {
        String type = null;
        switch(matchType) {
            case LPM: {
                type = getLpmEncodedType(bitwidth);
                break;
            }

            case EXACT: {
                type = getExactEncodedType(bitwidth);
                break;
            }

            case TERNARY: {
                type = getTernaryEncodedType(bitwidth);
                break;
            }

            case RANGE: {
                type = getRangeEncodedType(bitwidth);
                break;
            }
            default: throw new IllegalArgumentException("Invalid match type, type = " + matchType + ".");
        }
        return type;
    }

    private String getExactEncodedType(int bitwidth) {
        String[] type = new String[]{"uint32", "uint64", "bytes"};
        return type[getIndex(bitwidth)];
    }

    private String getLpmEncodedType(int bitwidth) {
        String[] type = new String[]{"LPM32", "LPM64", "LPM"};
        return type[getIndex(bitwidth)];
    }

    private String getTernaryEncodedType(int bitwidth) {
        String[] type = new String[]{"Ternary32", "Ternary64", "Ternary"};
        return type[getIndex(bitwidth)];
    }

    private String getRangeEncodedType(int bitwidth) {
        String[] type = new String[]{"Range32", "Range64", "Range"};
        return type[getIndex(bitwidth)];
    }

    private int getIndex(int bitwidth) {
        return bitwidth / 32 > 2 ? 2 : bitwidth / 32;
    }
}
