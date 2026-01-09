package com.amarple.expense.cli

import com.amarple.expense.read.Jackson.mapper
import com.amarple.expense.read.config.ImportInputReader
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.help.HelpFormatter
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import kotlin.system.exitProcess

@Component
class ImportCommandLineRunner : CommandLineRunner {

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

            println("Processing input: $inputPath")

            val importInput = ImportInputReader().read(inputPath)

            val importOutput = ImportTask().execute(importInput)

            println(mapper.writeValueAsString(importOutput))
        } catch (e: ParseException) {
            println(e.message)
            formatter.printHelp("xp-import", "", options, "", true)
            exitProcess(1)
        }

        exitProcess(0)
    }
}
