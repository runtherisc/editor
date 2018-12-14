package game.items;


import data.map.resources.Coords;
import game.path.CompletePath;

public class Worker {
	
	public Worker(){

		workerHash = hashCode();
		init();
	}
	
	public void init(){

		status = WORKER_STATUS.IDLE;
		desiredItem = -1;
		amount = -1;
//		timeLeft = -1;//not used?
		path = null;
		destinationId = -1;
		action = -1;
		mapItemCoords = null;
        pathPending = false;
		currentCoords = null;
        displayWorker = 0;
		terminateWorker = false;
		currentCoords = null;
//		holdWorkerBeforeReturn = false;
	}

	public enum WORKER_STATUS{
		IDLE,
		IDLE_WAITING_RETURN,
        IDLE_WAITING_CLEANUP,
        BUILDING_OUT,
		RETURNING_HOME,
		WORKING_HOME,
		GETTING_STORED_ITEM,
		GOING_TO_MAP_OBJECT,
		GATHERING_MAP_OBJECT,
		PLACEHOLDER_RETURN, 
		TERMINATED_RETURN
	}

	private WORKER_STATUS status;
    private int desiredItem;
    private short amount;

    private CompletePath path;

    private boolean pathPending;

    private long destinationId;


    private int workerIn;

    private int action;

    private Coords mapItemCoords;
	
    private int workerItemId;
    private Coords startCoords;

	private Coords currentCoords;

	private boolean isPlaceholder;

	private boolean isItemWorker;

    private byte displayWorker;

	private byte hideWorker;

	private boolean terminateWorker;

	private int workerHash;

}
