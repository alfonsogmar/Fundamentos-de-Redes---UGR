//
// YodafyServidorIterativo
// (CC) jjramos, 2012
//
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

// Añadir?
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

//
// Nota: si esta clase extendiera la clase Thread, y el procesamiento lo hiciera el método "run()",
// ¡Podríamos realizar un procesado concurrente! 
//
public class ProcesadorYodafy {
	// Referencia a un socket para enviar/recibir las peticiones/respuestas
	private Socket socketServicio;
	// stream de lectura (por aquí se recibe lo que envía el cliente)
	private InputStream inputStream;
	// stream de escritura (por aquí se envía los datos al cliente)
	private OutputStream outputStream;

	// AÑADIDO
	// Si se opta por hacerse con las clases PrintWriter y BufferedReader
	private PrintWriter outPrinter;
	private BufferedReader inReader;


	// Para que la respuesta sea siempre diferente, usamos un generador de números aleatorios.
	private Random random;
	
	// Constructor que tiene como parámetro una referencia al socket abierto en por otra clase
	public ProcesadorYodafy(Socket socketServicio) {
		this.socketServicio=socketServicio;
		random=new Random();
	}
	
	
	// Aquí es donde se realiza el procesamiento realmente:
	void procesa(){
/*
		// Como máximo leeremos un bloque de 1024 bytes. Esto se puede modificar.
		byte [] datosRecibidos=new byte[1024];
		int bytesRecibidos=0;
		
		// Array de bytes para enviar la respuesta. Podemos reservar memoria cuando vayamos a enviarka:
		byte [] datosEnviar;
*/
		String cadenaRecibida;
		String cadenaEnviar;
		
		
		try {
			// Obtiene los flujos de escritura/lectura
/*
			inputStream=socketServicio.getInputStream();
			outputStream=socketServicio.getOutputStream();
*/
			// AÑADIDO
			outPrinter = new PrintWriter(socketServicio.getOutputStream(),true);
			inReader = new BufferedReader(new InputStreamReader(socketServicio.getInputStream()));
			
			// Lee la frase a Yodaficar:
			////////////////////////////////////////////////////////
			// read ... datosRecibidos.. (Completar)
			//bytesRecibidos = inputStream.readLine(datosRecibidos);
			cadenaRecibida = inReader.readLine();
			////////////////////////////////////////////////////////
			
			// Yoda hace su magia:
			// Yoda reinterpreta el mensaje:
			cadenaEnviar = yodaDo(cadenaRecibida);

			
			// Enviamos la traducción de Yoda:
			////////////////////////////////////////////////////////
			// ... write ... datosEnviar... datosEnviar.length ... (Completar)
			//outputStream.write(datosEnviar,0,datosEnviar.length);
			outPrinter.println(cadenaEnviar);
			////////////////////////////////////////////////////////
			
			
			
		} catch (IOException e) {
			System.err.println("Error al obtener los flujos de entrada/salida.");
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
