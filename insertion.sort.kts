val name = "Insertion Sort"

fun run(list: VisualList) {
    for (i in 1 until list.size) {
        val item = list[i]
        var j = i - 1
        while (j >= 0 && item < list[j]) {
            list[j + 1] = list[j]
            j--
        }
        list[j + 1] = item
    }
}
