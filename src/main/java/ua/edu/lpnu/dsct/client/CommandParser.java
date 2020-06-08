package ua.edu.lpnu.dsct.client;

import ua.edu.lpnu.dsct.client.utilities.EnumManager;
import ua.edu.lpnu.dsct.common.implementation.NumberType;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommandParser {
    enum Command {
        PING,
        ECHO,
        GENERATE,
        PROCESS,
        HELP;
    }

    private static class Patterns {
        static final Pattern command = Pattern.compile("^([a-zA-z]+) ?.*");
        static final Pattern ping = Pattern.compile("(?i)^ping\\s*$");
        static final Pattern echo = Pattern.compile("(?i)^echo\\s+(.*)");
        static final Pattern generate = Pattern.compile("(?i)^generate\\s+\"(.+)\"\\s+(\\d+)\\s+(-?\\d+)\\s+(-?\\d+)$");
        static final Pattern process = Pattern.compile("(?i)^process\\s+\"(.+)\"\\s\"(.+)\"\\s+([0-9]+)$");
        static final Pattern plainHelp = Pattern.compile("(?i)^help\\s*$");
        static final Pattern help = Pattern.compile("(?i)^help\\s+([a-z]+)\\s*$");
    }

    private final RemoteRequestManager manager;
    private final Logger logger;

    public CommandParser(RemoteRequestManager manager) {
        this.manager = manager;
        this.logger = Logger.getGlobal();
    }

    public void callSafe(String execString) {
        try{
            call(execString);
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    public void call(String execString) throws IOException {
        Matcher commandMatcher = Patterns.command.matcher(execString);

        if(!commandMatcher.find()) {
            throw new IllegalArgumentException("Unknown command pattern");
        }

        String commandString = commandMatcher.group(1);
        Command command = (Command) EnumManager.parse(Command.class, commandString);

        switch (command) {
            case PING:
                if(!Patterns.ping.matcher(execString).find()) {
                    throw new IllegalArgumentException("No additional characters are allowed in PING command");
                }
                this.manager.ping();
                break;
            case ECHO:
                Matcher echoMatcher = Patterns.echo.matcher(execString);
                if(!echoMatcher.find()) {
                    throw new IllegalArgumentException("No text to send to server");
                }
                String text = echoMatcher.group(1);
                this.manager.echo(text);
                break;
            case GENERATE:
                Matcher generateMatcher = Patterns.generate.matcher(execString);
                if(!generateMatcher.find()) {
                    throw new IllegalArgumentException("Incorrect GENERATE command format. " +
                            "Use 'help generate' to get instructions.");
                }
                String filePath = generateMatcher.group(1);
                long count = Long.parseLong(generateMatcher.group(2));
//                NumberType type = (NumberType) EnumManager.parse(NumberType.class,  generateMatcher.group(3));
                long min = Long.parseLong(generateMatcher.group(3));
                long max = Long.parseLong(generateMatcher.group(4));

                this.manager.generate(filePath, count, min, max);
                break;
            case PROCESS:
                Matcher processMatcher = Patterns.process.matcher(execString);
                if(!processMatcher.find()) {
                    throw new IllegalArgumentException("Incorrect PROCESS command format. " +
                            "Use 'help process' to get instructions.");
                }

                String inputFilePath = processMatcher.group(1);
                String outputFilePath = processMatcher.group(2);
                String searchingNumber = processMatcher.group(3); ///////////////

                this.manager.sort(inputFilePath, outputFilePath, searchingNumber);
                break;
            case HELP:
                Matcher plainHelpMatcher = Patterns.plainHelp.matcher(execString);
                StringBuilder helpStringBuilder = new StringBuilder("Executing help command.");
                if(plainHelpMatcher.find()) {
                    helpStringBuilder.append(this.help());
                } else {
                    Matcher helpMatcher = Patterns.help.matcher(execString);
                    if(!helpMatcher.find()) {
                        throw new IllegalArgumentException("Incorrect HELP command format");
                    }

                    String argString = helpMatcher.group(1);
                    Command argCommand = (Command) EnumManager.parse(Command.class, argString);
                    helpStringBuilder.append(this.help(argCommand));
                }
                logger.info(helpStringBuilder.toString());
        }
    }

    public String help() {
        StringBuilder builder = new StringBuilder();
        for (Command command : Command.values()) {
            builder.append(help(command));
        }
        return builder.toString();
    }

    public String help(Command command) {
        switch (command) {
case PING:
        return "\n***********> PING <*********** \n" +
        "Напишіть у командній строці клієнта PING для перевірки з'єднання.\n" +
        "Ніякі інші символи окрім PNG не допускаються.";
        case ECHO:
        return "\n----------> ECHO <---------- \n" +
        "Відправте будь-який текст на сервер та отримайте його назад.\n" +
        "Приклад: echo 'ТРІМ11!'";
        case GENERATE:
        return "\n*************> GENERATE <*********** \n" +
        "З допомогою цієї команди ми згенеруємо файл з випадковими числами.\n" +
        "Шаблон: generate \"Шлях до файлу\" Кількість чисел  мінімальне число  Максимальне число\n" +
        "Приклад: generate \"generated.txt\" 100 0 1000\n" +
        "(В цьому випадку буде згенеровано 100  чисел від 0 до 1000 і вони будуть у файлі 'generated.txt')\n" +
        "Назву файлу можна вибрати будь-яку.";
        case PROCESS:
        return "\n***********> PROCESS <************ \n" +
        "Файл зчитується відправляється на сервер. Сервер сортує числа і повертає індекс розміщення числа у масиві.\n" +
        "Template: process \"path/to/input/file\" \"path/to/output/file\" searching number\n" +
        "Example: process \"to_sort.txt\" \"sorted.txt\" 9\n" +
        "Note: quotes (\") around file paths are mandatory.";
        case HELP:
        return "\n***********> HELP <*************\n" +
        "Print out helpful message on how to use the program.\n" +
        "Use 'help <command>' to get info about specific command";
default:
        return "";
        }
        }
        }

