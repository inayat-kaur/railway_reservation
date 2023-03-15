# Railway Reservation
This is a project to handle ticket booking for trains. It is designed to handle large number of requests at the same time using concurrency and multithreading.
Also, one can plan a journey from a given station to some other station.

To use the application,
1. Create a postgresql database ticket_booking.
2. Run all the queries of queries.txt file.
3. Change password in insertStation.java, journeyPlan.java, admin.java and ServiceModule.java
4. Now compile all the java files.
5. Add trains in the trains.txt file and then run admin.class
6. Add your inputs for booking to Input folder and run ServiceModule.class
7. Now, run client.java in another terminal.
8. Output is obtained in Output folder for each client.
9. Add stations in station_list.txt and run insertStations.class
10. Add the source and destination in stations.txt
11. Run journeyPlan.java and output is given in plan.txt
