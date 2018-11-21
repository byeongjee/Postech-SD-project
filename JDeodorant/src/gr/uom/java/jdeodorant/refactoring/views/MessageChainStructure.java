package gr.uom.java.jdeodorant.refactoring.views;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.Position;

import java.util.ArrayList;
import java.util.LinkedHashMap;
public class MessageChainStructure {
	private Integer startPos;
	private MessageChainStructure parent;
	private String name;
	private List<MessageChainStructure> childList;
	private int length;
	public MessageChainStructure(String _name) {
		parent = null;
		startPos = -1;
		length = -1;
		name = _name;
		childList = new ArrayList<MessageChainStructure>();
	}

    public Object[] getHighlightPositions() {
    	Map<Position, String> annotationMap = new LinkedHashMap<Position, String>();
    	Position position = new Position(startPos, length);
		annotationMap.put(position, "HELLO");
		return new Object[] {annotationMap};
		
    }
	public MessageChainStructure(Integer _startPos, MessageChainStructure _parent, String _name) {
		startPos = _startPos;
		parent = _parent;
		name = _name;
		childList = new ArrayList<MessageChainStructure>();
		length = -1;
	}
	
	public MessageChainStructure(Integer _startPos, MessageChainStructure _parent, String _name,int _length) {
		startPos = _startPos;
		parent = _parent;
		name = _name;
		length = _length;
		childList = new ArrayList<MessageChainStructure>();
	}
	
	public Integer getStart() {
		return startPos;
	}
	public MessageChainStructure getParent() {
		return parent;
	}
	public String getName() {
		return name;
	}
	public int getLength() {
		return length;
	}
	public List<MessageChainStructure> getChildren(){
		return childList;
	}
	public boolean addChild(MessageChainStructure _msgstructure) {
		return childList.add(_msgstructure);
	}
	public boolean removeChild(MessageChainStructure _msgstructure) {			
		return childList.remove(_msgstructure);
	}
	public int getSize() {
		return childList.size();
	}																						
}
