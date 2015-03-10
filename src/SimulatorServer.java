import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SimulatorServer {
	public static final int NUM_NODES = 4;
	public static int RoutersConnected = 0;
	private GuiTextArea myGUI;
	private ServerSocket server;
	public HandleARouter[] tasks;
	public static void main(String[] argv) {
		new SimulatorServer();

	}

	public SimulatorServer() {
		myGUI = new GuiTextArea("This The Simulation Server");
		tasks = new HandleARouter[NUM_NODES];
		try {
			server = new ServerSocket(8000);
			for (RoutersConnected = 0; RoutersConnected < NUM_NODES; RoutersConnected++) {
				Socket socket = server.accept();
				myGUI.println("Accepted from " + socket.getInetAddress()
						+ " Router " + RoutersConnected);
				tasks[RoutersConnected] = new HandleARouter(socket);

			}
			for(int i=0;i<NUM_NODES;i++){
				new Thread(tasks[i]).start();

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	class HandleARouter implements Runnable {
		private Socket socket;
		private ObjectInputStream in;
		private ObjectOutputStream out;

		public HandleARouter(Socket socket) {
			this.socket = socket;

		}

		@Override
		public void run() {
			try {
				in = new ObjectInputStream(socket.getInputStream());
				out = new ObjectOutputStream(socket.getOutputStream());
		
				while (true) {
					// myGUI.println(SimulatorServer.RoutersConnected+" Router Connected Until Now");

					RouterPacket rpkt = (RouterPacket) in.readObject();
					tasks[rpkt.getDestID()].sendPacket(rpkt);

				}

			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}

		}

		private synchronized void  sendPacket(RouterPacket pkt) {
			// Over Network
			try {
				out.writeObject(pkt);
				myGUI.println("Router Packet sent from " + pkt.getSourceID()
						+ " to " + pkt.getDestID());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
