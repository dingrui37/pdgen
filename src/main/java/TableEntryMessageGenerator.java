import org.opendaylight.p4plugin.p4info.proto.Table;

import java.util.Arrays;

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
        return null;
    }
}
