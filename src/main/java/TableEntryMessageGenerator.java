import org.opendaylight.p4plugin.p4info.proto.*;

import java.util.Arrays;
import java.util.List;

public class TableEntryMessageGenerator extends AbstractMessageGenerator<Table> {
    public TableEntryMessageGenerator(Table table, int indentCount) {
        super(table, indentCount);
    }

    @Override
    protected String makeName() {
        String tableName = element.getPreamble().getName();
        String[] splitedTableName = tableName.split("_");
        String result;
        if (splitedTableName.length > 1) {
            StringBuffer buffer = new StringBuffer();
            Arrays.stream(splitedTableName).map(name -> {
                if (!name.equals("")) {
                    return capitalizeFirstLetter(name);
                }
                return name;
            }).forEach(buffer::append);
            result = new String(buffer) + "Entry";
        } else {
            result = tableName + "Entry";
        }
        return result;
    }

    @Override
    protected String makeField() {
        return makeMatchMessageDeclare()
                + makeMatchMessageDefine()
                + makeActionMessageDeclare()
                + makeActionMessageDefine();
    }

    private String makeMatchMessageDeclare() {
        return new MatchMessageGenerator(element.getMatchFieldsList(), 2).construct();
    }

    private String makeMatchMessageDefine() {
        return getIndent(2) + "Match match = 1;\n";
    }

    private String makeActionMessageDeclare() {
        return new ActionMessageGenerator(element.getActionRefsList(), 2).construct();
    }

    private String makeActionMessageDefine() {
        return getIndent(2) + "Action action = 2;\n";
    }

    private class MatchMessageGenerator extends AbstractMessageGenerator<List<MatchField>> {
        public MatchMessageGenerator(List<MatchField> matchFileds, int indentCount) {
            super(matchFileds, indentCount);
        }

        @Override
        protected String makeName() {
            return  "Match";
        }

        @Override
        protected String makeField() {
            StringBuffer buffer = new StringBuffer();
            int fieldSeq = 1;
            for(MatchField matchField : element) {
                String type = getEncodedType(matchField.getMatchType(), matchField.getBitwidth());
                System.out.println("test = " + getTest(matchField.getMatchType(), matchField.getBitwidth()) +
                                    " --> " + type);
                String name = makeMatchFiledName(matchField.getName());
                buffer.append(indent)
                        .append("  ")
                        .append(type)
                        .append(" ")
                        .append(name)
                        .append(" = ")
                        .append(fieldSeq++)
                        .append(";\n");
            }
            return new String(buffer);
        }

        private String makeMatchFiledName(String fieldName) {
            return fieldName.replaceAll("\\.", "_");
        }
    }

    private class ActionMessageGenerator extends AbstractMessageGenerator<List<ActionRef>> {
        public ActionMessageGenerator(List<ActionRef> actionRefs, int indentCount) {
            super(actionRefs, indentCount);
        }

        @Override
        protected String makeName() {
            return  "Action";
        }

        @Override
        protected String makeField() {
            StringBuffer buffer = new StringBuffer();
            int fieldSeq = 1;
            for(ActionRef actionRef : element) {
                Integer actionRefId = actionRef.getId();
                String actionName = ProgramDependentDirector.getInstance().getAction(actionRefId).getPreamble().getName();
                String type = makeActionType(actionName);
                buffer.append(indent)
                        .append("  ")
                        .append(type)
                        .append(" ")
                        .append(actionName)
                        .append(" = ")
                        .append(fieldSeq++)
                        .append(";\n");
            }
            return new String(buffer);
        }

        private String makeActionType(String actionName) {
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
    }
}
