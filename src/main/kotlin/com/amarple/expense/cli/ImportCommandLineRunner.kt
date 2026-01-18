package com.amarple.expense.cli

import com.amarple.expense.read.Jackson.mapper
import com.amarple.expense.read.config.ImportInputReader
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.help.HelpFormatter
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import kotlin.system.exitProcess

class ImportCommandLineRunner(
    private val importTask: ImportTask
) : CommandLineRunner {
    private val logger = LoggerFactory.getLogger(ImportCommandLineRunner::class.java)

    override fun run(vararg args: String) {
        if (args.isEmpty()) return

        val options = Options()

        val inputPathOption = Option.builder("i")
            .longOpt("inputPath")
            .hasArg()
            .desc("path to file containing input")
            .required()
            .get()
        options.addOption(inputPathOption)

        val parser: CommandLineParser = DefaultParser()
        val formatter = HelpFormatter.builder().get()

        try {
            val cmd = parser.parse(options, args)
            val inputPath = cmd.getOptionValue("inputPath")

            logger.info("Processing input: {}", inputPath)

            val importInput = ImportInputReader().read(inputPath)

            val importOutput = importTask.execute(importInput)

            println(mapper.writeValueAsString(importOutput))
        } catch (e: ParseException) {
            logger.error("Encountered an error during import", e)
            println(e.message)
            formatter.printHelp("xp-import", "", options, "", true)
            exitProcess(1)
        }

        exitProcess(0)
    }
}
