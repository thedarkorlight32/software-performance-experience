package com.example.bigtablejoinperformancetest

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AppTest {
    @Test
    fun `benchmark runs successfully`() {
        val benchmark = BigTableJoinBenchmark()
        val output = benchmark.runSampleBenchmark()

        assertTrue(output.contains("Benchmark complete"))
    }
}
