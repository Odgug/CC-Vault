local reader = peripheral.wrap("vaultreader_2")

local input = peripheral.wrap("minecraft:chest_6")

local recycler = peripheral.wrap("minecraft:chest_5")

local output = peripheral.wrap("minecraft:chest_7")

local Weights = {}

local unavailableWeight = 1

local playerLevel = 100

local requiredWeight = 55

Weights["Implicit"] = {}
Weights["Prefix"] = {}
Weights["Suffix"] = {}

Weights["Implicit"]["Armor"] = 10
Weights["Implicit"]["BlockChance"] = 20
Weights["Implicit"]["Durability"] = 0.0001

Weights["Prefix"]["Armor"] = 20
Weights["Prefix"]["Resistance"] = 10
Weights["Prefix"]["Health"] = 15
Weights["Prefix"]["IncreasedAttackDamage"] = 25
Weights["Prefix"]["empty"] = 15


Weights["Suffix"]["ManaRegen"] = 15
Weights["Suffix"]["ItemQuantity"] = 12
Weights["Suffix"]["ItemRarity"] = 10
Weights["Suffix"]["empty"] = 10



function getMultiplier(val, min, max)
    if min == max then
        return 1.5
    end
    return (val-min)/(max-min)+0.5
    
end

function getAffixWeight(affix, type)
    if reader.getType(affix) == "legendary" then
        return 1000
    end
    local name = reader.getName(affix)
    local min = reader.getMinimumRoll(affix)
    local max = reader.getMaximumRoll(affix)
    local val = reader.getModifierValue(affix)
    
    if Weights[type][name] == nil then
        return unavailableWeight* getMultiplier(val,min,max)
    end
    return getMultiplier(val,min,max)*Weights[type][name]
    
end

function parseImplicits()
    local toReturn = 0
    for i = 0, reader.getImplicitCount()-1,1 do
        toReturn = toReturn + getAffixWeight(reader.getImplicit(i), "Implicit")
    end
    return toReturn
end

function parsePrefixes()
    local toReturn = 0
    for i = 0, reader.getPrefixCount()-1,1 do
        toReturn = toReturn + getAffixWeight(reader.getPrefix(i), "Prefix")
    end
    return toReturn
end

function parseSuffixes()
    local toReturn = 0
    for i = 0, reader.getSuffixCount()-1,1 do
        toReturn = toReturn + getAffixWeight(reader.getSuffix(i),"Suffix")
    end
    return toReturn
end
function parseRarity()
    local rarity = reader.getRarity()
    if rarity == "OMEGA" then return 10000 end
    if rarity == "EPIC" then return 60 end
    if rarity == "RARE" then return 20 end
    if rarity == "COMMON" then return 5 end
    if rarity == "SCRAPPY" then return -10 end
    return 0
    end

function getWeight()
    local weight = 0
    print(parseRarity())
    weight = weight + parseRarity()
    print(parseImplicits())
    weight = weight + parseImplicits()
    print(parsePrefixes())
    weight = weight + parsePrefixes()
    print(parseSuffixes())
    weight = weight + parseSuffixes()
    print(weight)
    return weight
end
function shouldKeep()
    return getWeight()>requiredWeight
end

local keep = shouldKeep()
print(keep)

print("Here's the TEST EXAMPLE algorithm, use the inventory API alongside it to make your own auto system, thats outside the bounds of this reader API demonstration tho, GL and enjoy the addon - Joseph")

    

