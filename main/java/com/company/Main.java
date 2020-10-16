package com.company;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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
            processQueriesSQL(inputFile, requestsFile, logFile);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void processQueriesSQL(String inputFile, String requestsFile, String logFile) throws IOException {
        CompanyQueries company_table = new CompanyQueries();
        //read from file
        try (Scanner scanner = new Scanner(new File(inputFile))) {
            while (scanner.hasNextLine()) {
                company_table.addCompany(new Company(scanner.nextLine()));
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        //process queries here
        String query = "";
        try (Scanner requestScanner = new Scanner(new File(requestsFile));
             BufferedWriter logWriter = new BufferedWriter(new FileWriter(logFile, true))
        ) {
            List<Company> result = new ArrayList<>();
            boolean fail = false;
            int requestIndex = 1;
            while (!(query = requestScanner.next()).isEmpty()) {
                try {
                    if (query.toLowerCase().equals(SQLKeyWords.SELECT) &&
                            requestScanner.next().toLowerCase().equals(SQLKeyWords.FROM) &&
                            requestScanner.next().toLowerCase().equals(SQLKeyWords.TABLE_NAME) &&
                            requestScanner.next().toLowerCase().equals(SQLKeyWords.WHERE)) {
                        query = requestScanner.nextLine();
                        query = query.toLowerCase();
                        Scanner lineScan = new Scanner(query);
                        String tmp;
                        if (lineScan.findInLine(Pattern.compile(" +?" +
                                Company.SHORTNAME +
                                " +?= +?")) != null) {
                            String name = lineScan.findInLine(Pattern.compile("[\"'][a-zA-Z][\"']"));
                            result = company_table.findByShortName(name.substring(1, name.length() - 1));
                        } else if (lineScan.findInLine(Pattern.compile("( +)?" +
                                Company.TYPE_OF_BUSINESS +
                                "( +)?=( +)?")) != null) {
                            String type = lineScan.findInLine(Pattern.compile("[\"'][a-zA-Z][\"']"));
                            result = company_table.findByTypeOfBusiness(type.substring(1, type.length() - 1));
                        } else if ((tmp = lineScan.findInLine(Pattern.compile(Company.EMPLOYEE_COUNT + "( +)?(>=|>)"))) != null) {
                            int from = -1;
                            int to = -1;
                            from = lineScan.nextInt();
                            if (tmp.charAt(tmp.length() - 1) != '=') {
                                ++from;
                            }
                            if (lineScan.findInLine(SQLKeyWords.AND) != null &&
                                    null != (tmp = lineScan.findInLine(Pattern.compile(Company.EMPLOYEE_COUNT + "( +)?(<=|<)")))) {
                                to = lineScan.nextInt();
                                if (tmp.charAt(tmp.length() - 1) != '=') {
                                    --to;
                                }
                            }
                            result = company_table.findByNumberOfEmployees(from, to);
                        } else {
                            fail = true;
                        }
                    } else {
                        fail = true;
                    }
                } catch(Exception e) {
                    fail = true;
                }
                BufferedWriter bw = new BufferedWriter(new FileWriter("request" + requestIndex + ".txt"));
                printOutput(result, "request " + requestIndex, bw);
                bw.close();
                printLog("request " + requestIndex, logWriter, fail);
                requestIndex++;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }



    public static void processQueries(String inputFile, String outputFile, String logFile) throws IOException {
        CompanyQueries companies = new CompanyQueries();
        //read from file
        try (Scanner scanner = new Scanner(new File(inputFile))) {
            while (scanner.hasNextLine()) {
                companies.addCompany(new Company(scanner.nextLine()));
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        //process queries here
        String query = "";
        try (Scanner scanner = new Scanner(System.in);
             BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFile));
             BufferedWriter logWriter = new BufferedWriter(new FileWriter(logFile, true))
             ) {
            System.out.println("Available queries: \n" + SHORTNAME + " String, " +
                    INDUSTRY + " String, " +
                    TYPE_OF_BUSINESS + " String, " +
                    FOUNDATION_DATE + " Date Date, " +
                    NUMBER_OF_EMPLOYEES + " int int, " +
                    EXIT);
            while (!(query = scanner.next()).equals(EXIT)) {
                List<Company> result;
                switch (query) {
                    case SHORTNAME:
                        String sName = scanner.next();
                        query += " " + sName;
                        result = companies.findByShortName(sName);
                        break;
                    case INDUSTRY:
                        String industry = scanner.next();
                        query += " " + industry;
                        result = companies.findByIndustry(industry);
                        break;
                    case TYPE_OF_BUSINESS:
                        String type = scanner.next();
                        query += " " + type;
                        result = companies.findByTypeOfBusiness(type);
                        break;
                    case FOUNDATION_DATE:
                        Date start = new SimpleDateFormat("dd/MM/yyyy").parse(scanner.next());
                        Date end = new SimpleDateFormat("dd/MM/yyyy").parse(scanner.next());
                        query += " " + start.toString() + " " + end.toString();
                        result = companies.findByDateOfFoundation(start, end);
                        break;
                    case NUMBER_OF_EMPLOYEES:
                        int min = scanner.nextInt();
                        int max = scanner.nextInt();
                        query += " " + min + " " + max;
                        result = companies.findByNumberOfEmployees(min, max);
                        break;
                    default:
                        scanner.nextLine();
                        System.out.println("query " + query + " not found\n");
                        printLog(query, logWriter, true);
                        continue;
                }
                printLog(query, logWriter, false);
                printOutput(result, query, outputWriter);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            if (query.length() > 0) {
                BufferedWriter logWriter = new BufferedWriter(new FileWriter(logFile, true));
                printLog(query, logWriter, true);
                logWriter.close();
            }
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

    static final String SHORTNAME = "SHORTNAME";
    static final String INDUSTRY = "INDUSTRY";
    static final String TYPE_OF_BUSINESS = "TYPE_OF_BUSINESS";
    static final String FOUNDATION_DATE = "FOUNDATION_DATE";
    static final String NUMBER_OF_EMPLOYEES = "NUMBER_OF_EMPLOYEES";
    static final String EXIT = "EXIT";
}
