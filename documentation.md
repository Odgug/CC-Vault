Skip To [Examples](#examples)
---
`getItemType()` 
Gets the type the item in the reader 
#### returns
- `string` The Item Type can be one of 
`"Charm","Trinket","Jewel","Inscription","Tool","Gear","Catalyst","Unknown"`
- `nil` if the slot is empty

---

`getToolDetails()` 
Gets the details of a Vault Tool in the reader
#### returns
`table` a table of type [Tool](#tool)
`nil` if the slot is empty
#### throws
- if the Item is not a vault Tool

---

`getJewelDetails()` 
Gets the details of a Vault Jewel in the reader
#### returns
`table` a table of type [Jewel](#jewel)
`nil` if the slot is empty
#### throws
- if the Item is not a Jewel

---

`getGearDetails()` 
Gets the details of a Vault Gear in the reader
#### returns
`table` a table of type [UnidentifiedGear](#unidentifiedgear) or [Gear](#gear)
`nil` if the slot is empty
#### throws
- if the Item is not Gear
---
`getInscriptionDetails()` 
Gets the details of a Inscription in the reader
#### returns
`table` a table of type [Inscription](#inscription)
`nil` if the slot is empty
#### throws
- if the Item is not an Inscription

---
`getCatalystDetails()` 
Gets the details of a Catalyst in the reader
#### returns
`table` a table of type [Catalyst](#catalyst)
`nil` if the slot is empty
#### throws
- if the Item is not an Catalyst


---
`getTrinketDetails()` 
Gets the details of a Trinket in the reader
#### returns
`table` a table of type [Trinket](#trinket)
`nil` if the slot is empty
#### throws
- if the Item is not an Trinket


---
`reader.getItemLevel()`
#### returns
- integer the items level

---
`reader.getRarity()`
 #### returns
- Rarity rarity the item rarity in full caps:

---
`reader.getRepairSlots()`
 #### returns
- integer slots max repair slots of the item

---
`reader.getUsedRepairSlots()`
 #### returns
- integer slots the amount of repair slots that have been used on the item

---
`reader.getImplicitCount()`
 #### returns
- integer count the amount of implicit slots the item has

---
`reader.getPrefixCount()`
 #### returns
- integer count the amount of prefix slots the item has (including empty ones)

---
`reader.getSuffixCount()`
 #### returns
- integer count the amount of suffix slots the item has (including empty ones)

---
`reader.getImplicits(index)`
 #### returns
- (Modifier|RolledModifier)[]

---
`reader.getPrefixes()`
 #### returns
- (Modifier|RolledModifier)[]

---
`reader.getSuffixes()`
 #### returns
- (Modifier|RolledModifier)[]


# Types
--- 
## Tool
- name `string` the name of the item
- type `"Tool"`
- level `integer` The Tool Level
- rarity [Rarity](#rarity) The Tool Rarity
- repairslots [RepairSlots](#repairslots)
- durability [Durability](#durability)
- implicits ([Modifier](#modifier))[]
- prefixes ([Modifier](#modifier))[]
- suffixes ([Modifier](#modifier))[]
---
## Rarity
`string` one of the following values
- SCRAPPY
- COMMON
- RARE
- EPIC
- OMEGA
---
## RepairSlots
- total `integer` Total Repair Slots
- used `integer` Slots Already Used Up
---
## Durability
- total `integer` Total Durability
- current `integer` Current Durability
---
## Modifier
- name `string` The modifier name
- value `string | integer | number` The value of the modifier
- legendary `?boolean` optional, the Modifier is legendary
- crafted `?boolean` optional, the Modifier is crafted
- unusual `?boolean` optional, the Modifier is unusual
- greater `?boolean` optional, the Modifier is greated
- frozen `?boolean` optional, the Modifier is frozen
---
## RolledModifier 
Extends [Modifier](#modifier) for modifiers that have some form of roll information
-  tier `integer` The rolled Tier
-  min `number` The maximum possible roll for the modifier
-  max `number` The minimum possible roll for the modifier
---
## Jewel
- name `string` the name of the item
- type `"Jewel"`
- level `integer` The Jewel Level
- rarity [Rarity](#rarity) The Jewel's Rarity
- implicits ([Modifier](#modifier)|[RolledModifier](#rolledmodifier))[]
- prefixes ([Modifier](#modifier)|[RolledModifier](#rolledmodifier))[]
- suffixes ([Modifier](#modifier)|[RolledModifier](#rolledmodifier))[]
---

## UnidentifiedGear
- name `string` the name of the item
- type `"Gear"`
- level `integer` The Tool Level
- rarity [Rarity](#rarity) The Gear's Rarity
- identified `false` Is The gear Identified
--- 

## Gear 
Identified Gear Has all the fields of [UnidientifiedGear](#unidentifiedgear) plus the following
Attributes is the field that holds stuff that is in gear but isn't part of implicits
for example base durability of an item, wether or not its soubound/living etc.
- identified `true` overrides the one set by [UnidientifiedGear](#unidentifiedgear)
- repairslots [RepairSlots](#repairslots)
- durability [Durability](#durability)
- attributes ([Modifier](#modifier)|[RolledModifier](#rolledmodifier))[]
- implicits ([Modifier](#modifier)|[RolledModifier](#rolledmodifier))[]
- prefixes ([Modifier](#modifier)|[RolledModifier](#rolledmodifier))[]
- suffixes ([Modifier](#modifier)|[RolledModifier](#rolledmodifier))[]

##  Trinket
Optional fields are populated only if the trinket is identified
- identified `boolean` Is the trinket Identified
- name `?string` optional,The name of the Trinket
- uses `?integer` optional,The number of uses left in the trinket
- slot `?string` optional, The slot the trinket uses

##  Inscription
- size `integer` The size of the Inscription
- rooms `string[]` The names of the rooms the Inscription adds

##  Catalyst
- size `integer` The size of the Catalyst
- modifiers `string[]` The resource Locations of the added vault effects eg: "the_vault:challenger_stack"

##  Charm
Optional fields are populated only if the charm is identified
- identified `boolean` Is the charm Identified
- god `?string` optional,The God associated with the charm
- uses `?integer` optional,The number of uses left in the charm
- prefixes ?[Modifier](#modifier)[] optional, The Prefixes on the Charm


# Examples
What follows is a set of examples, they are not complete and serve as a starting point for your code

```lua
-- Example on how to get the size of a jewel in the reader
local function getJewelSize()
	---@type vaultReader
	local reader = peripheral.find("vaultreader")

	if reader.getItemType() == "Jewel" then
		local details = reader.getJewelDetails()
		if details ~= nil then
			for _, mod in ipairs(details.implicits) do
				if mod.name == "Size" then
					return mod.value
				end
			end
		end
	end
	return nil
end
```
```


```
```lua
-- This Function returns a list of the names of the suffixes in the puece of gear
local function getSuffixes()
	---@type vaultReader
	local reader = peripheral.find("vaultreader")

	if reader.getItemType() == "Gear" then
		local details = reader.getGearDetails()
		local suffixes = {}
		if details ~= nil and details.identified == true then
			for _, mod in ipairs(details.suffixes) do
				table.insert(suffixes, mod.name)
			end
			return suffixes
		end
		return nil
	end
end
```
```

```lua

--- Example on how to check if a gear piece has Souldbound on it
local function hasSoulbound()
	---@type vaultReader
	local reader = peripheral.find("vaultreader")

	if reader.getItemType() == "Gear" then
		local details = reader.getGearDetails()
		if details ~= nil and details.identified == true then
			for _, mod in ipairs(details.attributes) do
				if mod.name == "Soulbound" then
					return true
				end
			end
			return false
		end
		return nil
	end
end
```
```
```

if for example one wants to check if a modifier is legendary one can do
`if mod.legendary then do_thing() end`



```lua

--- Example on getting the max a each suffix could have rolled
local function getMaxRolls()
	---@type vaultReader
	local reader = peripheral.find("vaultreader")

	local suffixes = {}
	if reader.getItemType() == "Gear" then
		local details = reader.getGearDetails()
		if details ~= nil and details.identified == true then
			for _, mod in ipairs(details.suffix) do
				if mod.roll then
					suffixes[mod.name] = mod.roll.max
				end
			end
			return suffixes
		end
		return nil
	end
end
```
```
```
