package gui.level;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import data.map.MapObjectItem;
import data.map.resources.MapItemResource;
import data.map.resources.Resource;
import gui.PropertyKeys;

public class LevelMapItemConfigureGui extends BaseMapAmountsGui {

	public LevelMapItemConfigureGui(String title, JFrame parent) {
		super(title, parent);
	}
	
	MapObjectItem mapItem;
	MapItemResource mapItemResource;

	@Override
	protected void passedBundle(Map<String, Object> properties) {
		
		mapItem = (MapObjectItem) properties.get(PropertyKeys.LEVEL_MAP_MAPITEM);
		mapItemResource = Resource.getMapItemResourceById(mapItem.getItemId());
		
	}

	@Override
	protected Set<Integer> getAllItems() {

		return new HashSet<Integer>(mapItemResource.getItemIdsFromAttrubutes());
	}

	@Override
	protected int getMaxAmountForItem(int itemId) {

		return mapItemResource.getAttributeMaxFromItemId(itemId);
	}

	@Override
	protected short getAmountForItem(int itemId) {

		return mapItem.getAmount(itemId);
	}

	@Override
	protected void addItemAndAmount(int item, short amount) {
		
		mapItem.putAmount(item, amount);
		
	}

	@Override
	protected String getHelpText() {
		return new StringBuilder("Map Item Configuration\n\n")
						 .append("This is where you can configure how many of each item a Map Item can hold.\n\n")
						 .append("Click the row of the item you wish to update and then enter the amount in the Select Row box.\n\n")
						 .append("When you are happy with the adjusted amount, click the Update button to update the row.").toString();
	}

}
