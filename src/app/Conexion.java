/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author pedro.ardila
 */
public class Conexion {
    
    private String dirip;
    private Connection conexion;
    private String IpLocal="";
    
     private PreparedStatement identificarStmt;
    private  ResultSet rsIdentificar ;
    
    public Conexion()
    {
            
    }
    public Connection getConexion()
    {
        return this.conexion;
    }
           
    
    
    public void getCommit(){
        try {
            this.conexion.commit();
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     public boolean ConectarOracle()
    {
        boolean respuesta=true;  
        
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            respuesta=false;
        }
        
        //System.out.println("Inicio conexión base de datos");
        try{
 
            String urll;
            urll = "jdbc:oracle:thin:@10.0.0.81:1521:kpitallb";
            Connection conexion=DriverManager.getConnection(urll,"AKCWWW","lrc_SpekFA6");
            if(conexion != null){
                
                this.conexion=conexion;
                this.identificarStmt=this.conexion.prepareStatement("Select * from pow_huellas  ");
            }
            else
                respuesta=false;
            
            
            } catch(SQLException s){
                s.printStackTrace();
                respuesta=false;
 
            }
        
        return respuesta;
    }
     
     public void cerrarConexion(){
         try{
            this.conexion.close();
         }
         catch(Exception e){}
     }
     
    
   
     
     
    public void cargarHuellas(Util util){
        try {
            System.out.println("Inicio de cargar huellas");
            this.rsIdentificar = identificarStmt.executeQuery();
            
            util.generarMapaBusqueda(this.rsIdentificar);
            
            System.out.println("Mapa de huellas generado");
            
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
     
    /* public void identificarPersona(Util util) {
       try {
           //Obtiene todas las huellas de la bd
           System.out.println("idnetificacion en conexion");
           
           boolean coinciden=false;
                     
           //Si se encuentra el nombre en la base de datos
        //   while(rsIdentificar.next()){
               //Lee la plantilla de la base de datos
             //  byte templateBuffer[] = rsIdentificar.getBytes("huelhuel");
               //Crea una nueva plantilla
               
//               coinciden=util.identificacion(templateBuffer);
           coinciden=util.identificacion();
               //Template referenceTemplate = new Template(templateBuffer);
                
               //compara las plantilas (actual vs bd)
              // boolean coinciden = fingerprintSDK.verify(template,referenceTemplate);
                
               //Si encuentra correspondencia dibuja el mapa 
               //e indica el nombre de la persona que coincidió.
               if (coinciden){                   
                   //ui.showImage(GrFingerJava.getBiometricImage(template, fingerprint, fingerprintSDK));                                  
                   //JOptionPane.showMessageDialog(ui, "La huella es de "+rsIdentificar.getString("huenombre"));
                   //return;
                   System.out.println("LAs huellas son las indicadas");
                   //break;
                   
               }
               
                
          // } 
           
           
           if(coinciden==false){
               System.out.println("Personal no encontrada");
           }
            
           //Si no encuentra alguna huella que coincida lo indica con un mensaje
           //JOptionPane.showMessageDialog(ui, "No existe ningún registro que coincida con la huella.");
                
      // } catch (SQLException e) {
          // e.printStackTrace();           
       } catch (Exception e) {
           e.printStackTrace();           
       }
   }*/
    
         
}   
    
    

