import java.io.Serializable;

public class RouterPacket implements Cloneable, Serializable {

	private static final long serialVersionUID = -8002369670310304390L;
	int sourceid;
	int destid;
	int[] mincost = new int[RouterNodeClient0.NUM_NODES]; /* min cost to node 0 ... 3 */

	RouterPacket(int sourceID, int destID, int[] mincosts) {
		this.sourceid = sourceID;
		this.destid = destID;
		System.arraycopy(mincosts, 0, this.mincost, 0, RouterNodeClient0.NUM_NODES);
	}
	public int getDestID(){
		return this.destid;
	}
	public int getSourceID(){
		return this.sourceid;
	}
	public Object clone() {
		try {
			RouterPacket newPkt = (RouterPacket) super.clone();
			newPkt.mincost = (int[]) newPkt.mincost.clone();
			return newPkt;
		} catch (CloneNotSupportedException e) {
			System.err.println(e);
			System.exit(1);
		}
		return null;
	}

}
