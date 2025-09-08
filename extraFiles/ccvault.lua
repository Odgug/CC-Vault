---@meta

---@alias Rarity
---| "SCRAPPY"
---| "COMMON"
---| "RARE"
---| "EPIC"
---| "OMEGA"

---@alias ItemTypes
---|"Charm"
---|"Trinket"
---|"Jewel"
---|"Inscription"
---|"Tool"
---|"Gear"
---|"Catalyst"
---|"Charm"
---|"Unknown"


---@class Modifier
---@field name string
---@field value string |  number | boolean
---@field legendary? boolean
---@field crafted? boolean
---@field unusual? boolean
---@field greater? boolean
---@field frozen? boolean

---@class RolledModifier : Modifier
---@field roll Roll

---@class Roll
---@field tier  integer
---@field min  number
---@field max  number

---@class RepairSlots
---@field total integer Total Repair Slots
---@field used integer Slots Already Used Up

---@class Durability
---@field total integer Total Durability
---@field current integer Current Durability

---@class Jewel
---@field name string the name of the item
---@field type "Jewel"
---@field level integer The Jewel Level
---@field rarity Rarity The Jewels Rarity
---@field implicits (Modifier|RolledModifier)[]
---@field prefixes (Modifier|RolledModifier)[]
---@field suffixes (Modifier|RolledModifier)[]

---@class Tool
---@field name string the name of the item
---@field type "Tool"
---@field level integer The Tool Level
---@field rarity Rarity The Tool Rarity
---@field repairslots RepairSlots
---@field durability Durability
---@field implicits (Modifier)[]
---@field prefixes (Modifier)[]
---@field suffixes (Modifier)[]

---@class UnidentifiedGear
---@field name string the name of the item
---@field type "Gear"
---@field level integer The Tool Level
---@field rarity Rarity The Tool Rarity
---@field identified false Is The gear Identified

---@class Gear : UnidentifiedGear
---@field identified true
---@field repairslots RepairSlots
---@field durability Durability
---@field attributes (Modifier|RolledModifier)[]
---@field implicits (Modifier|RolledModifier)[]
---@field prefixes (Modifier|RolledModifier)[]
---@field suffixes (Modifier|RolledModifier)[]

---@class Trinket
---@field identified boolean
---@field uses? integer
---@field slot? string
---@field name? string

---@class Inscription
---@field size integer
---@field rooms string[]

---@class Catalyst
---@field size integer
---@field modifiers string[]


---@class Charm
---@field identified boolean
---@field uses? integer
---@field god? string
---@field prefixes? Modifier[]

---@class vaultReader: ccTweaked.peripheral.Inventory
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

---@return (Modifier|RolledModifier)[]
function reader.getImplicits(index) end

---@return (Modifier|RolledModifier)[]
function reader.getPrefixes() end

---@return (Modifier|RolledModifier)[]
function reader.getSuffixes() end

---@return ItemTypes |
---| nil # if the slot is empty
function reader.getItemType() end

---@return Jewel
---| nil # if the slot is empty
---@throws If the item isnt Jewel
function reader.getJewelDetails() end

---@return Tool
---| nil # if the slot is empty
---@throws If the item isnt a Tool
function reader.getToolDetails() end

---@return UnidentifiedGear | Gear |nil
---@throws If the item isnt Gear
function reader.getGearDetails() end

---@return Trinket |nil
---@throws If the item isnt an Trinket
function reader.getTrinketDetails() end

---@return Inscription |nil
---@throws If the item isnt an Inscription
function reader.getInscriptionDetails() end

---@return Catalyst |nil
---@throws If the item isnt a Catalyst
function reader.getCatalystDetails() end
