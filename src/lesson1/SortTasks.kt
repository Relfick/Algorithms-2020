@file:Suppress("UNUSED_PARAMETER")

package lesson1

import java.io.File

/**
 * Сортировка времён
 *
 * Простая
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС AM/PM,
 * каждый на отдельной строке. См. статью википедии "12-часовой формат времени".
 *
 * Пример:
 *
 * 01:15:19 PM
 * 07:26:57 AM
 * 10:00:03 AM
 * 07:56:14 PM
 * 01:15:19 PM
 * 12:40:31 AM
 *
 * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
 * сохраняя формат ЧЧ:ММ:СС AM/PM. Одинаковые моменты времени выводить друг за другом. Пример:
 *
 * 12:40:31 AM
 * 07:26:57 AM
 * 10:00:03 AM
 * 01:15:19 PM
 * 01:15:19 PM
 * 07:56:14 PM
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */

// Время - O(N*log(N))
// Память - O(N)
fun sortTimes(inputName: String, outputName: String) {
    fun parseTime(s: String): Int {
        val splitted = s.split(":", " ")
        return 3600 * (splitted[0].toInt() % 12) + 60 * splitted[1].toInt() +
                +splitted[2].toInt() + if (splitted[3] == "PM") 43200 else 0
    }

    val times = mutableMapOf<Int, MutableList<String>>()
    for (line in File(inputName).readLines()) {
        require(line.matches(Regex("(0|1)\\d:[0-5]\\d:[0-5]\\d (P|A)M")))
        val intTime = parseTime(line)
        if (times.containsKey(intTime)) times[intTime]!!.add(line)
        else times[intTime] = mutableListOf(line)
    }

    File(outputName).bufferedWriter().use { writer ->
        times.toSortedMap().forEach { it.value.forEach { time -> writer.write("${time}\n") } }
    }
}

/**
 * Сортировка адресов
 *
 * Средняя
 *
 * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
 * где они прописаны. Пример:
 *
 * Петров Иван - Железнодорожная 3
 * Сидоров Петр - Садовая 5
 * Иванов Алексей - Железнодорожная 7
 * Сидорова Мария - Садовая 5
 * Иванов Михаил - Железнодорожная 7
 *
 * Людей в городе может быть до миллиона.
 *
 * Вывести записи в выходной файл outputName,
 * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
 * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
 *
 * Железнодорожная 3 - Петров Иван
 * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
 * Садовая 5 - Сидоров Петр, Сидорова Мария
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
//Трудоемкость - O(N*log(N))
//Ресурсоемкость - O(N)
fun sortAddresses(inputName: String, outputName: String) {
    fun parseNote(note: String): Triple<String, Int, String> {
        val (name, address) = note.split(" - ")
        val (street, number) = address.split(" ")
        return Triple(street, number.toInt(), name)
    }

    var notes = mutableMapOf<String, MutableMap<Int, MutableList<String>>>()
    for (line in File(inputName).readLines()) {
        require(line.matches(Regex("[a-zA-Zа-яА-Я-ёЁ]+ [a-zA-Zа-яА-Я-ёЁ]+ - [a-zA-Zа-яА-Я-ёЁ]+ \\d+")))
        val (street, number, name) = parseNote(line)
        if (notes.containsKey(street)) {
            if (notes[street]!!.containsKey(number))
                notes[street]!![number]!!.add(name)
            else
                notes[street]!![number] = mutableListOf(name)
        } else {
            notes[street] = mutableMapOf(Pair(number, mutableListOf(name)))
        }
    }

    notes = notes.toSortedMap()
    for (i in notes.keys) {
        notes[i] = notes[i]!!.toSortedMap()
        for (j in notes[i]!!.keys) {
            notes[i]!![j]!!.sort()
        }
    }

    File(outputName).bufferedWriter().use { writer ->
        notes.forEach { (street, numberAndNames) ->
            numberAndNames.forEach { numberAndName ->
                writer.write(
                    "$street ${numberAndName.key} - ${numberAndName.value.joinToString(separator = ", ")}\n"
                )
            }
        }
    }
}

/**
 * Сортировка температур
 *
 * Средняя
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
 * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
 * Например:
 *
 * 24.7
 * -12.6
 * 121.3
 * -98.4
 * 99.5
 * -12.6
 * 11.0
 *
 * Количество строк в файле может достигать ста миллионов.
 * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
 * Повторяющиеся строки сохранить. Например:
 *
 * -98.4
 * -12.6
 * -12.6
 * 11.0
 * 24.7
 * 99.5
 * 121.3
 */

// Время - O(N)
// Память - O(N)
fun sortTemperatures(inputName: String, outputName: String) {
    var temperatures = mutableListOf<Int>()
    for (line in File(inputName).readLines()) {
        temperatures.add((line.toDouble() * 10 + 2730).toInt())
    }
    temperatures = countingSort(temperatures.toIntArray(), 7730).toMutableList()

    File(outputName).bufferedWriter().use { writer ->
        temperatures.forEach { writer.write("${(it - 2730) / 10.0}\n") }
    }
}

/**
 * Сортировка последовательности
 *
 * Средняя
 * (Задача взята с сайта acmp.ru)
 *
 * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
 *
 * 1
 * 2
 * 3
 * 2
 * 3
 * 1
 * 2
 *
 * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
 * а если таких чисел несколько, то найти минимальное из них,
 * и после этого переместить все такие числа в конец заданной последовательности.
 * Порядок расположения остальных чисел должен остаться без изменения.
 *
 * 1
 * 3
 * 3
 * 1
 * 2
 * 2
 * 2
 */
fun sortSequence(inputName: String, outputName: String) {
    TODO()
}

/**
 * Соединить два отсортированных массива в один
 *
 * Простая
 *
 * Задан отсортированный массив first и второй массив second,
 * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
 * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
 *
 * first = [4 9 15 20 28]
 * second = [null null null null null 1 3 9 13 18 23]
 *
 * Результат: second = [1 3 4 9 9 13 15 20 23 28]
 */
fun <T : Comparable<T>> mergeArrays(first: Array<T>, second: Array<T?>) {
    TODO()
}

