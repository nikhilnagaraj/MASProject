# MASProject
This repository holds the MAS project.

## Things to do (Mark when done!)

* Create Graph - Done
* Create Taxi Agent - Have to Modify 
  * Battery capacity and behaviour
  * Deploying ants
  * Pick up and drop off
  * Store preious pick up data / drop off data - modifiable
* Create Charging Agent
  * Capacity modifiable- but start with 1
  * Cannot move when reserved
  * Can move at the same time
  * Can move only once every x ticks - modifiable
  * Maximum speed modifiable  - start with half the agent speed
  * Charging time is modifiable - start with 'w' ticks
  * Find optimal spot
  * Ability to block an area - Ability modifiable, Radius Modifiable (Low priority)
 * Create a repairing station
  *  A car that has died out goes there and is out of service for 'y' ticks.
* Create Ants
  * Exploration ant - Find the nearest vacant charging station; Poll agents for prior pick up/drop locations , find ara with highest pick     up density.
  * Intention - Reserve that charging station; Reserve area with highest pickup density
* Setup experimental playground
  * Central data distributor - Nearest customer.
  * 3 charging stations
  * 
* Setup Measurements
  * % accomplished trips
  * Avg time taken to pick a customer up
  * Total distance driven without a customer
