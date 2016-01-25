package com.sillysoft.tools;

/**

Interface for objects that can be serialized into XML and back.

**/

public interface XMLSerializable 
{

public String toStringXML();

// This is probably better as a static method in each implementing class?
// public XMLSerializable fromStringXML(String XML);

}
