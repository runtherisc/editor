package game.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.map.resources.BuildingItemResource;
import data.map.resources.ItemMakeResource;
import data.map.resources.Resource;

public class Warehouse extends AbstractBuilding{



    public Warehouse(long buildingNumber, int x, int y, int resourceId) {
        super(buildingNumber, x, y, resourceId);

        setupFirstCreationTimes(resourceId);
    }

	List<PendingTransfer> pendingTransfers = new ArrayList<PendingTransfer>();

    Set<Integer> autoTransferItems = new HashSet<Integer>();

    Map<Integer, CreateRequirement> createItemTimes = new HashMap<Integer, CreateRequirement>();

    private void setupFirstCreationTimes(int resourceId){

        Map<Integer, BuildingItemResource> warehouseItems = Resource.getBuildingResourceById(resourceId).getBuildingItemMap();

        if(warehouseItems!=null){

            for(int item : warehouseItems.keySet()){

                BuildingItemResource itemResource = warehouseItems.get(item);

                List<ItemMakeResource> itemMakeResource = itemResource.getWarehouseMake();

                if(itemMakeResource!=null && !itemMakeResource.isEmpty()){

                    ItemMakeResource itemMake = itemMakeResource.get(0);

                    CreateRequirement create = new CreateRequirement
                            (0,//position in make array
                             itemMake.getMakeRequirements().isEmpty(),//autocreate
                             itemMake.getFrequency());

                    createItemTimes.put(item, create);

                }
            }
        }
    }

}
