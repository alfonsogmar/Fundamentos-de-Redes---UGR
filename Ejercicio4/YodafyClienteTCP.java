//
// YodafyServidorIterativo
// (CC) jjramos, 2012
//
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;

public class YodafyClienteTCP {

	public static void main(String[] args) {
		
		byte []buferEnvio;
		byte []buferRecepcion=new byte[256];
		int bytesLeidos=0;
		
		// Nombre del host donde se ejecuta el servidor:
		String host="localhost";
		// Puerto en el que espera el servidor:
		int port=8989;
		
		// Socket para la conexión UDP
		DatagramSocket socket=null;
		
		// Dirección IP
		InetAddress address;

		// Paquetes o datagramas UDP
		DatagramPacket paquete_enviar, paquete_recibido;
		
		try {
			// Creamos un socket que se conecte a "host" y "port":
			//////////////////////////////////////////////////////
			socket = new DatagramSocket();
			//////////////////////////////////////////////////////			
			
			// Datos que vamos a enviar
			buferEnvio="Al monte del volcán debes ir sin demora".getBytes();

			// Obtenemos dirección
			address = InetAddress.getByName(host);

			// Creamos paquete...
			paquete_enviar = new DatagramPacket(buferEnvio, buferEnvio.length, address, port);

			// ...y lo mandamos
			socket.send(paquete_enviar);


			// Generamos el paquete donde recibirá los datos
			paquete_recibido = new DatagramPacket(buferRecepcion, buferRecepcion.length);
			
			socket.receive(paquete_recibido);
			bytesLeidos = paquete_recibido.getData().length;
			
			
			// Mostremos la cadena de caracteres recibidos:
			System.out.println("Recibido: ");
			for(int i=0;i<bytesLeidos;i++){
				System.out.print((char)buferRecepcion[i]);
			}
			
			// Una vez terminado el servicio, cerramos el socket
			//////////////////////////////////////////////////////
			socket.close();
			//////////////////////////////////////////////////////
			
			// Excepciones:
		} catch (UnknownHostException e) {
			System.err.println("Error: Nombre de host no encontrado.");
		} catch (IOException e) {
			System.err.println("Error de entrada/salida al abrir el socket.");
		}
	}
}
