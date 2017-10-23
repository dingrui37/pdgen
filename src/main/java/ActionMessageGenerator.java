import org.opendaylight.p4plugin.p4info.proto.Action;

public class ActionMessageGenerator extends AbstractMessageGenerator<Action> {
    public ActionMessageGenerator(Action action, int indentCount) {
        super(action, indentCount);
    }

    private String makeParamType(int paramBitwidth) {
        String[] type = new String[]{"uint32", "uint64", "bytes"};
        return type[getIndex(paramBitwidth)];
    }

    private String makeParamName(String paramName) {
        return stripDollorSign(stripBrackets(paramName.replaceAll("\\.", "_")));
    }

    @Override
    protected String makeName() {
        String actionName = element.getPreamble().getName();
        if (actionName.contains("_")) {
            String[] splitedName = actionName.split("_");
            StringBuffer buffer = new StringBuffer();
            for(String name : splitedName) {
                if (name.equals("")) {
                    continue;
                }
                buffer.append(capitalizeFirstLetter(name));
            }
            return new String(buffer);
        }
        return actionName;
    }

    @Override
    protected String makeField() {
        StringBuffer buffer = new StringBuffer();
        int paramSeq = 1;
        for(Action.Param param : element.getParamsList()) {
            String result = indent + "  "
                    + makeParamType(param.getBitwidth())
                    + " "
                    + makeParamName(param.getName())
                    + " = "
                    + paramSeq++
                    + ";\n";
            buffer.append(result);
        }
        return new String(buffer);
    }
}
