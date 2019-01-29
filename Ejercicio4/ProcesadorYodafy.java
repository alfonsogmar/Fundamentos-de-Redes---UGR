//
// YodafyServidorIterativo
// (CC) jjramos, 2012
//
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;

//
// Nota: si esta clase extendiera la clase Thread, y el procesamiento lo hiciera el método "run()",
// ¡Podríamos realizar un procesado concurrente! 
//
public class ProcesadorYodafy {
	// Referencia a un socket UDP para enviar/recibir las peticiones/respuestas
	private DatagramSocket socketServicio;

	// Paquete que enviará la cadena modificada
	private	DatagramPacket paqueteEnviar;

	// Paquete que recibe el servidor
	private DatagramPacket paqueteRecibido;
	
	// Para que la respuesta sea siempre diferente, usamos un generador de números aleatorios.
	private Random random;
	
	// Constructor que tiene como parámetro una referencia al paquete recibido por otra clase
	public ProcesadorYodafy(DatagramPacket recibido) {
		this.paqueteRecibido = recibido;
		random=new Random();
	}
	
	
	// Aquí es donde se realiza el procesamiento realmente:
	void procesa(){
		
		// Como máximo leeremos un bloque de 1024 bytes. Esto se puede modificar.
		byte [] datosRecibidos;//=new byte[1024];
		int bytesRecibidos=0;
		
		// Array de bytes para enviar la respuesta. Podemos reservar memoria cuando vayamos a enviarka:
		byte [] datosEnviar;

		int port;

		InetAddress address;
		
		
		try {

			
			// Obtiene del paquete la frase a Yodaficar y el resto de datos para conectarse al cliente:
			datosRecibidos = paqueteRecibido.getData();
			bytesRecibidos = datosRecibidos.length;
			port = paqueteRecibido.getPort();
			address = paqueteRecibido.getAddress();

			// Yoda hace su magia:
			// Creamos un String a partir de un array de bytes de tamaño "bytesRecibidos":
			String peticion=new String(datosRecibidos,0,bytesRecibidos);
			// Yoda reinterpreta el mensaje:
			String respuesta=yodaDo(peticion);
			// Convertimos el String de respuesta en una array de bytes:
			datosEnviar=respuesta.getBytes();
			
			// Generamos el paquete con los datos que enviaremos
			paqueteEnviar = new DatagramPacket(datosEnviar,datosEnviar.length,address,port);

			// Creamos el socket para conectarnos al cliente
			socketServicio = new DatagramSocket();

			// Enviamos la traducción de Yoda:
			socketServicio.send(paqueteEnviar);
			
			socketServicio.close();
			
		} catch (IOException e) {
			System.err.println("Error al mandar paquete a cliente.");
		}

	}

	// Yoda interpreta una frase y la devuelve en su "dialecto":
	private String yodaDo(String peticion) {
		// Desordenamos las palabras:
		String[] s = peticion.split(" ");
		String resultado="";
		
		for(int i=0;i<s.length;i++){
			int j=random.nextInt(s.length);
			int k=random.nextInt(s.length);
			String tmp=s[j];
			
			s[j]=s[k];
			s[k]=tmp;
		}
		
		resultado=s[0];
		for(int i=1;i<s.length;i++){
		  resultado+=" "+s[i];
		}
		
		return resultado;
	}
}
