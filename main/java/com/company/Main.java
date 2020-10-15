package com.company;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

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
            List<Company> result;
            while (!(query = requestScanner.next()).isEmpty()) {
                if (query.toLowerCase().equals(SQLKeyWords.SELECT) &&
                        requestScanner.next().toLowerCase().equals(SQLKeyWords.FROM) &&
                        requestScanner.next().toLowerCase().equals("company_table") &&
                        requestScanner.next().toLowerCase().equals(SQLKeyWords.WHERE)) {
                    query = requestScanner.nextLine();
                    query = query.trim();
                    query = query.toLowerCase();
                    if (query.startsWith(Company.SHORTNAME) &&
                            (query.charAt(Company.SHORTNAME.length()) == '=')) {
                        query = query.substring(Company.SHORTNAME.length() + 1);
                        String name = query.replaceAll("[\"']", "");
                        result = company_table.findByShortName(name);
                    } else if (query.startsWith(Company.TYPE_OF_BUSINESS) &&
                            (query.charAt(Company.TYPE_OF_BUSINESS.length()) == '=')) {
                        query = query.substring(Company.TYPE_OF_BUSINESS.length() + 1);
                        String type = query.replaceAll("[\"']", "");
                        result = company_table.findByTypeOfBusiness(type);
                    } else if (query.startsWith(Company.EMPLOYEE_COUNT)) {
                        query = query.substring(Company.EMPLOYEE_COUNT.length());
                        String[] conditions = query.split(" and ");
                        if (conditions.length == 2) {

                        }
                    } else {

                    }

                }
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

    private static void parseLogicalExpr(String str) {
        str = str.toLowerCase();
        String[] exprs = str.split(" and ");
        int from = -1;
        int to = -1;
        for (int i = 0; i < exprs.length; ++i) {
            if (Company.EMPLOYEE_COUNT.equals(exprs[i] = exprs[i].substring(0, Company.EMPLOYEE_COUNT.length()))) {
                if (exprs[i].charAt(0) == '<')
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
