package gr.uom.java.jdeodorant.refactoring.views;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.Position;
public class MessageChainStructure {
	private Integer startPos;
	private MessageChainStructure parent;
	private String name;
	private List<MessageChainStructure> childList;
	private int length;
	
	/**
	 * Constructor about MessageChainStructure class
	 * 
	 * <Arguments>
	 * String _name: Name of Massage Chain
	 * **/
	public MessageChainStructure(String _name) {
		parent = null;
		startPos = -1;
		length = -1;
		name = _name;
		childList = new ArrayList<MessageChainStructure>();
	}
	
	/**
	 * Constructor about MessageChainStructure class
	 * 
	 * <Arguments>
	 * Integer _startPos: Start position about detected code
	 * MessageChainStructure: Information of parent
	 * String _name: Name of Massage Chain
	 * **/
	public MessageChainStructure(Integer _startPos, MessageChainStructure _parent, String _name) {
		startPos = _startPos;
		parent = _parent;
		name = _name;
		childList = new ArrayList<MessageChainStructure>();
		length = -1;
	}
	
	/**
	 * Constructor about MessageChainStructure class
	 * 
	 * <Arguments>
	 * Integer _startPos: Start position about detected code
	 * MessageChainStructure: Information of parent
	 * String _name: Name of Massage Chain
	 * int _lenght: Length about detected code
	 * **/
	public MessageChainStructure(Integer _startPos, MessageChainStructure _parent, String _name,int _length) {
		startPos = _startPos;
		parent = _parent;
		name = _name;
		length = _length;
		childList = new ArrayList<MessageChainStructure>();
	}
		
	/**
	 * Function for getting start position of detected code
	 * **/
	public Integer getStart() {
		return startPos;
	}
	
	/**
	 * Function for getting information of parent
	 * **/
	public MessageChainStructure getParent() {
		return parent;
	}
	
	/**
	 * Function for getting name
	 * **/
	public String getName() {
		return name;
	}
	
	/**
	 * Function for getting length of detected code
	 * **/
	public int getLength() {
		return length;
	}
	
	/**
	 * Function for getting List of information of children
	 * **/
	public List<MessageChainStructure> getChildren(){
		return childList;
	}
	
	/**
	 * Function for adding new child to List of children
	 * 
	 * <Arguments>
	 * MessageChainStructure _msgstructure: information about new child
	 * **/
	public boolean addChild(MessageChainStructure _msgstructure) {
		return childList.add(_msgstructure);
	}
	
	/**
	 * Function for removing new child in List of children
	 * 
	 * <Arguments>
	 * MessageChainStructure _msgstructure: information about child which User wants to remove
	 * **/
	public boolean removeChild(MessageChainStructure _msgstructure) {			
		return childList.remove(_msgstructure);
	}
	
	/**
	 * Function for getting number of children
	 * **/
	public int getSize() {
		return childList.size();
	}																						
}
