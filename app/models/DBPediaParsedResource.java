package models;

import java.util.HashSet;
import java.util.Set;


public class DBPediaParsedResource implements Comparable<DBPediaParsedResource> {

	public String uri;
	public int count;
	public Set<String> surfaceForm = new HashSet<String>();
	public String types;
	
	public DBPediaParsedResource(String uri, String surfaceForm,  String types) {
		super();
		//this.surfaceForm = (List<String>) new HashSet();
		this.surfaceForm.add(surfaceForm);
		this.uri = uri;
		this.types = types;
		this.count = 1;
	}

	public String getSurfaceForms() {
			
		return surfaceForm.toString();
	}

	@Override
	public int compareTo(DBPediaParsedResource o) {
		// TODO Auto-generated method stub
		
		return this.count - o.count;
	}
}
