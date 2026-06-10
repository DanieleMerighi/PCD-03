package pcd.smartHomeAlarmSystem

import org.apache.pekko.actor.typed.ActorSystem
import pcd.smartHomeAlarmSystem.Sensor.Type.*
import pcd.smartHomeAlarmSystem.actors.Home


object SmartHomeAlarmSystemApp:

  @main
  def app(): Unit =
    // First floor
    val kitchenWindow = Sensor("KitchenWindow", Window)
    val loungeWindow = Sensor("LoungeWindow", Window)
    val hallwayWindow = Sensor("HallwayWindow", Window)
    val frontDoor = Sensor("FrontDoor", Door)
    val hallwayMotion = Sensor("HallwayMotion", Motion)
    val loungeMotion = Sensor("LoungeMotion", Motion)
    val kitchenMotion = Sensor("KitchenMotion", Motion)

    val livingArea = Set[Sensor](kitchenWindow,
      loungeWindow,
      hallwayWindow,
      frontDoor,
      hallwayMotion,
      loungeMotion,
      kitchenMotion)

    // Second floor
    val bathroomWindow = Sensor("BathroomWindow", Window)
    val kidsBedroomWindow = Sensor("KidsBedroomWindow", Window)
    val masterBedroomWindow = Sensor("MasterBedroomWindow", Window)
    val bathroomMotion = Sensor("BathroomMotion", Motion)
    val kidsBedroomMotion = Sensor("KidsBedroomMotion", Motion)
    val masterBedroomMotion = Sensor("MasterBedroomMotion", Motion)

    val sleepingArea = Set[Sensor](bathroomWindow,
      kidsBedroomWindow,
      masterBedroomWindow,
      bathroomMotion,
      kitchenMotion,
      masterBedroomMotion)

    val frontYardMotion = Sensor("FrontYardMotion", Motion)
    val backYardMotion = Sensor("BackYardMotion", Motion)
    val shedMotion = Sensor("ShedMotion", Motion)
    val shedDoor = Sensor("ShedDoor", Door)

    val perimeter = Set[Sensor](frontYardMotion,
      backYardMotion,
      shedMotion,
      shedDoor)

    val nightMode = Mode.fromSet(perimeter ++ livingArea, "NightMode")

    val allSensors = perimeter ++ livingArea ++ sleepingArea
    val home = ActorSystem(Home(allSensors), "Home")

    val pinCode = 1234
    home ! Home.InstallAlarmSystem(pinCode)

    // Example scenario

    Thread.sleep(5000)

    home ! Home.InteractWithSensor(loungeMotion) // roaming around
    home ! Home.InteractWithSensor(hallwayMotion)

    Thread.sleep(5000)

    home ! Home.ArmAlarmSystem(pinCode)
    Thread.sleep(1000)
    home ! Home.InteractWithSensor(frontDoor) // on the way out
    home ! Home.InteractWithSensor(frontYardMotion)

    Thread.sleep(5000)

    home ! Home.InteractWithSensor(frontYardMotion) // on the way in
    home ! Home.InteractWithSensor(frontDoor)
    Thread.sleep(1000)
    home ! Home.DisarmAlarmSystem(pinCode)

    Thread.sleep(5000)

    home ! Home.InteractWithSensor(kitchenMotion) // roaming around

    Thread.sleep(5000)

    home ! Home.ArmAlarmSystem(pinCode, nightMode)
    Thread.sleep(1000)
    home ! Home.InteractWithSensor(hallwayMotion) // on the way up

    Thread.sleep(20000)

    home ! Home.InteractWithSensor(masterBedroomMotion) // roaming around upstairs
    home ! Home.InteractWithSensor(masterBedroomWindow)

    Thread.sleep(5000)

    home ! Home.InteractWithSensor(backYardMotion) // intruder
    Thread.sleep(5000)
    home ! Home.InteractWithSensor(loungeWindow)
    Thread.sleep(2000)
    home ! Home.InteractWithSensor(loungeMotion)
    Thread.sleep(4000)
    home ! Home.InteractWithSensor(hallwayMotion) // gets caught

    Thread.sleep(10000)

    home ! Home.DisarmAlarmSystem(pinCode)

    home.terminate()

