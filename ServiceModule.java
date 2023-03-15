//Inayat Kaur - 2020csb1088
//Kushal Aggrawal - 2020csb1096
//Ruchika Sharma - 2020csb1119

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Properties;
import java.sql.*;
import java.util.StringTokenizer;

class QueryRunner implements Runnable {
    // Declare socket for client access
    protected Socket socketConnection;
    protected Connection conn;

    public QueryRunner(Socket clientSocket, Connection connec) {
        this.socketConnection = clientSocket;
        this.conn = connec;
    }

    public void run() {
        try {
            // Reading data from client
            InputStreamReader inputStream = new InputStreamReader(socketConnection
                    .getInputStream());
            BufferedReader bufferedInput = new BufferedReader(inputStream);
            OutputStreamWriter outputStream = new OutputStreamWriter(socketConnection
                    .getOutputStream());
            BufferedWriter bufferedOutput = new BufferedWriter(outputStream);
            PrintWriter printWriter = new PrintWriter(bufferedOutput, true);

            String clientCommand = "";
            String responseQuery = "";
            String queryInput = "";
            // Read client query from the socket endpoint
            clientCommand = bufferedInput.readLine();

            while (!clientCommand.equals("#")) {

                System.out.println("Recieved data <" + clientCommand + "> from client : "
                        + socketConnection.getRemoteSocketAddress().toString());

                StringTokenizer tokenizer = new StringTokenizer(clientCommand);
                queryInput = tokenizer.nextToken();

                int no_of_passengers = Integer.parseInt(queryInput);
                String[] passengers = new String[no_of_passengers];
                for (int i = 0; i < no_of_passengers; i++) {
                    passengers[i] = tokenizer.nextToken();
                    if (i != no_of_passengers - 1)
                        passengers[i] = passengers[i].substring(0, passengers[i].length() - 1);
                }
                int train_num = Integer.parseInt(tokenizer.nextToken());
                String DOJ = tokenizer.nextToken();
                String Class = tokenizer.nextToken();

                do {
                    try (CallableStatement cstmt = conn.prepareCall("{? = call book_ticket( ?,?,?,?,? ) }")) {
                        cstmt.registerOutParameter(1, Types.VARCHAR);
                        cstmt.setInt(2, no_of_passengers);
                        cstmt.setObject(3, passengers);
                        cstmt.setInt(4, train_num);
                        cstmt.setString(5, DOJ);
                        cstmt.setString(6, Class);
                        cstmt.execute();
                        responseQuery = cstmt.getString(1);
                    } catch (SQLException e) {
                        System.out.println(e);
                        responseQuery = "Error occurred";
                    }
                } while (responseQuery == "Error occurred");

                // Sending data back to the client
                printWriter.println(responseQuery);
                // Read next client query
                clientCommand = bufferedInput.readLine();
            }
            inputStream.close();
            bufferedInput.close();
            outputStream.close();
            bufferedOutput.close();
            printWriter.close();
            socketConnection.close();
        } catch (IOException e) {
            return;
        }
    }
}

/**
 * Main Class to controll the program flow
 */
public class ServiceModule {
    // Server listens to port
    static int serverPort = 7008;
    // Max no of parallel requests the server can process
    static int numServerCores = 8;

    // ------------ Main----------------------
    public static void main(String[] args) throws IOException {
        // Creating a thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(numServerCores);

        try (// Creating a server socket to listen for clients
                ServerSocket serverSocket = new ServerSocket(serverPort)) {
            Socket socketConnection = null;

            String url = "jdbc:postgresql://localhost:5432/ticket_booking";
            Properties props = new Properties();
            props.setProperty("user", "postgres");
            props.setProperty("password", "*******");
            props.setProperty("ssl", "false");

            try (Connection conn = DriverManager.getConnection(url, props);) {
                // Always-ON server
                while (true) {
                    System.out.println("Listening port : " + serverPort
                            + "\nWaiting for clients...");
                    socketConnection = serverSocket.accept(); // Accept a connection from a client
                    System.out.println("Accepted client :"
                            + socketConnection.getRemoteSocketAddress().toString()
                            + "\n");
                    // Create a runnable task
                    Runnable runnableTask = new QueryRunner(socketConnection, conn);
                    // Submit task for execution
                    executorService.submit(runnableTask);
                }
            } catch (SQLException e) {
                System.out.println(e);
            }
        }
    }
}
