package pcd.smartHomeAlarmSystem.actors

import org.apache.pekko.actor.typed.*
import org.apache.pekko.actor.typed.scaladsl.*

import scala.concurrent.duration.FiniteDuration

object PersonActor:

  enum Command:
    case RoamAround() //Disable case
    case SetAlarmAndExitHouse() //Exit delay case
    case EnterHouseAndDisableAlarm() //Entry delay case
    case SetNightMode() //Zone based control
    case AlarmTriggered()
    case DisableAlarm() // Alarm case
    case WakeUp()

  export Command.*

  def apply(duration: FiniteDuration): Behavior[Command] = {
    Behaviors.setup: contex =>
      Behaviors.withTimers: timers =>
        awake(contex, timers, duration)
  }

  private def awake(
    context: ActorContext[Command],
    timers: TimerScheduler[Command],
    duration: FiniteDuration
  ): Behavior[Command] =
    Behaviors.receiveMessagePartial:
        case RoamAround() =>
          context.log.info("[Person] Roaming around...")
          // sensor ! triggerMovents
          timers.startSingleTimer(SetAlarmAndExitHouse(), duration)
          Behaviors.same
        case SetAlarmAndExitHouse() =>
          context.log.info("[Person] Setting alarm and exiting")
          // alarm ! handlePinCode
          timers.startSingleTimer(EnterHouseAndDisableAlarm(), duration)
          Behaviors.same
        case EnterHouseAndDisableAlarm() =>
          context.log.info("[Person] Entering house and disabling alarm")
          timers.startSingleTimer(SetNightMode(), duration)
          Behaviors.same
        case SetNightMode() =>
          context.log.info("[Person] Setting night mode and going to bed")
          sleeping(context, timers, duration)
        case AlarmTriggered() =>
          context.log.info("[Person] Heard alarm")
          timers.startSingleTimer(DisableAlarm(), duration)
          Behaviors.same
        case DisableAlarm() =>
          context.log.info("[Person] disabled alarm")
          Behaviors.same

  private def sleeping(
    context: ActorContext[Command],
    timers: TimerScheduler[Command],
    duration: FiniteDuration
  ): Behavior[Command] =
    Behaviors.receiveMessagePartial:
        case AlarmTriggered() =>
          context.log.info("[Person] Heard alarm while sleeping, waking up")
          timers.startSingleTimer(DisableAlarm(), duration)
          awake(context, timers, duration)
        case WakeUp() =>
          context.log.info("[Person] Waking up")
          awake(context, timers, duration)
