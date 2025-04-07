import java.sql.*;
import java.util.Scanner;

public class usepreparestatementFMS {
    static Scanner scan=new Scanner(System.in);
    public static void main(String[] args) {
        String url="jdbc:mysql://localhost:3306/flight_db";
        String username="root";
        String password="root";
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded");
            Connection con = DriverManager.getConnection(url,username,password);
            System.out.println("conection established sucess");
            while(true){
                System.out.println("****** Flight Management System *****");
                System.out.println("1: Register the user");
                System.out.println("2: Searching for the flight");
                System.out.println("3: Booking a flight");
                System.out.println("4: cancel Booking");
                System.out.println("5: Veiw the boking");
                System.out.println("6: Exit");

                System.out.println("enter your choices: ");

                int choice=scan.nextInt();
                scan.nextLine();
                Statement stmt= con.createStatement();
                switch (choice){
                    case 1:
                        registeruser(con);
                        break;
                    case  2:
                        searchingflight(con);
                        break;
                    case 3:
                        bookingflight(con);
                        break;
                    case 4:
                        cancelbokking(con);
                        break;
                    case 5:
                        viewbooking(con);
                        break;
                    case 6:
                        System.out.println("Exit");
//                        stmt.close();
                        con.close();
                        return;
                    default:
                        System.out.println("invalid choice... try again");
                }

            }

        }catch(ClassNotFoundException | SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static void viewbooking(Connection con) throws SQLException {
        System.out.println("enter the userID");
        int user=scan.nextInt();
        String query="select * from booking where user_id= ?";
        PreparedStatement pstmt=con.prepareStatement(query);
        pstmt.setInt(1,user);
        ResultSet rs= pstmt.executeQuery(query);
        while(rs.next()){
            System.out.println("Booking ID: "+rs.getInt("booking_id")+"  "+"Flight" +
                    "Id:"+rs.getInt("flight_id")+"   "+"Seat number :"+rs.getInt("seat_number")+
                    "  "+"Status :"+rs.getString("status"));
        }


    }

    private static void cancelbokking(Connection con) throws SQLException {
        System.out.println("eneter the user id  to cancel the flight");
        int user=scan.nextInt();
        String query="update booking set status='CANCELLED' where user_id=?";
        PreparedStatement pstmt=con.prepareStatement(query);
        pstmt.setInt(1,user);
        int row=pstmt.executeUpdate(query);
        if(row>0){
            System.out.println("you flight booking is cancelled");
            String cancel="update flights set no_of_seats = no_of_seats+1 where flight_id= "+
                    "(select flight_id from booking where user_id=?)";
            pstmt=con.prepareStatement(cancel);
            pstmt.setInt(1,user);
            int r=pstmt.executeUpdate(cancel);
        }
    }

    private static void bookingflight(Connection con) throws SQLException {
        System.out.println("enter the userID");
        int user=scan.nextInt();
        System.out.println("eneter the flights id");
        int flight=scan.nextInt();
        String check="select no_of_seats from flights where flight_id=?";
        PreparedStatement pstmt= con.prepareStatement(check);
        pstmt.setInt(1,flight);
        ResultSet rs= pstmt.executeQuery(check);
        if(rs.next() && rs.getInt("no_of_seats")>0){
            int seatnumber=rs.getInt("no_of_seats");
            String insert="insert into booking (user_id,flight_id,seat_number)values(?,?,?)";
            pstmt= con.prepareStatement(insert);
            pstmt.setInt(1,user);
            pstmt.setInt(2,flight);
            pstmt.setInt(3,seatnumber);
            int x=pstmt.executeUpdate(insert);


            String update="update flights set no_of_seats=no_of_seats-1 where flight_id=?";
            pstmt= con.prepareStatement(update);
            pstmt.setInt(1,flight);
            int y=pstmt.executeUpdate(update);
            System.out.println("you flight booking sucessfully");
        }else{
            System.out.println("booking is not conformed ");
        }

    }

    private static void searchingflight(Connection con) throws SQLException {
        System.out.println("enter the departure");
        String departure=scan.nextLine();
        System.out.println("enter the destination");
        String destination=scan.nextLine();
        String query="select * from flights where depature=? and destination=? and no_of_seats>0 ";
        PreparedStatement pstmt= con.prepareStatement(query);
        pstmt.setString(1,departure);
        pstmt.setString(2,destination);
        ResultSet rs=pstmt.executeQuery(query);
        System.out.println("-----Available of Flights-------");
        while(rs.next()){
            System.out.println("FLIGHT ID : "+rs.getInt("flight_id")+
                    " , "+"AIRLINE : "+rs.getString("airline")+
                    " , "+"Departure : "+rs.getString("depature")+
                    " , "+"Destination : "+rs.getString("destination")+
                    " , "+"Date : "+rs.getDate("date")+
                    " , "+"number of seats : "+rs.getInt("no_of_seats")+
                    " , "+"Price : "+rs.getInt("price")
            );
        }
    }

    private static void registeruser(Connection con) throws SQLException {
        System.out.println("Enter the Name");
        String name=scan.nextLine();

        System.out.println("enter the Email");
        String email=scan.nextLine();

        System.out.println("enter the nummber");
        String number=scan.nextLine();
        String query="insert into user(name,email_id,number) values(?,?,?)";
        PreparedStatement pstmt= con.prepareStatement(query);
        pstmt.setString(1,name);
        pstmt.setString(1,email);
        pstmt.setString(1,number);
        int x= pstmt.executeUpdate(query);
        System.out.println("register user sucessfully");

    }
}
