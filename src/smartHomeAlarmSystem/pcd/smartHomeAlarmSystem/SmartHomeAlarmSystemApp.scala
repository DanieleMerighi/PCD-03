package pcd.smartHomeAlarmSystem

import org.apache.pekko.actor.typed.ActorSystem
import pcd.smartHomeAlarmSystem.actors.SmartHomeAlarmSystem


object SmartHomeAlarmSystemApp:

  @main
  def app(): Unit =
    val livingArea = Set[Sensor]()
    val sleepingArea = Set[Sensor]()
    val perimeter = Set[Sensor]()

    val nightMode = Mode.fromSet(perimeter ++ livingArea)

    val all = perimeter ++ livingArea ++ sleepingArea

    val system = ActorSystem(SmartHomeAlarmSystem(all), "SmartHomeAlarmSystem")
    system.log.info(s"Alarm system created with ${all.size} sensors.")

