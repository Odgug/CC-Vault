---@meta

---@alias Rarity
---| "SCRAPPY"
---| "COMMON"
---| "RARE"
---| "EPIC"
---| "OMEGA"

---@class vaultReader: inventory
local reader = {}

---@return integer level level of the item inside the reader
function reader.getItemLevel() end

---@return Rarity rarity the item rarity in full caps:
function reader.getRarity() end

---@return integer slots max repair slots of the item
function reader.getRepairSlots() end

---@return integer slots the amount of repair slots that have been used on the item
function reader.getUsedRepairSlots() end

---@return integer count the amount of implicit slots the item has
function reader.getImplicitCount() end

---@return integer count the amount of prefix slots the item has (including empty ones)
function reader.getPrefixCount() end

---@return integer count the amount of suffix slots the item has (including empty ones)
function reader.getSuffixCount() end



---@class modifierString: string

---@param index integer # 0 indexed
---@return modifierString modifier **modifierString**
--- returns "null" if index out of bounds
function reader.getImplicit(index) end

---@param index integer # 0 indexed
---@return modifierString modifier **modifierString**
--- returns "null" if index out of bounds
--- returns "empty" if modifier slot is empty
function reader.getPrefix(index) end

---@param index integer # 0 indexed
---@return modifierString modifier **modifierString**
--- returns "null" if index out of bounds
--- returns "empty" if modifier slot is empty
function reader.getSuffix(index) end

---@param modifier modifierString
---@return number # numerical value of modifier
-- **1.6 Attack Speed** -> 1.6
-- **Size 15** -> 15
-- **15% Undead Damage** -> 15
-- **Poison IV Cloud** -> 4
-- **Soulbound** -> 1
function reader.getModifierValue(modifier) end

---@param modifier modifierString
---@return number max maximum roll for a modifier of current tier
-- **1.6 Attack Speed(1.2-1.8)** -> 1.8
-- **15% Undead Damage(10%-45%)** -> 45
-- **Poison IV Cloud** -> 4
-- **Soulbound** -> 1
function reader.getMaximumRoll(modifier) end

---@param modifier modifierString
---@return number min minimum roll for a modifier of current tier
-- **1.6 Attack Speed(1.2-1.8)** -> 1.2
-- **15% Undead Damage(10%-45%)** -> 10
-- **Poison IV Cloud** -> 4
-- **Soulbound** -> 1
function reader.getMinimumRoll(modifier) end

---@param modifier modifierString
---@return string type 
-- **15% Undead Damage** -> "UndeadDamage"
-- **Chilling IV Cloud** -> "ChillingCloud"
function reader.getName(modifier) end

---@param modifier modifierString
---@return 
---| 'empty' # empty modifier slot
---| 'null' # has not been found on a piece
---| 'crafted' # modifier that's been crafted onto the gear piece via the modifier workbench
---| 'legendary' # legendary roll modifier
---| 'regular' # all else
function reader.getType(modifier) end

