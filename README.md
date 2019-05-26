# MASProject
This repository holds the MAS project.

## Things to do (Mark when done!)

* Create Graph - Done
* Create Regions - problem: how to define boundaries (in points) of specific region 
  * Each region has 4 adjacent regions
  * The ant moves from region to region
* Create Taxi Agent - Have to Modify 
  * Battery capacity and behaviour - DONE
  * Deploying ants
  * Pick up and drop off
  * Drop pheromone previous pick up data / drop off data - modifiable
     The taxi drops a pheromene at its current location, the oad model is responsible for collectioin and alloting it to the appropriate region
  
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
* Create Ants - Ant abstraction, TODO: PheromoneInfrastructure, specific Ants
  * Exploration ant - Find the nearest vacant charging station; Poll regions for prior pick up/drop locations , find area with highest pick up/drop density.
  * Intention - Reserve that charging station; Reserve area with highest pickup density
* Setup experimental playground
  * Central data distributor - Nearest customer.
  * 3 charging stations
  * 
* Setup Measurements
  * % accomplished trips
  * Avg time taken to pick a customer up
  * Total distance driven without a customer
