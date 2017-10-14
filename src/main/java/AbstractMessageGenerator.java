/**
 * Created by hll on 10/13/17.
 */
public abstract class AbstractMessageGenerator<T> {
    protected T element;
    protected String indent;

    public AbstractMessageGenerator(T element, int indentCount) {
        this.element = element;
        this.indent = getIndent(indentCount);
    }

    private String getIndent(int indentCount) {
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
        return indent + "}" + "\n";
    }
}
