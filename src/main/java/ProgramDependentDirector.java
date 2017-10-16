import org.opendaylight.p4plugin.p4info.proto.Action;
import org.opendaylight.p4plugin.p4info.proto.P4Info;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

public class ProgramDependentDirector {
    private P4Info p4Info;
    private File file;
    private static ProgramDependentDirector director = new ProgramDependentDirector();
    private ProgramDependentDirector() {}
    public static ProgramDependentDirector getInstance() {
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
        p4Info.getActionsList()
                .forEach(action -> buffer.append(new ActionMessageGenerater(action, 0).construct()));
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

//    //package xxx.xxx;
//    private String makeProtoPackageInfo() {
//        String[] f = file.getName().split("\\.");
//        StringBuffer buffer = new StringBuffer();
//        Collections.reverse(Arrays.asList(f));
//        Stream.of(f).forEach(v->buffer.append(v).append("."));
//       return new String(buffer).substring(0, buffer.length() - 1) +  "\n";
//    }

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
