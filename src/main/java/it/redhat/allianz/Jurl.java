package it.redhat.allianz;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import static com.mashape.unirest.http.Unirest.get;

public class Jurl {

    public static void main(String[] args) throws Exception {

        parseArguments(args);
        String pwd = getPasswordFor(user);

        HttpResponse<JsonNode> jsonResponse = get(uri)
                .header("accept", "application/json")
                .basicAuth("admin", pwd)
                .asJson();
        System.out.println(jsonResponse.getBody());

    }

    private static void parseArguments(String[] args) {
        CommandLineParser parser = new DefaultParser();

        Options options = new Options();
        options.addOption( "h", "help", false, "print this message" );

        options.addOption(Option.builder("m").longOpt("method")
                .desc("use GET, PUT, POST or DELETE method (default: GET)")
                .hasArg()
                .argName("METHOD")
                .build());

        options.addOption(Option.builder("u").longOpt("user")
                .desc("admin user (default: admin)")
                .hasArg()
                .argName("USER")
                .build());

        options.addOption(Option.builder("k").longOpt("karaf-home")
                .desc("KARAF_HOME directory, needed to read the etc/users.properties file")
                .hasArg()
                .argName("KARAF-HOME")
                .build());

        //args = new String[]{ "--help 3", "-m POST", "http://httpbin.org/post" };
        //args = new String[]{"-m POST", "http://httpbin.org/ip" };

        try {
            final CommandLine line = parser.parse( options, args );

            if (line.hasOption("method")) {
                method = line.getOptionValue("method");
            }

            if (line.hasOption("karaf-home")) {
                karaf_home = line.getOptionValue("karaf-home");
            }

            if (line.hasOption("user")) {
                user = line.getOptionValue("user");
            }

            if(line.hasOption( "help" )) {
                help(options);
            } else {

                List<String> arguments = line.getArgList();
                if (arguments.size() == 0) {
                    System.out.println("URI is not defined");
                    help(options);
                } else {
                    uri = arguments.get(0);
                    System.out.println("Connecting to " + uri);
                }
            }
        }
        catch( ParseException exp ) {
            System.out.println( "Unexpected exception:" + exp.getMessage() );
            help(options);
        }
    }

    private static void help(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("jurl <URI>", options);
        System.exit(1);
    }

    private static String getPasswordFor(String user) throws Exception {

        String pwd  ="admin";
        BufferedReader br = new BufferedReader(new FileReader(karaf_home + "/etc/users.properties"));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(user)) {
                    String restOfLine = line.substring(line.indexOf("=") + 1);
                    pwd  = (restOfLine.split(",")[0]);
                }
            }

        return pwd;
    }

    private static String user = "admin";
    private static String karaf_home="/opt/fuse";
    private static String method = "GET";
    private static String uri = "";

}
