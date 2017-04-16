/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package app;


import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPErrorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPErrorEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.capture.event.DPFPSensorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPSensorEvent;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

/**
 *
 * @author pedro.ardila
 */
public class ProcesosHuellero {
    
    private DPFPCapture Lector = DPFPGlobal.getCaptureFactory().createCapture();
    private DPFPEnrollment Reclutador = DPFPGlobal.getEnrollmentFactory().createEnrollment();
    private DPFPVerification Verificador = DPFPGlobal.getVerificationFactory().createVerification();
    private DPFPTemplate template;
    public static String TEMPLATE_PROPERTY = "template";
    public DPFPFeatureSet featureinscripcion;
    public DPFPFeatureSet featureverificacion;
    private static Util util;
    
    DPFPSample sampleGlobal;
    
    private ServerSocket ss = null;
    private Socket clientSocket = null;
    private Socket sock=null;
     
    boolean estadoHuellero=false;
    
    private int funcion=0;
    
    
    public void iniciarHuellero(){
        
        this.util=new Util();
        System.out.println("Utilitarios inicializados"); 
        Conexion conexion=new Conexion();
        boolean respuesta=conexion.ConectarOracle();
                    
       if(respuesta==true)
           System.out.println("Conexión establecida"); 
       else
           System.out.println("Conexión a fallado"); 
            
        conexion.cargarHuellas(util);
        
        System.out.println("Cargadas huellas: "+util.cantidadHuellas()); 
       
               
        Iniciar();
        
        start();
        
        
        
    }
    
    protected void Iniciar(){
            
            Lector.addDataListener(new DPFPDataAdapter(){
                public void dataAcquired(final DPFPDataEvent e){
                    
                            EnviarTexto("Huella Capturada");
                            
                           // if(funcion==0)
                                procesarCaptura(e.getSample());
                            //else
                              //  procesarVerificar(e.getSample());
                            
                            //procesarCaptura(e.getSample());
                        
                    
                    
                   
                }
            });

            Lector.addReaderStatusListener(new DPFPReaderStatusAdapter() {
                @Override
                public void readerConnected(DPFPReaderStatusEvent dpfprs) {
                   
                            EnviarTexto("Sensor de Huella Activado o conectado");
                        
                    
                    
                }

                @Override
                public void readerDisconnected(DPFPReaderStatusEvent dpfprs) {
                    
                            EnviarTexto("Sensor de Huella esta desactivado o no conectado");
                        
                   
                    
                }
            });

            Lector.addSensorListener(new DPFPSensorAdapter(){
                public void fingerTouched(final DPFPSensorEvent e){
                    
                           EnviarTexto("El dedo ha sido colocado sobre el lector de huella");
                        
                    
                    //System.out.println("Dedo colocado");
                }

                public void fingerGone(final DPFPSensorAdapter e){
                   
                            EnviarTexto("El dedo ha sido quitado del lector de huella");
                        
                    
                    //System.out.println("Dedo quitado");
                }
            });

            Lector.addErrorListener(new DPFPErrorAdapter(){
                public void errorReader(final DPFPErrorEvent e){
                   
                            EnviarTexto("Error: "+e.getError());
                        
                    
                    
                }
            });
    }
    
   
    
   
     public DPFPFeatureSet extraerCaracteristicas(DPFPSample sample,DPFPDataPurpose purpose){
        DPFPFeatureExtraction extractor=DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
        try {
            return extractor.createFeatureSet(sample, purpose);
        } catch (DPFPImageQualityException e) {
            return null;
        }
    }
    
    public void procesarCaptura(DPFPSample sample){
        featureinscripcion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);
        //featureverificacion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);
       // System.out.println("Captura a procesar");
         
        if(featureinscripcion!=null){
            try{
                Reclutador.addFeatures(featureinscripcion);

                //DPFPTemplate tempMuestra=Reclutador.getTemplate();

                //util.buscarHuella(featureverificacion,Verificador);
                //util.buscarHuellaInd(tempMuestra,featureverificacion,Verificador);
            }
            catch(DPFPImageQualityException ex){
                System.out.println("Problema "+ex);
            }
            finally{
        
                 EnviarTexto("Las caracteristicas de la huella han sido creadas");
                 EstadoHuellas();
                //Reclutador.addFeatures(featureinscripcion);
                this.sampleGlobal=sample;
               //Image image = CrearImagenHuella(sample);
                   
                
                switch(Reclutador.getTemplateStatus()){
                    case TEMPLATE_STATUS_READY:
                        stop();
                        setTemplate(Reclutador.getTemplate());
                        
                         util.setTemplate(Reclutador.getTemplate());
                         
                         EnviarTexto("La plantilla de la huella ha sido creada, ya puede verificarla o identificarla");
                        //System.out.println("La plantilla de la huella ha sido creada, ya puede verificarla o identificarla");
                          //funcion=1;
                        byte[] huella=Reclutador.getTemplate().serialize();
                        Image imagenHuella = CrearImagenHuella(this.sampleGlobal);
                        
                        responderDatos(huella,imagenHuella);
                   
                         start();
                        
                        break;
                    case TEMPLATE_STATUS_FAILED:
                        Reclutador.clear();
                        stop();
                        EstadoHuellas();
                        setTemplate(null);
                         EnviarTexto("Lectura de huella a fallado.. Inicializando...");
                        start();
                        break;
                }
            }
        }
    }   
    public Image CrearImagenHuella(DPFPSample sample){
        return DPFPGlobal.getSampleConversionFactory().createImage(sample);
    }
    
    
    
    public void EstadoHuellas(){
        //System.out.println("Muestra de Huellas necesarias para Guardar Template: " + Reclutador.getFeaturesNeeded());
        EnviarTexto("Muestra de Huellas necesarias para Guardar Template: " + Reclutador.getFeaturesNeeded());
    }
    
  
    
    public void start(){
        Lector.startCapture();
        EnviarTexto("Utilizando lector de huella");
        //System.out.println("Utilizando lector de huella");
    }
    
    public void stop(){
        Lector.stopCapture();
        EnviarTexto("No se está usando el lector de huella");
        
    }
    
    public DPFPTemplate getTemplate(){
        return template;
    }
    
    public void setTemplate(DPFPTemplate template){
        DPFPTemplate old = this.template;
        this.template = template;
        //firePropertyChange(TEMPLATE_PROPERTY, old, template);
    }
    
     public void EnviarTexto(String string){
        System.out.println(string + "\n");
    }
     
     public void responderDatos(byte[] huella,Image imagenHuella){
        
         System.out.println("Voy a procesar datos metodo "+huella.length);
         
        ByteArrayInputStream streamHuella=new ByteArrayInputStream(huella); 
        Integer tamHuella=huella.length;
        
        BufferedImage bImage = new BufferedImage(imagenHuella.getWidth(null), imagenHuella.getHeight(null), BufferedImage.TYPE_INT_RGB);
        byte[] imageString = imageToString(bImage);
        
        String imagenHuel=imageString.toString();
        
        Object[] objetos=new Object[3];
        objetos[0]=huella;
        objetos[1]=tamHuella;
        objetos[2]=imagenHuel;
        
        try{
          System.out.println("Devuelvo la conexion");
          this.sock = new Socket("apps.akc.co",2100);
          ObjectOutputStream outputStream = new ObjectOutputStream(this.sock.getOutputStream());
          outputStream.writeObject(objetos); 
          sock.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
         
     }
     
     public byte[] imageToString(BufferedImage bImage)   {
        String imageString = null;
        byte[] imageAsRawBytes=null;
        //image to bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, "jpg", baos);
             
            baos.flush();
            imageAsRawBytes = baos.toByteArray();
            baos.close();

            //bytes to string
            imageString = new String(imageAsRawBytes);
        } catch (IOException ex) {
           System.out.println(ex);
        }

        return imageAsRawBytes;
    }
     
     
     public void conectarSocketEspera(){
         
        try{ 
            ServerSocket ss = new ServerSocket(2000);
            Socket clientSocket = new Socket();
            clientSocket=ss.accept();
            
            clientSocket.close();
            ss.close();
            
            start();
            
           
        }
        catch(Exception e){
            System.out.println(e);
        }
         
     }
     
     
     public void procesarVerificar(DPFPSample sample){
        
        featureverificacion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);
        
        System.out.println("Proceso de busqueda");
        
        if(featureverificacion!=null){
            //Long cedula=this.util.buscarHuellaInd(featureverificacion, Verificador);
            Long cedula=this.util.buscarHuellaInd(featureverificacion, Verificador);
            
            
            
            
    }
    
 }   
    
      

    
    
    
}
