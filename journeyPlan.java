import java.sql.*;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.util.Properties;
import java.util.Scanner;
import java.text.ParseException;

public class journeyPlan {
    public static void main(String[] args) throws IOException, ParseException {

        String inputfile = "./station.txt";
        String outputfile = "./plan.txt";
        File queries = new File(inputfile);
        FileWriter filewriter = new FileWriter(outputfile);
        Scanner queryScanner = new Scanner(queries);
        String query = "";
        Array responseArray;
        String responseQuery = "";
        int error = 0;
        String source = "";
        String destination = "";
        String url = "jdbc:postgresql://localhost:5432/ticket_booking";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "******");
        props.setProperty("ssl", "false");
        try (Connection conn = DriverManager.getConnection(url, props);) {
            while (queryScanner.hasNextLine()) {
                query = queryScanner.nextLine();
                if (query.charAt(0) == '#')
                    break;
                StringTokenizer tokenizer = new StringTokenizer(query);
                source = tokenizer.nextToken();
                destination = tokenizer.nextToken();
                do {
                    error=0;
                    try (CallableStatement cstmt = conn.prepareCall("{? = call check_ifPossible(?,?) }")) {
                        cstmt.registerOutParameter(1, Types.ARRAY);
                        cstmt.setString(2, source);
                        cstmt.setString(3, destination);
                        cstmt.execute();
                        responseArray = cstmt.getArray(1);
                        responseQuery = responseArray.toString();
                        filewriter.write(responseQuery+"\n");
                        // System.out.println("Inserted successfully");
                    } catch (SQLException e) {
                        // System.out.println("Couldn't connect to database");
                        System.out.println(e);
                        error = 1;
                    }
                } while (error == 1);
            }
            System.out.println("Connected successfully");
        } catch (SQLException e) {
            // System.out.println("Couldn't connect to database");
            System.out.println(e);
        }
        queryScanner.close();
        filewriter.close();
    }
}
