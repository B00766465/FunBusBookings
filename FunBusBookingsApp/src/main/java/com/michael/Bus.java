package com.michael;

class Bus{
    private String destination;
    private int capacity;
    private String feature;
    private String accessiblity;

    Bus(){
        
    }//Default Constructor

    public Bus(String destination, int capacity, String feature, String accessiblity){
        this.destination = destination;
        this.capacity = capacity;
        this.feature = feature;
        this.accessiblity = accessiblity;
    }//Alternate Constructor
	
	//Get methods
    public String getDestination(){return destination;}
    public int getCapacity(){return capacity;}
    public String getFeature(){return feature;}
    public String getAccessibility(){return accessiblity;}
}