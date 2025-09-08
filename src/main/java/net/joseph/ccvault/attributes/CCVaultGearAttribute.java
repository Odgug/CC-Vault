package net.joseph.ccvault.attributes;

import java.util.HashMap;
import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;
import net.joseph.ccvault.interfaces.ILuaTable;

public class CCVaultGearAttribute implements ILuaTable{
    protected String name;
    protected AffixCategorySet categories = new AffixCategorySet();


    public CCVaultGearAttribute(String name, AffixCategorySet categories ){
        this.name = name;
        this.categories = categories;
    }



    @Override
    public HashMap<String, Object> toLuaTable(){
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("name", this.name);
        this.categories.stream().forEach(c->{
            map.put(c.getTooltipDescriptor().toLowerCase(), true);
        });
        return map;
    }
}