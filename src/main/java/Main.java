import com.google.protobuf.TextFormat;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.opendaylight.p4plugin.p4info.proto.P4Info;

import java.io.*;

/**
 * Created by dingrui on 10/16/17.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        CommandLineParser parser  = new BasicParser();
        Options options = new Options();
        options.addOption("h", "help", false, "Print usage.");
        options.addOption("f", "file", true, "P4Info proto file.");
        options.addOption("o", "output", true, "Generated file path.");
        CommandLine commandLine = parser.parse(options, args);

        if (commandLine.hasOption('h')) {
            printHelpMessage();
            System.exit(0);
        }

        if (!commandLine.hasOption('f') || !commandLine.hasOption('o')) {
            printHelpMessage();
            System.exit(0);
        }

        String protoFile = commandLine.getOptionValue('f');
        File pFile = new File(protoFile);
        if (!pFile.exists()) {
            System.out.println("File = " + protoFile + " not found.");
            System.exit(0);
        }

        String outputPath = commandLine.getOptionValue('o');
        File oFile = new File(outputPath);
        if (!oFile.exists()) {
            if (!oFile.isDirectory()) {
                System.out.println("OutPut path = " + outputPath + "invalid.");
                System.exit(0);
            } else {
                oFile.mkdir();
            }
        }

        ProtoFileGenerateDirector director = ProtoFileGenerateDirector.getInstance();
        director.setFile(pFile);
        director.setP4Info(parseRuntimeInfo(protoFile));
        File f = new File(outputPath + "/output.proto");
        PrintStream printStream = new PrintStream(f);
        printStream.println(director.construct());
        printStream.close();
    }

    static private P4Info parseRuntimeInfo(String file) throws IOException {
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

    static private void printHelpMessage() {
        System.out.println("-h, --help: Print help message.\n" +
                           "-f, --file: P4Info proto file.\n" +
                           "-o, --output: Generated file path.\n");
    }
}
