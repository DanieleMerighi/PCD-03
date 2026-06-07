package pcd.smartHomeAlarmSystem

import scala.collection.IterableOps


extension [K, V](i: Iterable[K])
  def associateWith(valueSelector: K => V): Map[K, V] =
    i.map(key => (key, valueSelector(key))).toMap


extension [K, V](m: Map[K, V])
  def foreachValue(f: V => Unit): Unit =
    m.foreach((_, value) => f(value))

