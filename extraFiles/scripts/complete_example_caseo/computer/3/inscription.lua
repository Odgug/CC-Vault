-- Adapt according to setup

-- For me, a "Inscription sorting group" is: 
--  - 1 computer to run this code, with ender Modem
--  - 1 vault reader 
--  - 3 modular router, one sending the item to a scrapping setup (Vault Recycler), one sending item to a Blank inscription saving setup (chest, RS, ...), one sending item to a Room inscription saving setup (chest, RS,..., can be combined with previous one)
-- All are connected together by a networking cable and wired Modem

-- The Inscriptions are inserted into the vault reader by an external MR

local reader = peripheral.wrap("vaultreader_2")

local recycler = peripheral.wrap("modularrouters:modular_router_7")

local output_room = peripheral.wrap("modularrouters:modular_router_5")

local output_blank = peripheral.wrap("modularrouters:modular_router_6")


-- See the other example different filter approach

function isRoom()

    local room = reader.getRoom()

    if room ~= "Empty" then
        return true
    end

    return false
end


----------

-- Keep good blank inscriptions

function goodBlank()
    local time = reader.getTime()
    local instability = reader.getInstability()
    local completion = reader.getCompletion()

    -- Keep all insta < 0.3
    if instability < 0.3 then
        return true
    end

    -- RMZing inscription formula modified :D
    local value = time + 5 * (completion - (instability * 15)) 

    if value > 60 then
        return true
    end

    return false
end



---------

-- Main funct

print("Started inscriptions monitoring!")

rednet.open("left") -- Adapt depending on where the Ender Modem is
local centralComputerID = 4 -- Adapt based on ID of the computer collecting the statistics

while true do

    if reader.getItemDetail(1) ~= nil then

        local name = reader.getItemDetail(1).displayName
        local rarity = "Blank"
        
        if isRoom() then
            rarity = "Room"
        end

        print("Currently checking a new : "..name)

        local keep_reason = "none"
        
        -- Get information on gear
        
        if isRoom() then
            keep_reason = "Room"
        elseif goodBlank() then 
            keep_reason = "Blank"
        end
        

        -- Send information to main computer
        rednet.send(centralComputerID, "InscriptionSorted|"..name.."|"..rarity.."|"..keep_reason)

        -- Do something with inscription
        if keep_reason == "Room" then
            print("Should keep it")
            reader.pushItems(peripheral.getName(output_room),1)

        elseif keep_reason == "Blank" then
            print("Should keep it")
            reader.pushItems(peripheral.getName(output_blank),1)

        else
            print("Scrap it")
            reader.pushItems(peripheral.getName(recycler),1)
        end
    end
    sleep(0.1)
end