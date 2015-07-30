package com.dgo.alarm.io;

public abstract class IDBElement {
	private long uniqueID;


    public IDBElement(){
        super();
        uniqueID = IDBTable.INVALID_ID;
    }
	public final long getID(){
		return  uniqueID;
	}
	public final void setID(long id){
		uniqueID = id;
	}

	public final boolean hasBeenStored(){
		return uniqueID != IDBTable.INVALID_ID;
	}
}
