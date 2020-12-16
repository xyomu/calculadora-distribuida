import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BasicServer extends Thread {
	private static ServerSocket ss;

	private final static int BASIC_SERVER_PORT = 10001;

	private Socket socket;

	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;

	public BasicServer(Socket s) {
		this.socket = s;

		try {
			objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			objectInputStream = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			ss = new ServerSocket(BASIC_SERVER_PORT);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println("Basic Server rodando na porta = " + ss.getLocalPort());
		System.out.println("Aguardando conexão do cliente...");

		while (true) {
			try {
				Socket conexao = ss.accept();

				System.out.println("\n======================================\n");
				System.out.println("Cliente Aceito");
				System.out.println("HOSTNAME = " + conexao.getInetAddress().getHostName());
				System.out.println("HOST ADDRESS = " + conexao.getInetAddress().getHostAddress());
				System.out.println("PORTA LOCAL = " + conexao.getLocalPort());
				System.out.println("PORTA DE CONEXAO = " + conexao.getPort());

				Thread thread = new BasicServer(conexao);
				thread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void run() {

		try {

			Operacao operacao = (Operacao) objectInputStream.readObject(); // Recebe Operação do Client

			System.out.println("\nOperação Recebida do Cliente: " + operacao);

			operacao.setResultado(processarResultado(operacao));

			System.out.println("\nResultado Processado: " + operacao.getResultado());

			objectOutputStream.writeObject(operacao);
			System.out.println("Resultado Enviado...");

			objectInputStream.close();
			objectOutputStream.close();
			socket.close();

			System.out.println("\nCliente desconectou do servidor...");
			System.out.println("\n======================================\n");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * Baseado na operação desejada, processa o resultado e o retorna
	 */
	public double processarResultado(Operacao op) {

		return switch (op.getOperador()) {
		case "+" -> (op.getValor1() + op.getValor2());
		case "-" -> (op.getValor1() - op.getValor2());
		case "/" -> (op.getValor1() / op.getValor2());
		case "*" -> (op.getValor1() * op.getValor2());
		default -> (op.getValor1() * op.getValor2());
		};

	}

}
