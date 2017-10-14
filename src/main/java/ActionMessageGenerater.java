import org.opendaylight.p4plugin.p4info.proto.Action;
import java.util.Arrays;

public class ActionMessageGenerater extends AbstractMessageGenerator<Action> {
    public ActionMessageGenerater(Action action, int indentCount) {
        super(action, indentCount);
    }

    private String makeParamType(int paramBitwidth) {
        String type;
        if (paramBitwidth >= 1 && paramBitwidth < 32) {
            type = "uint32";
        } else if (paramBitwidth >= 32 && paramBitwidth < 64) {
            type = "uint64";
        } else {
            type = "bytes";
        }
        return type;
    }

    private String makeParamName(String paramName) {
        return paramName.replaceAll("\\.", "_");
    }

    @Override
    protected String makeName() {
        String actionName = element.getPreamble().getName();
        String[] splitedActionName = actionName.split("_");
        String result;
        if (splitedActionName.length > 1) {
            StringBuffer buffer = new StringBuffer();
            Arrays.stream(splitedActionName).map(name -> {
                if (!name.equals("")) {
                    return capitalizeFirstLetter(name);
                }
                return name;
            }).forEach(buffer::append);
            result = new String(buffer);
        } else {
            result = actionName;
        }
        return result;
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
