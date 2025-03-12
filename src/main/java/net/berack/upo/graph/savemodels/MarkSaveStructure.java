package net.berack.upo.graph.savemodels;

/**
 * Support class used for saving a Graph in a file.
 * 
 * @author Berack96
 *
 */
public class MarkSaveStructure {
	protected MarkSaveStructure(String v, Object m) {
		this.vert = v;
		this.mark = m;
	}
	
	public String vert;
	public Object mark;
}
