import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	private static final int BASIC_SERVER_PORT = 10001;
	private static final int EXPERT_SERVER_PORT = 10002;

	private static Operacao operacao;
	private static Scanner scan = new Scanner(System.in);
	
	private static Socket socket;
	private static ObjectInputStream objectInputStream;
	private static ObjectOutputStream objectOutputStream;

	public static void main(String[] args) {

		int porta;
		String sair = "S";

		System.out.println("Cliente Inicializado!");

		try {

			while (sair.equalsIgnoreCase("S")) {

				operacao = new Operacao();

				lerOperacao();

				porta = encontrarServidor(operacao.getOperador());

				connect(porta);

				if (operacao.getError().equals("")) {

					objectOutputStream.writeObject(operacao);
					System.out.println("Operação Enviada...");

					operacao = (Operacao) objectInputStream.readObject();
					System.out.println("Resultado Recebido...");

					System.out.println(operacao);

				} else {
					System.out.println(operacao.getError());
					operacao.setError("");
				}


				do {
					
					System.out.println("\nDeseja realizar outra operação? S/N");
					sair = scan.next();

				} while (!sair.equalsIgnoreCase("S") && !sair.equalsIgnoreCase("N"));

				disconnect();
			}
			
			System.out.println("Cliente Desligado...");

		} catch (Exception e) {
			e.printStackTrace();
		}

		scan.close();
	}

	public static void lerOperacao() {
		System.out.println("Operadores Validos: " + "\nSoma: + " + "\nSubtração: - " + "\nMultiplicação: * "
				+ "\nDivisão: / " + "\nPotencia: ^ " + "\nPorcentagem: % " + "\nRaiz Quadrada: @");
		System.out.println("\nInforme um operador valido: ");
		String operador = scan.next();

		if (operacao.validaOperador(operador)) {

			operacao.setOperador(operador);

			try {

				if (operador.equals("@")) {
					System.out.println("Informe o valor: ");
					operacao.setValor1(Double.parseDouble(scan.next()));
				} else {
					System.out.println("Informe o primeiro valor: ");
					operacao.setValor1(Double.parseDouble(scan.next()));
					System.out.println("Informe o segundo valor: ");
					operacao.setValor2(Double.parseDouble(scan.next()));
				}

			} catch (NumberFormatException e) {
				operacao.setError("\nValor Invalido, Tente Novamente!");
			} 
			
		} else {
			operacao.setError("\nOperador Invalido, Tente Novamente!");
		}
	}

	public static int encontrarServidor(String operador) {
		return switch (operador) {
		case "+", "-", "*", "/" -> BASIC_SERVER_PORT;
		case "^", "%", "@" -> EXPERT_SERVER_PORT;
		default -> -1;
		};
	}

	public static void connect(int port) throws UnknownHostException, IOException {
		System.out.println("\nIniciando conexão com o servidor. PORTA: " + port);

		socket = new Socket("localhost", port);

		objectInputStream = new ObjectInputStream(socket.getInputStream());
		objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
	}

	private static void disconnect() throws IOException {
		if (objectInputStream != null)
			objectInputStream.close();

		if (objectOutputStream != null)
			objectOutputStream.close();

		if (socket != null && socket.isConnected())
			socket.close();
	}

}
