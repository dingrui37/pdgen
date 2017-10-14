import com.google.protobuf.TextFormat;
import com.sun.org.apache.xpath.internal.SourceTree;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.opendaylight.p4plugin.p4info.proto.Action;
import org.opendaylight.p4plugin.p4info.proto.MatchField;
import org.opendaylight.p4plugin.p4info.proto.P4Info;

import javax.xml.bind.SchemaOutputResolver;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * Created by dingrui ding.rui@zte.com.cn on 10/13/17.
 */
public class pdgen {
    public static void main(String[] args) throws Exception {
        CommandLineParser parser  = new BasicParser();
        Options options = new Options();
        options.addOption("h", "help", false, "Print usage.");
        options.addOption("f", "p4-info", true, "P4Info proto file.");
        options.addOption("p", "path", true, "Generated file path.");
        CommandLine commandLine = parser.parse(options, args);
        String protoFile = null;
        String outPutPath = null;

        if (commandLine.hasOption('h')) {
            System.out.println("-h --help: Print help message.");
            System.exit(0);
        }

        if (commandLine.hasOption('f')) {
            protoFile = commandLine.getOptionValue('f');
            File f = new File(protoFile);
            if (!f.exists()) {
                System.out.println("File = " + protoFile + " not found.");
                System.exit(0);
            }
        }

        if (commandLine.hasOption('p')) {
            outPutPath = commandLine.getOptionValue('p');
            File f = new File(outPutPath);
            System.out.println(f.exists());
            System.out.println(f.isDirectory());
            if (!f.exists()) {
                if (!f.isDirectory()) {
                    System.out.println("outPutPath = " + outPutPath + "invalid.");
                    System.exit(0);
                } else {
                    f.mkdir();
                }
            }
        }

        P4Info p4Info = new pdgen().parseRuntimeInfo(protoFile);
        p4Info.getActionsList().forEach(action -> System.out.println(new ActionMessageGenerater(action, 5).construct()));
        //System.out.println(new ActionMessageGenerater(p4Info.getActions(1), 5).generate());

//        p4Info.getActionsList().forEach(action -> {
//            System.out.println("message " + getActionName(action.getPreamble().getName()) + " {");
//            int paramSeq = 1;
//            for (Action.Param param : action.getParamsList()) {
//                String type;
//                String name;
//                if (param.getBitwidth() >= 1 && param.getBitwidth() < 32) {
//                    type = "uint32";
//                } else {
//                    type = "uint64";
//                }
//                name = param.getName().replaceAll("\\.","_");
//
//                System.out.println("  " + type + " " + name + " = " + paramSeq++ + ";");
//            }
//            System.out.println("}");
//            System.out.println();
//        });
//
//        p4Info.getTablesList().forEach(table -> {
//            System.out.println("message " + getTableName(table.getPreamble().getName()) + " {");
//            System.out.println("  message Match {");
//            int matchFiledSeq = 1;
//
//            for(MatchField matchField : table.getMatchFieldsList()) {
//                String type = null;
//                if (matchField.getBitwidth() >= 1 && matchField.getBitwidth() < 32) {
//                    type = "uint32";
//                } else if (matchField.getBitwidth() >= 32 && matchField.getBitwidth() < 64) {
//                    type = "uint64";
//                }
//
//                String name = matchField.getName().replaceAll("\\.", "_");
//                System.out.println("    " + type + " " + name + " = " + matchFiledSeq++ + ";");
//            }
//            System.out.println("  }");
//            System.out.println("  Match match = 1;");
//            System.out.println("  message Action {");
//            System.out.println("}");
//            System.out.println();
//        });
        System.out.println("proFile = " + protoFile);
        System.out.println("outPutPath = " + outPutPath);
    }

    static private String getTableName(String tableName) {
        String[] splitedTableName = tableName.split("_");
        String result;
        if (splitedTableName.length > 1) {
            StringBuffer buffer = new StringBuffer();
            Arrays.stream(splitedTableName).map(name -> {
                if (!name.equals("")) {
                    char[] cs = name.toCharArray();
                    cs[0] -= 32;
                    return String.valueOf(cs);
                }
                return name;
            }).forEach(buffer::append);
            result = new String(buffer) + "Entry";
        } else {
            result = tableName + "Entry";
        }
        return result;
    }

    static private String getActionName(String actionName) {
        String[] splitedActionName = actionName.split("_");
        String result;
        if (splitedActionName.length > 1) {
            StringBuffer buffer = new StringBuffer();
            Arrays.stream(splitedActionName).map(name -> {
                if (!name.equals("")) {
                    char[] cs = name.toCharArray();
                    cs[0] -= 32;
                    return String.valueOf(cs);
                }
                return name;
            }).forEach(buffer::append);
            result = new String(buffer);
        } else {
            result = actionName;
        }
        return result;
    }

    public P4Info parseRuntimeInfo(String file) throws IOException {
        if (file != null) {
            Reader reader = null;
            P4Info.Builder info = P4Info.newBuilder();
            try {
                reader = new FileReader(file);
                TextFormat.merge(reader, info);
                return info.build();
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }
        return null;
    }
}
