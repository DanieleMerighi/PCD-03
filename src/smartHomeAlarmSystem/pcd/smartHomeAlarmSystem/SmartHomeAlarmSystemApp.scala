package pcd.smartHomeAlarmSystem

import org.apache.pekko.actor.typed.ActorSystem
import pcd.smartHomeAlarmSystem.actors.Home


object SmartHomeAlarmSystemApp:

  @main
  def app(): Unit =
    val livingArea = Set[Sensor]()
    val sleepingArea = Set[Sensor]()
    val perimeter = Set[Sensor]()

    val nightMode = Mode.fromSet(perimeter ++ livingArea)

    val all = perimeter ++ livingArea ++ sleepingArea
    val home = ActorSystem(Home(all), "Home")
    
    val pinCode = 1234
    home ! Home.InstallAlarmSystem(pinCode)

