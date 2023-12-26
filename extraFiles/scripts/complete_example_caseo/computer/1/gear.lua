-- Adapt according to setup

-- For me, a "Gear sorting group" is: 
--  - 1 computer to run this code, with ender Modem
--  - 1 vault reader 
--  - 2 modular router, one sending the item to a scrapping setup (Vault Recycler), one sending item to a saving setup (chest, RS, ...)
-- All are connected together by a networking cable and wired Modem

-- The gears are inserted into the vault reader by an external MR which identifies it against a Identification table with an activator module

local reader = peripheral.wrap("vaultreader_0")

local recycler = peripheral.wrap("modularrouters:modular_router_0")

local output = peripheral.wrap("modularrouters:modular_router_2")

-- See the other example for a weighted filter system. I had very specific needs for my gears so I kept it simple

-- Check if the gear has a legendary modifier

function checkAffixLeg(affix)
    if reader.getType(affix) == "legendary" then
        return true
    end

    return false
end

function checkLegendary()
    for i = 0, reader.getPrefixCount()-1,1 do
        if checkAffixLeg(reader.getPrefix(i)) then 
            return true 
        end
    end
    for i = 0, reader.getSuffixCount()-1,1 do
        if checkAffixLeg(reader.getSuffix(i)) then 
            return true 
        end
    end

    return false
end

-- Modified version used for my specifics needs, see in the main code

-- Function to check if a string is in the list
function isInList(wordToCheck,listToCheck)
    for _, str in ipairs(listToCheck) do
        if str == wordToCheck then
            return true
        end
    end
    return false
end


local useless_leg = {"KnockbackResist","TrapDisarmChance","HealingEfficiency","Durability"}
local avoidance = {"PoisonAvoidance","WitherAvoidance","FatigueAvoidance","TimeAvoidance","WeaknessAvoidance","SlownessAvoidance"}

function checkGoodLegendary()
    
    local keep_it = true

    for i = 0, reader.getPrefixCount()-1,1 do
        local affix = reader.getPrefix(i)
        if reader.getType(affix) == "legendary" then
            local name = reader.getName(affix)

            -- Remove useless leg
            if isInList(name,useless_leg) then 
                keep_it = false 
            end
            -- Keep only 100% avoidance
            if isInList(name,avoidance) then
                local value = reader.getModifierValue(affix)
                if value < 100 then 
                    keep_it = false
                end
            end
        end
    end

    for i = 0, reader.getSuffixCount()-1,1 do
        local affix = reader.getSuffix(i)
        if reader.getType(affix) == "legendary" then
            local name = reader.getName(affix)

            -- Remove useless leg
            if isInList(name,useless_leg) then 
                keep_it = false
            end
            -- Keep only 100% avoidance
            if isInList(name,avoidance) then
                local value = reader.getModifierValue(affix)
                if value < 100 then
                    keep_it = false
                end
            end     
        end
    end

    local rarity = reader.getRarity()
    if rarity =="SCRAPPY" or rarity == "COMMON" then keep_it = false end

    return keep_it
end

----------

-- Check if the gear is Omega 

function checkOmega()
    local rarity = reader.getRarity()
    if rarity == "OMEGA" then 
        return true 
    end

    return false
end

----------

-- Keep if the gear is Important to me
-- Add personal filter according to what you need/want, falls into the "Special" Category

-- Keep epic Helmet with 5+ Lucky Hit Chance
function highLuckyImplicit()
    for i = 0, reader.getImplicitCount()-1,1 do
        affix = reader.getImplicit(i)
        local name = reader.getName(affix)
        local val = reader.getModifierValue(affix)

        if name == "LuckyHitChance" and val >= 5 then
            return true
        end
    end

    return false
end

-- Keep epic ChestPlate/Leggings with scatter +1/2
function scatterPrefix()
    for i = 0, reader.getPrefixCount()-1,1 do
        affix = reader.getPrefix(i)
        local name = reader.getName(affix)

        if name == "tolevelofScatterJavelin" then
            return true
        end
    end

    return false
end

-- Keep epic Boots with 18+ MovementSpeed 
function highSpeedImplicit()
    for i = 0, reader.getImplicitCount()-1,1 do
        affix = reader.getImplicit(i)
        local name = reader.getName(affix)
        local val = reader.getModifierValue(affix)

        if name == "MovementSpeed" and val >= 18 then
            return true
        end
    end

    return false
end




-- check important stuff

function checkImportant()
    local rarity = reader.getRarity()
    if rarity == "EPIC" then 
        if highLuckyImplicit() then return true end
        if scatterPrefix() then return true end
        if highSpeedImplicit() then return true end
    end

    return false 
end    


-- Parse the name "Vault Boots" -> "Boots" but "Focus" -> "Focus"

function getSecondWord(inputString)
    local words = {}
    for word in inputString:gmatch("%S+") do
        table.insert(words, word)
    end

    -- Return the second word if it exists, otherwise return the only word or nil
    return words[2] or words[1]
end

----------

-- Main funct

print("Started gear monitoring!")

rednet.open("right") -- Adapt depending on where the Ender Modem is
local centralComputerID = 4 -- Adapt based on ID of the computer collecting the statistics

while true do

    if reader.getItemDetail(1) ~= nil then
        
        local name = getSecondWord(reader.getItemDetail(1).displayName)
        local rarity = reader.getRarity()

        print("Currently checking a new : "..name)

        local keep_reason = "none"
        
        -- Get information on gear
        if checkLegendary() then
            keep_reason = "Legendary"
        elseif checkOmega() then 
            keep_reason = "Omega"
        elseif checkImportant() then
            keep_reason = "Special"
        end

        -- Send information to main computer
        rednet.send(centralComputerID, "GearSorted|"..name.."|"..rarity.."|"..keep_reason)


        -- Modified version because I've stored enough Omega gear of each type, and only care about specific type of
        --  legendary modifiers on Rare+ only gear
        
        -- DELETE FROM HERE
        if keep_reason == "Omega" then keep_reason = "none" end

        if keep_reason == "Legendary" then
            if not checkGoodLegendary() then keep_reason = "none" end
        end
        -- TO HERE to keep basic version

        -- Basic version of the code which keeps all Legendary, all Omega and all "Special" (according to my/your filters) Gears
        -- Do something with gear
        if keep_reason ~= "none" then
            print("Should keep it")
            reader.pushItems(peripheral.getName(output),1)

        else
            print("Scrap it")
            reader.pushItems(peripheral.getName(recycler),1)
        end
    end
    sleep(0.1)
end