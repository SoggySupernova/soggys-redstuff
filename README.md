# Yet Another Wireless Redstone

## Setup

For setup instructions, please see the [Fabric Documentation page](https://docs.fabricmc.net/develop/getting-started/creating-a-project#setting-up) related to the IDE that you are using.

## License

gplv3



## Future Ideas/Implementation Progress/Checklist
markdown is silly<br>


"A new kind of wireless redstone"

Wireless Endpoint
"Transmitter set to 0,0,0" ✅

Wireless Linker ✅

Position and dimension stored in item components ✅



Shift right click with linker: clear transmitter AND receiver, send a message and sound to client ✅

And reset receiver to uninitialized/transmktter✅

Swap transmitter and receiver?




Test edge cases:
- Transmitter and receiver same block ✅
- Receiver is deleted (Transmitter should check block before sending (ANd check if it's a receiver! (And, if I implement it, that its stored transmitter location matches us))
- Receiver is reset and a different transmitter connects to it (It should clear its first receiver on reset)
- Item is linked to a transmitter which is then deleted before linked to a receiver
- Item linked to transmitter, transmitter deleted and replaced with a reciever before linked to a receiver (check that isReceiver is false i guess)
- Multiplayer sync
- Multiple transmitters to one receiver
- Shift-right-click transmitter or receiver with active linker or air in hand: Reset item and block ✅
- Test custom dimensions
- Block linked to a dimension that is later deleted (e.g. sift downgrade, custom dimension)

Deduplicate receivers list (for comparator output)

When clearing connections, clear isReceiver of connected receivers✅

(In clearReceivers function so that this also fixes:

Transmitter is deleted (connections are automatically deleted, but connected receivers should reset to uninitialized/transmitter and clear redone power and block state)✅


Maybe store transmitter location to 
1. When receiver reset/deleted, remove from transmitter list
2. Prevent multiple transmitters to one receiver

Replace isCrouching with is holding shift key (fix flying)

Only do the item split thing if more than one in stack

Different block texture for transmitter/receiver

Functionize code more

Survival mechanics (crafting, mining, etc.)

Floatater: Pusher


Variant that pulls: Puller

Placer block

Pickaxer block


Redstone 90 degree turn repeater




Fix wrapping dimension in "\""+"\""



Crafting recipe includes ender pearls maybe?
