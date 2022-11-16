import java.sql.*;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.File;
import java.util.Properties;
import java.util.Scanner;
import java.text.ParseException;

public class admin {
    // ------------ Main----------------------
    public static void main(String[] args) throws IOException, ParseException {

        String inputfile = "./admin.txt";
        File queries = new File(inputfile);
        Scanner queryScanner = new Scanner(queries);
        String query = "";
        String responseQuery = "";
        int train_num, AC_coaches, Sleeper_coaches;
        String DOJ = "";
        String url = "jdbc:postgresql://localhost:5432/ticket_booking";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "avneet2004");
        props.setProperty("ssl", "false");
        try (Connection conn = DriverManager.getConnection(url, props);) {
            while (queryScanner.hasNextLine()) {
                query = queryScanner.nextLine();
                if (query.charAt(0) == '#')
                    break;
                StringTokenizer tokenizer = new StringTokenizer(query);
                train_num = Integer.parseInt(tokenizer.nextToken());
                DOJ = tokenizer.nextToken();
                java.sql.Date date = java.sql.Date.valueOf(DOJ);
                AC_coaches = Integer.parseInt(tokenizer.nextToken());
                Sleeper_coaches = Integer.parseInt(tokenizer.nextToken());

                do {
                    try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO train_release VALUES (?,?,?,?)")) {
                        stmt.setInt(1, train_num);
                        stmt.setInt(2, AC_coaches);
                        stmt.setInt(3, Sleeper_coaches);
                        stmt.setDate(4, date);
                        stmt.executeUpdate();
                        // System.out.println("Inserted successfully");
                    } catch (SQLException e) {
                        // System.out.println("Couldn't connect to database");
                        System.out.println(e);
                        responseQuery = "Error occurred";
                    }
                } while (responseQuery == "Error occurred");
            }
            System.out.println("Connected successfully");
        } catch (SQLException e) {
            // System.out.println("Couldn't connect to database");
            System.out.println(e);
        }
        queryScanner.close();
    }
}