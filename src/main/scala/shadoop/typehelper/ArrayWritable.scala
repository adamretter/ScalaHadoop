/**
 * Copyright (C) 2013 Adam Retter (adam.retter@googlemail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package shadoop.typehelper

import org.apache.hadoop.io.{ArrayWritable => HArrayWritable, Writable, Text, LongWritable}
import scala.reflect.ClassTag

trait ArrayWritableType[T <: Writable] {
  type ConcreteArrayWritable <: ArrayWritable[T]
}

abstract class ArrayWritable[T <: Writable](values: Array[T])(implicit val ctT: ClassTag[T])
  extends HArrayWritable(ctT.runtimeClass.asInstanceOf[Class[T]], values.toArray)
  with ArrayWritableType[T] with collection.mutable.Iterable[T] {

  def this(values: Seq[T])(implicit ctT: ClassTag[T]) = this(values.toArray)(ctT)

  protected def make(values: Seq[T]) : ConcreteArrayWritable

  def iterator = get().asInstanceOf[Array[T]].iterator

  /**
   * A copy of the ArrayWritable with a value prepended.
   */
  def +:(value: T) : ConcreteArrayWritable = make(value +: values)

  /**
   * A copy of the ArrayWritable with a value appended.
   */
  def :+(value: T) : ConcreteArrayWritable  = make(values :+ value)

  override def toString: String = {
    s"[${this.toStrings.mkString(",")}]"
  }
}

trait AbstractArrayWritableObject[T <: Writable] extends ArrayWritableType[T] {
  implicit def ArrayWritableUnbox(a: ConcreteArrayWritable) : Seq[T] = a.toSeq
  implicit def ArrayWritableBox(s: Seq[T]) = apply(s)
  def apply(values: Seq[T]) : ConcreteArrayWritable
  def apply(values: Array[T]) : ConcreteArrayWritable
}