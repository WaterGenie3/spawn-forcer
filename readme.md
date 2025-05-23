# Spawn Forcer

Ad-hoc mod to help test pack spawning.

## Rules

### Starting Coordinate

#### `/spawnforcer fixedStart {true, false}`

Force spawning to start at the coordinate specified by `startCoords`.  
If `startCoords` is not defined, it will default to (0,0,0).  
Defaults to false.

#### `/spawnforcer startCoords {x} {y} {z}`

Sets the starting position for the spawn attempts to (x,y,z) when `fixedStart` is enabled.

#### `/spawnforcer startSpreadX {n}`

Adds a random amount to the x coordinate of `startCoords`, the amount will be uniformly distributed between -n and n (inclusive).  
Defaults to 0.

#### `/spawnforcer startSpreadZ {n}`

Adds a random amount to the z coordinate of `startCoords`, the amount will be uniformly distributed between -n and n (inclusive).  
Defaults to 0.

### Pack Spawning Jumps

#### `/spawnforcer uniformJump {true, false}`

Change each x and z jump to be uniformly distributed between -5 and 5 (inclusive).  
The `spreadJump` rule takes precedence over this rule.  
The `jumpSequenceX` rule takes precedence over the x component of this rule.  
The `jumpSequenceZ` rule takes precedence over the z component of this rule.  
Defaults to false.

#### `/spawnforcer spreadJump {true, false}`

Change each x and z jump to heavily favour values closer to -5 and 5.  
This rule overrides `uniformJump` rule.  
The `jumpSequenceX` rule takes precedence over the x component of this rule.  
The `jumpSequenceZ` rule takes precedence over the z component of this rule.  
Defualts to false.

#### `/spawnforcer jumpSequenceX {n1...}`

Force each x jump within a pack to follow the given sequence.  
If the pack size is greater than the length of the sequence, the last number in the sequence will be repeated.  
This rule takes precedence over the x component of `uniformJump` and `spreadJump`.  

#### `/spawnforcer jumpSequenceZ {n1...}`

Force each z jump within a pack to follow the given sequence.  
If the pack size is greater than the length of the sequence, the last number in the sequence will be repeated.  
This rule takes precedence over the z component of `uniformJump` and `spreadJump`.  

### Misc.

#### `/spawnforcer allowNear {true, false}`

Allows spawning within 24 blocks of the players and the world spawn point.  
Defaults to false.
