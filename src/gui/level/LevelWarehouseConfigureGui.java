package gui.level;

import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import data.map.resources.BuildingResource;
import data.map.resources.Resource;
import game.items.Warehouse;
import gui.PropertyKeys;

public class LevelWarehouseConfigureGui extends BaseMapAmountsGui {

	public LevelWarehouseConfigureGui(String title, JFrame parent) {
		super(title, parent);

	}
	
	private Warehouse warehouse;
	private BuildingResource buildingResource;
	
	@Override
	protected void passedBundle(Map<String, Object> properties) {
	
		warehouse = (Warehouse) properties.get(PropertyKeys.LEVEL_MAP_WAREHOUSE);
		buildingResource = Resource.getBuildingResourceById(warehouse.getResourceId());

	}

	@Override
	protected int getMaxAmountForItem(int itemId) {

		return buildingResource.getBuildingItemMap().get(itemId).getAmount();
	}
	
	protected Set<Integer> getAllItems(){
		
		return buildingResource.getBuildingItemMap().keySet();
	}

	@Override
	protected short getAmountForItem(int itemId) {
	
		return warehouse.getStoredItemAmount(itemId);
	}
	
	@Override
	protected void addItemAndAmount(int item, short amount){
		
		warehouse.fillStoreItems(item, amount);
	}
	
	@Override
	protected String getHelpText() {
		return new StringBuilder("Warehouse Item Configuration\n\n")
						 .append("This is where you can configure how many of each item a warehouse can hold.\n\n")
						 .append("Click the row of the item you wish to update and then enter the amount in the Select Row box.\n\n")
						 .append("When you are happy with the adjusted amount, click the Update button to update the row.").toString();
	}

}
