package com.company;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

class Company {
    public static final String SHORTNAME = "shortname";
    public static final String EMPLOYEE_COUNT = "employee_count";
    public static final String TYPE_OF_BUSINESS = "type_of_business";

    private String name;
    private String shortName;
    private Date actualizationDate;
    private String address;
    private Date foundationDate;
    private Integer employeeCount;
    private String auditor;
    private String phone;
    private String email;
    private String industry;
    private String typeOfBusiness;
    private String URLAddress;

    public Company(String CSVString) throws Exception {
        String[] fields = CSVString.split(";");
        if (fields.length < 12) {
            throw new Exception("invalid CSV string");
        }
        this.name = fields[0];
        this.shortName = fields[1];
        this.actualizationDate = new SimpleDateFormat("dd/MM/yyyy").parse(fields[2]);
        this.address = fields[3];
        this.foundationDate = new SimpleDateFormat("dd/MM/yyyy").parse(fields[4]);
        this.employeeCount = Integer.parseInt(fields[5]);
        this.auditor = fields[6];
        this.phone = fields[7];
        this.email = fields[8];
        this.industry = fields[9];
        this.typeOfBusiness = fields[10];
        this.URLAddress = fields[11];
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setActualizationDate(Date actualizationDate) {
        this.actualizationDate = actualizationDate;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setFoundationDate(Date foundationDate) {
        this.foundationDate = foundationDate;
    }

    public void setEmployeeCount(Integer employeeCount) {
        this.employeeCount = employeeCount;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public void setTypeOfBusiness(String typeOfBusiness) {
        this.typeOfBusiness = typeOfBusiness;
    }

    public void setURLAddress(String URLAddress) {
        this.URLAddress = URLAddress;
    }

    public String getShortName() {
        return shortName;
    }

    public Date getActualizationDate() {
        return actualizationDate;
    }

    public String getAddress() {
        return address;
    }

    public Date getFoundationDate() {
        return foundationDate;
    }

    public Integer getEmployeeCount() {
        return employeeCount;
    }

    public String getAuditor() {
        return auditor;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getIndustry() {
        return industry;
    }

    public String getTypeOfBusiness() {
        return typeOfBusiness;
    }

    public String getURLAddress() {
        return URLAddress;
    }

    @Override
    public String toString() {
        return name + ";" +
                shortName + ";" +
                actualizationDate.toString() + ";" +
                address + ";" +
                foundationDate.toString() + ";" +
                employeeCount.toString() + ";" +
                auditor + ";" +
                phone + ";" +
                email + ";" +
                industry + ";" +
                typeOfBusiness + ";" +
                URLAddress + ";";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(name, company.name) &&
                Objects.equals(shortName, company.shortName) &&
                Objects.equals(actualizationDate, company.actualizationDate) &&
                Objects.equals(address, company.address) &&
                Objects.equals(foundationDate, company.foundationDate) &&
                Objects.equals(employeeCount, company.employeeCount) &&
                Objects.equals(auditor, company.auditor) &&
                Objects.equals(phone, company.phone) &&
                Objects.equals(email, company.email) &&
                Objects.equals(industry, company.industry) &&
                Objects.equals(typeOfBusiness, company.typeOfBusiness) &&
                Objects.equals(URLAddress, company.URLAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, shortName, actualizationDate, address, foundationDate, employeeCount, auditor, phone, email, industry, typeOfBusiness, URLAddress);
    }

    public String getName() {
        return name;
    }
}
