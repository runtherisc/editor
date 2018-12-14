package data.map.resources;

public interface ResourceConstants {
	
	
	String SETTLEMENT_TAG = "TheGatherers";	
	String SETT_VERSION = "version";
	String DEFAULT_LOCALE = "defaultLocale";
	String CODENAME = "codename";
	String XML_STATUS = "xmlstatus";
	String MIN_APK = "minApk";

//    <levels>
//    <level id="1" gold="300" silver="600" bronze="900">
//    <!-- add map and allowed buildings here including if you are allow to 'upgrade' them? -->
//    <target item="1" amount="20"/>
//    <target item="5" amount="20"/>
//    </level>
//    </levels>

    String LEVELS_TAG = "levels";

    String LEVEL_TAG = "level";
    String LEVEL_ID = "id";
    String LEVEL_GOLD = "gold";
    String LEVEL_SILVER = "silver";
    String LEVEL_BRONZE = "bronze";
	String LEVEL_TITLE = "title";
	String LEVEL_BUILDINGS = "buildings";
	String LEVEL_IMAGES = "images";
	String LEVEL_WORKERS = "workers";
	String LEVEL_MAP = "map";
	String LEVEL_JSON = "json";

    String LEV_TARGET_TAG = "target";
    String LEV_TARGET_ITEM = "item";
    String LEV_TARGET_AMOUNT = "amount";
	
	String ITEMS_TAG = "items";
	String ITEMS_PATH = "path";
	
	String ITEM_TAG = "item"; 
	String ITEM_NAME = "name";
	String ITEM_ID = "id";
	
	String ITEM_WORKER_TAG = "workerImage";
	String ITEM_WORKER_RESOURCE = "resource";
	String ITEM_WORKER_SPAN = "span";

	String IMAGES_TAG = "images";
	
	String MAP_ITEMS_TAG = "mapitems";
	
	String MAP_ITEM_TAG = "mapitem";
	String MAP_NAME = "name";
	String MAP_ID = "id";
	String MAP_TIME = "time";
	String MAP_ONELAPSE = "onElapse";
	String MAP_ALLOWEDON = "allowedon";
	String MAP_DRAWFIRST = "drawFirst";
	String MAP_SHOWATTRIBUTES = "showAttributes";
	String MAP_IMAGE = "image";
	
	String MAP_ITEM_ATT_TAG = "mapitematt";
	String MIA_ID = "id";
	String MIA_AMOUNT = "amount";
	String MIA_ONDEPLETION = "onDepletion";
	
	String MAP_ITEM_ACTION_TAG = "action";
	String MAC_ID = "id";
	String MAC_BUSY = "busy";
	String MAC_STATE = "state";
	String MAC_MAPITEM = "mapitem";
	String MAC_NAME = "name";

	String BUILDINGS_TAG = "buildings";
	
	String BUILDING_TAG = "building";
	String BUILD_NAME = "name";
	String BUILD_ID = "id";
	String BUILD_ISWAREHOUSE = "isWarehouse";
	String BUILD_WORKERS = "workers";
	String BUILD_ALLOWEDON = "allowedon";
	String BUILD_IMAGE = "image";
	
	//maybe merge creation and upgrade into the same object?
	String CREATION_TAG = "creation";
	String CREATION_REQ_TAG = "creationRequirement";
//	String CREQ_ID = "id";
//	String CREQ_AMOUNT = "amount";
	String CREQ_IDLE_ID = "idleId";
	String CREQ_ENDFRAME = "endFrame";
	String CREQ_SEQUENCE = "sequence";
	String CREQ_IDLE = "idle";
//	String CREQ_DESTSEQ = "destructionSeq";
//	String CREQ_DESTIDLE = "destructionIdle";
	String CREQ_DEST_IDLE_ID = "destructionIdleId";
	
	String CREQ_ITEM_TAG = "creationItem";
	String CREQ_ITEM_ID = "id";
	String CREQ_ITEM_AMOUNT = "amount";
	String CREQ_ITEM_WAREHOUSE = "fromWarehouse";
//	String CREQ_ITEM_SEQUENCE = "sequence";
//	String CREQ_ITEM_IDLE = "idle";
	String CREQ_ITEM_IDLE_ID = "idleId";
    String CREQ_ITEM_RETURN_AMOUNT = "returnAmount";
	String CREQ_ITEM_RETURN_SEQ = "returnSeq";
	String CREQ_ITEM_RETURN_IDLE = "returnIdle";

    String DESTRUCTION_REQ_TAG = "destructionRequirement";
//    String DEST_SEQUENCE = "sequence";
//    String DEST_IDLE = "idle";
    String DEST_IDLE_ID = "idleId";

	String DEST_ITEM_TAG = "destructionItem";
	String DEST_ITEM_ID = "id";
	String DEST_ITEM_AMOUNT = "amount";
//	String DEST_ITEM_SEQUENCE = "sequence";
//    String DEST_ITEM_IDLE = "idle";
	String DEST_ITEM_IDLE_ID = "idleId";
	String DEST_ITEM_ISSTOCKED = "onlyWhenStocked";
	
	String UPGRADE_TAG = "upgrade";
	String UPGRADE_REQ_TAG = "upgradeRequirement";
//	String UPREQ_ID = "id";
//	String UPREQ_AMOUNT = "amount";
	String UPREQ_ENDFRAME = "endFrame";
//	String UPREQ_SEQUENCE = "sequence";
//	String UPREQ_IDLE = "idle";
	String UPREQ_ITEM_IDLE_ID = "idleId";
	
	String UPREQ_ITEM_TAG = "upgradeItem";
	String UPREQ_ITEM_ID = "id";
	String UPREQ_ITEM_AMOUNT = "amount";
	String UPREQ_ITEM_WAREHOUSE = "fromWarehouse";
	
	String DESTRUCTION_TAG = "destruction";
	String BUSY_TAG = "busy";
	String IDLE_TAG = "idle";
	String MOVEMENT_TAG = "movement";

	String AREA_TAG = "area";
	String AREA_ISFIXED = "isFixed";
	String AREA_SIZE = "areaSize";
	
	String BITEM_TAG = "item";
	String BITEM_ID = "id";
	String BITEM_AMOUNT = "amount";
	
	String WORKER_ACTION_TAG = "workerAction";
	String WORKER_ACTION_IN = "in";
	String WORKER_ACTION_OUT = "out";
	String WORKER_CARRY = "carry";
	
	String BITEM_MAKE_TAG = "make";
	String BITEM_MAKE_FREQ = "frequency";
	String BITEM_MAKE_AMOUNT = "amount";
	
	String MAKE_REQUIRE_TAG = "makeRequire";
	String MAKE_REQUIRE_ISITEM = "isItem";
	String MAKE_REQUIRE_AMOUNT = "amount";
	String MAKE_REQUIRE_ID = "id";
	
	String ACTION_TAG = "action";
	String ACT_MUSTFULFILL = "mustFulfill";
	String ACT_BUSY = "busy";
	String ACT_ISSUEIDLE = "issueIdle";
	String ACT_TITLE = "title";
	
	String REQUIRE_TAG = "require";
	String REQ_AMOUNT = "amount";
//	String REQ_CARRY = "carry";
//	String REQ_AREA = "area";
//	String REQ_AREAFIXED = "areaFixed";
	String REQ_ITEM = "item";
	
	String PRODUCE_TAG = "produce";
	String PRO_AMOUNT = "amount";
//	String PRO_CARRY = "carry";
//	String PRO_AREA = "area";
//	String PRO_AREAFIXED = "areaFixed";
	String PRO_ITEM = "item";
	
	String BUILDING_MAPITEM_ACTION = "mapItemAction";
	String BMA_MAPITEM = "mapitem";
	String BMA_ACTION = "action";
//	String BMA_AREA = "area";
//	String BMA_AREAFIXED = "areaFixed";
	
	
	String IMAGE_TAG = "image";
	String IMA_BYTIME = "byTime";
	String IMA_DIR = "dir";
	String IMA_SPAN = "span";
	String IMA_HOTSPOT = "hotspot";
	String IMA_WALKOVER = "walkover";
//	String IMA_ENTER = "enter";
	String IMA_CANTOUCH = "canTouch";
	String IMA_IDLE_ID = "idleId";
	String IMA_ID = "id";
	String IMA_ISMAPOBJECT = "mapObject";

	String INFO_TAG = "info";
	String INFO_LOCALE = "locale";
	String INFO_TYPE = "type";
	String INFO_TEXT = "text";
	//helpers
	String INFO_TYPE_TITLE = "title";
	String INFO_TYPE_DESCRIPTION = "description";

	String IRA_SKIP = "skip";
	String IRA_TOTAL = "total";
	
	String BIR_SKIP = "skip";
	String BIR_TOTAL = "total";
	String BIR_DIRECTORY = "directory";
	String BIR_INTERNAL_NAME = "name";
	String BIR_ID = "id";
	
	String IDLE_ID = "id";
	String IDLE_TOTAL = "total";
	String IDLE_DIRECTORY = "sequence";
	String IDLE_INTERNAL_NAME = "name";
	
	String MOVE_SKIP = "skip";
	String MOVE_LEFT = "left";
	String MOVE_RIGHT = "right";
	String MOVE_UP = "up";
	String MOVE_DOWN = "down";
	
	//validation
	int MAX_SPAN = 15;
}
