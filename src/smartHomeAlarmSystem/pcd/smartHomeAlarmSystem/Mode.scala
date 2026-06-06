package pcd.smartHomeAlarmSystem


trait Mode:
  
  def isActive(sensor: Sensor): Boolean


object Mode:
  
  def fromSet(activeSensors: Set[Sensor]): Mode =
    activeSensors.contains(_)

  val AllActive: Mode = _ => true
  
  val AllInactive: Mode = _ => false

