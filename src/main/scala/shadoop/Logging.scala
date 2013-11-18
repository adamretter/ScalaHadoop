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

import org.apache.commons.logging.{LogFactory, Log}

trait Logging {
  other =>

  private lazy val logger: Log = LogFactory.getLog(other.getClass())

  protected def debug(message: => String) = log(logger.isDebugEnabled, logger.debug, message)
  protected def info(message: => String) = log(logger.isInfoEnabled, logger.info, message)
  private def log(fc: => Boolean, fl: (String) => Unit, message: String) = if(fc) fl(message)
}
