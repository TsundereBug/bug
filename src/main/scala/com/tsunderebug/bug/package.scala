package com.tsunderebug

import sx.blah.discord.util.RequestBuffer
import sx.blah.discord.util.RequestBuffer.RequestFuture

package object bug {

  implicit class FunctionLike[T](f: () => T) {
    def r: RequestFuture[T] = RequestBuffer.request(() => f())
  }

  implicit class LazyLike[T](f: => T) {
    def r: RequestFuture[T] = RequestBuffer.request(() => f)
  }

}
