import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

//
// YodafyServidorIterativo
// (CC) jjramos, 2012
//
public class YodafyServidorIterativo {

	public static void main(String[] args) {
	
		// Puerto de escucha
		int port=8989;
		// array de bytes auxiliar para recibir o enviar datos.
		byte []buffer=new byte[256];
		// Número de bytes leídos
		int bytesLeidos=0;

		DatagramSocket serverSocket = null;
		
		DatagramSocket serviceSocket = null;

		DatagramPacket paqueteRecibido;
		
		try {
			// Abrimos el socket en modo pasivo, escuchando el en puerto indicado por "port"
			//////////////////////////////////////////////////
			serverSocket = new DatagramSocket(port);
			//////////////////////////////////////////////////
			
			// Mientras ... siempre!
			do {
				
				// Recibimos paquete
				paqueteRecibido = new DatagramPacket(buffer,buffer.length);
				serverSocket.receive(paqueteRecibido);
				
				// Creamos un objeto de la clase ProcesadorYodafy, pasándole como 
				// argumento el paquete UDP recibido
				ProcesadorYodafy procesador=new ProcesadorYodafy(paqueteRecibido);
				procesador.procesa();
				
			} while (true);
			
		} catch (IOException e) {
			System.err.println("Error al escuchar en el puerto "+port);
		}

	}

}
