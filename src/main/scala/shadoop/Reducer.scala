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
package shadoop

import collection.JavaConversions._

import org.apache.hadoop.mapreduce.{Reducer => HReducer}

class Reducer[KIN, VIN, KOUT, VOUT](implicit kinTypeM: Manifest[KIN], vinTypeM: Manifest[VIN], kTypeM: Manifest[KOUT], vTypeM: Manifest[VOUT])
  extends HReducer[KIN, VIN, KOUT, VOUT] with OutTyped[KOUT, VOUT] with MapReduceConfig {

  type ContextType = HReducer[KIN, VIN, KOUT, VOUT]#Context
	type ReducerType = (KIN,Iterable[VIN]) => Iterable[(KOUT,VOUT)]

	// we wrap the reducer function in an option in case someone forgets to call reduceWith, and in that
	// case we'll return the empty iterable
	var reducer: Option[ReducerType] = None

  def kinType = kinTypeM.erasure.asInstanceOf[Class[KIN]]
  def vinType = vinTypeM.erasure.asInstanceOf[Class[VIN]]

  def kType = kTypeM.erasure.asInstanceOf[Class[KOUT]]
  def vType = vTypeM.erasure.asInstanceOf[Class[VOUT]]

	import scala.collection.JavaConversions._

	/**
	 * Run our reduce function, collect the output and save it into the context
	 */
	override def reduce(k: KIN, v: java.lang.Iterable[VIN], context: ContextType): Unit = {
    reducer.map(func => func(k, v).map(pair => context.write(pair._1, pair._2)))
	}

	/**
	 * Run our reduce function and return the raw output, may be useful during unit testing or troubleshooting
	 */
	def testF(k: KIN, v: java.lang.Iterable[VIN]) = {
		reducer.map(func => func(k, v))
	}

	/**
	 * Use this method to provide the function that will be used for the reducer
	 */
	def reduceWith(f:ReducerType) = reducer = Some(f)

  override def setup(context: ContextType) {
    this.configuration = Option(context.getConfiguration)
  }
}
