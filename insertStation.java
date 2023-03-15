import java.sql.*;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.File;
import java.util.Properties;
import java.util.Scanner;
import java.text.ParseException;

public class insertStation {
    public static void main(String[] args) throws IOException, ParseException {

        String inputfile = "./station_list.txt";
        File queries = new File(inputfile);
        Scanner queryScanner = new Scanner(queries);
        String query = "";
        String responseQuery = "";              
        String station = "";
        int train_num;
        String url = "jdbc:postgresql://localhost:5432/ticket_booking";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "*******");
        props.setProperty("ssl", "false");
        try (Connection conn = DriverManager.getConnection(url, props);) {
            while (queryScanner.hasNextLine()) {
                query = queryScanner.nextLine();
                if (query.charAt(0) == '#')
                    break;
                StringTokenizer tokenizer = new StringTokenizer(query);
                station = tokenizer.nextToken();
                train_num = Integer.parseInt(tokenizer.nextToken());
                java.sql.Date arrival_date = java.sql.Date.valueOf(tokenizer.nextToken());
                java.sql.Time arrival_time = java.sql.Time.valueOf(tokenizer.nextToken());
                java.sql.Date depart_date = java.sql.Date.valueOf(tokenizer.nextToken());
                java.sql.Time depart_time = java.sql.Time.valueOf(tokenizer.nextToken());

                do {
                    try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO Stations VALUES (?,?,?,?,?,?)")) {
                        stmt.setString(1, station);
                        stmt.setInt(2, train_num);
                        stmt.setDate(3, arrival_date);
                        stmt.setTime(4, arrival_time);
                        stmt.setDate(5, depart_date);
                        stmt.setTime(6, depart_time);
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
