import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

//
// YodafyServidorConcurrente
//
public class YodafyServidorConcurrente {

	public static void main(String[] args) {
	
		// Puerto de escucha
		int port=8989;
		// array de bytes auxiliar para recibir o enviar datos.
		byte []buffer=new byte[256];
		// Número de bytes leídos
		int bytesLeidos=0;

		ServerSocket serverSocket = null;
		
		Socket socketServicio = null;
		
		try {
			// Abrimos el socket en modo pasivo, escuchando el en puerto indicado por "port"
			//////////////////////////////////////////////////
				serverSocket = new ServerSocket(port);
			//////////////////////////////////////////////////
			
			// Mientras ... siempre!
			do {
				
				// Aceptamos una nueva conexión con accept()
				/////////////////////////////////////////////////
				socketServicio = serverSocket.accept();
				System.out.println("Conexión con cliente aceptada");
				//////////////////////////////////////////////////
				
				// Creamos una hebra para la nueva conexión/solicitud
				ProcesadorYodafy processor = new ProcesadorYodafy(socketServicio);
				System.out.println("Hebra creada, ejecutando hebra...");
				processor.run();
				System.out.println("Ejecución hebra terminada. ¿Sigue viva? " + processor.isAlive());
				
			} while (true);
			
		} catch (IOException e) {
			System.err.println("Error al escuchar en el puerto "+port);
		}

	}

}
