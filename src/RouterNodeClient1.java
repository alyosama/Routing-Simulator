import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RouterNodeClient1 {
	public static final String IP="localhost"; // to be tested in the same computer
	public static final int NUM_NODES = 4;
	public static final int INFINITY = 999;
	private int myID;
	private GuiTextArea myGUI;
	private Socket socket;
	ObjectInputStream in;
	ObjectOutputStream out;
	private int[][] costs = new int[NUM_NODES][NUM_NODES];
	private int[] real_costs = new int[NUM_NODES];
	private int[] route = new int[NUM_NODES];
	private boolean poisoned_reverse = true;

	private static int[][] connectcosts = new int[NUM_NODES][NUM_NODES];
	public static void main(String[] argv) {
		connectcosts[0][1] = 1;
		connectcosts[0][2] = 3;
		connectcosts[0][3] = 7;
		connectcosts[1][0] = 1;
		connectcosts[1][2] = 1;
		connectcosts[1][3] = INFINITY;
		connectcosts[2][0] = 3;
		connectcosts[2][1] = 1;
		connectcosts[2][3] = 2;
		connectcosts[3][0] = 7;
		connectcosts[3][1] = INFINITY;
		connectcosts[3][2] = 2;

		new RouterNodeClient1(1, connectcosts[1]);

	}

	public RouterNodeClient1(int ID, int[] costs) {
		myID = ID;
		myGUI = new GuiTextArea("  Output window for Router #" + ID + "  ");
		for (int i = 0; i < NUM_NODES; ++i) {
			route[i] = costs[i] == INFINITY ? -1 : i;
		}
		real_costs = costs.clone();
		for (int i = 0; i < NUM_NODES; ++i) {
			for (int j = 0; j < NUM_NODES; ++j) {
				if (i == myID) {
					this.costs[i][j] = costs[j];
				} else if (i == j) {
					this.costs[i][j] = 0;
				} else {
					this.costs[i][j] = INFINITY;
				}
			}
		}
		try {
			socket = new Socket(IP, 8000);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			printDistanceTable();
			sendUpdatesToNeighbors();
			while (true) {
				RouterPacket rpkt = (RouterPacket) in.readObject();
				recvUpdate(rpkt);

			}

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void sendUpdatesToNeighbors() {
		for (int i = 0; i < NUM_NODES; ++i) {
			if (i != myID && real_costs[i] != INFINITY) {
				if (poisoned_reverse) {
					int[] costs = this.costs[myID].clone();
					for (int j = 0; j < NUM_NODES; ++j) {
						if (route[j] == i) {
							costs[j] = INFINITY;
						}
					}
					sendUpdate(new RouterPacket(myID, i, costs));
				} else {
					sendUpdate(new RouterPacket(myID, i, costs[myID]));
				}
			}
		}
	}

	private void updateCosts() {
		/* Get best routes */
		int[] old_costs = costs[myID].clone();
		for (int i = 0; i < NUM_NODES; ++i) {
			if (i != myID) {
				if (route[i] == i && costs[myID][i] != real_costs[i]) {
					costs[myID][i] = real_costs[i];
				} else if (costs[myID][i] != costs[myID][route[i]]
						+ costs[route[i]][i]) {
					costs[myID][i] = costs[myID][route[i]] + costs[route[i]][i];
				}
				if (real_costs[i] <= costs[myID][i]) {
					costs[myID][i] = real_costs[i];
					route[i] = i;
				}
				for (int j = 0; j < NUM_NODES; ++j) {
					if (costs[myID][i] + costs[i][j] < costs[myID][j]) {
						costs[myID][j] = costs[myID][i] + costs[i][j];
						route[j] = route[i];
					}
				}
			}
		}
		for (int i = 0; i < NUM_NODES; ++i) {
			if (costs[myID][i] != old_costs[i]) {
				sendUpdatesToNeighbors();
				return;
			}
		}
	}
	public void recvUpdate(RouterPacket pkt) {

		// Over Network
		boolean they_have_changed = false;
		for (int i = 0; i < pkt.mincost.length; ++i) {
			if (costs[pkt.sourceid][i] != pkt.mincost[i]) {
				costs[pkt.sourceid][i] = pkt.mincost[i];
				they_have_changed = true;
			}
		}

		/* If they have changed, doublecheck cached costs */
		if (they_have_changed) {
			updateCosts();
			printDistanceTable();
		}
	}

	private void sendUpdate(RouterPacket pkt) {
		// Over Network
		try {
			out.writeObject(pkt);
			// printDistanceTable();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void printDistanceTable() {

		/* Header */
		myGUI.println();
		myGUI.println("Current table for " + myID);
		myGUI.println("\nOur distance vector and routes:");
		myGUI.print(GuiTextArea.format("dst    |", 12));
		for (int i = 0; i < NUM_NODES; ++i) {
			myGUI.print(GuiTextArea.format(i, 5));
		}
		myGUI.println();
		for (int i = 0; i < NUM_NODES; ++i) {
			myGUI.print("---------");
		}
		myGUI.println();

		/* Route */
		myGUI.print(GuiTextArea.format("route |", 10));
		for (int i = 0; i < NUM_NODES; ++i) {
			myGUI.print(GuiTextArea.format(route[i], 5));
		}
		myGUI.println();

		/* Costs */
		myGUI.print(GuiTextArea.format("cost   |", 11));
		for (int i = 0; i < NUM_NODES; ++i) {
			myGUI.print(GuiTextArea.format(costs[myID][i], 5));
		}
		myGUI.println();

	}

	public void updateLinkCost(int dest, int newcost) {
		real_costs[dest] = newcost;
		updateCosts();

	}
}
