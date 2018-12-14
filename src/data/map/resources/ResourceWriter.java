package data.map.resources;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
//import javax.xml.transform.OutputKeys;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import data.Constants;



public class ResourceWriter {
	
	public static String RESOURCE_FILENAME = "resource.xml";

	public ResourceWriter(){}
	
	public void writeXml(File outputFile){
		
		try {

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.newDocument();
			
			
			Element settlementEle = newElement(doc, ResourceConstants.SETTLEMENT_TAG);
			settlementEle.setAttribute(ResourceConstants.SETT_VERSION, Resource.getVersionStr());
			settlementEle.setAttribute(ResourceConstants.CODENAME, Resource.getCodename());
			settlementEle.setAttribute(ResourceConstants.XML_STATUS, Resource.getXmlstatus());
			settlementEle.setAttribute(ResourceConstants.DEFAULT_LOCALE, Resource.getDefaultLocale());
			settlementEle.setAttribute(ResourceConstants.MIN_APK, Constants.MINIMUM_APK_VERSION);
			
			addComment(settlementEle, doc, "This xml is a ingame object blueprint for the Android game little hoarders");
			addComment(settlementEle, doc, "Editing this document should only be done via the editor, unless you know what you are doing!");

			//levels
            Element levelsEle = newElement(doc, settlementEle, ResourceConstants.LEVELS_TAG);

            List<LevelResource> levelsList = Resource.getLevelResourceList();
            for (LevelResource levelResource : levelsList) {
            	
            	System.out.println("processing level..."+levelResource.getInternalTitle());

                Element levelEle = newElement(doc, levelsEle, ResourceConstants.LEVEL_TAG);
                levelEle.setAttribute(ResourceConstants.LEVEL_ID, String.valueOf(levelResource.getId()));
                levelEle.setAttribute(ResourceConstants.LEVEL_TITLE, levelResource.getInternalTitle());
                levelEle.setAttribute(ResourceConstants.LEVEL_GOLD, String.valueOf(levelResource.getGold()));
                levelEle.setAttribute(ResourceConstants.LEVEL_SILVER, String.valueOf(levelResource.getSilver()));
                levelEle.setAttribute(ResourceConstants.LEVEL_BRONZE, String.valueOf(levelResource.getBronze()));
                levelEle.setAttribute(ResourceConstants.LEVEL_BUILDINGS, getStringFromIntList(levelResource.getBuildings()));
                levelEle.setAttribute(ResourceConstants.LEVEL_IMAGES, getStringFromIntArray(levelResource.getImages()));
                levelEle.setAttribute(ResourceConstants.LEVEL_WORKERS, getStringFromIntArray(levelResource.getWorkers()));
                levelEle.setAttribute(ResourceConstants.LEVEL_MAP, levelResource.getMapPath());
                levelEle.setAttribute(ResourceConstants.LEVEL_JSON, levelResource.getJson());

                List<LevelTargetResource> targetsList = levelResource.getTargets();
                for (LevelTargetResource levelTargetResource : targetsList) {

                    Element levelTargetEle = newElement(doc, levelEle, ResourceConstants.LEV_TARGET_TAG);
                    levelTargetEle.setAttribute(ResourceConstants.LEV_TARGET_ITEM, String.valueOf(levelTargetResource.getItem()));
                    levelTargetEle.setAttribute(ResourceConstants.LEV_TARGET_AMOUNT, String.valueOf(levelTargetResource.getAmount()));
                }
                
                addInfoTag(doc, levelEle, levelResource.getInfoResource());
            }

            //items
			Element itemsEle = newElement(doc, settlementEle, ResourceConstants.ITEMS_TAG);
			itemsEle.setAttribute(ResourceConstants.ITEMS_PATH, Resource.getWorkerPath());
			
			List<ItemResource> itemList = Resource.getItemResourceList();			
			for (ItemResource itemResource : itemList) {
				
				System.out.println("processing item..."+itemResource.getName());
				
				Element itemEle = newElement(doc, itemsEle, ResourceConstants.ITEM_TAG);
				itemEle.setAttribute(ResourceConstants.ITEM_NAME, itemResource.getName());
				itemEle.setAttribute(ResourceConstants.ITEM_ID, String.valueOf(itemResource.getId()));
				
				WorkerImageResource workerResource = itemResource.getResource();
				if(workerResource!=null){
					
					Element workerImageEle = newElement(doc, itemEle, ResourceConstants.ITEM_WORKER_TAG);
					workerImageEle.setAttribute(ResourceConstants.ITEM_WORKER_RESOURCE, String.valueOf(workerResource.getResource()));
					workerImageEle.setAttribute(ResourceConstants.ITEM_WORKER_SPAN, getPipedStringFromCoords(workerResource.getSpan()));
				}
				
				addInfoTag(doc, itemEle, itemResource.getInfoResource());
				
			}
			
			//images
			Element imagesEle = newElement(doc, settlementEle, ResourceConstants.IMAGES_TAG);
			
			List<ImageResource> imageList = Resource.getMapImageResourceList();
			
			processImages(imagesEle, doc, imageList);
			
			imageList = Resource.getBuildingImageResourceList();
			
			processImages(imagesEle, doc, imageList);
			
			//map items
			Element mapItemsEle = newElement(doc, settlementEle, ResourceConstants.MAP_ITEMS_TAG);
					
			List<MapItemResource> mapItemList = Resource.getMapItemResourceList();
			
			for (MapItemResource mapItemResource : mapItemList) {
				
				System.out.println("processing mapitem..."+mapItemResource.getName());
				
				Element mapItemEle = newElement(doc, mapItemsEle, ResourceConstants.MAP_ITEM_TAG);
				mapItemEle.setAttribute(ResourceConstants.MAP_NAME, mapItemResource.getName());
				mapItemEle.setAttribute(ResourceConstants.MAP_ID, String.valueOf(mapItemResource.getId()));
				mapItemEle.setAttribute(ResourceConstants.MAP_TIME, String.valueOf(mapItemResource.getTime()));
				mapItemEle.setAttribute(ResourceConstants.MAP_ONELAPSE, String.valueOf(mapItemResource.getOnElapse()));
				mapItemEle.setAttribute(ResourceConstants.MAP_ALLOWEDON, getStringFromIntList(mapItemResource.getAllowedon()));
				mapItemEle.setAttribute(ResourceConstants.MAP_DRAWFIRST, String.valueOf(mapItemResource.isDrawFirst()));
				mapItemEle.setAttribute(ResourceConstants.MAP_SHOWATTRIBUTES, String.valueOf(mapItemResource.isShowAttributes()));
				mapItemEle.setAttribute(ResourceConstants.MAP_IMAGE, String.valueOf(mapItemResource.getImageResourceId()));
				
//				addImageTag(doc, mapItemEle, mapItemResource.getImageResource());
				addInfoTag(doc, mapItemEle, mapItemResource.getInfoResource());
				
				List<MapItemAttResource> mapItemAttList = mapItemResource.getMapItemAttList();
				
				for (MapItemAttResource mapItemAttResource : mapItemAttList) {
					
					Element mapItemAttEle = newElement(doc, mapItemEle, ResourceConstants.MAP_ITEM_ATT_TAG);
					mapItemAttEle.setAttribute(ResourceConstants.MIA_ID, String.valueOf(mapItemAttResource.getId()));
					mapItemAttEle.setAttribute(ResourceConstants.MIA_AMOUNT, String.valueOf(mapItemAttResource.getAmount()));
					mapItemAttEle.setAttribute(ResourceConstants.MIA_ONDEPLETION, String.valueOf(mapItemAttResource.getOnDepletion()));
					
				}
				
				List<MapItemActionResource> mapItemActionList = mapItemResource.getMapItemActionList();
				
				for (MapItemActionResource mapItemActionResource : mapItemActionList) {
					
					Element mapItemActionEle = newElement(doc, mapItemEle, ResourceConstants.MAP_ITEM_ACTION_TAG);
					mapItemActionEle.setAttribute(ResourceConstants.MAC_ID, String.valueOf(mapItemActionResource.getId()));
					mapItemActionEle.setAttribute(ResourceConstants.MAC_BUSY, String.valueOf(mapItemActionResource.getBusy()));
					mapItemActionEle.setAttribute(ResourceConstants.MAC_MAPITEM, String.valueOf(mapItemActionResource.getMapitem()));
					mapItemActionEle.setAttribute(ResourceConstants.MAC_STATE, String.valueOf(mapItemActionResource.getState()));
					mapItemActionEle.setAttribute(ResourceConstants.MAC_NAME, mapItemActionResource.getInternalName());
					
				}
				
			}
			
			//buildings
			Element buildingssEle = newElement(doc, settlementEle, ResourceConstants.BUILDINGS_TAG);

			List<BuildingResource> buildResourceList = Resource.getBuildingResourceList();			
			
			processBuildings(buildingssEle, doc, buildResourceList);
			
			
			
//			// create the root element node
//			Element element = doc.createElement("root");
//			doc.appendChild(element);
//
//			// create a comment node given the specified string
//			Comment comment = doc.createComment("This is a comment");
//			doc.insertBefore(comment, element);
//
//			// add element after the first child of the root element
//			Element itemElement = doc.createElement("item");
//			element.appendChild(itemElement);
//			
//			// add an attribute to the node
//			itemElement.setAttribute("myattr", "attrvalue");
//			
//			// create text for the node
//			itemElement.;insertBefore(doc.createTextNode("text"), itemElement.getLastChild());

			prettyPrint(doc, outputFile);



		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	protected void prettyPrint(Document xml, File outputFile) throws Exception {
		
//				Writer out = new StringWriter();	
		Writer out = new PrintWriter(outputFile, "UTF-8");
		synchronized (out) {
			Transformer tf = TransformerFactory.newInstance().newTransformer();
			tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			tf.setOutputProperty(OutputKeys.INDENT, "yes");	
			tf.transform(new DOMSource(xml), new StreamResult(out));
		}
//		System.out.println(out.toString());
	}
	
	protected void addComment(Element element, Document doc, String str){
		
		Comment comment = doc.createComment(str);
		doc.insertBefore(comment, element);		
	}
	
	protected Element newElement(Document doc, String str){
		
		Element element = doc.createElement(str);
		doc.appendChild(element);
		return element;
		
	}
	
	protected Element newElement(Document doc, Element parent, String str){
		
		Element element = doc.createElement(str);
		parent.appendChild(element);
		return element;
		
	}
	
	protected void processImages(Element imagesEle, Document doc, List<ImageResource> imageList){
		
		for (ImageResource imageResource : imageList) {
			
			Element imageEle = newElement(doc, imagesEle, ResourceConstants.IMAGE_TAG);
			imageEle.setAttribute(ResourceConstants.IMA_ID, String.valueOf(imageResource.getId()));
			imageEle.setAttribute(ResourceConstants.IMA_DIR, imageResource.getDirectory());
			imageEle.setAttribute(ResourceConstants.IMA_SPAN, getPipedStringFromCoords(imageResource.getSpan()));
			imageEle.setAttribute(ResourceConstants.IMA_HOTSPOT, getPipedStringWithCommasFromPointArray(imageResource.getHotspots()));
			imageEle.setAttribute(ResourceConstants.IMA_WALKOVER, getBitmaskArrayStrFromGrid(imageResource.getWalkover()));
			imageEle.setAttribute(ResourceConstants.IMA_IDLE_ID, String.valueOf(imageResource.getIdleId()));
//			imageEle.setAttribute(ResourceConstants.IMA_CANTOUCH, String.valueOf(imageResource.isCanTouch()));
			imageEle.setAttribute(ResourceConstants.IMA_ISMAPOBJECT, String.valueOf(imageResource.isMapObject()));
			
			addImageActionsTags(ResourceConstants.CREATION_TAG, imageResource.getCreation(), doc, imageEle);
			addImageActionsTags(ResourceConstants.UPGRADE_TAG, imageResource.getUpgrade(), doc, imageEle);
			addImageActionsTags(ResourceConstants.DESTRUCTION_TAG, imageResource.getDestruction(), doc, imageEle);
			
			List<BuildingCreationResource> creationList = imageResource.getBuildingCreationList();
			
			if(creationList!=null && !creationList.isEmpty()){
			
				for (BuildingCreationResource  buildingCreationResource : creationList) {
					
					Element creationEle = newElement(doc, imageEle, ResourceConstants.CREATION_REQ_TAG);
					creationEle.setAttribute(ResourceConstants.CREQ_ENDFRAME, String.valueOf(buildingCreationResource.getEndFrame()));
//					creationEle.setAttribute(ResourceConstants.CREQ_SEQUENCE, String.valueOf(buildingCreationResource.getSequence()));
//					creationEle.setAttribute(ResourceConstants.CREQ_IDLE, String.valueOf(buildingCreationResource.getIdle()));
					creationEle.setAttribute(ResourceConstants.CREQ_IDLE_ID, String.valueOf(buildingCreationResource.getIdleId()));
//					creationEle.setAttribute(ResourceConstants.CREQ_DESTSEQ, String.valueOf(buildingCreationResource.getDestructionSeq()));
//					creationEle.setAttribute(ResourceConstants.CREQ_DESTIDLE, String.valueOf(buildingCreationResource.getDestructionIdle()));
					creationEle.setAttribute(ResourceConstants.CREQ_DEST_IDLE_ID, String.valueOf(buildingCreationResource.getDestructionIdleId()));
					
					List<LifecycleItemResource> creationItems = buildingCreationResource.getLifecycleItems();
					
					if(creationItems!=null && !creationItems.isEmpty()){
					
						for (LifecycleItemResource lifecycleItemResource : creationItems) {
							
							Element creationItemEle = null;
							if(lifecycleItemResource instanceof CreationItemResource){
								creationItemEle = newElement(doc, creationEle, ResourceConstants.CREQ_ITEM_TAG);
							}else if(lifecycleItemResource instanceof DestructionItemResource){
								creationItemEle = newElement(doc, creationEle, ResourceConstants.DEST_ITEM_TAG);
							}
							creationItemEle.setAttribute(ResourceConstants.CREQ_ITEM_ID, String.valueOf(lifecycleItemResource.getId()));
							creationItemEle.setAttribute(ResourceConstants.CREQ_ITEM_AMOUNT, String.valueOf(lifecycleItemResource.getAmount()));
//							creationItemEle.setAttribute(ResourceConstants.CREQ_ITEM_IDLE, String.valueOf(lifecycleItemResource.getIdle()));
//							creationItemEle.setAttribute(ResourceConstants.CREQ_ITEM_SEQUENCE, String.valueOf(lifecycleItemResource.getSequence()));
							creationItemEle.setAttribute(ResourceConstants.CREQ_ITEM_IDLE_ID, String.valueOf(lifecycleItemResource.getIdleId()));
							
							if(lifecycleItemResource instanceof CreationItemResource){
								creationItemEle.setAttribute(ResourceConstants.CREQ_ITEM_WAREHOUSE, String.valueOf(((CreationItemResource) lifecycleItemResource).isFromWarehouse()));
							}else if(lifecycleItemResource instanceof DestructionItemResource){
								creationItemEle.setAttribute(ResourceConstants.DEST_ITEM_ISSTOCKED, String.valueOf(((DestructionItemResource) lifecycleItemResource).isOnlyWhenStocked()));
							}
						}
					}
				}
			}
			
			BuildingLifecycleResource destructionResource = imageResource.getDestructionResource();
	
			if(destructionResource!=null){
				
				Element destructionEle = newElement(doc, imageEle, ResourceConstants.DESTRUCTION_REQ_TAG);
//				destructionEle.setAttribute(ResourceConstants.DEST_IDLE, String.valueOf(destructionResource.getIdle()));
//				destructionEle.setAttribute(ResourceConstants.DEST_SEQUENCE, String.valueOf(destructionResource.getSequence()));
				destructionEle.setAttribute(ResourceConstants.DEST_IDLE_ID, String.valueOf(destructionResource.getIdleId()));
				
				List<LifecycleItemResource> destructionItems = destructionResource.getLifecycleItems();
				
				if(destructionItems!=null && !destructionItems.isEmpty()){
					
					for (LifecycleItemResource lifecycleItemResource : destructionItems) {
						
						Element destructionItemEle = newElement(doc, destructionEle, ResourceConstants.DEST_ITEM_TAG);
						
						destructionItemEle.setAttribute(ResourceConstants.DEST_ITEM_ID, String.valueOf(lifecycleItemResource.getId()));
						destructionItemEle.setAttribute(ResourceConstants.DEST_ITEM_AMOUNT, String.valueOf(lifecycleItemResource.getAmount()));
//						destructionItemEle.setAttribute(ResourceConstants.DEST_ITEM_IDLE, String.valueOf(lifecycleItemResource.getIdle()));
//						destructionItemEle.setAttribute(ResourceConstants.DEST_ITEM_SEQUENCE, String.valueOf(lifecycleItemResource.getSequence()));
						destructionItemEle.setAttribute(ResourceConstants.DEST_ITEM_IDLE_ID, String.valueOf(lifecycleItemResource.getIdleId()));
						
					}
				}
			}
			
//			List<BuildingLifecycleResource> upgradeList = imageResource.getBuildingUpgradeList();
//			
//			if(upgradeList!=null && !upgradeList.isEmpty()){
//			
//				for (BuildingLifecycleResource buildingUpgradeResource : upgradeList) {
//					
//					Element upgradeEle = newElement(doc, imageEle, ResourceConstants.UPGRADE_REQ_TAG);
//					upgradeEle.setAttribute(ResourceConstants.UPREQ_ENDFRAME, String.valueOf(buildingUpgradeResource.getEndFrame()));
//					upgradeEle.setAttribute(ResourceConstants.UPREQ_SEQUENCE, String.valueOf(buildingUpgradeResource.getSequence()));
//					upgradeEle.setAttribute(ResourceConstants.UPREQ_IDLE, String.valueOf(buildingUpgradeResource.getIdle()));
//					
//					List<LifecycleItemResource> upgradeItems = buildingUpgradeResource.getLifecycleItems();
//					
//					if(upgradeItems!=null && !upgradeItems.isEmpty()){
//					
//						for (LifecycleItemResource upgradeItemResource : upgradeItems) {
//							
//							Element upgradeItemEle = newElement(doc, upgradeEle, ResourceConstants.UPREQ_ITEM_TAG);
//							upgradeItemEle.setAttribute(ResourceConstants.UPREQ_ITEM_ID, String.valueOf(upgradeItemResource.getId()));
//							upgradeItemEle.setAttribute(ResourceConstants.UPREQ_ITEM_AMOUNT, String.valueOf(upgradeItemResource.getAmount()));
//							upgradeItemEle.setAttribute(ResourceConstants.UPREQ_ITEM_WAREHOUSE, String.valueOf(upgradeItemResource.isFromWarehouse()));
//						}
//					}
//				}
//			}
			
			List<MultiImageResourceAction> busyActions = imageResource.getBusy();
			if(busyActions!=null && !busyActions.isEmpty()){
				
				for (MultiImageResourceAction imageResourceActions : busyActions) {
					
					addBusyImageActionsTags(ResourceConstants.BUSY_TAG, imageResourceActions, doc, imageEle);
				}
			}
			
			List<MultiImageResourceAction> idles = imageResource.getIdles();
			if(idles!=null && !idles.isEmpty()){
				
				for (MultiImageResourceAction idle : idles) {
					
					addIdleImageTags(ResourceConstants.IDLE_TAG, idle, doc, imageEle);
				}
			}
			
			MovementImageResource movementResource = imageResource.getMovement();
			if(movementResource!=null){
				
				Element movementEle = newElement(doc, imageEle, ResourceConstants.MOVEMENT_TAG);
				movementEle.setAttribute(ResourceConstants.MOVE_SKIP, String.valueOf(movementResource.getSkip()));
				movementEle.setAttribute(ResourceConstants.MOVE_LEFT, String.valueOf(movementResource.getLeft()));
				movementEle.setAttribute(ResourceConstants.MOVE_RIGHT, String.valueOf(movementResource.getRight()));
				movementEle.setAttribute(ResourceConstants.MOVE_UP, String.valueOf(movementResource.getUp()));
				movementEle.setAttribute(ResourceConstants.MOVE_DOWN, String.valueOf(movementResource.getDown()));
			}
		}
	}
	
	protected void processBuildings(Element buildingssEle, Document doc, List<BuildingResource> buildResourceList){
		
		for (BuildingResource buildingResource : buildResourceList) {
			
			System.out.println("processing building..."+buildingResource.getTitle());
			
			Element buildingEle = newElement(doc, buildingssEle, ResourceConstants.BUILDING_TAG);
			buildingEle.setAttribute(ResourceConstants.BUILD_ID, String.valueOf(buildingResource.getId()));
			buildingEle.setAttribute(ResourceConstants.BUILD_NAME, buildingResource.getName());
			buildingEle.setAttribute(ResourceConstants.BUILD_ISWAREHOUSE, String.valueOf(buildingResource.isWarehouse()));
			buildingEle.setAttribute(ResourceConstants.BUILD_WORKERS, String.valueOf(buildingResource.getWorkers()));
			buildingEle.setAttribute(ResourceConstants.BUILD_ALLOWEDON, getStringFromIntList(buildingResource.getAllowedon()));
			buildingEle.setAttribute(ResourceConstants.BUILD_IMAGE, String.valueOf(buildingResource.getImageResourceId()));
			
//			addImageTag(doc, buildingEle, buildingResource.getImageResource());
			addInfoTag(doc, buildingEle, buildingResource.getInfoResource());
			
			Map<Integer, BuildingItemResource> bitemList = buildingResource.getBuildingItemMap();
			
			if(bitemList!=null && !bitemList.isEmpty()){
			
				for (BuildingItemResource buildingItemResource : bitemList.values()) {
					
					Element biteEle = newElement(doc, buildingEle, ResourceConstants.BITEM_TAG);
					biteEle.setAttribute(ResourceConstants.BITEM_ID, String.valueOf(buildingItemResource.getId()));
					biteEle.setAttribute(ResourceConstants.BITEM_AMOUNT, String.valueOf(buildingItemResource.getAmount()));
					
					List<WorkerActionResource> workerActions = buildingItemResource.getWorkerActions();
					
					for (WorkerActionResource workerActionResource : workerActions) {
						
						addWorkerActionElement(doc, biteEle, workerActionResource);
					}
					
					List<ItemMakeResource> makeList = buildingItemResource.getWarehouseMake();
					
					for (ItemMakeResource warehouseMakeResource : makeList) {
						
						Element makeEle = newElement(doc, biteEle, ResourceConstants.BITEM_MAKE_TAG);
						makeEle.setAttribute(ResourceConstants.BITEM_MAKE_FREQ, String.valueOf(warehouseMakeResource.getFrequency()));
						makeEle.setAttribute(ResourceConstants.BITEM_MAKE_AMOUNT, String.valueOf(warehouseMakeResource.getAmount()));
						
						List<MakeRequireResource> makeReqList = warehouseMakeResource.getMakeRequirements();
						
						for (MakeRequireResource makeRequireResource : makeReqList) {
							
							Element makeReqEle = newElement(doc, makeEle, ResourceConstants.MAKE_REQUIRE_TAG);
							makeReqEle.setAttribute(ResourceConstants.MAKE_REQUIRE_AMOUNT, String.valueOf(makeRequireResource.getAmount()));
							makeReqEle.setAttribute(ResourceConstants.MAKE_REQUIRE_ID, String.valueOf(makeRequireResource.getId()));

						}

					}
				}
			}
			
			List<BuildingActionResource> actionList = buildingResource.getBuildingActionList();
			
			for (BuildingActionResource buildingActionResource : actionList) {
				
				Element actionEle = newElement(doc, buildingEle, ResourceConstants.ACTION_TAG);

				actionEle.setAttribute(ResourceConstants.ACT_MUSTFULFILL, String.valueOf(buildingActionResource.isFulfill()));
				actionEle.setAttribute(ResourceConstants.ACT_BUSY, String.valueOf(buildingActionResource.getBusyAction()));
				actionEle.setAttribute(ResourceConstants.ACT_ISSUEIDLE, String.valueOf(buildingActionResource.getIssueIdle()));
				actionEle.setAttribute(ResourceConstants.ACT_TITLE, String.valueOf(buildingActionResource.getTitle()));
				
				addInfoTag(doc, actionEle, buildingActionResource.getInfoResource());	
				
				List<BuildingActionRequireResource> reqList = buildingActionResource.getRequirements();
				
				for (BuildingActionRequireResource buildingActionRequireResource : reqList) {
					
					Element reqEle = newElement(doc, actionEle, ResourceConstants.REQUIRE_TAG);
					reqEle.setAttribute(ResourceConstants.REQ_AMOUNT, String.valueOf(buildingActionRequireResource.getAmount()));
//					reqEle.setAttribute(ResourceConstants.REQ_CARRY, String.valueOf(buildingActionRequireResource.getCarry()));
//					reqEle.setAttribute(ResourceConstants.REQ_AREA, String.valueOf(buildingActionRequireResource.getArea()));
//					reqEle.setAttribute(ResourceConstants.REQ_AREAFIXED, String.valueOf(buildingActionRequireResource.isAreaFixed()));
					reqEle.setAttribute(ResourceConstants.REQ_ITEM, String.valueOf(buildingActionRequireResource.getItem()));
					
					List<WorkerActionResource> workerActions = buildingActionRequireResource.getWorkerActions();
					
					for (WorkerActionResource workerActionResource : workerActions) {
						
						addWorkerActionElement(doc, reqEle, workerActionResource);
					}
					
					List<BuildingMapItemActionResource> mapitemActions = buildingActionRequireResource.getBuildingMapItemActionResource();
					
					for (BuildingMapItemActionResource buildingMapItemActionResource : mapitemActions) {
						
						addMapItemActionElement(doc, reqEle, buildingMapItemActionResource);
					}

				}
				
				List<BuildingActionProduceResource> proList = buildingActionResource.getProduces();
				
				for (BuildingActionProduceResource buildingActionProduceResource : proList) {
					
					Element proEle = newElement(doc, actionEle, ResourceConstants.PRODUCE_TAG);
					proEle.setAttribute(ResourceConstants.PRO_AMOUNT, String.valueOf(buildingActionProduceResource.getAmount()));
//					proEle.setAttribute(ResourceConstants.PRO_CARRY, String.valueOf(buildingActionProduceResource.getCarry()));
//					proEle.setAttribute(ResourceConstants.PRO_AREA, String.valueOf(buildingActionProduceResource.getArea()));
//					proEle.setAttribute(ResourceConstants.PRO_AREAFIXED, String.valueOf(buildingActionProduceResource.isAreaFixed()));
					proEle.setAttribute(ResourceConstants.PRO_ITEM, String.valueOf(buildingActionProduceResource.getItem()));

					List<WorkerActionResource> workerActions = buildingActionProduceResource.getWorkerActions();
					
					for (WorkerActionResource workerActionResource : workerActions) {
						
						addWorkerActionElement(doc, proEle, workerActionResource);
					}
					
					List<BuildingMapItemActionResource> mapitemActions = buildingActionProduceResource.getBuildingMapItemActionResource();
					
					for (BuildingMapItemActionResource buildingMapItemActionResource : mapitemActions) {
						
						addMapItemActionElement(doc, proEle, buildingMapItemActionResource);
					}
				}
				
			}
			
			BuildingAreaResource buildingAreaResource = buildingResource.getBuildingAreaResource();
			
			if(buildingAreaResource!=null){
				Element areaEle = newElement(doc, buildingEle, ResourceConstants.AREA_TAG);
				areaEle.setAttribute(ResourceConstants.AREA_ISFIXED, String.valueOf(buildingAreaResource.isFixed()));
				areaEle.setAttribute(ResourceConstants.AREA_SIZE, String.valueOf(buildingAreaResource.getAreaSize()));
			}
		}
	}
	
	protected void addWorkerActionElement(Document doc, Element parentEle, WorkerActionResource workerActionResource){
		
		Element workerEle = newElement(doc, parentEle, ResourceConstants.WORKER_ACTION_TAG);
		workerEle.setAttribute(ResourceConstants.WORKER_ACTION_IN, String.valueOf(workerActionResource.getWorkerin()));
		workerEle.setAttribute(ResourceConstants.WORKER_ACTION_OUT, String.valueOf(workerActionResource.getWorkerout()));
		workerEle.setAttribute(ResourceConstants.WORKER_CARRY, String.valueOf(workerActionResource.getCarry()));
	}
	
	protected void addMapItemActionElement(Document doc, Element parentEle, BuildingMapItemActionResource buildingMapItemActionResource){
		
		Element mapitemEle = newElement(doc, parentEle, ResourceConstants.BUILDING_MAPITEM_ACTION);
		mapitemEle.setAttribute(ResourceConstants.BMA_MAPITEM, String.valueOf(buildingMapItemActionResource.getMapItem()));
//		mapitemEle.setAttribute(ResourceConstants.BMA_AREA, String.valueOf(buildingMapItemActionResource.getArea()));
		mapitemEle.setAttribute(ResourceConstants.BMA_ACTION, String.valueOf(buildingMapItemActionResource.getAction()));
//		mapitemEle.setAttribute(ResourceConstants.BMA_AREAFIXED, String.valueOf(buildingMapItemActionResource.isAreaFixed()));
	}

	protected void addInfoTag(Document doc, Element parentEle, InfoResource infoResource){
		
		HashMap<String, String> titles = new HashMap<String, String>();
		HashMap<String, String> descriptions = new HashMap<String, String>();
		
		infoResource.populateHashMapHelpers(titles, descriptions);
		
		for(String locale : titles.keySet()){
			
			if(titles.get(locale)!=null){
				
				Element infoEle = newElement(doc, parentEle, ResourceConstants.INFO_TAG);
				infoEle.setAttribute(ResourceConstants.INFO_LOCALE, locale);
				infoEle.setAttribute(ResourceConstants.INFO_TYPE, ResourceConstants.INFO_TYPE_TITLE);
				infoEle.setAttribute(ResourceConstants.INFO_TEXT, titles.get(locale));
			}
		}
		
		for(String locale : descriptions.keySet()){
			
			if(descriptions.get(locale)!=null){
				
				Element infoEle = newElement(doc, parentEle, ResourceConstants.INFO_TAG);
				infoEle.setAttribute(ResourceConstants.INFO_LOCALE, locale);
				infoEle.setAttribute(ResourceConstants.INFO_TYPE, ResourceConstants.INFO_TYPE_DESCRIPTION);
				infoEle.setAttribute(ResourceConstants.INFO_TEXT, descriptions.get(locale));
			}
		}


	}

	
	protected void addImageActionsTags(String actionTag, ImageResourceActions imageResourceActions, Document doc, Element parentEle){
		
		if(imageResourceActions!=null){
			Element imageActionEle = newElement(doc, parentEle, actionTag);
			imageActionEle.setAttribute(ResourceConstants.IRA_SKIP, String.valueOf(imageResourceActions.getSkip()));
			imageActionEle.setAttribute(ResourceConstants.IRA_TOTAL, String.valueOf(imageResourceActions.getTotalNumberImages()));
		}
		
	}
	
	protected void addBusyImageActionsTags(String actionTag,  MultiImageResourceAction imageResourceActions, Document doc, Element parentEle){
		
		if(imageResourceActions!=null){
			Element imageActionEle = newElement(doc, parentEle, actionTag);
			imageActionEle.setAttribute(ResourceConstants.BIR_ID, String.valueOf(imageResourceActions.getId()));
			imageActionEle.setAttribute(ResourceConstants.BIR_SKIP, String.valueOf(imageResourceActions.getSkip()));
			imageActionEle.setAttribute(ResourceConstants.BIR_TOTAL, String.valueOf(imageResourceActions.getTotalNumberImages()));
			imageActionEle.setAttribute(ResourceConstants.BIR_DIRECTORY, String.valueOf(imageResourceActions.getDirectory()));
			imageActionEle.setAttribute(ResourceConstants.BIR_INTERNAL_NAME, imageResourceActions.getInternalName());
		}
		
	}
	
	protected void addIdleImageTags(String idleTag,  MultiImageResourceAction idles, Document doc, Element parentEle){
		
		if(idles!=null){
			Element imageActionEle = newElement(doc, parentEle, idleTag);
			imageActionEle.setAttribute(ResourceConstants.IDLE_ID, String.valueOf(idles.getId()));
			imageActionEle.setAttribute(ResourceConstants.IDLE_TOTAL, String.valueOf(idles.getTotalNumberImages()));
			imageActionEle.setAttribute(ResourceConstants.IDLE_DIRECTORY, String.valueOf(idles.getDirectory()));
			imageActionEle.setAttribute(ResourceConstants.IDLE_INTERNAL_NAME, idles.getInternalName());
		}
		
	}
	
	protected String getStringFromIntArray(int[] array){
		
		if(array==null) return "0";
		
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < array.length; i++) {
			
			sb.append(array[i]);
			if(i < array.length-1) sb.append(",");
		}
		
		return sb.toString();
	}
	
	protected String getStringFromIntList(List<Integer> list){
		
		if(list==null) return "0";
		
		StringBuffer sb = new StringBuffer();
		
		for (int j = 0; j < list.size(); j++) {

			sb.append(list.get(j));
			if(j < list.size()-1) sb.append(",");
		}
		
		return sb.toString();
	}
	
	protected String getPipedStringWithCommasFromPointArray(Coords[] coords){
		
		if(coords==null) return "0|0,-1,-1,-1";
		
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < coords.length; i++) {
			
			if(coords[i]==null || !coords[i].isValid()) sb.append("-1");
			else sb.append(getPipedStringFromCoords(coords[i]));
			
			if(i < coords.length-1) sb.append(",");
		}
		return sb.toString();
		
	}
	
	protected String getPipedStringFromCoords(Coords coords){
		
		if(coords==null) return "-1|-1";
		StringBuffer sb = new StringBuffer();
		sb.append(coords.x);
		sb.append("|");
		sb.append(coords.y);
		return sb.toString();
	}

	protected String getBitmaskArrayStrFromGrid(boolean[][] grid){
		
		if(grid==null || grid.length==0 || grid[0].length==0) return "0";
		
		int[] ys = new int[grid[0].length];
		
		for (int y = 0; y < grid[0].length; y++) {
			
			for (int x = 0; x < grid.length; x++) {
				
				if(grid[x][y]){

					ys[y] = (int) (ys[y]+Math.pow(2, x));
				}
			}
		}
		
		String value = getStringFromIntArray(ys);
		
		return value;
		
	}
	


}
