package mispaquetes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ServerAPP {

	static int contadorW = 0;
	static int contadorL = 0;
	static int round = 1;

	/**
	 * 
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {
		int puerto;// puerto remoto
		Scanner sc = new Scanner(System.in);

		try {
			System.out.println("Introduzca puerto: (Default 7500)");
			puerto = Integer.parseInt(sc.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Se usara el Default");
			puerto=7500;
		}
		sc.close();
		ServerSocket servidor = null;
		Socket jugador1 = null;
		Socket jugador2 = null;
		DataInputStream flujoEntrada_1 = null;
		DataInputStream flujoEntrada_2 = null;
		DataOutputStream flujoSalida_1 = null;
		DataOutputStream flujoSalida_2 = null;
		do {
			try {
				servidor = new ServerSocket(puerto);
				System.out.println("Servidor abierto en " + servidor.getInetAddress().getHostAddress() + ":" + servidor.getLocalPort());
				System.out.println("Esperando conexiones...");

				jugador1 = servidor.accept();
				System.out.println("Jugador 1 conectado desde: " + jugador1.getInetAddress().getHostAddress());
				jugador2 = servidor.accept();
				System.out.println("Jugador 2 conectado desde: " + jugador2.getInetAddress().getHostAddress());
				// CREO FLUJO DE ENTRADA AL CLIENTE
				flujoEntrada_1 = new DataInputStream(jugador1.getInputStream());

				// CREO FLUJO DE SALIDA AL CLIENTE
				flujoSalida_1 = new DataOutputStream(jugador1.getOutputStream());

				// CREO FLUJO DE ENTRADA AL CLIENTE
				flujoEntrada_2 = new DataInputStream(jugador2.getInputStream());

				// CREO FLUJO DE SALIDA AL CLIENTE
				flujoSalida_2 = new DataOutputStream(jugador2.getOutputStream());

				Boolean lbAcabado = false;
				String juego1, juego2;

				while (!lbAcabado) {
					// Capturo lo que me manda el Jugador 1
					juego1 = flujoEntrada_1.readUTF();

					// Capturo lo que me manda el Jugador 2
					juego2 = flujoEntrada_2.readUTF();

					// Procesar la jugada y enviar respuesta al cliente
					procesarJugada(juego1, juego2, flujoSalida_1, flujoSalida_2);

					// Miro a ver si el servidor gan� o perdi�

					System.out.println("Jugador 1 va: " + contadorW + " y el Jugador 2: " + contadorL);

					// Compruebo si el servidor gan� o perdi�
					if (contadorL == 3 || contadorW == 3) {
						lbAcabado = true;
					} else {
						System.out.println("---------------");
					}
				}
				System.out.println("---Partida Terminada---");
				System.out.println("(Copyright Sam Kass)");

			} catch (EOFException e) {
				System.out.println("(1)- Nueva Partida");

			} catch (SocketException e) {
				System.out.println("(2)- Servidor ya iniciado");

			}
			// CERRAR STREAMS Y SOCKETS
			try {
			servidor.close();
			
			flujoEntrada_1.close();
			flujoSalida_1.close();
			flujoEntrada_2.close();
			flujoSalida_2.close();
			jugador1.close();
			jugador2.close();
			reiniciarContadores();
			System.out.println("(3)- Play Again");
			// Si no existe servidor CIERRA el programa
			}catch(NullPointerException e){
				System.exit(0);
			}
		} while (servidor.isClosed());
	}

	/**
	 * 
	 * @param eleccion1
	 * @param eleccion2
	 * @param salida1
	 * @param salida2
	 * @throws IOException
	 * @throws EOFException
	 * @throws SocketException
	 */
	public static void procesarJugada(String eleccion1, String eleccion2, DataOutputStream salida1,
			DataOutputStream salida2) throws IOException, EOFException, SocketException {
		int pos = 0;
		// Muestro Ronda y la envio a Jugadores
		String enfrentamiento = "---[Ronda " + round + "]---";
		salida1.writeUTF(enfrentamiento);
		salida2.writeUTF(enfrentamiento);
		System.out.println(enfrentamiento);
		round++;
		// Muestro Elecciones de los Jugadores y mando la del oponente
		salida1.writeUTF(eleccion2);
		salida2.writeUTF(eleccion1);
		System.out.println("Jugador 1 Jugo: " + eleccion1 + " y el Jugador 2 Jugo: " + eleccion2);
		// Compruebo si ganan empatan o pierden, primero envio mensaje de enfrentamiento
		// y despues del resultado.
		if (eleccion1.equals(eleccion2)) {
			enfrentamiento = "HAN EMPATADO";
			salida1.writeUTF(enfrentamiento);
			salida2.writeUTF(enfrentamiento);
			salida1.writeUTF("E");
			salida2.writeUTF("E");
			System.out.println(enfrentamiento);
		} else {
			switch (eleccion1) {
			//
			case "PIEDRA":
				switch (eleccion2) {
				case "PAPEL":
					enfrentamiento = "Papel envuelve piedra";
					pos = 1;
					break;
				case "TIJERAS":
					enfrentamiento = "Piedra aplasta tijeras";
					pos = 0;
					break;
				case "LAGARTO":
					enfrentamiento = "Piedra aplasta lagarto";
					pos = 0;
					break;
				case "SPOCK":
					enfrentamiento = "Spock desintegra piedra";
					pos = 1;
					break;
				}
				break;
			//
			case "PAPEL":
				switch (eleccion2) {
				case "PIEDRA":
					enfrentamiento = "Papel envuelve piedra";
					pos = 0;
					break;
				case "TIJERAS":
					enfrentamiento = "Tijeras cortan papel";
					pos = 1;
					break;
				case "LAGARTO":
					enfrentamiento = "Lagarto se come papel";
					pos = 1;
					break;
				case "SPOCK":
					enfrentamiento = "Papel desaprueba a Spock";
					pos = 0;
					break;
				}
				break;
			//
			case "TIJERAS":
				switch (eleccion2) {
				case "PIEDRA":
					enfrentamiento = "Piedra aplasta tijeras";
					pos = 1;
					break;
				case "PAPEL":
					enfrentamiento = "Tijeras cortan papel";
					pos = 0;
					break;
				case "LAGARTO":
					enfrentamiento = "Tijeras decapitan lagarto";
					pos = 0;
					break;
				case "SPOCK":
					enfrentamiento = "Spock aplasta tijeras";
					pos = 1;
					break;
				}
				break;
			//
			case "LAGARTO":
				switch (eleccion2) {
				case "PIEDRA":
					enfrentamiento = "Piedra aplasta lagarto";
					pos = 1;
					break;
				case "PAPEL":
					enfrentamiento = "Lagarto se come papel";
					pos = 0;
					break;
				case "TIJERAS":
					enfrentamiento = "Tijeras decapitan lagarto";
					pos = 1;
					break;
				case "SPOCK":
					enfrentamiento = "Lagarto envenena a Spock";
					pos = 0;
					break;
				}
				break;
			//
			case "SPOCK":
				switch (eleccion2) {
				case "PIEDRA":
					enfrentamiento = "Spock desintegra piedra";
					pos = 0;
					break;
				case "PAPEL":
					enfrentamiento = "Papel desaprueba a Spock";
					pos = 1;
					break;
				case "TIJERAS":
					enfrentamiento = "Spock aplasta tijeras";
					pos = 0;
					break;
				case "LAGARTO":
					enfrentamiento = "Lagarto envenena a Spock";
					pos = 1;
					break;
				}
				break;
			}
			// Mando mensaje enfrentamiento y lo muestro tambien en Servidor
			salida1.writeUTF(enfrentamiento);
			salida2.writeUTF(enfrentamiento);
			System.out.println(enfrentamiento);
			// Sumo al ganador, ense�o y mando resultado
			if (pos == 0) {
				contadorW++;
				salida1.writeUTF("W");
				salida2.writeUTF("L");
				System.out.println("JUGADOR 1 GANA");
			} else {
				salida1.writeUTF("L");
				salida2.writeUTF("W");
				contadorL++;
				System.out.println("JUGADOR 2 GANA");
			}

		}
		System.out.println("---------------");
	}

	public static void reiniciarContadores() {
		contadorW = 0;
		contadorL = 0;
		round = 1;
	}
}
