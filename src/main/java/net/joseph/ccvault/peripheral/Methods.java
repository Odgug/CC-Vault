package net.joseph.ccvault.peripheral;

import dan200.computercraft.api.lua.LuaException;

public class Methods {
    public static void assertBetween( double value, double min, double max, String message ) throws LuaException
    {
        if( value < min || value > max || Double.isNaN( value ) )
        {
            throw new LuaException( String.format( message, "between " + min + " and " + max ) );
        }
    }
}
