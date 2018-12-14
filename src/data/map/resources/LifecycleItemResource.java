package data.map.resources;

//used in creation and destruction
public abstract class LifecycleItemResource {

	private int id;
	private short amount;
//	private int idle = -1;
//	private int sequence = -1;
	private int idleId = -1;


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public short getAmount() {
		return amount;
	}
	public void setAmount(short amount) {
		this.amount = amount;
	}
	
	protected void populateCopy(LifecycleItemResource copy){
		
    	copy.setAmount(this.getAmount());
    	copy.setId(this.getId());
    	copy.setIdleId(this.getIdleId());
	}

//	public int getIdle() {
//		return idle;
//	}
//
//	protected void setIdle(int idle) {
//		this.idle = idle;
//	}
//
//	public int getSequence() {
//		return sequence;
//	}
//
//	protected void setSequence(int sequence) {
//		this.sequence = sequence;
//	}

	public int getIdleId() {
		return idleId;
	}
	public void setIdleId(int idleId) {
		this.idleId = idleId;
	}
	
	@Override
	public String toString() {

		String str = new StringBuilder(Resource.getItemInternalNameById(id))
				.append("[").append(id).append("]")
				.append(" Amount:").append(amount)
				.append(" idle id:").append(idleId).toString();
//				.append(" idle").append(idle)
//				.append(" sequence").append(sequence).toString();

		if(this instanceof CreationItemResource){

			return new StringBuilder("CreationItemResource; isFromWarehouse:")
					.append(((CreationItemResource)this).isFromWarehouse())
					.append(" ").append(str).toString();

		}else if(this instanceof DestructionItemResource){

			return new StringBuilder("DestructionItemResource; isOnlyWhenStocked:")
					.append(((DestructionItemResource)this).isOnlyWhenStocked())
					.append(" ").append(str).toString();
		}

		return str;
	}
	
	public abstract LifecycleItemResource copy();
}
