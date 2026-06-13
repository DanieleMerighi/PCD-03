package pcd.smartHomeAlarmSystem.actors

import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import pcd.smartHomeAlarmSystem.Mode
import pcd.smartHomeAlarmSystem.actors.Keypad.Command.{Arm, Disarm}


object Keypad:

  type Ref = ActorRef[Command]

  enum Command:
    case Arm(code: Int, mode: Mode = Mode.AllActive)
    case Disarm(code: Int)
  
  export Command.*

  def apply(alarmSystem: SmartHomeAlarmSystem.Ref): Behavior[Command] =
    active(using alarmSystem)

  private def active(using alarmSystem: SmartHomeAlarmSystem.Ref): Behavior[Command] =
    Behaviors.receiveMessage:
      case Arm(code, mode) =>
        alarmSystem ! SmartHomeAlarmSystem.Arm(code, mode)
        Behaviors.same
      case Disarm(code) =>
        alarmSystem ! SmartHomeAlarmSystem.Disarm(code)
        Behaviors.same

