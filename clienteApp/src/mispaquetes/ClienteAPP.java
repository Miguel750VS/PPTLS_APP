package mispaquetes;


import java.awt.EventQueue;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;


public class ClienteAPP extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = 8670939934650654871L;


/**
 *
 *
 * @throws UnknownHostException
 * @throws IOException
 */
	public static void main(String[] args) throws UnknownHostException, IOException {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				
					ImageIcon icon = new ImageIcon("img/Icon.png");

					ClienteAPP frame = new ClienteAPP();
					frame.setVisible(true);
					frame.setTitle("(C) Miguel v 1.1");
					frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
					frame.setBounds(400, 200, 450, 300);
					frame.setIconImage(icon.getImage());
					int Puerto = 7500;// puerto remoto
					Socket Cliente = null;
					String juego = "";
					int contadorW = 0, contadorL = 0;

					do {
					try {
						String Host="";
						Host = JOptionPane.showInputDialog(frame,
								"Ip del servidor:", "Conexion con Servidor",
								JOptionPane.QUESTION_MESSAGE).toString();//Red publica con puerto abierto / Ipv4 de red local / localhost
						Cliente = new Socket(Host, Puerto);

						// CREO FLUJO DE SALIDA => SERVIDOR
						DataOutputStream flujoSalida = new DataOutputStream(Cliente.getOutputStream());

						// CREO FLUJO DE ENTRADA <= SERVIDOR
						DataInputStream flujoEntrada = new DataInputStream(Cliente.getInputStream());

						boolean lbAcabado = false;
						String entrada = "";
						while (!lbAcabado) {
							do {
								System.out.println();
								// entrada de una cadena
								juego = JOptionPane.showInputDialog(frame,
										"Quieres Jugar ¿PIEDRA, PAPEL, TIJERAS, LAGARTO O SPOCK?", "-_JUEGA PPTLS_-",
										JOptionPane.QUESTION_MESSAGE, new ImageIcon(icon.getImage()), null, "").toString()
										.toUpperCase();
								if (juego.equals("TIJERA")) {

									juego = "TIJERAS";
								}
							} while (!juego.equals("PIEDRA") && !juego.equals("PAPEL") && !juego.equals("TIJERAS")
									&& !juego.equals("LAGARTO") && !juego.equals("SPOCK"));

							// EL JUGADOR 1 ENVIA SU ELECCION
							flujoSalida.writeUTF(juego);

							JOptionPane.showMessageDialog(frame, "Esperando al Rival", "Enter para continuar...",
									JOptionPane.PLAIN_MESSAGE);

							// Capturo lo que me manda el servidor, Primero el numero de Ronda
							entrada = flujoEntrada.readUTF();
							juego = "\t" + entrada + "\n";
							// Capturo lo que me manda el servidor, La jugada contraria para mostrarla en el
							// cliente
							entrada = flujoEntrada.readUTF();
							juego += "\tEl otro Jugador jugo: " + entrada + "\n";
							// Capturo lo que me manda el servidor,El mensaje de enfrentamiento
							entrada = flujoEntrada.readUTF();
							juego += "\t" + entrada + "\n";
							// Capturo lo que me manda el servidor, El resultado
							entrada = flujoEntrada.readUTF();
							juego += "\t---------------\n";

							// Miro a ver si he ganado perdido o empatado
							if (entrada.contains("W")) {
								contadorW++;
								juego += "\tHe ganado. Yo voy: " + contadorW + " y el otro: " + contadorL + "\n";
							}

							else if (entrada.contains("L")) {
								contadorL++;
								juego += "\tHe perdido. Yo voy: " + contadorW + " y el otro: " + contadorL + "\n";
							} else if (entrada.contains("E")) {
								juego += "\tHe empatado. Yo voy: " + contadorW + " y el otro: " + contadorL + "\n";
							}

							// Compruebo si he ganado o perdido
							if (contadorL == 3 || contadorW == 3) {
								lbAcabado = true;
							} else {
								juego += "\t---------------";
								JOptionPane.showMessageDialog(frame, juego, "Resultado", JOptionPane.PLAIN_MESSAGE);
							}

						}
						juego += "\t---Partida Terminada---";
						JOptionPane.showMessageDialog(frame, juego,
								"Resultado Final: " + ((contadorW == 3) ? "Enhorabuena!!" : "Suerte la Proxima vez"),
								JOptionPane.PLAIN_MESSAGE);
						// CERRAR STREAMS Y SOCKETS
						flujoEntrada.close();
						flujoSalida.close();
						Cliente.close();

					} catch (ConnectException e) {
						JOptionPane.showMessageDialog(frame, "El servidor no esta abierto", "Error", JOptionPane.ERROR_MESSAGE);

					} catch (SocketException e) {
						JOptionPane.showMessageDialog(frame,
								"Tiraron del Cable o la sala esta Ocupada, Espere la siguiente Partida", "Error",
								JOptionPane.ERROR_MESSAGE);

					} catch (EOFException e) {
						JOptionPane.showMessageDialog(frame,
								"Tiraron del Cable o la sala esta Ocupada, Espere la siguiente Partida", "Error",
								JOptionPane.ERROR_MESSAGE);

					} catch (NullPointerException e) {
						try {
							Cliente.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NullPointerException e1) {
							JOptionPane.showMessageDialog(frame, "El servidor no esta abierto", "Error", JOptionPane.ERROR_MESSAGE);
						}
						System.exit(0);
					}catch(UnknownHostException e) {
						JOptionPane.showMessageDialog(frame, "Esa ip no corresponde a ningun servidor", "Error", JOptionPane.ERROR_MESSAGE);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}while(true);
			}
		});
	}// main



}
