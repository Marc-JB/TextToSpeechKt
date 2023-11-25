package js_interop

fun <T : JsAny?> JsArray<T>.iterator(): Iterator<T?> {
    return object : Iterator<T?> {
        private var currentIndex = 0

        override fun hasNext() = currentIndex < this@iterator.length

        override fun next(): T? {
            if (!hasNext()){
                throw IndexOutOfBoundsException()
            }

            currentIndex++
            return this@iterator[currentIndex]
        }
    }
}
