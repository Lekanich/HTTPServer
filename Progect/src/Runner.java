import helper.FieldOption;
import helper.FieldOptionConstats;
import server.HTTPServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sasha
 * Date: 25.08.13
 * Time: 16:35
 * To change this template use File | Settings | File Templates.
 */
public class Runner {

    private static void run(String[] args){
        int port;
        try {
            if (args.length > 1) {
                port = Integer.parseInt(args[0]);
            } else {
                List<FieldOption> list = readOptionsFromFile();
                FieldOption fo = FieldOption.getFirstFieldOption(list, FieldOptionConstats.OPTION_PORT);
                if(fo==null) throw new Exception();
                port = (Integer)fo.getOption();
            }
            new HTTPServer(port).run();
        } catch (Exception e) {
            System.out.println("Something errors with options");
            System.exit(-1);
        }
    }

    private static List<FieldOption> readOptionsFromFile() throws Exception {
        //read option from the file
        File file = new File(".");
        BufferedReader reader = new BufferedReader(new FileReader(new File(HTTPServer.fileIni)));
        List<FieldOption> listOption = new ArrayList<FieldOption>();
        String line = reader.readLine();
        FieldOption fo;
        while (line != null) {
            fo = FieldOption.getInstance(line);
            if (fo != null)
                listOption.add(fo);
            line = reader.readLine();
        }
        if (listOption.size() < 2) {
            throw new Exception();
        }
        return listOption;
    }

    public static void main(String[] args) {
        Runner.run(args);
    }
}
