package data.map.resources;

public class BuildingAreaResource {

    public BuildingAreaResource() {
		super();
	}

	public BuildingAreaResource(boolean isFixed, int areaSize) {
		this.isFixed = isFixed;
		this.areaSize = areaSize;
	}

	private boolean isFixed;
    private int areaSize;

    public int getAreaSize() {
        return areaSize;
    }

    protected void setAreaSize(int areaSize) {
        this.areaSize = areaSize;
    }

    public boolean isFixed() {
        return isFixed;
    }

    protected void setFixed(boolean isFixed) {
        this.isFixed = isFixed;
    }
}
