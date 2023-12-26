-- Central Computer

-- Open the rednet side you are using 
rednet.open("back") 

-- File to store statistics
local statsFile = "sorting_stats.txt"

-- Table to store statistics
local statistics = {
    gears = {
        Helmet = {Legendary = 0, Omega = 0, Special = 0, Total = 0},
        Chestplate = {Legendary = 0, Omega = 0, Special = 0, Total = 0},
        Leggings = {Legendary = 0, Omega = 0, Special = 0, Total = 0},
        Boots = {Legendary = 0, Omega = 0, Special = 0, Total = 0},
        Sword = {Legendary = 0, Omega = 0, Special = 0, Total = 0},
        Axe = {Legendary = 0, Omega = 0, Special = 0, Total = 0},
        Wand = {Legendary = 0, Omega = 0, Special = 0, Total = 0},
        Shield = {Legendary = 0, Omega = 0, Special = 0, Total = 0},
        Focus = {Legendary = 0, Omega = 0, Special = 0, Total = 0},
        Magnet = {Legendary = 0, Omega = 0, Special = 0, Total = 0}
    },
    jewels = {Legendary = 0, Perfect = 0, Size10 = 0, Total = 0},
    inscriptions = {Room = 0, Blank = 0, Total = 0}
}

 



-- Function to load statistics from file
local function loadStatisticsFromFile()
    if fs.exists(statsFile) then
        local file = fs.open(statsFile, "r")
        local content = file.readAll()
        file.close()

        local loadedStats = textutils.unserialize(content)
        if loadedStats then
            statistics = loadedStats
        else
            print("Failed to load statistics from file. Using default statistics.")
        end
    else
        print("Statistics file not found. Using default statistics.")
    end
end


-- Load statistics from file during initialization
loadStatisticsFromFile()


-- Monitor setup for me is 6 x 8, which correspond to a 40 x 82 size, adapt as needed
local monitor = peripheral.wrap("left")  
monitor.setTextScale(1)  
monitor.setBackgroundColor(colors.black)
monitor.clear()


-- Function to parse the received message
function parseMessage(message)
    local parts = {}
    for part in string.gmatch(message, "[^|]+") do
        table.insert(parts, part)
    end
    return unpack(parts)
end

-- Function to update gear statistics
function updateGearStatistics(itemCategory, itemKeep)
    if itemKeep ~= "none" then
        statistics.gears[itemCategory][itemKeep] = statistics.gears[itemCategory][itemKeep] + 1
    end
    statistics.gears[itemCategory].Total = statistics.gears[itemCategory].Total + 1

    -- Save statistics to file
    saveStatisticsToFile()
end

-- Function to update jewel statistics
function updateJewelStatistics(itemKeep)
    if itemKeep ~= "none" then
        statistics.jewels[itemKeep] = statistics.jewels[itemKeep] + 1
    end
    statistics.jewels.Total = statistics.jewels.Total + 1

    -- Save statistics to file
    saveStatisticsToFile()
end

-- Function to update inscription statistics
function updateInscriptionStatistics(itemKeep)
    if itemKeep ~= "none" then
        statistics.inscriptions[itemKeep] = statistics.inscriptions[itemKeep] + 1
    end
    statistics.inscriptions.Total = statistics.inscriptions.Total + 1

    -- Save statistics to file
    saveStatisticsToFile()
end

-- Function to save statistics to file
function saveStatisticsToFile()
    local file = fs.open(statsFile, "w")
    file.write(textutils.serialize(statistics))
    file.close()
end

-- Function to write a text with Rainbow color
local c = {0,1,2,3,4,5,6,7,8,9,"a","b","c","d","e"}
function rainbowWrite(text)
    local textColorString = ""
    local backgroundColorString = ""
    for x = 1, #text do
        textColorString = textColorString..c[math.random(1, 15)]
        backgroundColorString = backgroundColorString.."0"
        --# the background will always be black
    end
    term.blit(text, textColorString, backgroundColorString)
 end

-- Function to display information on Monitor
-- Modify according to your taste

function drawLine(height)
    monitor.setTextColor(colors.lightGray)
    monitor.setCursorPos(1, height)
    monitor.write(string.rep("-", monitor.getSize(0)))
end

function displayInformation(itemCategory, itemRarity, itemKeep)
    
    monitor.clear()
    -- Date
    monitor.setTextColor(colors.red)  
    monitor.setCursorPos(1,1)
    monitor.write(string.format("%82s",textutils.formatTime(os.time("local"), true)))

    -- Title
    monitor.setCursorPos(10, 2)
    
    --rainbowWrite("Gear-O-Matic !!!")
    monitor.blit("Gear-O-Matic!!!","e145d93ba26e145","fffffffffffffff")

    monitor.setCursorPos(40, 2)
    monitor.setTextColor(colors.lightBlue)
    monitor.write("Gear and Jewels Sorting System")

    -- Separator
    drawLine(4)
    
    -- Last item sorted
    monitor.setCursorPos(1, 6)
    monitor.setTextColor(colors.lightGray)  
    monitor.write("Last Item: ")
    monitor.setCursorPos(10, 6)
    monitor.setTextColor(colors.white)
    monitor.write(string.format("%15s",itemCategory))

    monitor.setCursorPos(30, 6)
    monitor.setTextColor(colors.lightGray)  
    monitor.write("Rarity: ")
    monitor.setCursorPos(40, 6)

    if itemRarity == "OMEGA" or itemRarity == "Perfect" then
        monitor.setTextColor(colors.green)
    elseif itemRarity == "EPIC" or itemRarity == "Flawless" then
        monitor.setTextColor(colors.purple)
    elseif itemRarity == "RARE" or itemRarity == "Flawed" then
        monitor.setTextColor(colors.yellow)
    elseif itemRarity == "COMMON" or itemRarity == "Chipped" then
        monitor.setTextColor(colors.blue)
    elseif itemRarity == "Room" then
        monitor.setTextColor(colors.orange)
    else
        monitor.setTextColor(colors.gray)
    end
    monitor.write(string.format("%10s",itemRarity))

    monitor.setCursorPos(60, 6)
    monitor.setTextColor(colors.lightGray)  
    monitor.write("Decision: ")

    monitor.setCursorPos(73, 6)
    if itemKeep == "none" then
        symbol = "\31\31\31\31\31"
        monitor.setTextColor(colors.red)
    else
        symbol = "\30\30\30\30\30"
        monitor.setTextColor(colors.green)
    
    end
    monitor.write(symbol)    

    -- Separator
    drawLine(8)

    -- Gears Statistics
    monitor.setCursorPos(1, 9)
    monitor.setTextColor(colors.lightBlue)
    monitor.write("Statistics: Gears")

    -- Categories

    monitor.setCursorPos(1, 11)
    local textCategory = string.format("%25s%13s%13s%13s%13s","Legendary","Omega","Special","Total","% Legendary")
    local colorCategory = string.rep("1",25)..string.rep("5",13)..string.rep("9",13)..string.rep("8",13)..string.rep("6",13)
    local bgCategory = string.rep("f",#textCategory)
    
    monitor.blit(textCategory,colorCategory,bgCategory)

    -- Display gear statistics table
    local gearCategories = {"Helmet", "Chestplate", "Leggings", "Boots", "Sword", "Axe", "Wand", "Shield", "Focus", "Magnet"}
    
    local legendaryTotal = 0
    local omegaTotal = 0
    local specialTotal = 0
    local totalTotal = 0

    for offset, category in ipairs(gearCategories) do
        local legendary = statistics.gears[category].Legendary
        local omega = statistics.gears[category].Omega
        local special = statistics.gears[category].Special
        local total = statistics.gears[category].Total
        if total > 0 then
            legendaryPercent = ((legendary / total) * 100)
        else
            legendaryPercent = 0.000
        end

        monitor.setTextColor(colors.lightGray)  
        monitor.setCursorPos(1, 12 + offset)
        monitor.write(category)

        monitor.setTextColor(colors.white)
        monitor.setCursorPos(12, 12 + offset)
        monitor.write(string.format("%13d%13d%13d%13d%13.3f",legendary,omega,special,total,legendaryPercent).."%")

        legendaryTotal = legendaryTotal + legendary
        omegaTotal = omegaTotal + omega
        specialTotal = specialTotal + special
        totalTotal = totalTotal + total
        
    end
    -- Total for Gear
    if totalTotal > 0 then
        legendaryPercentTotal = ((legendaryTotal / totalTotal) * 100)
    else
        legendaryPercentTotal = 0
    end
    monitor.setTextColor(colors.lightGray)  
    monitor.setCursorPos(1, 24)
    monitor.write("Gear Total")

    monitor.setTextColor(colors.white)
    monitor.setCursorPos(12, 24)
    monitor.write(string.format("%13d%13d%13d%13d%13.3f",legendaryTotal,omegaTotal,specialTotal,totalTotal,legendaryPercent).."%")

    -- Separator
    drawLine(26)

    -- Jewels stats
    monitor.setCursorPos(1, 27)
    monitor.setTextColor(colors.lightBlue)
    monitor.write("Statistics: Jewels")

    -- Categories

    monitor.setCursorPos(1, 29)
    local textCategory = string.format("%25s%13s%13s%13s%13s","Legendary","3-4 Affixes","Special","Total","% Legendary")
    local colorCategory = string.rep("1",25)..string.rep("5",13)..string.rep("9",13)..string.rep("8",13)..string.rep("6",13)
    local bgCategory = string.rep("f",#textCategory)
    
    monitor.blit(textCategory,colorCategory,bgCategory)
    
    -- Display jewel statistics table
    local jewelLegendary = statistics.jewels.Legendary
    local jewelPerfect = statistics.jewels.Perfect
    local jewelSize10 = statistics.jewels.Size10
    local jewelTotal = statistics.jewels.Total

    if jewelTotal > 0 then
        jewelLegendaryPercent = ((jewelLegendary / jewelTotal) * 100)
    else
        jewelLegendaryPercent = 0
    end

    monitor.setCursorPos(1, 31)
    monitor.setTextColor(colors.lightGray)  
    monitor.write("Jewel")


    monitor.setTextColor(colors.white)
    monitor.setCursorPos(12, 31)
    monitor.write(string.format("%13d%13d%13d%13d%13.3f",jewelLegendary,jewelPerfect,jewelSize10,jewelTotal,jewelLegendaryPercent).."%")

    -- Separator
    drawLine(33)

    -- Inscriptions stats
    monitor.setCursorPos(1, 34)
    monitor.setTextColor(colors.lightBlue)
    monitor.write("Statistics: Inscriptions")

    -- Categories

    monitor.setCursorPos(1, 36)
    local textCategory = string.format("%25s%13s%13s","Room","Blank","Total")
    local colorCategory = string.rep("b",25)..string.rep("c",13)..string.rep("8",13)
    local bgCategory = string.rep("f",#textCategory)
    
    monitor.blit(textCategory,colorCategory,bgCategory)
    
    -- Display inscriptions statistics table
    local inscriptionsRoom = statistics.inscriptions.Room
    local inscriptionsBlank = statistics.inscriptions.Blank
    local inscriptionsTotal = statistics.inscriptions.Total

    monitor.setCursorPos(1, 38)
    monitor.setTextColor(colors.lightGray)  
    monitor.write("Inscription")

    monitor.setTextColor(colors.white)
    monitor.setCursorPos(12, 38)
    monitor.write(string.format("%13d%13d%13d",inscriptionsRoom,inscriptionsBlank,inscriptionsTotal))

    -- Separator
    drawLine(40)

end


--------------------------------------------------------------------
-- MAIN LOOP



print("Starting to monitor Statistics")

while true do

    
    local senderID, message = rednet.receive()
    
    if senderID ~= nil then
        -- Parse the message and handle accordingly
        local messageType, itemCategory, itemRarity, itemKeep = parseMessage(message)

        if messageType == "GearSorted" then
            -- Handle gear sorting information
            updateGearStatistics(itemCategory, itemKeep)
        elseif messageType == "JewelSorted" then
            -- Handle jewel sorting information
            updateJewelStatistics(itemKeep)
        elseif messageType == "InscriptionSorted" then
            -- Handle inscription sorting information
            updateInscriptionStatistics(itemKeep)
        end

        -- Display real-time information on the monitor
        displayInformation(itemCategory,itemRarity, itemKeep)
    end
    
    sleep(0.05)
end
