package com.example.bigtablejoinperformancetest

fun main() {
    val benchmark = BigTableJoinBenchmark()
    val result = benchmark.runSampleBenchmark()
    println(result)
}

class BigTableJoinBenchmark {
    fun runSampleBenchmark(): String {
        val leftTable = List(2000) { index -> Triple(index, "user_$index", index % 17) }
        val rightTable = List(2000) { index -> Triple(index, "order_$index", index % 17) }

        val matches = leftTable.joinToString(separator = "\n") { left ->
            val joined = rightTable.filter { right -> right.third == left.third }
                .joinToString { right -> "${left.second}->${right.second}" }
            "${left.first}: $joined"
        }

        return "Benchmark complete. ${matches.lines().size} matched rows evaluated."
    }
}
