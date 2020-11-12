package pknu.it;

import java.sql.*;
import java.util.Scanner;

public class StudentBrowser {
    public static void main(String[] args) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");

            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@db.pknu.ac.kr:1521:xe", "db201512117", "201512117");
            Statement stmt = con.createStatement();

            String sql = "select * from (enrol e join student s on e.sno=s.sno) join course c on e.cno=c.cno";
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("검색할 학생의 학번을 입력하세요!");
            Scanner sc = new Scanner(System.in);
            int sno = sc.nextInt();

            while (rs.next()) {
                String cno = rs.getString("cno");
                String cname = rs.getString("cname");
                String grade = rs.getString("grade");

                if (rs.getInt("sno") == sno)
                    System.out.println("\n과목번호: " + cno + "\n과목이름: " + cname + "\n성적: " + grade);
            }

            rs.close();
            stmt.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
