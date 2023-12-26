-- Adapt according to setup

-- For me, a "Jewel sorting group" is: 
--  - 1 computer to run this code, with ender Modem
--  - 1 vault reader 
--  - 2 modular router, one sending the item to a scrapping setup (Vault Recycler), one sending item to a saving setup (chest, RS, ...)
-- All are connected together by a networking cable and wired Modem

-- The jewel are inserted into the vault reader by an external MR

local reader = peripheral.wrap("vaultreader_1")

local recycler = peripheral.wrap("modularrouters:modular_router_4")

local output = peripheral.wrap("modularrouters:modular_router_3")

-- See the other example for a weighted filter system. I had very specific needs for my jewels so I kept it simple

-- Check if size is < 22, I don't want a jewel I can't cut to 10

function checkMaxSize()

    local size = reader.getModifierValue(reader.getImplicit(0))

    if size < 22 then
        return true
    end

    return false
end

-- Keep if the gear has a legendary modifier

function checkAffixLeg(affix)

    if reader.getType(affix) == "legendary" then
        return true
    end

    return false
end

function checkLegendary()
    for i = 0, reader.getSuffixCount()-1,1 do
        if checkAffixLeg(reader.getSuffix(i)) then 
            return true 
        end
    end

    return false
end

----------

-- Keep if the jewels has 3-4 suffix  

function checkPerfect()
    local counter = 0

    for i = 0, reader.getSuffixCount()-1,1 do
        counter = counter + 1
    end

    if counter >= 3 then
        return true
    end

    return false
end

----------

-- Keep if the jewels is size 10 already

function checkSize10()
    local size = reader.getModifierValue(reader.getImplicit(0))

    if size == 10 then
        return true
    end

    return false
end


-- For Jaymin on Discord, keep any copious >= 4.8 with size <=15
function jayminFilter()
    local size = reader.getModifierValue(reader.getImplicit(0))

    if size <= 15 then
        for i = 0, reader.getSuffixCount()-1,1 do
            local affix = reader.getSuffix(i)
            local val = reader.getModifierValue(affix)
            local name = reader.getName(affix)

            if name == "Copiously" and val >= 4.8 then return true end
                 
        end
    end

    return false
end
----------

-- Parse the jewel "Flawless Jewel" -> "Flawless" 

function getWordNumber(inputString,number)
    local words = {}
    for word in inputString:gmatch("%S+") do
        table.insert(words, word)
    end

    return words[number]
end
---------

-- Main funct

print("Started jewel monitoring!")

rednet.open("left") -- Adapt depending on where the Ender Modem is
local centralComputerID = 4 -- Adapt based on ID of the computer collecting the statistics

while true do

    if reader.getItemDetail(1) ~= nil then

        local name = getWordNumber(reader.getItemDetail(1).displayName,2)
        local rarity = getWordNumber(reader.getItemDetail(1).displayName,1)

        print("Currently checking a new : "..name)

        local keep_reason = "none"
        
        -- Get information on gear
        
        if checkLegendary() then
            keep_reason = "Legendary"
        elseif checkPerfect() then 
            keep_reason = "Perfect"
        elseif checkSize10() then
            keep_reason = "Size10"
        end
        

        -- Send information to main computer
        rednet.send(centralComputerID, "JewelSorted|"..name.."|"..rarity.."|"..keep_reason)


        -- Modified version because I only keep size < 22 Jewels
    
        -- DELETE FROM HERE
        if not checkMaxSize() then keep_reason = "none" end
        -- TO HERE to keep basic version


        -- Basic version of the code which keeps all Legendary, all Perfect/Flawless and all "Special" (according to my/your filters) Gears
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