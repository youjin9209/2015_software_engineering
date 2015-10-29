import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;


public class studentManagement extends JFrame implements ActionListener{
   
   Connection conn; //DB Connection 
   Statement  stat;
   ResultSet rs = null;
   
   JTabbedPane tabpane; //Create the tab
   Button insertInfoBtn, deleteBtn, updateBtn, viewBtn;
   //updatedPhone number
   TextField updatedPhone;
   
   //To insert id,name,department,phone   
   TextField insertId, insertName, insertDepartment, insertPhone; 
   
   //searchToDeleteBtn id to delete,to update,to view
   TextField searchIdToDelete, searchIdToUpdate, searchIdToView;
   
   //search id button to delete, search id button to update  
   Button searchToDeleteBtn, searchToUpdateBtn;
   JPanel addStudent;

   String index[] = {"ID","NAME","DEPARTMENT","PHONE"}; //table index create
   DefaultTableModel listmodel = new DefaultTableModel(index,0); //create table model
   JTable studentList = new JTable(listmodel); //view table create
   
   TextField informToDelete, informToUpdate;

   public studentManagement(){

      super("Student Management Program");
      tabpane = new JTabbedPane();

      addStudent = new JPanel();
      JPanel deleteStudent = new JPanel();
      JPanel updateStudent = new JPanel();
      JPanel viewStudent = new JPanel();

      
      //UI Component
      Panel IDpanel, NamePanel, DeptPanel, PhonePanel, IDpanel2, IDpanel3, phonePanel2;
      Label stID, stName, stDept, stPhone, stID2, stID3,newPhone, stID4;

      //UI Components Creation

      IDpanel = new Panel();
      NamePanel = new Panel();
      DeptPanel = new Panel();
      PhonePanel = new Panel();
      
      stID = new Label("Student ID");
      stName = new Label("Name");
      stDept = new Label("Department");
      stPhone = new Label("Phone number");

      /*
       *  INSERT STUDENT ID,NAME,DEPARTMENT,PHONE IN HERE
        */
      insertId = new TextField(20);
      insertName = new TextField(20);
      insertDepartment = new TextField(20);
      insertPhone = new TextField(20);

      
      IDpanel.add(stID);
      IDpanel.add(insertId);
      NamePanel.add(stName);
      NamePanel.add(insertName);
      DeptPanel.add(stDept);
      DeptPanel.add(insertDepartment);
      PhonePanel.add(stPhone);
      PhonePanel.add(insertPhone);

      addStudent.setLayout(new GridLayout(5,1));
      addStudent.add(IDpanel);
      addStudent.add(NamePanel);
      addStudent.add(DeptPanel);
      addStudent.add(PhonePanel);

      insertInfoBtn = new Button("ADD");
      addStudent.add(insertInfoBtn);

      /*
       *  DELETE STUDENT IN HERE BY SEARCHING ID
        */
      deleteStudent.setLayout(new BorderLayout(5, 5));
      IDpanel2 = new Panel();
      stID2 = new Label("Student ID");
      
      searchIdToDelete = new TextField(20); //put id here to delete
      searchToDeleteBtn = new Button("SEARCH"); 
      IDpanel2.add(stID2);
      IDpanel2.add(searchIdToDelete);
      IDpanel2.add(searchToDeleteBtn);

      informToDelete = new TextField("Please, Search Id to delete");   
      deleteBtn = new Button("DELETE");
      deleteBtn.setSize(300,10);
      deleteStudent.add(IDpanel2,BorderLayout.NORTH);
      deleteStudent.add(informToDelete, BorderLayout.CENTER);
      deleteStudent.add(deleteBtn, BorderLayout.SOUTH);

      /*
       *  UPDATE STUDENT PHONENUMBER IN HERE BY SEARCHING ID 
        */
      updateStudent.setLayout(new BorderLayout(5, 5));
      IDpanel3 = new Panel();
      stID3 = new Label("Student ID");
      searchIdToUpdate = new TextField(20); //id to update 
      searchToUpdateBtn = new Button("SEARCH");
      IDpanel3.add(stID3);
      IDpanel3.add(searchIdToUpdate);
      IDpanel3.add(searchToUpdateBtn);

      informToUpdate = new TextField("Please, Search Id to Update Phone Number");   
      phonePanel2 = new Panel();
      newPhone = new Label("New Phone Number");
      updatedPhone = new TextField(20); //phone number to update
      updateBtn = new Button("UPDATE");
      phonePanel2.add(newPhone);
      phonePanel2.add(updatedPhone);
      phonePanel2.add(updateBtn);

      updateStudent.add(IDpanel3,BorderLayout.NORTH);
      updateStudent.add(informToUpdate,BorderLayout.CENTER);
      updateStudent.add(phonePanel2,BorderLayout.SOUTH);

      /*
       *  VIEW STUDENT INFORMATION IN HERE BY SEARCHING ID 
        */
      JPanel searchInfo = new JPanel();
      stID4 = new Label("Student ID");
      searchIdToView = new TextField(20); // id to searchToDeleteBtn 
      viewBtn = new Button("SEARCH");
      searchInfo.add(stID4);
      searchInfo.add(searchIdToView);
      searchInfo.add(viewBtn);


      JScrollPane scroll = new JScrollPane(studentList);
      scroll.setSize(17,20);
      viewStudent.add(searchInfo,BorderLayout.NORTH);
      viewStudent.add(scroll,BorderLayout.SOUTH);
      
      addStudent.setBackground(Color.white);
      deleteStudent.setBackground(Color.white);
      updateStudent.setBackground(Color.white);
      viewStudent.setBackground(Color.white);

      tabpane.addTab("Add New Student", addStudent);
      tabpane.addTab("Delete Student", deleteStudent);
      tabpane.addTab("Update Student's Phone Number", updateStudent);
      tabpane.addTab("View Student's Information", viewStudent);

      insertInfoBtn.addActionListener(this);
      deleteBtn.addActionListener(this);
      searchToDeleteBtn.addActionListener(this);
      searchToUpdateBtn.addActionListener(this);
      updateBtn.addActionListener(this);
      viewBtn.addActionListener(this);

      getContentPane().add(tabpane, BorderLayout.CENTER);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setSize(700,400);
      setVisible(true);
      
      dbconnectionCheck();  //check db connected and toast message
      
   }


   @Override
   public void actionPerformed(ActionEvent e) {
      //Event Perform
      if(e.getSource() == insertInfoBtn){
         addStudentInfo();
      }else if(e.getSource()== deleteBtn){
         deleteStudentById();
      }else if(e.getSource()== viewBtn){
         viewStudentById(searchIdToView.getText().trim());
      }else if(e.getSource()== searchToDeleteBtn){
         searchId(searchIdToDelete.getText().trim(),informToDelete," exists, you can delete!");
      }else if(e.getSource()== searchToUpdateBtn){
         searchId(searchIdToUpdate.getText().trim(),informToUpdate," exists, please update phone number!");
      }else if(e.getSource()== updateBtn){
         updateStudentById(searchIdToUpdate.getText().trim(),updatedPhone.getText().trim());
      }

   }

   public void initialize(){

   }


   void dbconnectionCheck(){
      //DB Connection check
      try {
         //JDBC Driver -> DriverManager
         Class.forName("com.mysql.jdbc.Driver").newInstance();
         String url = "jdbc:mysql://127.0.0.1:3306/sook?";

         //get Connection from Driver
         conn = DriverManager.getConnection(url, "root", "apmsetup");
         //get Statement from Connection Object
         stat = conn.createStatement();
         initialize();
         System.out.println("DB Connect!!");
      } catch (Exception e) {
         e.printStackTrace(System.out);
      }
   }


   void addStudentInfo(){
      try{
         String id = insertId.getText().trim();
         String name = insertName.getText().trim();
         String department = insertDepartment.getText().trim();
         String phone = insertPhone.getText().trim();
            
         if( id == null || name == null || department == null || insertPhone == null ||
               id.length() == 0 || name.length() == 0 ||department.length() == 0 || phone.length() == 0)
            return ;

         String sql = "insert into student values(?,?,?,?)";
         PreparedStatement stat=conn.prepareStatement(sql);
         stat.setString(1, id);
         stat.setString(2, name);
         stat.setString(3, department );
         stat.setString(4, phone);
         stat.executeUpdate();
         System.out.println("student data insert success");
      }catch(Exception ex){

      }

   }

   void deleteStudentById(){

      try{
         String id = searchIdToDelete.getText().trim();
         if(id == null || id.length() == 0)
            return ;
         
         //data delete query
         stat.executeUpdate("delete from student where id=' " + id + " ' ");
         System.out.println("student data delete success ");   
      }catch(Exception e1){

      }
   }

   void viewStudentById(String id){

      String info[] = new String[4];
      info[0]= null;
      info[1] = null;
      info[2] =null;
      info[3] = null;
      DefaultTableModel model=(DefaultTableModel) studentList.getModel();

      try {
         String sql = "select * from student where id=' " + id + " ' ";
         PreparedStatement p = conn.prepareStatement(sql);
         ResultSet resultset = p.executeQuery(); 

         while(resultset.next()){
            info[0] = resultset.getString("id");
            info[1] = resultset.getString("name");
            info[2] = resultset.getString("department");
            info[3] = resultset.getString("phone");
            
            model.addRow(info);
            
         }
      }catch(Exception e1){

      }   

}


void searchId(String id,TextField t,String message){
      
      try {
         String sql = "select name from student where id=' " + id + " ' ";
         PreparedStatement p = conn.prepareStatement(sql);
         ResultSet resultset = p.executeQuery(); 
         String update = "ID "+id+" not exists";
         while(resultset.next()){
            if(resultset.getString("name") != null){
               update= "ID "+ id + message;
            }
         }
         t.setText(update);
      }catch(Exception e1){

      }   

   }


   void updateStudentById(String id,String phone){
      try{

         String sql = "update student set phone = ? where id='"+id+"'";
         PreparedStatement stat=conn.prepareStatement(sql);
         stat.setString(1, phone);
         stat.executeUpdate();

      }catch(Exception ex){

      }

   }

   public static void main(String args[]) {

      studentManagement test = new studentManagement();

   }

}