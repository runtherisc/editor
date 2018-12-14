package data.map.resources;

public class WorkerActionResource {
	
	public WorkerActionResource(){
		
	}
	
	public WorkerActionResource(int workerin, int workerout){
		
		this(workerin, workerout, (short)1);
	}
	
	public WorkerActionResource(int workerin, int workerout, short carry){
		
		this.workerin = workerin;
		this.workerout = workerout;
		this.carry = carry;
	}

	private int workerin;
	private int workerout;
	private short carry;
	
	public short getCarry() {
		return carry;
	}
	public void setCarry(short carry) {
		this.carry = carry;
	}
	
	public int getWorkerin() {
		return workerin;
	}
    protected void setWorkerin(int workerin) {
		this.workerin = workerin;
	}
	public int getWorkerout() {
		return workerout;
	}
    protected void setWorkerout(int workerout) {
		this.workerout = workerout;
	}

	public boolean isItemWorker(){

        return !(workerin>-1 && workerout>-1);
    }
	
	public WorkerActionResource copy(){
		
		return new WorkerActionResource(this.workerin, this.workerout, this.carry);
	}
	
	
}
