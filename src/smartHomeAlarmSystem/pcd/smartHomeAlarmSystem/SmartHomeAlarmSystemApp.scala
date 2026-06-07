package pcd.smartHomeAlarmSystem

import org.apache.pekko.actor.typed.ActorSystem
import pcd.smartHomeAlarmSystem.Sensor.Type.*
import pcd.smartHomeAlarmSystem.actors.Home


object SmartHomeAlarmSystemApp:

  @main
  def app(): Unit =
    // First floor
    val kitchenWindow = Sensor("Kitchen window", Window)
    val loungeWindow = Sensor("Lounge window", Window)
    val hallwayWindow = Sensor("Hallway window", Window)
    val frontDoor = Sensor("Front door", Door)
    val hallwayMotion = Sensor("Hallway motion", Motion)
    val loungeMotion = Sensor("Lounge motion", Motion)
    val kitchenMotion = Sensor("Kitchen motion", Motion)

    val livingArea = Set[Sensor](kitchenWindow,
      loungeWindow,
      hallwayWindow,
      frontDoor,
      hallwayMotion,
      loungeMotion,
      kitchenMotion)

    // Second floor
    val bathroomWindow = Sensor("Bathroom window", Window)
    val kidsBedroomWindow = Sensor("Kids bedroom window", Window)
    val masterBedroomWindow = Sensor("Master bedroom window", Window)
    val bathroomMotion = Sensor("Bathroom motion", Motion)
    val kidsBedroomMotion = Sensor("Kids bedroom motion", Motion)
    val masterBedroomMotion = Sensor("Master bedroom motion", Motion)

    val sleepingArea = Set[Sensor](bathroomWindow,
      kidsBedroomWindow,
      masterBedroomWindow,
      bathroomMotion,
      kitchenMotion,
      masterBedroomMotion)

    val outsideFrontYardMotion = Sensor("Front yard motion", Motion)
    val outsideBackYardMotion = Sensor("Back yard motion", Motion)
    val outsideShedMotion = Sensor("Shed motion", Motion)
    val outsideShedDoor = Sensor("Shed door", Door)

    val perimeter = Set[Sensor](outsideFrontYardMotion,
      outsideBackYardMotion,
      outsideShedMotion,
      outsideShedDoor)

    val nightMode = Mode.fromSet(perimeter ++ livingArea)

    val all = perimeter ++ livingArea ++ sleepingArea
    val home = ActorSystem(Home(all), "Home")
    
    val pinCode = 1234
    home ! Home.InstallAlarmSystem(pinCode)

