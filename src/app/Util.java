/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package app;


import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.IOUtils;
/**
 *
 * @author pedro.ardila
 */
public class Util  {
    
 
   
   /**Interfaz de usuario donde se muestra la imagen de la huella*/
   //private PantallaPrincipal ui;

   /** Indica si la plantilla o template debe ser extraída automáticamente*/
   private boolean autoExtract = true;

   /** Contiene localmente los datos de la huella capturada */
   private ByteArrayInputStream fingerprintData;
  
   /**Contiene la longitud del dato de la huella*/
   private int fingerprintDataLength;
  
   /** La imagen de la última huella digital capturada. */
  // private FingerprintImage fingerprint;
  
   /** La plantilla de la última imagen de huella capturada */
   DPFPTemplate key=null;
   
   private String cedulaId;

    private  Map<DPFPTemplate,Long> mHuellas;
    private  Map<DPFPTemplate,Long> mHuellasTom;
    
   
    
    public void generarMapaBusqueda(ResultSet rsIdentificar){
        
        System.out.println("a gen mapa de busqueda");
        
        this.mHuellas=new HashMap<DPFPTemplate,Long>();
       
                
        try {
           while(rsIdentificar.next()){
               //Lee la plantilla de la base de datos
               byte []templateBuffer =rsIdentificar.getBytes("HUELHUEL");
               
               InputStream templateString=rsIdentificar.getBinaryStream("HUELHUEL");
               
               ByteArrayOutputStream buffer = new ByteArrayOutputStream();
               int nRead;
               byte[] data = new byte[16384];
               
               try{
                    while ((nRead = templateString.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }

                    buffer.flush();
                    
                   
               }
               catch(Exception e){}
               
               byte []templateBuffer2=buffer.toByteArray();
               
               System.out.println("cadena  "+templateBuffer.length+"   "+buffer.toByteArray().length);
            //   byte templateBuffer[] = stringTemplate.getBytes();
               Long cedula=rsIdentificar.getLong("huelpers");
               System.out.println("CEdula "+cedula);
               DPFPTemplate templateTmp = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer2);
               //templateTmp.deserialize(templateBuffer);
               this.mHuellas.put(templateTmp, cedula);
               
               
              
               
           }
       } catch (SQLException ex) {
           System.out.println("Este error");
           Logger.getLogger("Error aqui "+Util.class.getName()).log(Level.SEVERE, null, ex);
       }
        
    }
    
    public Long buscarHuella(DPFPFeatureSet huellaVer,DPFPVerification Verificador){
        System.out.println("A buscar");
        Long cedulaBus=new Long(0);
        
        if(huellaVer!=null && Verificador!=null)
            System.out.println("Parametros adecuados");
         
                
        try {
           //compara las plantilas (actual vs bd)
           
           
           Iterator it =  mHuellas.keySet().iterator();
           int i=0;
           while(it.hasNext()){
                    DPFPTemplate key = (DPFPTemplate)it.next();
                    
                    //coinciden = fingerprintSDK.verify(template,key);+
                    
                    DPFPVerificationResult resultado=Verificador.verify(huellaVer, key);
                    
                    if(resultado.isVerified()){
                        String cedula=mHuellas.get(key).toString();
                        System.out.println("Si Encontrado");
                        System.out.println("Cedula "+cedula);
                    }
                    else{
                        System.out.println("No encontrado");
                    }
                    
                    System.out.println("COmparacion "+i);
                    i++;
                    
                  
            }
         
           
           
           //Si encuentra correspondencia dibuja el mapa
           //e indica el nombre de la persona que coincidió.
       } catch (Exception ex) {
          System.out.println("Problema "+ex);
       }       
        
     
        return cedulaBus;
    }
    
    
     public Long buscarHuellaInd(DPFPFeatureSet huellaVer,DPFPVerification Verificador){
        System.out.println("A buscar");
        Long cedulaBus=new Long(0);
        
        
         
                
        try {
           //compara las plantilas (actual vs bd)
           
                
                 DPFPVerificationResult resultado=Verificador.verify(huellaVer, this.key);
                 
      
                 
                    
                 if(resultado.isVerified()){
                        
                        System.out.println("PErsona verificada ");
                 }
                 else{
                        System.out.println("No encontrado ");
                 }
                
                           
                    
           //Si encuentra correspondencia dibuja el mapa
           //e indica el nombre de la persona que coincidió.
       } catch (Exception ex) {
          System.out.println("Problema "+ex);
       }       
        
     
        return cedulaBus;
    }
    
    public void setTemplate( DPFPTemplate key){
        
        byte[] huellaBytes=key.serialize();
        DPFPTemplate templateTmp = DPFPGlobal.getTemplateFactory().createTemplate(huellaBytes);
        System.out.println("COnversinon");
        this.key=templateTmp;
        
        
    }
     
    public long cantidadHuellas(){
        return mHuellas.size();
    }
    
    public String getCedulaId(){
        return this.cedulaId;
    }
    
    public void setCedulaId(){
        this.cedulaId="NN";
    }
    
   
}
