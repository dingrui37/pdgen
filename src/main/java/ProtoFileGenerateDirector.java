import org.opendaylight.p4plugin.p4info.proto.Action;
import org.opendaylight.p4plugin.p4info.proto.P4Info;

import java.io.File;
import java.util.Optional;

public class ProtoFileGenerateDirector {
    private P4Info p4Info;
    private File file;
    private static ProtoFileGenerateDirector director = new ProtoFileGenerateDirector();
    private ProtoFileGenerateDirector() {}
    public static ProtoFileGenerateDirector getInstance() {
        return director;
    }

    public void setP4Info(P4Info p4Info) {
        this.p4Info = p4Info;
    }

    public void setFile(File file) {
        this.file = file;
    }
    public String construct() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(makeSyntaxInfo())
                .append(makeJavaPackageInfo())
                .append(makeJavaMultipleFilesInfo());

        buffer.append("message LPM {\n")
                .append("  bytes value = 1;\n")
                .append("  uint32 prefix_len = 2;\n")
                .append("}\n\n");

        buffer.append("message LPM32 {\n")
                .append("  uint32 value = 1;\n")
                .append("  uint32 prefix_len = 2;\n")
                .append("}\n\n");

        buffer.append("message LPM64 {\n")
                .append("  uint64 value = 1;\n")
                .append("  uint32 prefix_len = 2;\n")
                .append("}\n\n");

        buffer.append("message Ternary {\n")
                .append("  bytes value = 1;\n")
                .append("  bytes mask = 2;\n")
                .append("}\n\n");

        buffer.append("message Ternary32 {\n")
                .append("  uint32 value = 1;\n")
                .append("  uint32 mask = 2;\n")
                .append("}\n\n");

        buffer.append("message Ternary64 {\n")
                .append("  uint64 value = 1;\n")
                .append("  uint64 mask = 2;\n")
                .append("}\n\n");

        p4Info.getActionsList()
                .forEach(action -> buffer.append(new ActionMessageGenerator(action, 0).construct()));
        p4Info.getTablesList()
                .forEach(table -> buffer.append(new TableEntryMessageGenerator(table, 0).construct()));
        return new String(buffer);
    }

    //syntax = "proto3";
    private String makeSyntaxInfo() {
        return "syntax = \"proto3\";\n";
    }

    //option java_package = ""; //Need to modify.
    private String makeJavaPackageInfo() {
        return "option java_package = \"\";//Need to modify.\n";
    }

    //option java_multiple_files = true;
    private String makeJavaMultipleFilesInfo() {
        return "option java_multiple_files = true;\n";
    }

    public Action getAction(int actionId) {
        Optional<Action> actionContainer =
                p4Info.getActionsList()
                        .stream()
                        .filter(action -> action.getPreamble().getId() == actionId)
                        .findFirst();
        Action result = null;
        if (actionContainer.isPresent()) {
            result = actionContainer.get();
        }
        return result;
    }

}
