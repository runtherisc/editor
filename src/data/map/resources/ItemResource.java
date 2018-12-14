package data.map.resources;

public class ItemResource {
	
	private String name;	
	private int id;
	private WorkerImageResource resource;
	private InfoResource texts = new InfoResource();

	public String getLocalizedName() {

		String name = texts.getText(ResourceConstants.INFO_TYPE_TITLE);

		if(name==null) name = getName();

		return name;
	}

	public String getDescription(){

		String name = texts.getText(ResourceConstants.INFO_TYPE_DESCRIPTION);

		if(name==null) name = "";

		return name;
	}

	public InfoResource getInfoResource() {
		return texts;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public WorkerImageResource getResource() {
		return resource;
	}

	public void setResource(WorkerImageResource resource) {
		this.resource = resource;
	}

}
