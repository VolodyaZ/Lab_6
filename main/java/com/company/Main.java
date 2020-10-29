package com.company;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {

        String inputFile;
        String requestsFile;
        String logFile;
        if (args.length < 3) {
            inputFile = "in.txt";
            requestsFile = "requests.txt";
            logFile = "log.txt";
        } else {
            inputFile = args[0];
            requestsFile = args[1];
            logFile = args[2];
        }
        try {
            CompanyQueries companies = readTable(inputFile);
            processQueriesSQL(companies, requestsFile, logFile);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static CompanyQueries readTable(String inputFile) throws Exception {
        CompanyQueries table = new CompanyQueries();
        Scanner scanner = new Scanner(new File(inputFile));
            while (scanner.hasNextLine()) {
                table.addCompany(new Company(scanner.nextLine()));
            }
        return table;
    }

    public static void processQueriesSQL(CompanyQueries companies, String requestsFile, String logFile) {
        //process queries here
        try (Scanner requestScanner = new Scanner(new File(requestsFile));
             BufferedWriter logWriter = new BufferedWriter(new FileWriter(logFile, true))
        ) {
            List<Company> result = new ArrayList<>();
            boolean fail = false;
            int requestIndex = 1;
            String query = "";
            while (requestScanner.hasNextLine()) {
                query = requestScanner.nextLine();
                try (Scanner lineScan = new Scanner(query.toLowerCase())){
                    if (lineScan.next().toLowerCase().equals(SQLKeyWords.SELECT) &&
                            lineScan.next().toLowerCase().equals(SQLKeyWords.FROM) &&
                            lineScan.next().toLowerCase().equals(SQLKeyWords.TABLE_NAME) &&
                            lineScan.next().toLowerCase().equals(SQLKeyWords.WHERE)) {
                        String tmp;
                        if (lineScan.findInLine(Pattern.compile(SQLKeyWords.SHORTNAME +
                                "( +)?=( +)?")) != null) {
                            String name = lineScan.findInLine(Pattern.compile("[\"'].*[\"']"));
                            result = companies.findByShortName(name.substring(1, name.length() - 1));
                        } else if (lineScan.findInLine(Pattern.compile(SQLKeyWords.TYPE_OF_BUSINESS +
                                "( +)?=( +)?")) != null) {
                            String type = lineScan.findInLine(Pattern.compile("[\"'].*[\"']"));
                            result = companies.findByTypeOfBusiness(type.substring(1, type.length() - 1));
                        } else if ((tmp = lineScan.findInLine(Pattern.compile(SQLKeyWords.EMPLOYEE_COUNT + "( +)?(>=|>)"))) != null) {
                            int from = -1;
                            int to = -1;
                            from = lineScan.nextInt();
                            if (tmp.charAt(tmp.length() - 1) != '=') {
                                ++from;
                            }
                            if (lineScan.findInLine(SQLKeyWords.AND) != null &&
                                    null != (tmp = lineScan.findInLine(Pattern.compile(SQLKeyWords.EMPLOYEE_COUNT + "( +)?(<=|<)")))) {
                                to = lineScan.nextInt();
                                if (tmp.charAt(tmp.length() - 1) != '=') {
                                    --to;
                                }
                            }
                            result = companies.findByNumberOfEmployees(from, to);
                        } else {
                            fail = true;
                        }
                    } else {
                        fail = true;
                    }
                } catch(Exception e) {
                    fail = true;
                }
                BufferedWriter bw = new BufferedWriter(new FileWriter("request" + requestIndex + ".csv"));
                printOutput(result, "", bw);
                bw.close();
                printLog("request " + requestIndex + " : \"" + query + "\"", logWriter, fail);
                requestIndex++;
                fail = false;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void printLog(String str, BufferedWriter writer, boolean failed) throws IOException {
        String result;
        if (failed) {
            result = "failed";
        } else {
            result = "succeeded";
        }
        writer.write(str + " " + LocalDateTime.now() + "  -- " + result + '\n');
    }

    private static void printOutput(List<Company> result, String query, BufferedWriter writer) throws IOException {
        writer.write(query + "\n");
        for (Company company : result) {
            writer.write(company.toString() + '\n');
        }
    }
}
