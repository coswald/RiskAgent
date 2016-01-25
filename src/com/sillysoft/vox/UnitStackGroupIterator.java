package com.sillysoft.vox;

//***************************************************************
//
//  Author: Bertrand Senecal,
//          bertrand.senecal@gmail.com
//          July 2007
//
//***************************************************************


/**
 * An Iterator that outputs all the UnitStack in a UnitStackGroup.
 * It will handle cases where the current UnitStack is deleted from the UnitStackGroup.
 */

import com.sillysoft.vox.*;
import com.sillysoft.tools.*;
import com.sillysoft.vox.unit.*;


public class UnitStackGroupIterator
{
    private UnitStackGroup usg;     // The usg we wish to iterate
    private int i;                  // The index of the next UnitStack to return
    private int usgSize;            // To verify if the usg changes size
    
    
    /**
     *  Extract the UnitStackGroup from Country "from" and create a UnitStackGroupIterator
     */
    public UnitStackGroupIterator(Country from)
    {
        usg = from.getUnitStackGroup();
        usgSize = usg.size();
        i = 0;
    }
    
    
    /**
     *  Create a UnitStackGroupIterator
     */
    public UnitStackGroupIterator(UnitStackGroup usg)
    {
        this.usg = usg;
        usgSize = usg.size();
        i = 0;
    }
    
    
    /**
     *  Is a next UnitStack available?
     */
    public boolean hasNext()
    {
        checkUsgSize();
        return i < usg.size();
    }
    
    
    /**
     *  Get the  next UnitStack
     */
    public UnitStack next()
    {
        checkUsgSize();
        if (i < usg.size())
            return usg.get(i++);
        else
            return null;
    }
    
    
    /**
     *  If the current UnitStack was deleted, back up the index
     */
    private void checkUsgSize()
    {
        if (usg.size() != usgSize)
        {
            usgSize = usg.size();
            i = Math.max(0, --i);
        }
    }
}
