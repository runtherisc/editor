package data.map.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class ResourceParser extends DefaultHandler{
	
	private enum BUILDING_ACTION{ITEM, REQUIRE, PRODUCE};

	private boolean isLevelResource = false;
	private boolean isItemResource = false;
	private boolean isMapItemsResource = false;
	private boolean isBuildingsResource = false;
	private boolean isImageResource = false;
	private boolean isCreationItemResource = false;
	private boolean isBuildingAction = false;
	//master
	private LevelResource levelResource;
	private ItemResource itemResource;
	private ImageResource imageResource;
	private MapItemResource mapItemResource;
	private BuildingResource buildingResource;

    //level
	private LevelTargetResource levelTarget;

	//map item
	private MapItemAttResource mapItemAttResource;
	private MapItemActionResource mapItemActionResource;
	//building
	private BuildingItemResource buildingItemResource;
	private BuildingCreationResource buildingCreationResource;
	private BuildingLifecycleResource buildingUpgradeResource;
	private BuildingLifecycleResource buildingDestructionResource;
	private BuildingActionResource buildingActionResource;
	private BuildingActionRequireResource buildingActionRequireResource;
	private BuildingActionProduceResource buildingActionProduceResource;
	private BuildingMapItemActionResource buildingMapItemActionResource;
	private BuildingAreaResource buildingAreaResource;

	private WorkerActionResource workerActionResource;
	private BUILDING_ACTION actionType;

	private ItemMakeResource warehouseMakeResource;
	private MakeRequireResource makeRequireResource;

	private CreationItemResource creationItemResource;
    private DestructionItemResource destructionItemResource;
//	private LifecycleItemResource upgradeItemResource;
	
//	ImageResource imageResource;
	private ImageResourceActions imageResourceAction;
	private MultiImageResourceAction busyImageResourceAction;
	private MultiImageResourceAction idleImageResourceAction;
	private WorkerImageResource workerImageResource;
	private MovementImageResource movementImageResource;
	
	public void parseDocument(String xmlFilename) throws SAXException, ParserConfigurationException, IOException {

        Resource.init();//reset Resources
		//get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();

		System.out.println("parsing");
		//get a new instance of parser
		SAXParser sp = spf.newSAXParser();

        XMLReader xr = sp.getXMLReader();
        xr.setContentHandler(this);

        File file  = new File(xmlFilename);
        
        System.out.println("file exist " + file.exists());

        InputStream is = new FileInputStream(file);

		//parse the file and also register this class for call backs
        xr.parse(new InputSource(is));
//			sp.parse(xmlFilename, this);

        System.out.println("finished parsing");

	}
	//Event Handlers
	public void startElement(String uri, String localName, String qName,
		Attributes attributes) throws SAXException {
		
		if(localName.length()==0) localName = qName;

		if (localName.equalsIgnoreCase(ResourceConstants.SETTLEMENT_TAG)) {

			setVersion(attributes.getValue(ResourceConstants.SETT_VERSION));
			Resource.setCodename(attributes.getValue(ResourceConstants.CODENAME));
			Resource.setXmlstatus(attributes.getValue(ResourceConstants.XML_STATUS));
			Resource.setDefaultLocale(attributes.getValue(ResourceConstants.DEFAULT_LOCALE));

		} else if (localName.equalsIgnoreCase(ResourceConstants.LEVELS_TAG)) {

			isLevelResource = true;

		} else if (localName.equalsIgnoreCase(ResourceConstants.ITEMS_TAG)) {

			isItemResource = true;
//			Resource.setWorkerPath(attributes.getValue(ResourceConstants.ITEMS_PATH));

		} else if (localName.equalsIgnoreCase(ResourceConstants.IMAGES_TAG)) {

			isImageResource = true;

		} else if (localName.equalsIgnoreCase(ResourceConstants.MAP_ITEMS_TAG)) {

			isMapItemsResource = true;

		} else if (localName.equalsIgnoreCase(ResourceConstants.BUILDINGS_TAG)) {

			isBuildingsResource = true;
		}

		if(isLevelResource) {

			if (localName.equalsIgnoreCase(ResourceConstants.LEVEL_TAG)) {

				levelResource = new LevelResource();
				levelResource.setInternalTitle(attributes.getValue(ResourceConstants.LEVEL_TITLE));
				levelResource.setId(getIntFromString(attributes.getValue(ResourceConstants.LEVEL_ID)));
				levelResource.setGold(getIntFromString(attributes.getValue(ResourceConstants.LEVEL_GOLD)));
				levelResource.setSilver(getIntFromString(attributes.getValue(ResourceConstants.LEVEL_SILVER)));
				levelResource.setBronze(getIntFromString(attributes.getValue(ResourceConstants.LEVEL_BRONZE)));
				levelResource.setBuildings(getCommaSepIntListFromString(attributes.getValue(ResourceConstants.LEVEL_BUILDINGS)));
				levelResource.setImages(getCommaSepIntFromString(attributes.getValue(ResourceConstants.LEVEL_IMAGES)));
				levelResource.setWorkers(getCommaSepIntFromString(attributes.getValue(ResourceConstants.LEVEL_WORKERS)));
				levelResource.setMapPath(attributes.getValue(ResourceConstants.LEVEL_MAP));
				levelResource.setJson(attributes.getValue(ResourceConstants.LEVEL_JSON));

			} else if (localName.equalsIgnoreCase(ResourceConstants.LEV_TARGET_TAG)) {

				levelTarget = new LevelTargetResource();
				levelTarget.setItem(getIntFromString(attributes.getValue(ResourceConstants.LEV_TARGET_ITEM)));
				levelTarget.setAmount(getIntFromString(attributes.getValue(ResourceConstants.LEV_TARGET_AMOUNT)));

			} else if (localName.equalsIgnoreCase(ResourceConstants.INFO_TAG)){

				processInfoResource(levelResource.getInfoResource(), attributes);
			}

		}else if (isItemResource) {
			if (localName.equalsIgnoreCase(ResourceConstants.ITEM_TAG)) {

				itemResource = new ItemResource();
				itemResource.setName(attributes.getValue(ResourceConstants.ITEM_NAME));
				itemResource.setId(getIntFromString(attributes.getValue(ResourceConstants.ITEM_ID)));

			}else if (localName.equalsIgnoreCase(ResourceConstants.ITEM_WORKER_TAG)) {

				workerImageResource = new WorkerImageResource();
				workerImageResource.setResource(getIntFromString(attributes.getValue(ResourceConstants.ITEM_WORKER_RESOURCE)));
				workerImageResource.setSpan(getCoordsFromPipedStr(attributes.getValue(ResourceConstants.ITEM_WORKER_SPAN)));

			} else if (localName.equalsIgnoreCase(ResourceConstants.INFO_TAG)){

				processInfoResource(itemResource.getInfoResource(), attributes);
			}
		}else if(isImageResource){
			if(localName.equalsIgnoreCase(ResourceConstants.IMAGE_TAG)) {

				imageResource = new ImageResource();
				imageResource.setId(getIntFromString(attributes.getValue(ResourceConstants.IMA_ID)));
				String dir = attributes.getValue(ResourceConstants.IMA_DIR);
				imageResource.setDirectory(dir);
//				System.out.println(dir);
//			imageResource.setSkip(getIntFromString(attributes.getValue(ResourceConstants.IMA_SKIP)));
//			if(Constants.DEBUG) System.out.println(getPointFromPipedStr(attributes.getValue(ResourceConstants.IMA_SPAN)));
				imageResource.setSpan(getCoordsFromPipedStr(attributes.getValue(ResourceConstants.IMA_SPAN)));//must be before walkover
				imageResource.setHotspots(getCompusCoordsFromPipedandCommaStr(attributes.getValue(ResourceConstants.IMA_HOTSPOT)));
				imageResource.setWalkover(getGridFromBitmaskArrayStr(attributes.getValue(ResourceConstants.IMA_WALKOVER), imageResource.getSpan()));
//			imageResource.setEnter(getBooleanFromString(attributes.getValue(ResourceConstants.IMA_ENTER)));
				imageResource.setCanTouch(getBooleanFromString(attributes.getValue(ResourceConstants.IMA_CANTOUCH)));

//				int idleTotal = getIntFromString(attributes.getValue(ResourceConstants.IMA_TOTAL_IDLE));
//
//				int idleSkip = 0;
//				if (idleTotal > 0) idleSkip = 8 / idleTotal;
//
//				imageResource.setIdleSkip(idleSkip);
//				imageResource.setIdleTotal(idleTotal);
				
				imageResource.setIdleId(getIntFromString(attributes.getValue(ResourceConstants.IMA_IDLE_ID), false));
				
				imageResource.setMapObject(getBooleanFromString(attributes.getValue(ResourceConstants.IMA_ISMAPOBJECT)));
				
//			if(idleTotal==0){
//				imageResource.setIdleSkip(0);
//			}else{
//				for (int i = 0; i < idleTotal; i++) {
//					String filename = new StringBuffer(dir).append("idle/").append(i).toString();
//					if(Constants.DEBUG) System.out.println(filename);
//					imageResource.addIdleImage(loadImage(filename));
//					imageResource.setIdleSkip(8/idleTotal);
//				}
//			}


			}else if(localName.equalsIgnoreCase(ResourceConstants.DESTRUCTION_REQ_TAG)){

                buildingDestructionResource = new BuildingLifecycleResource();
                buildingDestructionResource.setIdleId(getIntFromString(attributes.getValue(ResourceConstants.DEST_IDLE_ID), false));
//                buildingDestructionResource.setSequence(getIntFromString(attributes.getValue(ResourceConstants.DEST_SEQUENCE), false));
//                buildingDestructionResource.setIdle(getIntFromString(attributes.getValue(ResourceConstants.DEST_IDLE)));

            }else if(localName.equalsIgnoreCase(ResourceConstants.CREATION_REQ_TAG)){

				isCreationItemResource = true;
				buildingCreationResource = new BuildingCreationResource();
				buildingCreationResource.setEndFrame(getIntFromString(attributes.getValue(ResourceConstants.CREQ_ENDFRAME)));
				buildingCreationResource.setIdleId(getIntFromString(attributes.getValue(ResourceConstants.CREQ_IDLE_ID), false));
//				buildingCreationResource.setSequence(getIntFromString(attributes.getValue(ResourceConstants.CREQ_SEQUENCE), false));
//				buildingCreationResource.setIdle(getIntFromString(attributes.getValue(ResourceConstants.CREQ_IDLE)));
//				buildingCreationResource.setDestructionSeq(getIntFromString(attributes.getValue(ResourceConstants.CREQ_DESTSEQ)));
//				buildingCreationResource.setDestructionIdle(getIntFromString(attributes.getValue(ResourceConstants.CREQ_DESTIDLE)));
				buildingCreationResource.setDestructionIdleId(getIntFromString(attributes.getValue(ResourceConstants.CREQ_DEST_IDLE_ID)));

			}else if(localName.equalsIgnoreCase(ResourceConstants.CREQ_ITEM_TAG)){

				creationItemResource = new CreationItemResource();
				creationItemResource.setId(getIntFromString(attributes.getValue(ResourceConstants.CREQ_ITEM_ID)));
				creationItemResource.setAmount(getShortFromString(attributes.getValue(ResourceConstants.CREQ_ITEM_AMOUNT)));
				creationItemResource.setFromWarehouse(getBooleanFromString(attributes.getValue(ResourceConstants.CREQ_ITEM_WAREHOUSE)));
//				creationItemResource.setSequence(getIntFromString(attributes.getValue(ResourceConstants.CREQ_ITEM_SEQUENCE)));
//				creationItemResource.setIdle(getIntFromString(attributes.getValue(ResourceConstants.CREQ_ITEM_IDLE)));
				creationItemResource.setIdleId(getIntFromString(attributes.getValue(ResourceConstants.CREQ_ITEM_IDLE_ID), false));

			}else if(localName.equalsIgnoreCase(ResourceConstants.DEST_ITEM_TAG)){

                destructionItemResource = new DestructionItemResource();
                destructionItemResource.setId(getIntFromString(attributes.getValue(ResourceConstants.DEST_ITEM_ID)));
                destructionItemResource.setAmount(getShortFromString(attributes.getValue(ResourceConstants.DEST_ITEM_AMOUNT)));
//                destructionItemResource.setSequence(getIntFromString(attributes.getValue(ResourceConstants.DEST_ITEM_SEQUENCE)));
//                destructionItemResource.setIdle(getIntFromString(attributes.getValue(ResourceConstants.DEST_ITEM_IDLE)));
                destructionItemResource.setIdleId(getIntFromString(attributes.getValue(ResourceConstants.DEST_ITEM_IDLE_ID), false));
				destructionItemResource.setOnlyWhenStocked(getBooleanFromString(attributes.getValue(ResourceConstants.DEST_ITEM_ISSTOCKED)));

            }else if(localName.equalsIgnoreCase(ResourceConstants.UPGRADE_REQ_TAG)){

				buildingUpgradeResource = new BuildingLifecycleResource();
				buildingUpgradeResource.setEndFrame(getIntFromString(attributes.getValue(ResourceConstants.UPREQ_ENDFRAME)));
//				buildingUpgradeResource.setSequence(getIntFromString(attributes.getValue(ResourceConstants.UPREQ_SEQUENCE), false));
//				buildingUpgradeResource.setIdle(getIntFromString(attributes.getValue(ResourceConstants.UPREQ_IDLE)));
				 destructionItemResource.setIdleId(getIntFromString(attributes.getValue(ResourceConstants.UPREQ_ITEM_IDLE_ID), false));

			}
//			else if(localName.equalsIgnoreCase(ResourceConstants.UPREQ_ITEM_TAG)){
//
//				upgradeItemResource = new LifecycleItemResource();
//				upgradeItemResource.setId(getIntFromString(attributes.getValue(ResourceConstants.UPREQ_ITEM_ID)));
//				upgradeItemResource.setAmount(getShortFromString(attributes.getValue(ResourceConstants.UPREQ_ITEM_AMOUNT)));
//				upgradeItemResource.setFromWarehouse(getBooleanFromString(attributes.getValue(ResourceConstants.UPREQ_ITEM_WAREHOUSE)));
//
//			}
			else if(localName.equalsIgnoreCase(ResourceConstants.CREATION_TAG) ||
					localName.equalsIgnoreCase(ResourceConstants.UPGRADE_TAG) ||
					localName.equalsIgnoreCase(ResourceConstants.DESTRUCTION_TAG)){

				imageResourceAction = new ImageResourceActions();
				imageResourceAction.setSkip(getIntFromString(attributes.getValue(ResourceConstants.IRA_SKIP)));
				imageResourceAction.setTotalNumberImages(getIntFromString(attributes.getValue(ResourceConstants.IRA_TOTAL)));
			}else if(localName.equalsIgnoreCase(ResourceConstants.BUSY_TAG)){

				busyImageResourceAction = new MultiImageResourceAction();
				busyImageResourceAction.setId(getIntFromString(attributes.getValue(ResourceConstants.BIR_ID)));
				busyImageResourceAction.setSkip(getIntFromString(attributes.getValue(ResourceConstants.BIR_SKIP)));
				busyImageResourceAction.setTotalNumberImages(getIntFromString(attributes.getValue(ResourceConstants.BIR_TOTAL)));
				busyImageResourceAction.setSequence(getIntFromString(attributes.getValue(ResourceConstants.BIR_DIRECTORY)));
				busyImageResourceAction.setInternalName(attributes.getValue(ResourceConstants.BIR_INTERNAL_NAME));//editor only
			}else if(localName.equalsIgnoreCase(ResourceConstants.IDLE_TAG)){

				idleImageResourceAction = new MultiImageResourceAction();
				idleImageResourceAction.setId(getIntFromString(attributes.getValue(ResourceConstants.IDLE_ID)));
				idleImageResourceAction.setTotalNumberImages(getIntFromString(attributes.getValue(ResourceConstants.IDLE_TOTAL)));
				idleImageResourceAction.setSequence(getIntFromString(attributes.getValue(ResourceConstants.IDLE_DIRECTORY)));
				idleImageResourceAction.setInternalName(attributes.getValue(ResourceConstants.IDLE_INTERNAL_NAME));//editor only
			}else if(localName.equalsIgnoreCase(ResourceConstants.MOVEMENT_TAG)){

				movementImageResource = new MovementImageResource();
				movementImageResource.setSkip(getIntFromString(attributes.getValue(ResourceConstants.MOVE_SKIP)));
				movementImageResource.setUp(getIntFromString(attributes.getValue(ResourceConstants.MOVE_UP)));
				movementImageResource.setDown(getIntFromString(attributes.getValue(ResourceConstants.MOVE_DOWN)));
				movementImageResource.setLeft(getIntFromString(attributes.getValue(ResourceConstants.MOVE_LEFT)));
				movementImageResource.setRight(getIntFromString(attributes.getValue(ResourceConstants.MOVE_RIGHT)));
				
			}
		}else if(isMapItemsResource){
			if(localName.equalsIgnoreCase(ResourceConstants.MAP_ITEM_TAG)) {
				
				mapItemResource = new MapItemResource();
				mapItemResource.setImageResourceId(getIntFromString(attributes.getValue(ResourceConstants.MAP_IMAGE)));
				mapItemResource.setName(attributes.getValue(ResourceConstants.MAP_NAME));
				mapItemResource.setId(getIntFromString(attributes.getValue(ResourceConstants.MAP_ID)));
				mapItemResource.setTime(getIntFromString(attributes.getValue(ResourceConstants.MAP_TIME)));
				mapItemResource.setOnElapse(getIntFromString(attributes.getValue(ResourceConstants.MAP_ONELAPSE)));
				mapItemResource.setAllowedon(getCommaSepIntListFromString(attributes.getValue(ResourceConstants.MAP_ALLOWEDON)));
				mapItemResource.setDrawFirst(getBooleanFromString(attributes.getValue(ResourceConstants.MAP_DRAWFIRST)));
				mapItemResource.setShowAttributes(getBooleanFromString(attributes.getValue(ResourceConstants.MAP_SHOWATTRIBUTES)));
				
			}else if(localName.equalsIgnoreCase(ResourceConstants.MAP_ITEM_ATT_TAG)){
				
				mapItemAttResource = new MapItemAttResource();
				mapItemAttResource.setId(getIntFromString(attributes.getValue(ResourceConstants.MIA_ID)));
				mapItemAttResource.setAmount(getShortFromString(attributes.getValue(ResourceConstants.MIA_AMOUNT)));
				mapItemAttResource.setOnDepletion(getIntFromString(attributes.getValue(ResourceConstants.MIA_ONDEPLETION)));
			
			}else if(localName.equalsIgnoreCase(ResourceConstants.MAP_ITEM_ACTION_TAG)){
				
				mapItemActionResource = new MapItemActionResource();
				mapItemActionResource.setId(getIntFromString(attributes.getValue(ResourceConstants.MAC_ID)));
				mapItemActionResource.setBusy(getShortFromString(attributes.getValue(ResourceConstants.MAC_BUSY)));
				mapItemActionResource.setMapitem(getIntFromString(attributes.getValue(ResourceConstants.MAC_MAPITEM)));
				mapItemActionResource.setState(getIntFromString(attributes.getValue(ResourceConstants.MAC_STATE)));
				mapItemActionResource.setInternalName(attributes.getValue(ResourceConstants.MAC_NAME));

			}else if(localName.equalsIgnoreCase(ResourceConstants.INFO_TAG)){

				processInfoResource(mapItemResource.getInfoResource(), attributes);
			}
		}else if(isBuildingsResource){
			if(localName.equalsIgnoreCase(ResourceConstants.BUILDING_TAG)) {

				buildingResource = new BuildingResource();
				buildingResource.setImageResourceId(getIntFromString(attributes.getValue(ResourceConstants.BUILD_IMAGE)));
				buildingResource.setName(attributes.getValue(ResourceConstants.BUILD_NAME));
				buildingResource.setId(getIntFromString(attributes.getValue(ResourceConstants.BUILD_ID)));
				buildingResource.setWarehouse(getBooleanFromString(attributes.getValue(ResourceConstants.BUILD_ISWAREHOUSE)));
				buildingResource.setWorkers(getIntFromString(attributes.getValue(ResourceConstants.BUILD_WORKERS)));
				buildingResource.setAllowedon(getCommaSepIntListFromString(attributes.getValue(ResourceConstants.BUILD_ALLOWEDON)));

			}else if(localName.equalsIgnoreCase(ResourceConstants.BITEM_TAG)){
				
				actionType = BUILDING_ACTION.ITEM;
				buildingItemResource = new BuildingItemResource();
				buildingItemResource.setId(getIntFromString(attributes.getValue(ResourceConstants.BITEM_ID)));
				buildingItemResource.setAmount(getIntFromString(attributes.getValue(ResourceConstants.BITEM_AMOUNT)));
				
			}else if(localName.equalsIgnoreCase(ResourceConstants.WORKER_ACTION_TAG)){
				
				workerActionResource = new WorkerActionResource();
				workerActionResource.setWorkerin(getIntFromString(attributes.getValue(ResourceConstants.WORKER_ACTION_IN)));
				workerActionResource.setWorkerout(getIntFromString(attributes.getValue(ResourceConstants.WORKER_ACTION_OUT)));
				workerActionResource.setCarry(getShortFromString(attributes.getValue(ResourceConstants.WORKER_CARRY)));
				
			}else if(localName.equalsIgnoreCase(ResourceConstants.BITEM_MAKE_TAG)){
				
				warehouseMakeResource = new ItemMakeResource();
				warehouseMakeResource.setFrequency(getIntFromString(attributes.getValue(ResourceConstants.BITEM_MAKE_FREQ)));
				warehouseMakeResource.setAmount(getShortFromString(attributes.getValue(ResourceConstants.BITEM_MAKE_AMOUNT)));
				
			}else if(localName.equalsIgnoreCase(ResourceConstants.MAKE_REQUIRE_TAG)){
				
				makeRequireResource = new MakeRequireResource();
				makeRequireResource.setId(getIntFromString(attributes.getValue(ResourceConstants.MAKE_REQUIRE_ID)));
				makeRequireResource.setAmount(getShortFromString(attributes.getValue(ResourceConstants.MAKE_REQUIRE_AMOUNT)));
				
			}else if(localName.equalsIgnoreCase(ResourceConstants.ACTION_TAG)){
				
				isBuildingAction = true;
				buildingActionResource = new BuildingActionResource();
				buildingActionResource.setFulfill(getBooleanFromString(attributes.getValue(ResourceConstants.ACT_MUSTFULFILL)));
				buildingActionResource.setBusyAction(getIntFromString(attributes.getValue(ResourceConstants.ACT_BUSY)));
				buildingActionResource.setIssueIdle(getIntFromString(attributes.getValue(ResourceConstants.ACT_ISSUEIDLE)));
				buildingActionResource.setTitle(attributes.getValue(ResourceConstants.ACT_TITLE));
				
			}else if(localName.equalsIgnoreCase(ResourceConstants.REQUIRE_TAG)){
				
				actionType = BUILDING_ACTION.REQUIRE;
				buildingActionRequireResource = new BuildingActionRequireResource();
				buildingActionRequireResource.setAmount(getShortFromString(attributes.getValue(ResourceConstants.REQ_AMOUNT)));
//				buildingActionRequireResource.setCarry(getShortFromString(attributes.getValue(ResourceConstants.REQ_CARRY)));
//				buildingActionRequireResource.setArea(getIntFromString(attributes.getValue(ResourceConstants.REQ_AREA)));
//				buildingActionRequireResource.setAreaFixed(getBooleanFromString(attributes.getValue(ResourceConstants.REQ_AREAFIXED)));
				buildingActionRequireResource.setItem(getIntFromString(attributes.getValue(ResourceConstants.REQ_ITEM)));
			
			}else if(localName.equalsIgnoreCase(ResourceConstants.PRODUCE_TAG)){
				
				actionType = BUILDING_ACTION.PRODUCE;
				buildingActionProduceResource = new BuildingActionProduceResource();
				buildingActionProduceResource.setAmount(getShortFromString(attributes.getValue(ResourceConstants.PRO_AMOUNT)));
//				buildingActionProduceResource.setCarry(getShortFromString(attributes.getValue(ResourceConstants.PRO_CARRY)));
//				buildingActionProduceResource.setArea(getIntFromString(attributes.getValue(ResourceConstants.PRO_AREA)));
//				buildingActionProduceResource.setAreaFixed(getBooleanFromString(attributes.getValue(ResourceConstants.PRO_AREAFIXED)));
				buildingActionProduceResource.setItem(getIntFromString(attributes.getValue(ResourceConstants.PRO_ITEM)));
				
			}else if(localName.equalsIgnoreCase(ResourceConstants.BUILDING_MAPITEM_ACTION)){
				
				buildingMapItemActionResource = new BuildingMapItemActionResource();
				buildingMapItemActionResource.setMapItem(getIntFromString(attributes.getValue(ResourceConstants.BMA_MAPITEM)));
//				buildingMapItemActionResource.setArea(getIntFromString(attributes.getValue(ResourceConstants.BMA_AREA)));
				buildingMapItemActionResource.setAction(getIntFromString(attributes.getValue(ResourceConstants.BMA_ACTION)));
//				buildingMapItemActionResource.setAreaFixed(getBooleanFromString(attributes.getValue(ResourceConstants.BMA_AREAFIXED)));

			}else if(localName.equalsIgnoreCase(ResourceConstants.AREA_TAG)){

				buildingAreaResource = new BuildingAreaResource();
				buildingAreaResource.setFixed(getBooleanFromString(attributes.getValue(ResourceConstants.AREA_ISFIXED)));
				buildingAreaResource.setAreaSize(getIntFromString(attributes.getValue(ResourceConstants.AREA_SIZE)));

			}else if(localName.equalsIgnoreCase(ResourceConstants.INFO_TAG)){

				if(isBuildingAction){
					processInfoResource(buildingActionResource.getInfoResource(), attributes);
				}else{
					processInfoResource(buildingResource.getInfoResource(), attributes);
				}
			}
		}

	}


	public void endElement(String uri, String localName,
		String qName) throws SAXException {

		if(localName.length()==0) localName = qName;

        if(localName.equalsIgnoreCase(ResourceConstants.LEVELS_TAG)){

            isLevelResource=false;

        }else if(localName.equalsIgnoreCase(ResourceConstants.ITEMS_TAG)){
			
			isItemResource=false;
			
		}else if(localName.equalsIgnoreCase(ResourceConstants.MAP_ITEMS_TAG)){
			
			isMapItemsResource=false;
			
		}else if(localName.equalsIgnoreCase(ResourceConstants.IMAGES_TAG)){

			isImageResource=false;

		}else if(localName.equalsIgnoreCase(ResourceConstants.BUILDINGS_TAG)){
			
			isBuildingsResource=false;
		}

        if(isLevelResource){
            if(localName.equalsIgnoreCase(ResourceConstants.LEVEL_TAG)) {

                Resource.addLevelResource(levelResource);
            }else if(localName.equalsIgnoreCase(ResourceConstants.LEV_TARGET_TAG)){

                levelResource.addTarget(levelTarget);
            }
        }else if(isItemResource){
			if(localName.equalsIgnoreCase(ResourceConstants.ITEM_TAG)) {
	
				Resource.addItemResource(itemResource);
			}else if(localName.equalsIgnoreCase(ResourceConstants.ITEM_WORKER_TAG)){
				
				itemResource.setResource(workerImageResource);
			}
		}else if(isImageResource){
	        if(localName.equalsIgnoreCase(ResourceConstants.IMAGE_TAG)){

	        	System.out.println(imageResource.getDirectory()+" adding "+imageResource.getId());
	        	if(imageResource.isMapObject())
	        		Resource.addMapImageResource(imageResource);
	        	else{
	        		Resource.addBuildingImageResource(imageResource);
	        	}

			}else if(localName.equalsIgnoreCase(ResourceConstants.CREATION_TAG)){

				imageResource.setCreation(imageResourceAction);

			}else if(localName.equalsIgnoreCase(ResourceConstants.UPGRADE_TAG)){

				imageResource.setUpgrade(imageResourceAction);

			}else if(localName.equalsIgnoreCase(ResourceConstants.DESTRUCTION_TAG)){

				imageResource.setDestruction(imageResourceAction);

			}else if(localName.equalsIgnoreCase(ResourceConstants.BUSY_TAG)){

				imageResource.addBusy(busyImageResourceAction);

			}else if(localName.equalsIgnoreCase(ResourceConstants.IDLE_TAG)){

				imageResource.addIdle(idleImageResourceAction);

			}else if(localName.equalsIgnoreCase(ResourceConstants.MOVEMENT_TAG)){

				imageResource.setMovement(movementImageResource);

			}else if(localName.equalsIgnoreCase(ResourceConstants.CREATION_REQ_TAG)){

				imageResource.addBuildingCreation(buildingCreationResource);
				isCreationItemResource = false;

			}else if(localName.equalsIgnoreCase(ResourceConstants.DESTRUCTION_REQ_TAG)){

                imageResource.setDestructionResource(buildingDestructionResource);

            }else if(localName.equalsIgnoreCase(ResourceConstants.CREQ_ITEM_TAG)){

				buildingCreationResource.addLifecycleItem(creationItemResource);

			}else if(localName.equalsIgnoreCase(ResourceConstants.DEST_ITEM_TAG)){

				if(isCreationItemResource){
					buildingCreationResource.addLifecycleItem(destructionItemResource);
				}else {
					buildingDestructionResource.addLifecycleItem(destructionItemResource);
				}

            }else if(localName.equalsIgnoreCase(ResourceConstants.UPGRADE_REQ_TAG)){

				imageResource.addBuildingUpgrade(buildingUpgradeResource);

			}
//			else if(localName.equalsIgnoreCase(ResourceConstants.CREQ_ITEM_TAG)){
//
//				buildingUpgradeResource.addLifecycleItem(upgradeItemResource);
//
//			}

		}else if(isMapItemsResource){
			if(localName.equalsIgnoreCase(ResourceConstants.MAP_ITEM_TAG)) {
				
				Resource.addMapItemResource(mapItemResource);
			}else if(localName.equalsIgnoreCase(ResourceConstants.MAP_ITEM_ATT_TAG)){
				
				mapItemResource.addMapItemAtt(mapItemAttResource);
			
			}else if(localName.equalsIgnoreCase(ResourceConstants.MAP_ITEM_ACTION_TAG)){
				
				mapItemResource.addMapItemAction(mapItemActionResource);
			}
		}else if(isBuildingsResource){

			if(localName.equalsIgnoreCase(ResourceConstants.BUILDING_TAG)) {

	        	Resource.addBuildingResource(buildingResource);
				
			}else if(localName.equalsIgnoreCase(ResourceConstants.BITEM_TAG)){
				
				buildingResource.putBuildingItem(buildingItemResource, buildingItemResource.getId());
				
			}else if(localName.equalsIgnoreCase(ResourceConstants.WORKER_ACTION_TAG)){
				
				switch(actionType){
				
				case ITEM:
					buildingItemResource.addWorkerAction(workerActionResource);
					break;
				case REQUIRE:
					buildingActionRequireResource.addWorkerAction(workerActionResource);
					break;
				case PRODUCE:
					buildingActionProduceResource.addWorkerAction(workerActionResource);
					break;
				default:
					throw new SAXException("Action Type was not set");
				
				}
				
			}else if(localName.equalsIgnoreCase(ResourceConstants.BUILDING_MAPITEM_ACTION)){
				
				switch(actionType){			

				case REQUIRE:
					buildingActionRequireResource.addBuildingMapItemAction(buildingMapItemActionResource);
					break;
				case PRODUCE:
					buildingActionProduceResource.addBuildingMapItemAction(buildingMapItemActionResource);
					break;
				case ITEM:
				default:
					throw new SAXException("Action Type was not set");
				
				}
				
			}else if(localName.equalsIgnoreCase(ResourceConstants.BITEM_MAKE_TAG)){
				
				buildingItemResource.addWarehouseMake(warehouseMakeResource);
				
			}else if(localName.equalsIgnoreCase(ResourceConstants.MAKE_REQUIRE_TAG)){
				
				warehouseMakeResource.addMakeRequirement(makeRequireResource);
				
			}else if(localName.equalsIgnoreCase(ResourceConstants.ACTION_TAG)){
				
				isBuildingAction = false;
				buildingResource.addBuildingAction(buildingActionResource);
				
			}else if(localName.equalsIgnoreCase(ResourceConstants.REQUIRE_TAG)){
				
				buildingActionResource.addRequirement(buildingActionRequireResource);
				
			}else if(localName.equalsIgnoreCase(ResourceConstants.PRODUCE_TAG)){
				
				buildingActionResource.addProduce(buildingActionProduceResource);
				
			}else if(localName.equalsIgnoreCase(ResourceConstants.AREA_TAG)){

				buildingResource.setBuildingAreaResource(buildingAreaResource);

			}
		}
		
	}

	private void processInfoResource(InfoResource infoResource, Attributes attributes){

		infoResource.addText(
				attributes.getValue(ResourceConstants.INFO_TYPE),
				attributes.getValue(ResourceConstants.INFO_LOCALE),
				attributes.getValue(ResourceConstants.INFO_TEXT));
	}
	
	private int getIntFromString(String strToParse){
		
		return getIntFromString(strToParse, true);
	}
	
	private int getIntFromString(String strToParse, boolean defaultZero){
		
		try{
			return Integer.parseInt(strToParse.trim());
			
		}catch (Exception e) {
			if (defaultZero) return 0;
			return -1;
		}
	}
	
	private short getShortFromString(String strToParse){
		
		return getShortFromString(strToParse, true);
	}
	
	private short getShortFromString(String strToParse, boolean defaultZero){
		
		try{
			return Short.parseShort(strToParse.trim());
			
		}catch (Exception e) {
			if (defaultZero) return 0;
			return -1;
		}
	}
	
//	private float getFloatFromString(String strToParse){
//
//		try{
//			return Float.parseFloat(strToParse);
//			
//		}catch (Exception e) {
//			return 0;
//		}
//	}
	
	private List<Integer> getCommaSepIntListFromString(String strToParse){
		
		if(strToParse==null)return null;
		
		String[] commaSep = strToParse.split(",");
		
		List<Integer> intsToReturn = new ArrayList<Integer>();
		
		for (int i = 0; i < commaSep.length; i++) {
			
			intsToReturn.add(getIntFromString(commaSep[i]));
			
		}
		return intsToReturn;
	}
	
	private int[] getCommaSepIntFromString(String strToParse){
		
		if(strToParse==null)return null;
		
		String[] commaSep = strToParse.split(",");
		
		int[] intsToReturn = new int[commaSep.length];
		
		for (int i = 0; i < intsToReturn.length; i++) {
			
			intsToReturn[i] = getIntFromString(commaSep[i]);
			
		}
		return intsToReturn;
	}
	
	private boolean getBooleanFromString(String strToParse){
		
		if(strToParse==null) return false;
		return strToParse.equalsIgnoreCase("true")?true:false;
		
		
	}
	
	private ExitPoint getCoordsFromPipedStr(String pipedStr){
		
		if(pipedStr==null) return new ExitPoint(-1, -1);
		String[] pipeSep = pipedStr.split("\\|");
		int x = getIntFromString(pipeSep[0]);
		int y = 0;
		if(pipeSep.length>1) y = getIntFromString(pipeSep[1]);
		return new ExitPoint(x,y);
	}
	
	private ExitPoint[] getCompusCoordsFromPipedandCommaStr(String str){
		
		ExitPoint[] coords = new ExitPoint[4];
		if(str!=null){
			String[] commaSep = str.split(",");
			int numberOfCoords = commaSep.length;
			if(numberOfCoords!=4) System.err.println("ERROR: There must be 4 points to a compus hotspot array");
			for (int i = 0; i < numberOfCoords; i++) {
				
//				if(Constants.DEBUG) System.out.println(commaSep[i]);
				if(!commaSep[i].equals("-1")){
					coords[i] =  getCoordsFromPipedStr(commaSep[i]);
					coords[i].setDirection(i);
				}
				else coords[i] = new ExitPoint(-1, -1, i);
				
//				if(Constants.DEBUG) System.out.println(coords[i].toString());
			}
		};
		
		return coords;
	}
	
	private boolean[][] getGridFromBitmaskArrayStr(String str, Coords coords){
		
		if(str == null || str.equals("0")) return new boolean[coords.x][coords.y];
		
		boolean[][] grid = new boolean[coords.x][coords.y];
		int[] numberArray = getCommaSepIntFromString(str);
		
		for (int y = 0; y < grid[0].length; y++) {
			
			int mask = 1; 
			for(int x = 0; x < grid.length; ++x) { 
				
				grid[x][y] = ((mask & numberArray[y]) != 0);
				mask = mask << 1; //bit shift one place to the left
			}
		}
		
		return grid;
	}
	
	private void setVersion(String version){

		int periodPos = version.indexOf(".");

		if(periodPos==-1){
			Resource.setMajorVersion(getIntFromString(version));
			Resource.setMinorVersion(0);
		}else{
			Resource.setMajorVersion(getIntFromString(version.substring(0, periodPos)));
			Resource.setMinorVersion(getIntFromString(version.substring(periodPos+1)));
		}
	}
	
	public static void main(String[] args) {
		
//		new ResourceParser().parseDocument("assets/resource.xml");
//
//		new ResourceWriter().writeXml("filename");
		
	}
	


}
